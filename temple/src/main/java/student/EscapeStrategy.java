package student;

import game.EscapeState;

public interface EscapeStrategy {
    public abstract EscapePath findEscapePath(EscapeState state);
}
