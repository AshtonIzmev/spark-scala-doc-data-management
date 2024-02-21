package scala.dama

import scala.dama.dali.ScalaMetaToolkit.{getCompleteParse, getDefs, getDefsFilteredParse, linksSeqToJson, parse, tooltipsSeqToJson}
import scala.toolkit.Toolkit.{getCode, getScalaFiles}


object DataLineage {

  def run(folder:String) = {
    val tree = parse(getCode(getScalaFiles(folder)))

    val defsMap = getDefs(tree)
    val defsLinks = getDefsFilteredParse(tree)

    reflect.io.File("./web/files/tooltips.json").writeAll(tooltipsSeqToJson(defsMap))
    reflect.io.File("./web/files/links.json").writeAll(linksSeqToJson(defsLinks))

  }

}
