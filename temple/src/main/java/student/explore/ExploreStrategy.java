package student.explore;
import game.ExplorationState;

/**
 * A strategy for exploring a cavern using the exploration game state.
 */
public interface ExploreStrategy {
    /**
     * Explore the cavern from the given state until the orb is found.
     *
     * @param state the current exploration state
     */
    public void explore(ExplorationState state);
}
