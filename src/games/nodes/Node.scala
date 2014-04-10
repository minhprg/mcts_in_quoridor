package games.nodes

import scala.collection.mutable.ArrayBuffer

trait Node {  
  val move:String
  val step: Int
  val time_left:Int
  val childNodes: ArrayBuffer[QuoridorNode]
  val parentNode: Node
  val untriedMoves: ArrayBuffer[(String, Int, Int)]
  
  // Monte-Carlo tree search values
  val wins:Int = 0
  val visits:Int = 0
  val value:Int = 0
  val urgency:Double = 0
  val fairness:Double = 0
  
  def addChild
  def updateChild
}