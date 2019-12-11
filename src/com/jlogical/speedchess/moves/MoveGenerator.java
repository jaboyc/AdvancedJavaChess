package com.jlogical.speedchess.moves;

import com.jlogical.speedchess.bitboard.Bitboard;
import com.jlogical.speedchess.board.Board;

import static com.jlogical.speedchess.bitboard.Direction.*;
import static com.jlogical.speedchess.board.Piece.QUEEN;

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
    public static Moveset generateMoves(Board board, boolean player, boolean legalOnly) {

        Moveset moves = new Moveset();

        long pieces = board.getPieces(player); // Board of the current player's pieces.
        long enemyPieces = board.getPieces(!player); // Board of the enemy player's pieces.
        long empty = board.getEmptyTiles(); // Board of empty tiles.

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
    private static void addMove(Board board, boolean player, Moveset moves, boolean legalOnly, Move move) {
        if (move.isDefending())
            moves.addDefence(move);
        else {
            if (legalOnly) {
                board.makeMove(move, player);
                if (!board.inCheck(player)) {
                    moves.addMove(move);
                }
                board.unmakeMove(player);
            } else {
                moves.addMove(move);
            }
        }
    }

    /**
     * Adds the pawn moves to the given list of moves.
     */
    private static void addPawnMoves(Moveset moves, Board board, long pieces, long enemyPieces, long empty, boolean player, boolean legalOnly) {
        long pawns = board.pawns[player ? 0 : 1]; // Get the long of all the pawns.

        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(pawns, i)) {
                // Since white and black have different directions for pawns, have separate cases for each.
                if (player) {
                    int promotionPiece = i >= 48 ? QUEEN : 0; // Promote to queen if possible.
                    if (Bitboard.get(empty, i + NORTH)) {
                        addMove(board, true, moves, legalOnly, new Move(i, i + NORTH).setPromotionPiece(promotionPiece)); // Move forward one spot if empty.
                        if (i >= 8 && i < 16 && Bitboard.get(empty, i + NORTH + NORTH))
                            addMove(board, true, moves, legalOnly, new Move(i, i + NORTH + NORTH)); // Move forward two spots if on second row and empty
                    }
                    if ((i + NORTH_WEST) % 8 != 7 && (Bitboard.get(enemyPieces, i + NORTH_WEST) || Bitboard.get(pieces, i + NORTH_WEST))) // Attack/Defend to the left if an enemy exists there.
                        addMove(board, true, moves, legalOnly, new Move(i, i + NORTH_WEST, board.getPiece(i + NORTH_WEST)).setPromotionPiece(promotionPiece).setDefending(Bitboard.get(pieces, i + NORTH_WEST)));

                    if ((i + NORTH_EAST) % 8 != 0 && (Bitboard.get(enemyPieces, i + NORTH_EAST) || Bitboard.get(pieces, i + NORTH_EAST))) // Attack/Defend to the right if an enemy exists there.
                        addMove(board, true, moves, legalOnly, new Move(i, i + NORTH_EAST, board.getPiece(i + NORTH_EAST)).setPromotionPiece(promotionPiece).setDefending(Bitboard.get(pieces, i + NORTH_EAST)));
                } else {
                    int promotionPiece = i < 16 ? -QUEEN : 0; // Promote to queen if possible.
                    if (Bitboard.get(empty, i + SOUTH)) {
                        addMove(board, false, moves, legalOnly, new Move(i, i + SOUTH).setPromotionPiece(promotionPiece)); // Move forward one spot if empty.
                        if (i >= 48 && i < 56 && Bitboard.get(empty, i + SOUTH + SOUTH))
                            addMove(board, false, moves, legalOnly, new Move(i, i + SOUTH + SOUTH)); // Move forward two spots if on second row and empty
                    }
                    if ((i + SOUTH_EAST + 8) % 8 != 0 && (Bitboard.get(enemyPieces, i + SOUTH_EAST) || Bitboard.get(pieces, i + SOUTH_EAST))) // Attack/Defend to the left if an enemy exists there.
                        addMove(board, false, moves, legalOnly, new Move(i, i + SOUTH_EAST, board.getPiece(i + SOUTH_EAST)).setPromotionPiece(promotionPiece).setDefending(Bitboard.get(pieces, i + SOUTH_EAST)));

                    if ((i + SOUTH_WEST + 8) % 8 != 7 && (Bitboard.get(enemyPieces, i + SOUTH_WEST) || Bitboard.get(pieces, i + SOUTH_WEST))) // Attack/Defend to the right if an enemy exists there.
                        addMove(board, false, moves, legalOnly, new Move(i, i + SOUTH_WEST, board.getPiece(i + SOUTH_WEST)).setPromotionPiece(promotionPiece).setDefending(Bitboard.get(pieces, i + SOUTH_WEST)));
                }
            }
        }
    }

    /**
     * Adds the rook moves to the given list of moves.
     */
    private static void addRookMoves(Moveset moves, Board board, long pieces, long enemyPieces, long empty, boolean player, boolean legalOnly) {
        long rooks = board.rooks[player ? 0 : 1]; // Get the long of all the rooks.
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(rooks, i)) {

                // Whether moving the rook will disabling any of the castle moves.
                boolean willDisableCastleRight;
                boolean willDisableCastleLeft;

                // Moving the rook will disable castling.
                if (player) {
                    willDisableCastleRight = board.canCastleRight(true) && i == 7;
                    willDisableCastleLeft = board.canCastleLeft(true) && i == 0;
                } else {
                    willDisableCastleRight = board.canCastleRight(false) && i == 63;
                    willDisableCastleLeft = board.canCastleLeft(false) && i == 56;
                }

                // NORTH
                for (int j = i + NORTH; j < 64; j += NORTH) {
                    if (Bitboard.get(empty, j))
                        addMove(board, player, moves, legalOnly, new Move(i, j).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                        break;
                    }

                }

                // SOUTH
                for (int j = i + SOUTH; j >= 0; j += SOUTH) {
                    if (Bitboard.get(empty, j))
                        addMove(board, player, moves, legalOnly, new Move(i, j).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                        break;
                    }
                }

                // EAST
                for (int j = i + EAST; j % 8 != 0; j += EAST) {
                    if (Bitboard.get(empty, j))
                        addMove(board, player, moves, legalOnly, new Move(i, j).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                        break;
                    }
                }

                // WEST
                for (int j = i + WEST; (j + 8) % 8 != 7; j += WEST) {
                    if (Bitboard.get(empty, j))
                        addMove(board, player, moves, legalOnly, new Move(i, j).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableRightCastle(willDisableCastleRight).setDisableLeftCastle(willDisableCastleLeft));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds the knight moves to the given list of moves.
     */
    private static void addKnightMoves(Moveset moves, Board board, long pieces, long enemyPieces, long empty, boolean player, boolean legalOnly) {
        long knights = board.knights[player ? 0 : 1]; // Get the long of all the knights.
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(knights, i)) {
                int j; // Stores the destination pos.

                /*
                 * Check all knight possible moves.
                 */
                j = i + NORTH + NORTH + EAST;
                if (i < 48 && i % 8 < 7)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + NORTH + EAST + EAST;
                if (i < 56 && i % 8 < 6)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + SOUTH + EAST + EAST;
                if (i > 7 && i % 8 < 6)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + SOUTH + SOUTH + EAST;
                if (i > 15 && i % 8 < 7)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + SOUTH + SOUTH + WEST;
                if (i > 15 && i % 8 > 0)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + SOUTH + WEST + WEST;
                if (i > 7 && i % 8 > 1)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + NORTH + WEST + WEST;
                if (i < 56 && i % 8 > 1)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));

                j = i + NORTH + NORTH + WEST;
                if (i < 48 && i % 8 > 0)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
            }
        }
    }

    /**
     * Adds the bishop moves to the given list of moves.
     */
    private static void addBishopMoves(Moveset moves, Board board, long pieces, long enemyPieces, long empty, boolean player, boolean legalOnly) {
        long bishops = board.bishops[player ? 0 : 1]; // Get the long of all the bishops.
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(bishops, i)) {

                // NORTH EAST
                for (int j = i + NORTH_EAST; j < 64 && j % 8 != 0; j += NORTH_EAST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // SOUTH EAST
                for (int j = i + SOUTH_EAST; j >= 0 && (j + 8) % 8 != 0; j += SOUTH_EAST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // SOUTH WEST
                for (int j = i + SOUTH_WEST; j >= 0 && (j + 8) % 8 != 7; j += SOUTH_WEST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // NORTH WEST
                for (int j = i + NORTH_WEST; j < 64 && j % 8 != 7; j += NORTH_WEST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds the queen moves to the given list of moves.
     */
    private static void addQueenMoves(Moveset moves, Board board, long pieces, long enemyPieces, long empty, boolean player, boolean legalOnly) {
        long queens = board.queens[player ? 0 : 1]; // Get the long of all the queen.
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(queens, i)) {

                // NORTH
                for (int j = i + NORTH; j < 64; j += NORTH) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // SOUTH
                for (int j = i + SOUTH; j >= 0; j += SOUTH) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // EAST
                for (int j = i + EAST; j % 8 != 0; j += EAST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // WEST
                for (int j = i + WEST; (j + 8) % 8 != 7; j += WEST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // NORTH EAST
                for (int j = i + NORTH_EAST; j < 64 && j % 8 != 0; j += NORTH_EAST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // SOUTH EAST
                for (int j = i + SOUTH_EAST; j >= 0 && (j + 8) % 8 != 0; j += SOUTH_EAST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // SOUTH WEST
                for (int j = i + SOUTH_WEST; j >= 0 && (j + 8) % 8 != 7; j += SOUTH_WEST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }

                // NORTH WEST
                for (int j = i + NORTH_WEST; j < 64 && j % 8 != 7; j += NORTH_WEST) {
                    if (Bitboard.get(empty, j)) addMove(board, player, moves, legalOnly, new Move(i, j));
                    else {
                        addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds the king moves to the given list of moves.
     */
    private static void addKingMoves(Moveset moves, Board board, long pieces, long enemyPieces, long empty, boolean player, boolean legalOnly) {
        long king = board.kings[player ? 0 : 1]; // Get the long of the king.
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(king, i)) {

                // Whether moving the king to a position around it would disable any of the board's castling.
                boolean willDisableCastlingRight = board.canCastleRight(player);
                boolean willDisableCastlingLeft = board.canCastleLeft(player);

                int j; // Stores the destination pos.

                /*
                 * Check all the king's possible moves.
                 */
                j = i + NORTH;
                if (i < 56)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + NORTH_EAST;
                if (i < 56 && i % 8 < 7)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + EAST;
                if (i % 8 < 7)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + SOUTH_EAST;
                if (i > 7 && i % 8 < 7)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + SOUTH;
                if (i > 7)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + SOUTH_WEST;
                if (i > 7 && i % 8 != 0)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + WEST;
                if (i % 8 != 0)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                j = i + NORTH_WEST;
                if (i < 56 && i % 8 != 0)
                    addMove(board, player, moves, legalOnly, new Move(i, j, board.getPiece(j)).setDefending(Bitboard.get(pieces, j)).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));

                /*
                 * Castling.
                 */
                if (board.canCastleRight(player) && (!legalOnly || !board.inCheck(player))) {
                    if (player && Bitboard.get(empty,5) && Bitboard.get(empty,6)) {
                        addMove(board, true, moves, legalOnly, new Move(i, 6).setRightCastle(true).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));
                    } else if (!player && Bitboard.get(empty,61) && Bitboard.get(empty,62)) {
                        addMove(board, false, moves, legalOnly, new Move(i, 62).setRightCastle(true).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));
                    }
                }
                if (board.canCastleLeft(player) && (!legalOnly || !board.inCheck(player))) {
                    if (player && Bitboard.get(empty,3) && Bitboard.get(empty,2) && Bitboard.get(empty,1)) {
                        addMove(board, true, moves, legalOnly, new Move(i, 2).setLeftCastle(true).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));
                    } else if (!player && Bitboard.get(empty,59) && Bitboard.get(empty,58) && Bitboard.get(empty,57)) {
                        addMove(board, false, moves, legalOnly, new Move(i, 58).setLeftCastle(true).setDisableLeftCastle(willDisableCastlingLeft).setDisableRightCastle(willDisableCastlingRight));
                    }
                }

                return;
            }
        }
    }
}
