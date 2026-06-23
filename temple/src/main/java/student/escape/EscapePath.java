package student.escape;

import game.Edge;
import game.Node;
import java.util.List;

/**
 * Represents an escape path in the escape game with associated total costs (in terms of time) and
 * total amount of gold found along the path.
 * The total cost is calculated as the sum of the lengths of the edges along the path, 
 * while the total gold is calculated as the sum of the original gold amounts on the tiles of the nodes along the path.
 */
public class EscapePath {
    private final List<Node> path;
    private final int totalCost;
    private final int totalGold;

    /**
     * Constructor for the EscapePath class.
     * @param path the list of nodes representing the path
     */
    public EscapePath(List<Node> path) {
        this.path = path;
        this.totalCost = calculateTotalCost(path); // Initialize total cost
        this.totalGold = calculateTotalGold(path); // Initialize total gold
    }

    /**
     * Calculates the total cost of the path.
     * @param path the list of nodes representing the path
     * @return the total cost
     */
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

    /**
     * Calculates the total amount of gold found along the path.
     * @param path the list of nodes representing the path
     * @return the total gold
     */
    private int calculateTotalGold(List<Node> path) {
        int gold = 0;
        for (Node node : path) {
            gold += node.getTile().getOriginalGold();
        }
        return gold;
    }

    /**
     * Returns the path.
     * @return the list of nodes representing the path
     */
    public List<Node> getPath() {
        return path;
    }

    /**
     * Returns the total cost of the path.
     * @return the total cost
     */
    public int getTotalCost() {
        return totalCost;
    }

    /**
     * Returns the total amount of gold found along the path.
     * @return the total gold amount
     */
    public int getTotalGold() {
        return totalGold;
    }
}
