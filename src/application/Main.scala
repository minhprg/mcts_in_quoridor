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
    if (args.length != 13) {
      println("Missing argument! Sample: $scala main [1] [2] [3] [4] [5] [6] [7] [8] [9] [10] [11] [12] [13]")
      println("[1]: type of game. like: quoridor, quoridorPar (parallelism)")
      println("[2]: type of agent 1 (mcts or minimax)")
      println("[3]: type of agent 2 (mcts or minimax)")
      println("[4]: number of iterations of player 1, like 100")
      println("[5]: number of iterations of player 2, like 100")
      println("[6]: strategy for player 1. Like: omc, pbbm, ucb1tuned, uct")
      println("[7]: strategy for player 2. Like: omc, pbbm, ucb1tuned, uct")
      println("[8]: simulation strategy for player 1. Like: a, e")
      println("[9]: simulation strategy for player 2. Like: a, e")
      println("[10]: final move selection of player 1")
      println("[11]: final move selection of player 2")
      println("[12]: time set per move in seconds. like: 120")
      println("[13]: file path prefix. sample: /Users/abx/output/ . remember the / at the end")
    }
    else {
      // program type
      val game:String =  args(0)
      // type of agent
      val agent1:String = args(1)
      val agent2:String = args(2)
      // iterations
      val iteration1:Int = Integer.parseInt(args(3))
      val iteration2:Int = Integer.parseInt(args(4))
      // strategy
      var selection1:String = args(5)
      var selection2:String = args(6)
      // simulation
      val simulation1:String = args(7)
      val simulation2:String = args(8)
      // final move
      val final1:String = args(9)
      val final2:String = args(10)
      // time per move
      val timePerMove:Int = Integer.parseInt(args(11))
      // output
      val prefix:String = args(12)
      
      
      def selectGame(name:String): Any = game match {
        case "quoridor" => QuoridorGame.run(agent1, agent2, selection1, selection2, iteration1, iteration2, simulation1, simulation2, final1, final2, timePerMove, prefix)
        case "quoridorPar" => QuoridorGamePar.run(agent1, agent2, selection1, selection2, iteration1, iteration2, simulation1, simulation2, final1, final2, timePerMove, prefix)
        case _ => QuoridorGame.run(agent1, agent2, selection1, selection2, iteration1, iteration2, simulation1, simulation2, final1, final2, timePerMove, prefix)
      }
      
      selectGame(game)
    }
  }
  
}