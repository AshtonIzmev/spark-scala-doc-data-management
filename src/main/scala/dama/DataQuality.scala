package scala.dama

import scala.Console.println
import scala.dama.daqua.QualityProcessor.checkQuality

object DataQuality {

  //val filePath = "src/main/scala/product/DataProduct.scala"
  //val className = "scala.product.DataProduct"
  def runDataProduct(filePath:String, className:String): Unit = {
    val results = checkQuality(filePath, className)
    results.foreach(r => {
      println(r._1);
      r._2.foreach(println)
    })
  }

}