package application

import games._
import games.players._
import games.utils._

object QuoridorGame {
  def run(agent1: String, agent2:String, prefix:String, iterations:Int) = {
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
  
	val itermax:Int = iterations
	  
	// initial board
	println(board.toString)
    
    // initial loggers
    val filename:String = (System.currentTimeMillis / 1000).toString + "_" + // timestamp 
    						itermax + "_" + agent1 + "_" + agent2 + ".txt"  // agents + iterations
    var logger = new Logger(prefix + filename)
    
    // logs
    logger.log(players.PLAYER1.toString) // first player
    logger.log(agent1)
    logger.log(agent2)
    logger.log(itermax.toString)    
	  
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
	  val micros = (System.nanoTime - now) / 1000 / 1000000
	  println("Time taken: " + micros + " seconds.")
	  // print board
	  println(board.toString)
	  
	  // logger
	  logger.log(playerJustMoved.toString) // player just moved
	  logger.log(move._1 + " " + move._2 + " " + move._3) // move
	  logger.log(micros.toString) // time taken
	}
    
	println("Player: " + board.playerJustMoved + " wins!")
	
	// logger
	logger.log(board.playerJustMoved.toString) // player who won
	logger.log(steps.toString) // step taken	
	if (board.playerJustMoved == 0) // algorithm that won
	  logger.log(agent1)
	else
	  logger.log(agent2)
	  
	// save to file
	logger.save
  }
}