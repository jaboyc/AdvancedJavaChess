package com.jlogical.speedchess.board;

/**
 * Contains helper fields that represent/help pieces.
 */
public class Piece {

    /**
     * These are constants that represent pieces on the board. Negative numbers mean black pieces.
     */
    public static final int PAWN = 1;
    public static final int ROOK = 2;
    public static final int KNIGHT = 3;
    public static final int BISHOP = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    /**
     * Returns the text of the given piece. If white, its initial. If black, its initial surrounded by parentheses.
     *
     * @param piece the piece to get the text of.
     * @return the text.
     */
    public static String pieceText(int piece) {
        switch (piece) {
            case 0:
                return "   ";
            case PAWN:
                return " P ";
            case -PAWN:
                return "(P)";
            case ROOK:
                return " R ";
            case -ROOK:
                return "(R)";
            case KNIGHT:
                return " N ";
            case -KNIGHT:
                return "(N)";
            case BISHOP:
                return " B ";
            case -BISHOP:
                return "(B)";
            case QUEEN:
                return " Q ";
            case -QUEEN:
                return "(Q)";
            case KING:
                return " K ";
            case -KING:
                return "(K)";
        }
        return "???";
    }
}
