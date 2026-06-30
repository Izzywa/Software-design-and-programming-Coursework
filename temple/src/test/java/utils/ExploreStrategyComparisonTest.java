package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import game.MockGameState;
import student.explore.ExploreStrategyFactory;
import student.explore.ExploreStrategyFactory.Strategy;

/**
 * Utility class that compares explore strategies across a set of
 * random seeds and saves the results to CSV.
 */
public class ExploreStrategyComparisonTest {

    /**
     * Compares explore strategies across a set of random seeds and saves the
     * results to CSV.
     */
    public void testExploreStrategyAndSaveMultiplier() {
        final int numSeeds = 500;
        final long randomSeed = 123456789L;

        List<Strategy> strategies = new ArrayList<>(
                Arrays.asList(ExploreStrategyFactory.Strategy.values()));

        String filename = "explore_strategy_comparison.csv";
        String[] headers = {"Strategy", "Seed", "Bonus Factor"};
        List<String[]> results = new ArrayList<>();

        Random random = new Random(randomSeed);
        List<Long> seeds = new ArrayList<>();
        for (int i = 0; i < numSeeds; i++) {
            seeds.add(random.nextLong());
        }

        for (Strategy strategy : strategies) {
            for (long seed : seeds) {
                MockGameState state = new MockGameState(seed, false);
                state.explorer.setExploreStrategy(
                        ExploreStrategyFactory.getExploreStrategy(strategy));

                state.explore();
                double bonusFactor = state.computeBonusFactor();
                results.add(
                        new String[] {
                                strategy.getName(),
                                String.valueOf(seed),
                                String.valueOf(bonusFactor)
                        });
            }
        }
        LogToCsv.saveToCsv(filename, headers, results);
    }
}
