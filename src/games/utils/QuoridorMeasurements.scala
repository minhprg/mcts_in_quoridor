package games.utils

import scala.collection.mutable.ArrayBuffer
import games.nodes.QuoridorNode
import games.nodes.QuoridorNode

class QuoridorMeasurements {
  /**
   * Variables
   */
  var nbMoves:Int = 0 // number of moves of a game
  
  /**
   * Per Move info
   */
   // time taken per move
  var timePerMove:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]()
   // average branching factor of each move
  var averageBranchingFactor:ArrayBuffer[(Double, Double)] = new ArrayBuffer[(Double, Double)]()
  // the maximum depth of the tree of a move
  var depthOfTree:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]()
  // number of iterations of each move
  var numberOfIterationsOfEachMove:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]() 
  // each move info
  var moves:ArrayBuffer[(Int, String, Int, Int)] = new ArrayBuffer[(Int, String, Int, Int)]()
  
  /**
   * Per Player
   */
  // total time taken per player
  var totalTimeTakenPerPlayer:(Double, Double) = (0,0)
  // total number of moves per player
  var totalNumberOfMovesPerPlayer:(Int, Int) = (0,0)
  // average of the average of branching factor per player
  var averageOfAverageOfBranchingFactorPerPlayer:(Double, Double) = (0,0)
  // average max tree depth per player
  var averageMaxTreeDepthPerPlayer:(Double, Double) = (0,0)
  // average number of iterations per player
  var averageNumberOfIterationsPerPlayer:(Double, Double) = (0,0)
  
  /**
   * Per Game info
   */
  // fixed time per move (if we limit time)
  var fixedTimePerMove:Int = 0
  // total time taken
  var totalTimeTaken:Double = 0
  // average time per move, only use when we consider iteration numbers
  var averageTimePerMove:Double = 0
  // average of the average branching factor of a game
  var averageOfAverageBranchingFactor:Double = 0
  // average tree depth
  var averageTreeDepth:Double = 0
  // average number of iterations per game
  var averageNumberOfIterations:Double = 0
  
  
  // player selection strategies
  var playerTypes:(String, String) = ("null","null")
  // number of iterations per move
  var numberOfIterations:(Int,Int) = (0,0)
  // getChild strategy
  var getChildStrategy:(String, String) = ("e", "e")
  // final move selection
  var playerFinalMove:(String, String) = ("null", "null")
  // who is the winner
  var winner:Int = -1
   
  /**
   * Methods
   */
  // calculate stuffs
  def analyse():Unit = {
    println("Start analysing....")
    this.calculateAverageMaxTreeDepthPerPlayer
    this.calculateAverageNumberOfIterations
    this.calculateAverageNumberOfIterationsPerPlayer
    this.calculateAverageOfAverageBranchingFactor
    this.calculateAverageOfTheAverageBranchingFactorPerPlayer
    this.calculateAverageTreeDepth
    this.calculateTotalNumberOfMovesPerPlayer
    this.calculateTotalTimeTaken
    this.calculateTotalTimeTakenPerPlayer
  }
  
  // log file creation
  def createLogFile(filePath:String, fileName:String):Unit = {
    // Create an instance of logger class
    var logger = new Logger(filePath + fileName)    
    
    /**
     * Per game info
     */
    // Player strategy
    logger.log(this.playerTypes._1)
    logger.log(this.playerTypes._2)
    
    // Player Iterations
    logger.log(this.numberOfIterations._1.toString)
    logger.log(this.numberOfIterations._2.toString)
    
    // Player get child moves
    logger.log(this.getChildStrategy._1.toString())
    logger.log(this.getChildStrategy._2.toString())
    
    // Player Final move
    logger.log(this.playerFinalMove._1)
    logger.log(this.playerFinalMove._2)
    
    // Win Player
    logger.log(this.winner.toString)
    
    // Total time taken
    logger.log(this.totalTimeTaken.toString)
    
    // Total moves taken
    logger.log(this.nbMoves.toString)
    
    // Average of the average branching factor of a game
    logger.log(this.averageOfAverageBranchingFactor.toString)
    
    // Average max tree depth of the game
    logger.log(this.averageTreeDepth.toString)
    
    // Average number of iterations per game
    logger.log(this.averageNumberOfIterations.toString)
        
    /**
     * Per player info
     */
    // total time taken per player
    logger.log(this.totalTimeTakenPerPlayer._1 + " " + this.totalTimeTakenPerPlayer._2)
    
    // total number of moves per player (steps)
    logger.log(this.totalNumberOfMovesPerPlayer._1 + " " + this.totalNumberOfMovesPerPlayer._2)
    
    // average of the average of branching factor per player
    logger.log(this.averageOfAverageOfBranchingFactorPerPlayer._1 + " " + this.averageOfAverageOfBranchingFactorPerPlayer._2)
    
    // average max tree depth per player
    logger.log(this.averageMaxTreeDepthPerPlayer._1 + " " + this.averageMaxTreeDepthPerPlayer._2)
    
    // average number of iterations per player
    logger.log(this.averageNumberOfIterationsPerPlayer._1 + " " + this.averageNumberOfIterationsPerPlayer._2)
    
    /**
     * Detailed information - Per Move
     */
    // Time of each move
    this.timePerMove.foreach(move => {
      logger.log(move._1 + " " + move._2)
    })    
    
    // Average branching factor of each move
    this.averageBranchingFactor.foreach(move => {
      logger.log(move._1 + " " + move._2)
    })
    
    // Depth of the tree of each move
    this.depthOfTree.foreach(move => {
      logger.log(move._1 + " " + move._2)
    })
    
    // Number of iterations of each move
    this.numberOfIterationsOfEachMove.foreach(move => {
      logger.log(move._1 + " " + move._2)
    })
    
    // Moves
    this.moves.foreach(move => {
      logger.log(move._1 + " " + move._2.toString + " " + move._3.toString)
    })
    
    logger.save
  }
  
  /**
   * Calculations for per player
   */
  def calculateTotalTimeTakenPerPlayer():Unit = {
    var player1:Double = 0
    var player2:Double = 0
    
    this.timePerMove.foreach(item => {
      if (item._1 == 0) {
        player1 = player1 + item._2
      } 
      else {
        player2 = player2 + item._2
      }
    })
    this.totalTimeTakenPerPlayer = (player1, player2)
  }
  
  def calculateTotalNumberOfMovesPerPlayer():Unit = {
    var counter1:Int = 0
    var counter2:Int = 0
    this.moves.foreach(item => {
      if (item._1 == 0) {
        counter1 += 1
      } 
      else {
        counter2 += 1
      }
    })
    this.totalNumberOfMovesPerPlayer = (counter1, counter2)
  }
  
  def calculateAverageOfTheAverageBranchingFactorPerPlayer():Unit = {
    var total1:Double = 0
    var total2:Double = 0
    var counter1 = 0
    var counter2 = 0
    
    this.averageBranchingFactor.foreach(item => {
    	if (item._1 == 0) {
    	  total1 = total1 + item._2
    	  counter1 += 1
    	}
    	else {
    	  total2 = total2 + item._2
    	  counter2 += 1
    	}
    })
    
    this.averageOfAverageOfBranchingFactorPerPlayer = (total1 / counter1, total2 / counter2)
  }
  
  def calculateAverageMaxTreeDepthPerPlayer():Unit = {
    var total1:Double = 0
    var total2:Double = 0
    var counter1 = 0
    var counter2 = 0
    
    this.depthOfTree.foreach(item => {
      if (item._1 == 0) {
    	  total1 = total1 + item._2
    	  counter1 += 1
    	}
      else {
        total2 = total2 + item._2
        counter2 += 1
      }
    })
    
    this.averageMaxTreeDepthPerPlayer = (total1 / counter1, total2 / counter2)
  }
  
  def calculateAverageNumberOfIterationsPerPlayer():Unit = {
    var total1:Double = 0
    var total2:Double = 0
    var counter1 = 0
    var counter2 = 0
    
    this.numberOfIterationsOfEachMove.foreach(item => {
      if (item._1 == 0) {
    	  total1 = total1 + item._2
    	  counter1 += 1
    	}
      else {
        total2 = total2 + item._2
        counter2 += 1
      }
    })
    
    this.averageNumberOfIterationsPerPlayer = (total1 / counter1, total2 / counter2)
  }
  
  /**
   * Calculations for per game
   */
  
  def calculateTotalTimeTaken():Unit = {
    this.totalTimeTaken = this.nbMoves * this.fixedTimePerMove
  }
  
  def calculateAverageOfAverageBranchingFactor():Unit = {
    var total:Double = 0
    this.averageBranchingFactor.foreach(item => {
      total = total + item._2
    })
    this.averageOfAverageBranchingFactor = total / this.averageBranchingFactor.length
  }
  
  def calculateAverageTreeDepth():Unit = {
    var total = 0
    this.depthOfTree.foreach(item => {
      total = total + item._2
    })
    this.averageTreeDepth = total / this.depthOfTree.length
  }
  
  def calculateAverageNumberOfIterations():Unit = {
    var total = 0
    this.numberOfIterationsOfEachMove.foreach(item => {
      total = total + item._2
    })
    this.averageNumberOfIterations = total / this.numberOfIterationsOfEachMove.length
  }
}

object QuoridorMeasurements {
  var currentBranchingFactor = 0
  var currentTimePerMove = 0
  var currentDepthOfTree = 0
  var currentTreeMaxDepth = 0
  var currentAverageBranchingFactor:Double = 0 // average branching factor of a move
  var currentNumberOfIterations = 0 // number of iterations of a move   
}