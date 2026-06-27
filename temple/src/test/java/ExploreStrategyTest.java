import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import game.ExplorationState;
import game.MockGameState;
import student.explore.ExploreStrategy;

import student.explore.HeuristicDFSExploreStrategy;
import student.explore.NaiveDFSExploreStrategy;
import student.explore.AStarExploreStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests exploration strategies independently from the main {@code Explorer}
 * implementation.
 */
public class ExploreStrategyTest {
    private static final Path ONE_PATH_EXPLORE_PATH = Path.of(
        "src/test/resources/one_path_explore.txt"
    );

    private static final Path BACKTRACK_EXPLORE_PATH = Path.of(
        "src/test/resources/backtrack_explore.txt"
    );

    private static final Path SAMPLE_12X12_EXPLORE_PATH = Path.of(
        "src/test/resources/sample_12x12_explore.txt"
    );

    private static final Path DUMMY_ESCAPE_PATH = Path.of(
        "src/test/resources/dummy_escape.txt"
    );

    /**
     * Verifies every registered strategy can solve the one-path fixture.
     */
    @Test
    public void testExploreStrategyOnePath() {
        testExploreStrategyPath(ONE_PATH_EXPLORE_PATH);
    }

    /**
     * Verifies every registered strategy can solve the backtracking fixture.
     */
    @Test
    public void testExploreStrategyBacktrack() {
        testExploreStrategyPath(BACKTRACK_EXPLORE_PATH);
    }

    /**
     * Verifies every registered strategy can solve the sample 12x12 fixture.
     * Note by JY: A sample 12x12 fixture is chosen to avoid overly long
     * test times on less efficient explore strategies that are not even
     * used in the final Explorer class. This is for simple sanity check
     * purposes only.
     */
    @Test
    public void testExploreStrategySample12x12() {
        testExploreStrategyPath(SAMPLE_12X12_EXPLORE_PATH);
    }

    private static void testExploreStrategyPath(Path explorePath) {
        for (Strategy strategy : getStrategies()) {
            MockGameState state = new MockGameState(
                explorePath,
                DUMMY_ESCAPE_PATH,
                false
            );

            MockExplorer explorer = new MockExplorer();
            explorer.explore(state, strategy.getExploreStrategy());

            assertEquals(
                0,
                state.getDistanceToTarget(),
                String.format(
                    "%s did not finish on the orb for %s",
                    strategy.name,
                    explorePath
                )
            );
        }
    }

    private static Strategy[] getStrategies() {
        return new Strategy[] {
            new Strategy("HeuristicDFSExploreStrategy") {
                @Override
                ExploreStrategy getExploreStrategy() {
                    return new HeuristicDFSExploreStrategy();
                }
            },
            new Strategy("NaiveDFSExploreStrategy") {
                @Override
                ExploreStrategy getExploreStrategy() {
                    return new NaiveDFSExploreStrategy();
                }
            },
            new Strategy("AStarExploreStrategy") {
                @Override
                ExploreStrategy getExploreStrategy() {
                    return new AStarExploreStrategy();
                }
            },
        };
    }

    /**
     * Describes an {@link ExploreStrategy} implementation to run in this test.
     */
    private abstract static class Strategy {
        private final String name;

        private Strategy(String name) {
            this.name = name;
        }

        /**
         * Creates a fresh strategy instance for a single exploration run.
         *
         * @return a new strategy instance
         */
        abstract ExploreStrategy getExploreStrategy();
    }

    /**
     * Minimal explorer wrapper that delegates directly to an exploration
     * strategy.
     */
    private static class MockExplorer {
        /**
         * Runs the supplied strategy against the supplied exploration state.
         *
         * @param state the exploration state fixture
         * @param strategy the strategy under test
         */
        void explore(ExplorationState state, ExploreStrategy strategy) {
            strategy.explore(state);
        }
    }
}
