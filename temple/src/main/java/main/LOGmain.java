package main;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import game.GameState;

/**
 * Runs the program via the text (console) interface.
 * Loops the program over keys 1000 to 1049 to compare the performance of
 * solutions in student.Explorer.java.
 */
public class LOGmain {
    public static void main(String[] args) {
        String format = "%5s   %6s";
        System.out.println(String.format(format, "seed", "score"));
        for (long seed = 1000; seed < 1050; seed++) {
            int score;
            try {
                // Redirect GameState's normal console output to a temporary buffer.
                System.setOut(new PrintStream(new ByteArrayOutputStream()));
                score = GameState.runNewGame(seed, false);
            } finally {
                // Always restore the real console output, even if the game fails.
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            }
            System.out.println(String.format(format, seed, score));
        }
    }
}
