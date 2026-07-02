package student.explore;

import lombok.Getter;
import student.sort.RandomSort;

/**
 * Factory class for creating instances of {@link ExploreStrategy}
 * implementations.
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
            case HeuristicDFS -> new HeuristicDFSExploreStrategy();
            case NaiveDFS -> new NaiveDFSExploreStrategy();
            case AStar -> new AStarExploreStrategy();
            case HeuristicDFSWithRandomSort -> new HeuristicDFSExploreStrategy(new RandomSort());
            case BeamSearch -> new BeamSearchExploreStrategy();
            case BFS -> new BreadthFirstExploreStrategy();
            case DynamicAStar -> new DynamicAStarExploreStrategy();
            case HillClimbing -> new HillClimbingExploreStrategy();
            case RandomWalk -> new RandomWalkExploreStrategy();
        };
    }
}
