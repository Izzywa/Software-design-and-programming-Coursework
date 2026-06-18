package student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        // Check if current gold + the remaining gold available in the graph is less than or equal to bestGold, if yes we prune the branch

        // Base case for recursion: end node reached
        // Update best gold and best path if currentGold is more than the bestGold stored so far

        //Explore neighbours in for loop
            // Check if neighbor is unvisited and we have enough time budget
                // Visit and count gold on node

                // Update visited, current path and gold available on map before recursive call
                // Recurse

                //Track back state changes after return from recursive call
    }

    @Override
    public EscapePath findEscapePath() {
        checkGraphValidity();
        searchGraph(startNode, 0, bestGold);
        return null;
    }

}