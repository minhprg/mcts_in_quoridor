package games.utils

import scala.collection.mutable.ArrayBuffer

class QuoridorMeasurements {
  /**
   * Variables
   */
  var nbMoves:Int = 0 // number of moves of a game
  
  // Per move info
  var timePerMove:ArrayBuffer[Int] = new ArrayBuffer[Int]() // time taken per move
  var wallsLeftOfPlayers:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]() // walls left of each player after each move
  var branchingFactor:ArrayBuffer[Int] = new ArrayBuffer[Int]() // branching factor of each move
  var depthOfTree:ArrayBuffer[Int] = new ArrayBuffer[Int]() // the maximum depth of the tree of a move
  var distanceToGoal:ArrayBuffer[Int] = new ArrayBuffer[Int]() // distance to the goal of each move
  var moves:ArrayBuffer[(String, Int, Int)] = new ArrayBuffer[(String, Int, Int)]() // each move info  
  
  // Per game info
  var totalTimeTaken:Int = 0 
  var averageTimePerMove:Double = 0
  var maxDepthOfTree:Int = 0 // measure the max depth of the game tree
  var averageBranchingFactor:Double = 0
  var averageMoveDepth:Int = 0
  var playerTypes:(String, String) = ("null","null") // selection strategy
  var numberOfIterations:(Int,Int) = (0,0) // number of iterations of each player
  var playerFinalMove:(String, String) = ("null", "null") // final move selection
  var winner:Int = -1
   
  /**
   * Methods
   */
  def analyse():Unit = {
    this.calculateTotalTimeTaken
    this.calculateAverageTimePerMove
    this.calculateAverageBranchingFactor
    this.calculateAverageMoveDepth
  }
  
  def calculateTotalTimeTaken():Unit = {
    var total:Int = 0
    this.timePerMove.foreach(item => { total += item })
    this.totalTimeTaken = total
  }
  
  def calculateAverageTimePerMove():Unit = {
    var total:Int = 0
    this.timePerMove.foreach(item => { total += item })
    this.averageTimePerMove = total / this.nbMoves
  }
  
  def calculateAverageBranchingFactor():Unit = {
    var total = 0
    this.branchingFactor.foreach(item => { total += item })
    var average:Int = total / this.nbMoves
    this.averageBranchingFactor = average
  }
  
  def calculateAverageMoveDepth():Unit = {
    var total:Int = 0
    this.depthOfTree.foreach(item => { total += item })
    this.averageMoveDepth = total / this.nbMoves
  }
  
  def createLogFile(filePath:String, fileName:String):Unit = {
    // Create an instance of logger class
    var logger = new Logger(filePath + fileName)
    
    // Player strategy
    logger.log(this.playerTypes._1)
    logger.log(this.playerTypes._2)
    
    // Player Iterations
    logger.log(this.numberOfIterations._1.toString)
    logger.log(this.numberOfIterations._2.toString)
    
    // Player Final move
    logger.log(this.playerFinalMove._1)
    logger.log(this.playerFinalMove._2)
    
    // Win Player
    logger.log(this.winner.toString)
    
    // Total time taken
    logger.log(this.totalTimeTaken.toString)
    
    // Total moves taken
    logger.log(this.nbMoves.toString)
    
    // Average time
    logger.log(this.averageTimePerMove.toString)
    
    // Average branching factor
    logger.log(this.averageBranchingFactor.toString)
    
    // Average tree depth
    logger.log(this.averageMoveDepth.toString)
    
    // Moves
    this.moves.foreach(move => {
      logger.log(move._1 + " " + move._2.toString + " " + move._3.toString)
    })
    
    // Time of each move
    this.timePerMove.foreach(move => {
      logger.log(move.toString)
    })
    
    // Walls left
    this.wallsLeftOfPlayers.foreach(move => {
      logger.log(move._1 + " " + move._2)
    })
    
    // Branching factor of each move
    this.branchingFactor.foreach(move => {
      logger.log(move.toString)
    })
    
    // Depth of the tree of each move
    this.depthOfTree.foreach(move => {
      logger.log(move.toString)
    })
    
    // Distance to goal of each move
    this.distanceToGoal.foreach(move => {
      logger.log(move.toString)
    })
    
    logger.save
  }
}

object QuoridorMeasurements {
  var currentBranchingFactor = 0
  var currentTimePerMove = 0
  var currentDepthOfTree = 0
  var currentDistanceToGoal = 0
  var currentTreeMaxDepth = 0
}