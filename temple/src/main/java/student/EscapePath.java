package student;

import game.Edge;
import game.Node;
import java.util.List;

public class EscapePath implements Comparable<EscapePath> {
    private final List<Node> path;
    private final int totalCost;
    private final int totalGold;

    public EscapePath(List<Node> path) {
        this.path = path;
        this.totalCost = calculateTotalCost(path); // Initialize total cost
        this.totalGold = calculateTotalGold(path); // Initialize total gold
    }

    private int calculateTotalCost(List<Node> path) {
        int cost = 0;
        for (int i = 0; i < path.size(); i++) {
            Node currentNode = path.get(i);
            if (i < path.size() - 1) {
                Node nextNode = path.get(i + 1);
                Edge edge = currentNode.getEdge(nextNode);
                cost += edge.length();
            }
        }
        return cost;
    }

    private int calculateTotalGold(List<Node> path) {
        int gold = 0;
        for (Node node : path) {
            gold += node.getTile().getOriginalGold();
        }
        return gold;
    }

    public List<Node> getPath() {
        return path;
    }

    public int getTotalCost() {
        return totalCost;
    }
    
    public int getTotalGold() {
        return totalGold;
    }

    @Override
    public int compareTo(EscapePath other) {
        return Integer.compare(this.totalGold, other.totalGold);
    }
    
}
