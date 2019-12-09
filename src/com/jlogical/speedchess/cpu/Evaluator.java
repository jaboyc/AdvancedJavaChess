package com.jlogical.speedchess.cpu;

import com.jlogical.speedchess.board.Board;
import com.jlogical.speedchess.moves.Move;
import com.jlogical.speedchess.moves.MoveGenerator;

import java.util.List;

/**
 * Handles evaluating the score of a board.
 */
public class Evaluator {

    /**
     * Evaluates the given board for the given player.
     *
     * @param board  the board to evaluate.
     * @param player the player to get the score for.
     * @return the score of the player.
     */
    public static int evaluate(Board board, boolean player) {

        int score = 0;

        if(board.isCheckMate(player)) return Integer.MIN_VALUE;
        if(board.isCheckMate(!player)) return Integer.MAX_VALUE;

        score += (board.getPawns(player).count() - board.getPawns(!player).count()) * 100;
        score += (board.getRooks(player).count() - board.getRooks(!player).count()) * 500;
        score += (board.getKnights(player).count() - board.getKnights(!player).count()) * 320;
        score += (board.getBishops(player).count() - board.getBishops(!player).count()) * 330;
        score += (board.getQueen(player).count() - board.getQueen(!player).count()) * 900;
        score += (board.getKing(player).count() - board.getKing(!player).count()) * 25000;

        List<Move> moves = MoveGenerator.generateMoves(board, player, false);
        List<Move> enemyMoves = MoveGenerator.generateMoves(board, !player, false);
        score += (moves.size() - enemyMoves.size()) * 20;
        for(Move move : moves){
            if(move.getCapturedPiece() != 0){
                score += 30;
            }
        }
        for(Move move : enemyMoves){
            if(move.getCapturedPiece() != 0){
                score -= 30;
            }
        }

        return score;
    }
}
