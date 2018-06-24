package demo

import scala.util.Random
import scala.math._

object CalculatePi extends App {
	println("Calculate Pi")
	
	val max:Array[Int] = Array(3000, 30000, 300000)
	var pi:Double = 0
	
	def getPoint():Int = {
	  if (sqrt(pow(Random.nextDouble, 2) + pow(Random.nextDouble, 2)) <= 1)
	    return 1 // inside
	  else
	    return 0 // outside
	}	
		
	for (i <- 0 until max.length) {
		var sum:Int = 0
		for (j <- 0 until max(i))
			sum += getPoint()
			
		pi = 4 * sum.toDouble / max(i).toDouble
		println("Simulations = " + max(i) + ". Pi = " + pi + ". Variations:" + (pi / 3.14159265359))		
	}	
}