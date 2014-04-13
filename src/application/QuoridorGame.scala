package application

import games._
import games.players._

object QuoridorGame {
  def run(agent1: String, agent2:String) = {
	/**
	 * Get arguments to decide agents
	 */
    
    // 2 players
    var player1 = new Player(agent1)
    var player2 = new Player(agent1)
    
    //create a game board
	var board = new Quoridor()
	// players
	val players = new games.Players  
	// game state - initially for player1 = 0
	var state:(Quoridor, Int) = (board, players.PLAYER1)
  
  
	// steps
	var steps:Int = 0
  
	val itermax:Int = 200
	  
	// initial board
	println(board.toString)
	  
	while (!board.isFinished) {   
	  var move:(String, Int, Int) = ("", 0, 0)
	  var playerJustMoved = -1
	  val now = System.nanoTime // timer
	  // first move for player1 - this can be dynamically chosen in future!
	  if (board.playerJustMoved == -1) {
	    move = player1.playQuoridor(players.PLAYER1, itermax, board, steps + 1, 0)
	    board.playAction(move, players.PLAYER1)
	    playerJustMoved = players.PLAYER1
	  }
	  else if (board.playerJustMoved == 1) {
	    // player1 turn
	    move = player1.playQuoridor(players.PLAYER1, itermax, board, steps + 1, 0)
	    board.playAction(move, players.PLAYER1)
	    playerJustMoved = players.PLAYER1
	  }
	  else {
	    // player2 turn
	    move = player2.playQuoridor(players.PLAYER2, itermax, board, steps + 1, 0)
	    board.playAction(move, players.PLAYER2)
	    playerJustMoved = players.PLAYER2
	  }
	  // update board
	  steps += 1
	  board.playerJustMoved = playerJustMoved
	  // time alert
	  val micros = (System.nanoTime - now)
	  println("Time taken: " + micros + " seconds.")
	  // print board
	  println(board.toString)
	}
	// check result
	//println(board.toString) // final board state
	// announcement
	println("Player: " + board.playerJustMoved + " wins!")
  }
}