package resources.lineage

import org.apache.spark.sql.DataFrame
import resources.lineage.SourceDf1.getDf12
import resources.lineage.SourceDf2._


object ReferentielDf {

  def getRefDfA: DataFrame = SourceDf1.getDf11.union(getDf12)

  def getRefDfB: DataFrame = {
    val x = getDf21
    val y = getDf22
    x.union(y)
  }

}
