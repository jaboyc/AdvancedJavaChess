package com.jlogical.speedchess.moves;

import com.jlogical.speedchess.bitboard.Bitboard;

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
    private int capturedPiece; // The piece this move captured. 0 if none.

    /**
     * These are special circumstances.
     */
    private boolean kingCastle; // Whether the king castled king-side.
    private boolean queenCastle; // Whether the queen castled queen-side.
    private int promotionPiece; // The piece the pawn was promoted to. 0 if none.

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

        kingCastle = false;
        queenCastle = false;
        promotionPiece = 0;
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

    public boolean isKingCastle() {
        return kingCastle;
    }

    public void setKingCastle(boolean kingCastle) {
        this.kingCastle = kingCastle;
    }

    public boolean isQueenCastle() {
        return queenCastle;
    }

    public void setQueenCastle(boolean queenCastle) {
        this.queenCastle = queenCastle;
    }

    public int getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(int promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public String toString() {
        return posName(from) + " -> " + posName(to);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move m = (Move) obj;
            return pieceBoard == m.pieceBoard && from == m.from && to == m.to && capturedPiece == m.capturedPiece && queenCastle == m.queenCastle && kingCastle == m.kingCastle && promotionPiece == m.promotionPiece;
        }
        return false;
    }
}
