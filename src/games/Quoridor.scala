package games

import scala.collection.mutable.ArrayBuffer
import scala.math._
import scala.util.control._

class Quoridor extends Board {
  // Common board values
  val size:Int = 9
  val rows:Int = size
  val cols: Int = size
  var playerJustMoved:Int = -1
  
  // Quoridor specific values
  val startingWalls:Int = 10
  var pawns: Array[(Int, Int)] = Array((0,4), (8,4))
  val goals: Array[Int] = Array(8,0)
  var nbWalls: Array[Int] = Array(startingWalls, startingWalls)
  
  var horizWalls:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
  var vertiWalls:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
  
  // Common methods for every board
  def cloneBoard():Quoridor = {
    var cloner:Quoridor = new Quoridor()
    cloner.pawns(0) = this.pawns(0)
    cloner.pawns(1) = this.pawns(1)
    cloner.goals(0) = this.goals(0)
    cloner.goals(1) = this.goals(1)
    cloner.nbWalls(0) = this.nbWalls(0)
    cloner.nbWalls(1) = this.nbWalls(1)
    this.horizWalls.foreach(item => {
      cloner.horizWalls.append(item)
    })
    this.vertiWalls.foreach(item => {
      cloner.vertiWalls.append(item)
    })
    
    cloner
  }
  
  def canMoveHere(i:Int, j:Int, player:Int):Boolean = {
    this.isPawnMoveOk(this.pawns(player), (i,j), this.pawns((player + 1) % 2))
  }
  
  def isSimplifiedPawnMoveOk(formerPos:(Int, Int), newPos:(Int, Int)):Boolean = {
    /*
     * Returns True if moving one pawn from former_pos to new_pos
        is valid i.e. it respects the rules of quoridor (without the
        heap move above the opponent)
     */
    
    val (rowForm:Int, colForm:Int) = formerPos
    val (rowNew:Int, colNew:Int) = newPos
    
    if ((rowForm == rowNew && colForm == colNew) || (rowNew >= this.size || rowNew < 0) ||
    	(colNew >= this.size || colNew < 0))
      return false      
    
    val wallRight:Boolean = ( 
    				(if (this.vertiWalls.find(_ == (rowForm, colForm)) != None)  true else false) || 
    				(if (this.vertiWalls.find(_ == (rowForm - 1, colForm)) != None)  true else false) 
    			)
    val wallLeft:Boolean = (
    				(if (this.vertiWalls.find(_ == (rowForm - 1, colForm - 1)) != None)  true else false) || 
    				(if (this.vertiWalls.find(_ == (rowForm, colForm - 1)) != None)  true else false)
    			)
    			
    val wallUp:Boolean = (
    				(if (this.horizWalls.find(_ == (rowForm - 1, colForm - 1)) != None)  true else false) || 
    				(if (this.horizWalls.find(_ == (rowForm - 1, colForm)) != None)  true else false)
    			)
    			
    val wallDown:Boolean = (
    				(if (this.horizWalls.find(_ == (rowForm, colForm)) != None)  true else false) || 
    				(if (this.horizWalls.find(_ == (rowForm, colForm - 1)) != None)  true else false)
    			)    
    // check that the pawn doesn't move through a wall
    if (rowNew == rowForm + 1 && colNew == colForm)
      return !wallDown
    
    if (rowNew == rowForm -1 && colNew == colForm)
      return !wallUp
    
    if (rowNew == rowForm && colNew == colForm + 1)
      return !wallRight
      
    if (rowNew == rowForm && colNew == colForm - 1)
      return !wallLeft
      
    return false
  }
  
  def isPawnMoveOk(
      formerPos:(Int, Int), 
      newPos: (Int, Int), 
      opponentPos: (Int, Int)):Boolean = {
    // get values and extract to tuples
    val (xForm:Int, yForm:Int) = formerPos
    val (xNew:Int, yNew:Int) = newPos
    val (xOp:Int, yOp:Int) = opponentPos
    
    // check
    if ((xOp == xNew && yOp == yNew) || (xForm == xNew && yForm == yNew)) {
      return false
    }
    
    def manhattan(pos1:(Int, Int), pos2:(Int, Int)): Int = {
      abs(pos1._1 - pos2._1) + abs(pos1._2 - pos2._2)
    }
    
    if (manhattan(formerPos, opponentPos) + manhattan(opponentPos, newPos) == 2) {
      val ok:Boolean = this.isPawnMoveOk(opponentPos, newPos, (-10, -10)) &&
    		  			this.isPawnMoveOk(formerPos, opponentPos, (-10,-10))
      if (ok == false)
        return false
        
      if ( (pow(abs(xForm - xNew), 2) + pow(abs(yForm - yNew), 2)) == 2 )
        return !this.isPawnMoveOk(opponentPos, (xOp + (xOp - xForm), yOp + (yOp - yForm)), (-10, -10))
      
      return true
    }
    this.isSimplifiedPawnMoveOk(formerPos, newPos)
  }
  
