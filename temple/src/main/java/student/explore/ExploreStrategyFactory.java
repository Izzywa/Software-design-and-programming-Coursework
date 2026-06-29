package student.explore;

import lombok.Getter;
import student.sort.RandomSort;

/**
 * Factory class for creating instances of {@link ExploreStrategy} implementations.
 */
public class ExploreStrategyFactory {
  /**
   * The enumeration of available exploration strategies.
   */
  @Getter
  public enum Strategy {
    HeuristicDFS("HeuristicDFS"), 
    NaiveDFS("NaiveDFS"), 
    AStar("AStar"),
    HeuristicDFSWithRandomSort("HeuristicDFSWithRandomSort"),
    BeamSearch("BeamSearch"),
    BFS("BFS"),
    DynamicAStar("DynamicAStar"),
    HillClimbing("HillClimbing"),
    RandomWalk("RandomWalk");

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
      case HeuristicDFSWithRandomSort -> {
        yield new HeuristicDFSExploreStrategy(new RandomSort());
      }
      case BeamSearch -> {
        yield new BeamSearchExploreStrategy();
      }
      case BFS -> {
        yield new BreadthFirstExploreStrategy();
      }
      case DynamicAStar -> {
        yield new DynamicAStarExploreStrategy();
      }
      case HillClimbing -> {
        yield new HillClimbingExploreStrategy();
      }
      case RandomWalk -> {
        yield new RandomWalkExploreStrategy();
      }
      default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
    };
  }
}
