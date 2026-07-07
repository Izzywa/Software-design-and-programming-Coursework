import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import student.sort.RandomSort;

/**
 * Test class for {@link RandomSort} to confirm that while it sorts in ascending order, it also shuffles identical values.
 */
public class RandomSortTest {
    /**
     * Comparable test value used to check ordering independently from tie-breaking.
     *
     * @param value the numeric sort key
     * @param letter the label used to detect shuffled ties
     */
    private record RandomPair (int value, String letter) implements Comparable<RandomPair> {
        /**
         * Compare pairs by value only so equal values may be shuffled freely.
         *
         * @param other the pair to compare against
         * @return a negative number, zero, or a positive number as this pair is
         *         less than, equal to, or greater than the other pair
         */
        @Override
        public int compareTo(RandomPair other) {
            return Integer.compare(value, other.value);
        }
    }

    /**
     * Verifies that random sort still preserves ascending order by value.
     */
    @Test
    public void testRandomSortStillSortAscending() {
        RandomSort randomSort = new RandomSort();
        List<RandomPair> list = List.of(
                new RandomPair(1, "A"),
                new RandomPair(2, "B"),
                new RandomPair(1, "C"),
                new RandomPair(3, "D"),
                new RandomPair(2, "E")
        );

        List<RandomPair> sortedList = randomSort.sort(list);

        for (int i = 1; i < sortedList.size(); i++) {
            assertTrue(sortedList.get(i - 1).compareTo(sortedList.get(i)) <= 0,
                    "List is not sorted in ascending order");
        }
    }

    /**
     * Verifies that repeated sorts can produce different orders for equal values.
     */
    @Test
    public void testRandomSortShufflesIdenticalValues() {
        RandomSort randomSort = new RandomSort();

        List<RandomPair> list = List.of(
                new RandomPair(1, "A"),
                new RandomPair(1, "B"),
                new RandomPair(1, "C"),
                new RandomPair(1, "D"),
                new RandomPair(1, "E")
        );

        List<RandomPair> sortedList1 = randomSort.sort(list);
        
        // repeat the sort 5 times to rule out the possibility of getting the same order by chance
        boolean isDifferentOrder = false;
        for (int i = 0; i < 5; i++) {
            List<RandomPair> sortedList2 = randomSort.sort(list);
            if (!sortedList1.equals(sortedList2)) {
                isDifferentOrder = true;
                break;
            }
        }

        assertTrue(isDifferentOrder, "Identical values are not shuffled in random sort");
    }
}
