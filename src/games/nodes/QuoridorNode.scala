package games.nodes

import scala.collection.mutable.ArrayBuffer
import scala.math._
import games.Quoridor
import games.utils.Erf
import games.utils.QuoridorUtils

class QuoridorNode (
    m:(String, Int, Int) = null, 
    parent: QuoridorNode = null, 
    state: (Quoridor, Int),
    s: Int = 0,
    t: Int = 0
    ) extends GameNode {
  
  val board:Quoridor = state._1
  val player:Int = state._2
  val move:(String, Int, Int) = m
  val step: Int = s
  val time_left:Int = t
  var childNodes: ArrayBuffer[QuoridorNode] = ArrayBuffer[QuoridorNode]()
  val parentNode = parent
  var untriedMoves: ArrayBuffer[(String, Int, Int)] = QuoridorUtils.get_moves(this.board, this.player)
  
  // Monte-Carlo tree search values
  var wins:Int = 0
  var visits:Int = 0
  var value:Int = 0
  var urgency:Double = 0
  var fairness:Double = 0
  
  // Tree node methods
  def addChild(m:(String, Int, Int), s:(Quoridor, Int), step:Int, timeLeft:Int):QuoridorNode = {
    val n:QuoridorNode = new QuoridorNode(m, this, s, step, timeLeft)
    this.untriedMoves -= m
    this.childNodes.append(n)
    n
  }
  
  def updateChild(result:Int) {
    if (result > 0)
      this.wins += result
    this.visits += 1
    this.value += result
  }
  
  /**
   * Selection strategies
   */
  
  // Upper Confidence Bound for Trees
  def UCT():QuoridorNode = {
    this.childNodes.foreach(node => {
      if (node.visits  == 0) {
        this.childNodes -= node
      }
    })
    
    this.childNodes.sortWith(
        (node1, node2) => 
          (node1.wins / node1.visits + sqrt(2 * log(node1.visits)/node1.visits)) < 
          ((node2.wins / node2.visits + sqrt(2 * log(node2.visits)/node2.visits))) 
          ).takeRight(1)(0)    
  }
  
  
  // Objective Monte-Carlo
  def OMC():QuoridorNode = {
    // best move value
	val node0:QuoridorNode = this.childNodes.sortWith((n1, n2) => n1.value < n2.value).takeRight(1)(0)
	val v_0:Int = node0.value
	
	// n_p
	val n_p = this.visits
    
    // total urgenciness of child nodes
    var totalUrgency:Double = 0
	
    // calculate the urgency
    this.childNodes.foreach(child => {
      if (child.visits > 0) {
        val p_i: Double = 1 / child.visits
        // expectation
        val expectation:Double = p_i * (2 * child.wins - child.visits)
        
        // standard deviation
        val sigma:Double = sqrt( (pow(1 - expectation, 2)) + (pow(1 + expectation, 2)) * p_i )
        
        // urgency function update to child node
        val erf:Erf = new Erf
        child.urgency = 1 - erf.erf( (v_0 - child.value) / (sqrt(2) * sigma) )
        
        // update total urgency
        totalUrgency += child.urgency
      }
    })
    
    val m:QuoridorNode = this.childNodes.sortWith(
        (n1, n2) => 
          ((n_p * n1.urgency) / (n1.visits * totalUrgency)) <
          ((n_p * n2.urgency) / (n2.visits * totalUrgency))
          ).takeRight(1)(0)
          
    //println("Node move chosen:" + m.move)
    
    m
  }
  
  // Probability Better than the Best Moves
  def PBBM():QuoridorNode = {
    // best move value
    val node0 = this.childNodes.sortWith((n1, n2) => (n1.value) < (n2.value)).takeRight(1)(0)
    
    // v_0
    val v_0 = node0.value
    // p_0
    val p_0 = 1 / node0.visits
    // e(x_0)
    val e_0 = p_0 * (2 * node0.wins - node0.visits)
    // standard deviation
    val sigma_0:Double = sqrt( (pow(1 - e_0, 2) + pow(1 + e_0, 2)) * p_0 )
    // n_p
    val n_p:Int = this.visits
    
    // total urgenciness of child nodes
    var totalUrgency:Double = 0
    
    this.childNodes.foreach(child => {
      if (child.visits > 0) {
        val p_i:Double = 1 / child.visits
        
        // expectation
        val expectation:Double = p_i * (2 * child.wins - child.visits)
        // standard deviation
        val sigma:Double = sqrt( (pow(1 - expectation, 2) + pow(1 + expectation, 2)) * p_i )
        // urgency function update to child node
        child.urgency = exp( -2.4 * (v_0 - child.value) / sqrt(2 * (pow(sigma_0, 2) + pow(sigma, 2))) )
        // update total urgency
        totalUrgency += child.urgency        
      }
    })
    
    this.childNodes.sortWith((n1, n2) => 
      	( (n_p * n1.urgency) / (n1.visits * totalUrgency) ) < 
      	( (n_p * n2.urgency) / (n2.visits * totalUrgency) )
        ).takeRight(1)(0)
  }
  
  // Upper Confidence Bound 1 Tuned
  def UCB1TUNED():QuoridorNode = {
    this
  }
}