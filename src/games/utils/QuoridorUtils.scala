package games.utils

import games.Quoridor
import scala.collection.mutable.ArrayBuffer
import algorithms._
import scala.math._

object QuoridorUtils {
	def get_moves(board:Quoridor, player:Int): ArrayBuffer[(String, Int, Int)] = {
	  //board.getActions(player)
	  //board.getLegalPawnMoves(player)
	  //getDijkstraMoves(board, player)
	  getBFSMoves(board, player)	  
	}
	
	def getBFSMoves(board:Quoridor, player:Int):ArrayBuffer[(String, Int, Int)] = {
	  var path = QuoridorUtils.doBFSMoves(board, player)
	  var way:ArrayBuffer[(Int, Int)] = new ArrayBuffer[(Int, Int)]()
	  while (path != null) {
	    way += ((path.id._1, path.id._2))
	    path = path.previous
	  }
	  way = way.reverse
	  
	  var moves: ArrayBuffer[(String, Int, Int)] = new ArrayBuffer[(String, Int, Int)]()
	  
	  // along the path
	  for (i <- 0 until way.length) {
	    val x:Int = way(i)._1
	    val y:Int = way(i)._2
	    // defines positions
	    val positions:Array[(Int, Int)] = Array(
	    		(x - 2, y - 1), (x - 2, y),
	    		(x - 1, y - 2), (x - 1, y - 1), (x - 1, y), (x - 1, y + 1),
	    		(x, y - 2), (x, y - 1), (x, y), (x, y + 1),
	    		(x + 1, y - 1), (x + 1, y)
	    )
	    // with each position, check if it exists in the list, if not then add it to list
	    if (board.nbWalls(player) > 0) {
	    	for (item <- positions) {	      
		      // if still inside the ground
		      if (item._1 >= 0 && item._1 < board.size - 1 && item._2 >= 0 && item._2 < board.size - 1) {
		    	  // horizon
			      if (!moves.exists(_ == (("WH", item._1, item._2)))) {
			        if (board.isWallPossibleHere((item._1, item._2), true))
			          if (board.cloneBoard.playAction(("WH", item._1, item._2), player).getShortestPath(player).length > 0 &&
			              board.cloneBoard.playAction(("WH", item._1, item._2), player).getShortestPath((player + 1) % 2).length > 0
			              )
			        	moves.append(("WH", item._1, item._2))
			      }
			      
			      // vertical
			      if (!moves.exists(_ == (("WV", item._1, item._2)))) {
			        if (board.isWallPossibleHere((item._1, item._2), false))
			          if (board.cloneBoard.playAction(("WV", item._1, item._2), player).getShortestPath(player).length > 0 &&
			              board.cloneBoard.playAction(("WV", item._1, item._2), player).getShortestPath((player + 1) % 2).length > 0
			              )
			        	moves.append(("WV", item._1, item._2))
			      }
		      }
		    }
	    }
	  }
	  
	  for (item <- board.getLegalPawnMoves(player))
	    if (way(1)._1 == item._2 && way(1)._2 == item._3)
	      moves.append(("P", item._2, item._3))
	  
	  moves
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
	
	/**
	 * Get possible moves (walls, pawn) based on the path created by getDijkstraPath() method
	 */
	/*
	def getDijkstraMoves(board:Quoridor, player:Int): ArrayBuffer[(String, Int, Int)] = {
	  val path = getDijkstraPath(board, player)
	  var moves: ArrayBuffer[(String, Int, Int)] = new ArrayBuffer[(String, Int, Int)]() 
	  // along the path
	  for (i <- 0 until path.length) {
	    val x:Int = path(i)._1
	    val y:Int = path(i)._2
	    // defines positions
	    val positions:Array[(Int, Int)] = Array(
	    		(x - 2, y - 1), (x - 2, y),
	    		(x - 1, y - 2), (x - 1, y - 1), (x - 1, y), (x - 1, y + 1),
	    		(x, y - 2), (x, y - 1), (x, y), (x, y + 1),
	    		(x + 1, y - 1), (x + 1, y)
	    )
	    // with each position, check if it exists in the list, if not then add it to list
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
	  
	  // add legal pawn move that belongs to the shortest path	  
	  println("Path = " + path)
	  
	  for (item <- board.getLegalPawnMoves(player))
	    if (path(1)._1 == item._2 && path(1)._2 == item._3)
	      moves.append(("P", item._2, item._3))
	  
	  // return
	  moves
	}
	* 
	*/
	
	/**
	 * Dijkstra path finding
	 */
	/*
	def getDijkstraPath(b:Quoridor, p:Int): ArrayBuffer[(Int, Int)] = {
	  var board:Quoridor = b
	  var player:Int = p
	  var path:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
	  // prepare the graph
	  var G = new Dijkstra()
	  
	  // create vertex for each square
	  for (i <- 0 until board.size) {
	    for (j <- 0 until board.size) {
	    	G.addVertex(new Vertex((i, j), 9999))
	    }
	  }
	  
	  // create edges for each square
	  for (i <- 0 until board.size) {
	    for (j <- 0 until board.size) {
	      // up
	      if (i - 1 >= 0)
	        G.addNeighbor(
	            G.graph.vertex.find(_.id == (i,j)).get, 
	            G.graph.vertex.find(_.id == (i - 1,j)).get, 1)
	      // down
	      if (i + 1 < board.size)
	        G.addNeighbor(
	            G.graph.vertex.find(_.id == (i,j)).get, 
	            G.graph.vertex.find(_.id == (i + 1,j)).get, 1)
	      // left
	      if (j - 1 >= 0)
	        G.addNeighbor(
	            G.graph.vertex.find(_.id == (i,j)).get, 
	            G.graph.vertex.find(_.id == (i,j - 1)).get, 1)
	      // right
	      if (j + 1 < board.size)
	        G.addNeighbor(
	            G.graph.vertex.find(_.id == (i,j)).get, 
	            G.graph.vertex.find(_.id == (i,j + 1)).get, 1)
	    }
	  }
	  
	  //println("Before: " + G.graph.edges.length)
	  
	  // remove edges that fences intersect
	  if (board.vertiWalls.length > 0) {
	    board.vertiWalls.foreach(item => {
	    	// 01
		    var edge:((Vertex, Vertex), Int) = Pair((
		    		G.graph.vertex.find(_.id == (item._1,item._2)).get,
		    		G.graph.vertex.find(_.id == (item._1,item._2 + 1)).get
		    ), 1)
		    G.removeNeighbor(edge)
		    edge = Pair((		    		
		    		G.graph.vertex.find(_.id == (item._1,item._2 + 1)).get,
		    		G.graph.vertex.find(_.id == (item._1,item._2)).get
		    ), 1)
		    G.removeNeighbor(edge)
		    
		    // 02
		    edge = Pair((
		    		G.graph.vertex.find(_.id == (item._1 + 1,item._2)).get,
		    		G.graph.vertex.find(_.id == (item._1 + 1,item._2 + 1)).get
		    ), 1)
		    G.removeNeighbor(edge)
		    
		    edge = Pair((
		    		G.graph.vertex.find(_.id == (item._1 + 1,item._2 + 1)).get,
		    		G.graph.vertex.find(_.id == (item._1 + 1,item._2)).get
		    ), 1)
		    G.removeNeighbor(edge)
	    })
	    
	  }
	  
	  if (board.horizWalls.length > 0) {
		  board.horizWalls.foreach(item => {
		    var edge:((Vertex, Vertex), Int) = Pair((
		    		G.graph.vertex.find(_.id == (item._1,item._2)).get,
		    		G.graph.vertex.find(_.id == (item._1 + 1,item._2)).get
		    ), 1)
		    G.removeNeighbor(edge)
		    
		    edge = Pair((
		    		G.graph.vertex.find(_.id == (item._1,item._2 + 1)).get,
		    		G.graph.vertex.find(_.id == (item._1 + 1,item._2 + 1)).get
		    ), 1)
		    G.removeNeighbor(edge)
		  })
	  }
	  
	  //println("After: " + G.graph.edges.length)
	  //println("List:")
	  
	  // remove the opponent pawn vertex, and their edges
	  val opp = board.pawns((player + 1) % 2)
	  G.removeVertex(G.graph.vertex.find(_.id == (opp._1, opp._2)).get)
	  
	  
	  // create edges for possible legal jump -- PENDING!
	  
	  // find paths - find all 8 paths then select the smallest one
	  val start = G.graph.vertex.find(_.id == Pair(board.pawns(player)._1, board.pawns(player)._2)).get
	  var iterate:Int = 0
	  var min:Int = 9999
	  var endDestIndex:Int = -1
	  
	  try {
	    while (iterate < board.size) {
	    	// temp path
	    	var path_tmp:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
	    	// set end
	    	var end = G.graph.vertex.find(_.id == Pair(board.goals(player),iterate)).get
	    	// find it
			G.findPath(start, end)
			// get the paths
			while (end.id != start.id) {
				path_tmp += end.id
				end = end.previous
			}
	    	path_tmp += start.id
	    	// check if this is the min path
	    	if (min > path_tmp.length) {
	    	  min = path_tmp.length
	    	  path = path_tmp
	    	}
	    	
	    	iterate += 1
	    }
	  } catch {
	    case _:Exception => iterate += 1 
	  }
	  
	  // test edges
	  //println("Edges = ")
	  //G.graph.edges.foreach(item => println(item._1._1.id + " - " + item._1._2.id))
	  
	  // return
	  path.reverse
	}
	* 
	*/
}