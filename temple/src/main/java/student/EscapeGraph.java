package student;

import game.EscapeState;
import game.Edge;
import game.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class EscapeGraph {
    private Map<Node, Collection<Edge>> weighted;
    private Map<Node, Collection<Node>> unweighted;
    
    public EscapeGraph(EscapeState state) {
        weighted = new HashMap<>();
        unweighted = new HashMap<>();
        for (var node : state.getVertices()) {
            List<Edge> edges = new ArrayList<>();
            List<Node> neighbours = new ArrayList<>();
            for (var anotherNode : node.getNeighbours()) {
                edges.add(node.getEdge(anotherNode));
                neighbours.add(anotherNode);
            }
            weighted.put(node, edges);
            unweighted.put(node, neighbours);
        }
    }

    public Map<Node, Collection<Edge>> getWeighted() {
        return weighted;
    }

    public Map<Node, Collection<Node>> getUnweighted() {
        return unweighted;
    }
}
