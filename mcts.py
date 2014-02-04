from math import *
import random
from quoridor import *
import copy
import threading
from time import *

class Node:
    """ A node in the game tree. Note wins is always from the viewpoint of playerJustMoved.
        Crashes if state not specified.
    """
    def __init__(self, move = None, parent = None, state = None):
        self.board, self.player = state
        self.move = move # the move that got us to this node - "None" for the root node
        self.parentNode = parent # "None" for the root node
        self.childNodes = []
        self.wins = 0
        self.visits = 0
        self.untriedMoves = get_moves(self.board, self.player) # future child nodes

    def UCTSelectChild(self):
        """ Use the UCB1 formula to select a child node. Often a constant UCTK is applied so we have
            lambda c: c.wins/c.visits + UCTK * sqrt(2*log(self.visits)/c.visits to vary the amount of
            exploration versus exploitation.
        """
        s = sorted(self.childNodes, key = lambda c: c.wins/c.visits + sqrt(2*log(self.visits)/c.visits))[-1]
        return s

    def AddChild(self, m, s):
        """ Remove m from untriedMoves and add a new child node for this move.
            Return the added child node
        """
        n = Node(move = m, parent = self, state = s)

        board,player = s
        #print("Add child with move:")
        #print(n.move)
        #print("Child player now:", player)
        #print("Child board:")
        #print(board)
        self.untriedMoves.remove(m)
        self.childNodes.append(n)
        return n

    def Update(self, result):
        """ Update this node - one additional visit and result additional wins. result must be from the viewpoint of playerJustmoved.
        """
        self.visits += 1
        self.wins += result

def UCT(rootstate, itermax, verbose = False, step, time_left):
    """ Conduct a UCT search for itermax iterations starting from rootstate.
        Return the best move from the rootstate.
        Assumes 2 alternating players (player 1 starts), with game results in the range [0.0, 1.0]."""

    rootnode = Node(state = rootstate)

    rootboard, rootplayer = rootstate

    counter = 0

    for i in range(itermax):
        node = rootnode
        state = copy.deepcopy(rootstate)
        board, player = state

        print("=======================")
        print(counter, " - Iteration")
        #print("Node Player:", node.player)
        #print("Node Board is:")
        #print(board)
        #print("Untried Moves")
        #print(node.untriedMoves)
        counter += 1

        # Select
        while node.untriedMoves == [] and node.childNodes != []: # node is fully expanded and non-terminal
            print("1. SELECT")
            node = node.UCTSelectChild()
            print("Selected node info:")
            print("Move:", node.move)
            #board.play_action(node.move, node.player)
            board = node.board
            player = node.player

        # Expand
        if node.untriedMoves is not []: # if we can expand (i.e. state/node is non-terminal)
            print("2. EXPAND")
            #print(node.untriedMoves)
            m = random.choice(node.untriedMoves)
            print("action chosen:")
            print(m)
            state = (node.board.clone().play_action(m, node.player), (node.player+1)%2)
            print("player is playing:", node.player)
            #print("new board:")
            #print(board)
            node = node.AddChild(m, state) # add child and descend tree

        #print("Rollout player is:", player)

        # Rollout - this can often be made orders of magnitude quicker using a state.GetRandomMove() function
        rollplayer = copy.deepcopy(player)
        rollboard = copy.deepcopy(board)
        rollmove = node.move
        keepalive = 0
        print("3. ROLLOUT...", rollplayer)
        while rollboard.is_finished() is False: # while state is non-terminal
            rollmove = random.choice(get_moves(rollboard, rollplayer, step, time_left))
            rollboard.play_action(rollmove, rollplayer)
            rollplayer = (rollplayer + 1) % 2
            # keepalive
            keepalive +=1
            if keepalive % 2 == 0:
                print("Still running...")

        # board state before backpropagate
        #print("Board state:")
        #print(rollboard)

        # Backpropagate
        while node != None: # backpropagate from the expanded node and work back to the root node
            print("4. BACKPROPAGATE for:", rootplayer)
            if rollboard.is_playerwin(rootplayer) is True:
                #print("Win!")
                node.Update(1) # state is terminal. Update node with result from POV of node.playerJustMoved
            if rollboard.is_playerwin((rootplayer+1)%2) is True:
                #print("Lose!")
                node.Update(0)
            else: # Draw
                #print("Draw")
                node.Update(0.5)
            node = node.parentNode

    # test
    print("Results:")
    for item in rootnode.childNodes:
        print("Node action:", item.move)
        print("Node score:", item.visits, item.wins)

    return sorted(rootnode.childNodes, key = lambda c: c.visits)[-1].move # return the move that was most visited

def search(state, step, time_left):
    # step pre-process
    if (step <= 10):
        itermax = 20
    else:
        itermax = 100

    board, player = state
    print("START UCT!")
    start = clock()
    move = UCT(rootstate=state, itermax=itermax, verbose=False, step, time_left)
    print("NEXT MOVE IS:")
    print(move)
    end = clock() - start
    print("THINKING TIME: ", end)
    return move



'''
Get successors strategy
'''
def get_moves(board, player, step, time_left):
    if (step <= 10):
        return get_wall_moves(board, player)
    else:
        return get_all_actions(board, player)
    #return get_simple_moves(board, player)
    #return get_advanced_moves(board, player)

# get simple legal pawn moves - for testing
def get_simple_moves(board, player):
    return board.get_legal_pawn_moves(player)

# get wall moves
def get_wall_moves(board, player):
    return board.get_legal_wall_moves(player)

# all actions
def get_all_actions(board, player):
    return board.get_actions(player)

# get moves from advanced player
def get_advanced_moves(board, player):
    moves = []
    for move in board.get_legal_pawn_moves(player):
        moves.append (move)

    opponent_position = board.pawns[(player + 1) % 2]
    wall_adjacent_consider_level = 1

    if wall_adjacent_consider_level == 1:
        for move in board.get_legal_wall_moves(player):
            import math
            if math.fabs(opponent_position[0] - move[1]) < 2 and math.fabs(opponent_position[1] - move[2]) < 2:
                if move[1] - opponent_position[0] < 1 and move[2] - opponent_position[1] < 1:
                    moves.append(move)
    else:
        for move in board.get_legal_wall_moves(player):
            import math
            if math.fabs(opponent_position[0] - move[1]) < 3 and math.fabs(opponent_position[1] - move[2]) < 3:
                if move[1] - opponent_position[0] < 2 and move[2] - opponent_position[1] < 2:

                    moves.append (move)
    return moves