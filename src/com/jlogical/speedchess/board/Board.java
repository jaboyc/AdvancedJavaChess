package com.jlogical.speedchess.board;

import com.jlogical.speedchess.bitboard.Bitboard;
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
    private Bitboard[] pawns;
    private Bitboard[] rooks;
    private Bitboard[] knights;
    private Bitboard[] bishops;
    private Bitboard[] queens;
    private Bitboard[] kings;

    /**
     * The following boolean arrays have 2 elements. [0] is white, [1] is black.
     * These contain whether each player can castle right or left.
     */
    private boolean[] canCastleRight;
    private boolean[] canCastleLeft;

    private Stack<Move> history; // History of moves performed on this board.

    /**
     * Creates a new board. Generates all bitboards as well.
     */
    public Board() {
        pawns = new Bitboard[]{new Bitboard(), new Bitboard()};
        rooks = new Bitboard[]{new Bitboard(), new Bitboard()};
        knights = new Bitboard[]{new Bitboard(), new Bitboard()};
        bishops = new Bitboard[]{new Bitboard(), new Bitboard()};
        queens = new Bitboard[]{new Bitboard(), new Bitboard()};
        kings = new Bitboard[]{new Bitboard(), new Bitboard()};

        canCastleRight = new boolean[]{true, true};
        canCastleLeft = new boolean[]{true, true};

        history = new Stack<>();

        initPieceBitboards();
    }

    /**
     * Returns the pawn bitboard of the player.
     *
     * @param player whether the player is white.
     * @return the pawn bitboard that belongs to the player.
     */
    public Bitboard getPawns(boolean player) {
        return getPlayerBitboard(pawns, player);
    }

    /**
     * Returns the rook bitboard of the player.
     *
     * @param player whether the player is white.
     * @return the rook bitboard that belongs to the player.
     */
    public Bitboard getRooks(boolean player) {
        return getPlayerBitboard(rooks, player);
    }

    /**
     * Returns the knight bitboard of the player.
     *
     * @param player whether the player is white.
     * @return the knight bitboard that belongs to the player.
     */
    public Bitboard getKnights(boolean player) {
        return getPlayerBitboard(knights, player);
    }

    /**
     * Returns the bishop bitboard of the player.
     *
     * @param player whether the player is white.
     * @return the bishop bitboard that belongs to the player.
     */
    public Bitboard getBishops(boolean player) {
        return getPlayerBitboard(bishops, player);
    }

    /**
     * Returns the queen bitboard of the player.
     *
     * @param player whether the player is white.
     * @return the queen bitboard that belongs to the player.
     */
    public Bitboard getQueen(boolean player) {
        return getPlayerBitboard(queens, player);
    }

    /**
     * Returns the king bitboard of the player.
     *
     * @param player whether the player is white.
     * @return the king bitboard that belongs to the player.
     */
    public Bitboard getKing(boolean player) {
        return getPlayerBitboard(kings, player);
    }

    /**
     * @param player whether the player is white.
     * @return the intersection of all the pieces of the given player.
     */
    public Bitboard getPieces(boolean player) {
        return Bitboard.or(getPawns(player), getRooks(player), getKnights(player), getBishops(player), getQueen(player), getKing(player));
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
    public Bitboard getEmptyTiles() {
        return Bitboard.or(getPieces(true), getPieces(false)).not();
    }

    /**
     * Returns the piece at the given position.
     *
     * @param pos the position to look at (0-63).
     * @return the piece. 0 if none present.
     */
    public int getPiece(int pos) {
        if (pawns[0].get(pos)) return PAWN;
        if (pawns[1].get(pos)) return -PAWN;
        if (rooks[0].get(pos)) return ROOK;
        if (rooks[1].get(pos)) return -ROOK;
        if (knights[0].get(pos)) return KNIGHT;
        if (knights[1].get(pos)) return -KNIGHT;
        if (bishops[0].get(pos)) return BISHOP;
        if (bishops[1].get(pos)) return -BISHOP;
        if (queens[0].get(pos)) return QUEEN;
        if (queens[1].get(pos)) return -QUEEN;
        if (kings[0].get(pos)) return KING;
        if (kings[1].get(pos)) return -KING;
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
     * Returns the Bitboard associated with the current piece.
     *
     * @param piece the piece whose Bitboard to get.
     * @return the Bitboard.
     */
    public Bitboard getPieceBitboard(int piece) {
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
        return null;
    }

    /**
     * Performs the given move.
     *
     * @param move   the move to perform.
     * @param player the player performing the move.
     */
    public void makeMove(Move move, boolean player) {
        history.push(move); // Add the move to the board's history.

        // Move the piece in its piece board.
        move.getPieceBoard().clear(move.getFrom());
        move.getPieceBoard().set(move.getTo());

        // Handle piece capturing.
        if (move.getCapturedPiece() != 0) {
            Bitboard capturedBitboard = getPieceBitboard(move.getCapturedPiece());
            capturedBitboard.clear(move.getTo());
        }

        // Handle castling.
        if (move.isRightCastle()) {
            Bitboard rookBitboard = getRooks(player); // The bitboard of the rooks.

            // Move the rook to the correct location.
            if (player) {
                rookBitboard.clear(7);
                rookBitboard.set(5);
            } else {
                rookBitboard.clear(63);
                rookBitboard.set(61);
            }

        } else if (move.isLeftCastle()) {
            Bitboard rookBitboard = getRooks(player); // The bitboard of the rooks.

            // Move the rook to the correct location.
            if (player) {
                rookBitboard.clear(0);
                rookBitboard.set(3);
            } else {
                rookBitboard.clear(56);
                rookBitboard.set(59);
            }
        }

        // Handle disabling castling.
        if (move.isDisableLeftCastle()) canCastleLeft[player ? 0 : 1] = false;
        else if (move.isDisableRightCastle()) canCastleRight[player ? 0 : 1] = false;

        // Handle pawn promotion.
        if(move.getPromotionPiece() != 0){

            // Remove the piece and replace it with the given promoted piece.
            move.getPieceBoard().clear(move.getTo());
            getPieceBitboard(move.getPromotionPiece()).set(move.getTo());
        }
    }

    /**
     * Undoes the last move made.
     *
     * @param player the player unmaking the move
     */
    public void unmakeMove(boolean player) {
        Move lastMove = history.pop(); // Get the most-recently made move.

        // Unmove the piece in its piece board.
        lastMove.getPieceBoard().clear(lastMove.getTo());
        lastMove.getPieceBoard().set(lastMove.getFrom());

        // Replace the captured piece.
        if (lastMove.getCapturedPiece() != 0) {
            Bitboard capturedBitboard = getPieceBitboard(lastMove.getCapturedPiece());
            capturedBitboard.set(lastMove.getTo());
        }

        // Handle castling.
        if (lastMove.isRightCastle()) {
            Bitboard rookBitboard = getRooks(player); // The bitboard of the rooks.

            // Move the rook back to its starting location.
            if (player) {
                rookBitboard.clear(5);
                rookBitboard.set(7);
            } else {
                rookBitboard.clear(61);
                rookBitboard.set(63);
            }
        } else if (lastMove.isLeftCastle()) {
            Bitboard rookBitboard = getRooks(player); // The bitboard of the rooks.

            // Move the rook back to its starting location.
            if (player) {
                rookBitboard.clear(3);
                rookBitboard.set(0);
            } else {
                rookBitboard.clear(59);
                rookBitboard.set(56);
            }
        }

        // Handle disabling castling. Re-enable them.
        if (lastMove.isDisableRightCastle()) canCastleRight[player ? 0 : 1] = true;
        if (lastMove.isDisableLeftCastle()) canCastleLeft[player ? 0 : 1] = true;

        // Handle pawn promotion.
        if(lastMove.getPromotionPiece() != 0){

            // Remove the added piece.
            getPieceBitboard(lastMove.getPromotionPiece()).clear(lastMove.getTo());
        }
    }

    /**
     * @param player the player to look at.
     * @return whether the given player is in check.
     */
    public boolean inCheck(boolean player) {
        List<Move> enemyMoves = MoveGenerator.generateMoves(this, !player, false);

        // Find the player's king.
        int i;
        for (i = 0; i < 64; i++)
            if (getKing(player).get(i)) break;

        // Check if any of the enemy's move hit the king.
        for (Move move : enemyMoves) {
            if (move.getTo() == i)
                return true;
        }

        return false;
    }

    /**
     * @param player the player to look at.
     * @return whether the given player is in check mate.
     */
    public boolean isCheckMate(boolean player) {
        return inCheck(player) && MoveGenerator.generateMoves(this, player, true).isEmpty();
    }

    /**
     * @param player the player to look at.
     * @return whether the given player is in stale mate.
     */
    public boolean isStaleMate(boolean player) {
        return !inCheck(player) && MoveGenerator.generateMoves(this, player, true).isEmpty();
    }

    /**
     * Returns the bitboard that belongs to the player.
     *
     * @param bitboards the bitboard array. Use any of the pieces bitboard arrays.
     * @param player    whether the player is white.
     * @return the bitboard that belongs to the player.
     */
    private Bitboard getPlayerBitboard(Bitboard[] bitboards, boolean player) {
        return bitboards[player ? 0 : 1];
    }

    /**
     * Initializes the bitboards of all the pieces.
     */
    private void initPieceBitboards() {
        // WHITE
        // Pawns
        for (int i = 0; i < 8; i++) {
            pawns[0].set(i, 1);
        }

        // Rooks
        rooks[0].set(0, 0);
        rooks[0].set(7, 0);

        // Knights
        knights[0].set(1, 0);
        knights[0].set(6, 0);

        // Bishops
        bishops[0].set(2, 0);
        bishops[0].set(5, 0);

        // Queen
        queens[0].set(3, 0);

        // King
        kings[0].set(4, 0);

        // BLACK
        // Pawns
        for (int i = 0; i < 8; i++) {
            pawns[1].set(i, 6);
        }

        // Rooks
        rooks[1].set(0, 7);
        rooks[1].set(7, 7);

        // Knights
        knights[1].set(1, 7);
        knights[1].set(6, 7);

        // Bishops
        bishops[1].set(2, 7);
        bishops[1].set(5, 7);

        // Queen
        queens[1].set(3, 7);

        // King
        kings[1].set(4, 7);
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

        return output.toString();
    }
}
