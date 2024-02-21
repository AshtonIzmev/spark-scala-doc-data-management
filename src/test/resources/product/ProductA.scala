package resources.product

import org.apache.spark.sql.DataFrame

import scala.core.DataCore.spark.implicits._


object ProductA {

  val a1 = 2

  def getDataProductA1: DataFrame =
    Seq(
      ("id1", "NA", "21-12-2021"),
      ("id2", "NA", "18-12-2025")
    ).toDF("id", "categ", "dt")

  def getDataProductA2: DataFrame =
    Seq(
      ("id3", "NA", "21-12-2021"),
      ("id4", "NA", "18-12-2025")
    ).toDF("id", "categ", "dt")

  def getDataProductA: DataFrame = getDataProductA1.union(getDataProductA2)

}
