package student.escape;

import lombok.Getter;

/**
 * Factory class for creating instances of {@link EscapeStrategy}
 * implementations.
 */
public class EscapeStrategyFactory {

    /**
     * The enumeration of available exploration strategies.
     */
    @Getter
    public enum Strategy {
        BFS("BFS"),
        DFSAllPaths("DFSAllPaths"),
        DFSPruning("DFSPruning"),
        Dijkstra("Dijkstra"),
        KnapsackDFS("KnapsackDFS");

        private final String name;

        Strategy(String name) {
            this.name = name;
        }

    }

    /**
     * Returns an instance of the specified {@link EscapeStrategy}
     * implementation.
     *
     * @param strategyName The name of the strategy to instantiate.
     * @return An instance of the specified {@link EscapeStrategy}.
     * @throws IllegalArgumentException If the strategy name is unknown.
     */
    public static EscapeStrategy getEscapeStrategy(Strategy strategyName) {
        return switch (strategyName) {
            case BFS -> {
                yield new EscapeBreadthFirstSearch();
            }
            case DFSAllPaths -> {
                yield new EscapeDFSAllPaths();
            }
            case DFSPruning -> {
                yield new EscapeDFSPruning();
            }
            case Dijkstra -> {
                yield new EscapeDijkstra();
            }
            case KnapsackDFS -> {
                yield new EscapeKnapsackDFSBnB();
            }
            default -> throw new IllegalArgumentException(
                    "Unknown strategy: " + strategyName);
        };
    }
}
