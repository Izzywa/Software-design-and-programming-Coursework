package student.escape;

import game.Node;

public class BranchState {
    private final Node currentNode;
    private final int currentCost;
    private final int currentGold;
    private final int totalGraphGold;
        /**
     * BranchState record to encapsulate branch-specific data
     * 
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     * @param currentGold total gold found along this branch
     * @param totalGraphGold total gold remaining on graph
     */
    public BranchState(Node currentNode, int currentCost, int currentGold, int totalGraphGold) {
        this.currentNode = currentNode;
        this.currentCost = currentCost;
        this.currentGold = currentGold;
        this.totalGraphGold = totalGraphGold;
    }

    /**
     * Creates a new BranchState by moving to a neighbour node.
     *
     * @param neighbour the neighbour node to move to
     * @param edgeLength the cost to move to the neighbour node
     * @param goldOnNode the amount of gold on the neighbour node
     * @return a new BranchState representing the state after moving to the neighbour node
     */
    public BranchState moveTo(Node neighbour, int edgeLength, int goldOnNode) {
        return new BranchState(
            neighbour,
            this.getCurrentCost() + edgeLength,
            this.getCurrentGold() + goldOnNode,
            this.getTotalGraphGold() - goldOnNode
        );
    }

    /**
     * Returns the current node being explored.
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Returns the cost to reach the current node.
     */
    public int getCurrentCost() {
        return currentCost;
    }

    /**
     * Returns the total gold found along this branch.
     */
    public int getCurrentGold() {
        return currentGold;
    }

    /**
     * Returns the total gold remaining on the graph.
     */
    public int getTotalGraphGold() {
        return totalGraphGold;
    }

}
