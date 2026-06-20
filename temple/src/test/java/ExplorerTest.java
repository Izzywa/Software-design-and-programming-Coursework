import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import game.MockGameState;

public class ExplorerTest {
    private static final long[] seeds = {
        -6030386710065883781L,
        -5510290573061908338L,
        -1470501915284536918L,
        -4597737716715364520L,
        -4279636001738967398L,
        -4827998778597597300L,
        -476650471236701010L,
        -4700664647091235117L,
        -8569702676904288827L,
        -1223174312227623395L,
        -109228072190925086L,
        8373941321627401705L,
        -4430856129676541937L,
        2973620952636216003L,
        849478511168984701L,
        -1491982013190621682L,
        -3432020535710509840L,
        1826302837286649603L,
        1582682153095546457L,
        1563218599148826617L,
        -5093064752511861052L,
        -5598246820520287258L,
        1181240261008404084L,
        5828609575289425957L,
        -1983006326131866804L,
        -7846301534632239495L,
        2074749544520662127L,
        490439926682264401L,
        -709500976181275605L,
        -7138461780370279347L,
        -1375646864920288238L,
        -7008867534839307520L,
        1875691265350264681L,
        8897591032817311735L,
        -652186055178272826L,
        5201741409946798586L,
        8949487796961337332L,
        5372614946319871377L,
        -3986681301040840423L,
        -1817955391563073335L,
        -3351742447140321765L,
        -3054236072300091593L,
        -7471915603368644290L,
        -8418766960457110979L,
        -7572471800455966703L,
        2808117271838216588L,
        3291380574714216842L,
        8274252956518381545L,
        2256348683308726896L,
        4466691524055595058L,
    };

    @Test
    public void testExploreOnePath() {
        Path exploreCavernPath = Path.of(
            "src/test/resources/one_path_explore.txt"
        );

        // Note by JY: We'll not do any escaping here but we need a
        // escapeCavern file to create the MockGameState
        Path escapeCavernPath = Path.of(
            "src/test/resources/dummy_escape.txt"
        );

        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.explore();
        assertEquals(true, state.getExploreSucceeded());
        assertEquals(false, state.getExploreErrored());
    }

    @Test
    public void testExploreBacktrack() {
        Path exploreCavernPath = Path.of(
            "src/test/resources/backtrack_explore.txt"
        );

        // Note by JY: We'll not do any escaping here but we need a
        // escapeCavern file to create the MockGameState
        Path escapeCavernPath = Path.of(
            "src/test/resources/dummy_escape.txt"
        );

        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.explore();
        assertEquals(true, state.getExploreSucceeded());
        assertEquals(false, state.getExploreErrored());
    }

    @Test
    public void testEscapeShortest() {
        // Note by JY: We'll not do any exploring here but we need a
        // exploreCavern file to create the MockGameState
        // The Gem in the exploreCavern file will be the start location
        // for the escapeCaverns
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/shortest_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        assertEquals(true, state.getEscapeSucceeded());
        assertEquals(false, state.getEscapeErrored());
        assertEquals(6, state.getEscapeTimeSpent());
    }

    @Test
    public void testEscapeMaxGold() {
        // Note by JY: We'll not do any exploring here but we need a
        // exploreCavern file to create the MockGameState
        // The Gem in the exploreCavern file will be the start location
        // for the escapeCaverns
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/gold_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        assertEquals(true, state.getEscapeSucceeded());
        assertEquals(false, state.getEscapeErrored());
        assertEquals(15, state.getEscapeTimeSpent());
        assertEquals(10, state.getGoldCollected());
    }

    @Test
    public void testExplore50Seeds() {
        for (int i = 0; i < seeds.length; i++) {
            long seed = seeds[i];
            MockGameState state = new MockGameState(seed, false);
            state.explore();
            assertTrue(
                state.getExploreSucceeded(),
                String.format("Explore phase failed on seed %d", seed)
            );
            assertFalse(
                state.getExploreErrored(),
                String.format("Explore phase failed on seed %d", seed)
            );
        }
    }

    @Test
    public void testEscape50Seeds() {
        for (int i = 0; i < seeds.length; i++) {
            long seed = seeds[i];
            MockGameState state = new MockGameState(seed, false);
            state.setExploreSucceeded(true);
            state.escape();
            assertTrue(
                state.getEscapeSucceeded(),
                String.format("Escape phase failed on seed %d", seed)
            );
            assertFalse(
                state.getEscapeErrored(),
                String.format("Escape phase failed on seed %d", seed)
            );
        }
    }
}
