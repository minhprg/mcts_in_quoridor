Monte-Carlo Tree Search in Quoridor
================

Quoridor is a 2- or 4-player abstract strategy game designed by Mirko Marchesi and published by Gigamic Games. Quoridor received the Mensa Mind Game award in 1997 and the Game Of The Year in the USA, France, Canada and Belgium. [1]

This project aims to provide a good Monte-Carlo Tree Search implementations in Quoridor game. It is also a part of my Master thesis in Université Catholique de Louvain under supervision of Professor [Yves Deville](http://www.info.ucl.ac.be/~yde/ "Professor Yves Deville from University of Louvain").

The original code comes from http://mcts.ai with the implementation of UCT (Upper Confidence Bound for Trees) algorithm.

# Descriptions
The project was initially implemented using Python 3.3. However, after learning Scala, I was impressed with the new language and decided to convert all the code of the project to Scala 2.10.

This project contains:
- An implementation of Quoridor board in src/games/Quoridor.scala
- An implementation of Monte-Carlo Tree Search framework at src/mcts/quoridor/MCTS_Quoridor.scala
- Implementations of some search algorithms that are used during the simulation strategy of this thesis. Such as BFS, Dijkstra are stored at src/algorithms
- A Main class as an application loader src/application/Main.scala. This class receives many parameters to run different games, agents and iterations
- Some smaller implementations to demonstrate the power of Monte Carlo Method such as estimating Pi value, estimate integrals, are store at src/demo

# References
[1] http://en.wikipedia.org/wiki/Quoridor
