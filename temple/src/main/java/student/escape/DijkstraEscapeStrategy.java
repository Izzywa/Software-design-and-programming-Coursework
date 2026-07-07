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
 *          if alt less than dist[v]:           // A shorter path to v has been found
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
public class DijkstraEscapeStrategy implements EscapeStrategy {
    /** Maps each node to its parent node in the shortest path tree. */
    private Map<Node, Node> parentMap;

    /** Maps each node to its distance from the start node. */
    private Map<Node, Integer> distanceMap;

    /**
     * No-args constructor for DijkstraEscapeStrategy class
     */
    public DijkstraEscapeStrategy() {
        this.parentMap = new HashMap<>();
        this.distanceMap = new HashMap<>();
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using Dijkstra's algorithm.
     * Finds the shortest path from the start node to the end node.
     * 1. Check the validity of the graph before performing Dijkstra's algorithm
     * 2. Perform Dijkstra's algorithm to find the shortest path from start to end
     * 3. If the distance to the end node is still infinity, return an empty path
     * 4. Reconstruct the path from end to start using the parent map and return it
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

        dijkstra(graph);

        if (distanceMap.get(graph.getExitNode()) == Integer.MAX_VALUE) {
            return new EscapePath(state, Collections.emptyList());
        }

        List<Node> path = new LinkedList<>();
        for (Node node = graph.getExitNode(); node != null; node = parentMap.get(node)) {
            path.addFirst(node);
        }

        return new EscapePath(state, path);
    }

    /**
     * Performs Dijkstra's algorithm to find the shortest path from start to end
     * Updates the distance map and parent map accordingly during the search process
     * 1. Initialize distances to infinity and parents to null, except for the start node
     * 2. Use a priority queue to explore nodes with the smallest distance first
     * 3. While the priority queue is not empty, extract the node with the smallest distance
     * 4. For each neighbor of the current node, calculate the distance 
     * and update if it's shorter than the previously recorded distance
     * 5. Continue until the end node is reached
     * 
     * @param graph graph for current escape state
     */
    public void dijkstra(EscapeGraph graph) {
        
        for (Node node : graph.getWeighted().keySet()) {
            distanceMap.put(node, Integer.MAX_VALUE);
            parentMap.put(node, null);
        }
        distanceMap.put(graph.getStartNode(), 0);

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> 
        Integer.compare(distanceMap.get(a), distanceMap.get(b)));
        pq.add(graph.getStartNode());

        
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.equals(graph.getExitNode())) {
                break;
            }

            for (Edge edge : graph.getWeighted().getOrDefault(current, Collections.emptyList())) {
                Node neighbour = edge.getDest();
                int newDist = distanceMap.get(current) + edge.length();

                if (newDist < distanceMap.get(neighbour)) {
                    distanceMap.put(neighbour, newDist);
                    parentMap.put(neighbour, current);
                    pq.add(neighbour);
                }
            }
        }
    }
}
