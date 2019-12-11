package com.jlogical.speedchess.board;

import com.jlogical.speedchess.bitboard.Bitboard;
import com.jlogical.speedchess.cpu.Evaluator;
import com.jlogical.speedchess.moves.Move;
import com.jlogical.speedchess.moves.MoveGenerator;

import java.util.List;
import java.util.Stack;

import static com.jlogical.speedchess.board.Piece.*;

/**
 * Represents the internal state of a chess board.
 */
public class Board {

    /**
     * The following Bitboards arrays have 2 elements. board[0] is white, board[1] is black.
     * These bitboards contain the positions of all the types of pieces for each player.
     */
    public long[] pawns;
    public long[] rooks;
    public long[] knights;
    public long[] bishops;
    public long[] queens;
    public long[] kings;

    /**
     * The following boolean arrays have 2 elements. [0] is white, [1] is black.
     * These contain whether each player can castle right or left.
     */
    public boolean[] canCastleRight;
    public boolean[] canCastleLeft;

    private Stack<Move> moveHistory; // History of previous moves.

    private boolean currPlayer; // The current player.

    /**
     * Creates a new board. Generates all bitboards as well.
     *
     * @param fen the FEN code to use.
     */
    public Board(String fen) {
        pawns = new long[]{0L, 0L};
        rooks = new long[]{0L, 0L};
        knights = new long[]{0L, 0L};
        bishops = new long[]{0L, 0L};
        queens = new long[]{0L, 0L};
        kings = new long[]{0L, 0L};

        canCastleRight = new boolean[]{true, true};
        canCastleLeft = new boolean[]{true, true};

        moveHistory = new Stack<>();
        moveHistory.push(new Move(0, -1, -1)); // Add one move to the move history to allow next-move searching.

        currPlayer = true;

        initPieceBitboards(fen);
    }

    /**
     * @param player the player whose number to get.
     * @return the index the player is in each bitboard.
     */
    public static int playerBitboardNum(boolean player) {
        return player ? 0 : 1;
    }

    /**
     * @param player whether the player is white.
     * @return the intersection of all the pieces of the given player.
     */
    public long getPieces(boolean player) {
        int playerNum = playerBitboardNum(player);
        return pawns[playerNum] | rooks[playerNum] | knights[playerNum] | bishops[playerNum] | queens[playerNum] | kings[playerNum];
    }

    /**
     * Returns the Bitboard associated with the current piece.
     *
     * @param piece the piece whose Bitboard to get.
     * @return the Bitboard.
     */
    public long getPieceBitboard(int piece) {
        switch (piece) {
            case PAWN:
                return pawns[0];
            case -PAWN:
                return pawns[1];
            case ROOK:
                return rooks[0];
            case -ROOK:
                return rooks[1];
            case KNIGHT:
                return knights[0];
            case -KNIGHT:
                return knights[1];
            case BISHOP:
                return bishops[0];
            case -BISHOP:
                return bishops[1];
            case QUEEN:
                return queens[0];
            case -QUEEN:
                return queens[1];
            case KING:
                return kings[0];
            case -KING:
                return kings[1];
        }
        return 0;
    }

    /**
     * Sets the bitboard of the given pieceType to the given value.
     *
     * @param pieceType the pieceType whose bitboard to get.
     * @param value     the value to set it to.
     */
    public void setBitboard(int pieceType, long value) {
        switch (pieceType) {
            case PAWN:
                pawns[0] = value;
                break;
            case -PAWN:
                pawns[1] = value;
                break;
            case ROOK:
                rooks[0] = value;
                break;
            case -ROOK:
                rooks[1] = value;
                break;
            case KNIGHT:
                knights[0] = value;
                break;
            case -KNIGHT:
                knights[1] = value;
                break;
            case BISHOP:
                bishops[0] = value;
                break;
            case -BISHOP:
                bishops[1] = value;
                break;
            case QUEEN:
                queens[0] = value;
                break;
            case -QUEEN:
                queens[1] = value;
                break;
            case KING:
                kings[0] = value;
                break;
            case -KING:
                kings[1] = value;
                break;
        }
    }

    /**
     * @param player the player to look at.
     * @return whether the given player can castle right.
     */
    public boolean canCastleRight(boolean player) {
        return canCastleRight[player ? 0 : 1];
    }

    /**
     * @param player the player to look at.
     * @return whether the given player can castle left.
     */
    public boolean canCastleLeft(boolean player) {
        return canCastleLeft[player ? 0 : 1];
    }

    /**
     * @return a bitboard of all the empty tiles.
     */
    public long getEmptyTiles() {
        return ~(getPieces(true) | getPieces(false));
    }

