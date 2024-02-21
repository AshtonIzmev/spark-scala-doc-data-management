package scala.dama.dali

import scala.meta._
import upickle.default._


object ScalaMetaToolkit {

  private case class LineageLink(source: String, target: String, kind: String)
  private case class LineageTooltip(definition: String, tip: String)

  def linksSeqToJson(links: Seq[(String, String)]): String = {
    implicit val linkRw: ReadWriter[LineageLink] = macroRW
    write(links.map(l=>LineageLink(l._2, l._1, "link")))
  }

  def tooltipsSeqToJson(tooltips: Seq[(String, String)]): String = {
    implicit val linkRw: ReadWriter[LineageTooltip] = macroRW
    write(tooltips.map(l=>LineageTooltip(l._1, l._2)))
  }

  def parse(code: String): Tree = code.parse[Source].get

  def getObjectImports(tree: Tree): Stream[(String, String)] = {
    tree.collect {
      case q"import ..$importersnel" =>
        val importObject = importersnel.head.collect {
          case Name.Indeterminate(name) => name
          case Importee.Wildcard() => ""
        }
        val importPackage = importersnel.head.collect {
          case Term.Name(name) => name
        }
        Stream.continually(importPackage.last).zip(importObject).filter(_._2.nonEmpty)
    }.reduce((a1,a2)=>a1.union(a2))
  }

  def getDefs(tree: Tree): Seq[(String, String)] = {
    tree.collect {
      case defn@q"..$mods def $name[..$tparams](...$paramss): $tpeopt = $expropt" =>
        (name.toString(), expropt.toString())
      case defn@q"..$mods def $name: $tpeopt = $expropt" =>
        (name.toString(), expropt.toString())
    }
  }

  def getDefsFilteredParse(tree: Tree):  Seq[(String, String)] = {
    val allLinks = getCompleteParse(tree).filter(_._1 != "root")
    val defsMap = getDefs(tree).map(_._1)
    allLinks.filter(l => defsMap.contains(l._1) && defsMap.contains(l._2))
  }

  def getCompleteParse(tree: Tree): Seq[(String, String)] = recurseParse("root", tree, "object")

  private def recurseParse(father:String, tree:Tree, categ:String): Seq[(String, String)] = {
    tree.collect {
      case defn@q"..$mods object $ename $template" =>
        recurseParse(ename.toString(), template, "object") :+ (father, ename.toString())
      case defn@q"..$mods def $name[..$tparams](...$paramss): $tpeopt = $expropt" =>
        recurseParse(name.toString(), expropt, "def") :+ (father, name.toString())
      case defn@q"..$mods def $name: $tpeopt = $expropt" =>
        recurseParse(name.toString(), expropt, "def") :+ (father, name.toString())
      case defn@q"..$mods val ..$patsnel: $tpeopt = $expropt" =>
        recurseParse(patsnel.head.toString(), expropt, "val") :+ (father, patsnel.head.toString())
      case q"$expr.$name" =>
        if(Seq("def", "val").contains(categ)) Seq((father, expr.toString() + ":" + name.toString())) else Seq[(String, String)]()
      case q"$name" =>
        if(Seq("def", "val").contains(categ)) Seq((father, name.toString())) else Seq[(String, String)]()
    }.reduceOption((a1,a2)=>a1.union(a2)).getOrElse(Seq())
  }

  def filterStartEndLineage(start:Seq[String], end:Seq[String], deps:Seq[(String, String)]): Seq[(String, String)] = {

    def recurseFilter(st:String, stack:Seq[(String, String)]): Seq[(String, String)] = {
      val next = deps.filter(n => n._1==st)
      val nodesEnd = next.filter(n => end.contains(n._2))
      val nodesNext = next.filter(n => !end.contains(n._1))

      if (nodesEnd.nonEmpty) return stack ++ nodesEnd
      if (nodesNext.isEmpty) return Seq.empty
      nodesNext.map(node => recurseFilter(node._2, Seq(node) ++ stack)).reduce((a1, a2)=>a1.union(a2))
    }

    start.map(st => recurseFilter(st, Seq.empty)).filter(_.nonEmpty).reduce((a1, a2)=>a1.union(a2))
  }

}