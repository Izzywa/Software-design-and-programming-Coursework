import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import game.MockGameState;
import student.Explorer;
import student.explore.AStarExploreStrategy;
import student.explore.ExploreStrategy;
import student.explore.ExploreStrategyFactory;
import student.explore.ExploreStrategyFactory.Strategy;
import student.explore.HeuristicDFSExploreStrategy;
import student.explore.NaiveDFSExploreStrategy;

/** Tests for {@link ExploreStrategyFactory}. */
public class ExploreStrategyFactoryTest {
  @Test
  public void testExploreStrategySetter() {
    Explorer explorer = new Explorer();

    ExploreStrategy newStrategy =
        ExploreStrategyFactory.getExploreStrategy(ExploreStrategyFactory.Strategy.NaiveDFS);

    explorer.setExploreStrategy(newStrategy);

    assertEquals(newStrategy, explorer.getExploreStrategy(),
        "The setter should update the exploration strategy.");

    ExploreStrategy anotherStrategy =
        ExploreStrategyFactory.getExploreStrategy(ExploreStrategyFactory.Strategy.AStar);

    explorer.setExploreStrategy(anotherStrategy);

    assertEquals(anotherStrategy, explorer.getExploreStrategy(),
        "The setter should update the exploration strategy to the new value.");
  }

  @Test
  public void testExploreStrategyDefault() {
    Explorer explorer = new Explorer();

    assertTrue(explorer.getExploreStrategy() instanceof HeuristicDFSExploreStrategy,
        "The default exploration strategy should be an instance of HeuristicDFSExploreStrategy.");
  }

  @Test
  public void testHeuristicDFSExploreStrategyFactory() {
    assertTrue(
        ExploreStrategyFactory.getExploreStrategy(
            ExploreStrategyFactory.Strategy.HeuristicDFS) instanceof HeuristicDFSExploreStrategy,
        "The factory should return an instance of HeuristicDFSExploreStrategy for the HeuristicDFS strategy.");
  }

  @Test
  public void testNaiveDFSExploreStrategyFactory() {
    assertTrue(
        ExploreStrategyFactory.getExploreStrategy(
            ExploreStrategyFactory.Strategy.NaiveDFS) instanceof NaiveDFSExploreStrategy,
        "The factory should return an instance of NaiveDFSExploreStrategy for the NaiveDFS strategy.");
  }

  @Test
  public void testAStarExploreStrategyFactory() {
    assertTrue(
        ExploreStrategyFactory.getExploreStrategy(
            ExploreStrategyFactory.Strategy.AStar) instanceof AStarExploreStrategy,
        "The factory should return an instance of AStarExploreStrategy for the AStar strategy.");

  }

  @RepeatedTest(50)
  public void testAllStrategiesSucceedInReachingOrb() {
    long seed = new Random().nextLong();

    List<Strategy> strategies =
        new ArrayList<>(Arrays.asList(ExploreStrategyFactory.Strategy.values()));
    
    for (Strategy strategy : strategies) {
      MockGameState mockState = new MockGameState(seed, false);

      mockState.explorer.setExploreStrategy(ExploreStrategyFactory.getExploreStrategy(strategy));
      mockState.explore();

      assertEquals(0, mockState.getDistanceToTarget(),
          "The " + strategy.getName() + " strategy should reach the orb.");
    }
  }
}
