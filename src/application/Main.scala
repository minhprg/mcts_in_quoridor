package application

import games._
import games.players._
import java.lang.Integer

object Main extends App {
  override def main(args:Array[String]) = {
    // constructor right here
    println("Welcome to mcts games")
        
    // receive arguments
    // 1. game {quoridor}
    // 2. agent1 {omc, uct, pbbm, ucb1}
    // 3. agent2 {...}    
    // 4. prefix of file path
    if (args.length != 5) {
      println("Missing argument! Sample: $scala main [1] [2] [3] [4] [5]")
      println("[1]: type of game. like: quoridor")
      println("[2]: number of iterations, like 100")
      println("[3]: first agent type. Like: omc, pbbm, ucb1tuned, uct")
      println("[4]: second agent type. Like: omc, pbbm, ucb1tuned, uct")
      println("[5]: file path prefix. sample: /Users/abx/output/ . remember the / at the end")
    }
    else {
      val game:String =  args(0)
      val iterations:Int = Integer.parseInt(args(1))
      var agent1:String = args(2)
      var agent2:String = args(3)
      val prefix:String = args(4)
      
      
      def selectGame(name:String): Any = game match {
        case "quoridor" => QuoridorGame.run(agent1, agent2, prefix, iterations)
        case _ => QuoridorGame.run(agent1, agent2, prefix, iterations)
      }
      
      selectGame(game)
    }
  }
  
}