package student.escape.experiment;

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
import student.escape.EscapeGraph;
import student.escape.EscapePath;
import student.escape.EscapeStrategy;

/**
 * Class that implements the Breadth-first search algorithm to find the shortest path 
 * from start to end in an unweighted graph.
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
    private Queue<Node> queue;
    private Map<Node, Node> parentMap;
    private Set<Node> visited;
    private boolean foundPath;
    
    /**
     * No-args constructor for EscapeBreadthFirstSearch class
     */
    public EscapeBreadthFirstSearch() {
        this.queue = new LinkedList<>();
        this.parentMap = new HashMap<>();
        this.visited = new HashSet<>();
        this.foundPath = false;
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using breadth-first search.
     * Finds the shortest path from the start node to the end node.
     * 
     * @param state the current escape state
     * @return the shortest path from start to end, or an empty list if no path exists
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        EscapeGraph graph = new EscapeGraph(state);
        // Check if graph is empty or null
        try {
            graph.checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Perform BFS to find the path from start to end
        breadthFirstSearch(graph);

        // If we did not find the end node, return an empty path
        if (!foundPath) {
            return new EscapePath(state, Collections.emptyList()); // No path found
        }

        // Reconstruct the path from end to start using the parent map
        List<Node> path = new LinkedList<>();
        for (Node node = graph.getExitNode(); node != null; node = parentMap.get(node)) {
            path.addFirst(node); // Add to the front of the list
        }

        return new EscapePath(state, path);
    }

    /**
     * Performs breadth-first search algorithm to find the path from start to end
     * Updates the queue, visited set, and parent map accordingly during the search process
     * 
     * @param graph graph for current escape state
     */
    public void breadthFirstSearch(EscapeGraph graph) {
        // Start BFS from the start node
        queue.add(graph.getStartNode());
        visited.add(graph.getStartNode());
        // Perform BFS until the queue is empty or we find the target node
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            // If we have reached the end node, stop the search
            if (current.equals(graph.getExitNode())) {
                foundPath = true;
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
    }


}
