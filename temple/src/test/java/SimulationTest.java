import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Testing by way of simulation of running TXTmain.
 * Automated tests to ensure the Explore and Escape phases execute without fail
 * over multiple randomised runs.
 */
public class SimulationTest {

    /**
     * Automated continuous test.
     * Runs the simulation 50 times using randomly generated seeds.
     * Results are logged to an external file 
     * to keep a permanent record of which specific seeds caused failures for later debugging.
     * * @throws IOException If there is an issue creating or writing to the log file.
     */
    @Test
    public void testExploreFiftyRandomSeedsWithContinuousTracking() throws IOException {
        
        // Check if the 'logs' folder exists at the root of the project.
        // If it doesn't, create it automatically so the FileWriter doesn't throw a FileNotFoundException on another machine.
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs(); 
        }

        // Set up a PrintWriter to write test results into a text file instead of the console.
        PrintWriter writer = new PrintWriter(new FileWriter("logs/simulation_results.log"));
        
        System.out.println("Test running... See logs/simulation_results.log for full details.");
        writer.println("Simulation Log - " + java.time.LocalDateTime.now());
        
        // Initialize random number generator and set test limit to 50 runs.
        Random randomGenerator = new Random();
        int totalRuns = 50;
        
        // Keep a list of any seeds that cause the simulation to fail, so we can debug/ test the pathfinding logic using those specific maps later.
        List<Long> failedSeeds = new ArrayList<>();

        // Save the original error stream so we can restore it later
        PrintStream originalErr = System.err;

        // Run the test according to the totalRuns.
        for (int i = 1; i <= totalRuns; i++) {
            long currentSeed = randomGenerator.nextLong(); 
            
            // Create a temporary stream to catch error messages from GameState
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            
            String status;

            try {
                // Redirect error output to our temporary stream
                System.setErr(new PrintStream(errContent));

                // "-s" is the flag for setting the map seed, followed by the actual seed value.
                String[] args = {"-s", String.valueOf(currentSeed)};
                
                // Call the main entry point to run the full simulation
                main.TXTmain.main(args); 
                
                // Read whatever the game printed to the error stream and determine the status
                String errorLogs = errContent.toString().trim();
                status = determineStatus(errorLogs);
                
                // If the status isn't exactly SUCCESS, record the seed as a failure
                if (!status.equals("SUCCESS")) {
                    failedSeeds.add(currentSeed);
                }
                
            } catch (Exception e) {
                // If the simulation throws a hard Java error (crash)
                status = "CRASHED (Java Exception)";
                failedSeeds.add(currentSeed);
            } finally {
                // Always restore the normal console error output after every single run
                System.setErr(originalErr);
            }

            // Write the formatted outcome to the log file
            writer.println(String.format("Run %02d: %-35s (Seed: %d)", i, status, currentSeed));
        }

        // Print a clean summary at the bottom of the text file for easy reading.
        writer.println("\n--- SUMMARY ---");
        writer.println("Total Runs with Issues: " + failedSeeds.size());
        
        if (!failedSeeds.isEmpty()) {
            writer.println("Seeds to investigate: " + failedSeeds.toString());
        }
        
        // Always close the writer to save the file and free up system resources.
        writer.close(); 

        // Custom message to show failures and direct the reader to the log file.
        assertTrue(failedSeeds.isEmpty(), 
            "Simulation encountered issues on " + failedSeeds.size() + " seeds. Check logs/simulation_results.log");
    }

    /**
     * Analyzes the error output from GameState to categorize the exact failure reason.
     */
    private String determineStatus(String errLogs) {
        if (errLogs.isEmpty()) {
            return "SUCCESS"; // No errors printed means both phases finished correctly
        }
        
        // Explore phase failures
        if (errLogs.contains("Your solution to explore returned at the wrong location")) {
            return "EXPLORE_FAILED (Wrong Location)";
        }
        if (errLogs.contains("Your code caused an error  during the explore phase")) {
            return "EXPLORE_FAILED (Exception Caught)";
        }
        
        // Escape phase failures
        if (errLogs.contains("Your code caused an error during the escape phase")) {
            return "ESCAPE_FAILED (Exception Caught)";
        }
        if (errLogs.contains("Your solution to escape ran out of steps before returning")) {
            return "ESCAPE_FAILED (Time Out)";
        }
        if (errLogs.contains("Your solution to escape failed to end at the stairs")) {
            return "ESCAPE_FAILED (Wrong Location)";
        }

        // Catch-all for any unknown errors printed to System.err
        return "FAILED (Unknown Error)";
    }    
}
