package resources.product

import org.apache.spark.sql.DataFrame
import resources.product.ProductA.getDataProductA
import resources.product.ProductB._

object ProductC {

  def getDataProductC: DataFrame = getDataProductA.join(getDataProductB)

}
