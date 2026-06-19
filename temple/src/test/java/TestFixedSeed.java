import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Testing by way of simulation of running TXTmain over a fixed range of seeds.
 * Automated tests to ensure the Explore and Escape phases execute without fail,
 * while capturing performance metrics (Gold, Multiplier, Score) for baseline comparisons.
 */
public class TestFixedSeed {

    /**
     * Runs the simulation sequentially from a starting predefined seed to an ending predefined seed.
     * Captures Gold, Multiplier, and Score from the console output.
     * Log is timestamped so it is never overwritten, allowing for easy A/B testing of algorithms.
     */
    @Test
    public void testSequentialSeedsWithScoreTracking() throws IOException {
        File logDir = new File("logs");
        if (!logDir.exists()) logDir.mkdirs(); 

        // Add a timestamp to the filename to prevent overwriting
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String logFileName = "logs/simulation_fixed_seed_" + timestamp + ".log";
        PrintWriter writer = new PrintWriter(new FileWriter(logFileName));
        
        System.out.println("Fixed Seed Baseline test running... See " + logFileName);
        writer.println("Fixed Seed Baseline Log - " + LocalDateTime.now());
        
        // Define the specific range of seeds to test (1000-1049)
        long startSeed = 1000;
        long endSeed = 1049;
        int totalRuns = (int) (endSeed - startSeed + 1);
        
        List<Long> failedSeeds = new ArrayList<>();
        
        // Save BOTH original streams to restore them later
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        
        long totalDuration = 0;
        long totalScore = 0;
        long totalGold = 0;
        int successfulRuns = 0;

        for (long currentSeed = startSeed; currentSeed <= endSeed; currentSeed++) {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            String status;

            long runStartTime = System.currentTimeMillis();

            try {
                // Redirect BOTH streams to capture errors AND the final score prints
                System.setOut(new PrintStream(outContent));
                System.setErr(new PrintStream(errContent));
                
                String[] args = {"-s", String.valueOf(currentSeed)};
                main.TXTmain.main(args); 
                
                String errorLogs = errContent.toString().trim();
                status = determineStatus(errorLogs);
                
                if (!status.equals("SUCCESS")) {
                    failedSeeds.add(currentSeed);
                }
                
            } catch (Exception e) {
                status = "CRASHED (Java Exception)";
                failedSeeds.add(currentSeed);
            } finally {
                // Always restore the console outputs after every single run!
                System.setOut(originalOut);
                System.setErr(originalErr);
            }

            long runEndTime = System.currentTimeMillis();
            long duration = runEndTime - runStartTime;
            totalDuration += duration;

            String timeFlag = (duration > 8000) ? " [SLOW]" : "";
            
            // If the run succeeded, parse the console output to find the scores
            if (status.equals("SUCCESS")) {
                String consoleOutput = outContent.toString();
                
                String gold = extractMetric(consoleOutput, "Gold");
                String multiplier = extractMetric(consoleOutput, "Multiplier");
                String score = extractMetric(consoleOutput, "Score");
                
                // Keep track for averages
                try {
                    totalGold += Long.parseLong(gold);
                    totalScore += Long.parseLong(score);
                    successfulRuns++;
                } catch (NumberFormatException ignored) {}

                writer.println(String.format("Seed %02d: %-10s | Time: %4d ms%s | Gold: %5s | Mult: %4s | Score: %6s", 
                               currentSeed, status, duration, timeFlag, gold, multiplier, score));
            } else {
                // If it failed, we don't have a score to report
                writer.println(String.format("Seed %02d: %-10s | Time: %4d ms%s | Gold:   N/A | Mult:  N/A | Score:    N/A", 
                               currentSeed, status, duration, timeFlag));
            }
        }

        // Print a detailed summary with averages
        writer.println("\n--- BASELINE SUMMARY ---");
        writer.println("Seed Range: " + startSeed + " to " + endSeed);
        writer.println("Total Runs with Issues: " + failedSeeds.size());
        writer.println(String.format("Average Execution Time: %.2f ms", (double) totalDuration / totalRuns));
        
        if (successfulRuns > 0) {
            writer.println(String.format("Average Gold:   %.0f", (double) totalGold / successfulRuns));
            writer.println(String.format("Average Score:  %.0f", (double) totalScore / successfulRuns));
        }

        if (!failedSeeds.isEmpty()) {
            writer.println("Seeds to investigate: " + failedSeeds.toString());
        }
        
        writer.close(); 
        assertTrue(failedSeeds.isEmpty(), "Sequential simulation encountered issues. Check " + logFileName);
    }

    /**
     * Helper method to scan the console output and extract numbers based on a keyword.
     * Works by looking for the keyword (e.g. "Score") and grabbing the first number after it.
     */
    private String extractMetric(String outputText, String keyword) {
        // Regex: case-insensitive keyword, followed by anything, followed by a number (decimals allowed)
        Pattern pattern = Pattern.compile("(?i)" + keyword + ".*?([0-9]+(?:\\.[0-9]+)?)");
        Matcher matcher = pattern.matcher(outputText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "N/A";
    }

    /**
     * Analyzes the error output from GameState to categorize the exact failure reason.
     */
    private String determineStatus(String errLogs) {
        if (errLogs.isEmpty()) return "SUCCESS"; 
        if (errLogs.contains("Your solution to explore returned at the wrong location")) return "EXPLORE_FAILED (Wrong Location)";
        if (errLogs.contains("Your code caused an error  during the explore phase")) return "EXPLORE_FAILED (Exception Caught)";
        if (errLogs.contains("Your code caused an error during the escape phase")) return "ESCAPE_FAILED (Exception Caught)";
        if (errLogs.contains("Your solution to escape ran out of steps before returning")) return "ESCAPE_FAILED (Time Out)";
        if (errLogs.contains("Your solution to escape failed to end at the stairs")) return "ESCAPE_FAILED (Wrong Location)";
        return "FAILED (Unknown Error)";
    }    
}