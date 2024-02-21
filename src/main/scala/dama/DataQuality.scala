package scala.dama

import com.amazon.deequ.VerificationResult

import scala.dama.daqua.QualityProcessor.checkQuality

object DataQuality {

  def run(filePath: String, className: String):  List[(String, Array[VerificationResult])] = {
    //val filePath = "src/main/scala/product/DataProductQuality.scala"
    //val className = "scala.product.DataProductQuality"
    checkQuality(filePath, className)
  }

}