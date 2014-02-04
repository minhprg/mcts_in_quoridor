"""
Common definitions for the Arlecchino players.
Author: Cyrille Dejemeppe <cyrille.dejemeppe@uclouvain.be>
Copyright (C) 2013, Université catholique de Louvain

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, see <http://www.gnu.org/licenses/>.

"""

import random
import itertools
import operator

PLAYER1 = 0
PLAYER2 = 1


class InvalidAction(Exception):

    """Raised when an invalid action is played."""

    def __init__(self, action=None, player=-1):
        self.action = action
        self.player = player


class NoPath(Exception):
    """Raised when a player puts a wall such that no path exists
    between a player and its goal row"""
    def __repr__(self):
        return "Exception: no path to reach the goal"


class Board:

    """
    Representation of a Quoridor Board.

    """

    def __init__(self, percepts=None):
        """
        Constructor of the representation for a quoridor game of size 9.
        The representation can be initialized by a percepts
        If percepts==None:
            player 0 is position (4,0) and its goal is to reach the raw 8
            player 1 is position (4,8) and its goal is to reach the raw 0
            each player owns 10 walls and there is initially no wall on the
            board
        """
        self.size = 9
        self.rows = self.size
        self.cols = self.size
        self.starting_walls = 10
        self.pawns = [(0, 4), (8, 4)]
        self.goals = [8, 0]
        self.nb_walls = [self.starting_walls, self.starting_walls]
        self.horiz_walls = []
        self.verti_walls = []
        if percepts is not None:
            self.pawns[0] = percepts.pawns[0]
            self.goals[0] = percepts.goals[0]
            self.pawns[1] = percepts.pawns[1]
            self.goals[1] = percepts.goals[1]
            for (x, y) in percepts.horiz_walls:
                self.horiz_walls.append((x, y))
            for (x, y) in percepts.verti_walls:
                self.verti_walls.append((x, y))
            self.nb_walls[0] = percepts.nb_walls[0]
            self.nb_walls[1] = percepts.nb_walls[1]

    def pretty_print(self):
        """print of the representation"""
        print ("Player 0 => pawn:", self.pawns[0], "goal:",
            self.goals[0], "nb walls:", self.nb_walls[0])
        print ("Player 1 => pawn:", self.pawns[1], "goal:",
            self.goals[1], "nb walls:", self.nb_walls[1])
        print ("Horizontal walls:", self.horiz_walls)
        print ("Vertical walls:", self.verti_walls)

    def __str__(self):
        """String representation of the board"""
        board_str = ""
        for i in range(self.size):
            for j in range(self.size):
                if self.pawns[0][0] == i and self.pawns[0][1] == j:
                    board_str += "P1"
                elif self.pawns[1][0] == i and self.pawns[1][1] == j:
                    board_str += "P2"
                else:
                    board_str += "OO"
                if (i, j) in self.verti_walls:
                    board_str += "|"
                elif (i - 1, j) in self.verti_walls:
                    board_str += "|"
                else:
                    board_str += " "
            board_str += "\n"
            for j in range(self.size):
                if (i, j) in self.horiz_walls:
                    board_str += "---"
                elif (i, j - 1) in self.horiz_walls:
                    board_str += "-- "
                elif (i, j) in self.verti_walls:
                    board_str += "  |"
                elif (i, j - 1) in self.horiz_walls and \
                    (i, j) in self.verti_walls:
                    board_str += "--|"
                else:
                    board_str += "   "
            board_str += "\n"
        return board_str

    def clone(self):
        """Return a clone of this object."""
        clone_board = Board()
        clone_board.pawns[0] = self.pawns[0]
        clone_board.pawns[1] = self.pawns[1]
        clone_board.goals[0] = self.goals[0]
        clone_board.goals[1] = self.goals[1]
        clone_board.nb_walls[0] = self.nb_walls[0]
        clone_board.nb_walls[1] = self.nb_walls[1]
        for (x, y) in self.horiz_walls:
            clone_board.horiz_walls.append((x, y))
        for (x, y) in self.verti_walls:
            clone_board.verti_walls.append((x, y))
        return clone_board

    def can_move_here(self, i, j, player):
        """Returns true if the player can move to (i, j),
        false otherwise
        """
        return self.is_pawn_move_ok(self.pawns[player], (i, j),
            self.pawns[(player + 1) % 2])

    def is_simplified_pawn_move_ok(self, former_pos, new_pos):
        """Returns True if moving one pawn from former_pos to new_pos
        is valid i.e. it respects the rules of quoridor (without the
        heap move above the opponent)
        """
        (row_form, col_form) = former_pos
        (row_new, col_new) = new_pos

        if (row_form == row_new and col_form == col_new) or \
            row_new >= self.size or row_new < 0 or \
            col_new >= self.size or col_new < 0:
            return False
        wall_right = ((row_form, col_form) in self.verti_walls) or \
                    ((row_form - 1, col_form) in self.verti_walls)
        wall_left = ((row_form - 1, col_form - 1) in self.verti_walls) or \
                    ((row_form, col_form - 1) in self.verti_walls)
        wall_up = ((row_form - 1, col_form - 1) in self.horiz_walls) or \
                    ((row_form - 1, col_form) in self.horiz_walls)
        wall_down = ((row_form, col_form) in self.horiz_walls) or \
                    ((row_form, col_form - 1) in self.horiz_walls)

        # check that the pawn doesn't move through a wall
        if row_new == row_form + 1 and col_new == col_form:
            return not wall_down
        if row_new == row_form - 1 and col_new == col_form:
            return not wall_up
        if row_new == row_form and col_new == col_form + 1:
            return not wall_right
        if row_new == row_form and col_new == col_form - 1:
            return not wall_left
        return False

    def is_pawn_move_ok(self, former_pos, new_pos, opponent_pos):
        """Returns True if moving one pawn from former_pos to new_pos is
        valid i.e. it respects the rules of quoridor
        """
        (x_form, y_form) = former_pos
        (x_new, y_new) = new_pos
        (x_op, y_op) = opponent_pos

        if (x_op == x_new and y_op == y_new) or \
            (x_form == x_new and y_form == y_new):
            return False

        # Move of 2 (above the opponent pawn) or diagonal
        def manhattan(pos1, pos2):
            return abs(pos1[0] - pos2[0]) + abs(pos1[1] - pos2[1])

        if manhattan(former_pos, opponent_pos) + \
            manhattan(opponent_pos, new_pos) == 2:
            ok = self.is_pawn_move_ok(opponent_pos, new_pos, (-10, -10)) and \
                self.is_pawn_move_ok(former_pos, opponent_pos, (-10, -10))
            if not ok:
                return False
            # Check if the move is in straight angle that there were no
            # possibility of moving straight ahead
            if abs(x_form - x_new) ** 2 + abs(y_form - y_new) ** 2 == 2:
                # There is a possibility of moving straight ahead leading the
                # move to be illegal
                return not self.is_pawn_move_ok(opponent_pos,
                    (x_op + (x_op - x_form), y_op + (y_op - y_form)),
                    (-10, -10))
            return True
        return self.is_simplified_pawn_move_ok(former_pos, new_pos)

    def paths_exist(self):
        """Returns True if there exists a path from both players to
        at least one of their respective goals; False otherwise.
        """
        try:
            self.min_steps_before_victory(0)
            self.min_steps_before_victory(1)
            return True
        except NoPath:
            return False

    def get_shortest_path(self, player):
        """ Returns a shortest path for player to reach its goal
        if player if on its goal, the shortest path is an empty list
        if no path exists, exception is thrown.
        """

        def get_pawn_moves(pos):
            (x, y) = pos
            positions = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
                (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
                (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)]
            moves = []
            for new_pos in positions:
                if self.is_pawn_move_ok(pos, new_pos,
                    self.pawns[(player + 1) % 2]):
                    moves.append(new_pos)
            return moves

        (a, b) = self.pawns[player]
        if a == self.goals[player]:
            return []
        visited = [[False for i in range(self.size)] for i in range(self.size)]
        # Predecessor matrix in the BFS
        prede = [[None for i in range(self.size)] for i in range(self.size)]
        neighbors = [self.pawns[player]]
        while len(neighbors) > 0:
            neighbor = neighbors.pop(0)
            (x, y) = neighbor
            visited[x][y] = True
            if x == self.goals[player]:
                succ = [neighbor]
                curr = prede[x][y]
                while curr is not None and curr != self.pawns[player]:
                    succ.append(curr)
                    (x_, y_) = curr
                    curr = prede[x_][y_]
                succ.reverse()
                return succ
            unvisited_succ = [(x_, y_) for (x_, y_) in
                get_pawn_moves(neighbor) if not visited[x_][y_]]
            for n_ in unvisited_succ:
                (x_, y_) = n_
                if not n_ in neighbors:
                    neighbors.append(n_)
                    prede[x_][y_] = neighbor
        raise NoPath()

    def get_shortest_path_simplified(self, player):
        """ Returns a shortest path for player to reach its goal
        if player if on its goal, the shortest path is an empty list
        if no path exists, exception is thrown.
        NOTE: this shortest path does not count the jump over opponent
        """

        def get_pawn_moves(pos):
            (x, y) = pos
            positions = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
                (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
                (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)]
            moves = []
            for new_pos in positions:
                if self.is_simplified_pawn_move_ok(pos, new_pos):
                    moves.append(new_pos)
            return moves

        (a, b) = self.pawns[player]
        if a == self.goals[player]:
            return []
        visited = [[False for i in range(self.size)] for i in range(self.size)]
        # Predecessor matrix in the BFS
        prede = [[None for i in range(self.size)] for i in range(self.size)]
        neighbors = [self.pawns[player]]
        while len(neighbors) > 0:
            neighbor = neighbors.pop(0)
            (x, y) = neighbor
            visited[x][y] = True
            if x == self.goals[player]:
                succ = [neighbor]
                curr = prede[x][y]
                while curr is not None and curr != self.pawns[player]:
                    succ.append(curr)
                    (x_, y_) = curr
                    curr = prede[x_][y_]
                succ.reverse()
                return succ
            unvisited_succ = [(x_, y_) for (x_, y_) in
                get_pawn_moves(neighbor) if not visited[x_][y_]]
            for n_ in unvisited_succ:
                (x_, y_) = n_
                if not n_ in neighbors:
                    neighbors.append(n_)
                    prede[x_][y_] = neighbor
        raise NoPath()

    def min_steps_before_victory(self, player):
        """Returns the minimum number of pawn moves necessary for the
        player to reach its goal raw.
        """
        return len(self.get_shortest_path(player))

    def add_wall(self, pos, is_horiz, player):
        """Player adds a wall in position pos. The wall is horizontal
        if is_horiz and is vertical otherwise.
        if it is not possible to add such a wall because the rules of
        quoridor game don't accept it nothing is done.
        """
        if self.nb_walls[player] <= 0 or \
            not self.is_wall_possible_here(pos, is_horiz):
            return
        if is_horiz:
            self.horiz_walls.append(pos)
        else:
            self.verti_walls.append(pos)
        self.nb_walls[player] -= 1

    def move_pawn(self, new_pos, player):
        """Modifies the state of the board to take into account the
        new position of the pawn of player.
        """
        self.pawns[player] = new_pos

    def is_wall_possible_here(self, pos, is_horiz):
        """
        Returns True if it is possible to put a wall in position pos
        with direction specified by is_horiz.
        """
        (x, y) = pos
        if x >= self.size - 1 or x < 0 or y >= self.size - 1 or y < 0:
            return False
        if not (tuple(pos) in self.horiz_walls or
                tuple(pos) in self.verti_walls):
            wall_horiz_right = (x, y + 1) in self.horiz_walls
            wall_horiz_left = (x, y - 1) in self.horiz_walls
            wall_vert_up = (x - 1, y) in self.verti_walls
            wall_vert_down = (x + 1, y) in self.verti_walls
            if is_horiz:
                if wall_horiz_right or wall_horiz_left:
                    return False
                self.horiz_walls.append(tuple(pos))
                if not self.paths_exist():
                    a = self.horiz_walls.pop()
                    return False
                self.horiz_walls.pop()
                return True
            else:
                if wall_vert_up or wall_vert_down:
                    return False
                self.verti_walls.append(tuple(pos))
                if not self.paths_exist():
                    a = self.verti_walls.pop()
                    return False
                self.verti_walls.pop()
                return True
        else:
            return False

    def get_legal_pawn_moves(self, player):
        """Returns legal moves for the pawn of player."""
        (x, y) = self.pawns[player]
        positions = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
        (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
        (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)]
        moves = []
        for new_pos in positions:
            if self.is_pawn_move_ok(self.pawns[player], new_pos,
                self.pawns[(player + 1) % 2]):
                moves.append(('P', new_pos[0], new_pos[1]))
        return moves


    def get_legal_wall_moves(self, player):
        """Returns legal wall placements (adding a wall
        somewhere) for player.
        """
        positions = []
        moves = []
        if self.nb_walls[player] <= 0:
            return moves
        for i in range(self.size - 1):
            for j in range(self.size - 1):
                positions.append((i, j))
        for pos in positions:
            if self.is_wall_possible_here(pos, True):
                moves.append(('WH', pos[0], pos[1]))
            if self.is_wall_possible_here(pos, False):
                moves.append(('WV', pos[0], pos[1]))
        return moves

    def get_actions(self, player):
        """ Returns all the possible actions for player."""
        pawn_moves = self.get_legal_pawn_moves(player)
        wall_moves = self.get_legal_wall_moves(player)
        pawn_moves.extend(wall_moves)
        return pawn_moves

    def is_action_valid(self, action, player):
        """Returns True if the action played by player
        is valid; False otherwise.
        """
        kind, i, j = action
        if kind == 'P':
            return self.is_pawn_move_ok(self.pawns[player], (i, j),
                self.pawns[(player + 1) % 2])
        elif kind == 'WH':
            wall_pos = self.is_wall_possible_here((i, j), True)
            return wall_pos
        elif kind == 'WV':
            wall_pos = self.is_wall_possible_here((i, j), False)
            return wall_pos
        else:
            return False

    def play_action(self, action, player):
        """Play an action if it is valid.

        If the action is invalid, raise an InvalidAction exception.
        Return self.

        Arguments:
        action -- the action to be played
        player -- the player who is playing

        """
        if not self.is_action_valid(action, player):
            raise InvalidAction(action, player)
        kind, x, y = action
        if kind == 'WH':
            self.add_wall((x, y), True, player)
        elif kind == 'WV':
            self.add_wall((x, y), False, player)
        elif kind == 'P':
            self.move_pawn((x, y), player)
        else:
            raise InvalidAction(action, player)
        return self

    def is_finished(self):
        """Return whether no more moves can be made (i.e.,
        game finished).
        """
        return self.pawns[PLAYER1][0] == self.goals[PLAYER1] or \
            self.pawns[PLAYER2][0] == self.goals[PLAYER2]

    def is_playerwin(self, player):
        if (self.pawns[player][0] == self.goals[player]):
            return True
        else:
            return False

    def get_score(self, player=PLAYER1):
        """Return a score for this board for the given player.

        The score is the difference between the lengths of the shortest path
        of the player minus the one of its opponent. It also takes into
        account the remaining number of walls.

        """
        score = self.min_steps_before_victory((player + 1) % 2) - \
                self.min_steps_before_victory(player)
        if not score:
            score = self.nb_walls[player] - self.nb_walls[(player + 1) % 2]
        return score


