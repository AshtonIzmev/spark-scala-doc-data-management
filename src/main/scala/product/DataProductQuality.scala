package scala.product

import org.apache.spark.sql.DataFrame

import scala.core.DataCore.spark.implicits._


object DataProductQuality {

  /**
   * @description My little Data Product 1
   * @quality unique(value)
   */
  def getDataProduct1: DataFrame = Seq("v1", "v2").toDF("value")

  /**
   * @description My little Data Product 2
   * @quality date_shape(dt,dd-MM-yyyy)
   */
  def getDataProduct2: DataFrame = Seq("21-12-2021", "18-12-2025").toDF("dt")

}