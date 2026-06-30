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
    private final EscapeState state;
    private final EscapeGraph graph;
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
    public EscapeState getEscapeState() {
        return state;
    }

    /**
     * Returns the current EscapeGraph that belongs to EscapeState.
     * 
     * @return current EscapeGraph
     */
    public EscapeGraph getEscapeGraph() {
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
        Node node;
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
