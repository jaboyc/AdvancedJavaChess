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

    private static HashMap<Board, Integer> scoreHash = new HashMap<>(); // Stores a hash of every board to its score.

    /**
     * Evaluates the given board for the given player.
     *
     * @param board  the board to evaluate.
     * @param player the player to get the score for.
     * @return the score of the player.
     */
    public static int evaluate(Board board, boolean player) {

        // Return the hashed score if it exists.
        // Integer hashedScore = scoreHash.get(board);
        //if(hashedScore != null) return hashedScore;

        int score = 0;

        // Check mates.
        if (board.isCheckMate(player)) return Integer.MIN_VALUE;
        if (board.isCheckMate(!player)) return Integer.MAX_VALUE;

        // Stale mates.
        if (board.isStaleMate(player)) return Integer.MIN_VALUE / 2;
        if (board.isStaleMate(!player)) return Integer.MIN_VALUE / 2;

        // Material points.
        score += (board.getPawns(player).count() - board.getPawns(!player).count()) * 100;
        score += (board.getRooks(player).count() - board.getRooks(!player).count()) * 500;
        score += (board.getKnights(player).count() - board.getKnights(!player).count()) * 320;
        score += (board.getBishops(player).count() - board.getBishops(!player).count()) * 330;
        score += (board.getQueen(player).count() - board.getQueen(!player).count()) * 900;
        score += (board.getKing(player).count() - board.getKing(!player).count()) * 25000;

        // Positional bonus.
        Bitboard pieces = board.getPieces(player);
        for (int i = 0; i < 64; i++) {
            if (pieces.get(i)) {

                // Pawn position.
                if (board.getPawns(player).get(i)) {
                    score += forwardDistance(i, player) * PAWN_FORWARD_BONUS;
                }

                // Rook/Knight/Bishop/Queen
                if (board.getRooks(player).get(i) || board.getKnights(player).get(i) || board.getBishops(player).get(i) || board.getQueen(player).get(i)) {
                    score += centerDistance(i) * CENTER_POSITION_BONUS;
                }
            }
        }
        Bitboard enemyPieces = board.getPieces(!player);
        for (int i = 0; i < 64; i++) {
            if (enemyPieces.get(i)) {

                // Pawn position.
                if (board.getPawns(!player).get(i)) {
                    score -= forwardDistance(i, !player) * PAWN_FORWARD_BONUS;
                }

                // Rook/Knight/Bishop/Queen
                if (board.getRooks(!player).get(i) || board.getKnights(!player).get(i) || board.getBishops(!player).get(i) || board.getQueen(!player).get(i)) {
                    score -= centerDistance(i) * CENTER_POSITION_BONUS;
                }
            }
        }

        // Move bonuses.
        if (!board.getHistory().isEmpty()) {
            Moveset moves = board.getHistory().peek().getNextMoves(board, player);
            Moveset enemyMoves = board.getHistory().peek().getNextMoves(board, !player);

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
                if(dest == Piece.getValue(KING)) continue;

                score += Math.min(origin, dest) / 15 + 10;
            }

            for (Move move : enemyMoves.getDefences()) {
                int origin = Piece.getValue(board.getPieceFromBitboard(move.getPieceBoard()));
                int dest = Piece.getValue(move.getCapturedPiece());

                // Skip defending the king.
                if(dest == Piece.getValue(KING)) continue;

                score -= Math.min(origin, dest) / 15 + 10;
            }
        }

        //scoreHash.put(board, score);

        return score;
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
     * @param pos the position to check.
     * @param player the player to check on.
     * @return a bonus score for how far in front the given position is.
     */
    private static int forwardDistance(int pos, boolean player){
        if(player){
            return pos / 8;
        }else{
            return 7 - pos / 8;
        }
    }
}
