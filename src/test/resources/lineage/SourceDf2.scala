package resources.lineage

import org.apache.spark.sql.DataFrame

import scala.core.DataCore.spark.implicits._


object SourceDf2 {

  def getDf21: DataFrame =
    Seq(
      ("id5", "NA", "21-12-2021"),
      ("id6", "NA", "18-12-2025")
    ).toDF("id", "categ", "dt")

  def getDf22: DataFrame =
    Seq(
      ("id8", "NA", "21-12-2021"),
      ("id9", "NA", "18-12-2025")
    ).toDF("id", "categ", "dt")

}
