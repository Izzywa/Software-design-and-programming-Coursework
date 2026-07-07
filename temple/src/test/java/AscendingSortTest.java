import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import student.sort.AscendingSort;

/**
 * Tests for {@link AscendingSort}.
 */
public class AscendingSortTest {
    /**
     * Verifies that the ascending sorter returns values in ascending order.
     */
    @Test
    public void testAscendingSort() {
        AscendingSort ascendingSort = new AscendingSort();

        List<Integer> list = List.of(5, 2, 9, 1, 5, 6);
        List<Integer> sortedList = ascendingSort.sort(list);
        List<Integer> expectedList = List.of(1, 2, 5, 5, 6, 9);

        assertEquals(expectedList, sortedList, String.format("Expected %s, but got %s", expectedList, sortedList));
    }
}
