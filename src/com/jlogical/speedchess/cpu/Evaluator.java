package com.jlogical.speedchess.cpu;

import com.jlogical.speedchess.bitboard.Bitboard;
import com.jlogical.speedchess.board.Board;
import com.jlogical.speedchess.board.Piece;
import com.jlogical.speedchess.moves.Move;
import com.jlogical.speedchess.moves.Moveset;

import java.util.HashMap;

import static com.jlogical.speedchess.board.Piece.KING;

/**
 * Handles evaluating the score of a board.
 */
public class Evaluator {

    private static final int CENTER_MOBILITY_BONUS = 3; // Bonus for being able to move towards the center.
    private static final int CENTER_POSITION_BONUS = 8; // Bonus for being positioned in the middle.
    private static final int PAWN_FORWARD_BONUS = 20; // Bonus for being up front for a pawn.

    private static HashMap<Long, Integer> scoreHash = new HashMap<>(10000); // Hash map of all the evaluations of states already looked at.

    /**
     * Evaluates the given board for the given player.
     *
     * @param board  the board to evaluate.
     * @param player the player to get the score for.
     * @return the score of the player.
     */
    public static int evaluate(Board board, boolean player) {

        int scoreMultiplier = player ? 1 : -1; // Negate the score if the player to get the score for is black.

        long key = ZobristKey.getKeyForBoard(board, player);
        Integer hashScore; // The score for player "true"
        if ((hashScore = scoreHash.getOrDefault(key, null)) != null) {
            return hashScore * scoreMultiplier;
        }

        int score = 0;

        // Check mates.
        if (board.isCheckMate(true)) return -1000000 * scoreMultiplier;
        if (board.isCheckMate(false)) return 1000000 * scoreMultiplier;

        // Stale mates.
        if (board.isStaleMate(true)) return -50000 * scoreMultiplier;
        if (board.isStaleMate(false)) return -50000 * scoreMultiplier;

        // Material points.
        score += (board.getPawns(true).count() - board.getPawns(false).count()) * 100;
        score += (board.getRooks(true).count() - board.getRooks(false).count()) * 500;
        score += (board.getKnights(true).count() - board.getKnights(false).count()) * 320;
        score += (board.getBishops(true).count() - board.getBishops(false).count()) * 330;
        score += (board.getQueen(true).count() - board.getQueen(false).count()) * 900;
        score += (board.getKing(true).count() - board.getKing(false).count()) * 25000;

        // Positional bonus.
        Bitboard pieces = board.getPieces(true);
        for (int i = 0; i < 64; i++) {
            if (pieces.get(i)) {

                // Pawn position.
                if (board.getPawns(true).get(i)) {
                    score += forwardDistance(i, true) * PAWN_FORWARD_BONUS;
                }

                // Rook/Knight/Bishop/Queen
                if (board.getRooks(true).get(i) || board.getKnights(true).get(i) || board.getBishops(true).get(i) || board.getQueen(true).get(i)) {
                    score += centerDistance(i) * CENTER_POSITION_BONUS;
                }
            }
        }
        Bitboard enemyPieces = board.getPieces(false);
        for (int i = 0; i < 64; i++) {
            if (enemyPieces.get(i)) {

                // Pawn position.
                if (board.getPawns(false).get(i)) {
                    score -= forwardDistance(i, false) * PAWN_FORWARD_BONUS;
                }

                // Rook/Knight/Bishop/Queen
                if (board.getRooks(false).get(i) || board.getKnights(false).get(i) || board.getBishops(false).get(i) || board.getQueen(false).get(i)) {
                    score -= centerDistance(i) * CENTER_POSITION_BONUS;
                }
            }
        }

        // Move bonuses.
        if (!board.getHistory().isEmpty()) {
            Moveset moves = board.getHistory().peek().getNextMoves(board, true);
            Moveset enemyMoves = board.getHistory().peek().getNextMoves(board, false);

            // Bonus points for attacks / mobility.
            for (Move move : moves.getMoves()) {

                score += centerDistance(move.getTo()) * CENTER_MOBILITY_BONUS + 5;

                if (move.getCapturedPiece() != 0) {
                    int origin = Piece.getValue(board.getPieceFromBitboard(move.getPieceBoard()));
                    int dest = Piece.getValue(move.getCapturedPiece());

                    // If this move is checking the king, reduce the bonus points.
                    if (dest == Piece.getValue(KING))
                        dest /= 25;

                    // Calculate the score.
                    if (origin > dest) {
                        score += 1000 / (origin - dest) + 5;
                    } else {
                        score += (dest - origin) / 70 + 5;
                    }
                }
            }
            for (Move move : enemyMoves.getMoves()) {

                score -= centerDistance(move.getTo()) * CENTER_MOBILITY_BONUS + 5;

                if (move.getCapturedPiece() != 0) {
                    int origin = Piece.getValue(board.getPieceFromBitboard(move.getPieceBoard()));
                    int dest = Piece.getValue(move.getCapturedPiece());

                    // If this move is checking the king, reduce the bonus points.
                    if (dest == Piece.getValue(KING))
                        dest /= 25;

                    if (origin > dest) {
                        score -= 1000 / (origin - dest) + 5;
                    } else {
                        score -= (dest - origin) / 70 + 5;
                    }
                }
            }

            // Bonus points for defense.
            for (Move move : moves.getDefences()) {
                int origin = Piece.getValue(board.getPieceFromBitboard(move.getPieceBoard()));
                int dest = Piece.getValue(move.getCapturedPiece());

                // Skip defending the king.
                if (dest == Piece.getValue(KING)) continue;

                score += Math.min(origin, dest) / 15 + 10;
            }

            for (Move move : enemyMoves.getDefences()) {
                int origin = Piece.getValue(board.getPieceFromBitboard(move.getPieceBoard()));
                int dest = Piece.getValue(move.getCapturedPiece());

                // Skip defending the king.
                if (dest == Piece.getValue(KING)) continue;

                score -= Math.min(origin, dest) / 15 + 10;
            }
        }

        if (scoreHash.size() >= 10000 - 1) {
            scoreHash.remove(scoreHash.keySet().iterator().next());
        }
        scoreHash.put(key, score);

        return score * scoreMultiplier;
    }

    private static final int[] CENTER_DISTANCE = { // Contains the distance from the center for all positions in the board.
            3, 3, 3, 3, 3, 3, 3, 3,
            3, 2, 2, 2, 2, 2, 2, 3,
            3, 2, 1, 1, 1, 1, 2, 3,
            3, 2, 1, 0, 0, 1, 2, 3,
            3, 2, 1, 0, 0, 1, 2, 3,
            3, 2, 1, 1, 1, 1, 2, 3,
            3, 2, 2, 2, 2, 2, 2, 3,
            3, 3, 3, 3, 3, 3, 3, 3
    };

    /**
     * @param pos the position to check.
     * @return a bonus score for how close the given position is near the center.
     */
    private static int centerDistance(int pos) {
        return (3 - CENTER_DISTANCE[pos]);
    }

    /**
     * @param pos    the position to check.
     * @param player the player to check on.
     * @return a bonus score for how far in front the given position is.
     */
    private static int forwardDistance(int pos, boolean player) {
        if (player) {
            return pos / 8;
        } else {
            return 7 - pos / 8;
        }
    }
}
