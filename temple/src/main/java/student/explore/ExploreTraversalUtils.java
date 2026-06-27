package student.explore;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import game.ExplorationState;

final class ExploreTraversalUtils {
    private ExploreTraversalUtils() {
    }

    /**
     * Store an undirected connection between two discovered nodes.
     *
     * <p>This keeps the discovered graph symmetric so movement can reuse the edge in
     * either direction.
     *
     * @param adjacency the discovered adjacency list
     * @param leftId one endpoint of the edge
     * @param rightId the other endpoint of the edge
     */
    static void connectNodes(
        Map<Long, Set<Long>> adjacency,
        long leftId,
        long rightId
    ) {
        adjacency.computeIfAbsent(leftId, ignored -> new HashSet<>()).add(rightId);
        adjacency.computeIfAbsent(rightId, ignored -> new HashSet<>()).add(leftId);
    }

    /**
     * Find the shortest path between two discovered nodes with BFS.
     *
     * <p>This follows the same queue, visited-set, and parent-map pattern used by the
     * escape-side BFS implementation. The returned list is ordered from source to target.
     *
     * @param adjacency the discovered adjacency list
     * @param sourceId the starting node
     * @param targetId the destination node
     * @return the shortest path from {@code sourceId} to {@code targetId}
     */
    public static List<Long> breadthFirstSearch(
        Map<Long, Set<Long>> adjacency,
        long sourceId,
        long targetId
    ) {
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Long> parentMap = new HashMap<>();
        Set<Long> visited = new HashSet<>();

        // Start BFS from the start node
        queue.add(sourceId);
        parentMap.put(sourceId, null);
        visited.add(sourceId);

        // Perform BFS until the queue is empty or we find the target node
        while (!queue.isEmpty()) {
            long current = queue.poll();

            // If we have reached the end node, stop the search
            if (current == targetId) {
                break;
            }

            // Traverse neighbors of the current node, adding unvisited ones to the queue
            for (long neighbourId : adjacency.getOrDefault(current, Collections.emptySet())) {
                if (!visited.contains(neighbourId)) {
                    visited.add(neighbourId);
                    parentMap.put(neighbourId, current);
                    queue.add(neighbourId);
                }
            }
        }

        LinkedList<Long> path = new LinkedList<>();
        for (Long current = targetId; current != null; current = parentMap.get(current)) {
            path.addFirst(current);
        }
        return path;
    }

    /**
     * Move to a discovered node using the shortest route through discovered space.
     *
     * <p>The method uses BFS over the discovered adjacency graph and then walks the
     * resulting path one step at a time.
     *
     * @param state the live exploration state
     * @param adjacency the discovered adjacency list
     * @param targetId the discovered node to move to
     */
    static void moveToDiscoveredNode(
        ExplorationState state,
        Map<Long, Set<Long>> adjacency,
        long targetId
    ) {
        List<Long> path = breadthFirstSearch(adjacency, state.getCurrentLocation(), targetId);
        for (int i = 1; i < path.size(); i++) {
            state.moveTo(path.get(i));
        }
    }
}
