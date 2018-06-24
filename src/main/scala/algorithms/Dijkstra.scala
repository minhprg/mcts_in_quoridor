package algorithms

import scala.collection.mutable._
import scala.util.control.Breaks._

/**
 * Single-source shortest path algorithms
 */
class Dijkstra {
  // initialize an empty graph 
  var graph = new Graph
  
  // add vertex to graph
  def addVertex(v: Vertex):Graph = {
    this.graph.vertex += v
    this.graph
  }
  
  // add neighbor to graph
  def addNeighbor(n1:Vertex, n2:Vertex, value:Int):Unit = {
    this.graph.edges += Pair((n1, n2), value)
  }
  
  // remove vertex from graph
  def removeVertex(v: Vertex):Graph = {   
    // remove the vertex
    this.graph.vertex -= v 
    var newEdges:ArrayBuffer[((Vertex, Vertex), Int)] = ArrayBuffer[((Vertex, Vertex), Int)]()
    
    this.graph.edges.foreach(item => {
      if (v.id != item._1._1.id && v.id != item._1._2.id) {
        newEdges += item
      }
    })
    
    this.graph.edges = newEdges
    
    this.graph
  }
  
  // remove neighbor from graph
  def removeNeighbor(e:((Vertex, Vertex), Int)):Graph = {
    this.graph.edges -= e
    this.graph
  }
  
  def findPath(s: Vertex, e:Vertex):ArrayBuffer[Vertex] = {
    // set all vertex value to infinity, except the source Vertex to 0
    this.graph.vertex.foreach(item => { if (item.id != s.id) item.value = 9999 else item.value = 0})    
    // set of vertex on the path
    //var S:ArrayBuffer[Vertex] = ArrayBuffer[Vertex]()
    var Q = new PriorityQueue[Vertex]()(Ordering.by(_.value * (-1))) // min priority queue
    this.graph.vertex.foreach(item => {Q += item})
    
    while (Q.nonEmpty) {
      val u = Q.dequeue
      
      if (u.value == 9999) {
        throw new Exception("No Dijkstra path")
      }
        
      // for each neighbor v of u
      this.graph.edges.foreach(item => {
        // if a neighbor found
        if (item._1._1.id == u.id) { // left side is U
          val v:Vertex = item._1._2
          val alt:Int = u.value + item._2
          if (alt <= item._1._2.value) { // shorter path found
            // update graph vertex value and previous
            this.graph.vertex.find(_.id == v.id).get.value = alt
            this.graph.vertex.find(_.id == v.id).get.previous = u
            // update the Q
            var tmp_queue = new PriorityQueue[Vertex]()(Ordering.by(_.value * (-1)))
            Q.foreach(node => { 
              // update the value of the Vertex in old queue
              if (node.id == v.id) { node.value = alt; node.previous = u}
              tmp_queue += node
            })
            Q.dequeueAll
            Q = tmp_queue
          }
        }
      })
    }
    
    this.graph.vertex
  }
}


// a class Vertex for Vertex presentation, this Vertex id is the pair of XY matrix 
class Vertex(pos:(Int, Int), weight:Int) {
  val id = pos
  var value = weight
  var previous:Vertex = null  
}
  
class Graph {
  var vertex:ArrayBuffer[Vertex] = ArrayBuffer[Vertex]()
  var edges:ArrayBuffer[((Vertex, Vertex), Int)] = ArrayBuffer[((Vertex, Vertex), Int)]()
}