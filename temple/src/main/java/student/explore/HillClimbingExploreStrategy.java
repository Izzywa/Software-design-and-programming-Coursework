package student.explore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;

/**
 * Explores by repeatedly choosing the best currently available neighbor and
 * backtracking when the search reaches a dead end.
 *
 * <p>The strategy keeps two pieces of state:
 * <ul>
 *   <li>{@code visited}, which prevents revisiting locations we have already explored</li>
 *   <li>{@code path}, which records the current walk so we can step back one move at a time</li>
 * </ul>
 * The next move is chosen from the current neighbors in sorted order, which makes the
 * exploration deterministic.
 */
public class HillClimbingExploreStrategy implements ExploreStrategy {
    private final Set<Long> visited = new HashSet<>();
    private final List<Long> path = new ArrayList<>();

    /**
     * Explore until the orb is reached or no unexplored path remains.
     *
     * @param state the live exploration state supplied by the game
     */
    @Override
    public void explore(ExplorationState state) {
        // Seed the traversal with the starting location.
        path.add(state.getCurrentLocation());
        visited.add(state.getCurrentLocation());

        while (state.getDistanceToTarget() != 0) {
            // Prefer the first unvisited neighbor in sorted order.
            List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
            Collections.sort(neighbours);

            NodeStatus next = null;
            for (NodeStatus neighbour : neighbours) {
                if (!visited.contains(neighbour.nodeID())) {
                    next = neighbour;
                    break;
                }
            }

            if (next != null) {
                // Move forward and extend the current path.
                state.moveTo(next.nodeID());
                visited.add(next.nodeID());
                path.add(next.nodeID());
                continue;
            }

            // No unseen neighbors remain, so backtrack one step.
            if (path.size() <= 1) {
                return;
            }

            path.remove(path.size() - 1);
            state.moveTo(path.get(path.size() - 1));
        }
    }
}
