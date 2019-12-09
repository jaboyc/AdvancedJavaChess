package com.jlogical.speedchess;

import com.jlogical.speedchess.board.Board;
import com.jlogical.speedchess.moves.Move;
import com.jlogical.speedchess.moves.MoveGenerator;

import java.util.List;
import java.util.Scanner;

/**
 * Driver for the chess app.
 */
public class Chess {

    private Board board; // The board used in the current game.
    private boolean[] players; // Whether the players are CPU controlled.

    /**
     * Creates a Chess object.
     */
    public Chess(boolean... players) {
        board = new Board();
        this.players = players;
    }

    public void play() {

        boolean currPlayer = true; // Whether the current player is white.

        // Repeatedly switch turns until checkmate or stalemate.
        while (true) {

            // Print board information out.
            System.out.println(board);
            System.out.println(MoveGenerator.generateMoves(board, currPlayer, true));

            // Get the next move.
            Move nextMove;
            if (players[currPlayer ? 0 : 1]) {
                nextMove = cpuMove(currPlayer);
            } else {
                nextMove = humanMove(currPlayer);
            }

            // Make the next move.
            if (nextMove != null)
                board.makeMove(nextMove);

            // Swap the current player.
            currPlayer = !currPlayer;
        }
    }

    /**
     * Asks for user input and returns the move that the current user wants to make.
     *
     * @param player whether the current player is white.
     * @return the move the human inputted.
     */
    private Move humanMove(boolean player) {

        // Keep asking for user input until they give a valid move.
        while (true) {

            // Get the from position and to position.
            int fromPos = getPosFromUserInput("Input Source Position: ");
            int toPos = getPosFromUserInput("Input Destination Position: ");

            // Calculate the captured piece.
            int capturedPiece = board.getPiece(toPos);

            // Generate the move from the input.
            Move move = new Move(board.getPieceBitboard(board.getPiece(fromPos)), fromPos, toPos, capturedPiece);

            // If the move is valid, return the move. Otherwise try again.
            if (MoveGenerator.generateMoves(board, player, true).contains(move)) {
                return move;
            } else {
                System.out.println("Invalid Move. Try Again.");
            }
        }
    }

    /**
     * Gets a position by prompting the user. Must be in the format of (file)(rank). Ex: e2, d6, etc...
     *
     * @param prompt the prompt to print.
     * @return the position the user chose (0-63).
     */
    private int getPosFromUserInput(String prompt) {

        // Keep getting user input until its valid.
        while (true) {
            System.out.print(prompt);

            // Get the user input.
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            // Handle special cases.
            if(input.equals("undo")){
                board.unmakeMove();
                board.unmakeMove();
                System.out.println(board);
                continue;
            }else if(input.equals("q") || input.equals("quit")){
                System.exit(0);
            }

            if (input.length() != 2) {
                System.out.println("Position must be the file followed by the rank. Try again.");
                continue;
            }

            // Get the file.
            char file = input.charAt(0);
            if(file < 'a' || file > 'h'){
                System.out.println("Invalid file. Must be a letter between 'a' and 'h'. Try again.");
                continue;
            }

            // Get the rank.
            int rank;
            try {
                rank = Integer.parseInt("" + input.charAt(1));
            } catch (NumberFormatException e) {
                System.out.println("The second character must be a number!");
                continue;
            }
            if(rank < 1 || rank > 8){
                System.out.println("Invalid rank. Must be a number between 1 and 8. Try again.");
                continue;
            }

            // Get the position from the input.
            int pos = (rank - 1) * 8 + (file - 'a');
            if (pos < 0 || pos >= 64) {
                System.out.println("Invalid position. Try again.");
                continue;
            }

            return pos;
        }
    }

    /**
     * Uses NegaMax Alpha-Beta pruning to generate the next move.
     *
     * @param player whether the current player is white.
     * @return the move the cpu chose.
     */
    private Move cpuMove(boolean player) {
        return null;
    }

    public static void main(String[] args) {
        Chess chess = new Chess(false, false);
        chess.play();
    }
}