package student.sort;

import java.util.List;

public class DescendingSort implements SortingStrategy {

    @Override
    public <T extends Comparable<T>> List<T> sort(List<T> list) {
        return list.stream()
                .sorted((a, b) -> b.compareTo(a))
                .toList();
    }
}
