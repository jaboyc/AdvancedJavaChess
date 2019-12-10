package com.jlogical.speedchess.moves;

import com.jlogical.speedchess.bitboard.Bitboard;
import com.jlogical.speedchess.board.Board;

/**
 * Represents a possible move a piece can make.
 */
public class Move {

    /**
     * These are needed for all moves.
     */
    private Bitboard pieceBoard; // The Bitboard that the piece that moved belongs to.
    private int from; // The position the move originated from.
    private int to; // The position the move went to.
    private int capturedPiece; // The piece this move captured. 0 if none. If [defending], then the piece that this is defending.

    /**
     * These are special circumstances.
     */
    private boolean rightCastle; // Whether this move was a castle to the right.
    private boolean leftCastle; // Whether this move was a castle to the left.
    private boolean disableRightCastle; // Whether this move disabled castling right.
    private boolean disableLeftCastle; // Whether this move disabled castling left.
    private int promotionPiece; // The piece the pawn was promoted to. 0 if none.
    private boolean defending; // Whether the move is simply one that defends another piece.

    private Moveset[] nextMoves; // List of moves that are possible after this move. Essentially, a cache.
    private Moveset[] nextLegalMoves; // List of legal moves that are posssible after this move. Essentially, a cache.

    /**
     * Creates a move that goes from [from] to [to] while capturing [capturedPiece].
     *
     * @param pieceBoard    the Bitboard that the piece that moved belongs to.
     * @param from          the position the move originated from.
     * @param to            the position the move went to.
     * @param capturedPiece the piece this move captured. 0 if none.
     */
    public Move(Bitboard pieceBoard, int from, int to, int capturedPiece) {
        this.pieceBoard = pieceBoard;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;

        rightCastle = false;
        leftCastle = false;
        disableRightCastle = false;
        disableLeftCastle = false;
        promotionPiece = 0;
        defending = false;
    }

    /**
     * Creates a move that goes from [from] to [to] with no capture.
     *
     * @param pieceBoard the Bitboard that the piece that moved belongs to.
     * @param from       the position the move originated from.
     * @param to         the position the move went to.
     */
    public Move(Bitboard pieceBoard, int from, int to) {
        this(pieceBoard, from, to, 0);
    }

    /**
     * @param move the move to compare with.
     * @return whether the given move is similar to the current one. Similar means they go from the same source tile to the same destination tile.
     */
    public boolean similar(Move move) {
        return from == move.from && to == move.to;
    }

    /**
     * @param board  the board to generate the moves for.
     * @param player the player to generate the moves for.
     * @return the moveset that can occur after this one. If not calculated yet, will do so.
     */
    public Moveset getNextMoves(Board board, boolean player) {
        if (nextMoves == null) {
            nextMoves = new Moveset[2];
        }
        if (nextMoves[player ? 0 : 1] == null)
            nextMoves[player ? 0 : 1] = MoveGenerator.generateMoves(board, player, false);
        return nextMoves[player ? 0 : 1];
    }

    /**
     * @param board  the board to generate the moves for.
     * @param player the player to generate the moves for.
     * @return the legal moveset that can occur after this one. If not calculated yet, will do so.
     */
    public Moveset getNextLegalMoves(Board board, boolean player) {
        if (nextLegalMoves == null) {
            nextLegalMoves = new Moveset[2];
        }
        if (nextLegalMoves[player ? 0 : 1] == null)
            nextLegalMoves[player ? 0 : 1] = MoveGenerator.generateMoves(board, player, true);
        return nextLegalMoves[player ? 0 : 1];
    }

    /**
     * Clears the next moves caches.
     */
    public void clearCache() {
        nextLegalMoves = null;
        nextMoves = null;
    }

    /**
     * @param pos the position (0-63).
     * @return the position name from the given position. Ex: e2, a6, d8, etc...
     */
    public static String posName(int pos) {
        int x = pos % 8;
        int y = pos / 8;

        char file = (char) ('a' + x);
        int rank = y + 1;

        return "" + file + rank;
    }

    public Bitboard getPieceBoard() {
        return pieceBoard;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isRightCastle() {
        return rightCastle;
    }

    public boolean isLeftCastle() {
        return leftCastle;
    }

    public Move setRightCastle(boolean rightCastle) {
        this.rightCastle = rightCastle;
        return this;
    }

    public Move setLeftCastle(boolean leftCastle) {
        this.leftCastle = leftCastle;
        return this;
    }

    public boolean isDisableRightCastle() {
        return disableRightCastle;
    }

    public Move setDisableRightCastle(boolean disableRightCastle) {
        this.disableRightCastle = disableRightCastle;
        return this;
    }

    public boolean isDisableLeftCastle() {
        return disableLeftCastle;
    }

    public Move setDisableLeftCastle(boolean disableLeftCastle) {
        this.disableLeftCastle = disableLeftCastle;
        return this;
    }

    public int getPromotionPiece() {
        return promotionPiece;
    }

    public Move setPromotionPiece(int promotionPiece) {
        this.promotionPiece = promotionPiece;
        return this;
    }

    public boolean isDefending() {
        return defending;
    }

    public Move setDefending(boolean defending) {
        this.defending = defending;
        return this;
    }


    public String toString() {
        return posName(from) + " -> " + posName(to);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move m = (Move) obj;
            return pieceBoard == m.pieceBoard && from == m.from && to == m.to && capturedPiece == m.capturedPiece && disableLeftCastle == m.disableLeftCastle && disableRightCastle == m.disableRightCastle && promotionPiece == m.promotionPiece;
        }
        return false;
    }
}
