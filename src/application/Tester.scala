package application

import algorithms._
import games.utils._

object Tester extends App {
  // test file output
  var log = new Logger("/Users/qmha/test.txt")
  log.log("Hello World")
  log.log("I lost")
  log.save()
  
	val G = new Dijkstra
	// Add vertex
	G.addVertex(new Vertex((0, 1), 9999))
	G.addVertex(new Vertex((0, 2), 9999))
	G.addVertex(new Vertex((0, 3), 9999))
	G.addVertex(new Vertex((0, 4), 9999))
	// Add edges
	G.addNeighbor(
	    G.graph.vertex.find(_.id == Pair(0,1)).get
	    , G.graph.vertex.find(_.id == Pair(0,2)).get, 1)
	G.addNeighbor(
	    G.graph.vertex.find(_.id == Pair(0,1)).get
	    , G.graph.vertex.find(_.id == Pair(0,3)).get, 2)
	G.addNeighbor(
	    G.graph.vertex.find(_.id == Pair(0,2)).get
	    , G.graph.vertex.find(_.id == Pair(0,4)).get, 100)
	G.addNeighbor(
	    G.graph.vertex.find(_.id == Pair(0,3)).get
	    , G.graph.vertex.find(_.id == Pair(0,4)).get, 3)
	    
	G.findPath(G.graph.vertex.find(_.id == Pair(0,1)).get, G.graph.vertex.find(_.id == Pair(0,4)).get)
	
	// print path
	val start = G.graph.vertex.find(_.id == Pair(0,1)).get
	var dest = G.graph.vertex.find(_.id == Pair(0,4)).get
	
	while (dest.id != start.id) {
	  println(dest.id)
	  dest = dest.previous
	}
	println(start.id)
	
}