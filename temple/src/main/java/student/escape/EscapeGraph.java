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
