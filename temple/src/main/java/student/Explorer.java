package student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Set;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;
import game.Edge;

public class Explorer {

    private Set<Long> discovered;

    public Explorer() {
        discovered = new HashSet<>();
    }

    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        depthFirstSearch(state);
    }

    /**
     * Depth-first search algorithm
     * Reference: <a href="https://en.wikipedia.org/wiki/Depth-first_search">Wikipedia DFS</a>
     *
     * <pre>
     * procedure DFS(G, v) is
     *     label v as discovered
     *     for all directed edges from v to w that are in G.adjacentEdges(v) do
     *         if vertex w is not labeled as discovered then
     *             recursively call DFS(G, w)
     * </pre>
     *
     * @param state the exploration state
     * @return true if the orb is found, false otherwise
     */
    private boolean depthFirstSearch(ExplorationState state) {
        long current = state.getCurrentLocation();
        discovered.add(current);

        if (state.getDistanceToTarget() == 0) {
            return true;
        }

        List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
        Collections.sort(neighbours);
        for (var neighbour : neighbours) {
            long neighbourId = neighbour.nodeID();
            if (!discovered.contains(neighbourId)) {
                state.moveTo(neighbourId);
                if (depthFirstSearch(state)) {
                    return true;
                }
                state.moveTo(current);
            }
        }

        return false;
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
        EscapeGraph graph = new EscapeGraph(state);

        EscapePath path = new EscapePath(shortestPathDijkstra(graph.getWeighted(), state.getCurrentNode(), state.getExit()));
         
        // Pick up gold on the starting node if it exists
        if(path.getPath().get(0).getTile().getGold() > 0) {
                state.pickUpGold();
        }

        // Follow the path to the exit, picking up gold along the way
        for (int i = 1; i < path.getPath().size(); i++) {
            state.moveTo(path.getPath().get(i));
            if(state.getCurrentNode().getTile().getGold() > 0) {
                state.pickUpGold();
            }

            if (state.getTimeRemaining() <= 0) {
                throw new RuntimeException("Time ran out before escaping!");
            }
        }
    }

    /**
     * Breadth-first search algorithm to find the shortest path from start to end in an unweighted graph.
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
     *
     * @param graph the unweighted graph to search
     * @param start the starting node
     * @param end   the target node
     * @return the shortest path from start to end, or an empty list if no path exists
     */
    private List<Node> shortestPathBFS(Map<Node, Collection<Node>> graph, Node start, Node end) {
        // Check if graph is empty or null
        if (graph == null || graph.isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        // Check if start and end nodes are in the graph
        if(!graph.containsKey(start) || !graph.containsKey(end)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }

        // BFS initialization
        // Queue for BFS and map to track their parents and a set to track visited nodes
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        boolean found = false;

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            // If we have reached the end node, stop the search
            if (current.equals(end)) {
                found = true;
                break;
            }

            // Traverse neighbors of the current node, adding unvisited ones to the queue
            for (Node neighbour : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    parentMap.put(neighbour, current);
                    queue.add(neighbour);
                }
            }
        }
        if(!found) {
            return Collections.emptyList(); // No path found
        }

        // Reconstruct the path from end to start using the parent map
        List<Node> path = new LinkedList<>();
        for (Node node = end; node != null; node = parentMap.get(node)) {
            path.addFirst(node); // Add to the front of the list
        }

        return path;
    }

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
     *     
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
     *    
     *
     * @param graph the weighted graph to search
     * @param start the starting node
     * @param end   the target node
     * @return the shortest path from start to end, or an empty list if no path exists
     */
    private List<Node> shortestPathDijkstra(Map<Node, Collection<Edge>> graph, Node start, Node end) {  
        // Check if graph is empty or null
        if (graph == null || graph.isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        // Check if start and end nodes are in the graph
        if(!graph.containsKey(start) || !graph.containsKey(end)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }

        // Dijkstra's algorithm initialization
        // Map to track the shortest distance to each node and the parent of each node in the shortest path
        Map<Node, Node> parentMap = new HashMap<>();
        Map<Node, Integer> distanceMap = new HashMap<>();

        // Initialize distances to infinity and parents to null, except for the start node
        for (Node node : graph.keySet()) {
            distanceMap.put(node, Integer.MAX_VALUE);
            parentMap.put(node, null);
        }
        distanceMap.put(start, 0);

        // Priority queue to select the node with the smallest distance
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Integer.compare(distanceMap.get(a), distanceMap.get(b)));
        pq.add(start);

        // Dijkstra's algorithm main loop
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.equals(end)) {
                break; // Found the shortest path to the end node
            }

            // Traverse neighbors of the current node, updating distances and parents as needed
            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                Node neighbour = edge.getDest();
                int newDist = distanceMap.get(current) + edge.length();

                if (newDist < distanceMap.get(neighbour)) {
                    distanceMap.put(neighbour, newDist);
                    parentMap.put(neighbour, current);
                    pq.add(neighbour); // Add the neighbor to the priority queue
                }
            }
        }

        // If the distance to the end node is still infinity, there is no path
        if (distanceMap.get(end) == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        // Reconstruct the path from end to start using the parent map
        List<Node> path = new LinkedList<>();
        for (Node node = end; node != null; node = parentMap.get(node)) {
            path.addFirst(node); // Add to the front of the list
        }

        return path;
    }
}
