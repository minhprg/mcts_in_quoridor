package games.players

import scala.collection.mutable.ArrayBuffer
import games.Quoridor
import algorithms.Minimax
import games.utils._

class MinimaxAgent {
  def successors(board:Quoridor, player:Int):ArrayBuffer[((String, Int, Int), Quoridor, Int)] = {
    var moves:ArrayBuffer[((String, Int, Int), Quoridor, Int)] = new ArrayBuffer[((String, Int, Int), Quoridor, Int)]()
    /*
    board.getLegalPawnMoves(player).foreach(item => {
      moves += ((item, board.cloneBoard().playAction(item, player), (player + 1) % 2))
    })
    
    board.getLegalWallMoves(player).foreach(item => {
      moves += ((item, board.cloneBoard.playAction(item, player), (player + 1) % 2))
    })
    * 
    */
    
    board.getActions(player).foreach(item => {
      moves += ((item, board.cloneBoard.playAction(item, player), (player + 1) % 2))
    })
    
    moves
  }
  
  def cutoff(board:Quoridor, player:Int, depth:Int):Boolean = {
    if (depth == 2) {
      return true
    }
    return board.isFinished
  }
  
  def evaluate(board:Quoridor, player:Int):Double = {
    var score:Double = 0
    /*
    val SPP = board.minStepsBeforeVictory(player)
    val OPP = board.minStepsBeforeVictory((player + 1) % 2)
    val player_manhattan = Math.abs(board.pawns(player)._1 - board.goals(player))
    val opponent_manhattan = Math.abs(board.pawns((player + 1) % 2)._1 - board.goals((player + 1) % 2))
    val MDP = (9 - player_manhattan) / 9
    val MDO = (9 - opponent_manhattan) / 9
    
    val wall_left = board.nbWalls(player) - board.nbWalls((player + 1) % 2)
    
    score = (((81 - SPP.toDouble) / 81) - ((81 - OPP.toDouble) / 81)) + 1 * (MDP - MDO) + wall_left
    * 
    */
    score = board.getScore(player)
    
    return score
  }
  
  def evaluateForSimulation(board:Quoridor, player:Int):Double = {
    var score:Double = 0
    /*
    val player_manhattan = Math.abs(board.pawns(player)._1 - board.goals(player))
    val opponent_manhattan = Math.abs(board.pawns((player + 1) % 2)._1 - board.goals((player + 1) % 2))
    val MDP = (9 - player_manhattan) / 9
    val MDO = (9 - opponent_manhattan) / 9
    
    //val wall_left = board.nbWalls(player) + board.nbWalls((player + 1) % 2)    
    
    score = 20 * (MDP - MDO) + 10 * (board.nbWalls(player) - board.nbWalls((player + 1) % 2))
    * 
    */
    score = board.getScore(player)
    
    return score
  }
  
  def play(board:Quoridor, player:Int, step:Int):(String, Int, Int) = {
    var minimax:Minimax = new Minimax(board, player, this)
    minimax.minimax_decision
  }
}