package application

import games._
import games.players.OMCPlayer
import games.players.OMCPlayer

object Main extends App {
  // constructor right here
  println("welcome to mcts games")
  
  // create a game board
  var board = new Quoridor()
  // players
  val players = new games.Players  
  // game state - initially for player1 = 0
  var state:(Quoridor, Int) = (board, players.PLAYER1)
  
  // 2 agents
  var agent1:OMCPlayer = new OMCPlayer()
  var agent2:OMCPlayer = new OMCPlayer()
  
  // steps
  var steps:Int = 0
  
  val itermax:Int = 70
  
  // initial board
  println(board.toString)
  
  while (!board.isFinished) {    
    var move:(String, Int, Int) = ("", 0, 0)
    var playerJustMoved = -1
    // first move for player1 - this can be dynamically chosen in future!
    if (board.playerJustMoved == -1) {
      move = agent1.playQuoridor(players.PLAYER1, itermax, board, steps + 1, 0)
      board.playAction(move, players.PLAYER1)
      playerJustMoved = players.PLAYER1
    }
    else if (board.playerJustMoved == 1) {
      // player1 turn
      move = agent1.playQuoridor(players.PLAYER1, itermax, board, steps + 1, 0)
      board.playAction(move, players.PLAYER1)
      playerJustMoved = players.PLAYER1
    }
    else {
      // player2 turn
      move = agent1.playQuoridor(players.PLAYER2, itermax, board, steps + 1, 0)
      board.playAction(move, players.PLAYER2)
      playerJustMoved = players.PLAYER2
    }
    
    // update board
    steps += 1
    board.playerJustMoved = playerJustMoved    
    
    // print board
    println(board.toString)
  }
  
  // check result
  //println(board.toString) // final board state
  
  // announcement
  println("Player: " + board.playerJustMoved + " wins!")
}