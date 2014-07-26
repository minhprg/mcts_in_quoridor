package games.utils

import games.Quoridor
import scala.collection.mutable.ArrayBuffer
import algorithms._
import scala.math._

object QuoridorUtils {
	/**
	 * Get children nodes for a new node
	 */
	def getchildNodes(board:Quoridor, player:Int): ArrayBuffer[(String, Int, Int)] = {
	  getSimulationMoves(board, player)
	}
	
	def get_moves(board:Quoridor, player:Int): ArrayBuffer[(String, Int, Int)] = {
	  getSimulationMoves(board, player)	     
	}
	
	def getSimulationMoves(board:Quoridor, player:Int):ArrayBuffer[(String, Int, Int)] = {
	  val opponent:Int = (player + 1) % 2
	  var path = QuoridorUtils.doBFSMoves(board, opponent) // get path of opponents
	  var mypath = QuoridorUtils.doBFSMoves(board, player) // my path
	  var way:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]()
	  var myway:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]()
	  
	  while (path != null) {
	    way += ((path.id._1, path.id._2))
	    path = path.previous
	  }
	  way = way.reverse
	  
	  while (mypath != null) {
	    myway += ((mypath.id._1, mypath.id._2))
	    mypath = mypath.previous
	  }
	  myway = myway.reverse
	  
	  var moves: ArrayBuffer[(String, Int, Int)] = new ArrayBuffer[(String, Int, Int)]()
	  
	 if (myway.length > 1)
	    moves += (("P", myway(1)._1, myway(1)._2))
	  else
	    board.getLegalPawnMoves(player).foreach(f => {moves += f})
	  
	  // consider walls
	  considerWallMoves(way, myway.length, board, player).foreach(f => { moves += f})
	  
	  moves
	}
	
	def considerWallMoves(oppway:ArrayBuffer[(Int, Int)], myway:Int, board:Quoridor, player:Int):ArrayBuffer[(String, Int, Int)] = {
	  var moves: ArrayBuffer[(String, Int, Int)] = new ArrayBuffer[(String, Int, Int)]()	  
	  var limit:Int = oppway.length
	  if (oppway.length > 4)
	    limit = 4
	    
	  // along the path of opponent	  
	  for (i <- 0 until limit) {
	    val x:Int = oppway(i)._1
	    val y:Int = oppway(i)._2
	    // defines positions
	    val positions:Array[(Int, Int)] = Array(
	    		//(x - 2, y - 2), 
	    		//(x - 2, y - 1), 
	    		//(x - 2, y), 
	    		//(x - 2, y + 1),  
	    		
	    		(x - 1, y - 2), 
	    		(x - 1, y - 1), 
	    		(x - 1, y), 
	    		(x - 1, y + 1),
	    		
	    		(x, y - 2), 
	    		(x, y - 1), 
	    		(x, y), 
	    		(x, y + 1),
	    		
	    		(x + 1, y - 2), 
	    		(x + 1, y - 1), 
	    		(x + 1, y), 
	    		(x + 1, y + 1)
	    		
	    		//(x + 2, y - 2), 
	    		//(x + 2, y - 1), 
	    		//(x + 2, y), 
	    		//(x + 2, y + 1)
	    )
	    // if player still has walls left
	    if (board.nbWalls(player) > 0) {
	    	for (item <- positions) {	  	    	  
		      // if still inside the ground
		      if (item._1 >= 0 && item._1 < board.size - 1 && item._2 >= 0 && item._2 < board.size - 1) {
		    	  // horizon
			      if (!moves.exists(_ == (("WH", item._1, item._2)))) {
			        if (board.isWallPossibleHere((item._1, item._2), true))
			            moves.append(("WH", item._1, item._2))
			      }
			      
			      // vertical
			      if (!moves.exists(_ == (("WV", item._1, item._2)))) {
			        if (board.isWallPossibleHere((item._1, item._2), false))
			        	  moves.append(("WV", item._1, item._2))
			      }
		      }
		    }
	    }
	  }
	  
	  moves
	}
	
	def isMoveBetter(opppath:Int, mypath:Int, newBoard:Quoridor):Boolean = {
	  false
	}
	
	def doBFSMoves(board:Quoridor, player:Int):Vertex = {
	  var start:Vertex = new Vertex(board.pawns(player), 1)
	  var visited:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]()	  
	  var queue = new Queue
	  queue.push(start)
	  
	  
	  while (!queue.isEmpty) {
	    var node:Vertex = queue.pop	    
	    // check for termination
	    if (node.id._1 == board.goals(player))
	      return node
	    
	    // add to visited coordinates	    
	    visited += Pair(node.id._1, node.id._2)
	    
	    // get all surrounding moves
	    var moves:ArrayBuffer[(Int, Int)] = board.getLegalMovesFromPoint(node.id, player)	    
	    moves.foreach(item => {
	      // if not visited
	      if (visited.find(_ == item) == None) {
	        // add to queue
	        var newVertex = new Vertex(item, 1)
	        newVertex.previous = node 
	        queue.push(newVertex)
	      }
	    })
	  }
	  null
	}
}