    /**
     * Returns the piece at the given position.
     *
     * @param pos the position to look at (0-63).
     * @return the piece. 0 if none present.
     */
    public int getPiece(int pos) {
        if (Bitboard.get(pawns[0], pos)) return PAWN;
        if (Bitboard.get(pawns[1], pos)) return -PAWN;
        if (Bitboard.get(rooks[0], pos)) return ROOK;
        if (Bitboard.get(rooks[1], pos)) return -ROOK;
        if (Bitboard.get(knights[0], pos)) return KNIGHT;
        if (Bitboard.get(knights[1], pos)) return -KNIGHT;
        if (Bitboard.get(bishops[0], pos)) return BISHOP;
        if (Bitboard.get(bishops[1], pos)) return -BISHOP;
        if (Bitboard.get(queens[0], pos)) return QUEEN;
        if (Bitboard.get(queens[1], pos)) return -QUEEN;
        if (Bitboard.get(kings[0], pos)) return KING;
        if (Bitboard.get(kings[1], pos)) return -KING;
        return 0;
    }

    /**
     * Returns the piece at the given x & y location.
     *
     * @param x the x position. (0-7)
     * @param y the y position. (0-7)
     * @return
     */
    public int getPiece(int x, int y) {
        return getPiece(y * 8 + x);
    }


    /**
     * Performs the given move.
     *
     * @param move   the move to perform.
     * @param player the player performing the move.
     */
    public void makeMove(Move move, boolean player) {

        moveHistory.push(move); // Add the move to the board's moveHistory.

        // Move the piece in its piece board.
        int pieceType = move.getPieceType();
        setBitboard(pieceType, Bitboard.clear(getPieceBitboard(pieceType), move.getFrom()));
        setBitboard(pieceType, Bitboard.set(getPieceBitboard(pieceType), move.getTo()));

        // Handle piece capturing.
        if (move.getCapturedPiece() != 0) {
            setBitboard(move.getCapturedPiece(), Bitboard.clear(getPieceBitboard(move.getCapturedPiece()), move.getTo()));
        }

        // Handle castling.
        if (move.isRightCastle()) {

            // Move the rook to the correct location.
            if (player) {
                rooks[0] = Bitboard.clear(rooks[0], 7);
                rooks[0] = Bitboard.set(rooks[0], 5);
            } else {
                rooks[1] = Bitboard.clear(rooks[1], 63);
                rooks[1] = Bitboard.set(rooks[1], 61);
            }

        } else if (move.isLeftCastle()) {

            // Move the rook to the correct location.
            if (player) {
                rooks[0] = Bitboard.clear(rooks[0], 0);
                rooks[0] = Bitboard.set(rooks[0], 3);
            } else {
                rooks[1] = Bitboard.clear(rooks[1], 56);
                rooks[1] = Bitboard.set(rooks[1], 59);
            }
        }

        // Handle disabling castling.
        if (move.isDisableLeftCastle()) {
            canCastleLeft[player ? 0 : 1] = false;
        }
        if (move.isDisableRightCastle()) {
            canCastleRight[player ? 0 : 1] = false;
        }

        // Handle pawn promotion.
        if (move.getPromotionPiece() != 0) {

            int playerNum = playerBitboardNum(player);

            // Remove the piece and replace it with the given promoted piece.
            pawns[playerNum] = Bitboard.clear(pawns[playerNum], move.getTo());
            setBitboard(move.getPromotionPiece(), Bitboard.set(getPieceBitboard(move.getPromotionPiece()), move.getTo()));
        }

    }

    /**
     * Undoes the last move made.
     *
     * @param player the player unmaking the move
     */
    public void unmakeMove(boolean player) {

        Move move = moveHistory.pop(); // Get the most-recently made move.
        move.clearCache();

        // Replace the captured piece.
        if (move.getCapturedPiece() != 0) {
            setBitboard(move.getCapturedPiece(), Bitboard.set(getPieceBitboard(move.getCapturedPiece()), move.getTo()));
        }

        // Unmove the piece in its piece board.
        int pieceType = move.getPieceType();
        setBitboard(pieceType, Bitboard.clear(getPieceBitboard(pieceType), move.getTo()));
        setBitboard(pieceType, Bitboard.set(getPieceBitboard(pieceType), move.getFrom()));

        // Handle castling.
        if (move.isRightCastle()) {

            // Move the rook to the correct location.
            if (player) {
                rooks[0] = Bitboard.set(rooks[0], 7);
                rooks[0] = Bitboard.clear(rooks[0], 5);
            } else {
                rooks[1] = Bitboard.set(rooks[1], 63);
                rooks[1] = Bitboard.clear(rooks[1], 61);
            }

        } else if (move.isLeftCastle()) {

            // Move the rook to the correct location.
            if (player) {
                rooks[0] = Bitboard.set(rooks[0], 0);
                rooks[0] = Bitboard.clear(rooks[0], 3);
            } else {
                rooks[1] = Bitboard.set(rooks[1], 56);
                rooks[1] = Bitboard.clear(rooks[1], 59);
            }
        }

        // Handle disabling castling. Re-enable them.
        if (move.isDisableRightCastle()) canCastleRight[player ? 0 : 1] = true;
        if (move.isDisableLeftCastle()) canCastleLeft[player ? 0 : 1] = true;

        // Handle pawn promotion.
        if (move.getPromotionPiece() != 0) {

            // Remove the promoted piece.
            setBitboard(move.getPromotionPiece(), Bitboard.clear(getPieceBitboard(move.getPromotionPiece()), move.getTo()));
        }
    }

