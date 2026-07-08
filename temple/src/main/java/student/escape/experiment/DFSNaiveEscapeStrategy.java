package student.escape.experiment;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Collectors;

import game.EscapeState;
import game.Node;
import student.escape.DijkstraEscapeStrategy;
import student.escape.EscapeGraph;
import student.escape.EscapePath;
import student.escape.EscapeStrategy;

/**
 * Class that implements the Depth-first search algorithm to find all paths from start to end in a weighted graph
 *  then filters them based on the remaining time, then selects the best path based on gold collected.
 * Time complexity: O(V^E) in the worst case, where V is the number of vertices and E is the number of edges. 
 * This will occur when the graph is a complete graph and all paths are explored. 
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
public class DFSNaiveEscapeStrategy implements EscapeStrategy {
    /** List to hold all paths found during the DFS exploration. */
    private List<EscapePath> allPaths;

    /** Set to track visited nodes to avoid cycles during DFS. */
    private Set<Node> visited;

    /** List to hold the current path being explored during DFS. */
    private List<Node> currentPath;

    /** Counter to track the number of paths explored */
    private int pathCount;

    /** Maximum number of paths to explore to prevent combinatorial explosion. */
    private final int MAX_PATHS = 1000;

    /**
     * No-args constructor for the DFSNaiveEscapeStrategy class.
     */
    public DFSNaiveEscapeStrategy() {
        this.allPaths = new ArrayList<>();
        this.visited = new HashSet<>();
        this.currentPath = new ArrayList<>();
        this.pathCount = 0;
    }

    /**
     * Recursively searches the graph for all possible paths from the current node to the end node.
     * 1. Mark the current node as visited and add it to the current path
     * 2. If the current node is the end node, add the current path to the list of all paths
     * 3. If the current node is not the end node, explore its unvisited neighbors recursively
     * 4. Backtrack by removing the current node from the visited set and the current path
     * 5. Stop exploring if the maximum number of paths has been reached
     * 
     * @param state the current escape state
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     */
    public void depthFirstSearch(EscapeState state, EscapeGraph graph, Node currentNode) {
        
        visited.add(currentNode);
        currentPath.add(currentNode);

        if (currentNode.equals(graph.getExitNode())) {
            pathCount++;
            allPaths.add(new EscapePath(state, new ArrayList<>(currentPath)));
        } else if (pathCount < MAX_PATHS) {
             for (Node neighbor : graph.getUnweighted().getOrDefault(currentNode, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    depthFirstSearch(state, graph, neighbor);
                }
            }
        }

        visited.remove(currentNode);
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Filters the list of paths based on the remaining time.
     * @param state the current escape state
     * @param paths the list of EscapePaths to filter
     * @return the list of valid EscapePaths
     */
    public List<EscapePath> filterPaths(EscapeState state, List<EscapePath> paths) {
        List<EscapePath> validPaths = paths.stream()
                .filter(path -> path.getTotalCost() <= state.getTimeRemaining())
                .collect(Collectors.toList());
        return validPaths;
    }

    /**
     * Selects the best path from the list of valid paths based on the total amount of gold collected, 
     * and in case of a tie, selects the one with the lowest total cost.
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
                }).orElse(shortestPath);
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using the DFS algorithm. 
     * 1. Check if the graph is valid and not empty
     * 2. Finds all possible paths from the start node to the end node. 
     * 3. Then filters the paths based on the remaining time and selects the best path 
     * based on the total amount of gold collected, and in case of a tie, selects the one with the lowest total cost.
     * 4. If no valid paths are found, returns the shortest path as a backup.
     * 
     * @param state the current escape state
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        EscapeGraph graph = new EscapeGraph(state);
        EscapeStrategy dijkstraStrategy = new DijkstraEscapeStrategy();
        EscapePath shortestPath = dijkstraStrategy.findEscapePath(state);

        try {
            graph.checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        depthFirstSearch(state, graph, graph.getStartNode());
        List<EscapePath> validPaths = filterPaths(state, allPaths);
        return selectBestPath(validPaths, shortestPath);
    }

}
