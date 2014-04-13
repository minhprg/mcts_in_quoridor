package games.players

import games.players.Agent
import games._
import mcts.quoridor._

class UCB1TUNNEDPlayer extends Agent  {
	def initialize():Unit = {
	  
	} 
	
	def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  
	  val agent = new UCB1TUNNED(state, itermax, step, timeLeft)	  
	  agent.start()
	}
}