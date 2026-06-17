package student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.Edge;
import game.Node;
import game.EscapeState;

public class EscapeDFSPruning implements EscapeStrategy {
    private final EscapeState state;
    private final EscapeGraph graph;
    private final Node startNode;
    private final Node endNode;
    private List<EscapePath> allPaths;
    private Set<Node> visited;
    private List<Node> currentPath;
    private int pathCount;
    private final int MAX_PATHS = 1000; // Limit the number of paths to explore to prevent combinatorial explosion
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
        EscapeStrategy dijkstraStrategy = new EscapeDijkstra(state, start, end);
        this.shortestPath = dijkstraStrategy.findEscapePath(); // Initialize with the shortest path found
    }

    /**
     * Checks the validity of the graph before performing DFS algorithm
     * Ensures that the graph is not null or empty, and that the start and end nodes exist in the graph
     */
    private void checkGraphValidity() {
        // Check if the graph is null or empty
        if (graph.getUnweighted() == null || graph.getUnweighted().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        // Check if start and end nodes are in the graph
        if(!graph.getUnweighted().containsKey(startNode) || !graph.getUnweighted().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }
    }

    /**
     * Recursively searches the graph for all possible paths from the current node to the end node.
     * @param currentNode the current node being explored
     */
    private void searchGraph(Node currentNode, int currentCost) {
         // PRUNING: If we already exceeded the remaining time, stop exploring this branch
        if (currentCost >= state.getTimeRemaining() || pathCount >= MAX_PATHS) {
            return;
        }

        visited.add(currentNode);
        currentPath.add(currentNode);
        System.out.println("Exploring node: " + currentNode.getId() + " with current cost: " + currentCost);

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
    }

    /**
     * Selects the best path from the list of valid paths based on the total amount of gold collected, and in case of a tie, selects the one with the lowest total cost.
     * @param paths the list of valid EscapePaths to select from
     * @return the best EscapePath based on gold collected and total cost or the shortest path if no valid paths are found
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
     * Implements EscapeStrategy interface to find the escape path using the DFS algorithm.
     * 
     * Finds all possible paths from the start node to the end node. 
     * Then filters the paths based on the remaining time and selects the best path based on the total amount of gold collected, and in case of a tie, selects the one with the lowest total cost.
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath() {
        checkGraphValidity();
        searchGraph(startNode, 0);
        return selectBestPath(allPaths);
    }

}
