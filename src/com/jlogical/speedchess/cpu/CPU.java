package com.jlogical.speedchess.cpu;

import com.jlogical.speedchess.board.Board;
import com.jlogical.speedchess.moves.Move;
import com.jlogical.speedchess.moves.Moveset;

import java.awt.*;

/**
 * Handles the Minimax Alpha-Beta algorithm to determine the next move.
 */
public class CPU {

    private static final boolean DEBUG = true; // Whether to output debug information as the CPU is thinking.

    private static final int COMPLEXITY = 2; // The number of turns to look ahead to decide its next move.

    private static int count = 0;

    /**
     * Calculates the next move for the cpu.
     *
     * @param board  the board to calculate on.
     * @param player the player the CPU is maximizing.
     * @return the move to perform. Null if none are possible.
     */
    public static Move calculateNextMove(Board board, boolean player) {

        count = 0;

        Pair<Move, Integer> highestMove = calculate(board, player, true, COMPLEXITY, null, board.getHistory().peek().getNextLegalMoves(board, player), Integer.MIN_VALUE, Integer.MAX_VALUE);

        // Check for null.
        if (highestMove == null || highestMove.getFirst() == null) {
            System.out.println("NO MOVES LEFT FOR CPU");
            return null;
        }

        if (DEBUG) Toolkit.getDefaultToolkit().beep();

        System.out.println("\n\n");
        System.out.println(highestMove.getFirst());
        System.out.println("Node Count: " + count);

        return highestMove.getFirst();
    }

    /**
     * Uses minimax algorithm to calculate the move-score the player would most optimally choose.
     *
     * @param board      the board to use.
     * @param player     the player currently in the search tree.
     * @param maximizing the player that is maximizing the score.
     * @param layersLeft the number of layers left.
     * @param rootMove   the move that started this calculate chain.
     * @param moveset    the moveset that it should calculate.
     * @return the move-score with the most likelihood of being chosen.
     */
    private static Pair<Move, Integer> calculate(Board board, boolean player, boolean maximizing, double layersLeft, Move rootMove, Moveset moveset, int alpha, int beta) {

        if (layersLeft <= 0 || board.isCheckMate(player) || board.isCheckMate(!player) || board.isStaleMate(player) || board.isStaleMate(!player)) {
            count++;
            return new Pair<>(rootMove, Evaluator.evaluate(board, player));
        }

        if (maximizing) {

            Pair<Move, Integer> bestMove = new Pair<>(null, Integer.MIN_VALUE);

            for (Move move : moveset.getMoves()) {
                board.makeMove(move, player);

                double depth = board.inCheck(!player) || move.getCapturedPiece() != 0 ? layersLeft - 0.2 : layersLeft - 1;

                Pair<Move, Integer> result = calculate(board, player, false, depth, rootMove == null ? move : rootMove, move.getNextLegalMoves(board, !player), alpha, beta);
                bestMove = bestMove.getSecond() >= result.getSecond() ? bestMove : result;

                board.unmakeMove(player);

                if (layersLeft == COMPLEXITY && DEBUG) {
                    System.out.println(move + " (" + String.format("%d", result.getSecond()) + ")");
                }

                alpha = Math.max(alpha, result.getSecond());
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        } else {

            Pair<Move, Integer> worstMove = new Pair<>(null, Integer.MAX_VALUE);

            for (Move move : moveset.getMoves()) {
                board.makeMove(move, !player);

                double depth = board.inCheck(player) || move.getCapturedPiece() != 0 ? layersLeft - 0.2 : layersLeft - 1;

                Pair<Move, Integer> result = calculate(board, player, true, depth, rootMove == null ? move : rootMove, move.getNextLegalMoves(board, player), alpha, beta);
                worstMove = worstMove.getSecond() <= result.getSecond() ? worstMove : result;

                board.unmakeMove(!player);

                beta = Math.min(beta, result.getSecond());
                if (beta <= alpha) {
                    break;
                }
            }

            return worstMove;
        }
    }

}
