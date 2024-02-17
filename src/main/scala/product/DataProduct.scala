package scala.product

import org.apache.spark.sql.DataFrame

import scala.core.DataCore.spark
import scala.core.DataCore.spark.implicits._
import scala.dama.DataDictionnary._
import scala.dama.DataGovernance._
import scala.dama.DataTable._


object DataProduct {

  /**
   * @description My little Data Product
   * @owner [[owner_issam]]
   * @custodian [[custodian_sami]]
   * @database [[database_khalid_db]]
   * @table [[table_anas_tb]]
   * @key column2
   * @return [[dictionnaryStr_dataproduct_mainRun]]
   * @quality unique(column1);unique(column2);date_shape(column3,dd-MM-yyyy)
   */
  def getDataProduct: DataFrame =
    Seq(
      ("value1", "NA", "21-12-2021"),
      ("value2", "NA", "18-12-2025")
    ).toDF("column1", "column2", "column3")

}