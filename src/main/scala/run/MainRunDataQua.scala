package scala.run

import scala.Console.println
import scala.dama.daqua.QualityProcessor.checkQuality

object MainRunDataQua {

  def main(args: Array[String]): Unit = {
    runDataProductQuality()
  }

  def runDataProductQuality(): Unit = {
    val filePath = "src/main/scala/product/DataProductQuality.scala"
    val className = "scala.product.DataProductQuality"
    val results = checkQuality(filePath, className)
    results.foreach(r => {
      println(r._1);
      r._2.foreach(println)
    })
  }

  def runDataProduct(): Unit = {
    val filePath = "src/main/scala/product/DataProduct.scala"
    val className = "scala.product.DataProduct"
    val results = checkQuality(filePath, className)
    results.foreach(r => {
      println(r._1);
      r._2.foreach(println)
    })
  }

}
