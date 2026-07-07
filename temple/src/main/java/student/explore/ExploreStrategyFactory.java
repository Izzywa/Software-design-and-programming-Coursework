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
        /** Heuristic depth-first search with ascending distance ordering. */
        HeuristicDFS("HeuristicDFS"),

        /** Plain depth-first search using the game-provided neighbor order. */
        NaiveDFS("NaiveDFS"),

        /** A* search using distance from the start plus orb distance as priority. */
        AStar("AStar"),

        /** Heuristic depth-first search with randomized neighbor ordering. */
        HeuristicDFSWithRandomSort("HeuristicDFSWithRandomSort"),

        /** Beam search that keeps only the best-scoring discovered candidates. */
        BeamSearch("BeamSearch"),

        /** Breadth-first search over the discovered graph. */
        BFS("BFS"),

        /** A* variant that recomputes path cost from the current location. */
        DynamicAStar("DynamicAStar"),

        /** Greedy hill-climbing with backtracking when progress stalls. */
        HillClimbing("HillClimbing"),

        /** Random exploration with backtracking over previously visited nodes. */
        RandomWalk("RandomWalk");

        /** Display name used by tests and selection code. */
        private final String name;

        /**
         * Create a strategy enum entry with its display name.
         *
         * @param name the display name associated with the strategy
         */
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
