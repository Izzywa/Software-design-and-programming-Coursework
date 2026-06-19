package student;

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
 * Class that implements a Knapsack-style Depth-first search algorithm with Branch and Bound to find the best path from start to end in a weighted graph that satisfies the remaining time constraint and maximizes gold collected.
 * Branch and Bound is a search algorithm that explores the solution space by creating branches for each decision and uses bounds to prune branches that cannot yield better solutions than the best one found so far.
 * Time complexity: O(V^E) in the worst case, where V is the number of vertices and E is the number of edges. However, with pruning based on remaining time and gold collected, the actual time complexity can be significantly reduced in practice.
 * Space complexity: O(V) for the visited set and current path, and O(P) for storing all valid paths, where P is the number of valid paths found.
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
public class EscapeKnapsackDFSBnB implements EscapeStrategy {
    private final EscapeState state;
    private final EscapeGraph graph;
    private final Node startNode;
    private final Node endNode;
    private Set<Node> visited;
    private List<Node> currentPath;
    private List<Node> bestPath;
    private int bestGold;
    private int totalGraphGold;
    private Map<Node, Integer> minDistanceToExit; // Map to store shortest distance to exit frome ach node

    /**
     * Constructor for the EscapeKnapsackDFSBnB class.
     * @param state the escape state
     * @param start the start node
     * @param end the end node
     */
    public EscapeKnapsackDFSBnB(EscapeState state, Node start, Node end) {
        this.state = state;
        this.graph = new EscapeGraph(state);
        this.startNode = start;
        this.endNode = end;
        this.visited = new HashSet<>();
        this.currentPath = new ArrayList<>();
        this.bestPath = null; // Initialize best path as null
        this.bestGold = -1; // Initialize best gold to -1 to ensure any valid path with non-negative gold will be considered better
        this.totalGraphGold = graph.getGoldMap().values().stream().mapToInt(Integer::intValue).sum(); // Calculate total gold in the graph
        this.minDistanceToExit = shortestDistancesToExit();
    }

    /**
     * Checks the validity of the graphs before performing the Knapsack-style DFS with Branch and Bound algorithm.
     * Ensures that the graphs are not null or empty, and that the start and end nodes exist in the graphs.
     */
    private void checkGraphValidity() {
        // Check if the graphs are null or empty
        if (graph.getWeighted() == null || graph.getWeighted().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        if (graph.getGoldMap() == null || graph.getGoldMap().isEmpty()) {
            throw new IllegalArgumentException("Gold map cannot be null or empty");
        }
        // Check if start and end nodes are in the graphs
        if(!graph.getWeighted().containsKey(startNode) || !graph.getWeighted().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }
        if(!graph.getGoldMap().containsKey(startNode) || !graph.getGoldMap().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the gold map");
        }
    }

    
    private void searchGraph(Node currentNode, int currentCost, int currentGold) {
        int minTimeToExit = minDistanceToExit.getOrDefault(currentNode, Integer.MAX_VALUE);
        // Check if 
        // 1. current total cost + minimum time (shortest path) from current node exceeds total escape time
        // OR
        // 2. current gold + the remaining gold available in the graph is less than or equal to bestGold, if yes we prune the branch
        if(currentCost + minTimeToExit >= state.getTimeRemaining() || currentGold + totalGraphGold <= bestGold) {
            return;
        }

        // Base case for recursion: end node reached
        // Update best gold and best path if currentGold is more than the bestGold stored so far
        if(currentNode.equals(endNode)) {
            if(currentGold > bestGold) {
                bestGold = currentGold;
                bestPath = new ArrayList<>(currentPath);
            }
        }

        //Explore neighbours in for loop
        for(Edge edge : graph.getWeighted().getOrDefault(currentNode, Collections.emptyList())) {
            Node neighbour = edge.getDest();
            int newCost = currentCost + edge.length();
            // Check if neighbor is unvisited and we have enough time budget
            if(!visited.contains(neighbour) && newCost + minTimeToExit < state.getTimeRemaining()) {
                // Visit and count gold on node
                int goldOnNode = graph.getGoldMap().getOrDefault(neighbour, 0);

                // Update visited, current path and gold available on map before recursive call
                visited.add(neighbour);
                currentPath.add(neighbour);
                totalGraphGold -= goldOnNode;

                // Recurse
                searchGraph(neighbour, newCost, currentGold + goldOnNode);

                //Track back state changes after return from recursive call
                visited.remove(neighbour);
                currentPath.remove(currentPath.size() - 1);
                totalGraphGold += goldOnNode;
            }
        }

    }

    // Dijktra's algorithm with priority queue implementation to create lookup table with shortest distances from the end node to each node
    private Map<Node,Integer> shortestDistancesToExit() {
        Map<Node,Integer> shortestDistLookupMap = new HashMap<>();
        PriorityQueue<NodeDistancePair> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        pq.add(new NodeDistancePair(endNode, 0));
        shortestDistLookupMap.put(endNode, 0);

        while(!pq.isEmpty()) {
            NodeDistancePair current = pq.poll();

            
            if(current.distance > shortestDistLookupMap.getOrDefault(current.node, Integer.MAX_VALUE)) {
                continue;
            }

            for(Edge edge : graph.getWeighted().getOrDefault(current.node, Collections.emptyList())) {
                Node neighbour = edge.getDest();
                int newDist = current.distance + edge.length();
                if(newDist < shortestDistLookupMap.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    shortestDistLookupMap.put(neighbour, newDist);
                    pq.add(new NodeDistancePair(neighbour, newDist));
                }
            }
            
        }

        return shortestDistLookupMap;
    }

    // Inner helper class which stores nodes and shortest distances to populate priority queue that prioritizes nodes by distances
    private static class NodeDistancePair {
        Node node;
        int distance;

        NodeDistancePair(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    @Override
    public EscapePath findEscapePath() {
        // Check validity of graph representations
        checkGraphValidity();

        // Initialize search with start node
        int startGold = graph.getGoldMap().getOrDefault(startNode, 0);
        visited.add(startNode);
        currentPath.add(startNode);
        totalGraphGold -= startGold;
        // Run recursive search from start node
        searchGraph(startNode, 0, startGold);
        // Return best path
        return new EscapePath(bestPath);
    }

}