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
    private List<EscapePath> allPaths;
    private Set<Node> visited;
    private List<Node> currentPath;
    private int pathCount;
    private int stepCount;
    private final int MAX_PATHS = 1000; // Limit the number of paths to explore to prevent combinatorial explosion
    private final int MAX_STEPS = 500000; // Limit the maximum steps to prevent pseudo-infinite loops

    /**
     * No-args constructor for the EscapeDFSPruning class.
     */
    public EscapeDFSPruning() {
        this.allPaths = new ArrayList<>();
        this.visited = new HashSet<>();
        this.currentPath = new ArrayList<>();
        this.pathCount = 0;
        this.stepCount = 0;
    }

    /**
     * Recursively searches the graph for all possible paths from the current node to the end node.
     * However, it stops exploring a branch if the remaining time is exceeded a certain limit and the branch is pruned
     * 
     * @param state the current escape state
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     */
    public void depthFirstSearchPruning(EscapeState state, EscapeGraph graph, Node currentNode, int currentCost) {
        // Increment the step count for each recursive call
        stepCount++;
         // PRUNING: If we already exceeded the remaining time, stop exploring this branch
        if (stepCount >= MAX_STEPS || currentCost >= state.getTimeRemaining() || pathCount >= MAX_PATHS) {
            return;
        }

        visited.add(currentNode);
        currentPath.add(currentNode);

        if (currentNode.equals(graph.getExitNode())) {
            pathCount++;
            allPaths.add(new EscapePath(state, new ArrayList<>(currentPath)));
        } else {
            var neighbors = graph.getWeighted().getOrDefault(currentNode, Collections.emptyList());
            for (Edge edge : neighbors) {
                Node neighbor = edge.getDest();
                int edgeWeight = edge.length();

                if (!visited.contains(neighbor)) {
                    depthFirstSearchPruning(state, graph, neighbor, currentCost + edgeWeight);
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
     * 
     * @param paths the list of valid EscapePaths to select from
     * @param shortestPath a shortest path as backup if no valid paths are found
     * @return the best EscapePath based on gold collected and total cost or the shortest path if no valid paths found
     */
    private EscapePath selectBestPath(List<EscapePath> paths, EscapePath shortestPath) {
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
     * 
     * @param state the current escape state
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        EscapeGraph graph = new EscapeGraph(state);
        EscapeStrategy dijkstraStrategy = new EscapeDijkstra();
        EscapePath shortestPath = dijkstraStrategy.findEscapePath(state);

        // Check if graph is empty or null
        try {
            graph.checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        stepCount = 0; // Reset step count before starting the search
        depthFirstSearchPruning(state, graph, graph.getStartNode(), 0);
        return selectBestPath(allPaths, shortestPath);
    }

}
