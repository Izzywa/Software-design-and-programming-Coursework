package student.escape;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import game.Edge;
import game.Node;

/**
 * Class that implements a Knapsack-style Depth-first search algorithm to find the best simple path from start to end 
 * in a weighted graph that satisfies the remaining time constraint and maximizes gold collected.
 * 
 * Algorithmic optimizations:
 * 1. Pruning branches if:
 *   a. Potential available gold less than already collected 
 *   b. Potential path is longer than time needed to exit based on Dijkstra's algorithm
 *   c. New branch is inferior in terms of time remaining and gold compared to already known branches
 * 2. Memoization to store and check paths visited earlier without recomputing them all the time during recursion. 
 * If a branch has been seen before with more or equal time left AND more or equal gold collected, 
 * then the branch is pruned as per 1.c.
 * 3. Neighbours are sorted in a greedy manner to improve memoization efficiency 
 * by discovering paths with higher potential gold values first.
 * 
 * Computational optimizations implemented in superclass KnapsackDFSBaseEscapeStrategy:
 * 1. Using a separate thread to perform the optimization search with a timeout to ensure responsiveness 
 * and prevent long-running computations from blocking the main thread.
 * 2. Using a fallback path (shortest path) if the optimization search takes too long or fails due to an error.
 * 3. Logging errors and falling back safely to ensure that the program continues to function 
 * even if the optimization search fails.
 */
public class KnapsackDFSSimpleEscapeStrategy extends KnapsackDFSBaseEscapeStrategy {

    /**
     * No-args constructor for the KnapsackDFSSimpleEscapeStrategy class.
     */
    public KnapsackDFSSimpleEscapeStrategy() {
        super();
    }

    /** 
     * Recursively searches the graph for the best simple path containing the most gold but within the time constraint 
     * from the current node to the end node.
     * However, it stops exploring a branch if:
     * 1. Potential available gold less than already collected
     * 2. Potential path is longer than time needed to exit based on Dijkstra's algorithm
     * 3. New branch is inferior in terms of time remaining and gold compared to already known branches
     * 
     * Computational steps:
     * 1. Checks if the current thread is interrupted due to timeout and throws an exception to stop the search.
     * 2. Checks if the current total cost + minimum time to exit from the current node exceeds total escape time
     * OR if the current gold + the remaining gold available in the graph is less than or equal to bestGold. 
     * If true, the branch is pruned.
     * 3. Memoization is used to store and check paths visited earlier 
     * without recomputing them all the time during recursion. 
     * If a branch has been seen before with more or equal time left AND more or equal gold collected, 
     * then the branch is pruned.
     * 4. If the end node is reached, the best gold and best path are updated 
     * if the currentGold is more than the bestGold stored so far.
     * 5. Neighbours are sorted in a greedy manner to improve memoization efficiency 
     * by discovering paths with higher potential gold values first.
     * 6. Main loop - For each neighbour, if it is unvisited and there is enough time budget:
     *      a. the neighbour is visited, 
     *      b. the current path and gold available on the map are updated in a new BranchState 
     *      before the recursive call.
     *      c. Recursively call knapsackDFS to explore the neighbour.
     *      d. After returning from the recursive call, the state changes are tracked back.
     * 
     * @param wrapper the EscapeStateWrapper object that contains the current escape state and graph
     * @param bState the current BranchState object that contains the current node, cost, gold collected, 
     * and remaining total graph gold
     * @param visited the set of nodes that have been visited in the current path to prevent cycles
     * @param currentPath the list of nodes that form the current path from start to the current node
     */
    @Override
    public void knapsackDFS(
        EscapeStateWrapper wrapper, 
        BranchState bState, 
        Set<Node> visited, 
        List<Node> currentPath) {
        
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Search cancelled due to timeout");
        }

        int minTimeToExit = wrapper.getMinDistanceToExit().getOrDefault(bState.getCurrentNode(), Integer.MAX_VALUE);
        int timeLeft = wrapper.getState().getTimeRemaining() - bState.getCurrentCost();

        if (bState.getCurrentCost() + minTimeToExit >= wrapper.getState().getTimeRemaining() 
            || bState.getCurrentGold() + bState.getTotalGraphGold() <= super.getBestGold()) { return; }


        if (super.shouldPruneBranch(bState.getCurrentNode(), timeLeft, bState.getCurrentGold())) {
            return;
        }

        if (bState.getCurrentNode().equals(wrapper.getGraph().getExitNode())) {
            if (bState.getCurrentGold() > super.getBestGold()) {
                super.setBestGold(bState.getCurrentGold());
                super.setBestPath(new ArrayList<>(currentPath));
            }
        }

        List<Edge> neighbours = super.sortNeighbours(wrapper, bState.getCurrentNode());

        for (Edge edge : neighbours) {
            Node neighbour = edge.getDest();
            int newCost = bState.getCurrentCost() + edge.length();
            int neighbourMinTime = wrapper.getMinDistanceToExit().getOrDefault(neighbour, Integer.MAX_VALUE);

            if (!visited.contains(neighbour) && newCost + neighbourMinTime < wrapper.getState().getTimeRemaining()) {
                int goldOnNode = wrapper.getGraph().getGoldMap().getOrDefault(neighbour, 0);

                visited.add(neighbour);
                currentPath.add(neighbour);
                BranchState nextState = bState.moveTo(neighbour, edge.length(), goldOnNode);

                knapsackDFS(wrapper, nextState, visited, currentPath);

                visited.remove(neighbour);
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }
}
