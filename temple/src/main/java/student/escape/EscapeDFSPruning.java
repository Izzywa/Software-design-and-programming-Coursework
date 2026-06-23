package student.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.Edge;
import game.Node;
import game.EscapeState;

/**
 * Class that implements the Depth-first search algorithm to find all paths from start to end in a weighted graph 
 * that satisfy the remaining time constraint, then selects the best path based on gold collected.
 * Time complexity: O(V^E) in the worst case, where V is the number of vertices and E is the number of edges. 
 * However, with pruning based on remaining time, the actual time complexity can be significantly reduced in practice.
 * Space complexity: O(V) for the visited set and current path, and O(P) for storing all valid paths, 
 * where P is the number of valid paths found.
 * Reference: <a href="https://en.wikipedia.org/wiki/Depth-first_search">Wikipedia DFS</a>
 *
 * <pre>
 * procedure DFS(G, v) is
 *     mark v as visited
 *     for each neighbor w of v do
 *         if w is not visited then
 *             DFS(G, w)
 * </pre>
 */
public class EscapeDFSPruning implements EscapeStrategy {
    private final EscapeState state;
    private final EscapeGraph graph;
    private final Node startNode;
    private final Node endNode;
    private List<EscapePath> allPaths;
    private Set<Node> visited;
    private List<Node> currentPath;
    private int pathCount;
    private int stepCount;
    private final int MAX_PATHS = 1000; // Limit the number of paths to explore to prevent combinatorial explosion
    private final int MAX_STEPS = 500000; // Limit the maximum steps to prevent pseudo-infinite loops
    private EscapePath shortestPath;

    /**
     * Constructor for the EscapeDFSPruning class.
     * @param state the escape state
     * @param start the start node
     * @param end the end node
     */
    public EscapeDFSPruning(EscapeState state, Node start, Node end) {
        this.state = state;
        this.graph = new EscapeGraph(state);
        this.startNode = start;
        this.endNode = end;
        this.allPaths = new ArrayList<>();
        this.visited = new HashSet<>();
        this.currentPath = new ArrayList<>();
        this.pathCount = 0;
        this.stepCount = 0;
        EscapeStrategy dijkstraStrategy = new EscapeDijkstra(state, start, end);
        this.shortestPath = dijkstraStrategy.findEscapePath(); // Initialize with the shortest path found
    }

    /**
     * Checks the validity of the graph before performing DFS algorithm with pruning
     * Ensures that the graph is not null or empty, and that the start and end nodes exist in the graph
     */
    private void checkGraphValidity() {
        // Check if the graph is null or empty
        if (graph.getWeighted() == null || graph.getWeighted().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        // Check if start and end nodes are in the graph
        if (!graph.getWeighted().containsKey(startNode) || !graph.getWeighted().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }
    }

    /**
     * Recursively searches the graph for all possible paths from the current node to the end node.
     * However, it stops exploring a branch if the remaining time is exceeded a certain limit and the branch is pruned 
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     */
    private void searchGraph(Node currentNode, int currentCost) {
        // Increment the step count for each recursive call
        stepCount++;
         // PRUNING: If we already exceeded the remaining time, stop exploring this branch
        if (stepCount >= MAX_STEPS || currentCost >= state.getTimeRemaining() || pathCount >= MAX_PATHS) {
            return;
        }

        visited.add(currentNode);
        currentPath.add(currentNode);

        if (currentNode.equals(endNode)) {
            pathCount++;
            allPaths.add(new EscapePath(new ArrayList<>(currentPath)));
        } else {
            var neighbors = graph.getWeighted().getOrDefault(currentNode, Collections.emptyList());
            for (Edge edge : neighbors) {
                Node neighbor = edge.getDest();
                int edgeWeight = edge.length();

                if (!visited.contains(neighbor)) {
                    searchGraph(neighbor, currentCost + edgeWeight);
                }
            }
        }

        // Backtrack
        visited.remove(currentNode);
        currentPath.remove(currentPath.size() - 1);
        stepCount--;
    }

    /**
     * Selects the best path from the list of valid paths based on the total amount of gold collected, 
     * and in case of a tie, selects the one with the lowest total cost.
     * @param paths the list of valid EscapePaths to select from
     * @return the best EscapePath based on gold collected and total cost or the shortest path if no valid paths found
     */
    private EscapePath selectBestPath(List<EscapePath> paths) {
        return paths.stream()
                .max((p1, p2) -> {
                    if (p1.getTotalGold() != p2.getTotalGold()) {
                        return Integer.compare(p1.getTotalGold(), p2.getTotalGold());
                    } else {
                        return Integer.compare(p2.getTotalCost(), p1.getTotalCost());
                    }
                }).orElse(shortestPath); // Return the shortest path if no valid paths found
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using the DFS algorithm with pruning.
     * 
     * Finds all possible paths from the start node to the end node that fit within the remaining time.
     * Then it selects the best path based on the total amount of gold collected, and in case of a tie, 
     * selects the one with the lowest total cost.
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath() {
        checkGraphValidity();
        stepCount = 0; // Reset step count before starting the search
        searchGraph(startNode, 0);
        return selectBestPath(allPaths);
    }

}
