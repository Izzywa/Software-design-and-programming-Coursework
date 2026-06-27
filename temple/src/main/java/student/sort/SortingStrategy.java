package student.sort;

import java.util.List;

public interface SortingStrategy {
    public <T extends Comparable<T>> List<T> sort(List<T> list);
}
