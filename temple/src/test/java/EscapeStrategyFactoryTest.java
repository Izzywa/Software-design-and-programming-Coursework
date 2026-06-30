import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import student.Explorer;
import student.escape.EscapeBreadthFirstSearch;
import student.escape.EscapeDFSAllPaths;
import student.escape.EscapeDFSPruning;
import student.escape.EscapeDijkstra;
import student.escape.EscapeKnapsackDFSBnB;
import student.escape.EscapeStrategy;
import student.escape.EscapeStrategyFactory;

public class EscapeStrategyFactoryTest {

    @Test
    public void testEscapeStrategyDefault() {
        Explorer explorer = new Explorer();
        
    }

    @Test
    public void testGetBFSStrategy() {
        EscapeStrategy bfsStrategy = EscapeStrategyFactory.getEscapeStrategy(
            EscapeStrategyFactory.Strategy.BFS
        );
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
}
