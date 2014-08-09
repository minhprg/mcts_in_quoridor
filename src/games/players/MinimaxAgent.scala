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
    
    val SPP = board.minStepsBeforeVictory(player)
    val OPP = board.minStepsBeforeVictory((player + 1) % 2)
    /*
    val player_manhattan = Math.abs(board.pawns(player)._1 - board.goals(player))
    val opponent_manhattan = Math.abs(board.pawns((player + 1) % 2)._1 - board.goals((player + 1) % 2))
    var goal_side_player = 0
    if (player_manhattan < 4)
      goal_side_player = 1
    var goal_side_opponent = 0
    if (opponent_manhattan < 4)
      goal_side_opponent = 1
    
    val MDP = (9 - player_manhattan) / 9
    val MDO = (9 - opponent_manhattan) / 9
    */
    
    val wall_left = board.nbWalls(player) + board.nbWalls((player + 1) % 2)
    
    //score = 1 * (((81 - SPP.toDouble) / 81) - ((81 - OPP.toDouble) / 81)) + 1 * (MDP - MDO) + goal_side_player + wall_left
    // glendenning
    score = 0.747 * (81 - SPP) / 81 + 0.096 * (81 - OPP) / 81 + 0.327 * wall_left / 10
    // score = 20 * (OPP - SPP) + 10
    //score = board.getScore(player)
    
    return score
  }
  
  def evaluateForSimulation(board:Quoridor, player:Int):Double = {
    var score:Double = 0
        
    val SPP = board.minStepsBeforeVictory(player)
    val OPP = board.minStepsBeforeVictory((player + 1) % 2)
    //val player_manhattan = Math.abs(board.pawns(player)._1 - board.goals(player))
    //val opponent_manhattan = Math.abs(board.pawns((player + 1) % 2)._1 - board.goals((player + 1) % 2))
    //val MDP = (9 - player_manhattan) / 9
    //val MDO = (9 - opponent_manhattan) / 9
    
    val wall_left = board.nbWalls(player) + board.nbWalls((player + 1) % 2)    
    
    //score = 1 * (MDO - MDP) + 1 * (board.nbWalls(player) - board.nbWalls((player + 1) % 2))
    
    //score = 0.747 * SPP + 0.096 * OPP + 0.327 * wall_left
    //score = 20 * (opponent_manhattan - player_manhattan) + 10 + wall_left
    score = 0.747 * (81 - SPP) / 81 + 0.096 * (81 - OPP) / 81 + 0.327 * wall_left / 10
    //score = board.getScore(player)
    
    return score
  }
  
  def play(board:Quoridor, player:Int, step:Int):(String, Int, Int) = {
    var minimax:Minimax = new Minimax(board, player, this)
    minimax.minimax_decision
  }
}