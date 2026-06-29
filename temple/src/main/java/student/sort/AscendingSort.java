package student.sort;

import java.util.List;

/**
 * Sorts a list in ascending order.
 */
public class AscendingSort implements SortingStrategy {

  /**
   * Sorts a list in ascending order.
   *
   * @param list the list to sort
   * @param <T> the type of elements in the list
   * @return a new list sorted in ascending order
   */
  @Override
  public <T extends Comparable<T>> List<T> sort(List<T> list) {
    return list.stream()
      .sorted(Comparable::compareTo)
      .toList();
  }
}
