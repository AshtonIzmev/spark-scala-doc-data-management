package resources.product

import org.apache.spark.sql.DataFrame

import scala.core.DataCore.spark.implicits._


object ProductB {

  val x4 = 4
  val b1 = 10
  val b2 = 20

  def getDataProductB1: DataFrame =
    Seq(
      ("id1", 1),
      ("id2", 2),
      ("id4", x4)
    ).toDF("id", "val")

  def getDataProductB2: DataFrame =
    Seq(
      ("id1", b1),
      ("id2", b2),
      ("id3", 30)
    ).toDF("id", "val")

  def getDataProductB: DataFrame = getDataProductB1.join(getDataProductB2)

}
