import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import benchmark.LogToCsv;
import game.MockGameState;
import student.explore.ExploreStrategyFactory;
import student.explore.ExploreStrategyFactory.Strategy;

/**
 * Compares explore strategies across a set of random seeds and saves the
 * results to CSV.
 */
public class ExploreStrategyComparisonTest {

  private final int NUM_SEEDS = 100;

  @Test
  public void testExploreStrategyAndSaveMultiplier() {

    List<Strategy> strategies = new ArrayList<>(Arrays.asList(ExploreStrategyFactory.Strategy.values()));

    String filename = "explore_strategy_comparison.csv";
    String[] headers = { "Strategy", "Seed", "Bonus Factor" };
    List<String[]> results = new ArrayList<>();

    for (Strategy strategy : strategies) {
      for (int i = 0; i < NUM_SEEDS; i++) {
        long seed = new Random().nextLong();
        
        MockGameState state = new MockGameState(seed, false);
        state.explorer.setExploreStrategy(ExploreStrategyFactory.getExploreStrategy(strategy));

        state.explore();
        double bonusFactor = state.computeBonusFactor();
        results.add(
            new String[] { strategy.getName(), String.valueOf(seed), String.valueOf(bonusFactor) });
      }
    }
    LogToCsv.saveToCsv(filename, headers, results);
  }
}
