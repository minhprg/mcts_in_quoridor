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
    simulation: String,
    stepNumber: Int = 0
    ) extends GameNode {
  
  val board:Quoridor = state._1
  val player:Int = state._2
  val move:(String, Int, Int) = m
  val step: Int = stepNumber
  var childNodes: ArrayBuffer[QuoridorNode] = ArrayBuffer[QuoridorNode]()
  val parentNode = parent
  val simulationType = simulation
  
  // If the simulation is "e" then use the branching factor reduction, else use all actions
  var untriedMoves: ArrayBuffer[(String, Int, Int)] = QuoridorUtils.getchildNodes(this.board, this.player)
  if (this.simulationType == "a")
	    untriedMoves = this.board.getActions(this.player)
  
  // Monte-Carlo tree search values
  var wins:Double = 0
  var visits:Double = 1 // when you create a node, it is visited 1
  var value:Double = 0
  var payoffs:Double = 0
  var urgency:Double = 0
  var fairness:Double = 0
  var depth:Int = 0
  // Quoridor
  var omc_urgency:Double = 0 // portion for quoridor, used in OMC Urgency
  var omc_fairness:Double = 0 // portion for quoridor, used in Fairness
  var uct_value:Double = 0 // UCT Value
  var pbbm_urgency:Double = 0 // PBBM urgency
  var pbbm_fairness:Double = 0 // PBBM value
  
  // Tree node methods
  def addChild(m:(String, Int, Int), s:(Quoridor, Int), step:Int):QuoridorNode = {
    val n:QuoridorNode = new QuoridorNode(m, this, s, this.simulation, step)
    n.depth = this.depth + 1 // increase the depth of the tree when descent
    this.untriedMoves -= m
    this.childNodes.append(n)
    n
  }
  
  /**
   * Backpropagate strategies
   */
  def updateUCT(result:Int) {
    if (result > 0)
      this.wins += result
    this.visits += 1
    this.payoffs += result
    if (this.childNodes.length != 0) {
      var total:Double = 0
      this.childNodes.foreach(node => {
        total += node.value
        })
        this.value = total / this.childNodes.length
    }
    else
      this.value = (2 * this.wins - this.visits) / this.visits
  }
  
  def updateOMC(result:Int) {
    // update wins and payoffs
    if (result > 0)
      this.wins += result
    this.visits += 1
    this.payoffs += result
    
    // if it has child node - start calculate Informed Average
    if (this.childNodes.length != 0) {
      var t1:Double = 0
      var t2:Double = 0
      this.childNodes.foreach(node => {
        if (node.urgency != 0) {
          t1 += (node.value * node.visits * node.urgency)
          t2 += node.visits * node.urgency
        }
        else {
          t1 += (node.value * node.visits)
          t2 += node.visits
        }
      })
      
      // update node value following OMC strategy
      this.value = (t1 / t2)
    }
    else
      this.value = this.wins / this.visits
    
    // calculate urgency of this node
    if (this.parentNode != null) {
    	// best move value
		val node0:QuoridorNode = this.parentNode.childNodes.sortWith((n1, n2) => n1.value < n2.value).takeRight(1)(0)
		val v_0:Double = node0.value	
		// n_p
		val n_p = this.parentNode.visits
		// expectation
		val expectation:Double = this.value
		// standard deviation
		val sigma:Double = sqrt( ( this.wins * pow((1 - expectation), 2) + (this.visits - this.wins) * pow((1 + expectation), 2) ) / this.visits )
	    // urgency function update to child node
		val erf:Erf = new Erf
		this.urgency = 1 - erf.erf( (v_0 - this.value) / (sqrt(2) * sigma) )		
		//println("U(i) = " + this.urgency + ". Sigma:" + sigma + ". E(X):" + expectation + ". Value:" + this.value + ". V0: " + node0.value + ". Visits:" + this.visits + ". Wins:" + this.wins + ". Parent:" + this.parentNode.visits)
    }
  }
  
  def updatePBBM(result:Int) {
    // update wins and payoffs
    if (result > 0)
      this.wins += result
    this.visits += 1
    this.payoffs += result
    
    // update values
    if (this.childNodes.length > 0) {
      var total:Double = 0
      this.childNodes.foreach(node => {
        total += node.value
      })
      this.value = total / this.childNodes.length
    }
    else
      this.value = this.wins / this.visits
    
    // calculate urgency of this node
    if (this.parentNode != null) {
    	// best move value
		val node0:QuoridorNode = this.parentNode.childNodes.sortWith((n1, n2) => n1.value < n2.value).takeRight(1)(0)
		val v_0:Double = node0.value	
		// e(x_0)
	    val e_0 = node0.value
		// standard deviation
	    val sigma_0:Double = sqrt( 1 - pow(e_0, 2) )
		// n_p
		val n_p = this.parentNode.visits
		// expectation
		val expectation:Double = this.value
		// standard deviation
		val sigma:Double = sqrt( ( this.wins * pow((1 - expectation), 2) + (this.visits - this.wins) * pow((1 + expectation), 2) ) / this.visits )
	    // urgency function update to child node
		val erf:Erf = new Erf
		this.urgency = exp( -2.4 * (v_0 - this.value) / sqrt(2 * (pow(sigma_0, 2) + pow(sigma, 2))) )
		//println("U(i) = " + this.urgency + ". Value:" + this.value + ". V0: " + node0.value + ". Visits:" + this.visits + ". Wins:" + this.wins + ". Parent:" + this.parentNode.visits)
    }
  }
  
  def updateUCB1Tuned(result:Int) {
    if (result > 0)
      this.wins += result
    this.visits += 1
    this.payoffs += result
  }
  
  /**
   * Selection strategies
   */
  
  // Upper Confidence Bound for Trees
  def UCT():QuoridorNode = {
    val constant:Int = 2
    // calculate UCT Value the way down
    this.childNodes.foreach(node => {
      node.uct_value = node.wins / node.visits + sqrt(constant * log(this.visits) / node.visits)
    })
    // sort
    this.childNodes.sortWith(
        (node1, node2) => 
          (node1.wins / node1.visits + sqrt(constant * log(this.visits)/node1.visits)) < 
          ((node2.wins / node2.visits + sqrt(constant * log(this.visits)/node2.visits))) 
          ).takeRight(1)(0)    
  }
  
  
  // Objective Monte-Carlo
  def OMC():QuoridorNode = {
    // best move value
	val node0:QuoridorNode = this.childNodes.sortWith((n1, n2) => n1.value < n2.value).takeRight(1)(0)
	val v_0:Double = node0.value
	
	// n_p
	val n_p = this.visits
    
    // total urgenciness of child nodes
    var totalUrgency:Double = 0
	
    // calculate the urgency
    this.childNodes.foreach(child => {
      if (child.visits > 0) {        
        // expectation
        val expectation:Double = child.value
        
        // standard deviation
		val sigma:Double = sqrt( ( this.wins * pow((1 - expectation), 2) + (this.visits - this.wins) * pow((1 + expectation), 2) ) / this.visits )
        
        // urgency function update to child node
        val erf:Erf = new Erf
        child.urgency = 1 - erf.erf( (v_0 - child.value) / (sqrt(2) * sigma) )
        //println("U(i) = " + child.urgency + ". Sigma: " + sigma + ". Value:" + child.value + ". V0: " + node0.value + ". Visits:" + child.visits + ". Wins:" + child.wins + ". Parent:" + child.parentNode.visits)
        
        // update total urgency
        totalUrgency += child.urgency
      }
    })
    
    // store fairness values
    this.childNodes.foreach(n1 => {
      n1.fairness = ((n_p * n1.urgency) / (n1.visits * totalUrgency))
    })
    
    // sort and select
    val m:QuoridorNode = this.childNodes.sortWith(
        (n1, n2) => 
          ((n_p * n1.urgency) / (n1.visits * totalUrgency)) <
          ((n_p * n2.urgency) / (n2.visits * totalUrgency))
          ).takeRight(1)(0)
    m
  }
  
  // Probability Better than the Best Moves
  def PBBM():QuoridorNode = {
    // best move value
    val node0 = this.childNodes.sortWith((n1, n2) => (n1.value) < (n2.value)).takeRight(1)(0)
    
    // v_0
    val v_0:Double = node0.value
    // e(x_0)
    val e_0:Double = node0.value
    // standard deviation
    val sigma_0:Double = sqrt( 1 - pow(e_0, 2) )
    // n_p
    val n_p:Double = this.visits
    
    // total urgenciness of child nodes
    var totalUrgency:Double = 0
    
    this.childNodes.foreach(child => {
      if (child.visits > 0) {
        // expectation
        val expectation:Double = child.value
        
        // standard deviation
		val sigma:Double = sqrt( ( this.wins * pow((1 - expectation), 2) + (this.visits - this.wins) * pow((1 + expectation), 2) ) / this.visits )
        // urgency function update to child node
        child.urgency = exp( -2.4 * (v_0 - child.value) / sqrt(2 * (pow(sigma_0, 2) + pow(sigma, 2))) )
        // update total urgency
        totalUrgency += child.urgency        
      }
    })
    
    // store fairness value
    this.childNodes.foreach(n1 => {
      n1.fairness = ( (n_p * n1.urgency) / (n1.visits * totalUrgency) )
    })
    
    // sort and select
    this.childNodes.sortWith((n1, n2) => 
      	( (n_p * n1.urgency) / (n1.visits * totalUrgency) ) < 
      	( (n_p * n2.urgency) / (n2.visits * totalUrgency) )
        ).takeRight(1)(0)
  }
  
  // Upper Confidence Bound 1 Tuned
  def UCB1TUNED():QuoridorNode = {
    this
  }
  
  
  //////////////////////////////////////////////////////////////////////
  ///////////////////// Modifications for Quoridor /////////////////////
  //////////////////////////////////////////////////////////////////////
  
  /**
   * Factor functions - each selection has its own factor
   */
  def factorUCT(node:QuoridorNode):Double = {
    val x1 = 0.06
    val x2 = 0.02
    val opp = node.board.nbWalls((node.player + 1) % 2)
    val my = node.board.nbWalls(node.player)
    x1 * my + x2 * opp
  }
  
  def factorUrgencyOMC(node:QuoridorNode):Double = {
    // only add this portion if this is a pawn move
    //if (node.move._1 == "P") {
      val x1 = 1
      val x2 = 0.5
      val opp = node.board.nbWalls((node.player + 1) % 2)
      val my = node.board.nbWalls(node.player)    
      node.omc_urgency = (x1 * my + x2 * opp) / (my + opp) // update this value
      node.omc_urgency
    //}
    //0 
  }
  
  def factorFairnessOMC(node:QuoridorNode):Double = {
    val x1 = 0.36
    val x2 = 0.005
    val opp = node.board.nbWalls((node.player + 1) % 2)
    val my = node.board.nbWalls(node.player)    
    node.omc_fairness = exp(x1 * my + x2 * opp) // update this value
    node.omc_fairness    
  }
  
  def factorUrgencyPBBM(node:QuoridorNode):Double = {
    // only add this portion if this is a pawn move
    //if (node.move._1 == "P") {
      val x1 = 0.5
      val x2 = 0.05
      val opp = node.board.nbWalls((node.player + 1) % 2)
      val my = node.board.nbWalls(node.player)    
      node.pbbm_urgency = (x1 * my + x2 * opp) / (my + opp) // update this value
      node.pbbm_urgency
    //}
    //0    
  }
  
  def factorFairnessPBBM(node:QuoridorNode):Double = {
    val x1 = 0.1
    val x2 = 0.03
    val opp = node.board.nbWalls((node.player + 1) % 2)
    val my = node.board.nbWalls(node.player)
    node.pbbm_fairness =  exp(x1 * my + x2 * opp)
    node.pbbm_fairness
  }
  
  
  /**
   * UCT Quoridor
   *
   */
  def UCTQ():QuoridorNode = {
    val constant:Int = 2
    // calculate UCT Value the way down
    this.childNodes.foreach(node => {
      node.uct_value = node.wins / node.visits + sqrt(constant * log(this.visits) / node.visits) + factorUCT(node)
    })
    // sort
    this.childNodes.sortWith(
        (node1, node2) => 
          (node1.wins / node1.visits + sqrt(constant * log(this.visits)/node1.visits)) + factorUCT(node1) < 
          ((node2.wins / node2.visits + sqrt(constant * log(this.visits)/node2.visits)))  + factorUCT(node2)
          ).takeRight(1)(0)    
  }
  
  
  // Objective Monte-Carlo for Quoridor
  def OMCQ():QuoridorNode = {
    // best move value
	val node0:QuoridorNode = this.childNodes.sortWith((n1, n2) => n1.value < n2.value).takeRight(1)(0)
	val v_0:Double = node0.value
	
	// n_p
	val n_p = this.visits
    
    // total urgenciness of child nodes
    var totalUrgency:Double = 0
	
    // calculate the urgency
    this.childNodes.foreach(child => {
      if (child.visits > 0) {
        // expectation
        val expectation:Double = child.value
        
        // standard deviation
		val sigma:Double = sqrt( ( this.wins * pow((1 - expectation), 2) + (this.visits - this.wins) * pow((1 + expectation), 2) ) / this.visits )
        
        // urgency function update to child node
        val erf:Erf = new Erf
        child.urgency = 1 - erf.erf( (v_0 - child.value) / (sqrt(2) * sigma) ) + factorUrgencyOMC(child)
        
        // update total urgency
        totalUrgency += child.urgency
      }
    })
    
    // store fairness values in node
    this.childNodes.foreach(node => {
      node.fairness = ((n_p * node.urgency) / (node.visits * totalUrgency)) + factorFairnessOMC(node)
    })
    
    // sort and select
    val m:QuoridorNode = this.childNodes.sortWith(
        (n1, n2) => 
          ((n_p * n1.urgency) / (n1.visits * totalUrgency)) + factorFairnessOMC(n1) <
          ((n_p * n2.urgency) / (n2.visits * totalUrgency)) + factorFairnessOMC(n2)
          ).takeRight(1)(0)              
    m
  }
  
  
  // PBBM for Quoridor
  def PBBMQ():QuoridorNode = {
    // best move value
    val node0 = this.childNodes.sortWith((n1, n2) => (n1.value) < (n2.value)).takeRight(1)(0)
    
    // v_0
    val v_0:Double = node0.value
    // e(x_0)
    val e_0:Double = (2 * node0.wins - node0.visits) / node0.visits
    // standard deviation
    val sigma_0:Double = sqrt( 1 - pow(e_0, 2) )
    // n_p
    val n_p:Double = this.visits
    
    // total urgenciness of child nodes
    var totalUrgency:Double = 0
    
    this.childNodes.foreach(child => {
      if (child.visits > 0) {
        // expectation
        val expectation:Double = child.value
        
        // standard deviation
		val sigma:Double = sqrt( ( this.wins * pow((1 - expectation), 2) + (this.visits - this.wins) * pow((1 + expectation), 2) ) / this.visits )
        // urgency function update to child node
        child.urgency = exp( -2.4 * (v_0 - child.value) / sqrt(2 * (pow(sigma_0, 2) + pow(sigma, 2))) ) 
        				+ factorUrgencyPBBM(child) // a factor for PBBM
        // update total urgency
        totalUrgency += child.urgency        
      }
    })
    
    // store fairness value
    this.childNodes.foreach(n1 => {
      n1.fairness = ( (n_p * n1.urgency) / (n1.visits * totalUrgency) ) + factorFairnessPBBM(n1)
    })
    
    // sort and select
    this.childNodes.sortWith((n1, n2) => 
      	( (n_p * n1.urgency) / (n1.visits * totalUrgency) ) + factorFairnessPBBM(n1) < 
      	( (n_p * n2.urgency) / (n2.visits * totalUrgency) ) + factorFairnessPBBM(n2)
        ).takeRight(1)(0)
  }
  
  // UCB1 Tuned for Quoridor
  def UCB1TUNEDQ():QuoridorNode = {
    null
  }
  
  //////////////////////////////////////////////////////////////////////
  ///////////////////// Final Move selection strategies ////////////////
  //////////////////////////////////////////////////////////////////////
  def maxChild():(String, Int, Int) = {
    this.childNodes.sortWith((n1,n2)=> (n1.value) < (n2.value)).takeRight(1)(0).move
  }
  
  def robustChild():(String, Int, Int) = {
    this.childNodes.sortWith((n1,n2)=> (n1.visits) < (n2.visits)).takeRight(1)(0).move
  }
  
  def robustMaxChild():(String, Int, Int) = {
    this.childNodes.sortWith((n1,n2)=> (n1.visits + n1.value) < (n2.visits + n2.value)).takeRight(1)(0).move
  }
  
  def secureChild():(String, Int, Int) = {
    this.childNodes.sortWith((n1,n2)=> (n1.visits) < (n2.visits)).takeRight(1)(0).move
  }
}