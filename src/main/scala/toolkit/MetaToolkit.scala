package scala.toolkit

import org.apache.spark.sql.DataFrame

import scala.dama.daqua.QualityProcessor.FileContent
import scala.meta.{Parsed, _}
import scala.reflect.runtime.universe
import scala.util.{Try, Using}



object MetaToolkit {

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

}