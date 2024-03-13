package scala

import org.scalatest.BeforeAndAfter
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Path, Paths}
import scala.dama.dali.ScalaMetaToolkit._
import scala.meta.Tree
import scala.toolkit.Toolkit._


class ScalaMetaToolkitTest extends AnyFreeSpec with Matchers with BeforeAndAfter{

  var testTree: Seq[Tree]= Seq.empty

  before {
    val testScalaFiles: Seq[Path] = getScalaFiles("./src/test/resources/product")
    val testCode = getCode(testScalaFiles)
    testTree = testCode.map(c=> parse(c))
  }

  "Scala files" - {
    "Should be correctly listed" in {
      val paths_results: Seq[String] = getScalaFiles("./src/test/resources/product").map(_.toString)
      val paths_expected = Seq(
        "./src/test/resources/product/ProductC.scala",
        "./src/test/resources/product/ProductA.scala",
        "./src/test/resources/product/ProductB.scala"
      )
      assertResult(paths_results.toSet)(paths_expected.toSet)
    }
  }

  "Scala code" - {
    "Should be correctly parsed" in {
      val code_result = getCode(Seq(Paths.get("./src/test/resources/product/ProductC.scala"))).head
      val code_expected =
        """|package resources.product
           |
           |import org.apache.spark.sql.DataFrame
           |import resources.product.ProductA.getDataProductA
           |import resources.product.ProductB._
           |
           |object ProductC {
           |
           |  def getDataProductC: DataFrame = getDataProductA.join(getDataProductB)
           |
           |}""".stripMargin
      assertResult(code_result)(code_expected)
    }
  }

  "Imports in scala code" - {
    "Should be correct" in {
      val trees = getCode(Seq(Paths.get("./src/test/resources/lineage/ReferentielDf.scala"))).map(parse)
      val imports_results = trees.flatMap(getObjectImports)
      val imports_expected = Seq(("sql", "DataFrame"), ("SourceDf1", "getDf12"), ("SourceDf2", "_"))
      assertResult(imports_results)(imports_expected)
    }
  }

  "Objects in scala code" - {
    "Should be correct parsed" in {
      val trees = getCode(Seq(Paths.get("./src/test/resources/product/ProductC.scala"))).map(parse)
      val objects_results = trees.flatMap(getObjectObject)
      val objects_expected = Seq("ProductC")
      assertResult(objects_results)(objects_expected)
    }
  }

  "Defs in scala code" - {
    "Should be correct parsed" in {
      val defs_results = testTree.flatMap(getDefs)
      val defs_expected = Seq(
        ("ProductA", "getDataProductA"),
        ("ProductB", "getDataProductB"),
        ("ProductC", "getDataProductC"),
        ("ProductA", "getDataProductA1"),
        ("ProductA", "getDataProductA2"),
        ("ProductB", "getDataProductB1"),
        ("ProductB", "getDataProductB2")
      )
      assertResult(defs_results.map(c => (c._1, c._2)).toSet)(defs_expected.toSet)
    }
  }

  "Recursive parsing in scala code" - {
    "Should be parsed correctly in a single object" in {
      val trees = getCode(Seq(Paths.get("./src/test/resources/parser/ProductX.scala"))).map(parse)
      val links_results = trees.flatMap(getRecurseParse).toSet.filter(_._1 != "root")
      val links_expected = Set(
        ("a3", "a1","val"), ("a3", "a2","val"),
        ("getX1", "a1","def"), ("getX1", "a2","def"), ("getX2", "a1","def"), ("getX2", "a2","def"), ("getX3", "a1","def"), ("getX3", "a3","def"),
        ("getX", "getX1","def"), ("getX", "getX2","def"), ("getX", "getX3","def")
      )
      assert(links_results.intersect(links_expected).size == links_expected.size)
    }
  }

  "Complete parse in scala code" - {

    "Should be correct parsed using a map for indirect imports" in {
      val allTrees = getCode(getScalaFiles("./src/test/resources/lineage")).map(parse)
      val allDefs = allTrees.flatMap(getDefs).map(p => (p._1, p._2))

      val tree = getCode(getScalaFiles("./src/test/resources/lineage/ReferentielDf.scala")).map(parse).head
      val results = getIndirectDefMap(tree, allDefs)
      val expected = Seq(
        ("DataFrame", "sql"),
        ("getDf12", "SourceDf1"),
        ("getDf21", "SourceDf2"),
        ("getDf22", "SourceDf2")
      )
      assertResult(expected.toSet)(results.toSet)
    }

    "Should be correct parsed in a single object" in {
      val allTrees = getCode(getScalaFiles("./src/test/resources/lineage")).map(parse)
      val allDefs = allTrees.flatMap(getDefs).map(p => (p._1, p._2))

      val tree = getCode(getScalaFiles("./src/test/resources/lineage/ReferentielDf.scala")).map(parse).head
      val results = getCompleteParse(tree, allDefs)
      val expected = Seq(
        ("ReferentielDf.getRefDfA", "SourceDf1.getDf11", "def"),
        ("ReferentielDf.getRefDfA", "SourceDf1.getDf12", "def"),
        ("ReferentielDf.getRefDfB", "SourceDf2.getDf21", "def"),
        ("ReferentielDf.getRefDfB", "SourceDf2.getDf22", "def"))
      assertResult(expected.toSet)(results.toSet)
    }

    "Should be correct parsed in multiple objects" in {
      val allTrees = getCode(getScalaFiles("./src/test/resources/lineage")).map(parse)
      val allDefs = allTrees.flatMap(getDefs).map(p => (p._1, p._2))

      val results = getCode(getScalaFiles("./src/test/resources/lineage")).map(parse)
        .flatMap(t => getCompleteParse(t, allDefs))
      val expected = Seq(
        ("ReferentielDf.getRefDfA", "SourceDf1.getDf11", "def"),
        ("ReferentielDf.getRefDfA", "SourceDf1.getDf12", "def"),
        ("ReferentielDf.getRefDfB", "SourceDf2.getDf21", "def"),
        ("ReferentielDf.getRefDfB", "SourceDf2.getDf22", "def"),
        ("OutputDf.getOut", "ReferentielDf.getRefDfB", "def"),
        ("OutputDf.getOut", "ReferentielDf.getRefDfA", "def"))
      assertResult(expected.toSet)(results.toSet)
    }
  }

}