package scala.dama.daqua

import com.amazon.deequ.VerificationResult
import org.apache.spark.sql.DataFrame

import scala.Console.println
import scala.dama.daqua.CheckMapping.qualityFuns
import scala.meta.{Parsed, _}
import scala.toolkit.MetaToolkit._


object QualityProcessor {

  case class FileContent(comments: IndexedSeq[String], parsed: Parsed[Source])

  def checkQuality(filePath:String, className: String): List[(String, Array[VerificationResult])] = {
    val result = extractComments(filePath)
    val comments = result.get.comments
    val parsed = result.get.parsed
    val qualityTags = comments.filter(_.contains("@quality"))
    parsed match {
      case Parsed.Success(tree) =>
        tree.collect[(String, Array[VerificationResult])] {
          case q"..$mods  def $name: $tpeopt = $expr" =>
            (name.value, checkDataframe(getDf(className, name.value), qualityTags))
          case q"..$mods  def $name[..$tparams](...$paramss): $tpeopt = $expr" =>
            (name.value, checkDataframe(getDf(className, name.value), qualityTags))
        }
      case Parsed.Error(_, message, _) =>
        println(s"Error parsing source code: $message")
        List(("", Array.empty[VerificationResult]))
    }
  }

  def checkDataframe(df:DataFrame, qualityTags:Seq[String]): Array[VerificationResult] = {
    qualityTags.find(_.contains(s"@quality")).toArray
      .flatMap(tag => tag.substring(tag.indexOf("@quality") + 8).replace("@quality", "").split(";").map(_.trim))
      .map(check => applyOneCheck(df, check))
  }

  def applyOneCheck(df: DataFrame, check: String): VerificationResult = {
    val parts = check.split("[(),]")
    callFunctionByName(
      qualityFuns.getOrElse(parts(0).trim, "not_found"),
      "check",
      Seq(df) ++ parts.drop(1).map(_.replace("[", "").replace("]", "").trim))
      .asInstanceOf[VerificationResult]
  }

}