package student.sort;

import java.util.List;

/**
 * Sorts a list in ascending order.
 */
public class AscendingSort implements SortingStrategy {

  @Override
  public <T extends Comparable<T>> List<T> sort(List<T> list) {
    return list.stream()
      .sorted(Comparable::compareTo)
      .toList();
  }
}
