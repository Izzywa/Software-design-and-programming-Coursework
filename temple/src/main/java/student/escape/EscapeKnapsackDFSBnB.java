package student.escape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.Edge;
import game.Node;
import game.EscapeState;

/**
 * Class that implements a Knapsack-style Depth-first search algorithm with Branch and Bound to find the best path 
 * from start to end in a weighted graph that satisfies the remaining time constraint and maximizes gold collected.
 * Branch and Bound is a search algorithm that explores the solution space by creating branches for each decision 
 * and uses bounds to prune branches that cannot yield better solutions than the best one found so far.
 * Pruning algorithms implemented in class:
 *      1. Potential available gold less than already collected
 *      2. Potential path is longer than time needed to exit based on Dijkstra's algorithm
 *      3. Memoization-based pruning of inferior branches
 */
public class EscapeKnapsackDFSBnB extends EscapeKnapsackDFSBase {

    /**
     * No-args constructor for the EscapeKnapsackDFSBnB class.
     */
    public EscapeKnapsackDFSBnB() {
        super();
    }

    /**
     * Implements EscapeStrategy interface to find the escape path using the DFS algorithm with pruning.
     * Finds the best path in the graph after searching is assisted by pruning unuseful 
     * or illegal branches during recursive DFS path discovery.
     * 
     * @param state the current escape state
     * @return the best possible path from start to end or the shortest path if no valid paths are found
     */
    @Override
    public EscapePath findEscapePath(EscapeState state) {
        //Initialize graph
        EscapeStateWrapper wrapper = new EscapeStateWrapper(state);
        int totalGraphGold = wrapper.getGraph().getTotalGold();

        // Handling test egde case where there's no gold on map
        // Theoretical possibility for smaller maps with P = 0.33 ^ node count 
        // (Map with 10 nodes has P = 0.0000153 (0.0015%) probability that no node has gold.)
        if (totalGraphGold == 0) {
            EscapeStrategy dijkstra = new EscapeDijkstra();
            return dijkstra.findEscapePath(state);
        }

        // Check graph validity
        try {
            wrapper.getGraph().checkGraphValidity();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Initialize search with start node and total graph gold
        Set<Node> visited = new HashSet<>();
        List<Node> currentPath = new ArrayList<>();

        int startGold = wrapper.getGraph().getGoldMap().getOrDefault(wrapper.getGraph().getStartNode(), 0);
        visited.add(wrapper.getGraph().getStartNode());
        currentPath.add(wrapper.getGraph().getStartNode());

        // Create initial BranchState
        BranchState initialState = new BranchState(
            wrapper.getGraph().getStartNode(),
            0,
            startGold,
            totalGraphGold - startGold
        );

        // Run recursive search from start node
        knapsackDFS(wrapper, initialState, visited, currentPath);
        // Return best path
        return new EscapePath(state, super.getBestPath());
    }

    /** 
     * Recursively searches the graph for all possible paths from the current node to the end node.
     * However, it stops exploring a branch if:
     *    1. Potential available gold less than already collected
     *    2. Potential path is longer than time needed to exit based on Dijkstra's algorithm
     *    3. New branch is inferior in terms of time remaining and gold compared to already known branches
     * the remaining time is exceeded a certain limit and the branch is pruned
     * 
     * @param state the current escape state
     * @param graph graph for current escape state
     * @param currentNode the current node being explored
     * @param currentCost the cost to reach the current node
     * @param currentGold total gold found along this branch
     * @param totalGraphGold total gold on graph
     */
    @Override
    public void knapsackDFS(EscapeStateWrapper wrapper, BranchState bState, Set<Node> visited, List<Node> currentPath) {
        int minTimeToExit = wrapper.getMinDistanceToExit().getOrDefault(bState.getCurrentNode(), Integer.MAX_VALUE);
        int timeLeft = wrapper.getState().getTimeRemaining() - bState.getCurrentCost();
        // Check if 
        // 1. current total cost + minimum time (shortest path) from current node exceeds total escape time
        // OR
        // 2. current gold + the remaining gold available in the graph is less than or equal to bestGold
        // If true, we prune the branch
        if (bState.getCurrentCost() + minTimeToExit >= wrapper.getState().getTimeRemaining() 
            || bState.getCurrentGold() + bState.getTotalGraphGold() <= super.getBestGold()) { return; }

        // Memoization can be used to store and check paths visited earlier 
        // without recomputing them all the time during recursion
        // If we've seen this branch before with more or equal time left AND more or equal gold collected,
        // then we prune the branch
        if (shouldPruneBranch(bState.getCurrentNode(), timeLeft, bState.getCurrentGold())) {
            return;
        }

        // Base case for recursion: end node reached
        // Update best gold and best path if currentGold is more than the bestGold stored so far
        if (bState.getCurrentNode().equals(wrapper.getGraph().getExitNode())) {
            if (bState.getCurrentGold() > super.getBestGold()) {
                super.setBestGold(bState.getCurrentGold());
                super.setBestPath(new ArrayList<>(currentPath));
            }
        }

        // Memeoization efficiency can be improved if we discover paths with higher potantial gold values first
        // Greedy sorting neighbours
        List<Edge> neighbours = sortNeighbours(wrapper, bState.getCurrentNode());

        //Explore neighbours in for loop
        for (Edge edge : neighbours) {
            Node neighbour = edge.getDest();
            int newCost = bState.getCurrentCost() + edge.length();
            int neighbourMinTime = wrapper.getMinDistanceToExit().getOrDefault(neighbour, Integer.MAX_VALUE);
            // Check if neighbor is unvisited and we have enough time budget
            if (!visited.contains(neighbour) && newCost + neighbourMinTime < wrapper.getState().getTimeRemaining()) {
                // Visit and count gold on node
                int goldOnNode = wrapper.getGraph().getGoldMap().getOrDefault(neighbour, 0);

                // Update visited, current path and gold available on map before recursive call
                visited.add(neighbour);
                currentPath.add(neighbour);

                BranchState nextState = bState.moveTo(neighbour, edge.length(), goldOnNode);

                // Recurse
                knapsackDFS(wrapper, nextState, visited, currentPath);

                //Track back state changes after return from recursive call
                visited.remove(neighbour);
                currentPath.remove(currentPath.size() - 1);
            }
        }

    }
}