def dict_to_board(dictio):
    """Return a clone of the board object encoded as a dictionary."""
    clone_board = Board()
    clone_board.pawns[0] = dictio['pawns'][0]
    clone_board.goals[0] = dictio['goals'][0]
    clone_board.pawns[1] = dictio['pawns'][1]
    clone_board.goals[1] = dictio['goals'][1]
    for (x, y) in dictio['horiz_walls']:
        clone_board.horiz_walls.append((x, y))
    for (x, y) in dictio['verti_walls']:
        clone_board.verti_walls.append((x, y))
    clone_board.nb_walls[0] = dictio['nb_walls'][0]
    clone_board.nb_walls[1] = dictio['nb_walls'][1]
    return clone_board


def load_percepts(csvfile):
    """Load percepts from a CSV file.

    Cells are hexadecimal numbers.

    """
    if isinstance(csvfile, str):
        with open(csvfile, "r") as f:
            return load_percepts(f)
    else:
        import csv
        percepts = []
        for row in csv.reader(csvfile):
            if not row:
                continue
            row = [int(c.strip(), 16) for c in row]
            percepts.append(row)
            assert len(row) == len(percepts[0]), \
                    "rows must have the same length"
        return percepts


class Agent:

    """Interface for an Arlecchino agent"""

    def initialize(self, percepts, players, time_left):
        """Begin a new game.

        The computation done here also counts in the time credit.

        Arguments:
        percepts -- the initial board in a form that can be fed to the Board
            constructor.
        players -- sequence of players this agent controls
        time_left -- a float giving the number of seconds left from the time
            credit for this agent (all players taken together). If the game is
            not time-limited, time_left is None.

        """
        pass

    def play(self, percepts, player, step, time_left):
        """Play and return an action.

        Arguments:
        percepts -- the current board in a form that can be fed to the Board
            constructor.
        player -- the player to control in this step
        step -- the current step number, starting from 1
        time_left -- a float giving the number of seconds left from the time
            credit. If the game is not time-limited, time_left is None.

        """
        pass


