package games.players

import games.players.Agent
import games.Quoridor
import mcts.quoridor._
import games.utils._
import scala.collection.mutable.ArrayBuffer

class Player(t:String) extends Agent {
  val ptype:String = t
    
  def initialize():Unit = {
    
  } 
	
  /**
   * Quoridor play method
   */
  def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int, finalmove:String):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  	  
	  
	  var agent = new MCTS_Quoridor(state, itermax, step, timeLeft)
	  
	  if (ptype == AlgorithmNames.OMC || ptype == AlgorithmNames.UCT || 
	      ptype == AlgorithmNames.PBBM || ptype == AlgorithmNames.UCB1TUNED || ptype == AlgorithmNames.UCTQ ||
	      ptype == AlgorithmNames.OMCQ || ptype == AlgorithmNames.PBBMQ || ptype == AlgorithmNames.UCB1TUNEDQ	      
	      ) {	    
	    return agent.start(ptype, finalmove)
	  }
	  else
	    return agent.start(AlgorithmNames.UCTQ, finalmove)
	  
	  throw new Exception("Failed to playQuoridor in class games.Player!")
  }
}