package scala.dama.daqua

object CheckMapping {

  val qualityFuns = Map(
    "unique" -> "scala.dama.daqua.IsUnique",
    "date_shape" -> "scala.dama.daqua.HasDateShape",
    "is_in" -> "scala.dama.daqua.IsIn"
  )

}
