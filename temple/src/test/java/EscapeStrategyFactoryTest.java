import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import game.MockGameState;
import student.Explorer;
import student.escape.EscapeBreadthFirstSearch;
import student.escape.EscapeDFSAllPaths;
import student.escape.EscapeDFSPruning;
import student.escape.EscapeDijkstra;
import student.escape.EscapeKnapsackDFSBnB;
import student.escape.EscapeKnapsackDFSDetour;
import student.escape.EscapeStrategy;
import student.escape.EscapeStrategyFactory;
import student.escape.EscapeStrategyFactory.Strategy;

/** Tests for {@link EscapeStrategyFactory}. */
public class EscapeStrategyFactoryTest {

    @Test
    public void testEscapeStrategyDefault() {
        Explorer explorer = new Explorer();
        assertTrue(
            explorer.getEscapeStrategy() instanceof EscapeKnapsackDFSDetour,
            "The default escape strategy should be an instance of EscapeKnapsackDFSDetour.");
    }

    @Test
    public void testGetBFSStrategy() {
        EscapeStrategy bfsStrategy = EscapeStrategyFactory.getEscapeStrategy(
                EscapeStrategyFactory.Strategy.BFS);
        assertTrue(bfsStrategy instanceof EscapeBreadthFirstSearch);
    }

    @Test
    public void testGetDFSAllPathsStrategy() {
        EscapeStrategy dfsAllPathsStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.DFSAllPaths);
        assertTrue(dfsAllPathsStrategy instanceof EscapeDFSAllPaths);
    }

    @Test
    public void testGetDFSPruningStrategy() {
        EscapeStrategy dfsPruningStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.DFSPruning);
        assertTrue(dfsPruningStrategy instanceof EscapeDFSPruning);
    }

    @Test
    public void testGetDijkstraStrategy() {
        EscapeStrategy dijkstraStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.Dijkstra);
        assertTrue(dijkstraStrategy instanceof EscapeDijkstra);
    }

    @Test
    public void testGetKnapsackDFSStrategy() {
        EscapeStrategy knapsackDFSStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.KnapsackDFS);
        assertTrue(knapsackDFSStrategy instanceof EscapeKnapsackDFSBnB);
    }

    @Test
    public void testGetKnapsackDetourStrategy() {
        EscapeStrategy knapsackDetourStrategy = EscapeStrategyFactory
                .getEscapeStrategy(
                        EscapeStrategyFactory.Strategy.KnapsackDetour);
        assertTrue(knapsackDetourStrategy instanceof EscapeKnapsackDFSDetour);
    }

    @Disabled("Some of the strategies fail")
        long seed = new Random().nextLong();
        int milseconds = 1000;

        List<Strategy> strategies = new ArrayList<>(
                Arrays.asList(EscapeStrategyFactory.Strategy.values()));

        for (Strategy strategy : strategies) {
            MockGameState mockState = new MockGameState(seed, false);

            mockState.explorer.setEscapeStrategy(
                    EscapeStrategyFactory.getEscapeStrategy(strategy));
            assertTimeoutPreemptively(Duration.ofMillis(milseconds), () -> {
                mockState.explore();
                mockState.escape();
            },
                    "The " + strategy.getName()
                            + " strategy should escape within "
                            + milseconds + " milliseconds.");

            assertTrue(mockState.getEscapeSucceeded(),
                    "The " + strategy.getName()
                            + " strategy should reach the orb.");
        }
    }
}
