import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import student.explore.HeuristicDFSExploreStrategy;
import student.sort.AscendingSort;

/**
 * Test the SortingStrategy in HeuristicDFSExploreStrategy.
 */
public class DfsSortingTest {
  @Test
  public void testDefaultSortingStrategy() {
    HeuristicDFSExploreStrategy strategy = new HeuristicDFSExploreStrategy();

    assertTrue(strategy.getSortingStrategy() instanceof AscendingSort,
        "Default sorting strategy should be AscendingSort");
  }

}
