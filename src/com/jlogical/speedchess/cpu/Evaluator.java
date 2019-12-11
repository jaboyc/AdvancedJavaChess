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

        score += (Bitboard.count(board.pawns[0]) - Bitboard.count(board.pawns[1])) * 100;
        score += (Bitboard.count(board.rooks[0]) - Bitboard.count(board.rooks[1])) * 500;
        score += (Bitboard.count(board.knights[0]) - Bitboard.count(board.knights[1])) * 320;
        score += (Bitboard.count(board.bishops[0]) - Bitboard.count(board.bishops[1])) * 330;
        score += (Bitboard.count(board.queens[0]) - Bitboard.count(board.queens[1])) * 900;
        score += (Bitboard.count(board.kings[0]) - Bitboard.count(board.kings[1])) * 25000;

        // Positional bonus.
        long pieces = board.getPieces(true);
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(pieces,i)) {

                // Pawn position.
                if (Bitboard.get(board.pawns[0],i)) {
                    score += forwardDistance(i, true) * PAWN_FORWARD_BONUS;
                }

                // Rook/Knight/Bishop/Queen
                if (Bitboard.get(board.rooks[0],i) || Bitboard.get(board.knights[0],i) || Bitboard.get(board.bishops[0],i) || Bitboard.get(board.queens[0],i)) {
                    score += centerDistance(i) * CENTER_POSITION_BONUS;
                }
            }
        }
        pieces = board.getPieces(false);
        for (int i = 0; i < 64; i++) {
            if (Bitboard.get(pieces,i)) {

                // Pawn position.
                if (Bitboard.get(board.pawns[1],i)) {
                    score -= forwardDistance(i, false) * PAWN_FORWARD_BONUS;
                }

                // Rook/Knight/Bishop/Queen
                if (Bitboard.get(board.rooks[1],i) || Bitboard.get(board.knights[1],i) || Bitboard.get(board.bishops[1],i) || Bitboard.get(board.queens[1],i)) {
                    score -= centerDistance(i) * CENTER_POSITION_BONUS;
                }
            }
        }

        // Move bonuses.
        if (!board.getMoveHistory().isEmpty()) {
            Moveset moves = board.getMoveHistory().peek().getNextMoves(board, true);
            Moveset enemyMoves = board.getMoveHistory().peek().getNextMoves(board, false);

            // Bonus points for attacks / mobility.
            for (Move move : moves.getMoves()) {

                if(move.getTo()> 64){
                    System.out.println(Bitboard.format(board.pawns[0]));
                    System.out.println(move.getPieceType() + " -> " + move);
                }
                score += centerDistance(move.getTo()) * CENTER_MOBILITY_BONUS + 5;

                if (move.getCapturedPiece() != 0) {
                    int origin = Piece.getValue(board.getPiece(move.getFrom()));
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
                    int origin = Piece.getValue(board.getPiece(move.getFrom()));
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
                int origin = Piece.getValue(board.getPiece(move.getFrom()));
                int dest = Piece.getValue(move.getCapturedPiece());

                // Skip defending the king.
                if (dest == Piece.getValue(KING)) continue;

                score += Math.min(origin, dest) / 15 + 10;
            }

            for (Move move : enemyMoves.getDefences()) {
                int origin = Piece.getValue(board.getPiece(move.getFrom()));
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
