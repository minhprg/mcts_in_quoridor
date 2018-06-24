package application

import games._
import games.players._
import games.utils._

object QuoridorGame {
  def run(program1:String, program2:String, agent1: String, agent2:String, iteration1:Int, iteration2:Int, simulation1: String, simulation2:String, final1:String, final2:String, timePerMove:Int, prefix:String) = {
	/**
	 * Get arguments to decide agents
	 */
    
    // player information initialization
    var player1 = new Player(agent1)
    var player2 = new Player(agent2)
    
    val itermax1:Int = iteration1
	val itermax2:Int = iteration2
    
    //create a game board
	var board = new Quoridor()
	
    // players
	val players = new games.Players
	
	// game state - initially for player1 = 0
	var state:(Quoridor, Int) = (board, players.PLAYER1)
  
	// steps
	var steps:Int = 0
	
	// measurement initialization
	var measurement = new QuoridorMeasurements
	  
	// print initial board
	println(board.toString)
    
    val fileName:String = (System.currentTimeMillis / 1000).toString + "_" + // timestamp 
    						itermax1 + "_" + itermax2 + "_" + agent1 + "_" + agent2 + ".txt"  // agents + iterations
    
    // measurement
    measurement.numberOfIterations = (itermax1, itermax2) // update iteration of each player
    measurement.playerTypes = (agent1, agent2) // update the agent strategy of each player
    measurement.playerFinalMove = (final1, final2) // update the final move of each player
    measurement.getChildStrategy = (simulation1, simulation2) // update get child strategy
    measurement.fixedTimePerMove = timePerMove
    
	// start the game loop  
	while (!board.isFinished) {   
	  var move:(String, Int, Int) = ("", 0, 0)
	  var playerJustMoved = -1
	  val now = System.nanoTime // timer
	  // first move for player1 - this can be dynamically chosen in future!
	  if (board.playerJustMoved == -1) {
	    move = player1.playQuoridor(players.PLAYER1, program1, itermax1, simulation1, final1, timePerMove, board, steps + 1)
	    board.playAction(move, players.PLAYER1)
	    playerJustMoved = players.PLAYER1
	  }
	  else if (board.playerJustMoved == 1) {
	    // player1 turn
	    move = player1.playQuoridor(players.PLAYER1, program1, itermax1, simulation1, final1, timePerMove, board, steps + 1)
	    board.playAction(move, players.PLAYER1)
	    playerJustMoved = players.PLAYER1
	  }
	  else {
	    // player2 turn
	    move = player2.playQuoridor(players.PLAYER2, program2, itermax2, simulation2, final2, timePerMove, board, steps + 1)
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

	  // measurements for per move information
	  measurement.timePerMove += ((playerJustMoved, micros.toInt)) // insert new time per move
	  measurement.averageBranchingFactor += ((playerJustMoved, QuoridorMeasurements.currentAverageBranchingFactor)) // average branching factor of the move
	  measurement.depthOfTree += ((playerJustMoved, QuoridorMeasurements.currentTreeMaxDepth)) // depth of tree of the move
	  measurement.numberOfIterationsOfEachMove += ((playerJustMoved, QuoridorMeasurements.currentNumberOfIterations))
	  measurement.moves += ((playerJustMoved, move._1, move._2, move._3)) // update the move	  
	  
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
  }
}