package student.escape;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import game.Edge;
import game.Node;

/**
 * Class that implements a Knapsack-style Depth-first search algorithm to find the best path 
 * which can contain cycles from start to end in a weighted graph 
 * that satisfies the remaining time constraint and maximizes gold collected.
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
 * 4. We identify the immediate parent node we just came from to prevent advancing that direction and creating loops.
 * 5. If the neighbour is a zero-gold node and it takes us further from the exit, 
 * we skip moving into it unless we have a certain amount of spare time.
 * 
 * Computational optimizations implemented in superclass KnapsackDFSBaseEscapeStrategy:
 * 1. Using a separate thread to perform the optimization search with a timeout to ensure responsiveness 
 * and prevent long-running computations from blocking the main thread.
 * 2. Using a fallback path (shortest path) if the optimization search takes too long or fails due to an error.
 * 3. Logging errors and falling back safely to ensure that the program continues to function 
 * even if the optimization search fails.
 */
public class KnapsackDFSDetourEscapeStrategy extends KnapsackDFSBaseEscapeStrategy {
    /** 
     * Multiplier to determine the amount of spare time needed 
     * to explore zero-gold nodes that take us further from the exit.
     * 
     * Development system: The multiplier was set to 1.2 
     * and all league table seeds ran successfully within the time limit.
     * Production system (Codio): The multiplier was increased to 2.0 
     * and all league table seeds but one ran successfully within the time limit.
     * A value of 2.7 is required to run all league table seeds successfully 
     * within the time limit on the production system. However, this value is too high 
     * and results in suboptimal paths for some seeds.
     */
    private final double SPARE_TIME_MULTIPLIER = 2.0;

    /**
     * No-args constructor for the KnapsackDFSDetourEscapeStrategy class.
     */
    public KnapsackDFSDetourEscapeStrategy() {
        super();
    }

    /** 
     * Recursively searches the graph for the best path which can include cycles 
     * and contains the most gold but within the time constraint from the current node to the end node.
     * However, it stops exploring a branch if:
     * 1. Potential available gold less than already collected
     * 2. Potential path is longer than time needed to exit based on Dijkstra's algorithm
     * 3. New branch is inferior in terms of time remaining and gold compared to already known branches
     * 
     * Computational steps:
     * 1. Checks if the current thread is interrupted due to timeout and throws an exception to stop the search.
     * 2. Checks if the current total cost + minimum time to exit from the current node exceeds total escape time
     * OR if the current gold + the remaining gold available in the graph is less than or equal to bestGold.
     * 3. Memoization is used to store and check paths visited earlier 
     * without recomputing them all the time during recursion.
     * If a branch has been seen before with more or equal time left AND more or equal gold collected,
     * then the branch is pruned.
     * 4. If the end node is reached, the best gold and best path are updated 
     * if the currentGold is more than the bestGold stored so far.
     * 5. Neighbours are sorted in a greedy manner to improve memoization efficiency 
     * by discovering paths with higher potential gold values first.
     * 6. We identify the immediate parent node we just came from to prevent advancing that direction and creating loops.
     * 7. In the main loop, instead of excluding already visited neighbour nodes, we allow exploration of neighbour cells 
     * by tracking a local set of nodes belonging to the current path instead of a global set of visited nodes.
     * 8. Main loop - For each neighbour, if there is enough time budget:
     *      a. Check if node was already visited in the current path
     *      b. Get gold on the node, if already visited, set to 0
     *      c. If the neighbour is a zero-gold node and it takes us further from the exit, 
     *      we skip moving into it unless we have a certain amount of spare time.
     *      d. Update the inserted boolean flag, current path, 
     *      and gold available on the map in a new BranchState before the recursive call.
     *      e. Recursively call knapsackDFS to explore the neighbour.
     *      f. We track back state changes after returning from the recursive call 
     *      to ensure the search continues correctly.
     * 
     * @param wrapper the EscapeStateWrapper object that contains the current escape state and graph
     * @param bState the current BranchState object that contains the current node, cost, gold collected, 
     * and remaining total graph gold
     * @param visited the set of nodes that have been visited in the current path to track gold collection
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
        Node immediateParent = currentPath.size() >= 2 ? currentPath.get(currentPath.size() - 2) : null;

        for (Edge edge : neighbours) {
            Node neighbour = edge.getDest();

            if (neighbour.equals(immediateParent)) {
                continue;
            }

            int newCost = bState.getCurrentCost() + edge.length();
            int neighbourExitTime = wrapper.getMinDistanceToExit().getOrDefault(neighbour, Integer.MAX_VALUE);

            if (newCost + neighbourExitTime < wrapper.getState().getTimeRemaining()) {
                boolean alreadyVisited = visited.contains(neighbour);
                int goldOnNode = alreadyVisited ? 0 : wrapper.getGraph().getGoldMap().getOrDefault(neighbour, 0);

                if (goldOnNode == 0 && neighbourExitTime > minTimeToExit) {
                    if (timeLeft < minTimeToExit * SPARE_TIME_MULTIPLIER) {
                        continue; 
                    }
                }

                boolean inserted = visited.add(neighbour);
                currentPath.add(neighbour);
                BranchState nextState = bState.moveTo(neighbour, edge.length(), inserted ? goldOnNode : 0);

                knapsackDFS(wrapper, nextState, visited, currentPath);

                if (inserted) {
                    visited.remove(neighbour);
                }
                currentPath.remove(currentPath.size() - 1);    
            }
        }
    }
}
