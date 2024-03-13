package scala.dama

import scala.dama.dali.ScalaMetaToolkit._
import scala.toolkit.Toolkit.{getCode, getScalaFiles}


object DataLineage {

  def run(folder:String) = {
    val trees = getCode(getScalaFiles(folder)).map(parse)
    val defs = trees.flatMap(getDefs)

    val allDefs = defs.map(p => (p._1, p._2))
    val defsMap = defs.map(p => (p._1+"."+p._2, p._3))

    val defsLinks = trees.flatMap(t => getCompleteParse(t, allDefs))

    reflect.io.File("./web/files/tooltips.json").writeAll(tooltipsSeqToJson(defsMap))
    reflect.io.File("./web/files/links.json").writeAll(linksSeqToJson(defsLinks))

  }

}
