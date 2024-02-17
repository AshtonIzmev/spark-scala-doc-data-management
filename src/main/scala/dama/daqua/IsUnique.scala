package scala.dama.daqua

import com.amazon.deequ.checks.{Check, CheckLevel}
import com.amazon.deequ.{VerificationResult, VerificationSuite}
import org.apache.spark.sql.DataFrame


object IsUnique {

  def uniqueConstraint(column: String): Check =
    Check(CheckLevel.Warning, column + " uniqueness check")
      .hasCompleteness(column, _ >= 0.95)
      .isUnique(column)

  def check(df: DataFrame, column: String): VerificationResult =
    VerificationSuite()
      .onData(df)
      .addCheck(uniqueConstraint(column))
      .run()

}