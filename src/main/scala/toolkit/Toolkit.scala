package scala.toolkit

import org.apache.spark.sql.DataFrame

import java.nio.file.{Files, Path, Paths}
import scala.dama.daqua.QualityProcessor.FileContent
import scala.meta.{Parsed, _}
import scala.reflect.runtime.universe
import scala.util.{Try, Using}



object Toolkit {

  def callFunctionByName(className: String, functionName: String, params: Seq[Any]): Any = {
    println((className, functionName, params))
    val mirror = universe.runtimeMirror(getClass.getClassLoader)
    val module = mirror.staticModule(className)
    val im = mirror.reflectModule(module)
    val methodSymbol = im.symbol.typeSignature.member(universe.TermName(functionName)).asMethod
    val method = mirror.reflect(im.instance).reflectMethod(methodSymbol)
    try {
      method(params: _*)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw e
    }
  }

  def getDf(className: String, funGetName: String): DataFrame =
    callFunctionByName(className, funGetName, Seq.empty).asInstanceOf[DataFrame]

  def extractStructure(filePath: String): Try[FileContent] = {
    Using(scala.io.Source.fromFile(filePath)) { reader =>
      val scalaCode = reader.mkString
      val tokens = scalaCode.tokenize.get
      val comments = tokens.collect {
        case Token.Comment(comment) => comment
      }
      val parsed: Parsed[Source] = scalaCode.parse[Source]
      FileContent(comments, parsed)
    }
  }

  def flatten(lst: List[_]): List[String] = lst flatMap {
    case sublist: List[_] => flatten(sublist)
    case item: String => List(item)
  }

  /**
   * Select all *.scala ending files in a directory
   *
   * @param directoryPath path directory
   * @return
   */
  def getScalaFiles(directoryPath: String): Seq[Path] =
    Files.walk(Paths.get(directoryPath))
      .filter(path => Files.isRegularFile(path) && path.toString.endsWith(".scala"))
      .toArray
      .map(_.asInstanceOf[Path])
      .toSeq

  /**
   * Transform files into code string
   *
   * @param scalaFiles the files we want to parse
   * @return
   */
  def getCode(scalaFiles: Seq[Path]): String =
    scalaFiles.map(f => scala.io.Source.fromFile(f.toString))
      .map(source => try source.getLines().mkString("\n") finally source.close())
      .mkString("\n")


}