package scala.dama.daqua

import com.amazon.deequ.checks.{Check, CheckLevel}
import com.amazon.deequ.{VerificationResult, VerificationSuite}
import org.apache.spark.sql.DataFrame


object HasDateShape {

  def dateShapeConstraint(column: String, format: String): Check =
    Check(CheckLevel.Warning, s"$column date shape check")
      .satisfies(s"date_format($column, '$format') IS NOT NULL", s"Date format check for $column")

  def check(df: DataFrame, column: String, format: String): VerificationResult =
    VerificationSuite()
      .onData(df)
      .addCheck(dateShapeConstraint(column, format))
      .run()

}