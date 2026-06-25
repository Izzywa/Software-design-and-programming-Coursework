package student.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.PriorityQueue;
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
public class EscapeKnapsackDFSBnB implements EscapeStrategy {
    private Set<Node> visited;
    private List<Node> currentPath;
    private List<Node> bestPath;
    private int bestGold;
    private Map<Node, Integer> minDistanceToExit; // Map to store shortest distance to exit frome each node
    private Map<Node, Map<Integer, Integer>> memoMap; // Memoization map: Map<Node, Map<remainingTime, maxGoldFound>>

    /**
     * No-args constructor for the EscapeKnapsackDFSBnB class.
     */
    public EscapeKnapsackDFSBnB() {
        this.visited = new HashSet<>();
        this.currentPath = new ArrayList<>();
        this.bestPath = null; // Initialize best path as null
        // Initialize best gold to -1 to ensure any valid path with non-negative gold will be considered better
        this.bestGold = -1;
        this.minDistanceToExit = new HashMap<>();
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
        EscapeGraph graph = new EscapeGraph(state);
        int totalGraphGold = graph.getTotalGold();

        // Handling test egde case where there's no gold on map
        // Theoretical possibility for smaller maps with P = 0.33 ^ node count 
        // (Map with 10 nodes has P = 0.0000153 (0.0015%) probability that no node has gold.)
        if (totalGraphGold == 0) {
            EscapeStrategy dijkstra = new EscapeDijkstra();
            return dijkstra.findEscapePath(state);
        }

        // Check graph validity
        try {
            graph.checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Populate map to store shortest distance to exit frome each node
        minDistanceToExit = shortestDistancesToExit(graph);


        // Initialize search with start node and total graph gold
        int startGold = graph.getGoldMap().getOrDefault(graph.getStartNode(), 0);
        visited.add(graph.getStartNode());
        currentPath.add(graph.getStartNode());
        totalGraphGold -= startGold;

        // Run recursive search from start node
        knapsackDFS(state, graph, graph.getStartNode(), 0, startGold, totalGraphGold);
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
     * @param totalGraphGold total gold on graph
     */
    public void knapsackDFS(EscapeState state, EscapeGraph graph, Node currentNode, int currentCost, int currentGold, int totalGraphGold) {
        int minTimeToExit = minDistanceToExit.getOrDefault(currentNode, Integer.MAX_VALUE);
        int timeLeft = state.getTimeRemaining() - currentCost;
        // Check if 
        // 1. current total cost + minimum time (shortest path) from current node exceeds total escape time
        // OR
        // 2. current gold + the remaining gold available in the graph is less than or equal to bestGold
        // If true, we prune the branch
        if (currentCost + minTimeToExit >= state.getTimeRemaining() || currentGold + totalGraphGold <= bestGold) {
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
        if (currentNode.equals(graph.getExitNode())) {
            if (currentGold > bestGold) {
                bestGold = currentGold;
                bestPath = new ArrayList<>(currentPath);
            }
        }

        // Memeoization efficiency can be improved if we discover paths with higher potantial gold values first
        // We can use a greedy approach to sort the neighbours based on gold amount
        // This is similar to sorting the neighbours by distance to orb that we did in the explore phase
        List<Edge> neighbours = new ArrayList<>(graph.getWeighted().getOrDefault(currentNode, Collections.emptyList()));
        neighbours.sort((a, b) -> {
            int goldA = graph.getGoldMap().getOrDefault(a.getDest(), 0);
            int goldB = graph.getGoldMap().getOrDefault(b.getDest(), 0);
            if (goldA != goldB) {
                return Integer.compare(goldB, goldA);
            } else {
                return Integer.compare(
                    minDistanceToExit.getOrDefault(a.getDest(), Integer.MAX_VALUE), 
                    minDistanceToExit.getOrDefault(b.getDest(), Integer.MAX_VALUE));
            }
        });

        //Explore neighbours in for loop
        for (Edge edge : neighbours) {
            Node neighbour = edge.getDest();
            int newCost = currentCost + edge.length();
            // Check if neighbor is unvisited and we have enough time budget
            if (!visited.contains(neighbour) && newCost + minTimeToExit < state.getTimeRemaining()) {
                // Visit and count gold on node
                int goldOnNode = graph.getGoldMap().getOrDefault(neighbour, 0);

                // Update visited, current path and gold available on map before recursive call
                visited.add(neighbour);
                currentPath.add(neighbour);
                totalGraphGold -= goldOnNode;

                // Recurse
                knapsackDFS(state, graph, neighbour, newCost, currentGold + goldOnNode, totalGraphGold);

                //Track back state changes after return from recursive call
                visited.remove(neighbour);
                currentPath.remove(currentPath.size() - 1);
                totalGraphGold += goldOnNode;
            }
        }

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

        // Otherwise, record our new time left and current gold amount for this branch
        timeToGoldMap.put(timeLeft, currentGold);
        return false;
    }

    /**
     * Dijktra's algorithm with priority queue implementation to create lookup table 
     * with shortest distances from each node to the exit node
     * This algorithm traverses the graph backwards from end node towards the start node
     * 
     * @param graph graph for current escape state
     * @return Map<Node,Integer> that contains nodes and their shortest distances to the exit node
     */
    public Map<Node,Integer> shortestDistancesToExit(EscapeGraph graph) {
        Map<Node,Integer> shortestDistLookupMap = new HashMap<>();
        PriorityQueue<NodeDistancePair> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        pq.add(new NodeDistancePair(graph.getExitNode(), 0));
        shortestDistLookupMap.put(graph.getExitNode(), 0);

        while (!pq.isEmpty()) {
            NodeDistancePair current = pq.poll();

            
            if (current.distance > shortestDistLookupMap.getOrDefault(current.node, Integer.MAX_VALUE)) {
                continue;
            }

            for (Edge edge : graph.getInvertedWeighted().getOrDefault(current.node, Collections.emptyList())) {
                Node neighbour = edge.getDest();
                int newDist = current.distance + edge.length();
                if (newDist < shortestDistLookupMap.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    shortestDistLookupMap.put(neighbour, newDist);
                    pq.add(new NodeDistancePair(neighbour, newDist));
                }
            }
            
        }

        return shortestDistLookupMap;
    }

    
    /**
     * Inner helper class which stores nodes and shortest distances to populate priority queue 
     * that prioritizes nodes by distances
     */
    private static class NodeDistancePair {
        Node node;
        int distance;
        /** 
         * Constructor
         * @param node current node
         * @param distance distance from current node to a certain node
         */
        NodeDistancePair(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
