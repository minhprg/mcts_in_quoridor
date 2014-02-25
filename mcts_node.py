from helpers import *
from math import *

class MCTSNode:
    def __init__(self, move = None, parent = None, state = None, step = 0, time_left = 0):
        self.board, self.player = state
        self.move = move # the move that got us to this node - "None" for the root node
        self.step = step # step played
        self.time_left = time_left # time left
        self.parentNode = parent # "None" for the root node
        self.childNodes = []
        self.wins = 0
        self.visits = 0
        self.value = 0
        self.untriedMoves = get_astar_moves(self.board, self.player) # future child nodes
        # urgency value
        self.urgency = 0
        # fairness value
        self.fairness = 0

    def UCT(self):
        s = sorted(self.childNodes, key = lambda c: c.wins/c.visits + sqrt(2*log(self.visits)/c.visits))[-1]
        return s

    def OMC(self):
        # best move value
        node0 = sorted(self.childNodes, key = lambda c: c.value)[-1]
        v_0 = node0.value
        # n_p
        n_p = self.visits

        # total urgenciness of child nodes
        total_urgency = 0

        # calculate the urgency
        for child in self.childNodes:
            p_i = 1 / child.visits
            # expectation
            expectation = p_i * (2 * child.wins - child.visits)

            # standard deviation
            sigma = sqrt( (pow(1 - expectation, 2) + pow(1 + expectation, 2)) * p_i )
            #print("visits:", child.visits, " - wins: ", child.wins, " - E(X): ", expectation, " - sigma: ", sigma)
            # Urgency function update to child node
            child.urgency = erfc( (v_0 - child.value) / (sqrt(2) * sigma) )

            # update total urgency
            total_urgency += child.urgency

        # choose the node based on fairness
        s = sorted(self.childNodes, key = lambda c : (n_p * c.urgency) / (c.visits / total_urgency))[-1]

        return s

    def PMBB(self):
        return 0

    def UCB1TUNED(self):
        return 0

    def AddChild(self, m, s, step, time_left):
        n = MCTSNode(move = m, parent = self, state = s, step=step)

        self.untriedMoves.remove(m)
        self.childNodes.append(n)
        return n

    def Update(self, result):
        if result > 0:
            self.wins += result
        self.visits += 1
        self.value += result