package testbed.parallelism

import games.Quoridor
import mcts.quoridor._
import games.utils._
import scala.collection.mutable.ArrayBuffer
import algorithms._
import games.players._

class PlayerPar(t:String) {
  val ptype:String = t
    
  def initialize():Unit = {
    
  } 
	
  /**
   * Quoridor play method
   */
  def playQuoridor(player:Int, agentType:String, itermax:Int, simulation:String, finalmove:String, timePerMove:Int, percepts:Quoridor, step:Int):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  	  
	  
	  /**
	   * MONTE CARLO TREE SEARCH PROGRAM
	   */
	  if (agentType == "mcts") {
	    // Initialize the MCTS_Quoridor program with state, iterations, timePerMove
	    var agent = new MCTS_Quoridor_Par(state, itermax, timePerMove, step)	  
	  
	    if (ptype == AlgorithmNames.OMC || ptype == AlgorithmNames.UCT || 
	      ptype == AlgorithmNames.PBBM || ptype == AlgorithmNames.UCB1TUNED || ptype == AlgorithmNames.UCTQ ||
	      ptype == AlgorithmNames.OMCQ || ptype == AlgorithmNames.PBBMQ || ptype == AlgorithmNames.UCB1TUNEDQ	      
	      ) {	    
	      return agent.start(ptype, simulation, finalmove)
	      }
	    else
	      return agent.start(AlgorithmNames.UCTQ, simulation, finalmove)
	  }
	    
	  /**
	   * MINIMAX PROGRAM
	   */
	  if (agentType == "minimax") {
	    var agent = new MinimaxAgent()
	    return agent.play(percepts, player, step)
	  }
	  
	  /**
	   * HUMAN PROGRAM
	   */
	  if (agentType == "human") {
	    var agent = new HumanAgent()
	    return agent.play(percepts, player, step)
	  }
	  
	  throw new Exception("Failed to playQuoridor in class games.Player!")
  }
}