package student.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import game.Edge;
import game.Node;
import game.EscapeState;

/**
 * Class that implements a Knapsack-style Depth-first search algorithm with Branch and Bound to find the best path 
 * from start to end in a weighted graph that satisfies the remaining time constraint and maximizes gold collected.
 * Branch and Bound is a search algorithm that explores the solution space by creating branches for each decision 
 * and uses bounds to prune branches that cannot yield better solutions than the best one found so far.
 * Pruning algorithms implemented in class:
 *      1. Potential available gold less than already collected
 *      2. Potential path is longer than time needed to exit based on Dijkstra's algorithm
 *      3. Memoization-based pruning of inferior branches
 */
public class EscapeKnapsackDFSDetour implements EscapeStrategy {
    private List<Node> currentPath;
    private List<Node> bestPath;
    private int bestGold;
    private Map<Node, Map<Integer, Integer>> memoMap; // Memoization map: Map<Node, Map<remainingTime, maxGoldFound>>
    private final double SPARE_TIME_MULTIPLIER = 1.25;

    /**
     * No-args constructor for the EscapeKnapsackDFSBnB class.
     */
    public EscapeKnapsackDFSDetour() {
        this.currentPath = new ArrayList<>();
        this.bestPath = null; // Initialize best path as null
        // Initialize best gold to -1 to ensure any valid path with non-negative gold will be considered better
        this.bestGold = -1;
        this.memoMap = new HashMap<>();
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using the DFS algorithm with pruning.
     * Finds the best path in the graph after searching is assisted by pruning unuseful 
     * or illegal branches during recursive DFS path discovery.
     * 
     * @param state the current escape state
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        //Initialize graph
        EscapeStateWrapper wrapper = new EscapeStateWrapper(state);
        int totalGraphGold = wrapper.getGraph().getTotalGold();

        // Handling test egde case where there's no gold on map
        // Theoretical possibility for smaller maps with P = 0.33 ^ node count 
        // (Map with 10 nodes has P = 0.0000153 (0.0015%) probability that no node has gold.)
        if (totalGraphGold == 0) {
            EscapeStrategy dijkstra = new EscapeDijkstra();
            return dijkstra.findEscapePath(state);
        }

        // Check graph validity
        try {
            wrapper.getGraph().checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Initiliaze path-local set for storing vistied nodes
        Set<Node> pathVisited = new HashSet<>();

        // Initialize search with start node and total graph gold
        int startGold = wrapper.getGraph().getGoldMap().getOrDefault(wrapper.getGraph().getStartNode(), 0);
        pathVisited.add(wrapper.getGraph().getStartNode());
        currentPath.add(wrapper.getGraph().getStartNode());
        totalGraphGold -= startGold;

        // Run recursive search from start node
        knapsackDFS(wrapper, wrapper.getGraph().getStartNode(), 0, startGold, totalGraphGold, pathVisited);
        // Return best path
        return new EscapePath(state, bestPath);
    }

    /** 
     * Recursively searches the graph for all possible paths from the current node to the end node.
     * However, it stops exploring a branch if:
     *    1. Potential available gold less than already collected
     *    2. Potential path is longer than time needed to exit based on Dijkstra's algorithm
     *    3. New branch is inferior in terms of time remaining and gold compared to already known branches
     * the remaining time is exceeded a certain limit and the branch is pruned
     * 
     * @param state the current escape state
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     * @param currentGold total gold found along this branch
     * @param totalGraphGold total gold remaining on graph
     * @param pathVisited set to store nodes visited along the path
     */
    public void knapsackDFS(EscapeStateWrapper wrapper, Node currentNode, 
                            int currentCost, int currentGold, int totalGraphGold, Set<Node> pathVisited) {

        int minTimeToExit = wrapper.getMinDistanceToExit().getOrDefault(currentNode, Integer.MAX_VALUE);
        int timeLeft = wrapper.getState().getTimeRemaining() - currentCost;
        // Check if 
        // 1. current total cost + minimum time (shortest path) from current node exceeds total escape time
        // OR
        // 2. current gold + the remaining gold available in the graph is less than or equal to bestGold
        // If true, we prune the branch
        if (currentCost + minTimeToExit >= wrapper.getState().getTimeRemaining() || currentGold + totalGraphGold <= bestGold) {
            return;
        }

        // Memoization can be used to store and check paths visited earlier 
        // without recomputing them all the time during recursion
        // If we've seen this branch before with more or equal time left AND more or equal gold collected,
        // then we prune the branch
        if (shouldPruneBranch(currentNode, timeLeft, currentGold)) {
            return;
        }

        // Base case for recursion: end node reached
        // Update best gold and best path if currentGold is more than the bestGold stored so far
        if (currentNode.equals(wrapper.getGraph().getExitNode())) {
            if (currentGold > bestGold) {
                bestGold = currentGold;
                bestPath = new ArrayList<>(currentPath);
            }
        }

        // Memeoization efficiency can be improved if we discover paths with higher potantial gold values first
        // Greedy sorting neighbours
        List<Edge> neighbours = sortNeighbours(wrapper, currentNode);

        // Identify the immediate parent node we just came from to prevent advancing that direction
        Node immediateParent = currentPath.size() >= 2 ? currentPath.get(currentPath.size() - 2) : null;

        //Explore neighbours in for loop
        for (Edge edge : neighbours) {
            Node neighbour = edge.getDest();

            if (neighbour.equals(immediateParent)) {
                continue;
            }

            int newCost = currentCost + edge.length();

            // Instead of excluding already visited neighbour nodes, we allow exploration of neighbour cells
            // We will achieve this by tracking a local set of nodes belonging to the current path 
            // instead of a global set of vistied nodes
            // We have to initialize that set before calling the recursion, then it's passed as a parameter

            // Check if we have enough time to hit this neighbor and still make it to the exit
            int neighbourExitTime = wrapper.getMinDistanceToExit().getOrDefault(neighbour, Integer.MAX_VALUE);
            if (newCost + neighbourExitTime < wrapper.getState().getTimeRemaining()) {
                // Boolean flag to exclude parent nodes from the search to prevent loops
                boolean alreadyVisited = pathVisited.contains(neighbour);
                // Gold is removed from search if we already visited the node
                int goldOnNode = alreadyVisited ? 0 : wrapper.getGraph().getGoldMap().getOrDefault(neighbour, 0);

                // Skip moving into a zero-gold node if it takes us further from the exit, 
                // unless we have an abundance of spare time.
                if (goldOnNode == 0 && neighbourExitTime > minTimeToExit) {
                    if (timeLeft < minTimeToExit * SPARE_TIME_MULTIPLIER) {
                        continue; 
                    }
                }

                // Update visited, current path and gold available on map before recursive call
                // But only update gold if the node is not already visited
                boolean inserted = pathVisited.add(neighbour);
                currentPath.add(neighbour);
                if (inserted) {
                    totalGraphGold -= goldOnNode;
                }

                // Recurse
                knapsackDFS(wrapper, neighbour, newCost, currentGold + goldOnNode, totalGraphGold, pathVisited);

                //Track back state changes after return from recursive call
                if (inserted) {
                    pathVisited.remove(neighbour);
                    totalGraphGold += goldOnNode;
                }
                currentPath.remove(currentPath.size() - 1);    
            }
        }

    }

    /**
     * Greedy sorting of neighbour edges in descending order based on gold amount,
     * then based on distance from exit node if they hold the same amount of gold
     * 
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     * @return a sorted list of neighbour edges
     */
    public List<Edge> sortNeighbours(EscapeStateWrapper wrapper, Node currentNode) {
        List<Edge> neighbours = new ArrayList<>(wrapper.getGraph().getWeighted().getOrDefault(currentNode, Collections.emptyList()));
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

    /**
     * BranchState record to encapsulate branch-specific data
     * 
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     * @param currentGold total gold found along this branch
     * @param totalGraphGold total gold remaining on graph
     */
    public record BranchState(Node currentNode, int currentCost, int currentGold, int totalGraphGold) {
        public BranchState moveTo(Node neighbour, int edgeLength, int goldOnNode) {
            return new BranchState(
                neighbour, 
                this.currentCost + edgeLength,
                this.currentGold + goldOnNode,
                this.totalGraphGold - goldOnNode
            );
        }
    }
}
