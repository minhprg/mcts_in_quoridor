package testbed.parallelism

import games.nodes.QuoridorNode
import games.Quoridor
import games.nodes.QuoridorNode
import java.util.Random
import scala.collection.mutable.ArrayBuffer
import scala.math._
import games.utils._
import games.players._
import scala.util.control.Breaks._

class MCTS_Quoridor_Par(state:(Quoridor, Int), iterations:Int, timePerMove:Int, step:Int) {
  val rootState:(Quoridor, Int) = state
  val itermax:Int = iterations
  val timeLimit:Int = timePerMove
  val steps:Int = step    
  
  
  def run(ptype:String, simulation:String, finalmove:String):(String, Int, Int) = {
	  var rootNode:QuoridorNode = new QuoridorNode(state = this.rootState, simulation = simulation)
	  val (rootBoard:Quoridor, rootPlayer:Int) = this.rootState
	  
	  var iterationsCounter:Int = 0
	  
	  println("Branching:" + rootNode.untriedMoves.length)
	  
	  // update branching factor
	  QuoridorMeasurements.currentBranchingFactor = rootNode.untriedMoves.length
	  
	  // current max depth
	  var currentMaxDepth = 0
	  
	  /**
	   * ***************************
	   * Start full MCTS Iterations
	   * ***************************
	   */
	  val startTime = System.nanoTime // fixed start time
	  var endTime = System.nanoTime // end time init
	  
	  //for (i <- 0 until this.itermax) {
	  // it will stop when still has time
	  (1 to 1000000000).par.foreach(item => {
	    // TIMER CHECK FIRST
	    if (((endTime - startTime) / 1000 / 1000000) > timePerMove)
	      break
	      
	    var node:QuoridorNode = rootNode
	    var state:(Quoridor, Int) = (rootBoard.cloneBoard, rootPlayer)
	    var (board:Quoridor, player:Int) = state
	    
	    // print(iterationsCounter + 1 + " ")
	    iterationsCounter += 1
	    
	    /**
	     *  Selection
	     */ 
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
	    
	    /**
	     * Expansion
	     */
	    if (node.untriedMoves.length != 0) {
	      // choose a random move using java random
	      val rand = new Random(System.currentTimeMillis());
	      val random_index = rand.nextInt(node.untriedMoves.length);	      
	      val m:(String, Int, Int) = node.untriedMoves(random_index)
	      
	      // play the action
	      state = ( (node.board.cloneBoard().playAction(m, player)), (player + 1) % 2 )
	      // add new child node
	      node = node.addChild(m, state, this.steps)
	      // update player and board
	      board = state._1
	      player = state._2
	    }
	    
	    /**
	     * Simulation
	     */
	    var rollplayer:Int = player // self player
	    var rollboard:Quoridor = board.cloneBoard() // self board	    	    
	    // get move selection
	    var rollmove:ArrayBuffer[(String, Int, Int)] = QuoridorUtils.get_moves(rollboard, rollplayer)	    
	    if (simulation == "a")
	      rollmove = QuoridorUtils.getRandomActions(rollboard, rollplayer)	    
	    
	    var agent = new MinimaxAgent // for minimax evluation case
	    
	    // Start simulations!
	    while (rollboard.isFinished == false) {	
	      val rand = new Random(System.currentTimeMillis())
	      var random_index = rand.nextInt(rollmove.length)
	      
	      /*
	      var tmp_move = rollmove.sortWith(
	        (move1, move2) => 
	          (agent.evaluateForSimulation(rollboard.cloneBoard.playAction(move1, rollplayer), rollplayer)) < 
	          (agent.evaluateForSimulation(rollboard.cloneBoard.playAction(move2, rollplayer), rollplayer)) 
	          ).takeRight(1)(0) 
	      */
	      
	      //println("Rollmoves:" + rollmove)
	      //println("Selected:" + rollmove(random_index))	      
        	      
	      rollboard = rollboard.playAction(rollmove(random_index), rollplayer)
	      //rollboard = rollboard.playAction(tmp_move, rollplayer)
	      
	      //println("Rollboard:\n" + rollboard)
	      
	      rollplayer = (rollplayer + 1) % 2
	      // get move selection
	      if (simulation == "a")
	        rollmove = QuoridorUtils.getRandomActions(rollboard, rollplayer)
	      else  
	        rollmove = QuoridorUtils.get_moves(rollboard, rollplayer)	      
	    }	    
	   	    
	    // Depth counter
	    var depthCounter = -1
	    
	    /**
	     *  Backpropagation
	     */  
	    while (node != null) {
	      // update depth counter
	      depthCounter += 1
	      
	      var result:Int = 0
	      if (rollboard.isPlayerWin(rootPlayer))
	        result = 1
	      
	      // Select different backpropagate strategies
	      if (ptype == AlgorithmNames.OMC || ptype == AlgorithmNames.OMCQ)
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
	      	      
	    // Update the end time
	    endTime = System.nanoTime
	  })
	  /**
	   * End of full MCTS iterations
	   */

	  // call tree helpers to calculate the number of average branching factor
	  var treeHelper:QuoridorTreeUtils = new QuoridorTreeUtils(rootNode)
	  treeHelper.analyseAverageBranchingFactor()
	  QuoridorMeasurements.currentAverageBranchingFactor = treeHelper.averageBranchingFactor
	  println("\n total BF: " + treeHelper.totalNumberOfBranches + ", Nodes:" + treeHelper.totalNumberOfNodes + ", AVG: " + treeHelper.averageBranchingFactor)
	  // update the max depth to measurement
	  QuoridorMeasurements.currentTreeMaxDepth = currentMaxDepth
	  // update number of iterations
	  QuoridorMeasurements.currentNumberOfIterations = iterationsCounter
	  
	  /**
	   * Testing part - SHOULD BE REMOVED when finish
	   */
	  println("Iterations: " + iterationsCounter)
	  //println("Results:" + rootNode.childNodes.length)
	  /*
	  rootNode.childNodes.foreach(item => {	    
	    println("Action:" + item.move + ", Payoffs: " + item.payoffs + ", Visit: " + item.visits + ", Win: " + item.wins + ", Value:" + item.value + 
	        ", Urgency:" + item.urgency + ", Fairness: " + item.fairness + ", OMC_Urgency:" + item.omc_urgency + ", OMC_Fairness:" + item.omc_fairness + ", UCT:" + item.uct_value + 
	        ", PBBM_Urgency:" + item.pbbm_urgency + ", PBBM_Fairness:" + item.pbbm_fairness)
	  })
	  * 
	  */
	  
	  /**
	   * Final Move selections
	   */
	  var result:(String, Int, Int) = rootNode.robustChild
	  if (finalmove == "max")
	    result = rootNode.maxChild
	  else if (finalmove == "robust")
	    result = rootNode.robustChild
	  else if (finalmove == "robustmax")
	    result = rootNode.robustMaxChild
	  else (finalmove == "secure")
	    result = rootNode.secureChild
	  
	  println("\nFinal move of player " + rootNode.player + " (" + ptype + ") is:" + result)
	  
	  // returns
	  result
  }
  
  
  def start(ptype:String, simulation:String, finalmove:String):(String, Int, Int) = {
    println("Start mcts step: " + this.steps)
    val move:(String, Int, Int) = this.run(ptype, simulation, finalmove)
    move
  }
}