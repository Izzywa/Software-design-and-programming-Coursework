package student;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Collectors;

import game.EscapeState;
import game.Node;

public class EscapeDFSAllPaths implements EscapeStrategy {
    private final EscapeState state;
    private final EscapeGraph graph;
    private final Node startNode;
    private final Node endNode;
    private List<EscapePath> allPaths;
    private Set<Node> visited;
    private List<Node> currentPath;

    public EscapeDFSAllPaths(EscapeState state, Node start, Node end) {
        this.state = state;
        this.graph = new EscapeGraph(state);
        this.startNode = start;
        this.endNode = end;
        this.allPaths = new ArrayList<>();
        this.visited = new HashSet<>();
        this.currentPath = new ArrayList<>();
    }

    /**
     * Checks the validity of the graph before performing DFS algorithm
     * Ensures that the graph is not null or empty, and that the start and end nodes exist in the graph
     */
    private void checkGraphValidity() {
        if (graph.getWeighted() == null || graph.getWeighted().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        // Check if start and end nodes are in the graph
        if(!graph.getWeighted().containsKey(startNode) || !graph.getWeighted().containsKey(endNode)) {
            throw new IllegalArgumentException("Start or end node does not exist in the graph");
        }
    }

    private void searchGraph(Node currentNode) {
        visited.add(currentNode);
        currentPath.add(currentNode);

        if (currentNode.equals(endNode)) {
;
            allPaths.add(new EscapePath(new ArrayList<>(currentPath)));
        } else{
            for (Node neighbor : graph.getUnweighted().getOrDefault(currentNode, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    searchGraph(neighbor);
                }
            }
        }

        visited.remove(currentNode);
        currentPath.remove(currentPath.size() - 1);
    }

    private List<EscapePath> filterPaths(List<EscapePath> paths) {
        List<EscapePath> validPaths = paths.stream()
                .filter(path -> path.getTotalCost() <= state.getTimeRemaining())
                .collect(Collectors.toList());
        return validPaths;
    }

    private EscapePath selectBestPath(List<EscapePath> paths) {
        return paths.stream()
                .max((p1, p2) -> {
                    if (p1.getTotalGold() != p2.getTotalGold()) {
                        return Integer.compare(p1.getTotalGold(), p2.getTotalGold());
                    } else {
                        return Integer.compare(p2.getTotalCost(), p1.getTotalCost());
                    }
                })
                .orElse(null);
    }


    @Override
    public EscapePath findEscapePath() {
        checkGraphValidity();
        searchGraph(startNode);
        System.out.println("All paths found: " + allPaths.size());
        List<EscapePath> validPaths = filterPaths(allPaths);
        return selectBestPath(validPaths);
    }

}
