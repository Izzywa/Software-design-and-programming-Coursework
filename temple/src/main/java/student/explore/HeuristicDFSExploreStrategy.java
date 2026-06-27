package student.explore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;
import lombok.Getter;
import lombok.Setter;
import student.sort.AscendingSort;
import student.sort.SortingStrategy;

/**
 * A depth-first exploration strategy that prefers neighbors closer to the orb.
 *
 * Reference:
 * <a href="https://en.wikipedia.org/wiki/Depth-first_search">
 *   Wikipedia DFS
 * </a>
 *
 * <pre>
 * procedure DFS(G, v) is
 *     label v as discovered
 *     for all directed edges from v to w that are in G.adjacentEdges(v) do
 *         if vertex w is not labeled as discovered then
 *             recursively call DFS(G, w)
 * </pre>
 */
public class HeuristicDFSExploreStrategy implements ExploreStrategy {

    private final Set<Long> discovered;

    @Getter
    @Setter
    private SortingStrategy sortingStrategy = new AscendingSort();

    /**
     * Create a fresh heuristic DFS strategy.
     */
    public HeuristicDFSExploreStrategy() {
        discovered = new HashSet<>();
    }

    /**
     * Explore using depth-first search with distance-based neighbor ordering.
     *
     * @param state the current exploration state
     */
    @Override
    public void explore(ExplorationState state) {
        DFS(state);
    }

    /**
     * Recursively search for the orb from the current location.
     *
     * @param state the current exploration state
     * @return true once the orb is reached
     */
    private boolean DFS(ExplorationState state) {
        long current = state.getCurrentLocation();
        discovered.add(current);

        if (state.getDistanceToTarget() == 0) {
            return true;
        }

        List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
        neighbours = sortingStrategy.sort(neighbours);

        for (var neighbour : neighbours) {
            long neighbourId = neighbour.nodeID();
            if (!discovered.contains(neighbourId)) {
                state.moveTo(neighbourId);
                if (DFS(state)) {
                    return true;
                }
                state.moveTo(current);
            }
        }

        return false;
    }
}
