package application

import games._
import games.players._

object Main extends App {
  override def main(args:Array[String]) = {
    // constructor right here
    println("Welcome to mcts games")
    QuoridorGame.run("uct", "omc")
    /*
    // receive arguments
    // 1. game {quoridor}
    // 2. agent1 {omc, uct, pbbm, ucb1}
    // 3. agent2 {...}    
    if (args.length != 3) {
      println("Missing argument! Sample: $scala main [1] [2] [3]")
      println("[1]: type of game. like: quoridor")
      println("[2]: first agent type. Like: omc, pbbm, ucb1tuned, uct")
      println("[3]: second agent type. Like: omc, pbbm, ucb1tuned, uct")
    }
    else {
      val game:String =  args(0)
      var agent1:String = args(1)
      var agent2:String = args(2)
      
      def selectGame(name:String): Any = game match {
        case "quoridor" => QuoridorGame.run(agent1, agent2)
        case _ => QuoridorGame.run(agent1, agent2)
      }
      
      selectGame(game)
    }
    * 
    */
  }
  
}