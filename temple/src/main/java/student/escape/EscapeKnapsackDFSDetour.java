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
public class EscapeKnapsackDFSDetour extends EscapeKnapsackDFSBase {
    private final double SPARE_TIME_MULTIPLIER = 1.25;

    /**
     * No-args constructor for the EscapeKnapsackDFSDetour class.
     */
    public EscapeKnapsackDFSDetour() {
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
        //Initialize EscapeState wrapper object
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

        // Initiliaze path-local set for storing vistied nodes
        Set<Node> pathVisited = new HashSet<>();
        List<Node> currentPath = new ArrayList<>();

        // Initialize search with start node and total graph gold
        int startGold = wrapper.getGraph().getGoldMap().getOrDefault(wrapper.getGraph().getStartNode(), 0);
        pathVisited.add(wrapper.getGraph().getStartNode());
        currentPath.add(wrapper.getGraph().getStartNode());
        // Create starting branch state bundle
        BranchState initialState = new BranchState(
            wrapper.getGraph().getStartNode(), 
            0, 
            startGold, 
            totalGraphGold - startGold
        );

        // Run recursive search from start node
        knapsackDFS(wrapper, initialState, pathVisited, currentPath);
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
     * @param wrapper the EscapeStateWrapper object that contains the current escape state and graph
     * @param bState the current BranchState object that contains the current node, cost, gold collected, 
     * and remaining total graph gold
     * @param visited the set of nodes that have been visited in the current path
     * @param currentPath the list of nodes that form the current path from start to the current node
     */
    public void knapsackDFS(
        EscapeStateWrapper wrapper, 
        BranchState bState, 
        Set<Node> pathVisited, 
        List<Node> currentPath) {

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
        if (super.shouldPruneBranch(bState.getCurrentNode(), timeLeft, bState.getCurrentGold())) {
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
        List<Edge> neighbours = super.sortNeighbours(wrapper, bState.getCurrentNode());

        // Identify the immediate parent node we just came from to prevent advancing that direction
        Node immediateParent = currentPath.size() >= 2 ? currentPath.get(currentPath.size() - 2) : null;

        //Explore neighbours in for loop
        for (Edge edge : neighbours) {
            Node neighbour = edge.getDest();

            if (neighbour.equals(immediateParent)) {
                continue;
            }

            int newCost = bState.getCurrentCost() + edge.length();

            // Instead of excluding already visited neighbour nodes, we allow exploration of neighbour cells
            // We will achieve this by tracking a local set of nodes belonging to the current path 
            // instead of a global set of vistied nodes
            // We have to initialize that set before calling the recursion, then it's passed as a parameter

            // Check if we have enough time to hit this neighbor and still make it to the exit
            int neighbourExitTime = wrapper.getMinDistanceToExit().getOrDefault(neighbour, Integer.MAX_VALUE);
            if (newCost + neighbourExitTime < wrapper.getState().getTimeRemaining()) {
                // Boolean flag to exclude parent nodes from the search to prevent loops
                boolean alreadyVisited = pathVisited.contains(neighbour);
                // Gold is removed from search if we already visited the node
                int goldOnNode = alreadyVisited ? 0 : wrapper.getGraph().getGoldMap().getOrDefault(neighbour, 0);

                // Skip moving into a zero-gold node if it takes us further from the exit, 
                // unless we have an abundance of spare time.
                if (goldOnNode == 0 && neighbourExitTime > minTimeToExit) {
                    if (timeLeft < minTimeToExit * SPARE_TIME_MULTIPLIER) {
                        continue; 
                    }
                }

                // Update visited, current path and gold available on map before recursive call
                // But only update gold if the node is not already visited
                boolean inserted = pathVisited.add(neighbour);
                currentPath.add(neighbour);
                // Update state cleanly across frames
                BranchState nextState = bState.moveTo(neighbour, edge.length(), inserted ? goldOnNode : 0);

                // Recurse
                knapsackDFS(wrapper, nextState, pathVisited, currentPath);

                //Track back state changes after return from recursive call
                if (inserted) {
                    pathVisited.remove(neighbour);
                }
                currentPath.remove(currentPath.size() - 1);    
            }
        }
    }
}
