package student.escape;

/**
 * Factory class for creating instances of {@link EscapeStrategy} implementations.
 */
public class EscapeStrategyFactory {

    @Getter
  public enum Strategy {
    BFS("BFS"),
    DFSAllPaths("DFSAllPaths"),
    DFSPruning("DFSPruning"),
    Dijkstra("Dijkstra"),
    KnapsackDFS("KnapsackDFS");

    private final String name;

    Strategy(String name) {
      this.name = name;
    }

  }
}
