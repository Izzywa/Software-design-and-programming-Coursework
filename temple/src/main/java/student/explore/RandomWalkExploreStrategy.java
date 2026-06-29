package student.explore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;

/**
 * Explores by randomly choosing among unseen neighbors, with backtracking as a fallback.
 *
 * <p>The strategy shuffles the visible neighbors at each step, then moves to the first
 * neighbor it has not visited yet. If every visible neighbor has already been visited,
 * it backtracks along the current path one step at a time until it finds a new branch
 * or runs out of path to follow.
 */
public class RandomWalkExploreStrategy implements ExploreStrategy {
    private final Random random = new Random(0L);
    private final Set<Long> visited = new HashSet<>();
    private final List<Long> path = new ArrayList<>();

    /**
     * Explore until the orb is reached or no unexplored path remains.
     *
     * @param state the live exploration state supplied by the game
     */
    @Override
    public void explore(ExplorationState state) {
        // Record the start so we can backtrack from it later if needed.
        visited.add(state.getCurrentLocation());
        path.add(state.getCurrentLocation());

        while (state.getDistanceToTarget() != 0) {
            // Randomize the current neighbor order before choosing an unseen move.
            List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
            Collections.shuffle(neighbours, random);

            NodeStatus next = null;
            for (NodeStatus neighbour : neighbours) {
                if (!visited.contains(neighbour.nodeID())) {
                    next = neighbour;
                    break;
                }
            }

            if (next != null) {
                // Move forward into the next unseen location and extend the path.
                state.moveTo(next.nodeID());
                visited.add(next.nodeID());
                path.add(next.nodeID());
                continue;
            }

            // No unseen neighbors remain, so step back along the current path.
            if (path.size() <= 1) {
                return;
            }

            path.remove(path.size() - 1);
            state.moveTo(path.get(path.size() - 1));
        }
    }
}
