package scala.run

import scala.dama.DataLineage

object MainRunDataLin {

  def main(args: Array[String]): Unit = runDataLineage()

  def runDataLineage(): Unit = {
    DataLineage.run("./src/test/resources/lineage")
  }

}