  /**
   * Returns True if there exists a path from both players to
        at least one of their respective goals; False otherwise.
   */
  def pathExists():Boolean = {
    try {
      this.minStepsBeforeVictory(0)
      this.minStepsBeforeVictory(1)
      return true
    }
    catch {
      case _:Exception => false
    }
  }
  
  
  /**
   * Returns a shortest path for player to reach its goal
        if player if on its goal, the shortest path is an empty list
        if no path exists, exception is thrown.
   */
  def getShortestPath(player:Int):ArrayBuffer[(Int, Int)] = {
    def getPawnMoves(pos: (Int, Int)): ArrayBuffer[(Int, Int)] = {
    	val (x:Int, y:Int) = pos
    	val positions:Array[(Int, Int)] = Array((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
                (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
                (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2))
        val moves:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
        
        positions.foreach(newPos => {
          if (this.isPawnMoveOk(pos, newPos, this.pawns((player + 1) % 2))) 
            moves += newPos
          })
        moves
    }
    
    val (a:Int, b:Int) = this.pawns(player)
    if (a == this.goals(player))
      return ArrayBuffer[(Int, Int)]()
    
    // visited positions
    var visited:Array[Array[Boolean]] = Array.fill[Boolean](this.size,this.size) { false }
    // predecessor matrix in the BFS
    var prede:Array[Array[(Int, Int)]] = Array.fill[(Int, Int)](this.size,this.size) { (-1, -1) }
    var neighbors:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
    neighbors += this.pawns(player)
    
    while (neighbors.length > 0) {
      var neighbor:(Int, Int) = neighbors(0)
      neighbors.remove(0) // to act like pop in stack
      var (x:Int, y:Int) = neighbor
      visited(x)(y) = true
      if (x == this.goals(player)) {
        var succ:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
        succ += neighbor
        var curr:(Int, Int) = prede(x)(y)
        while (curr != (-1, -1) && curr != this.pawns(player)) {
          succ.append(curr)
          var (x1:Int, y1:Int) = curr
          curr = prede(x1)(y1)
        }
        succ.reverse
        return succ
      }
      
      var unvisited_succ:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
      getPawnMoves(neighbor).foreach(item => {
        if (!visited(item._1)(item._2)) unvisited_succ += item
        })
      
      unvisited_succ.foreach(n => {
        val (x1:Int, y1:Int) = n
        if (neighbors.find(_ == n) == None) {
          neighbors.append(n)
          prede(x1)(y1) = neighbor
        }
      })
    }
    // No Path here!
    throw new Exception("No Path")    
  }
  
  /**
   * Returns the minimum number of pawn moves necessary for the
        player to reach its goal raw.
   */
  def minStepsBeforeVictory(player: Int): Int = {
    this.getShortestPath(player).length
  }
  
  /**
   * Player adds a wall in position pos. The wall is horizontal
        if is_horiz and is vertical otherwise.
        if it is not possible to add such a wall because the rules of
        quoridor game don't accept it nothing is done.
   */
  def addWall(pos:(Int, Int), isHorizon:Boolean, player:Int):Unit = {
    if (this.nbWalls(player) <= 0 || !this.isWallPossibleHere(pos, isHorizon))
      return
    if (isHorizon)
      this.horizWalls.append(pos)
    else
      this.vertiWalls.append(pos)
    
    this.nbWalls(player) -= 1
  }
  
  /**
   * Modifies the state of the board to take into account the
        new position of the pawn of player.
   */
  def movePawn(newPos:(Int, Int), player:Int):Unit = {
    this.pawns(player) = newPos
  }
  
  /**
   * Returns True if it is possible to put a wall in position pos
        with direction specified by is_horiz.
   */
  def isWallPossibleHere(pos:(Int, Int), isHorizon:Boolean):Boolean = {
    val (x:Int, y:Int) = pos
    
    if (x >= this.size -1 || x < 0 || y >= this.size - 1 || y < 0)
      return false
    
      
    val tmp1:Boolean = if (this.horizWalls.find(_ == pos) != None) true else false
    val tmp2:Boolean = if (this.vertiWalls.find(_ == pos) != None) true else false
    if (!( tmp1 || tmp2 )) {
      val wallHorizRight:Boolean = if (this.horizWalls.find(_ == (x, y + 1)) != None) true else false
      val wallHorizLeft:Boolean = if (this.horizWalls.find(_ == (x, y - 1)) != None) true else false
      val wallVertUp:Boolean = if (this.vertiWalls.find(_ == (x - 1, y)) != None) true else false
      val wallVertDown:Boolean = if (this.vertiWalls.find(_ == (x + 1, y)) != None) true else false
      
      if (isHorizon) {
        if (wallHorizRight || wallHorizLeft)
          return false
        this.horizWalls.append(pos)
        
        if (!this.pathExists) {
          // act like pop
          this.horizWalls.remove(this.horizWalls.length - 1)
          return false
        }
        this.horizWalls.remove(this.horizWalls.length - 1)
        return true
      }
      else {
        if (wallVertUp || wallVertDown)
          return false
        this.vertiWalls.append(pos)
        if (!this.pathExists) {
          this.vertiWalls.remove(this.vertiWalls.length - 1)
          return false
        }
        this.vertiWalls.remove(this.vertiWalls.length - 1)
        return true
      }
    }
    else
      return false
  }
  
  /**
   * Returns legal moves for the pawn of player.
   */
  def getLegalPawnMoves(player:Int):ArrayBuffer[(String, Int, Int)] = {
    val (x:Int, y:Int) = this.pawns(player)
    val positions:Array[(Int, Int)] = Array((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
        (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
        (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2))
    val moves:ArrayBuffer[(String, Int, Int)] = ArrayBuffer[(String, Int, Int)]()
    positions.foreach(newPos => {
      if (this.isPawnMoveOk(this.pawns(player), newPos, this.pawns((player + 1) % 2)))
        moves.append(("P", newPos._1, newPos._2))
    })
    
    moves
  }
  
  def getLegalMovesFromPoint(pos:(Int, Int), player:Int):ArrayBuffer[(Int, Int)] = {
    val (x:Int, y:Int) = pos
    val positions:Array[(Int, Int)] = Array((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
        (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
        (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2))
    val moves:ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
    positions.foreach(newPos => {
      if (this.isPawnMoveOk(pos, newPos, this.pawns((player + 1) % 2)))
        moves.append((newPos._1, newPos._2))
    })
    
    moves
  }
  
  /**
   * Returns legal wall placements (adding a wall
        somewhere) for player.
   */
  def getLegalWallMoves(player:Int):ArrayBuffer[(String, Int, Int)] = {
    val positions: ArrayBuffer[(Int, Int)] = ArrayBuffer[(Int, Int)]()
    val moves: ArrayBuffer[(String, Int, Int)] = ArrayBuffer[(String, Int, Int)]()
    
    if (this.nbWalls(player) <= 0)
      return moves
    for (i <- 0 until (this.size - 1))
      for (j <- 0 until (this.size - 1))
        positions.append((i, j))
    positions.foreach(pos => {
      if (this.isWallPossibleHere(pos,true))
        moves.append(("WH", pos._1, pos._2))
      if (this.isWallPossibleHere(pos, false))
        moves.append(("WV", pos._1, pos._2))
    })
    
    moves
  }
  
  /**
   * Returns all the possible actions for player.
   */
  def getActions(player: Int):ArrayBuffer[(String, Int, Int)] = {
    val pawnMoves:ArrayBuffer[(String, Int, Int)] = this.getLegalPawnMoves(player)
    val wallMoves:ArrayBuffer[(String, Int, Int)] = this.getLegalWallMoves(player)
    
    (pawnMoves ++ wallMoves)
  }
  
  /**
   * Returns True if the action played by player
        is valid; False otherwise.
   */
  def isActionValid(action:(String, Int, Int), player:Int):Boolean = {
    val (kind:String, i:Int, j:Int) = action
    
    if (kind == "P")
      return this.isPawnMoveOk(this.pawns(player), (i,j), this.pawns((player + 1) % 2))
    else if (kind == "WH")
      return this.isWallPossibleHere((i, j), true)
    else if (kind == "WV")
      return this.isWallPossibleHere((i, j), false)
    else
      return false
  }
  
  /**
   * Play an action if it is valid.

        If the action is invalid, raise an InvalidAction exception.
        Return self.

        Arguments:
        action -- the action to be played
        player -- the player who is playing
   * 
   */
  def playAction(action:(String, Int, Int), player:Int):Quoridor = {
    if (!this.isActionValid(action, player))
      throw new Exception("Invalid Action")
    
    val (kind:String, x:Int, y:Int) = action
    if (kind == "WH")
      this.addWall((x,y), true, player)
    else if (kind == "WV")
      this.addWall((x,y), false, player)
    else if (kind == "P")
      this.movePawn((x,y), player)
    else
      throw new Exception("Invalid Action")
    
    this
  }
  
  /**
   * Return whether no more moves can be made (i.e.,
        game finished).
   */
  def isFinished():Boolean = {
    val player = new Players
    (this.pawns(player.PLAYER1)._1 == this.goals(player.PLAYER1) || 
        (this.pawns(player.PLAYER2)._1 == this.goals(player.PLAYER2)))
  }
  
  
  def isPlayerWin(player: Int):Boolean = {
    if (this.pawns(player)._1 == this.goals(player))
      true
    else
      false
  }
  
  /**
   * Return a score for this board for the given player.

        The score is the difference between the lengths of the shortest path
        of the player minus the one of its opponent. It also takes into
        account the remaining number of walls.
   */
  def getScore(player:Int):Int = {
    /*
    var score:Int = this.minStepsBeforeVictory((player + 1) % 2) - 
    				this.minStepsBeforeVictory(player)
    if (score == 0)
      score = this.nbWalls(player) - this.nbWalls((player + 1) % 2)
    score
    * 
    */
    var score:Int = 0;
    if (this.pathExists()) {
    	var opponent_steps = this.minStepsBeforeVictory((player + 1) % 2)
	    var player_steps = this.minStepsBeforeVictory(player)
	    
	    if (opponent_steps == 0)
	      return -65
	    else
	      return 65
	    
	    score = opponent_steps - player_steps + 1 * (this.nbWalls(player) - this.nbWalls((player + 1) % 2))
	    
	    return score
    }
    else {
    	val player_manhattan = Math.abs(this.pawns(player)._1 - this.goals(player))
	    val opponent_manhattan = Math.abs(this.pawns((player + 1) % 2)._1 - this.goals((player + 1) % 2))
	    val MDP = (9 - player_manhattan) / 9
	    val MDO = (9 - opponent_manhattan) / 9
	    val wall_left = this.nbWalls(player) - this.nbWalls((player + 1) % 2)
	    
	    score = (MDP - MDO) + wall_left
	    return score
    }
  }  
  
  /**
   * String representation of the board
   */
  override def toString = {
    var boardStr:String = ""
    for (i <- 0 until this.size) {
      for (j <- 0 until this.size) {
        if (this.pawns(0)._1 == i && this.pawns(0)._2 == j)
          boardStr += "P1"
        else if (this.pawns(1)._1 == i && this.pawns(1)._2 == j)
          boardStr += "P2"
        else
          boardStr += "00"
        
        if (this.vertiWalls.find(_ == (i,j)) != None)
          boardStr += "|"
        else if (this.vertiWalls.find(_ == (i - 1, j)) != None)
          boardStr += "|"
        else
          boardStr += " "
      }
      boardStr += "\n"
      
      for (j <- 0 until this.size) {
        if (this.horizWalls.find(_ == (i, j)) != None) {
          boardStr += "---"
        }
        else if (this.horizWalls.find(_ == (i, j - 1)) != None) {
          boardStr += "-- "
        }
        else if (this.vertiWalls.find(_ == (i, j)) != None) {
          boardStr += "  |"
        }
        else if (this.horizWalls.find(_ == (i, j - 1)) != None && this.vertiWalls.find(_ == (i,j)) != None)
          boardStr += "--|"
        else {
          boardStr += "   "
        }
      }
      
      boardStr += "\n"
    }
    boardStr
  }
}