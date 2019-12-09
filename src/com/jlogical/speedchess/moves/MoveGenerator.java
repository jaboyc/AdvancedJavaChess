package com.jlogical.speedchess.moves;

import com.jlogical.speedchess.bitboard.Bitboard;
import com.jlogical.speedchess.board.Board;

import java.util.LinkedList;
import java.util.List;

import static com.jlogical.speedchess.bitboard.Direction.*;

/**
 * Generates all possible moves from a given board.
 */
public class MoveGenerator {

    /**
     * @param board     the board to generate moves from.
     * @param player    the player to generate moves from.
     * @param legalOnly whether the moves should only be legal moves (will not result in check).
     * @return the list of all possible moves from the given board state.
     */
    public static List<Move> generateMoves(Board board, boolean player, boolean legalOnly) {
        List<Move> moves = new LinkedList<>();

        Bitboard pieces = board.getPieces(player); // Board of the current player's pieces.
        Bitboard enemyPieces = board.getPieces(!player); // Board of the enemy player's pieces.
        Bitboard empty = board.getEmptyTiles(); // Board of empty tiles.

        // Add all the piece's possible moves.
        addPawnMoves(moves, board, pieces, enemyPieces, empty, player, legalOnly);
        addRookMoves(moves, board, pieces, enemyPieces, empty, player, legalOnly);
        addKnightMoves(moves, board, pieces, enemyPieces, empty, player, legalOnly);
        addBishopMoves(moves, board, pieces, enemyPieces, empty, player, legalOnly);
        addQueenMoves(moves, board, pieces, enemyPieces, empty, player, legalOnly);
        addKingMoves(moves, board, pieces, enemyPieces, empty, player, legalOnly);

        return moves;
    }

    /**
     * Adds a move to the list of moves. If [legalOnly], then checks to make sure the move is legal before adding it.
     */
    private static void addMove(Board board, boolean player, List<Move> moves, boolean legalOnly, Move move) {
        if (legalOnly) {
            board.makeMove(move);
            if (!board.inCheck(player)) {
                moves.add(move);
            }
            board.unmakeMove();
        } else {
            moves.add(move);
        }
    }

    /**
     * Adds the pawn moves to the given list of moves.
     */
    private static void addPawnMoves(List<Move> moves, Board board, Bitboard pieces, Bitboard enemyPieces, Bitboard empty, boolean player, boolean legalOnly) {
        Bitboard pawns = board.getPawns(player); // Get the bitboard of all the pawns.
        for (int i = 0; i < 64; i++) {
            if (pawns.get(i)) {
                // Since white and black have different directions for pawns, have separate cases for each.
                if (player) {
                    if (empty.get(i + NORTH)) {
                        addMove(board, true, moves, legalOnly, new Move(pawns, i, i + NORTH)); // Move forward one spot if empty.
                        if (i >= 8 && i < 16 && empty.get(i + NORTH + NORTH))
                            addMove(board, true, moves, legalOnly, new Move(pawns, i, i + NORTH + NORTH)); // Move forward two spots if on second row and empty
                    }
                    if ((i + NORTH_WEST + 7) % 8 != 0 && enemyPieces.get(i + NORTH_WEST)) // Attack to the left if an enemy exists there.
                        addMove(board, true, moves, legalOnly, new Move(pawns, i, i + NORTH_WEST, board.getPiece(i + NORTH_WEST)));

                    if ((i + NORTH_EAST) % 8 != 0 && enemyPieces.get(i + NORTH_EAST)) // Attack to the right if an enemy exists there.
                        addMove(board, true, moves, legalOnly, new Move(pawns, i, i + NORTH_EAST, board.getPiece(i + NORTH_EAST)));
                } else {
                    if (empty.get(i + SOUTH)) {
                        addMove(board, false, moves, legalOnly, new Move(pawns, i, i + SOUTH)); // Move forward one spot if empty.
                        if (i >= 48 && i < 56 && empty.get(i + SOUTH + SOUTH))
                            addMove(board, false, moves, legalOnly, new Move(pawns, i, i + SOUTH + SOUTH)); // Move forward two spots if on second row and empty
                    }
                    if ((i + SOUTH_WEST) % 8 != 0 && enemyPieces.get(i + SOUTH_EAST)) // Attack to the left if an enemy exists there.
                        addMove(board, false, moves, legalOnly, new Move(pawns, i, i + SOUTH_EAST, board.getPiece(i + SOUTH_EAST)));

                    if ((i + SOUTH_EAST + 7) % 8 != 0 && enemyPieces.get(i + SOUTH_WEST)) // Attack to the right if an enemy exists there.
                        addMove(board, false, moves, legalOnly, new Move(pawns, i, i + SOUTH_WEST, board.getPiece(i + SOUTH_WEST)));
                }
            }
        }
    }

