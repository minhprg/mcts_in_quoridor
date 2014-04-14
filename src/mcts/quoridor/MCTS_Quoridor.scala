package mcts.quoridor

import games.nodes.QuoridorNode
import games.Quoridor
import games.nodes.QuoridorNode
import java.util.Random
import scala.collection.mutable.ArrayBuffer
import scala.math._
import games.utils._

class MCTS_Quoridor(state:(Quoridor, Int), iterations:Int, s:Int, t:Int) {
  val rootState:(Quoridor, Int) = state
  val itermax:Int = iterations
  val steps:Int = s
  val timeLeft:Int = t
  
  
  def run(ptype:String):(String, Int, Int) = {
	  var rootNode:QuoridorNode = new QuoridorNode(state = this.rootState)
	  val (rootBoard:Quoridor, rootPlayer:Int) = this.rootState
	  
	  var counter:Int = 0
	  
	  for (i <- 0 until this.itermax) {
	    var node:QuoridorNode = rootNode
	    var state:(Quoridor, Int) = (rootBoard.cloneBoard, rootPlayer)
	    var (board:Quoridor, player:Int) = state
	    
	    println("Iteration " + (counter + 1) + ". Branching:" + node.untriedMoves.length)
	    
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
	    
	    //println("Rollplayer is:" + rollplayer)
	    //println("Roll move is:" + rollmove)
	    
	    // Start simulations!
	    println("3. Simulation")
	    while (rollboard.isFinished == false && rollmove.length > 0) {	
	      val rand = new Random(System.currentTimeMillis())
	      var random_index = rand.nextInt(rollmove.length)
	      
	      rollboard = rollboard.playAction(rollmove(random_index), rollplayer)	        	        	        
	      rollplayer = (rollplayer + 1) % 2
	      rollmove = QuoridorUtils.get_moves(rollboard, rollplayer)
	      
	      println(rollboard)
	      println("Next player:" + rollplayer + ". Iteration:" + counter + ". Step:" + steps)
	      println("Moves = " + rollmove)
	      
	      /*
	      try {
	      
	      }
	      catch {
	        case _ => {
	          println("Error report:")
	          println(rollboard)
	          println("Player:" + rollplayer)
	          println("Moves:" + rollmove)
	          println("Random Index:" + random_index)
	        }
	      }
	      *      
	      */      
	    }	    
	    
	    // Back propagation
	    while (node != null) {
	      //println("4. Backpropagate for: " + rootPlayer)
	      if (rollboard.isPlayerWin(rootPlayer))
	        node.updateChild(1)
	      else
	        node.updateChild(-1)
	      node = node.parentNode
	    }
	  }
	  
	  println("")
	  
	  // statistics	
	  println("Results:" + rootNode.childNodes.length)	  
	  rootNode.childNodes.foreach(item => {
	    println("Node action: " + item.move)
	    println("Node score: " + item.visits + ", " + item.wins)
	  })
	  
	  // Return the appropriate move
	  val result:(String, Int, Int) = rootNode.childNodes.sortWith((n1,n2)=> (n1.visits) < (n2.visits)).takeRight(1)(0).move
	  println("Final move of player " + rootNode.player + " is:" + result)
	  result
  }
  
  
  def start(ptype:String):(String, Int, Int) = {
    println("Start mcts step: " + this.steps)
    val move:(String, Int, Int) = this.run(ptype)
    move
  }
}