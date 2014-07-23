package application

import games._
import games.players._
import games.utils._

object QuoridorGame {
  def run(agent1: String, agent2:String, prefix:String, iteration1:Int, iteration2:Int, final1:String, final2:String) = {
	/**
	 * Get arguments to decide agents
	 */
    
    // 2 players
    var player1 = new Player(agent1)
    var player2 = new Player(agent2)
    
    //create a game board
	var board = new Quoridor()
	// players
	val players = new games.Players  
	// game state - initially for player1 = 0
	var state:(Quoridor, Int) = (board, players.PLAYER1)
  
	// steps
	var steps:Int = 0
	
	// measurement
	var measurement = new QuoridorMeasurements	
  
	val itermax1:Int = iteration1
	val itermax2:Int = iteration2
	  
	// initial board
	println(board.toString)
    
    
    val fileName:String = (System.currentTimeMillis / 1000).toString + "_" + // timestamp 
    						itermax1 + "_" + itermax2 + "_" + agent1 + "_" + agent2 + ".txt"  // agents + iterations
    /*
    var logger = new Logger(prefix + filename)
    
    logger.log(players.PLAYER1.toString) // first player
    logger.log(agent1)
    logger.log(agent2)
    logger.log(itermax1.toString)
    logger.log(itermax2.toString)
    * 
    */
    
    // measurement
    measurement.numberOfIterations = (itermax1, itermax2) // update iteration of each player
    measurement.playerTypes = (agent1, agent2) // update the agent strategy of each player
    measurement.playerFinalMove = (final1, final2) // update the final move of each player
    
	// start the game loop  
	while (!board.isFinished) {   
	  var move:(String, Int, Int) = ("", 0, 0)
	  var playerJustMoved = -1
	  val now = System.nanoTime // timer
	  // first move for player1 - this can be dynamically chosen in future!
	  if (board.playerJustMoved == -1) {
	    move = player1.playQuoridor(players.PLAYER1, itermax1, board, steps + 1, 0, final1)
	    board.playAction(move, players.PLAYER1)
	    playerJustMoved = players.PLAYER1
	  }
	  else if (board.playerJustMoved == 1) {
	    // player1 turn
	    move = player1.playQuoridor(players.PLAYER1, itermax1, board, steps + 1, 0, final1)
	    board.playAction(move, players.PLAYER1)
	    playerJustMoved = players.PLAYER1
	  }
	  else {
	    // player2 turn
	    move = player2.playQuoridor(players.PLAYER2, itermax2, board, steps + 1, 0, final2)
	    board.playAction(move, players.PLAYER2)
	    playerJustMoved = players.PLAYER2
	  }
	  // update board
	  steps += 1
	  board.playerJustMoved = playerJustMoved
	  // time alert
	  val micros = (System.nanoTime - now) / 1000 / 1000000
	  println("Time taken: " + micros + " seconds.")
	  // print walls left of each player
	  println("Walls left: P1 = " + board.nbWalls(0) + ". P2 = " + board.nbWalls(1))
	  // print board
	  println(board.toString)	
	  
	  // measurement
	  measurement.timePerMove += micros.toInt // insert new time per move
	  measurement.wallsLeftOfPlayers += ((board.nbWalls(0), board.nbWalls(1))) // walls left
	  measurement.branchingFactor += QuoridorMeasurements.currentBranchingFactor // branching factor of the move
	  measurement.depthOfTree += QuoridorMeasurements.currentTreeMaxDepth // depth of tree of the move	  
	  measurement.moves += move // update the move
	  
	  // test
	  println("Tree Depth:" + QuoridorMeasurements.currentTreeMaxDepth)
	}
    
	println("Player: " + board.playerJustMoved + " wins!")
	
	// measurement
	measurement.nbMoves = steps // update steps	
	if (board.playerJustMoved == 0) // update winner
	  measurement.winner = 0
	else
	    measurement.winner = 1
	
	// measurement analyse
	measurement.analyse
	
	// measurement create log file
	measurement.createLogFile(prefix, fileName)
	
	// logger
	/*
	logger.log(board.playerJustMoved.toString) // player who won
	logger.log(steps.toString) // step taken	
	if (board.playerJustMoved == 0) // algorithm that won
	  logger.log(agent1)
	else
	  logger.log(agent2)
	  * 
	  */
	  
	// save to file
	// logger.save
  }
}