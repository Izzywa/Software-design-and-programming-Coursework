import game.GameState;
import org.junit.jupiter.api.RepeatedTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGameCanRun {

    @RepeatedTest(50)
    void canRunTheGame() throws Exception {
        long seed = new Random().nextLong();
        GameState state = newGameState(seed);

        assertTrue(canExplore(state),"explore phase should end on the orb");

        assertTrue(canEscape(state), "escape phase should end on the stairs");
    }

    private static GameState newGameState(long seed) throws Exception {
        Constructor<GameState> constructor = GameState.class.getDeclaredConstructor(long.class, boolean.class);
        constructor.setAccessible(true);
        return constructor.newInstance(seed, false);
    }

    private static Method exploreSucceededMethod() {
        try {
            Method method = GameState.class.getDeclaredMethod("getExploreSucceeded");
            method.setAccessible(true);
            return method;
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getExploreMethod() {
        try {
            Method method = GameState.class.getDeclaredMethod("explore");
            method.setAccessible(true);
            return method;
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean canExplore(GameState state) {
        try {
            Method exploreMethod = getExploreMethod();
            exploreMethod.invoke(state);
            Method exploreSucceeded = exploreSucceededMethod();
            return (boolean) exploreSucceeded.invoke(state);
        }
        catch (Exception e) {
            return false;
        }
    }

    private static Method getEscapeSucceededMethod() {
        try {
            Method method = GameState.class.getDeclaredMethod("getEscapeSucceeded");
            method.setAccessible(true);
            return method;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getEscapeMethod() {
        try
        {
            Method method = GameState.class.getDeclaredMethod("escape");
            method.setAccessible(true);
            return method;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean canEscape(GameState state) {
        try {
            Method escapeMethod = getEscapeMethod();
            escapeMethod.invoke(state);
            Method escapeSucceeded = getEscapeSucceededMethod();
            return (boolean) escapeSucceeded.invoke(state);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
