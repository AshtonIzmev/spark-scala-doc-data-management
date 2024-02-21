package scala

import com.amazon.deequ.VerificationResult
import com.amazon.deequ.checks.CheckStatus
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.dama.daqua.CheckMapping.qualityFuns
import scala.dama.daqua.QualityProcessor.FileContent
import scala.toolkit.Toolkit.{callFunctionByName, extractStructure}

class ToolkitTest extends AnyWordSpec with Matchers with MockFactory {

  private val spark: SparkSession = SparkSession.builder().appName("test").master("local[1]").getOrCreate()
  import spark.implicits._

  "callFunctionByName" should {
    "return a unique column success check" in {
      val data: Any = Seq("v1", "v2", "v5").toDF("column")
      val result: VerificationResult = callFunctionByName(
        qualityFuns.getOrElse("unique", ""),
        "check",
        Seq(data, "column")).asInstanceOf[VerificationResult]
      result.status shouldBe CheckStatus.Success
    }

    "return a warning dateshape check" in {
      val data: DataFrame = Seq("21-12-2021", "18-12-2025").toDF("column")
      val result: VerificationResult = callFunctionByName(
        qualityFuns.getOrElse("date_shape", ""),
        "check",
        Seq(data, "column", "yyyy-MM-dd")).asInstanceOf[VerificationResult]
      result.status shouldBe CheckStatus.Warning
    }

    "return a correctly parsed dataframe" in {
      val result: DataFrame = callFunctionByName("scala.product.DataProduct", "getDataProduct", Seq.empty)
        .asInstanceOf[DataFrame]
      result.count() shouldBe 2
      result.columns.length shouldBe 3
      result.columns should contain theSameElementsAs Seq("column1", "column2", "column3")
    }
  }

  "extractComments" should {
    "correctly extract comments and parse code from a file" in {
      val filePath = "src/main/scala/product/DataProduct.scala"
      val result: FileContent = extractStructure(filePath).get
      result.comments shouldBe a[IndexedSeq[_]]
      result.comments should not be empty
      result.parsed.get shouldBe a[scala.meta.Source]
      result.parsed shouldBe a[scala.meta.Parsed.Success[_]]
    }
  }

}