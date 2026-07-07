import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import student.explore.HeuristicDFSExploreStrategy;
import student.sort.AscendingSort;
import student.sort.RandomSort;

/**
 * Test the SortingStrategy in HeuristicDFSExploreStrategy.
 */
public class DFSSortingTest {
    /**
     * Confirms that heuristic DFS defaults to ascending neighbor ordering.
     */
    @Test
    public void testDefaultSortingStrategy() {
        HeuristicDFSExploreStrategy strategy = new HeuristicDFSExploreStrategy();

        assertTrue(strategy.getSortingStrategy() instanceof AscendingSort,
                "Default sorting strategy should be AscendingSort");
    }

    /**
     * Confirms that the custom sorting strategy constructor preserves a random sorter.
     */
    @Test
    public void testRandomSortStrategyConstructor() {
        HeuristicDFSExploreStrategy strategy = new HeuristicDFSExploreStrategy(new RandomSort());

        assertTrue(strategy.getSortingStrategy() instanceof RandomSort,
                "Custom sorting strategy should be RandomSort");
    }
}
