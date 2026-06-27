import org.junit.jupiter.api.Test;

import student.Explorer;
import student.explore.AStarExploreStrategy;
import student.explore.ExploreStrategy;
import student.explore.ExploreStrategyFactory;
import student.explore.HeuristicDFSExploreStrategy;
import student.explore.NaiveDFSExploreStrategy;

import static org.junit.jupiter.api.Assertions.*;

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

      assertInstanceOf(HeuristicDFSExploreStrategy.class, explorer.getExploreStrategy(), "The default exploration strategy should be an instance of HeuristicDFSExploreStrategy.");
  }

  @Test
  public void testHeuristicDFSExploreStrategyFactory() {
      assertInstanceOf(HeuristicDFSExploreStrategy.class, ExploreStrategyFactory.getExploreStrategy(
              ExploreStrategyFactory.Strategy.HeuristicDFS), "The factory should return an instance of HeuristicDFSExploreStrategy for the HeuristicDFS strategy.");
  }

  @Test
  public void testNaiveDFSExploreStrategyFactory() {
      assertInstanceOf(NaiveDFSExploreStrategy.class, ExploreStrategyFactory.getExploreStrategy(
              ExploreStrategyFactory.Strategy.NaiveDFS), "The factory should return an instance of NaiveDFSExploreStrategy for the NaiveDFS strategy.");
  }

  @Test
  public void testAStarExploreStrategyFactory() {
      assertInstanceOf(AStarExploreStrategy.class, ExploreStrategyFactory.getExploreStrategy(
              ExploreStrategyFactory.Strategy.AStar), "The factory should return an instance of AStarExploreStrategy for the AStar strategy.");
  }
}
