package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import game.MockGameState;
import student.escape.EscapeStrategyFactory;
import student.escape.EscapeStrategyFactory.Strategy;

/**
 * Utility class that compares escape strategies across a set of
 * random seeds and saves the results to CSV.
 */
public class EscapeStrategyComparisonTest {
    int timeout = 30000;

    /**
     * Compares escape strategies across a set of random seeds and saves the
     * results to CSV.
     */
    public void testEscapeStrategyAndSaveResults() {
        final int numSeeds = 100;
        final long randomSeed = 123456789L;

        List<Strategy> strategies = new ArrayList<>(
                Arrays.asList(EscapeStrategyFactory.Strategy.values()));

        String filename = "escape_strategy_comparison.csv";
        String[] headers = {
                "Strategy",
                "Seed",
                "Gold Collected",
                "Time Given",
                "Time Taken",
                "Seed"
        };
        List<String[]> results = new ArrayList<>();

        Random random = new Random(randomSeed);
        List<Long> seeds = new ArrayList<>();
        for (int i = 0; i < numSeeds; i++) {
            seeds.add(random.nextLong());
        }

        for (EscapeStrategyFactory.Strategy strategy : strategies) {
            for (long seed : seeds) {
                MockGameState state = new MockGameState(seed, false);
                state.explorer.setEscapeStrategy(
                        EscapeStrategyFactory.getEscapeStrategy(strategy));

                state.explore();

                long timeTaken = timeTakenToEscape(state);

                results.add(
                        new String[] {
                                strategy.getName(),
                                String.valueOf(seed),
                                String.valueOf(
                                        timeTaken == timeout
                                                ? 0
                                                : state.getGoldCollected()),
                                String.valueOf(state.computeTimeToEscape()),
                                String.valueOf(timeTaken),
                                String.valueOf(seed)
                        });
            }
        }
        LogToCsv.saveToCsv(filename, headers, results);
    }

    /**
     * Times how long it takes to escape from the game state.
     * The escape is given 30 seconds to complete, after which it is terminated.
     * This is to prevent the test from hanging indefinitely.
     * @param state the game state to escape from
     * @return the time taken to escape in milliseconds
     */
    private long timeTakenToEscape(MockGameState state) {
        long startTime = System.currentTimeMillis();
        try {
            Thread escapeThread = new Thread(state::escape);
            escapeThread.setDaemon(true);
            escapeThread.start();
            escapeThread.join(timeout);

            if (escapeThread.isAlive()) {
                escapeThread.interrupt();
                throw new RuntimeException(
                        "Escape took too long and was terminated.");
            }
        } catch (RuntimeException | InterruptedException e) {
            System.out.println("Escape took too long and was terminated.");
            return timeout;
        }
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}