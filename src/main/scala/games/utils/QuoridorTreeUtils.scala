package games.utils

import games.nodes.QuoridorNode

class QuoridorTreeUtils(startNode:QuoridorNode) {
  val rootNode:QuoridorNode = startNode
  var totalNumberOfBranches:Double = this.rootNode.childNodes.length
  var totalNumberOfNodes:Double = 1
  var averageBranchingFactor:Double = 0
  
  def analyseAverageBranchingFactor():Unit = {
    this.rootNode.childNodes.foreach(node => {
      if (node.childNodes.length > 0) {
        this.analyseNode(node)
      }
    })
    
    this.averageBranchingFactor = this.totalNumberOfBranches / this.totalNumberOfNodes
  }
  
  def analyseNode(node:QuoridorNode):Unit = {
    this.totalNumberOfNodes += 1
    this.totalNumberOfBranches += node.childNodes.length    
    node.childNodes.foreach(child => {
      if (child.childNodes.length > 0) {
        this.analyseNode(child)
      }
    })
  }
}