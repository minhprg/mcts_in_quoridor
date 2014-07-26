package application

import games._
import games.players._
import testbed.parallelism._ // For testing purpose
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
    if (args.length != 11) {
      println("Missing argument! Sample: $scala main [1] [2] [4] [5] [6] [7] [8] [9] [10] [11]")
      println("[1]: type of game. like: quoridor, quoridorPar (parallelism)")
      println("[2]: number of iterations of player 1, like 100")
      println("[3]: number of iterations of player 2, like 100")
      println("[4]: strategy for player 1. Like: omc, pbbm, ucb1tuned, uct")
      println("[5]: strategy for player 2. Like: omc, pbbm, ucb1tuned, uct")
      println("[6]: simulation strategy for player 1. Like: a, e")
      println("[7]: simulation strategy for player 2. Like: a, e")
      println("[8]: final move selection of player 1")
      println("[9]: final move selection of player 2")
      println("[10]: time set per move in seconds. like: 120")
      println("[11]: file path prefix. sample: /Users/abx/output/ . remember the / at the end")
    }
    else {
      // program type
      val game:String =  args(0)
      // iterations
      val iteration1:Int = Integer.parseInt(args(1))
      val iteration2:Int = Integer.parseInt(args(2))
      // strategy
      var agent1:String = args(3)
      var agent2:String = args(4)
      // simulation
      val simulation1:String = args(5)
      val simulation2:String = args(6)
      // final move
      val final1:String = args(7)
      val final2:String = args(8)
      // time per move
      val timePerMove:Int = Integer.parseInt(args(9))
      // output
      val prefix:String = args(10)
      
      
      def selectGame(name:String): Any = game match {
        case "quoridor" => QuoridorGame.run(agent1, agent2, iteration1, iteration2, simulation1, simulation2, final1, final2, timePerMove, prefix)
        case "quoridorPar" => QuoridorGamePar.run(agent1, agent2, iteration1, iteration2, simulation1, simulation2, final1, final2, timePerMove, prefix)
        case _ => QuoridorGame.run(agent1, agent2, iteration1, iteration2, simulation1, simulation2, final1, final2, timePerMove, prefix)
      }
      
      selectGame(game)
    }
  }
  
}