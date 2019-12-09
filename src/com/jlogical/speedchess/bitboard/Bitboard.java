package com.jlogical.speedchess.bitboard;

/**
 * Represents an 8x8 grid of 1s or 0s. Used in a variety of ways.
 */
public class Bitboard {

    private long board; // The internal representation is a 64-bit long.

    public Bitboard() {
        board = 0;
    }

    /**
     * Returns the value at the given position
     *
     * @param pos the position to look at (0-255)
     * @return the value at the position.
     */
    public boolean get(int pos) {
        return ((board >> pos) & 1) == 1;
    }

    /**
     * Returns the value at x and y.
     *
     * @param x the x position (0-7)
     * @param y the y position (0-7)
     * @return the value at the position.
     */
    public boolean get(int x, int y) {
        return get(y * 8 + x);
    }

    /**
     * Sets the value at the given position to the given value.
     *
     * @param pos   the position to set (0-63)
     * @param value the value to set it to.
     */
    public Bitboard set(int pos, boolean value) {
        if (value)
            board |= (1L << pos);
        else
            board &= ~(1L << pos);
        return this;
    }

    /**
     * Sets the given position to 1.
     *
     * @param pos the position to set.
     */
    public Bitboard set(int pos) {
        return set(pos, true);
    }

    /**
     * Sets the given x & y position to the given value.
     *
     * @param x     the x position (0-7)
     * @param y     the y position (0-7)
     * @param value 1 or 0
     */
    public Bitboard set(int x, int y, boolean value) {
        return set(y * 8 + x, value);
    }

    /**
     * Sets the given x & y position to 1.
     *
     * @param x the x position (0-7)
     * @param y the y position (0-7)
     */
    public Bitboard set(int x, int y) {
        return set(x, y, true);
    }

    /**
     * Sets the given x & y position to 0.
     *
     * @param x the x position (0-7)
     * @param y the y position (0-7)
     */
    public Bitboard clear(int x, int y) {
        return set(x, y, false);
    }

    /**
     * Sets the given position to 0.
     * @param pos the position to clear. (0-63)
     */
    public Bitboard clear(int pos){
        return set(pos, false);
    }

    /**
     * Negates the board by flipping all the bits.
     */
    public Bitboard not() {
        Bitboard bitboard = new Bitboard();
        bitboard.board = ~board;
        return bitboard;
    }

    /**
     * @return the number of bits that are 1.
     */
    public int count() {
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (get(i)) count++;
        }
        return count;
    }


    /**
     * Returns the intersection between two bitboards.
     *
     * @param b1 the first bitboard.
     * @param b2 the second bitboard.
     * @return the intersection.
     */
    public static Bitboard and(Bitboard b1, Bitboard b2) {
        Bitboard output = new Bitboard();

        output.board = b1.board & b2.board;

        return output;
    }

    /**
     * Returns the intersection between an n number of bitboards.
     *
     * @param boards the boards to intersect.
     * @return the intersection.
     */
    public static Bitboard and(Bitboard... boards) {
        Bitboard output = new Bitboard();
        output.board = Long.MAX_VALUE;

        for (Bitboard b : boards) {
            output.board &= b.board;
        }

        return output;
    }

    /**
     * Returns the union of two bitboards.
     *
     * @param b1 the first bitboard.
     * @param b2 the second bitboard.
     * @return the union.
     */
    public static Bitboard or(Bitboard b1, Bitboard b2) {
        Bitboard output = new Bitboard();

        output.board = b1.board | b2.board;
        return output;
    }

    /**
     * Returns the union of n bitboards.
     *
     * @param boards the boards to union.
     * @return the union.
     */
    public static Bitboard or(Bitboard... boards) {
        Bitboard output = new Bitboard();

        for (Bitboard b : boards) {
            output.board |= b.board;
        }

        return output;
    }

    /**
     * @return the String representation of the bit board. Used for debugging.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Go through each bit and append its value to the board.
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                builder.append(get(j, i) ? "*X" : "* ");
            }
            builder.append("*\n");
        }

        return builder.toString();
    }

    /**
     * Returns whether the 'board' of another Bitboard is equal to this one.
     *
     * @param obj the object to compare.
     * @return whether they are equal.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Bitboard) {
            Bitboard b = (Bitboard) obj;
            return board == b.board;
        }
        return false;
    }
}
