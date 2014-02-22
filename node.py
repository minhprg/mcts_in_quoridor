from utils import *

class Node:
    def __init__(self, move = None, parent = None, state = None, step = 0, time_left = 0):
        self.board, self.player = state
        self.move = move # the move that got us to this node - "None" for the root node
        self.step = step # step played
        self.time_left = time_left # time left
        self.parentNode = parent # "None" for the root node
        self.childNodes = []
        self.wins = 0
        self.visits = 0
        self.untriedMoves = get_advanced_moves(self.board, self.player) # future child nodes

    def UCT(self):
        s = sorted(self.childNodes, key = lambda c: c.wins/c.visits + sqrt(2*log(self.visits)/c.visits))[-1]
        return s

    def OMC(self):
        return 0

    def PMBB(self):
        return 0

    def UCB1TUNED(self):
        return 0

    def AddChild(self, m, s, step, time_left):
        n = Node(move = m, parent = self, state = s, step=step)

        self.untriedMoves.remove(m)
        self.childNodes.append(n)
        return n

    def Update(self, result):
        self.visits += 1
        self.wins += result