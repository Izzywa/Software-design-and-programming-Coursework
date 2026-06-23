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
 * Testing by way of simulation of running TXTmain over specific top-tier seeds.
 * Automated tests to ensure the Explore and Escape phases execute without fail,
 * while capturing performance metrics to compare against the League Table.
 */
public class LeagueTableSeedTest {

    /**
     * Runs the simulation sequentially using seeds extracted from the League Table.
     * Captures Gold, Multiplier, and Score from the console output.
     * Log is timestamped so it is never overwritten, allowing for easy A/B testing of algorithms.
     */
    @Test
    public void testLeagueTableSeedsWithScoreTracking() throws IOException {
        File logDir = new File("logs");
        if (!logDir.exists()) logDir.mkdirs(); 

        // Add a timestamp to the filename to prevent overwriting
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String logFileName = "logs/simulation_league_table_" + timestamp + ".log";
        PrintWriter writer = new PrintWriter(new FileWriter(logFileName));
        
        System.out.println("League Table Baseline test running... See " + logFileName);
        writer.println("League Table Baseline Log - " + LocalDateTime.now());
        
        // Define the specific seeds from the league table
        long[] leagueSeeds = {
            -4152836868077314850L, -3967848802208875438L, 5864101433891852061L,
            7445652272991402161L, 8781946738346443336L, -8753562310865996698L,
            -757868709594414956L, -5747184872657058727L, -7761980840912806448L,
            -4501867144509231625L, 9178600685835736767L, 8849755165154918804L,
            -8795875982559746259L, 8207908124709091172L, 4218948394500449828L,
            3694314465540184459L, -5936268151507118028L, -4779223688972917879L,
            -8522969912440837840L, 6496594554013205192L, 6629009396325103285L,
            2832876979625815005L, -5845531988250598653L, -1906048792819286095L
        };
        
        int totalRuns = leagueSeeds.length;
        List<Long> failedSeeds = new ArrayList<>();
        
        // Save BOTH original streams to restore them later
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        
        long totalDuration = 0;
        long totalScore = 0;
        long totalGold = 0;
        int successfulRuns = 0;

        for (int i = 0; i < leagueSeeds.length; i++) {
            long currentSeed = leagueSeeds[i];
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

                writer.println(String.format("Seed %20d: %-10s | Time: %4d ms%s | Gold: %5s | Mult: %4s | Score: %6s", 
                               currentSeed, status, duration, timeFlag, gold, multiplier, score));
            } else {
                // If it failed, we don't have a score to report
                writer.println(String.format("Seed %20d: %-10s | Time: %4d ms%s | Gold:   N/A | Mult:  N/A | Score:    N/A", 
                               currentSeed, status, duration, timeFlag));
            }
        }

        // Print a detailed summary with averages
        writer.println("\n--- BASELINE SUMMARY ---");
        writer.println("Total Runs Completed: " + totalRuns);
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
        assertTrue(failedSeeds.isEmpty(), "League Table simulation encountered issues. Check " + logFileName);
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