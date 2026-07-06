import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import game.MockGameState;
import student.Explorer;
import student.explore.AStarExploreStrategy;
import student.explore.BeamSearchExploreStrategy;
import student.explore.BreadthFirstExploreStrategy;
import student.explore.DynamicAStarExploreStrategy;
import student.explore.ExploreStrategy;
import student.explore.ExploreStrategyFactory;
import student.explore.ExploreStrategyFactory.Strategy;
import student.explore.HeuristicDFSExploreStrategy;
import student.explore.HillClimbingExploreStrategy;
import student.explore.NaiveDFSExploreStrategy;
import student.explore.RandomWalkExploreStrategy;
import student.sort.RandomSort;

/** Tests for {@link ExploreStrategyFactory}. */
public class ExploreStrategyFactoryTest {
    /**
     * Verifies that the explorer accepts and stores a replacement exploration strategy.
     */
    @Test
    public void testExploreStrategySetter() {
        Explorer explorer = new Explorer();

        ExploreStrategy newStrategy = ExploreStrategyFactory
                .getExploreStrategy(ExploreStrategyFactory.Strategy.NaiveDFS);

        explorer.setExploreStrategy(newStrategy);

        assertEquals(newStrategy, explorer.getExploreStrategy(),
                "The setter should update the exploration strategy.");

        ExploreStrategy anotherStrategy = ExploreStrategyFactory
                .getExploreStrategy(ExploreStrategyFactory.Strategy.AStar);

        explorer.setExploreStrategy(anotherStrategy);

        assertEquals(anotherStrategy, explorer.getExploreStrategy(),
                "The setter should update the exploration strategy to the new value.");
    }

    /**
     * Verifies the explorer's default exploration strategy.
     */
    @Test
    public void testExploreStrategyDefault() {
        Explorer explorer = new Explorer();

        assertTrue(explorer.getExploreStrategy() instanceof DynamicAStarExploreStrategy,
                "The default exploration strategy should be an instance of DynamicAStarExploreStrategy.");
    }

    /**
     * Verifies that the factory creates the heuristic DFS strategy.
     */
    @Test
    public void testHeuristicDFSExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.HeuristicDFS) instanceof HeuristicDFSExploreStrategy,
                "The factory should return an instance of HeuristicDFSExploreStrategy for the HeuristicDFS strategy.");
    }

    /**
     * Verifies that the factory creates the naive DFS strategy.
     */
    @Test
    public void testNaiveDFSExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.NaiveDFS) instanceof NaiveDFSExploreStrategy,
                "The factory should return an instance of NaiveDFSExploreStrategy for the NaiveDFS strategy.");
    }

    /**
     * Verifies that the factory creates the A* exploration strategy.
     */
    @Test
    public void testAStarExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.AStar) instanceof AStarExploreStrategy,
                "The factory should return an instance of AStarExploreStrategy for the AStar strategy.");

    }

    /**
     * Verifies that the random-sort heuristic DFS option uses a random sorting strategy.
     */
    @Test
    public void testDfsRandomSortExploreStrategyFactory() {
        ExploreStrategy strategy = ExploreStrategyFactory
                .getExploreStrategy(ExploreStrategyFactory.Strategy.HeuristicDFSWithRandomSort);

        assertTrue(
                strategy instanceof HeuristicDFSExploreStrategy,
                "The factory should return an instance of HeuristicDFSExploreStrategy for the HeuristicDFSWithRandomSort strategy.");

        assertTrue(
                ((HeuristicDFSExploreStrategy) strategy).getSortingStrategy() instanceof RandomSort,
                "The factory should return an instance of HeuristicDFSWithRandomSort with a RandomSort sorting strategy.");
    }

    /**
     * Verifies that the factory creates the beam search strategy.
     */
    @Test
    public void testBeamSearchExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.BeamSearch) instanceof BeamSearchExploreStrategy,
                "The factory should return an instance of BeamSearchExploreStrategy for the BeamSearch strategy.");
    }

    /**
     * Verifies that the factory creates the breadth-first exploration strategy.
     */
    @Test
    public void testBFSExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.BFS) instanceof BreadthFirstExploreStrategy,
                "The factory should return an instance of BreadthFirstExploreStrategy for the BFS strategy.");
    }

    /**
     * Verifies that the factory creates the dynamic A* exploration strategy.
     */
    @Test
    public void testDynamicAStarExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.DynamicAStar) instanceof DynamicAStarExploreStrategy,
                "The factory should return an instance of DynamicAStarExploreStrategy for the DynamicAStar strategy.");
    }

    /**
     * Verifies that the factory creates the hill-climbing exploration strategy.
     */
    @Test
    public void testHillClimbingExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.HillClimbing) instanceof HillClimbingExploreStrategy,
                "The factory should return an instance of HillClimbingExploreStrategy for the HillClimbing strategy.");
    }

    /**
     * Verifies that the factory creates the random-walk exploration strategy.
     */
    @Test
    public void testRandomWalkExploreStrategyFactory() {
        assertTrue(
                ExploreStrategyFactory.getExploreStrategy(
                        ExploreStrategyFactory.Strategy.RandomWalk) instanceof RandomWalkExploreStrategy,
                "The factory should return an instance of RandomWalkExploreStrategy for the RandomWalk strategy.");
    }

    /**
     * Verifies that every registered strategy can reach the orb within the timeout.
     */
    @RepeatedTest(10)
    public void testAllStrategiesSucceedInReachingOrb() {
        long seed = new Random().nextLong();
        int milseconds = 10000;

        List<Strategy> strategies = new ArrayList<>(Arrays.asList(ExploreStrategyFactory.Strategy.values()));

        for (Strategy strategy : strategies) {
            MockGameState mockState = new MockGameState(seed, false);

            assertTimeoutPreemptively(Duration.ofMillis(milseconds), () -> {
                mockState.explorer.setExploreStrategy(
                        ExploreStrategyFactory.getExploreStrategy(strategy)
                );

                mockState.explore();
            },
                    "The " + strategy.getName()
                            + " strategy should reach the orb within "
                            + (milseconds / 1000)
                            + " seconds.");

            assertTrue(mockState.getExploreSucceeded(),
                    "The " + strategy.getName() + " strategy should reach the orb.");
        }
    }
}
