#!/usr/bin/env python3
"""
Quoridor agent.
Copyright (C) 2013, <<<<<<<<<<< YOUR NAMES HERE >>>>>>>>>>>

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

from quoridor import *
import minimax


class MyAgent(Agent, minimax.Game):

    """My Quoridor agent."""

    def successors(self, state):
        """The successors function must return (or yield) a list of
        pairs (a, s) in which a is the action played to reach the
        state s; s is the new state, i.e. a pair (b, p) where
        b is the new board after the action a has been played and
        p is the player to play the next move.
        """
        board, player = state
        # TODO
        # IF in the last row AND found the end game state
        if (board.goals[player] == 8):
            last = -1
        else:
            last = 1

        manhattan = board.goals[player] - board.pawns[player][0]

        if board.pawns[player][0] == board.goals[player] + last and len(board.get_shortest_path_simplified(player)) == 1 and manhattan == 1:
            print("Last step!")
            move = ('P', board.goals[player], board.pawns[player][1])
            yield (move, (board.clone().play_action(move, player), (player + 1) % 2))

        else:
            for move in board.get_legal_pawn_moves(player):
                yield (move, (board.clone().play_action(move, player), (player + 1) % 2))

            opponent_position = board.pawns[(player + 1) % 2]
            wall_adjacent_consider_level = 2

            if wall_adjacent_consider_level == 1:
                for move in board.get_legal_wall_moves(player):
                    import math
                    if math.fabs(opponent_position[0] - move[1]) < 2 and math.fabs(opponent_position[1] - move[2]) < 2:
                        if move[1] - opponent_position[0] < 1 and move[2] - opponent_position[1] < 1:

                            yield (move, (board.clone().play_action(move, player), (player + 1) % 2))
            else:
                for move in board.get_legal_wall_moves(player):
                    import math
                    if math.fabs(opponent_position[0] - move[1]) < 3 and math.fabs(opponent_position[1] - move[2]) < 3:
                        if move[1] - opponent_position[0] < 2 and move[2] - opponent_position[1] < 2:

                            yield (move, (board.clone().play_action(move, player), (player + 1) % 2))


    def cutoff(self, state, depth):
        """The cutoff function returns true if the alpha-beta/minimax
        search has to stop; false otherwise.
        """
        board, player = state
        # TODO
        if board.is_finished():
            return True

        if depth == 2:
            return True
        return board.is_finished()

    def evaluate(self, state):
        """The evaluate function must return an integer value
        representing the utility function of the board.
        """
        import math
        board, player = state
        # TODO
        SPP = len(board.get_shortest_path_simplified(player))
        OPP = len(board.get_shortest_path_simplified((player + 1) % 2))
        player_manhattan = math.fabs(board.pawns[player][0] - board.goals[player])

        opponent_manhattan = math.fabs(board.pawns[(player + 1) % 2][0] - board.goals[(player + 1) % 2])
        MDP = (9 - player_manhattan) / 9
        MDO = (9 - opponent_manhattan) / 9

        wall_left = board.nb_walls[player] - board.nb_walls[(player + 1) % 2]

        score = 20 * (((81 - SPP) / 81) - ((81 - OPP) / 81)) + 10 * (MDP - MDO) + wall_left + random.randrange(1, 2, 1) / 10

        return score

    def play(self, percepts, player, step, time_left):
        """This function is used to play a move according
        to the percepts, player and time left provided as input.
        It must return an action representing the move the player
        will perform.
        """
        self.player = player
        state = (dict_to_board(percepts), player)
        return minimax.search(state, self)


if __name__ == "__main__":
    agent_main(MyAgent())
