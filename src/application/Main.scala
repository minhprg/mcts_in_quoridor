package application

import games._

object Main extends App {
  // constructor right here
  println("welcome to mcts games")
  val board = new Quoridor
  board.playAction(("P", 1, 4), 0)
  println(board.toString)
}