package games.nodes

import scala.collection.mutable.ArrayBuffer

trait GameNode {  
  val move:(String, Int, Int)
  val step: Int
  val time_left:Int    
  var untriedMoves: ArrayBuffer[(String, Int, Int)]
  
  
}