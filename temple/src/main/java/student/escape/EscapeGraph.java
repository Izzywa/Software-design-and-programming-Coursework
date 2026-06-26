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
 * Constructs the follwoing representations of the graph based on escape state:
 * -unweighted
 * -weighted
 * -inverted weighted
 * -gold map
 * The weighted and inverted graphs are used for optimal pathfinding, 
 * while the unweighted graph is used for basic connectivity checks.
 */
public class EscapeGraph {
    private final Node startNode;
    private final Node exitNode;
    private Map<Node, Collection<Edge>> weighted;
    private Map<Node, Collection<Node>> unweighted;
    private Map<Node, Collection<Edge>> invertedWeighted;
    private Map<Node, Integer> goldMap;
    private final int totalGraphGold;
    
    /**
     * Constructor for the EscapeGraph class.
     * 
     * @param state the escape state containing the graph information
     */
    public EscapeGraph(EscapeState state) {
        startNode = state.getCurrentNode();
        exitNode = state.getExit();
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
        totalGraphGold = goldMap.values().stream().mapToInt(value -> value == null ? 0 : value).sum();
        invertedWeighted = createInvertedGraph(weighted);
    }

    /**
     * Checks the validity of the graph before performing Dijkstra's algorithm
     * Ensures that the graph is not null or empty, and that the start and end nodes exist in the graph
     */
    public void checkGraphValidity() throws IllegalArgumentException {
        // Check if the graph is null or empty
        if (weighted == null || weighted.isEmpty()) {
            throw new IllegalArgumentException("Weighted graph cannot be null or empty");
        }
        if (unweighted == null || unweighted.isEmpty()) {
            throw new IllegalArgumentException("Unweighted graph cannot be null or empty");
        }
        if (goldMap == null || goldMap.isEmpty()) {
            throw new IllegalArgumentException("Gold map cannot be null or empty");
        }
        if (invertedWeighted == null || invertedWeighted.isEmpty()) {
            throw new IllegalArgumentException("Inverted weighted graph cannot be null or empty");
        }
        

        // Check if start and end nodes are in the graph
        if (!weighted.containsKey(startNode) || !weighted.containsKey(exitNode)) {
            throw new IllegalArgumentException("Start or exit node does not exist in weighted graph");
        }
        if (!unweighted.containsKey(startNode) || !unweighted.containsKey(exitNode)) {
            throw new IllegalArgumentException("Start or exit node does not exist in unweighted graph");
        }
        if (!goldMap.containsKey(startNode) || !goldMap.containsKey(exitNode)) {
            throw new IllegalArgumentException("Start or exit node does not exist in gold map");
        }
        if (!invertedWeighted.containsKey(startNode) || !invertedWeighted.containsKey(exitNode)) {
            throw new IllegalArgumentException("Start or exit node does not exist in inverted weighted graph");
        }
    }

    /**
     * Creates an inverted version of a weighte dgraph.
     * This is required for algorithms which traverse directed graphs backwards from end node to start node.
     * 
     * @param weightedGraph a weighted representation of a graph
     * @return a map of nodes to their incident edges with weights
     */
    public static Map<Node, Collection<Edge>> createInvertedGraph(Map<Node, Collection<Edge>> weightedGraph) {
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
     * 
     * @return a map of nodes to their incident edges with weights
     */
    public Map<Node, Collection<Edge>> getInvertedWeighted() {
        return invertedWeighted;
    }

    /**
     * Returns the weighted representation of the graph.
     * 
     * @return a map of nodes to their incident edges with weights
     */
    public Map<Node, Collection<Edge>> getWeighted() {
        return weighted;
    }

    /**
     * Returns the unweighted representation of the graph.
     * 
     * @return a map of nodes to their neighboring nodes
     */
    public Map<Node, Collection<Node>> getUnweighted() {
        return unweighted;
    }

    /**
     * Returns the map of nodes to their gold values.
     * 
     * @return a map of nodes to their gold values
     */
    public Map<Node, Integer> getGoldMap() {
        return goldMap;
    }

    /**
     * Returns the total amount of gold on the graph.
     * 
     * @return an integer value for the total gold amount on the graph
     */
    public int getTotalGold() {
        return totalGraphGold;
    }

    /**
     * Returns the start node for current escape graph.
     * 
     * @return a Node object where the escape state starts from
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * Returns the exit node for current escape graph.
     * 
     * @return a Node object where the exit is for this escape state
     */
    public Node getExitNode() {
        return exitNode;
    }
}
