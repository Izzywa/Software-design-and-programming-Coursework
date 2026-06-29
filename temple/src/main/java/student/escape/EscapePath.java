package student.escape;

import game.Edge;
import game.Node;
import game.EscapeState;
import java.util.List;

/**
 * Represents an escape path in the escape game with associated total costs (in terms of time) and
 * total amount of gold found along the path.
 * The total cost is calculated as the sum of the lengths of the edges along the path, 
 * while the total gold is calculated as the sum of the original gold amounts on the tiles of the nodes along the path.
 */
public class EscapePath {
    private final EscapeState state;
    private final List<Node> path;
    private final int totalCost;
    private final int totalGold;

    /**
     * Constructor for the EscapePath class.
     * 
     * @param state current EscapeState state
     * @param path the list of nodes representing the path
     */
    public EscapePath(EscapeState state, List<Node> path) {
        this.state = state;
        this.path = path;
        this.totalCost = calculateTotalCost(); // Initialize total cost
        this.totalGold = calculateTotalGold(); // Initialize total gold
    }

    /**
     * Calculates the total cost of the path.
     * 
     * @return the total cost
     */
    private int calculateTotalCost() {
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
     * 
     * @return the total gold
     */
    private int calculateTotalGold() {
        int gold = 0;
        for (Node node : path) {
            gold += node.getTile().getOriginalGold();
        }
        return gold;
    }

    /**
     * Returns the path.
     * 
     * @return the list of nodes representing the path
     */
    public List<Node> getPath() {
        return path;
    }

    /**
     * Returns the first node in the path.
     * 
     * @return a Node object at the beginning of the list
     */
    public Node getFirstNode() {
        return path.get(0);
    }

    /**
     * Returns the last node in the path.
     * 
     * @return a Node object at the endof the list
     */
    public Node getLastNode() {
        return path.get(path.size() - 1);
    }

    /**
     * Returns the total cost of the path.
     * 
     * @return the total cost
     */
    public int getTotalCost() {
        return totalCost;
    }

    /**
     * Returns the total amount of gold found along the path.
     * 
     * @return the total gold amount
     */
    public int getTotalGold() {
        return totalGold;
    }

    /**
     * Traverses the path and collects gold along the way.
     */
    public void traverseAndCollect() {    
    // Pick up gold on the starting node if it exists
        if (path.get(0).getTile().getGold() > 0) {
                state.pickUpGold();
        }

        // Follow the path to the exit, picking up gold along the way
        for (int i = 1; i < path.size(); i++) {
            state.moveTo(path.get(i));
            if (state.getCurrentNode().getTile().getGold() > 0) {
                state.pickUpGold();
            }

            if (state.getTimeRemaining() <= 0) {
                throw new RuntimeException("Time ran out before escaping!");
            }
        }
    }
}
