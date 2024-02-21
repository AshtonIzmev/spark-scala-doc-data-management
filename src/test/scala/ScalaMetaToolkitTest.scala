package scala

import org.scalatest.BeforeAndAfter
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Path, Paths}
import scala.dama.dali.ScalaMetaToolkit._
import scala.meta.Tree
import scala.toolkit.Toolkit._


class ScalaMetaToolkitTest extends AnyFreeSpec with Matchers with BeforeAndAfter{

  var testTree: Option[Tree]=None

  before {
    val testScalaFiles: Seq[Path] = getScalaFiles("./src/test/resources/product")
    val testCode = getCode(testScalaFiles)
    testTree = Option(parse(testCode))
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
      val code_result = getCode(Seq(Paths.get("./src/test/resources/product/ProductC.scala")))
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
      val tree = parse(getCode(Seq(Paths.get("./src/test/resources/product/ProductC.scala"))))
      val imports_results = getObjectImports(tree)
      val imports_expected = Seq(("sql", "DataFrame"), ("ProductA", "getDataProductA"))
      assertResult(imports_results)(imports_expected)
    }
  }

  "Defs in scala code" - {
    "Should be correct parsed" in {
      val defs_results = getDefs(testTree.get)
      val defs_expected = Seq(
        "getDataProductA", "getDataProductB", "getDataProductC",
        "getDataProductA1", "getDataProductA2",
        "getDataProductB1", "getDataProductB2")
      assertResult(defs_results.map(_._1).toSet)(defs_expected.toSet)
    }
  }

  "Recursive parsing in scala code" - {
    "Should be parsed correctly in a single object" in {
      val tree = parse(getCode(Seq(Paths.get("./src/test/resources/parser/ProductX.scala"))))
      val links_results = getCompleteParse(tree).toSet.filter(_._1 != "root")
      val links_expected = Set(
        ("ProductX","a1"), ("ProductX","a2"), ("ProductX","a3"),
        ("a3", "a1"), ("a3", "a2"),
        ("ProductX","getX1"), ("ProductX","getX2"), ("ProductX","getX3"),
        ("getX1", "a1"), ("getX1", "a2"), ("getX2", "a1"), ("getX2", "a2"), ("getX3", "a1"), ("getX3", "a3"),
        ("getX", "getX1"), ("getX", "getX2"), ("getX", "getX3")
      )
      assert(links_results.intersect(links_expected).size == links_expected.size)
    }

    "Should be parsed correctly in a multi object after filtering important Terms" in {
      val tree = parse(getCode(getScalaFiles("./src/test/resources/lineage")))

      val links_results = getCompleteParse(tree).filter(_._1 != "root")
      val links_filtered = filterStartEndLineage(Seq("OutputDf"), Seq("getDf11", "getDf12", "getDf21", "getDf22"), links_results)

      val links_expected = Set(
        ("OutputDf", "getOut"),
        ("getOut", "getRefDfA"), ("getOut", "getRefDfB"),
        ("getRefDfA", "getDf11"), ("getRefDfA", "getDf12"),
        ("getRefDfB", "getDf21"), ("getRefDfB", "getDf22")
      )
      assertResult(links_filtered.toSet)(links_expected)
    }

    "Should be parsed correctly in a multi object after joining on Defs" in {
      val tree = parse(getCode(getScalaFiles("./src/test/resources/lineage")))

      val links_results = getDefsFilteredParse(tree)

      val links_expected = Set(
        ("getOut", "getRefDfA"), ("getOut", "getRefDfB"),
        ("getRefDfA", "getDf11"), ("getRefDfA", "getDf12"),
        ("getRefDfB", "getDf21"), ("getRefDfB", "getDf22")
      )
      assertResult(links_results.toSet)(links_expected)
    }
  }

}