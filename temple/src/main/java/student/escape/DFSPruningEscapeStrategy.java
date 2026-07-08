package student.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import game.Edge;
import game.Node;
import game.EscapeState;

/**
 * Class that implements the Depth-first search algorithm with pruning to find the best escape path 
 * that maximizes gold collection while respecting the remaining time constraint.
 * 
 * Algorithmic optimizations:
 * 1. Pruning branches that exceed the remaining time or maximum allowed paths to prevent combinatorial explosion.
 * 2. Limiting the number of recursive steps to prevent pseudo-infinite recursion.
 * 
 * Computational optimizations:
 * 1. Using a separate thread to perform the optimization search with a timeout to ensure responsiveness 
 * and prevent long-running computations from blocking the main thread.
 * 2. Using a fallback path (shortest path) if the optimization search takes too long or fails due to an error.
 * 3. Logging errors and falling back safely to ensure that the program continues to function 
 * even if the optimization search fails.
 */
public class DFSPruningEscapeStrategy implements EscapeStrategy {
    /** List to store all valid paths found during the search. */
    private List<EscapePath> allPaths;

    /** Set to keep track of visited nodes during the search. */
    private Set<Node> visited;

     /** List to hold the current path being explored during search. */
    private List<Node> currentPath;

    /** Counter to track the number of paths explored */
    private int pathCount;

    /** Counter to track the number of recursive steps taken during the search */
    private int stepCount;

    /** Maximum number of paths to explore to prevent combinatorial explosion. */
    private final int MAX_PATHS = 1000;

    /** Maximum number of recursive steps to prevent pseudo-infinite recursion. */
    private final int MAX_STEPS = 500000;

    /**
     * No-args constructor for the DFSPruningEscapeStrategy class.
     */
    public DFSPruningEscapeStrategy() {
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
     * 1. Check if the current thread is interrupted due to timeout and throw an exception to stop the recursive search
     * 2. Increment the step count for each recursive call
     * 3. If the step count exceeds the maximum allowed steps, or the current cost exceeds the remaining time, 
     * or the path count exceeds the maximum allowed paths, stop exploring this branch
     * 4. Mark the current node as visited and add it to the current path
     * 5. If the current node is the end node, add the current path to the list of all paths
     * 6. If the current node is not the end node, explore its unvisited neighbors recursively
     * 7. Backtrack by removing the current node from the visited set and the current path, then decrement step count
     * 
     * @param state the current escape state
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     */
    public void depthFirstSearchPruning(EscapeState state, EscapeGraph graph, Node currentNode, int currentCost) {
 
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Search cancelled due to timeout");
        }

        stepCount++;
  
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
    public EscapePath selectBestPath(List<EscapePath> paths, EscapePath shortestPath) {
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
     * Implements EscapeStrategy interface to find the escape path.
     * 
     * 1. Starts a separate thread to find the optimized path that maximizes gold collection 
     * while respecting the remaining time constraint.
     * 2. If the optimization search takes longer than a specified timeout, 
     * it is cancelled and a fallback path is returned.
     * 3. Log the error and fall back safely if something goes wrong structurally
     * 
     * @param state the current escape state
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        /** Timeout in milliseconds */
        final long SEARCH_TIMEOUT_MS = 10000L;
        EscapePath fallbackPath = findShortestEscapePath(state);

        CompletableFuture<EscapePath> optimizationTask = CompletableFuture.supplyAsync(() -> {
            return findOptimizedGoldEscapePath(state);
        });

        try {
            return optimizationTask.get(SEARCH_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            optimizationTask.cancel(true);
            System.out.println("Optimization search timed out. Returning fallback path.");
            return fallbackPath;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Optimization failed due to an error: " + e.getMessage());
            return fallbackPath;
        }
    }

    /**
     * Finds the optimized escape path that maximizes gold collection while respecting the remaining time constraint.
     * 1. Check if the graph is valid and not empty
     * 2. Resets the path count and step count before starting the search
     * 3. Finds all possible paths from the start node to the end node that fit within the remaining time.
     * 4. Then selects the best path based on the total amount of gold collected, 
     * and in case of a tie, selects the one with the lowest total cost.
     * 
     * @param state the current escape state
     * @return the best possible path from start to end based on gold collected and total cost
     */
    public EscapePath findOptimizedGoldEscapePath(EscapeState state) {
        EscapeGraph graph = new EscapeGraph(state);
        EscapePath shortestPath = findShortestEscapePath(state);

        try {
            graph.checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        stepCount = 0;
        depthFirstSearchPruning(state, graph, graph.getStartNode(), 0);
        return selectBestPath(allPaths, shortestPath);
    }

    /**
     * Finds the shortest escape path using Dijkstra's algorithm as a fallback.
     *
     * @param state the current escape state
     * @return the shortest escape path
     */
    public EscapePath findShortestEscapePath(EscapeState state) {
        EscapeStrategy dijkstra = new DijkstraEscapeStrategy();
        return dijkstra.findEscapePath(state);
    }

}
