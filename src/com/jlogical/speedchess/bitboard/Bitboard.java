package com.jlogical.speedchess.bitboard;

/**
 * Represents an 8x8 grid of 1s or 0s. This class contains helper methods to use them easily.
 */
public class Bitboard {

    private Bitboard() {
    }

    /**
     * Returns the value at the given position
     *
     * @param pos the position to look at (0-255)
     * @return the value at the position.
     */
    public static boolean get(long board, int pos) {
        return ((board >> pos) & 1) == 1;
    }

    /**
     * Returns the value at x and y.
     *
     * @param x the x position (0-7)
     * @param y the y position (0-7)
     * @return the value at the position.
     */
    public static boolean get(long board, int x, int y) {
        return get(board, y * 8 + x);
    }

    /**
     * Sets the value at the given position to the given value.
     *
     * @param pos   the position to set (0-63)
     * @param value the value to set it to.
     */
    public static long set(long board, int pos, boolean value) {
        if (value)
            board |= (1L << pos);
        else
            board &= ~(1L << pos);
        return board;
    }

    /**
     * Sets the given position to 1.
     *
     * @param pos the position to set.
     */
    public static long set(long board, int pos) {
        return set(board, pos, true);
    }

    /**
     * Sets the given x & y position to the given value.
     *
     * @param x     the x position (0-7)
     * @param y     the y position (0-7)
     * @param value 1 or 0
     */
    public static long set(long board, int x, int y, boolean value) {
        return set(board, y * 8 + x, value);
    }

    /**
     * Sets the given x & y position to 1.
     *
     * @param x the x position (0-7)
     * @param y the y position (0-7)
     */
    public static long set(long board, int x, int y) {
        return set(board, x, y, true);
    }

    /**
     * Sets the given x & y position to 0.
     *
     * @param x the x position (0-7)
     * @param y the y position (0-7)
     */
    public static long clear(long board, int x, int y) {
        return set(board, x, y, false);
    }

    /**
     * Sets the given position to 0.
     *
     * @param pos the position to clear. (0-63)
     */
    public static long clear(long board, int pos) {
        return set(board, pos, false);
    }

    /**
     * @return the number of bits that are 1.
     */
    public static int count(long board) {
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (get(board, i)) count++;
        }
        return count;
    }

    /**
     * @return the String representation of the bit board. Used for debugging.
     */
    public static String format(long board) {
        StringBuilder builder = new StringBuilder();

        // Go through each bit and append its value to the board.
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                builder.append(get(board, j, i) ? "*X" : "* ");
            }
            builder.append("*\n");
        }

        return builder.toString();
    }
}
