package student.sort;

import java.util.List;

/**
 * A strategy for sorting a list of comparable elements.
 */
public interface SortingStrategy {
    public <T extends Comparable<T>> List<T> sort(List<T> list);
}
