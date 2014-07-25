package testbed.parallelism

import games.nodes.QuoridorNode
import games.Quoridor
import games.nodes.QuoridorNode
import java.util.Random
import scala.collection.mutable.ArrayBuffer
import scala.math._
import games.utils._

class MCTS_Quoridor_Par(state:(Quoridor, Int), iterations:Int, s:Int, t:Int) {
  val rootState:(Quoridor, Int) = state
  val itermax:Int = iterations
  val steps:Int = s
  val timeLeft:Int = t
  
  
  def run(ptype:String, finalmove:String):(String, Int, Int) = {
	  var rootNode:QuoridorNode = new QuoridorNode(state = this.rootState)
	  val (rootBoard:Quoridor, rootPlayer:Int) = this.rootState
	  
	  var counter:Int = 0
	  
	  println("Branching:" + rootNode.untriedMoves.length)
	  
	  // update branching factor
	  QuoridorMeasurements.currentBranchingFactor = rootNode.untriedMoves.length
	  
	  // current max depth
	  var currentMaxDepth = 0
	  
	  // start iterations
	  (0 to this.itermax).par.foreach(item => {
	    var node:QuoridorNode = rootNode
	    var state:(Quoridor, Int) = (rootBoard.cloneBoard, rootPlayer)
	    var (board:Quoridor, player:Int) = state
	    
	    //println("Iteration " + (counter + 1) + ". Branching:" + node.untriedMoves.length)
	    print(counter + 1 + " ")
	    counter += 1
	    
	    // Selection 
	    while (node.untriedMoves.length == 0 && node.childNodes.length > 0) {
	      // choosing different algorithm
	      if (ptype == AlgorithmNames.OMC)
	        node = node.OMC()
	      else if (ptype == AlgorithmNames.UCT)
	        node = node.UCT()
	      else if (ptype == AlgorithmNames.PBBM)
	        node = node.PBBM()
	      else if (ptype == AlgorithmNames.UCB1TUNED)
	        node = node.UCB1TUNED()
	      // Custom quoridor options  
	      else if (ptype == AlgorithmNames.UCTQ)
	        node = node.UCTQ()
	      else if (ptype == AlgorithmNames.OMCQ)
	        node = node.OMCQ()
	      else if (ptype == AlgorithmNames.PBBMQ)
	        node = node.PBBMQ()
	      else if (ptype == AlgorithmNames.UCB1TUNEDQ)
	        node = node.UCB1TUNEDQ()
	      else
	        node = node.OMC()
	        
	      board = node.board
	      player = node.player
	    }
	    
	    // Expansion
	    // println("2. Expansion")
	    if (node.untriedMoves.length != 0) {
	      // choose a random move using java random
	      val rand = new Random(System.currentTimeMillis());
	      val random_index = rand.nextInt(node.untriedMoves.length);	      
	      val m:(String, Int, Int) = node.untriedMoves(random_index)
	      
	      // play the action
	      state = ( (node.board.cloneBoard().playAction(m, player)), (player + 1) % 2 )
	      // add new child node
	      node = node.addChild(m, state, this.steps, this.timeLeft)
	      // update player and board
	      board = state._1
	      player = state._2
	    }
	    
	    // Simulation - Self playing mode
	    var rollplayer:Int = player // self player
	    var rollboard:Quoridor = board.cloneBoard() // self board
	    var rollmove:ArrayBuffer[(String, Int, Int)] = QuoridorUtils.get_moves(rollboard, rollplayer)
	    
	    // Start simulations!
	    // println("3. Simulation")
	    while (rollboard.isFinished == false && rollmove.length > 0) {	
	      val rand = new Random(System.currentTimeMillis())
	      var random_index = rand.nextInt(rollmove.length)
        	      
	      rollboard = rollboard.playAction(rollmove(random_index), rollplayer)	        	        	        
	      rollplayer = (rollplayer + 1) % 2
	      rollmove = QuoridorUtils.get_moves(rollboard, rollplayer)  
	    }	    
	    
	    // Depth counter
	    var depthCounter = -1
	    
	    // Back propagation	    
	    while (node != null) {
	      //println("4. Backpropagate for: " + rootPlayer)
	      // update depth counter
	      depthCounter += 1
	      
	      var result:Int = 0
	      if (rollboard.isPlayerWin(rootPlayer))
	        result = 1
	      
	      // Select different backpropagate strategies
	      if (ptype == AlgorithmNames.OMC)
	        node.updateOMC(result)
	      else if (ptype == AlgorithmNames.UCT || ptype == AlgorithmNames.UCTQ)
	        node.updateUCT(result)
	      else if (ptype == AlgorithmNames.PBBM || ptype == AlgorithmNames.PBBMQ)
	        node.updatePBBM(result)
	      else if (ptype == AlgorithmNames.UCB1TUNED || ptype == AlgorithmNames.UCB1TUNEDQ)
	        node.updateUCB1Tuned(result)
	      else
	        node.updateOMC(result)
	        
	      node = node.parentNode
	    }
	    
	    // Check to update the current Max Depth
	    if (depthCounter > currentMaxDepth)
	      currentMaxDepth = depthCounter
	  })
	  
	  // update the max depth to measurement
	  QuoridorMeasurements.currentTreeMaxDepth = currentMaxDepth
	  
	  // statistics
	  println("\n Results:" + rootNode.childNodes.length)	  
	  rootNode.childNodes.foreach(item => {	    
	    println("Action:" + item.move + ", Payoffs: " + item.payoffs + ", Visit: " + item.visits + ", Win: " + item.wins + ", Value:" + item.value + 
	        ", Urgency:" + item.urgency + ", fUrgency:" + item.omc_urgency + ", fFairness:" + item.omc_fairness + ", UCT:" + item.uct_value)
	  })
	  
	  
	  // Final move selection
	  var result:(String, Int, Int) = rootNode.robustChild
	  if (finalmove == "max")
	    result = rootNode.maxChild
	  else if (finalmove == "robust")
	    result = rootNode.robustChild
	  else if (finalmove == "robustmax")
	    result = rootNode.robustMaxChild
	  else (finalmove == "secure")
	    result = rootNode.secureChild
	  
	  
	  println("\nFinal move of player " + rootNode.player + " is:" + result)
	  result
  }
  
  
  def start(ptype:String, finalmove:String):(String, Int, Int) = {
    println("Start mcts step: " + this.steps)
    val move:(String, Int, Int) = this.run(ptype, finalmove:String)
    move
  }
}