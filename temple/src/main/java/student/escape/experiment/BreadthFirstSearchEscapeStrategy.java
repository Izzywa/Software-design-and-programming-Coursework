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
public class BreadthFirstSearchEscapeStrategy implements EscapeStrategy {
    /** Queue to hold nodes to be explored in BFS order. */
    private Queue<Node> queue;

    /** Map to track the parent of each node for path reconstruction. */
    private Map<Node, Node> parentMap;

    /** Set to track visited nodes to avoid cycles. */
    private Set<Node> visited;

    /** Flag to indicate if a path to the exit has been found. */
    private boolean foundPath;
    
    /**
     * No-args constructor for BreadthFirstSearchEscapeStrategy class
     */
    public BreadthFirstSearchEscapeStrategy() {
        this.queue = new LinkedList<>();
        this.parentMap = new HashMap<>();
        this.visited = new HashSet<>();
        this.foundPath = false;
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using breadth-first search.
     * Finds the shortest path from the start node to the end node.
     * 1. Check if the graph is valid and not empty
     * 2. Perform BFS to find the path from start to end
     * 3. If no path is found, return an empty EscapePath object
     * 4. If a path is found, reconstruct the path using the parent map
     * 5. Return the EscapePath object containing the path from start to end
     * 
     * @param state the current escape state
     * @return the shortest path from start to end, or an empty list if no path exists
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        EscapeGraph graph = new EscapeGraph(state);

        try {
            graph.checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        breadthFirstSearch(graph);

        if (!foundPath) {
            return new EscapePath(state, Collections.emptyList());
        }

        List<Node> path = new LinkedList<>();
        for (Node node = graph.getExitNode(); node != null; node = parentMap.get(node)) {
            path.addFirst(node);
        }

        return new EscapePath(state, path);
    }

    /**
     * Performs breadth-first search algorithm to find the path from start to end
     * Updates the queue, visited set, and parent map accordingly during the search process
     * 1. Start BFS from the start node
     * 2. Explore neighbors in a breadth-first manner until the queue is empty or we find the target node
     * 3. Traverse neighbors of the current node, adding unvisited ones to the queue
     * 4. Track visited nodes and store the parent of each visited node for path reconstruction
     * 
     * @param graph graph for current escape state
     */
    public void breadthFirstSearch(EscapeGraph graph) {
        queue.add(graph.getStartNode());
        visited.add(graph.getStartNode());
   
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(graph.getExitNode())) {
                foundPath = true;
                break;
            }

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
