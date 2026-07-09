package student.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import game.Edge;
import game.EscapeState;
import game.Node;

/**
 * Abstract base class for escape strategies that use a knapsack-style depth-first search (DFS) 
 * to find the best escape path that maximizes gold collection while respecting the remaining time constraint.
 * 
 * <p>This class provides common functionality for subclasses, including:
 * 1. Tracking the best path and gold collected during the search
 * 2. Memoization to avoid redundant calculations and prune inferior branches
 * 3. A method to sort neighbor edges based on gold amount and distance from exit node
 * 4. A method to find the shortest escape path using Dijkstra's algorithm as a fallback
 * 
 * <p>Subclasses must implement the {@code knapsackDFS} method to define
 * the specific search behavior and optimization criteria.
 */
public abstract class KnapsackDFSBaseEscapeStrategy implements EscapeStrategy {
    /** List to hold the best path found during the search. */
    private List<Node> bestPath;

    /** Integer to hold the best gold collected during the search. */
    private int bestGold;

    /** Memoization map to store the best gold collected for each node and remaining time. */
    private Map<Node, Map<Integer, Integer>> memoMap;

    /**
     * No-args constructor for the KnapsackDFSBaseEscapeStrategy class.
     * 
     * Initializes the best path as null, best gold as -1, and the memoization map as an empty HashMap.
     * The best gold is initialized to -1 to ensure that 
     * any valid path with non-negative gold will be considered better than the initial value.
     * 
     */
    public KnapsackDFSBaseEscapeStrategy() {
        this.bestPath = null;
        this.bestGold = -1;
        this.memoMap = new HashMap<>();
    }

    /** 
     * Abstract method to be implemented by subclasses to perform a knapsack-style DFS search.
     * 
     * @param wrapper the EscapeStateWrapper object that contains the current escape state and graph
     * @param bState the current BranchState object that contains the current node, cost, gold collected, 
     * and remaining total graph gold
     * @param visited the set of nodes that have been visited in the current path
     * @param currentPath the list of nodes that form the current path from start to the current node
     */
    public abstract void knapsackDFS(
        EscapeStateWrapper wrapper, 
        BranchState bState, 
        Set<Node> visited, 
        List<Node> currentPath);

    /**
     * Returns the best path found during the search.
     *
     * @return the best path as a list of nodes
     */
    public List<Node> getBestPath() {
        return bestPath;
    }

    /**
     * Sets the best path found during the search.
     *
     * @param bestPath the best path as a list of nodes
     */
    public void setBestPath(List<Node> bestPath) {
        this.bestPath = bestPath;
    }

    /**
     * Returns the best gold collected during the search.
     *
     * @return the best gold collected
     */
    public int getBestGold() {
        return bestGold;
    }

