import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Testing by way of simulation of running TXTmain.
 * Automated tests to ensure the Explore and Escape phases execute without fail
 * over multiple randomised runs.
 */
public class SimulationTest {

    /**
     * Automated continuous test.
     * Runs the simulation 50 times using randomly generated seeds.
     * Results are logged to an external file and 
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

        // Set up a PrintWriter to write our test results into a text file instead of the console.
        PrintWriter writer = new PrintWriter(new FileWriter("logs/simulation_results.log"));
        
        System.out.println("Test running... See logs/simulation_results.log for full details.");
        writer.println("Simulation Log - " + java.time.LocalDateTime.now());
        
        // Initialize our random number generator and set our test limit to 50 runs.
        Random randomGenerator = new Random();
        int totalRuns = 50;
        
        // Keep a list of any seeds that cause the game to fail, so we can debug/ test the pathfinding logic using those specific maps later.
        List<Long> failedSeeds = new ArrayList<>();

        // Run the test according to the totalRuns.
        for (int i = 1; i <= totalRuns; i++) {
            long currentSeed = randomGenerator.nextLong(); 
            
            try {
                // Set up the command line arguments exactly as if a user typed them into the terminal.
                // "-s" is the flag for setting the map seed, followed by the actual seed value.
                String[] args = {"-s", String.valueOf(currentSeed)};
                
                // Call the main entry point to run the full simulation (Explore + Escape) in headless mode.
                main.TXTmain.main(args); 
                
                // If without an exception, the run finished successfully.
                writer.println("Run " + i + ": SUCCESS (Seed: " + currentSeed + ")");
                
            } catch (Exception e) {
                // If the simulation throws any errors, catch it here and continiue with the remaining run.
                writer.println("Run " + i + ": FAILED  (Seed: " + currentSeed + ")");
                failedSeeds.add(currentSeed); // Save the seed in the log for failed run.
            }
        }

        // Print a clean summary at the bottom of the text file for easy reading.
        writer.println("\n--- SUMMARY ---");
        writer.println("Total Failures: " + failedSeeds.size());
        
        if (!failedSeeds.isEmpty()) {
            writer.println("Failing Seeds: " + failedSeeds.toString());
        }
        
        // Always close the writer to save the file and free up system resources.
        writer.close(); 

        // Custom message to show failed and direct the reader to the log file.
        assertTrue(failedSeeds.isEmpty(), "Simulation failed on " + failedSeeds.size() + " seeds. Check logs/simulation_results.log");
    }
}