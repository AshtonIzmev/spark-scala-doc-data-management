package resources.parser


object ProductX {

  val a1 = 2
  val a2 = 4
  val a3 = a1 + a2

  def getX1: Int = a1 + a2
  def getX2: Int = a1 * a2
  def getX3: Int = a1 - a3

  def getX: Int = getX1 + getX2.+(getX3)

}