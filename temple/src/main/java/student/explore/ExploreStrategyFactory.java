package student.explore;

import java.util.List;

/**
 * Factory class for creating instances of {@link ExploreStrategy} implementations.
 */
public class ExploreStrategyFactory {
  /**
   * Returns an instance of the specified {@link ExploreStrategy} implementation.
   *
   * @param strategyName The name of the strategy to instantiate.
   * @return An instance of the specified {@link ExploreStrategy}.
   * @throws IllegalArgumentException If the strategy name is unknown.
   */
  public enum Strategy {
    HeuristicDFS("HeuristicDFS"), NaiveDFS("NaiveDFS"), AStar("AStar");

    private final String name;

    Strategy(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public static List<Strategy> getAllStrategies() {
      return List.of(HeuristicDFS, NaiveDFS, AStar);
    }
  }

  /**
   * Returns an instance of the specified {@link ExploreStrategy} implementation.
   *
   * @param strategyName The name of the strategy to instantiate.
   * @return An instance of the specified {@link ExploreStrategy}.
   * @throws IllegalArgumentException If the strategy name is unknown.
   */
  public static ExploreStrategy getExploreStrategy(Strategy strategyName) {
    switch (strategyName) {
      case HeuristicDFS -> {
        return new HeuristicDFSExploreStrategy();
      }
      case NaiveDFS -> {
        return new NaiveDFSExploreStrategy();
      }
      case AStar -> {
        return new AStarExploreStrategy();
      }
      default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
    }
  }
}
