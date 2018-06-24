package demo

import scala.util.Random

// to test anything relate to scala
object JustAMess extends App {
  var i:Int = 0
  while (i < 1000) {
    try {
    	val logic:Boolean = Random.nextBoolean()
    	if (logic == false) {
    	  throw new Exception ("Raise false")
    	}
    	else {
    	  println(i)
    	  i += 1
    	}
    }
    catch {
      case _:Exception => i += 1
    }
    
  }
}