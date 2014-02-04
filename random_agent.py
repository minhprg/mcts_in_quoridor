#!/usr/bin/env python3
"""
Dummy random Quoridor agent.
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
from quoridor import *
import shutil


class RandomAgent(Agent):

    """A dumb random Quoridor agent."""

    def play(self, percepts, player, step, time_left):
        board = dict_to_board(percepts)
        print(len(board.get_actions(player)))
        if step == 101:
            # end file
            import datetime
            import time
            ts = time.time()
            newfile = datetime.datetime.fromtimestamp(ts).strftime('%Y%m%d%H%M%S')
            # copy file
            shutil.copyfile("tmp.txt", "results/" + newfile)
            # clear tmp file
            open("tmp.txt", "w").close()

        if step < 101:
            # append to file
            with open("tmp.txt", "a") as myfile:
                myfile.writelines(len(board.get_actions(player)).__str__() + "\n")

        return random.choice(list(board.get_actions(player)))


if __name__ == "__main__":
    agent_main(RandomAgent())
