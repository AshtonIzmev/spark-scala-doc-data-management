package scala

import com.amazon.deequ.VerificationResult
import com.amazon.deequ.checks.CheckStatus
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.dama.daqua.{HasDateShape, IsUnique}

class DeequeTest  extends AnyWordSpec with Matchers with MockFactory {

  private val spark: SparkSession = SparkSession.builder().appName("test").master("local[1]").getOrCreate()
  import spark.implicits._

  "uniquecall" should {
    "return a unique column success check" in {
      val data: DataFrame = Seq("v1", "v2", "v5").toDF("column")
      val result: VerificationResult = IsUnique.check(data, "column")
      result.status shouldBe CheckStatus.Success
    }

    "return a unique column warning check" in {
      val data: DataFrame = Seq("v1", "v2", "v1").toDF("column")
      val result: VerificationResult = IsUnique.check(data, "column")
      result.status shouldBe CheckStatus.Warning
    }
  }

  "dateshapecall" should {
    "return a success dateshape check" in {
      val data: DataFrame = Seq("2021-03-06", "2025-01-01").toDF("column")
      val result: VerificationResult = HasDateShape.check(data, "column", "yyyy-MM-dd")
      result.status shouldBe CheckStatus.Success
    }

    "return a warning dateshape check" in {
      val data: DataFrame = Seq("21-12-2021", "18-12-2025", "2025-01-01").toDF("column")
      val result: VerificationResult = HasDateShape.check(data, "column", "yyyy-MM-dd")
      result.status shouldBe CheckStatus.Warning
    }
  }
}
