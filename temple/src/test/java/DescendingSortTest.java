import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import student.sort.DescendingSort;

public class DescendingSortTest {
    @Test
    public void testDescendingSort() {
        DescendingSort descendingSort = new DescendingSort();

        List<Integer> list = List.of(5, 2, 9, 1, 5, 6);
        List<Integer> sortedList = descendingSort.sort(list);
        List<Integer> expectedList = List.of(9, 6, 5, 5, 2, 1);

        assertEquals(expectedList, sortedList, String.format("Expected %s, but got %s", expectedList, sortedList));
    }
}
