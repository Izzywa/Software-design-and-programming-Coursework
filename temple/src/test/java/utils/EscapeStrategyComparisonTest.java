package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import game.MockGameState;
import student.escape.EscapeStrategyFactory;

/**
 * Utility class that compares escape strategies across a set of
 *  random seeds and saves the results to CSV.
 */
public class EscapeStrategyComparisonTest {
    /**
     * Compares escape strategies across a set of random seeds and saves the
     * results to CSV.
     */
    public void testEscapeStrategyAndSaveResults() {
        final int numSeeds = 500;
        final long randomSeed = 123456789L;

        List<EscapeStrategyFactory.Strategy> strategies = new ArrayList<>(
                Arrays.asList(EscapeStrategyFactory.Strategy.values()));

        String filename = "escape_strategy_comparison.csv";
        String[] headers = {
            "Strategy", "Seed", "Gold Collected", "Time Given", "Time Taken"
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

                long startTime = System.currentTimeMillis();
                state.escape();
                long endTime = System.currentTimeMillis();

                results.add(
                        new String[] {
                                strategy.getName(),
                                String.valueOf(seed),
                                String.valueOf(state.getGoldCollected()),
                                String.valueOf(state.computeTimeToEscape()),
                                String.valueOf(endTime - startTime)
                        });
            }
        }
        LogToCsv.saveToCsv(filename, headers, results);
    }
}
