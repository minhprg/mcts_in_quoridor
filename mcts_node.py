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
        # moves
        astar = get_astar_moves(self.board, self.player)
        self.untriedMoves = astar

        # urgency value
        self.urgency = 0
        # fairness value
        self.fairness = 0

    '''
    Upper Confidence Bound for Trees by Kocsis - 2006
    '''
    def UCT(self):
        s = sorted(self.childNodes, key = lambda c: c.wins/c.visits + sqrt(2*log(self.visits)/c.visits))[-1]
        return s


    '''
    Objective Monte-Carlo by Chaslot - 2006a
    '''
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
            # check if visit > 0
            if child.visits > 0:
                p_i = 1 / child.visits
                # expectation
                expectation = p_i * (2 * child.wins - child.visits)

                # standard deviation
                sigma = sqrt( (pow(1 - expectation, 2) + pow(1 + expectation, 2)) * p_i )
                print("visits:", child.visits, " - wins: ", child.wins, " - E(X): ", expectation, " - sigma: ", sigma)
                # Urgency function update to child node
                child.urgency = erfc( (v_0 - child.value) / (sqrt(2) * sigma) )

                # update total urgency
                total_urgency += child.urgency

        print("total urgency: ", total_urgency)

        # choose the node based on fairness
        s = sorted(self.childNodes, key = lambda c : (n_p * c.urgency) / (c.visits * total_urgency))[-1]

        return s

    '''
    Probability to be Better than Best Move - by Coulom - 2006
    '''
    def PBBM(self):
        # best move value
        node0 = sorted(self.childNodes, key = lambda c: c.value)[-1]
        # v_0
        v_0 = node0.value
        # p_0
        p_0 = 1 / node0.visits
        # e(x_0)
        e_0 = p_0 * (2 * node0.wins - node0.visits)
        # standard deviation of best value
        sigma_0 = sqrt( (pow(1 - e_0, 2) + pow(1 + e_0, 2)) * p_0 )
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
            child.urgency = exp( -2.4 * (v_0 - child.value) / sqrt(2 * (pow(sigma_0,2) + pow(sigma,2))) )

            # update total urgency
            total_urgency += child.urgency

        # choose the node based on fairness
        s = sorted(self.childNodes, key = lambda c : (n_p * c.urgency) / (c.visits * total_urgency))[-1]

        return s

    '''
    UCB1 - Tunned by Gelly and Wang - 2006
    '''
    def UCB1TUNED(self):
        return 0


    # Add new child to node
    def AddChild(self, m, s, step, time_left):
        #print("Init node")
        n = MCTSNode(move = m, parent = self, state = s, step=step)
        #print("Add child!")
        self.untriedMoves.remove(m)
        self.childNodes.append(n)
        return n

    # Update node results
    def Update(self, result):
        if result > 0:
            self.wins += result
        self.visits += 1
        self.value += result