package games.nodes

import scala.collection.mutable.ArrayBuffer
import games.Quoridor

class QuoridorNode (
    m:String, 
    parent: QuoridorNode, 
    state: Option[(Quoridor, Int)],
    s: Int,
    t: Int = 0
    ) extends Node {
  
  val move:String = m
  val step: Int = s
  val time_left:Int = t
  val childNodes: ArrayBuffer[QuoridorNode] = ArrayBuffer[QuoridorNode]()
  val parentNode = parent
  val untriedMoves: ArrayBuffer[(String, Int, Int)] = ArrayBuffer[(String, Int, Int)]()
  
  // Tree node methods
  def addChild():Unit = {
    
  }
  
  def updateChild():Unit = {
    
  }
}