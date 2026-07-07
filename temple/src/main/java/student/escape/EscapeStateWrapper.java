package student.escape;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import game.EscapeState;
import game.Node;
import game.Edge;

/**
 * Wrapper class for EscapeState related objects to provide more logical encapsulation
 */
public class EscapeStateWrapper {
    /** The current EscapeState object. */
    private final EscapeState state;

    /** The current EscapeGraph object that belongs to the EscapeState. */
    private final EscapeGraph graph;

    /** A lookup table containing nodes and their distances to the exit node. */
    private Map<Node, Integer> minDistanceToExit;

    /**
     * Constructor EscapeStateWrapper
     * @param state
     */
    public EscapeStateWrapper(EscapeState state) {
        this.state = state;
        this.graph = new EscapeGraph(this.state);
        this.minDistanceToExit = shortestDistancesToExit(this.graph);
    }

    /**
     * Returns the current Escape state.
     * 
     * @return current EscapeState
     */
    public EscapeState getState() {
        return state;
    }

    /**
     * Returns the current EscapeGraph that belongs to EscapeState.
     * 
     * @return current EscapeGraph
     */
    public EscapeGraph getGraph() {
        return graph;
    }

    /**
     * Returns a lookup table containing nodes and their distances to the exit node.
     * 
     * @return a map containing nodes and their distances to the exit node
     */
    public Map<Node, Integer> getMinDistanceToExit() {
        return minDistanceToExit;
    }

    /**
     * Dijktra's algorithm with priority queue implementation to create lookup table 
     * with shortest distances from each node to the exit node
     * This algorithm traverses the graph backwards from end node towards the start node
     * 1. Initialize a priority queue and a map to store shortest distances
     * 2. Add the exit node to the priority queue with distance 0
     * 3. While the priority queue is not empty, pop the node with the smallest distance
     * 4. For each neighbor of the current node, calculate the distance to the exit node
     * 5. If the calculated distance is smaller than the current stored distance, 
     * update the map and add the neighbor to the priority queue
     * 6. Return the map containing nodes and their shortest distances to the exit node
     * 
     * @param graph graph for current escape state
     * @return a map that contains nodes and their shortest distances to the exit node
     */
    private Map<Node,Integer> shortestDistancesToExit(EscapeGraph graph) {
        Map<Node,Integer> shortestDistLookupMap = new HashMap<>();
        PriorityQueue<NodeDistancePair> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        pq.add(new NodeDistancePair(graph.getExitNode(), 0));
        shortestDistLookupMap.put(graph.getExitNode(), 0);

        while (!pq.isEmpty()) {
            NodeDistancePair current = pq.poll();

            
            if (current.distance > shortestDistLookupMap.getOrDefault(current.node, Integer.MAX_VALUE)) {
                continue;
            }

            for (Edge edge : graph.getInvertedWeighted().getOrDefault(current.node, Collections.emptyList())) {
                Node neighbour = edge.getDest();
                int newDist = current.distance + edge.length();
                if (newDist < shortestDistLookupMap.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    shortestDistLookupMap.put(neighbour, newDist);
                    pq.add(new NodeDistancePair(neighbour, newDist));
                }
            }
            
        }

        return shortestDistLookupMap;
    }
    
    /**
     * Inner helper class which stores nodes and shortest distances to populate priority queue 
     * that prioritizes nodes by distances
     */
    private static class NodeDistancePair {
        /** The node in the graph. */
        Node node;
        /** The distance from the node to the exit node. */
        int distance;

        /** 
         * Constructor
         * @param node current node
         * @param distance distance from current node to a certain node
         */
        NodeDistancePair(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
