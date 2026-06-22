package student.escape;

import game.EscapeState;
import game.Edge;
import game.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the graph for the escape game.
 * Constructs both weighted and unweighted representations of the graph based on the escape state.
 * The weighted graph is used for optimal pathfinding, while the unweighted graph is used for basic connectivity checks.
 */
public class EscapeGraph {
    private Map<Node, Collection<Edge>> weighted;
    private Map<Node, Collection<Node>> unweighted;
    private Map<Node, Collection<Edge>> invertedWeighted;
    private Map<Node, Integer> goldMap;
    
    /**
     * Constructor for the EscapeGraph class.
     * @param state the escape state containing the graph information
     */
    public EscapeGraph(EscapeState state) {
        weighted = new HashMap<>();
        unweighted = new HashMap<>();
        goldMap = new HashMap<>();
        for (var node : state.getVertices()) {
            List<Edge> edges = new ArrayList<>();
            List<Node> neighbours = new ArrayList<>();
            for (var anotherNode : node.getNeighbours()) {
                edges.add(node.getEdge(anotherNode));
                neighbours.add(anotherNode);
            }
            weighted.put(node, edges);
            unweighted.put(node, neighbours);
            goldMap.put(node, node.getTile().getOriginalGold());
        }
        invertedWeighted = createInvertedGraph(weighted);
    }

    /**
     * Creates an inverted version of a weighte dgraph.
     * This is required for algorithms which traverse directed graphs backwards from end node to start node.
     * @param weightedGraph a weighted representation of a graph
     * @return a map of nodes to their incident edges with weights
     */
    public Map<Node, Collection<Edge>> createInvertedGraph(Map<Node, Collection<Edge>> weightedGraph) {
        Map<Node, Collection<Edge>> inverted = new HashMap<>();

        // Initialize empty lists for every node present in the original graph
        for (Node src : weightedGraph.keySet()) {
            inverted.putIfAbsent(src, new ArrayList<>());
        }

        // Populate with reversed edges
        for (Map.Entry<Node, Collection<Edge>> entry : weightedGraph.entrySet()) {
            Node source = entry.getKey();
            for (Edge edge : entry.getValue()) {
                Node destination = edge.getDest();
                int weight = edge.length();
                // Create a reversed edge: destination -> source with same weight 
                Edge reversedEdge = new Edge(destination, source, weight);
                inverted.get(destination).add(reversedEdge);
            }
        }
        return inverted;
    }

    /**
     * Returns the inverted version of the weighted representation of the graph.
     * @return a map of nodes to their incident edges with weights
     */
    public Map<Node, Collection<Edge>> getInvertedWeighted() {
        return invertedWeighted;
    }

    /**
     * Returns the weighted representation of the graph.
     * @return a map of nodes to their incident edges with weights
     */
    public Map<Node, Collection<Edge>> getWeighted() {
        return weighted;
    }

    /**
     * Returns the unweighted representation of the graph.
     * @return a map of nodes to their neighboring nodes
     */
    public Map<Node, Collection<Node>> getUnweighted() {
        return unweighted;
    }

    /**
     * Returns the map of nodes to their gold values.
     * @return a map of nodes to their gold values
     */
    public Map<Node, Integer> getGoldMap() {
        return goldMap;
    }
}
