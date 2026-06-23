package student.escape;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;

import game.EscapeState;
import game.Node;
import game.Edge;


/**
 * Finds the shortest path between two nodes in a weighted graph using Dijkstra's algorithm.
 * Reference: <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Wikipedia Dijkstra's Algorithm</a>
 * 
 * Pseudocode:
 * procedure Dijkstra(Graph, source):
 *    Q ← Queue storing vertex priority
 *
 *    dist[source] ← 0                          // Initialization
 *    Q.add_with_priority(source, 0)            // associated priority equals dist[·]
 * 
 *    for each vertex v in Graph.Vertices:
 *      if v ≠ source                           // Initialization
 *          dist[v] ← infinity                  // Unknown distance from source to v
 *          prev[v] ← undefined                 // Previous node in optimal path from source
 *          Q.add_with_priority(v, infinity)    // All nodes initially in Q
 *     while Q is not empty:                    // The main loop
 *       u ← Q.extract_min()                    // Remove and return best vertex
 *       for each edge (u, v) :                 // Go through all v neighbors of u
 *          alt ← dist[u] + length(u, v)
 *          if alt < dist[v]:                   // A shorter path to v has been found
 *             prev[v] ← u
 *             dist[v] ← alt
 *             Q.decrease_priority(v, alt)      // Reorder v in the Queue
 *     
 *     return dist[], prev[]
 *       
 * To reconstruct the shortest path from source to target, we can use the prev[] map:
 * procedure reconstruct_path(prev, target):
 *    S ← empty sequence
 *    u ← target
 *    if prev[u] is defined or u = source:      // Proceed if the vertex is reachable
 *      while u is defined:                     // Construct shortest path with stack S
 *        S.push(u)                             // Push the vertex onto the stack
 *        u ← prev[u]                           // Traverse from target to source
 * 
 */
public class EscapeDijkstra implements EscapeStrategy {
    private final EscapeGraph graph;
    private final Node startNode;
    private final Node endNode;
    private Map<Node, Node> parentMap;
    private Map<Node, Integer> distanceMap;

    /**
     * Constructor 
     * @param state the escape state containing the graph and other relevant information
     * @param start the starting node
     * @param end   the target node
     */
    public EscapeDijkstra(EscapeState state, Node start, Node end) {
        this.graph = new EscapeGraph(state);
        this.startNode = start;
        this.endNode = end;
        this.parentMap = new HashMap<>();
        this.distanceMap = new HashMap<>();
    }

    /**
     * Checks the validity of the graph before performing Dijkstra's algorithm
     * Ensures that the graph is not null or empty, and that the start and end nodes exist in the graph
     */
    private void checkGraphValidity() {
        // Check if the graph is null or empty
        if (graph.getWeighted() == null || graph.getWeighted().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        // Check if start and end nodes are in the graph
        if (!graph.getWeighted().containsKey(startNode) || !graph.getWeighted().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }
    }

    /**
     * Performs Dijkstra's algorithm to find the shortest path from start to end
     * Updates the distance map and parent map accordingly during the search process
     */
    private void searchGraph() {
        // Initialize distances to infinity and parents to null, except for the start node
        for (Node node : graph.getWeighted().keySet()) {
            distanceMap.put(node, Integer.MAX_VALUE);
            parentMap.put(node, null);
        }
        distanceMap.put(startNode, 0);

        // Priority queue to select the node with the smallest distance
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> 
        Integer.compare(distanceMap.get(a), distanceMap.get(b)));
        pq.add(startNode);

        // Dijkstra's algorithm main loop
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.equals(endNode)) {
                break; // Found the shortest path to the end node
            }

            // Traverse neighbors of the current node, updating distances and parents as needed
            for (Edge edge : graph.getWeighted().getOrDefault(current, Collections.emptyList())) {
                Node neighbour = edge.getDest();
                int newDist = distanceMap.get(current) + edge.length();

                if (newDist < distanceMap.get(neighbour)) {
                    distanceMap.put(neighbour, newDist);
                    parentMap.put(neighbour, current);
                    pq.add(neighbour); // Add the neighbor to the priority queue
                }
            }
        }
    }


    /**
     * Implements EscapeStrategy interface to find the escape path using Dijkstra's algorithm.
     * 
     * Finds the shortest path from the start node to the end node.
     * @return the shortest path from start to end, or an empty list if no path exists
     */
    @Override
    public EscapePath findEscapePath() {
        // Check if graph is empty or null
        checkGraphValidity();

        // Perform Dijkstra's algorithm to find the path from start to end
        searchGraph();

        // If the distance to the end node is still infinity, there is no path
        if (distanceMap.get(endNode) == Integer.MAX_VALUE) {
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
