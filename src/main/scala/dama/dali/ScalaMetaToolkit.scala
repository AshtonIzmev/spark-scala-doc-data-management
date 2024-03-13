package scala.dama.dali

import upickle.default._

import scala.meta._


object ScalaMetaToolkit {

  private case class LineageLink(source: String, target: String, kind: String)
  private case class LineageTooltip(definition: String, tip: String)

  def linksSeqToJson(links: Seq[(String, String, String)]): String = {
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
          case Importee.Wildcard() => "_"
        }
        val importPackage = importersnel.head.collect {
          case Term.Name(name) => name
        }
        Stream.continually(importPackage.last).zip(importObject).filter(_._2.nonEmpty)
    }.reduce((a1,a2)=>a1.union(a2))
  }

  def getObjectObject(tree: Tree): Seq[String] = {
    tree.collect {
      case defn@q"..$mods object $ename $template" =>
        ename.toString()
    }
  }

  def getDefs(tree: Tree): Seq[(String, String, String)] = {
    val obj = getObjectObject(tree).head
    tree.collect {
      case defn@q"..$mods def $name[..$tparams](...$paramss): $tpeopt = $expropt" =>
        (obj, name.toString(), expropt.toString())
      case defn@q"..$mods def $name: $tpeopt = $expropt" =>
        (obj, name.toString(), expropt.toString())
    }
  }

  def getIndirectDefMap(tree: Tree, allDefs: Seq[(String, String)]): Map[String, String] = {
    val treeImports = getObjectImports(tree)
    val treeImportsFull = treeImports.filter(_._2 != "_").map(_.swap).toMap
    val treeImportsObjIncomplete = treeImports.filter(_._2 == "_").map(_._1)

    val importDefsInvMap = allDefs.filter(x => treeImportsObjIncomplete.contains(x._1)).map(_.swap).toMap
    treeImportsFull ++ importDefsInvMap
  }

  def getCompleteParse(tree: Tree, allDefs: Seq[(String, String)]): Seq[(String, String, String)] = {
    val defToObjMap = getIndirectDefMap(tree, allDefs)

    val allDefsInvMap = allDefs.map(_.swap).toMap
    val treeObject = getObjectObject(tree).head

    val parsed = getRecurseParse(tree)

    val parsedDefs = parsed.filter(p => allDefs.map(_._2).contains(p._1) && allDefs.map(_._2).contains(p._2))

    def addObj(elem: String): String = {
      if (allDefs.map(_._2).contains(elem) && defToObjMap.keySet.contains(elem)) return defToObjMap.getOrElse(elem, "")+"."+elem
      if (allDefs.map(_._2).contains(elem) && allDefsInvMap.keySet.contains(elem)) return allDefsInvMap.getOrElse(elem, "")+"."+elem
      if (allDefs.map(_._2).contains(elem)) return treeObject+"."+elem
      elem
    }

    parsedDefs.map(p => (addObj(p._1), addObj(p._2), p._3))
  }

  def getRecurseParse(tree: Tree): Seq[(String, String, String)] = recurseParse("root", tree, "object")

  private def recurseParse(father:String, tree:Tree, categ:String): Seq[(String, String, String)] = {
    tree.collect {
      case defn@q"..$mods def $name[..$tparams](...$paramss): $tpeopt = $expropt" =>
        recurseParse(name.toString(), expropt, "def") :+ (father, name.toString(), categ)
      case defn@q"..$mods def $name: $tpeopt = $expropt" =>
        recurseParse(name.toString(), expropt, "def") :+ (father, name.toString(), categ)
      case defn@q"..$mods val ..$patsnel: $tpeopt = $expropt" =>
        recurseParse(patsnel.head.toString(), expropt, "val") :+ (father, patsnel.head.toString(), categ)
      case q"$expr.$name" =>
        if(Seq("def", "val").contains(categ)) Seq((father, expr.toString() + ":" + name.toString(), categ))
        else Seq[(String, String, String)]()
      case q"$name" =>
        if(Seq("def", "val").contains(categ)) Seq((father, name.toString(), categ))
        else Seq[(String, String, String)]()
    }.reduceOption((a1,a2)=>a1.union(a2)).getOrElse(Seq())
  }

}