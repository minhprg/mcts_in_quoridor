package games.players

import games.players.Agent
import games.Quoridor
import mcts.quoridor._
import games.utils._
import scala.collection.mutable.ArrayBuffer

class OMCPlayer extends Agent {
	def initialize():Unit = {
	  
	} 
	
	/**
	 * Quoridor play method
	 */
	def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  
	  val agent = new OMC(state, itermax, step, timeLeft)	  
	  agent.start()
	}
}