package games.nodes

import scala.collection.mutable.ArrayBuffer

trait GameNode {  
  val move:(String, Int, Int)
  val step: Int
  val time_left:Int = 0    
  var untriedMoves: ArrayBuffer[(String, Int, Int)]
}