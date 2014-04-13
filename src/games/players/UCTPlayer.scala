package games.players

import games.players.Agent
import games._
import algorithms._
import mcts.quoridor._

class UCTPlayer extends Agent  {
	def initialize():Unit = {
	  
	} 
	
	def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int):(String,Int,Int) = {
	  val state:(Quoridor, Int) = (percepts, player)	  
	  val agent = new UCT(state, itermax, step, timeLeft)	  
	  agent.start()
	}
}