package student.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.Edge;
import game.EscapeState;
import game.Node;

public abstract class EscapeKnapsackDFSBase implements EscapeStrategy {
    private List<Node> bestPath;
    private int bestGold;
    private Map<Node, Map<Integer, Integer>> memoMap;

    /**
     * No-args constructor for the EscapeKnapsackDFSBase class.
     */
    public EscapeKnapsackDFSBase() {
        this.bestPath = null; // Initialize best path as null
        // Initialize best gold to -1 to ensure any valid path with non-negative gold will be considered better
        this.bestGold = -1;
        this.memoMap = new HashMap<>();
    }

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
     * Abstract method of EscapeStrategy interfaceto be implemented by subclasses 
     * to find the best escape path using a knapsack-style DFS search.
     *
     * @param state the current escape state
     */
    @Override
    public abstract EscapePath findEscapePath(EscapeState state);

    /** 
     * Abstract method to be implemented by subclasses to perform a knapsack-style DFS search.
     * 
     * @param state the current escape state
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     * @param currentGold total gold found along this branch
     * @param totalGraphGold total gold on graph
     */
    public abstract void knapsackDFS(
        EscapeStateWrapper wrapper, 
        BranchState bState, 
        Set<Node> visited, 
        List<Node> currentPath);

    /**
     * Greedy sorting of neighbour edges in descending order based on gold amount,
     * then based on distance from exit node if they hold the same amount of gold
     * 
     * @param graph graph for current escape state
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
     * If yes, we decide to prune the branch.
     * If no, then we record our new values of time left and collected
     * 
     * @param node the current node being explored
     * @param timeLeft time left to escape from current node
     * @param currentGold gold collected along path
     * @return boolean value if branch should be pruned early or not
     */
    public boolean shouldPruneBranch(Node node, int timeLeft, int currentGold) {
        // Creates new HashMap for node key if it doesn't exist already
        Map<Integer, Integer> timeToGoldMap = memoMap.computeIfAbsent(node, k-> new HashMap<>());

        //Check previous visits in the memoization map
        for (Map.Entry<Integer, Integer> entry : timeToGoldMap.entrySet()) {
            int memoizedTimeLeft = entry.getKey();
            int memoizedGold = entry.getValue();

            // If we previously had MORE (or equal) time left, AND collected MORE (or equal) gold,
            // then our current branch is inferior in terms of cost and gold collected
            if (memoizedTimeLeft >= timeLeft && memoizedGold >= currentGold) {
                return true;
            }
        }

        // Clean up outdated entries that are explicitly worse than our new tracking entry
        timeToGoldMap.entrySet().removeIf(entry -> entry.getKey() <= timeLeft && entry.getValue() <= currentGold);

        // Otherwise, record our new time left and current gold amount for this branch
        timeToGoldMap.put(timeLeft, currentGold);
        return false;
    }
}
