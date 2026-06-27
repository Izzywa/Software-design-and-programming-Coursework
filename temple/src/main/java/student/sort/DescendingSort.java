package student.sort;

import java.util.List;

public class DescendingSort implements SortingStrategy {

    @Override
    public <T extends Comparable<T>> List<T> sort(List<T> list) {
        list.sort((a, b) -> b.compareTo(a));
        return list;
    }
}