def serve_agent(agent, address, port):
    """Serve agent on specified bind address and port number."""
    from xmlrpc.server import SimpleXMLRPCServer
    server = SimpleXMLRPCServer((address, port), allow_none=True)
    server.register_instance(agent)
    print("Listening on ", address, ":", port, sep="")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass


def agent_main(agent, args_cb=None, setup_cb=None):
    """Launch agent server depending on arguments.

    Arguments:
    agent -- an Agent instance
    args_cb -- function taking two arguments: the agent and an
        ArgumentParser. It can add custom options to the parser.
        (None to disable)
    setup_cb -- function taking three arguments: the agent, the
        ArgumentParser and the options dictionary. It can be used to
        configure the agent based on the custom options. (None to
        disable)

    """
    import argparse

    def portarg(string):
        value = int(string)
        if value < 1 or value > 65535:
            raise argparse.ArgumentTypeError("%s is not a valid port number" %
                                             string)
        return value

    parser = argparse.ArgumentParser()
    parser.add_argument("-b", "--bind", dest="address", default="",
                        help="bind to address ADDRESS (default: *)")
    parser.add_argument("-p", "--port", type=portarg, default=8000,
                        help="set port number (default: %(default)s)")
    if args_cb is not None:
        args_cb(agent, parser)
    args = parser.parse_args()
    if setup_cb is not None:
        setup_cb(agent, parser, args)

    serve_agent(agent, args.address, args.port)
