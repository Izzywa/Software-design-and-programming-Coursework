package student.sort;

import java.util.List;

/**
 * A strategy for sorting a list of comparable elements.
 */
public interface SortingStrategy {
  /**
   * Sorts a list of comparable elements according
   * to the strategy defined by the implementing class.
   * @param <T> the type of elements in the list,
   * which must implement Comparable
   * @param list the list to sort
   * @return a new list containing the sorted elements
   */
  public <T extends Comparable<T>> List<T> sort(List<T> list);
}
