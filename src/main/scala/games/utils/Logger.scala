package games.utils

import java.io._
import scala.collection.mutable.ArrayBuffer

class Logger(output:String) {
  var fringe:ArrayBuffer[String] = new ArrayBuffer[String]
  var output_file = output
  
  // log new event string
  def log(item:String) {
    fringe.append(item)
  }
  
  // save to file
  def save() {
    printToFile(new File(this.output_file))(p => {
      this.fringe.foreach(item => p.println(item))
    })
  }
  
  
  // file printing function
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
}