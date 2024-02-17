package scala.dama.daqua

import scala.Console.println
import scala.dama.daqua.QualityProcessor.checkQuality

object MainRunDataQua {

  def main(args: Array[String]): Unit = {
    val filePath = "src/main/scala/product/DataProduct.scala"
    val className = "main.scala.product.DataProduct"
    val results = checkQuality(filePath, className)
    results.foreach(r => {
      println(r._1);
      r._2.foreach(println)
    })
  }

}
