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

    /**
     * Starts playing the chess game.
     */
    public void play() {

        boolean currPlayer = true; // Whether the current player is white.

        // Repeatedly switch turns until checkmate or stalemate.
        while (!board.isCheckMate(currPlayer) && !board.isStaleMate(currPlayer)) {

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
                board.makeMove(nextMove, currPlayer);

            // Swap the current player.
            currPlayer = !currPlayer;
        }

        System.out.println(board);

        // Print the end condition.
        if (board.isCheckMate(currPlayer)) {
            System.out.println("===(CHECK MATE)===");
        } else {
            System.out.println("===(STALE MATE)===");
        }
    }

    /**
     * Asks for user input and returns the move that the current user wants to make.
     *
     * @param player whether the current player is white.
     * @return the move the human inputted.
     */
    private Move humanMove(boolean player) {

        if (board.inCheck(player))
            System.out.println("   [CHECK]");

        // Keep asking for user input until they give a valid move.
        while (true) {

            // Get the from position and to position.
            int fromPos = getPosFromUserInput("Input Source Position: ", player);
            int toPos = getPosFromUserInput("Input Destination Position: ", player);

            // Calculate the captured piece.
            int capturedPiece = board.getPiece(toPos);

            // Generate the move from the input.
            Move move = new Move(board.getPieceBitboard(board.getPiece(fromPos)), fromPos, toPos, capturedPiece);

            // If the move is valid, return the move. Otherwise try again.
            List<Move> possibleMoves = MoveGenerator.generateMoves(board, player, true);
            for (Move possibleMove : possibleMoves) {
                if (possibleMove.similar(move)) {
                    return possibleMove;
                }
            }

            System.out.println("Invalid Move. Try Again.");

        }
    }

    /**
     * Gets a position by prompting the user. Must be in the format of (file)(rank). Ex: e2, d6, etc...
     *
     * @param prompt the prompt to print.
     * @param player the player whose inputs this is getting. Used for undoing moves.
     * @return the position the user chose (0-63).
     */
    private int getPosFromUserInput(String prompt, boolean player) {

        // Keep getting user input until its valid.
        while (true) {
            System.out.print(prompt);

            // Get the user input.
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            // Handle special cases.
            if (input.equals("undo")) {
                board.unmakeMove(!player);
                board.unmakeMove(player);
                System.out.println(board);
                continue;
            } else if (input.equals("q") || input.equals("quit")) {
                System.exit(0);
            }

            if (input.length() != 2) {
                System.out.println("Position must be the file followed by the rank. Try again.");
                continue;
            }

            // Get the file.
            char file = input.charAt(0);
            if (file < 'a' || file > 'h') {
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
            if (rank < 1 || rank > 8) {
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
