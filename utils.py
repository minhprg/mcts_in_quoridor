
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