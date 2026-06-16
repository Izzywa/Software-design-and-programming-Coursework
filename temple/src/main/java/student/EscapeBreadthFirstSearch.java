package student;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import game.EscapeState;
import game.Node;

/**
 * Class that implements the Breadth-first search algorithm to find the shortest path from start to end in an unweighted graph.
 * Reference: <a href="https://en.wikipedia.org/wiki/Breadth-first_search">Wikipedia BFS</a>
 *
 * <pre>
 * procedure BFS(G, start) is
 *     create a queue Q
 *     create a set V
 *     add start to Q
 *     add start to V
 *     while Q is not empty do
 *         current = Q.dequeue()
 *         if current is the target then
 *             return true
 *         for each neighbor of current do
 *             if neighbor is not in V then
 *                 add neighbor to V
 *                 add neighbor to Q
 *     return false
 * </pre>
 */
public class EscapeBreadthFirstSearch implements EscapeStrategy {
    private final EscapeGraph graph;
    private final Node startNode;
    private final Node endNode;
    
    /**
     * Constructor 
     * @param state the escape state containing the graph and other relevant information
     * @param start the starting node
     * @param end   the target node
     */
    public EscapeBreadthFirstSearch(EscapeState state, Node start, Node end) {
        this.graph = new EscapeGraph(state);
        this.startNode = start;
        this.endNode = end;
    }

    
    /**
     * Checks the validity of the graph before performing BFS
     */
    private void checkGraphValidity() {
        if (graph.getUnweighted() == null || graph.getUnweighted().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        if(!graph.getUnweighted().containsKey(startNode) || !graph.getUnweighted().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using breadth-first search.
     * 
     * Finds the shortest path from the start node to the end node.
     * @return the shortest path from start to end, or an empty list if no path exists
     */
    @Override
    public EscapePath findEscapePath() {
        // Check if graph is empty or null
        checkGraphValidity();

        // BFS initialization
        // Queue for BFS and map to track their parents and a set to track visited nodes
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        // Start BFS from the start node
        queue.add(startNode);
        visited.add(startNode);

        boolean found = false;

        // Perform BFS until the queue is empty or we find the target node
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            // If we have reached the end node, stop the search
            if (current.equals(endNode)) {
                found = true;
                break;
            }

            // Traverse neighbors of the current node, adding unvisited ones to the queue
            for (Node neighbour : graph.getUnweighted().getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    parentMap.put(neighbour, current);
                    queue.add(neighbour);
                }
            }
        }

        // If we did not find the end node, return an empty path
        if(!found) {
            return new EscapePath(Collections.emptyList()); // No path found
        }

        // Reconstruct the path from end to start using the parent map
        List<Node> path = new LinkedList<>();
        for (Node node = endNode; node != null; node = parentMap.get(node)) {
            path.addFirst(node); // Add to the front of the list
        }

        return new EscapePath(path);
    }

}
