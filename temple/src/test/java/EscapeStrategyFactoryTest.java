import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import game.MockGameState;
import student.Explorer;
import student.escape.DFSPruningEscapeStrategy;
import student.escape.DijkstraEscapeStrategy;
import student.escape.EscapeStrategy;
import student.escape.EscapeStrategyFactory;
import student.escape.EscapeStrategyFactory.Strategy;
import student.escape.KnapsackDFSDetourEscapeStrategy;
import student.escape.KnapsackDFSSimpleEscapeStrategy;

/** Tests for {@link EscapeStrategyFactory}. */
public class EscapeStrategyFactoryTest {

    @Test
    public void testEscapeStrategyDefault() {
        Explorer explorer = new Explorer();
        assertTrue(
            explorer.getEscapeStrategy() instanceof KnapsackDFSDetourEscapeStrategy,
            "The default escape strategy should be an instance of EscapeKnapsackDFSDetour.");
    }

    @Test
    public void testGetDFSPruningStrategy() {
        EscapeStrategy dfsPruningStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.DFSPruning);
        assertTrue(dfsPruningStrategy instanceof DFSPruningEscapeStrategy);
    }

    @Test
    public void testGetDijkstraStrategy() {
        EscapeStrategy dijkstraStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.Dijkstra);
        assertTrue(dijkstraStrategy instanceof DijkstraEscapeStrategy);
    }

    @Test
    public void testGetKnapsackDFSStrategy() {
        EscapeStrategy knapsackDFSStrategy = EscapeStrategyFactory
                .getEscapeStrategy(EscapeStrategyFactory.Strategy.KnapsackSimple);
        assertTrue(knapsackDFSStrategy instanceof KnapsackDFSSimpleEscapeStrategy);
    }

    @Test
    public void testGetKnapsackDetourStrategy() {
        EscapeStrategy knapsackDetourStrategy = EscapeStrategyFactory
                .getEscapeStrategy(
                        EscapeStrategyFactory.Strategy.KnapsackDetour);
        assertTrue(knapsackDetourStrategy instanceof KnapsackDFSDetourEscapeStrategy);
    }

    @RepeatedTest(10)
    public final void testAllStrategiesSucceedInEscaping() {
        long seed = new Random().nextLong();
        int milseconds = 60000;

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
                            + (milseconds / 1000)
                            + " seconds.");

            assertTrue(mockState.getEscapeSucceeded(),
                    "The " + strategy.getName()
                            + " strategy should reach the orb.");
        }
    }
}