    /**
     * Sets the best gold collected during the search.
     *
     * @param bestGold the best gold collected
     */
    public void setBestGold(int bestGold) {
        this.bestGold = bestGold;
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

    /**
     * Method to find the best path from start to end in a weighted graph 
     * that satisfies the remaining time constraint and maximizes gold collected.
     * 1. Initializes the EscapeStateWrapper and checks for graph validity
     * 2. Checks if the total graph gold is zero, and if so, returns the shortest path
     * 3. Initializes the search with the start node and total graph gold
     * 4. Creates the initial BranchState with the start node, cost, gold collected, and remaining total graph gold
     * 5. Calls the knapsackDFS method to perform the knapsack-style depth-first search
     * 
     * @param state the current escape state
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    public EscapePath findOptimizedGoldEscapePath(EscapeState state) {
        EscapeStateWrapper wrapper = new EscapeStateWrapper(state);
        int totalGraphGold = wrapper.getGraph().getTotalGold();

        if (totalGraphGold == 0) {
            return findShortestEscapePath(state);
        }

        try {
            wrapper.getGraph().checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        Set<Node> visited = new HashSet<>();
        List<Node> currentPath = new ArrayList<>();
        int startGold = wrapper.getGraph().getGoldMap().getOrDefault(wrapper.getGraph().getStartNode(), 0);
        visited.add(wrapper.getGraph().getStartNode());
        currentPath.add(wrapper.getGraph().getStartNode());

        BranchState initialState = new BranchState(
            wrapper.getGraph().getStartNode(),
            0,
            startGold,
            totalGraphGold - startGold
        );

        knapsackDFS(wrapper, initialState, visited, currentPath);

        return new EscapePath(state, getBestPath());
    }

    /**
     * Implements the findEscapePath method of EscapeStrategy interface
     * This implementation returns the best optimized escape path or a fallback path.
     * 
     * 1. Starts a separate thread to find the optimized path that maximizes gold collection 
     * while respecting the remaining time constraint.
     * 2. If the optimization search takes longer than a specified timeout, 
     * it is cancelled and a fallback path is returned.
     * 3. Log the error and fall back safely if something goes wrong structurally
     *
     * @param state the current escape state
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
     * Greedy sorting of neighbour edges in descending order based on gold amount,
     * then based on distance from exit node if they hold the same amount of gold
     * 
     * @param wrapper the EscapeStateWrapper object that contains the current escape state and graph
     * @param currentNode the current node being explored
     * @return a sorted list of neighbour edges
     */
    public List<Edge> sortNeighbours(EscapeStateWrapper wrapper, Node currentNode) {
        List<Edge> neighbours = 
        new ArrayList<>(wrapper.getGraph().getWeighted().getOrDefault(currentNode, Collections.emptyList()));

        neighbours.sort((a, b) -> {
            int goldA = wrapper.getGraph().getGoldMap().getOrDefault(a.getDest(), 0);
            int goldB = wrapper.getGraph().getGoldMap().getOrDefault(b.getDest(), 0);
            if (goldA != goldB) {
                return Integer.compare(goldB, goldA);
            } else {
                return Integer.compare(
                    wrapper.getMinDistanceToExit().getOrDefault(a.getDest(), Integer.MAX_VALUE), 
                    wrapper.getMinDistanceToExit().getOrDefault(b.getDest(), Integer.MAX_VALUE));
            }
        });
        return neighbours;
    }

    /**
     * Checks the memoization table to see if the current search path is strictly 
     * worse regarding the remaining time and gold collected than a sub-problem path we have already evaluated.
     * 
     * 1. Creates a new HashMap for the current node in the memoization map if it doesn't exist already
     * 2. Checks previous visits in the memoization map for the current node
     * 3. If we previously had more (or equal) time left, AND collected more (or equal) gold,
     * then our current branch is inferior in terms of cost and gold collected, and we prune the branch
     * 4. Clean up outdated entries that are explicitly worse than our new tracking entry
     *   4.1. Remove any entries where the memoized time left is less than or equal to the current time left 
     *   AND the memoized gold is less than or equal to the current gold
     *   4.2. This ensures that we only keep entries that are potentially useful for future comparisons, 
     *   and we don't waste memory on entries that are strictly worse than our current state
     *   4.3. This cleanup step is important for maintaining the efficiency of the memoization map, 
     *   especially in large graphs with many nodes and paths
     * 5. Otherwise, records our new time left and current gold amount for this branch
     * 6. Returns false indicating the branch should be kept for further exploration
     * 
     * @param node the current node being explored
     * @param timeLeft time left to escape from current node
     * @param currentGold gold collected along path
     * @return boolean value if branch should be pruned early or not
     */
    public boolean shouldPruneBranch(Node node, int timeLeft, int currentGold) {       
        Map<Integer, Integer> timeToGoldMap = memoMap.computeIfAbsent(node, k-> new HashMap<>());

        for (Map.Entry<Integer, Integer> entry : timeToGoldMap.entrySet()) {
            int memoizedTimeLeft = entry.getKey();
            int memoizedGold = entry.getValue();

            if (memoizedTimeLeft >= timeLeft && memoizedGold >= currentGold) {
                return true;
            }
        }

        timeToGoldMap.entrySet().removeIf(entry -> entry.getKey() <= timeLeft && entry.getValue() <= currentGold);

        timeToGoldMap.put(timeLeft, currentGold);
        return false;
    }
}