    /**
     * Adds the rook moves to the given list of moves.
     */
    private static void addRookMoves(List<Move> moves, Board board, Bitboard pieces, Bitboard enemyPieces, Bitboard empty, boolean player, boolean legalOnly) {
        Bitboard rooks = board.getRooks(player); // Get the bitboard of all the rooks.
        for (int i = 0; i < 64; i++) {
            if (rooks.get(i)) {
                // NORTH
                for (int j = i + NORTH; j < 63; j += NORTH) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(rooks, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(rooks, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // SOUTH
                for (int j = i + SOUTH; j >= 0; j += SOUTH) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(rooks, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(rooks, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // EAST
                for (int j = i + EAST; j % 8 != 0; j += EAST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(rooks, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(rooks, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // WEST
                for (int j = i + WEST; (j + 8) % 8 != 7; j += WEST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(rooks, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(rooks, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }
            }
        }
    }

    /**
     * Adds the knight moves to the given list of moves.
     */
    private static void addKnightMoves(List<Move> moves, Board board, Bitboard pieces, Bitboard enemyPieces, Bitboard empty, boolean player, boolean legalOnly) {
        Bitboard knights = board.getKnights(player); // Get the bitboard of all the knights.
        Bitboard notMine = pieces.not(); // Stores all valid locations for the knight to go.
        for (int i = 0; i < 64; i++) {
            if (knights.get(i)) {
                int j; // Stores the destination pos.

                /*
                 * Check all knight possible moves.
                 */
                j = i + NORTH + NORTH + EAST;
                if (i < 48 && i % 8 < 7 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + NORTH + EAST + EAST;
                if (i < 56 && i % 8 < 6 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + SOUTH + EAST + EAST;
                if (i > 7 && i % 8 < 6 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + SOUTH + SOUTH + EAST;
                if (i > 15 && i % 8 < 7 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + SOUTH + SOUTH + WEST;
                if (i > 15 && i % 8 > 0 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + SOUTH + WEST + WEST;
                if (i > 7 && i % 8 > 1 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + NORTH + WEST + WEST;
                if (i < 56 && i % 8 > 1 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));

                j = i + NORTH + NORTH + WEST;
                if (i < 48 && i % 8 > 0 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(knights, i, j, board.getPiece(j)));
            }
        }
    }

    /**
     * Adds the bishop moves to the given list of moves.
     */
    private static void addBishopMoves(List<Move> moves, Board board, Bitboard pieces, Bitboard enemyPieces, Bitboard empty, boolean player, boolean legalOnly) {
        Bitboard bishops = board.getBishops(player); // Get the bitboard of all the bishops.
        for (int i = 0; i < 64; i++) {
            if (bishops.get(i)) {

                // NORTH EAST
                for (int j = i + NORTH_EAST; j < 64 && j % 8 != 0; j += NORTH_EAST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(bishops, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(bishops, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // SOUTH EAST
                for (int j = i + SOUTH_EAST; j >= 0 && j % 8 != 0; j += SOUTH_EAST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(bishops, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(bishops, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // SOUTH WEST
                for (int j = i + SOUTH_WEST; j >= 0 && (j + 8) % 8 != 7; j += SOUTH_WEST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(bishops, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(bishops, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // NORTH WEST
                for (int j = i + NORTH_WEST; j < 64 && j % 8 != 7; j += NORTH_WEST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(bishops, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(bishops, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }
            }
        }
    }

    /**
     * Adds the queen moves to the given list of moves.
     */
    private static void addQueenMoves(List<Move> moves, Board board, Bitboard pieces, Bitboard enemyPieces, Bitboard empty, boolean player, boolean legalOnly) {
        Bitboard queens = board.getQueen(player); // Get the bitboard of all the queen.
        for (int i = 0; i < 64; i++) {
            if (queens.get(i)) {

                // NORTH
                for (int j = i + NORTH; j < 63; j += NORTH) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // SOUTH
                for (int j = i + SOUTH; j >= 0; j += SOUTH) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // EAST
                for (int j = i + EAST; j % 8 != 0; j += EAST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // WEST
                for (int j = i + WEST; (j + 8) % 8 != 7; j += WEST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // NORTH EAST
                for (int j = i + NORTH_EAST; j < 64 && j % 8 != 0; j += NORTH_EAST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // SOUTH EAST
                for (int j = i + SOUTH_EAST; j >= 0 && j % 8 != 0; j += SOUTH_EAST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // SOUTH WEST
                for (int j = i + SOUTH_WEST; j >= 0 && (j + 8) % 8 != 7; j += SOUTH_WEST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }

                // NORTH WEST
                for (int j = i + NORTH_WEST; j < 64 && j % 8 != 7; j += NORTH_WEST) {
                    if (empty.get(j)) addMove(board, player, moves, legalOnly, new Move(queens, i, j));
                    else if (enemyPieces.get(j)) {
                        addMove(board, player, moves, legalOnly, new Move(queens, i, j, board.getPiece(j)));
                        break;
                    } else break;
                }
            }
        }
    }

    /**
     * Adds the king moves to the given list of moves.
     */
    private static void addKingMoves(List<Move> moves, Board board, Bitboard pieces, Bitboard enemyPieces, Bitboard empty, boolean player, boolean legalOnly) {
        Bitboard king = board.getKing(player); // Get the bitboard of the king.
        Bitboard notMine = pieces.not(); // Stores all valid locations for the king to go.
        for (int i = 0; i < 64; i++) {
            if (king.get(i)) {
                int j; // Stores the destination pos.

                /*
                 * Check all the king's possible moves.
                 */
                j = i + NORTH;
                if (i < 56 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + NORTH_EAST;
                if (i < 56 && i % 8 != 0 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + EAST;
                if (i % 8 < 7 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + SOUTH_EAST;
                if (i > 7 && i % 8 != 0 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + SOUTH;
                if (i > 7 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + SOUTH_WEST;
                if (i > 7 && i % 8 != 7 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + WEST;
                if (i % 8 != 0 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                j = i + NORTH_WEST;
                if (i < 56 && i % 8 != 7 && notMine.get(j))
                    addMove(board, player, moves, legalOnly, new Move(king, i, j, board.getPiece(j)));

                return;
            }
        }
    }
}
