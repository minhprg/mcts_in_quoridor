package games.players

import games.Quoridor
import mcts.quoridor._

class HumanAgent {
  def play(percepts: Quoridor, player: Int, step:Int):(String, Int, Int) = {
    var continue:Boolean = true
    while (continue == true) {
    	println("Your move type(P = pawn, H = wall horizon, V = wall vertical):")
	    var moveType = Console.readChar()
	    var moveString = "P"
	    if (moveType == 'V')
	      moveString = "WV"
	    if (moveType == 'H')
	      moveString = "WH"
	    println("Row:")
	    var x = Console.readInt
	    println("Column:")
	    var y = Console.readInt
	    var move = (moveString, x, y)
	    if (percepts.isActionValid(move, player))
	      return move
    }
    return ("P", -1, -1)
  }
}