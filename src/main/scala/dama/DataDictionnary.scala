package scala.dama

object DataDictionnary {

  private val dictionnary_dataproduct_mainRun = Map(
    "column1" -> "First column",
    "column2" -> "Second column",
    "column3" -> "Oh a third column"
  )

  val dictionnaryStr_dataproduct_mainRun = dictionnary_dataproduct_mainRun
    .map(e => e._1 + " => " + e._2).mkString("\n")

}
