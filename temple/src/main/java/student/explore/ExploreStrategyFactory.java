package student.explore;

import lombok.Getter;

/**
 * Factory class for creating instances of {@link ExploreStrategy} implementations.
 */
public class ExploreStrategyFactory {
  /**
   * Returns an instance of the specified {@link ExploreStrategy} implementation.
   */
  @Getter
  public enum Strategy {
    HeuristicDFS("HeuristicDFS"), NaiveDFS("NaiveDFS"), AStar("AStar");

    private final String name;

    Strategy(String name) {
      this.name = name;
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
    return switch (strategyName) {
      case HeuristicDFS -> {
        yield new HeuristicDFSExploreStrategy();
      }
      case NaiveDFS -> {
        yield new NaiveDFSExploreStrategy();
      }
      case AStar -> {
        yield new AStarExploreStrategy();
      }
      default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
    };
  }
}
