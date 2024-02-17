package scala.core

import org.apache.spark.sql.DataFrame

import scala.meta.tokens.Token

object Toolkit {

  def saveTo(df:DataFrame, database:String, table:String): Unit = {
    println("Saving ")
  }

  def gr(token: Token, fix:String): String = {
    val results = s"@${fix} \\[\\[(.*)\\]\\]".r.findAllMatchIn(token.syntax).map(m => m.group(1))
    if (results.isEmpty) return "NA"
    results.next()
  }

}
