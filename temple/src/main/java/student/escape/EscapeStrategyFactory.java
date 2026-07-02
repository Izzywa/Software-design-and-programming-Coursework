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
        DFSPruning("DFSPruning"),
        Dijkstra("Dijkstra"),
        KnapsackSimple("KnapsackSimple"),
        KnapsackDetour("KnapsackDetour");

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
            case Dijkstra -> new EscapeDijkstra();
            case KnapsackSimple -> new EscapeKnapsackDFSSimple();
            case KnapsackDetour ->  new EscapeKnapsackDFSDetour();
            case DFSPruning -> new EscapeDFSPruning();
        };
    }
}
