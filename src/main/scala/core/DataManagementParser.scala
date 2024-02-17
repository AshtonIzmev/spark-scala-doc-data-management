package scala.core

import scala.core.Toolkit.gr
import scala.dama.{DataDictionnary, DataGovernance, DataQuality, DataTable}
import scala.meta._
import scala.meta.tokens.Token
import scala.util.Using


object DataManagementParser {

  def main(args: Array[String]): Unit = {

    val dadic = getDataManagementMap(DataDictionnary, "dictionnaryStr")
    val dago = getDataManagementMap(DataGovernance, "owner,custodian")
    val datb = getDataManagementMap(DataTable, "", "tb")
    val dadb = getDataManagementMap(DataTable, "", "db")
    val daqua = getDataManagementMap(DataQuality)

    val path = scala.reflect.io.Path("src/main/scala/product")
    scala.tools.nsc.io.Path.onlyFiles(path.walk).foreach { f =>
      val code = Using(scala.io.Source.fromFile(f.path)) { source => source.mkString }
      code.get.parse[Source].get.tokens
        .filter(_.isInstanceOf[Token.Comment])
        .filter(_.syntax.startsWith("/**"))
        .map(token => Map(
          "Dictionnaire" -> dadic(gr(token, "return")),
          "Database" -> dadb(gr(token, "database")),
          "Table" -> datb(gr(token, "table")),
          "Owner métier" -> dago(gr(token, "owner")),
          "Responsable IT" -> dago(gr(token, "custodian")),
          "Garanties qualité" -> daqua(gr(token, "quality")),
          )
        ).foreach(println)
    }
  }

  def getDataManagementMap(dataClass: Any, prefixes: String="", suffixes: String=""): Map[String, String] = {
    dataClass.getClass.getDeclaredFields
      .filter(!_.getName.startsWith("MODULE$"))
      .filter(f => prefixes.split(",").foldLeft(false) {case (b, prefix) => b || f.getName.startsWith(prefix)})
      .filter(f => suffixes.split(",").foldLeft(false) {case (b, suffix) => b || f.getName.endsWith(suffix)})
      .map { field =>
        field.setAccessible(true)
        field.getName -> field.get(dataClass).asInstanceOf[String]
      }.toMap ++ Map("NA" -> "NA")
  }
}