    /**
     * @param player the player to look at.
     * @return whether the given player is in check.
     */
    public boolean inCheck(boolean player) {
        if (moveHistory.isEmpty()) return false;
        List<Move> enemyMoves = moveHistory.peek().getNextMoves(this, !player).getMoves();

        // Check if any of the enemy's move hit the king.
        for (Move move : enemyMoves) {
            if (move.getCapturedPiece() == KING * (player ? 1 : -1))
                return true;
        }

        return false;
    }

    /**
     * @param player the player to look at.
     * @return whether the given player is in check mate.
     */
    public boolean isCheckMate(boolean player) {
        if (moveHistory.isEmpty()) return false;
        return inCheck(player) && moveHistory.peek().getNextLegalMoves(this, player).isEmpty();
    }

    /**
     * @param player the player to look at.
     * @return whether the given player is in stale mate.
     */
    public boolean isStaleMate(boolean player) {
        if (moveHistory.isEmpty()) return false;
        return !inCheck(player) && moveHistory.peek().getNextLegalMoves(this, player).isEmpty();
    }

    /**
     * Initializes the bitboards of all the pieces.
     *
     * @param fen the FEN code to initialize all the pieces to. If null, sets to the default board.
     */
    private void initPieceBitboards(String fen) {

        if (fen == null) fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        // Split the FEN to get the separate messages.
        String[] split = fen.split(" ");

        String pieces = split[0];
        String[] ranks = pieces.split("/");

        currPlayer = split[1].equals("w");

        // Place the pieces.
        int pos = 0;
        for (int i = ranks.length - 1; i >= 0; i--) {
            for (int j = 0; j < ranks[i].length(); j++) {
                char c = ranks[i].charAt(j);

                // See if the character is a number. If it is, skip that many positions.
                try {
                    int numSkip = Integer.parseInt("" + c);
                    pos += numSkip;
                    continue;
                } catch (Exception ignored) {
                }

                // Otherwise, place the given piece down.
                switch (c) {
                    case 'P':
                        pawns[0] = Bitboard.set(pawns[0], pos);
                        break;
                    case 'p':
                        pawns[1] = Bitboard.set(pawns[1], pos);
                        break;
                    case 'R':
                        rooks[0] = Bitboard.set(rooks[0], pos);
                        break;
                    case 'r':
                        rooks[1] = Bitboard.set(rooks[1], pos);
                        break;
                    case 'N':
                        knights[0] = Bitboard.set(knights[0], pos);
                        break;
                    case 'n':
                        knights[1] = Bitboard.set(knights[1], pos);
                        break;
                    case 'B':
                        bishops[0] = Bitboard.set(bishops[0], pos);
                        break;
                    case 'b':
                        bishops[1] = Bitboard.set(bishops[1], pos);
                        break;
                    case 'Q':
                        queens[0] = Bitboard.set(queens[0], pos);
                        break;
                    case 'q':
                        queens[1] = Bitboard.set(queens[1], pos);
                        break;
                    case 'K':
                        kings[0] = Bitboard.set(kings[0], pos);
                        break;
                    case 'k':
                        kings[1] = Bitboard.set(kings[1], pos);
                        break;
                }

                pos++;
            }
        }
    }

    public Stack<Move> getMoveHistory() {
        return moveHistory;
    }

    public boolean getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(boolean currPlayer) {
        this.currPlayer = currPlayer;
    }

    /**
     * @return the String representation of the board.
     */
    public String toString() {
        StringBuilder output = new StringBuilder();

        // Add the col coordinates.
        output.append("     A   B   C   D   E   F   G   H\n");

        // Add the board boundary.
        output.append("  ***********************************\n");

        // Go through the rows, starting with the top one first.
        for (int i = 7; i >= 0; i--) {

            // Add the row coordinates.
            output.append(i + 1);

            // Add the chess boundary.
            output.append(" *");

            // Add each tile in the row.
            for (int j = 0; j < 8; j++) {
                output.append("|").append(Piece.pieceText(getPiece(j, i)));
            }

            // Add the chess boundary.
            output.append("|* ");

            // Add the row coordinates.
            output.append(i + 1);

            // Add a new line.
            output.append("\n");
        }

        // Add the board boundary.
        output.append("  ***********************************\n");

        // Add the col coordinates.
        output.append("     A   B   C   D   E   F   G   H\n");

        // Add scores
        output.append("\n").append("       Score: [").append(Evaluator.evaluate(this, true)).append("]\n");
//        output.append("\n").append(MoveGenerator.generateMoves(this, true, true).getMoves());
        return output.toString();
    }
}
