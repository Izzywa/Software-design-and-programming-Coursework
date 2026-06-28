package student.explore;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;
import java.util.Map;

import game.ExplorationState;
import game.NodeStatus;

/**
 * Explores the cavern in breadth-first order over the discovered graph.
 *
 * <p>This strategy mirrors the escape-side BFS structure:
 * it uses a FIFO queue for the frontier, a visited set to avoid revisiting nodes,
 * and parent pointers to move between already discovered nodes without restarting
 * from the beginning of the path each time.
 *
 * <pre>
 * procedure BFS(G, start) is
 *     create a queue Q
 *     create a set V
 *     create a parent map P
 *     add start to Q
 *     add start to V
 *     while Q is not empty do
 *         current = Q.dequeue()
 *         move to current if needed
 *         if current is the target then
 *             return true
 *         for each neighbor of current do
 *             if neighbor is not in V then
 *                 add neighbor to V
 *                 record current as its parent
 *                 add neighbor to Q
 *     return false
 * </pre>
 */
public class BreadthFirstExploreStrategy implements ExploreStrategy {
    private final Queue<Long> queue = new ArrayDeque<>();
    private final Set<Long> visited = new HashSet<>();
    private final Map<Long, Set<Long>> adjacency = new HashMap<>();

    @Override
    public void explore(ExplorationState state) {
        // Note by JY: used adjacency to match ExploreTraversalUtils.moveToDiscoveredNode method
        long startId = state.getCurrentLocation();
        queue.add(startId);
        visited.add(startId);
        adjacency.computeIfAbsent(startId, ignored -> new HashSet<>());

        while (!queue.isEmpty()) {
            long currentId = queue.remove();
            if (state.getCurrentLocation() != currentId) {
                ExploreTraversalUtils.moveToDiscoveredNode(state, adjacency, currentId);
            }

            // If we have reached the end node, stop the search
            if (state.getDistanceToTarget() == 0) {
                return;
            }

            // Traverse neighbors of the current node, adding unvisited ones to the queue
            for (NodeStatus neighbour : state.getNeighbours()) {
                long neighbourId = neighbour.nodeID();
                ExploreTraversalUtils.connectNodes(adjacency, currentId, neighbourId);
                if (!visited.contains(neighbourId)) {
                    visited.add(neighbourId);
                    queue.add(neighbourId);
                }
                if (neighbour.distanceToTarget() == 0) {
                    state.moveTo(neighbourId);
                    return;
                }
            }
        }
    }
}
