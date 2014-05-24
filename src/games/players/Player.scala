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
  def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  	  
	  
	  var agent = new MCTS_Quoridor(state, itermax, step, timeLeft)
	  
	  if (ptype == AlgorithmNames.OMC || ptype == AlgorithmNames.UCT || 
	      ptype == AlgorithmNames.PBBM || ptype == AlgorithmNames.UCB1TUNED || ptype == AlgorithmNames.UCTQ) {	    
	    return agent.start(ptype)
	  }
	  else
	    return agent.start(AlgorithmNames.OMC)
	  
	  throw new Exception("Failed to playQuoridor in class games.Player!")
  }
}