import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import student.Explorer;
import student.explore.ExploreStrategy;
import student.explore.ExploreStrategyFactory;

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

    ExploreStrategy defaultStrategy =
        ExploreStrategyFactory.getExploreStrategy(ExploreStrategyFactory.Strategy.HeuristicDFS);
        
    assertEquals(defaultStrategy.getClass(), explorer.getExploreStrategy().getClass(),
        "The default exploration strategy should be HeuristicDFS.");
  }
}
