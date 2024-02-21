package resources.lineage

import org.apache.spark.sql.DataFrame

import scala.core.DataCore.spark.implicits._


object SourceDf1 {

  def getDf11: DataFrame =
    Seq(
      ("id1", "NA", "21-12-2021"),
      ("id2", "NA", "18-12-2025")
    ).toDF("id", "categ", "dt")

  def getDf12: DataFrame =
    Seq(
      ("id3", "NA", "21-12-2021"),
      ("id4", "NA", "18-12-2025")
    ).toDF("id", "categ", "dt")

}
