package student.sort;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Sorts a list in random order for the items in the list that is the same
 * value.s
 * If the items are different values, they will be sorted in ascending order.
 */
public class RandomSort implements SortingStrategy {

    /**
     * Sorts a list in random order for the items in the list that is the same
     * values.
     * Otherwise, the items will be sorted in ascending order.
     * @param list the list to sort
     * @param <T> the type of elements in the list
     * @return a new list sorted in random order for the items
     * in the list that is the same value.
     */
    @Override
    public <T extends Comparable<T>> List<T> sort(List<T> list) {
        Random random = new Random();

        return list.stream()
                .map(item -> new RandomPair<>(item, random.nextDouble()))
                .sorted((a, b) -> {
                    int compare = a.value.compareTo(b.value);
                    if (compare != 0)
                        return compare; // Keep original sorting order
                    return Double.compare(a.randomScore, b.randomScore); // Shuffle identical values
                })
                .map(pair -> pair.value)
                .collect(Collectors.toList());
    }

    /**
     * A helper class to hold a value and its associated random score.
     * @param <T> the type of the value
     */
    private static class RandomPair<T> {
        T value;
        double randomScore;

        RandomPair(T value, double randomScore) {
            this.value = value;
            this.randomScore = randomScore;
        }
    }
}
