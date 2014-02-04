#!/usr/bin/env python3
"""
Main program for the Quoridor game.
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

import logging
import time
import socket
import xmlrpc.client
import pickle

from quoridor import *


class TimeCreditExpired(Exception):
    """An agent has expired its time credit."""


class Viewer(Agent):

    """Interface for an Quoridor viewer and human agent."""

    def init_viewer(self, board):
        """Initialize the viewer.

        Arguments:
        board -- initial board

        """
        pass

    def playing(self, step, player):
        """Player player is currently playing step step."""
        pass

    def update(self, step, action, player):
        """Update the viewer after an action has been played.

        Arguments:
        step -- current step number
        action -- action played
        player -- player that has played

        """
        pass

    def finished(self, steps, winner, reason=""):
        """The game is finished.

        Arguments:
        steps -- the number of steps played
        winner -- the winner (>0: even players, <0: odd players, 0: draw)
        reason -- a specific reason for the victory or "" if standard
        """
        pass

    def replay(self, trace, speed=1.0):
        """Replay a game given its saved trace."""
        step = 0
        self.init_viewer(trace.get_initial_board())
        for player, action, t in trace.actions:
            step += 1
            self.playing(step, player)
            if speed < 0:
                time.sleep(-t / speed)
            else:
                time.sleep(speed)
            self.update(step, action, player)
        self.finished(step, trace.winner, trace.reason)


class ConsoleViewer(Viewer):

    """Simple console viewer."""

    def init_viewer(self, board):
        self.board = board
        print(self.board)

    def playing(self, step, player):
        print("Step", step, "- player", player, "is playing")

    def update(self, step, action, player):
        print("Step", step, "- player", player, "has played", action)
        self.board.play_action(action, player)
        print(self.board)

    def play(self, percepts, player, step, time_left):
        while True:
            try:
                line = input("Player %d plays (kind, i, j): " % player)
            except EOFError:
                exit(1)
            try:
                kind, i, j = [x.strip() for x in line.split(",")]
                return (kind, int(i), int(j))
            except (ValueError, AssertionError):
                pass

    def finished(self, steps, winner, reason=""):
        if winner == 0:
            print("Draw game")
        else:
            print("Blue" if winner > 0 else "Red", "player has won!")
        if reason:
            print("Reason:", reason)


class Trace:

    """Keep track of a played game.

    Attributes:
    time_limits -- a sequence of 2 elements containing the time limits in
        seconds for each agent, or None for a time-unlimitted agent
    initial_board -- the initial board
    actions -- list of tuples (player, action, time) of the played action.
        Respectively, the player number, the action and the time taken in
        seconds.
    winner -- winner of the game
    reason -- specific reason for victory or "" if standard

    """

    def __init__(self, board, time_limits):
        """Initialize the trace.

        Arguments:
        board -- the initial board
        time_limits -- a sequence of 2 elements containing the time limits in
            seconds for each agent, or None for a time-unlimitted agent

        """
        self.time_limits = [t for t in time_limits]
        self.initial_board = board.clone()
        self.actions = []
        self.winner = 0
        self.reason = ""

    def add_action(self, player, action, t):
        """Add an action to the trace.

        Arguments:
        player -- the player
        action -- the played action, a tuple as specified by
            avalam.Board.play_action
        t -- a float representing the number of seconds the player has taken
            to generate the action

        """
        self.actions.append((player, action, t))

    def set_winner(self, winner, reason):
        """Set the winner.

        Arguments:
        winner -- the winner
        reason -- the specific reason of victory

        """
        self.winner = winner
        self.reason = reason

    def get_initial_board(self):
        """Return a Board instance representing the initial board."""
        return Board(self.initial_board)

    def write(self, f):
        """Write the trace to a file."""
        pickle.dump(self, f)


def load_trace(f):
    """Load a trace from a file."""
    return pickle.load(f)


class Game:

    """Main Quoridor game class."""

    def __init__(self, agents, board, viewer=None, credits=[None, None]):
        """New Quoridor game.

        Arguments:
        agents -- a sequence of 2 elements containing the agents (instances
            of Agent)
        board -- the board on which to play
        viewer -- the viewer or None if none should be used
        credits -- a sequence of 2 elements containing the time credit in
            seconds for each agent, or None for a time-unlimitted agent

        """
        self.agents = agents
        self.board = board
        self.viewer = viewer if viewer is not None else Viewer()
        self.credits = credits
        self.step = 0
        self.player = 0
        self.trace = Trace(board, credits)

    def play(self):
        """Play the game."""
        logging.info("Starting new game")
        self.viewer.init_viewer(self.board.clone())
        try:
            for agent in range(2):
                logging.debug("Initializing agent %d", agent)
                self.timed_exec("initialize",
                    self.board,
                    [agent, agent + 2],
                    agent=agent)

            while not self.board.is_finished():
                self.step += 1
                logging.debug("Asking player %d to play step %d",
                              self.player, self.step)
                self.viewer.playing(self.step, self.player)
                action, t = self.timed_exec("play",
                    self.board,
                    self.player,
                    self.step)
                self.board.play_action(action, self.player)
                self.viewer.update(self.step, action, self.player)
                self.trace.add_action(self.player, action, t)
                self.player = (self.player + 1) % 2
        except (TimeCreditExpired, InvalidAction) as e:
            if isinstance(e, TimeCreditExpired):
                logging.debug("Time credit expired")
                reason = "Opponent's time credit has expired."
            else:
                logging.debug("Invalid action: %s", e.action)
                reason = "Opponent has played an invalid action."
            if self.player % 2 == 0:
                winner = -1
            else:
                winner = 1
            self.step -= 1
        else:
            reason = ""
            winner = self.board.get_score()
            logging.info("Score: %d", winner)
        if winner > 0:
            logging.info("Winner: Blue player")
        elif winner < 0:
            logging.info("Winner: Red player")
        else:
            logging.info("Winner: draw game")
        self.trace.set_winner(winner, reason)
        self.viewer.finished(self.step, winner, reason)

    def timed_exec(self, fn, *args, agent=None):
        """Execute self.agents[agent].fn(*args, time_left) with the
        time limit for the current player.

        Return a tuple (result, t) with the function result and the time taken
        in seconds. If agent is None, the agent will be computed from
        self.player.

        """
        if agent is None:
            agent = self.player % 2
        if self.credits[agent] is not None:
            logging.debug("Time left for agent %d: %f", agent,
                          self.credits[agent])
            if self.credits[agent] < 0:
                raise TimeCreditExpired
            socket.setdefaulttimeout(self.credits[agent] + 1)
        start = time.time()
        try:
            result = getattr(self.agents[agent], fn) \
                        (*args + (self.credits[agent],))
        except socket.timeout:
            self.credits[agent] = -1.0  # ensure it is counted as expired
            raise TimeCreditExpired
        except (socket.error, xmlrpc.client.Fault) as e:
            logging.error("Agent %d was unable to play step %d." +
                    " Reason: %s", agent, self.step, e)
            raise InvalidAction
        end = time.time()
        t = end - start
        logging.info("Step %d: received result %s in %fs",
                     self.step, result, t)
        if self.credits[agent] is not None:
            self.credits[agent] -= t
            logging.debug("New time credit for agent %d: %f", agent,
                          self.credits[agent])
            if self.credits[agent] < -0.5:  # small epsilon to be sure
                raise TimeCreditExpired
        return (result, t)


def connect_agent(uri):
    """Connect to a remote player and return a proxy for the Player object."""
    return xmlrpc.client.ServerProxy(uri, allow_none=True)


if __name__ == "__main__":
    import argparse

    def posfloatarg(string):
        value = float(string)
        if value <= 0:
            raise argparse.ArgumentTypeError("%s is not strictly positive" %
                                             string)
        return value

    parser = argparse.ArgumentParser(
        usage="%(prog)s [options] AGENT1 AGENT2\n" +
              "       %(prog)s [options] -r FILE")
    parser.add_argument("agent1", nargs='?', default='human',
                        help="URI of the first agent (blue player) or" +
                             " keyword 'human' (default: human)",
                        metavar="AGENT1")
    parser.add_argument("agent2", nargs='?', default='human',
                        help="URI of the second agent (red player) or" +
                             " keyword 'human' (default: human)",
                        metavar="AGENT2")
    parser.add_argument("-v", "--verbose", action="store_true", default=False,
                        help="be verbose")
    parser.add_argument("--no-gui",
                        action="store_false", dest="gui", default=True,
                        help="do not try to load the graphical user interface")
    g = parser.add_mutually_exclusive_group()
    g.add_argument("--headless", action="store_true", default=False,
                   help="run without user interface (players cannot be" +
                        " human)")
    g.add_argument("-r", "--replay", type=argparse.FileType('rb'),
                   help="replay the trace written in FILE",
                   metavar="FILE")
    parser.add_argument("-w", "--write", type=argparse.FileType('wb'),
                        help="write the trace to FILE for replay with -r" +
                             " (no effect on replay)",
                        metavar="FILE")
    g = parser.add_argument_group("Rule options (no effect on replay)")
    g.add_argument("-t", "--time", type=posfloatarg,
                   help="set the time credit per player (default: untimed" +
                        " game)",
                   metavar="SECONDS")
    g.add_argument("--board", type=argparse.FileType('r'),
                   help="load initial board from FILE", metavar="FILE")
    g = parser.add_argument_group("Replay options")
    g.add_argument("-s", "--speed", type=posfloatarg,
                   help="set the duration of each step in seconds or scale" +
                        " if realtime (default: %(default)s)",
                   metavar="SECONDS", default=2.0)
    g.add_argument("--realtime", action="store_true", default=False,
                   help="replay with the real durations")
    args = parser.parse_args()
    if args.replay is None and args.headless and \
            (args.agent1 == "human" or args.agent2 == "human"):
        parser.error("human players are not allowed in headless mode")

    if args.realtime:
        args.speed = -args.speed

    level = logging.WARNING
    if args.verbose:
        level = logging.DEBUG
    logging.basicConfig(format="%(asctime)s -- %(levelname)s: %(message)s",
                        level=level)

    # Create initial board
    if args.replay is not None:
        # replay mode
        logging.info("Loading trace '%s'", args.replay.name)
        try:
            trace = load_trace(args.replay)
            args.replay.close()
        except (IOError, pickle.UnpicklingError) as e:
            logging.error("Unable to load trace. Reason: %s", e)
            exit(1)
        board = trace.get_initial_board()
    elif args.board is not None:
        # load board
        logging.debug("Loading board from '%s'", args.board.name)
        try:
            percepts = load_percepts(args.board)
            args.board.close()
        except Exception as e:
            logging.warning("Unable to load board. Reason: %s", e)
            parser.error("argument --board: invalid board")
        board = Board(percepts)
    else:
        # default board
        board = Board()

    # Create viewer
    if args.headless:
        args.gui = False
        viewer = None
    else:
        if args.gui:
            try:
                import gui
                viewer = gui.TkViewer()
            except Exception as e:
                logging.warning("Unable to load GUI, falling back to" +
                        " console. Reason: %s", e)
                args.gui = False
        if not args.gui:
            viewer = ConsoleViewer()

    if args.replay is None:
        # Normal play mode
        agents = [args.agent1, args.agent2]
        credits = [None, None]
        for i in range(2):
            if agents[i] == 'human':
                agents[i] = viewer
            else:
                agents[i] = connect_agent(agents[i])
                credits[i] = args.time
        game = Game(agents, board, viewer, credits)

        def play():
            try:
                game.play()
            except KeyboardInterrupt:
                exit()
            if args.write is not None:
                logging.info("Writing trace to '%s'", args.write.name)
                try:
                    game.trace.write(args.write)
                    args.write.close()
                except IOError as e:
                    logging.error("Unable to write trace. Reason: %s", e)
            if args.gui:
                logging.debug("Replaying trace.")
                viewer.replay(game.trace, args.speed, show_end=True)

        if args.gui:
            import threading
            threading.Thread(target=play).start()
            viewer.run()
        else:
            play()
    else:
        # Replay mode
        logging.debug("Replaying trace.")
        viewer.replay(trace, args.speed)
