package games.players
import games._

abstract class Agent {
  def initialize
  def playQuoridor(player:Int, itermax:Int, percepts:Quoridor, step:Int, timeLeft:Int, finalmove:String):(String,Int,Int)
}