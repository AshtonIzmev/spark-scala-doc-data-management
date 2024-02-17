package scala
import com.amazon.deequ.VerificationResult
import com.amazon.deequ.checks.CheckStatus
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.dama.daqua.QualityProcessor._


class QualityProcessorTest  extends AnyWordSpec with Matchers with MockFactory {

  val spark: SparkSession = SparkSession.builder().appName("test").master("local[1]").getOrCreate()
  import spark.implicits._

  "applyOneCheck" should {
    "correctly process unique check and return results" in {
      val data: DataFrame = Seq(1, 2).toDF("id")
      val results: VerificationResult = applyOneCheck(data, "unique(id)")
      results.status shouldBe CheckStatus.Success
    }

    "correctly process date check and return results" in {
      val data: DataFrame = Seq("1999-01-05", "2035-12-31").toDF("dt")
      val results: VerificationResult = applyOneCheck(data, "date_shape(dt,yyyy-MM-dd)")
      results.status shouldBe CheckStatus.Success
    }
  }

  "checkDataframe" should {
    "correctly process unique check and return results" in {
      val data: DataFrame = Seq(1, 2).toDF("id")
      val comments =
        """   *
          |   * @description My little Data Product
          |   * @owner [[owner_issam]]
          |   * @custodian [[custodian_sami]]
          |   * @database [[database_khalid_db]]
          |   * @table [[table_anas_tb]]
          |   * @key column2
          |   * @return [[dictionnaryStr_dataproduct_mainRun]]
          |   * @quality unique(id)
          |   """.stripMargin
      val results: Array[VerificationResult] = checkDataframe(data, comments)
      results.foreach(_.status shouldBe CheckStatus.Success)
    }
  }

  "checkQuality" should {
    "correctly check quality end to end" in {
      val filePath = "src/main/scala/product/DataProduct.scala"
      val className = "scala.product.DataProduct"
      val results: List[(String, Array[VerificationResult])] = checkQuality(filePath, className)
      results.length shouldBe 1
      results.head._2.length shouldBe 3
      results.head._2.map(_.status) should contain theSameElementsAs
        Seq(CheckStatus.Success, CheckStatus.Warning, CheckStatus.Warning)
    }

    "correctly check quality end to end when multiple dataproducts in same file" in {
      val filePath = "src/main/scala/product/DataProductQuality.scala"
      val className = "scala.product.DataProductQuality"
      val results: List[(String, Array[VerificationResult])] = checkQuality(filePath, className)
      results.length shouldBe 2
      results.head._2.length shouldBe 1
      results.last._2.length shouldBe 1
      results.head._1 shouldBe "getDataProduct1"
      results.head._2.head.checkResults.head._1.description shouldBe "value column uniqueness check"
      results.last._1 shouldBe "getDataProduct2"
      results.last._2.head.checkResults.head._1.description shouldBe "dt column date shape check"
    }
  }


}
