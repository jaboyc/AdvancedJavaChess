package com.jlogical.speedchess.cpu;

import com.jlogical.speedchess.board.Board;
import com.jlogical.speedchess.moves.Move;
import com.jlogical.speedchess.moves.MoveGenerator;
import com.jlogical.speedchess.moves.Moveset;

import java.util.HashMap;
import java.util.List;

/**
 * Handles evaluating the score of a board.
 */
public class Evaluator {

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

        if(board.isCheckMate(player)) return Integer.MIN_VALUE;
        if(board.isCheckMate(!player)) return Integer.MAX_VALUE;

        score += (board.getPawns(player).count() - board.getPawns(!player).count()) * 100;
        score += (board.getRooks(player).count() - board.getRooks(!player).count()) * 500;
        score += (board.getKnights(player).count() - board.getKnights(!player).count()) * 320;
        score += (board.getBishops(player).count() - board.getBishops(!player).count()) * 330;
        score += (board.getQueen(player).count() - board.getQueen(!player).count()) * 900;
        score += (board.getKing(player).count() - board.getKing(!player).count()) * 25000;

        if(!board.getHistory().isEmpty()){
            Moveset moves = board.getHistory().peek().getNextMoves(board, player);
            Moveset enemyMoves = board.getHistory().peek().getNextMoves(board, !player);
            score += (moves.getMoves().size() - enemyMoves.getMoves().size()) * 20;
            for(Move move : moves.getMoves()){
                if(move.getCapturedPiece() != 0){
                    score += 30;
                }
            }
            for(Move move : enemyMoves.getMoves()){
                if(move.getCapturedPiece() != 0){
                    score -= 30;
                }
            }
        }

        //scoreHash.put(board, score);

        return score;
    }
}
