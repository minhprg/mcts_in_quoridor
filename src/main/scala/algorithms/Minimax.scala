package algorithms

import games.players.MinimaxAgent
import games.Quoridor

class Minimax(board:Quoridor, player:Int, game:MinimaxAgent, prune:Boolean = true) {
  var percepts:Quoridor = board
  var minimaxPlayer:Int = player
  
  def max_value(board:Quoridor, player:Int, alpha:Double, beta:Double, depth:Int):(Double, (String, Int, Int)) = {
    var current_alpha = alpha
    var current_beta = beta
    
    if (game.cutoff(board, player, depth)) {
      return (game.evaluate(board, player), null)
    }
    var value = Double.NegativeInfinity
    var action:(String, Int, Int) = ("P", -1, -1)
    game.successors(board, player).foreach(s => {
      val tmp:(Double, (String, Int, Int)) = this.min_value(s._2, s._3, current_alpha, current_beta, depth + 1)
      if (tmp._1 > value) {
        value = tmp._1
        action = s._1
        if (prune == true) {
          if (tmp._1 >= current_beta)
            return (tmp._1, s._1)
          current_alpha = Math.max(current_alpha, tmp._1)
        }
      }
    })
    
    // return
    return (value, action)
  }
  
  def min_value(board:Quoridor, player:Int, alpha:Double, beta:Double, depth:Int):(Double, (String, Int, Int)) = {
    var current_alpha = alpha
    var current_beta = beta
    
    if (game.cutoff(board, player, depth)) {
      return (game.evaluate(board, player), null)
    }
    var value = Double.PositiveInfinity
    var action:(String, Int, Int) = ("P", -1, -1)
    game.successors(board, player).foreach(s => {
      val tmp:(Double, (String, Int, Int)) = this.min_value(s._2, s._3, current_alpha, current_beta, depth + 1)
      if (tmp._1 < value) {
        value = tmp._1
        action = s._1
        if (prune == true) {
          if (tmp._1 <= current_alpha)
            return (tmp._1, s._1)
          current_beta = Math.min(current_beta, tmp._1)
        }
      }
    })
    
    // return
    return (value, action)
  }
  
  def minimax_decision():(String, Int, Int) = {
    val pinf = Double.PositiveInfinity
    val ninf = Double.NegativeInfinity
    var minimax_value:(Double, (String, Int, Int)) = (0, ("P", 0, 0))
    
    minimax_value = this.max_value(this.percepts, this.minimaxPlayer, ninf, pinf, 0)
    
    // return action
    minimax_value._2
  }
}