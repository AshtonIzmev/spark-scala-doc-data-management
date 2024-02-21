package resources.lineage

import org.apache.spark.sql.DataFrame
import resources.lineage.ReferentielDf.{getRefDfA, getRefDfB}


object OutputDf {

  def getOut: DataFrame = {
    val u = 1
    val v = u+2
    getRefDfA.join(getRefDfB)
  }


}
