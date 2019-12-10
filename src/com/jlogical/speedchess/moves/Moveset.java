package com.jlogical.speedchess.moves;

import java.util.LinkedList;
import java.util.List;

/**
 * A set of valid moves a player can do in a board configuration.
 */
public class Moveset {

    private List<Move> moves; // List of valid movements.
    private List<Move> defences; // List of "moves" that defend a piece.

    /**
     * Creates an empty moveset.
     */
    public Moveset() {
        moves = new LinkedList<>();
        defences = new LinkedList<>();
    }

    /**
     * Adds a move to the moveset. If the move is defending, add it to the defences instead.
     *
     * @param move the move to add.
     */
    public void addMove(Move move) {
        if (move.isDefending()) {
            addDefence(move);
        } else {
            if (move.getCapturedPiece() != 0)
                moves.add(0, move);
            else
                moves.add(move);
        }
    }

    /**
     * Adds a defense move to the moveset.
     *
     * @param move the defense to add.
     */
    public void addDefence(Move move) {
        defences.add(move);
    }

    /**
     * @return whether there are no valid moves to make.
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }

    public List<Move> getMoves() {
        return moves;
    }

    public List<Move> getDefences() {
        return defences;
    }
}
