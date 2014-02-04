"""
Graphical user interface for the Quoridor game.
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

import threading
from tkinter import *
from tkinter.font import Font

from quoridor import *
from game import Viewer


class TkViewer(Viewer):

    """Graphical viewer using Tk."""
    # size of a tile
    w_tile = 50
    # width of a wall
    w_wall = 20
    # size of a cell (including the walls)
    w = w_tile + w_wall
    # diameter of a pawn
    w_pawn = 4 * w_tile / 5
    # length of a wall
    l_wall = 2 * w_tile + w_wall
    # the x offset before the board begins
    x_offset = 20
    # the y offset before the board begins
    y_offset = 20
    # the separation between the tiles and the unused walls
    y_separa = 10
    # the width of the scoreboard
    scoreboard_width = 300
    # the y distance between the walls on the scoreboard
    y_wall_scoreboard_offset = y_offset
    # the x distance between the walls on the scoreboard
    x_wall_scoreboard_offset = 25
    # the x offset of the scoreboard
    x_scoreboard_offset = \
        (scoreboard_width - 5 * w_wall - 4 * x_wall_scoreboard_offset) / 2
    # the y offset of the scoreboard
    y_scoreboard_offset = y_offset / 2
    # the fontsize used in the scoreboard
    scoreboard_font_size = 14
    # the total x offset on the left
    left_off_x = x_offset + w_wall / 2
    # the total y offset on the left
    left_off_y = y_offset + w_wall / 2
    # the total x offset on the right
    right_off_x = left_off_x + w_tile * 9 + w_wall * 8
    # the total y offset on the right
    right_off_y = left_off_y + w_tile * 9 + w_wall * 8

    names = ("Blue", "Red")  # player names
    pawn_colors = ("#3465A4", "#EF2929")  # pawn player colors
    pawn_outlin = ("#C2D0E3", "#FABEBE")
    tile_color = "#EEEEEC"
    tile_backg = "#631919"
    wall_color = "#F1E2BE"
    wall_outli = ("#3465A4", "#EF2929")
    wall_backg = "#999999"

    ###########################################################################
    ## Helper functions

    def get_tile_xy(self, i, j):
        y = self.y_offset + (i + .5) * self.w
        x = self.x_offset + (j + .5) * self.w
        return (x, y)

    def get_wall_xy(self, i, j):
        y = self.y_offset + (i + 1) * self.w
        x = self.x_offset + (j + 1) * self.w
        return (x, y)

    def get_wall_scoreboard_xy(self, player, wall_num):
        y = self.scoreboard_font_size + self.y_scoreboard_offset + \
            self.y_wall_scoreboard_offset + 0.5 * self.l_wall
        if player == 1:
            y += self.canvas_height / 2
        if wall_num >= 5:
            y += self.l_wall + self.y_wall_scoreboard_offset
        x = self.x_scoreboard_offset + (wall_num % 5 + 0.5) * self.w_wall + \
            (wall_num % 5) * self.x_wall_scoreboard_offset
        return (x, y)

    def get_object(self, x, y):
        if x < self.left_off_x or y < self.left_off_y or \
                x >= self.right_off_x or y >= self.right_off_y:
            return (-1, -1, "border")
        x_rest = (x - self.left_off_x) % self.w
        y_rest = (y - self.left_off_y) % self.w
        if x_rest < self.w_tile and y_rest < self.w_tile:
            column = int((x - self.left_off_x) / self.w)
            row = int((y - self.left_off_y) / self.w)
            return (row, column, "tile")
        elif x_rest > self.w_tile and y_rest < self.w_tile:
            column = int((x - self.left_off_x) / self.w)
            if (y - self.left_off_y) < (self.w + self.w_tile / 2):
                row = 0
            elif (y - self.left_off_y) >= (self.w * 7 + self.w_tile / 2):
                row = 7
            else:
                row = 1 + int((y -
                    (self.left_off_y + self.w + self.w_tile / 2)) /
                    (self.w_tile + self.w_wall))
            return(row, column, "bg_v_wall")
        else:
            row = int((y - self.left_off_y) / self.w)
            if (x - self.x_offset) < (self.w + self.w_tile / 2):
                column = 0
            elif (x - self.x_offset) >= (self.w * 7 + self.w_tile / 2):
                column = 7
            else:
                column = 1 + int((x -
                    (self.left_off_x + self.w + self.w_tile / 2)) /
                    (self.w_tile + self.w_wall))
            return(row, column, "bg_h_wall")

    ###########################################################################
    ## UI construction

    def __init__(self):
        self.running = False
        self.root = Tk()
        self.barrier = threading.Event()
        self.barrier.clear()

    def init_viewer(self, board):
        self.board = board
        self.barrier.set()

    def run(self):
        """Launch the GUI."""
        if self.running:
            return
        self.running = True
        self.barrier.wait()
        ## Create UI
        self.canvas_width = 2 * self.x_offset + self.w * self.board.cols
        self.canvas_height = 2 * self.y_offset + self.w * self.board.rows
        ## Root window
        self.root.title("Quoridor")
        self.root.resizable(False, False)
        self.root.bind_all("<Escape>", self.close)
        mainframe = Frame(self.root)
        mainframe.pack()
        ## Canvas board
        self.canvas = Canvas(mainframe, width=self.canvas_width,
                             height=self.canvas_height)
        self.canvas.pack(side=LEFT)
        # tile_ids is filled by _update_gui
        self.tile_ids = [[0 for j in range(self.board.cols)]
                         for i in range(self.board.rows)]
        self.bg_h_wall_ids = [[0 for j in range(self.board.cols - 1)]
                         for i in range(self.board.rows - 1)]
        self.bg_v_wall_ids = [[0 for j in range(self.board.cols - 1)]
                         for i in range(self.board.rows - 1)]
        self.h_wall_ids = [[0 for j in range(self.board.cols - 1)]
                         for i in range(self.board.rows - 1)]
        self.v_wall_ids = [[0 for j in range(self.board.cols - 1)]
                         for i in range(self.board.rows - 1)]
        # tile_ids is filled by _update_gui
        self.scoreboard_wall_ids = [[0 for j in
                        range(self.board.starting_walls)]
                            for i in range(len(self.board.pawns))]
        self.pawn_ids = [0 for i in range(len(self.board.pawns))]
        for i in range(self.board.rows):
            for j in range(self.board.cols):
                x, y = self.get_tile_xy(i, j)
                self.canvas.create_rectangle(
                    x - self.w_tile / 2, y - self.w_tile / 2,
                    x + self.w_tile / 2, y + self.w_tile / 2,
                    width=2, outline="grey")
        ## Score board
        medfont = Font(size=self.scoreboard_font_size)
        self.scoreboard = Canvas(mainframe, width=self.scoreboard_width,
            height=self.canvas_height)
        self.scoreboard.pack(side=RIGHT)
        self.wall_titles = \
           [self.scoreboard.create_text(self.scoreboard_width / 2,
            self.y_scoreboard_offset, font=medfont, anchor='n'),
            self.scoreboard.create_text(self.scoreboard_width / 2,
                self.y_scoreboard_offset + self.canvas_height / 2,
                font=medfont, anchor='n')]
        self.scoreboard.itemconfigure(self.wall_titles[0],
            fill=self.pawn_colors[0], text="Walls of player 1")
        self.scoreboard.itemconfigure(self.wall_titles[1],
            fill=self.pawn_colors[1], text="Walls of player 2")
        ## Status bar
        self.status = Label(self.root, height=2, justify=LEFT)
        self.status.pack(side=LEFT)
        self.status_text = ""
        self.substatus_text = ""
        self.buttons = Frame(self.root)
        self.buttons.pack(side=RIGHT)
        ## Draw board
        self.draw_board(self.board)
        ## Launch event loop
        try:
            self.root.mainloop()
        except KeyboardInterrupt:
            pass
        # ensure the main thread exits when closing viewer during human action
        self.root = None
        self.action = None  # generate invalid action
        self.barrier.set()

    ###########################################################################
    ## General UI

    def close(self, event=None):
        """Close the GUI."""
        if self.root is not None:
            self.root.destroy()

    def set_status(self, new_status):
        """Set the first line of the status bar."""
        s = self.status_text = new_status
        if self.substatus_text:
            s += "\n" + self.substatus_text
        self.status["text"] = s

    def set_substatus(self, new_substatus):
        """Set the second line of the status bar."""
        self.substatus_text = new_substatus
        self.set_status(self.status_text)

    ###########################################################################
    ## Viewer UI

    def playing(self, step, player):
        if self.root is None:
            return
        self.root.after_idle(self._playing, step, player)

    def _playing(self, step, player):
        """Same as self.playing(step, player), but may only be called from the
        gui thread."""
        self.set_status("Step %d: %s's turn." % (step, self.names[player]))
        self.set_substatus("")

    def update(self, step, action, player):
        self.board.play_action(action, player)
        if self.root is not None:
            self.root.after_idle(self.redraw_board, self.board)

    def draw_board(self, board):
        # creating the background
        self.canvas.create_rectangle(
            self.x_offset, self.y_offset,
            self.x_offset + self.w * self.board.cols,
            self.y_offset + self.w * self.board.rows,
            fill=self.wall_backg, outline=self.wall_backg)
        # creating the walls (on the board)
        for i in range(board.rows - 1):
            for j in range(board.cols - 1):
                x, y = self.get_wall_xy(i, j)
                self.canvas.create_rectangle(
                    x - self.l_wall / 2, y - self.w_wall / 2,
                    x + self.l_wall / 2, y + self.w_wall / 2,
                    fill=self.wall_backg, outline=self.wall_backg,
                    tags=["bg_h_walls"])
                self.bg_h_wall_ids[i][j] = self.canvas.create_rectangle(
                    x - self.l_wall / 2, y - self.w_wall / 2 + 2,
                    x + self.l_wall / 2, y + self.w_wall / 2 - 3,
                    tags=["bg_h_walls"], width=0)
                self.mark_object((i, j, "bg_h_wall"))
                self.canvas.create_rectangle(
                    x - self.w_wall / 2, y - self.l_wall / 2,
                    x + self.w_wall / 2, y + self.l_wall / 2,
                    fill=self.wall_backg, outline=self.wall_backg,
                    tags=["bg_v_walls"])
                self.bg_v_wall_ids[i][j] = self.canvas.create_rectangle(
                    x - self.w_wall / 2 + 2, y - self.l_wall / 2,
                    x + self.w_wall / 2 - 3, y + self.l_wall / 2,
                    tags=["bg_v_walls"], width=0)
                self.mark_object((i, j, "bg_v_wall"))
        # creating the tiles
        for i in range(board.rows):
            for j in range(board.cols):
                x, y = self.get_tile_xy(i, j)
                self.canvas.create_rectangle(
                    x - self.w_tile / 2, y - self.w_tile / 2,
                    x + self.w_tile / 2, y + self.w_tile / 2,
                    fill=self.tile_backg, tags=["tiles"])
                self.tile_ids[i][j] = self.canvas.create_rectangle(
                    x - self.w_tile / 2, y - self.w_tile / 2,
                    x + self.w_tile / 2, y + self.w_tile / 2,
                    tags=["tiles"])
                # ensure coherent unselected appearance
                self.mark_object((i, j, "tile"))
        # creating the wall slots (on the scoreboard)
        for player in range(2):
            i, j = self.board.pawns[player]
            x, y = self.get_tile_xy(i, j)
            self.pawn_ids[player] = self.canvas.create_oval(
                x - self.w_pawn / 2, y - self.w_pawn / 2,
                x + self.w_pawn / 2, y + self.w_pawn / 2,
                fill=self.pawn_colors[player],
                outline=self.pawn_outlin[player],
                width=2, tags=["pawns"])
            for wall_num in range(self.board.nb_walls[player]):
                x, y = self.get_wall_scoreboard_xy(player, wall_num)
                self.scoreboard.create_rectangle(
                    x - self.w_wall / 2, y - self.l_wall / 2,
                    x + self.w_wall / 2, y + self.l_wall / 2,
                    fill=self.wall_backg, outline=self.wall_backg)
                self.scoreboard_wall_ids[player][wall_num] = \
                    self.scoreboard.create_rectangle(
                    x - self.w_wall / 2 + 1, y - self.l_wall / 2 + 1,
                    x + self.w_wall / 2, y + self.l_wall / 2,
                    fill=self.wall_color, outline=self.wall_outli[player],
                    width=2,
                    tags=["scoreboard_walls"])
        for (i, j) in self.board.horiz_walls:
            x, y = self.get_wall_xy(i, j)
            self.h_wall_ids[i][j] = self.canvas.create_rectangle(
                x - self.l_wall / 2, y - self.w_wall / 2 + 2,
                x + self.l_wall / 2, y + self.w_wall / 2 - 3,
                fill=self.wall_color, width=2,
                tags=["h_walls"])
        for (i, j) in self.board.verti_walls:
            x, y = self.get_wall_xy(i, j)
            self.v_wall_ids[i][j] = self.canvas.create_rectangle(
                x - self.w_wall / 2 + 2, y - self.l_wall / 2,
                x + self.w_wall / 2 - 3, y + self.l_wall / 2,
                fill=self.wall_color, width=2,
                tags=["v_walls"])

    def redraw_board(self, board):
        """Draw a board with all unselected tiles."""
        self.canvas.delete("pawns")
        self.canvas.delete("h_walls")
        self.canvas.delete("v_walls")
        self.scoreboard.delete("scoreboard_walls")
        for i in range(board.rows):
            for j in range(board.cols):
                # ensure coherent unselected appearance
                self.mark_object((i, j, "tile"))
        for player in range(2):
            i, j = self.board.pawns[player]
            x, y = self.get_tile_xy(i, j)
            self.pawn_ids[player] = self.canvas.create_oval(
                x - self.w_pawn / 2, y - self.w_pawn / 2,
                x + self.w_pawn / 2, y + self.w_pawn / 2,
                fill=self.pawn_colors[player],
                outline=self.pawn_outlin[player],
                width=2, tags=["pawns"])
            for wall_num in range(self.board.nb_walls[player]):
                x, y = self.get_wall_scoreboard_xy(player, wall_num)
                self.scoreboard_wall_ids[player][wall_num] = \
                    self.scoreboard.create_rectangle(
                    x - self.w_wall / 2 + 1, y - self.l_wall / 2 + 1,
                    x + self.w_wall / 2, y + self.l_wall / 2,
                    fill=self.wall_color, outline=self.wall_outli[player],
                    width=2,
                    tags=["scoreboard_walls"])
        for i in range(board.rows - 1):
            for j in range(board.cols - 1):
                self.mark_object((i, j, "bg_h_wall"))
                self.mark_object((i, j, "bg_v_wall"))
        for (i, j) in self.board.horiz_walls:
            x, y = self.get_wall_xy(i, j)
            self.canvas.create_rectangle(
                x - self.l_wall / 2, y - self.w_wall / 2 + 2,
                x + self.l_wall / 2, y + self.w_wall / 2 - 3,
                fill=self.wall_color, width=2,
                tags=["h_walls"])
        for (i, j) in self.board.verti_walls:
            x, y = self.get_wall_xy(i, j)
            self.canvas.create_rectangle(
                x - self.w_wall / 2 + 2, y - self.l_wall / 2,
                x + self.w_wall / 2 - 3, y + self.l_wall / 2,
                fill=self.wall_color, width=2,
                tags=["v_walls"])

    def mark_object(self, selection, style="unselected"):
        """Mark tile as unselected, hover or moving."""
        i, j, object_type = selection
        if object_type == "tile":
            o = self.tile_ids[i][j]
            if style == "unselected":
                self.canvas.itemconfigure(o, outline=self.tile_color, width=2)
            elif style == "hover":
                self.canvas.itemconfigure(o, outline="#008000", width=3)
            else:
                assert False
        if object_type == "bg_h_wall":
            o = self.bg_h_wall_ids[i][j]
            if style == "unselected":
                self.canvas.tag_lower(o, "tiles")
                self.canvas.itemconfigure(o, width=0)
            elif style == "hover":
                self.canvas.tag_raise(o, "tiles")
                self.canvas.itemconfigure(o, outline="#008000", width=3)
            else:
                assert False
        if object_type == "bg_v_wall":
            o = self.bg_v_wall_ids[i][j]
            if style == "unselected":
                self.canvas.tag_lower(o, "tiles")
                self.canvas.itemconfigure(o, width=0)
            elif style == "hover":
                self.canvas.tag_raise(o, "tiles")
                self.canvas.itemconfigure(o, outline="#008000", width=3)
            else:
                assert False

    def put_wall(self, position, player):
        i, j, is_horiz = position
        self.scoreboard.delete(
            self.scoreboard_wall_ids[player][self.board.nb_walls[player] - 1])

    def finished(self, steps, winner, reason=""):
        if self.root is None:
            return
        self.root.after_idle(self._finished, steps, winner, reason)

    def _finished(self, steps, winner, reason=""):
        """Same as self.finished(steps, winner, reason), but may only be called
        from the gui thread."""
        if winner == 0:
            s = "Draw game"
        elif winner > 0:
            s = "/".join(n for p, n in enumerate(self.names) if p % 2 == 0) + \
                " has won"
        else:
            s = "/".join(n for p, n in enumerate(self.names) if p % 2 == 1) + \
                " has won"
        s += " after " + str(steps) + " steps."
        self.set_status(s)
        if reason:
            self.set_substatus(reason)
        self.canvas.unbind("<Motion>")
        self.canvas.unbind("<Button-1>")

    ###########################################################################
    ## Human player UI

    def play(self, percepts, player, step, time_left):
        if self.root is None:
            return None
        self.player = player
        self.barrier.clear()
        self.root.after_idle(self._play_start)
        self.barrier.wait()
        return self.action

    def _play_start(self):
        """Configure GUI to accept user input."""
        self.canvas.bind("<Leave>", self._play_leave)
        self.canvas.bind("<Motion>", self._play_motion)
        self.canvas.bind("<Button-1>", self._play_click)
        self._play_reset()

    def _play_reset(self, event=None):
        """Handler for Reset button click during play mode. If clear is false,
        do not clear the current action (this is a hack to keep the last played
        action on screen when a player is beginning to play)."""
        self.action = None
        self.selection = None
        self.set_substatus("Select a tile to move.")
        self.canvas.event_generate("<Motion>")

    def _play_leave(self, event):
        """Handler for Mouse Leave event"""
        if event.state & 0x100:  # leave event is also called on mouse click
            return
        if self.selection is not None:
            self.mark_object(self.selection)
            self.selection = None

    def _play_motion(self, event):
        """Handler for Mouse Motion event"""
        self._play_leave(event)
        i, j, object_type = self.get_object(event.x, event.y)
        if i < 0 or i >= self.board.rows or j < 0 or j >= self.board.cols:
            return
        if object_type == "tile" and \
                self.board.can_move_here(i, j, self.player):
            self.selection = (i, j, object_type)
            self.mark_object(self.selection, "hover")
        elif object_type == "bg_h_wall" and \
                self.board.is_wall_possible_here((i, j), True) and \
                self.board.nb_walls[self.player] > 0:
            self.selection = (i, j, object_type)
            self.mark_object(self.selection, "hover")
        elif object_type == "bg_v_wall" and \
                self.board.is_wall_possible_here((i, j), False) and \
                self.board.nb_walls[self.player] > 0:
            self.selection = (i, j, object_type)
            self.mark_object(self.selection, "hover")

    def _play_click(self, event):
        """Handler for Mouse Click event"""
        if self.selection is None:
            return
        i, j, object_type = self.selection
        if object_type == "tile":
            self.set_substatus("")
            self.barrier.set()
            self.action = ("P", i, j)
        elif object_type == "bg_h_wall":
            self.put_wall((i, j, True), self.player)
            self.set_substatus("")
            self.barrier.set()
            self.action = ("WH", i, j)
        elif object_type == "bg_v_wall":
            self.put_wall((i, j, False), self.player)
            self.set_substatus("")
            self.barrier.set()
            self.action = ("WV", i, j)

    ###########################################################################
    ## Replay UI

    def replay(self, trace, speed, show_end=False):
        """Replay a game given its saved trace.

        Attributes:
        trace -- trace of the game
        speed -- speed scale of the replay
        show_end -- start with the final state instead of the initial state

        """
        self.trace = trace
        self.speed = speed
        # generate all boards to access them backwards
        self.boards = [trace.get_initial_board()]
        for player, action, t in trace.actions:
            b = self.boards[-1].clone()
            b.play_action(action, player)
            self.boards.append(b)
        if self.root is not None:
            self.root.after_idle(self._replay_gui, show_end)
        self.board = self.boards[0]
        self.barrier.set()
        self.run()

    def _replay_gui(self, show_end):
        """Initialize replay UI"""
        self.b_prev = Button(self.buttons, text="<", command=self._replay_prev)
        self.b_play = Button(self.buttons, text="Play",
                             command=self._replay_play)
        self.b_next = Button(self.buttons, text=">", command=self._replay_next)
        self.b_prev.pack(side=LEFT)
        self.b_play.pack(side=LEFT)
        self.b_next.pack(side=LEFT)
        self.root.bind_all("<Left>", self._replay_prev)
        self.root.bind_all("<Right>", self._replay_next)
        self.root.bind_all("<Home>", self._replay_first)
        self.root.bind_all("<End>", self._replay_last)
        self.root.bind_all("<space>", self._replay_play)
        self.isplaying = False
        if show_end:
            # Only used when replaying right after a game, so use the hack to
            # keep the last played action on screen
            self._replay_goto(len(self.boards) - 1, clear=False)
        else:
            self._replay_goto(0)

    def _replay_goto(self, step, clear=True):
        """Update UI to show step step. If clear is false, we do not clear the
        current action if this is the last step (this is a hack to keep the
        last played action on screen when replaying right after a game."""
        self.step = step
        self.board = self.boards[step]
        self.redraw_board(self.board)
        if step == len(self.boards) - 1:
            self._finished(step, self.trace.winner, self.trace.reason)
            if self.isplaying:
                self.isplaying = False
                self.b_play["text"] = "Play"
        else:
            player, action, t = self.trace.actions[step]
            self._playing(step, player)
        if self.isplaying:
            if self.speed < 0:
                steptime = -self.trace.actions[step][1] / self.speed
            else:
                steptime = self.speed
            self.after_id = self.root.after(
                    int(steptime * 1000),
                    self._replay_goto, step + 1)
        else:
            if step == 0:
                self.b_prev["state"] = DISABLED
            else:
                self.b_prev["state"] = NORMAL
            if step == len(self.boards) - 1:
                self.b_next["state"] = DISABLED
            else:
                self.b_next["state"] = NORMAL

    def _replay_next(self, event=None):
        if not self.isplaying and self.step < len(self.boards) - 1:
            self._replay_goto(self.step + 1)

    def _replay_prev(self, event=None):
        if not self.isplaying and self.step > 0:
            self._replay_goto(self.step - 1)

    def _replay_first(self, event=None):
        if not self.isplaying:
            self._replay_goto(0)

    def _replay_last(self, event=None):
        if not self.isplaying:
            self._replay_goto(len(self.boards) - 1)

    def _replay_play(self, event=None):
        if self.isplaying:
            self.root.after_cancel(self.after_id)
            self.isplaying = False
            self.b_play["text"] = "Play"
            self._replay_goto(self.step)
        else:
            self.isplaying = True
            self.b_prev["state"] = DISABLED
            self.b_next["state"] = DISABLED
            self.b_play["text"] = "Pause"
            if self.step < len(self.boards) - 1:
                self._replay_goto(self.step)
            else:
                self._replay_goto(0)
