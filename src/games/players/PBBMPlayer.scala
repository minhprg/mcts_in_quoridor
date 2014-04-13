package games.players

import games.players.Agent
import mcts.quoridor._
import games._

class PBBMPlayer extends Agent  {
	def initialize():Unit = {
	  
	} 
	
	def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  
	  val agent = new PBBM(state, itermax, step, timeLeft)	  
	  agent.start()
	}
}