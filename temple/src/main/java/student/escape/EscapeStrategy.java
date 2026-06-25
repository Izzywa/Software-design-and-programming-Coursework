package student.escape;

import game.EscapeState;

/**
 * A strategy for escaping the cavern using the EscapeState game state.
 */
public interface EscapeStrategy {
    /**
     * Escape the cavern before it collapses while also collecting gold along the way.
     *
     * @param state the current escape state
     */
    public default void escape(EscapeState state) {
        EscapePath escapePath = findEscapePath(state);
        escapePath.traverseAndCollect();
    }

    /**
     * Find an escape path from the cavern before it collapses.
     *
     * @param state the current escape state
     */
    public abstract EscapePath findEscapePath(EscapeState state);
}
