package algorithms

import scala.collection.mutable.ArrayBuffer

/**
 * Queue implementation
 */
class Queue {
  var items:ArrayBuffer[Vertex] = new ArrayBuffer[Vertex]()
  
  // get the first item
  def pop(): Vertex = {    
    var tmp = this.items(0)
    this.items -= tmp
    return tmp
  }
  
  // push to the end
  def push(value:Vertex):Unit = {
    if (this.items.find(_ == value) == None) {
      this.items += value
      
    }
  }
  
  def isEmpty():Boolean = {
    if (this.items.length == 0)
      true      
    else
      false
  }
}