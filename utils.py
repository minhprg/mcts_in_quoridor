
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
    moves = board.get_legal_pawn_moves(player)
    #for move in board.get_legal_pawn_moves(player):
        #d0 = board.get_shortest_path_simplified(player)
        #d1 = board.clone().play_action(move, player).get_shortest_path_simplified(player)
        #if (d1 <= d0):
        #moves.append (move)

    opponent_position = board.pawns[(player + 1) % 2]
    x = opponent_position[0];
    y = opponent_position[1];
    positions = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1),
        (x + 1, y + 1), (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1),
        (x + 2, y), (x - 2, y), (x, y + 2), (x, y - 2)]

    for pos in positions:
        if board.is_wall_possible_here(pos, True):
            moves.append(('WH', pos[0], pos[1]))
        if board.is_wall_possible_here(pos, False):
            moves.append(('WV', pos[0], pos[1]))

    return moves