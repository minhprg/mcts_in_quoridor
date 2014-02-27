from search import *
from quoridor import *

'''
This is referenced from Marco Guido
'''
def a_star(board, player, find_opponent=False):
    def h(n):
        return n.state.man_dist()

    clone = board.clone()
    problem = MiniProblem(clone, player, find_opponent)
    node=astar_graph_search(problem, h)
    solution = []
    if node != None :
        path=node.path()
        path.pop()
        path.reverse()
        for n in path:
            solution.append(n.state.pos)
        return solution
    else:
        return []


class MiniState:
    def __init__(self, pos):
        self.pos = pos

    def __eq__(self, other):
        #useful if we need to compare two State.
        if type(self) == type(other):
            return self.pos == other.pos
        else:
            return self is other
    def __hash__(self):
        # Le hash des positions des dollars ainsi que de la position du @ semble suffisant
        return hash(self.pos.__str__())
    def man_dist(self):
        # renvoie la mannathan distance entre 2 points.
        if MiniState.objective: #Si on recherche pas vraiment un goal mais une IA
            opponent = (MiniState.player + 1) % 2
            dist = math.fabs(self.pos[0] - MiniState.board.pawns[opponent][0]) + math.fabs(self.pos[1] - MiniState.board.pawns[opponent][1])
            return dist -1
        else: # on recherche bien un goal
            if (MiniState.player == 0):
                dist = 8 - self.pos[0]
            else:
                dist = self.pos[0]
            if dist > 1:
                return dist-1
            else:
                return dist

class MiniProblem(Problem):
    #for aStar
    def __init__(self, board, player, find_opponent=False):
        MiniState.board = board
        MiniState.player = player
        MiniState.objective = find_opponent
        self.initial = MiniState(board.pawns[player])

    def goal_test(self, state):
        return state.man_dist() == 0

    def successor(self, state):
        MiniState.board.pawns[MiniState.player] = state.pos
        moves = MiniState.board.get_legal_pawn_moves(MiniState.player)
        for move in moves:
            new_state = MiniState((move[1], move[2]))
            yield(move, new_state)



'''
Get successors strategy
'''
def get_moves(board, player, step, time_left):
    if (step <= 10):
        return get_advanced_moves(board, player)
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

# get moves based on astars for node expansion selection
def get_astar_moves(board, player):
    cloneboard = board.clone()
    moves = []

    # print("--- start A* ---")

    opponent = (player + 1) % 2
    mypath = a_star(cloneboard, player)
    #print(mypath)
    opponent_paths = a_star(cloneboard, opponent)

    # add my move
    if len(mypath) > 0:
        # add the first move
        moves.append(('P', mypath[0][0], mypath[0][1]))

    # add wall to opponent path
    if len(opponent_paths) > 0:
        #print("Opponent not []!")
        # if there are still walls
        if cloneboard.nb_walls[player] > 0:
            # list of wall moves
            positions = [
                            (0, 0), (1, 0), (-1, 0), (0, 1), (0, -1),
                            (1, 1), (-1, -1), (1, -1), (-1, 1)
                            #(x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)
                        ]
            # consider walls along the shortest path of the opponent
            visited = []

            # reduce branching factor by ONLY consider wall moves around the first step of shortest path
            #for path in opponent_paths:
            path = opponent_paths[0]
            for pos in positions:
                _x = path[0] + pos[0]
                _y = path[1] + pos[1]

                # consider wall moves
                if cloneboard.is_wall_possible_here((_x, _y), True) and (('WH', _x, _y) in visited) is False:
                    #h_board = cloneboard.clone().play_action(('WH', _x, _y), player)
                    # if the wall move gives better advantage
                    #if len(a_star(h_board, player)) <= len(a_star(h_board, opponent)):
                    moves.append(('WH', _x, _y))
                    visited.append(('WH', _x, _y)) # trace
                if cloneboard.is_wall_possible_here((_x, _y), False) and (('WV', _x, _y) in visited) is False:
                    #v_board = cloneboard.clone().play_action(('WV', _x, _y), player)
                    # if the wall move gives better advantage
                    #if len(a_star(v_board,player)) <= len(a_star(v_board, opponent)):
                    moves.append(('WV', _x, _y))
                    visited.append(('WV', _x, _y)) # trace

    # branching factor
    #print(moves)
    if len(moves) > 0:
        print("Branching factor: ", len(moves))
        return moves
    else:
        return []



# get moves based on astars for node simulation
def get_simulation_moves(board, player):
    cloneboard = board.clone()
    moves = []

    # print("--- start A* ---")

    mypaths = a_star(cloneboard, player)
    if len(mypaths) > 0:
        moves.append(('P', mypaths[0][0], mypaths[0][1]))
    elif cloneboard.nb_walls[player] == 0:
        moves = cloneboard.get_legal_pawn_moves(player)

    opponent = (player + 1) % 2
    opponent_paths = a_star(cloneboard, opponent)

    # add wall to opponent path
    if len(opponent_paths) > 0:
        #print("Opponent not []!")
        # if there are still walls
        if cloneboard.nb_walls[player] > 0:
            # list of wall moves
            positions = [
                            (0, 0), (1, 0), (-1, 0), (0, 1), (0, -1),
                            (1, 1), (-1, -1), (1, -1), (-1, 1)
                            #(x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)
                        ]
            # consider walls along the shortest path of the opponent
            visited = []
            for path in opponent_paths:
                for pos in positions:
                    _x = path[0] + pos[0]
                    _y = path[1] + pos[1]

                    # consider wall moves
                    if cloneboard.is_wall_possible_here((_x, _y), True) and (('WH', _x, _y) in visited) is False:
                        #h_board = cloneboard.clone().play_action(('WH', _x, _y), player)
                        # if the wall move gives better advantage
                        #if len(a_star(h_board, player)) <= len(a_star(h_board, opponent)):
                        moves.append(('WH', _x, _y))
                        visited.append(('WH', _x, _y)) # trace
                    if cloneboard.is_wall_possible_here((_x, _y), False) and (('WV', _x, _y) in visited) is False:
                        #v_board = cloneboard.clone().play_action(('WV', _x, _y), player)
                        # if the wall move gives better advantage
                        #if len(a_star(v_board,player)) <= len(a_star(v_board, opponent)):
                        moves.append(('WV', _x, _y))
                        visited.append(('WV', _x, _y)) # trace

    # branching factor
    #print(moves)
    if len(moves) > 0:
        print("Simulation Branching factor: ", len(moves))
        return moves
    else:
        return []


# get moves from advanced player
def get_advanced_moves(board, player):
    moves = []

    # a star first
    path = a_star(board, player)
    if len(path) > 0:
        moves.append(('P', path[0][0], path[0][1]))
    else:
        moves = board.get_legal_pawn_moves(player)


    # if still has walls
    if board.nb_walls[player] > 0:
        opponent_position = board.pawns[(player + 1) % 2]
        x = opponent_position[0];
        y = opponent_position[1];
        positions = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
            (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1)
            #(x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)
            ]

        for pos in positions:
            if board.is_wall_possible_here(pos, True):
                moves.append(('WH', pos[0], pos[1]))
            if board.is_wall_possible_here(pos, False):
                moves.append(('WV', pos[0], pos[1]))

    print("Simulation Branching factor: ", len(moves))

    return moves