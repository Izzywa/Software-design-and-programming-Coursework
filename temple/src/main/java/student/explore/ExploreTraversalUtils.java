package student.explore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import game.ExplorationState;

final class ExploreTraversalUtils {
    private ExploreTraversalUtils() {
    }

    /**
     * Reconstruct the path from a node back to the root using parent pointers.
     *
     * <p>The returned list starts at {@code nodeId} and ends at the root.
     *
     * @param parentMap a map from each discovered node to its parent
     * @param nodeId the node to trace back from
     * @return the path from {@code nodeId} back to the root, inclusive
     */
    static List<Long> pathToRoot(Map<Long, Long> parentMap, long nodeId) {
        List<Long> path = new ArrayList<>();
        Long current = nodeId;
        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }
        return path;
    }

    /**
     * Move to a discovered node using the shortest route within the discovered tree.
     *
     * <p>The method compares the current location and target location by following both
     * parent chains back to the root. It then:
     * <ol>
     *   <li>walks up from the current location to the lowest common ancestor, and</li>
     *   <li>walks down from that ancestor to the target.</li>
     * </ol>
     * This avoids the longer detour of always returning to the root first when the two
     * nodes share a deeper common branch.
     *
     * @param state the live exploration state
     * @param parentMap a map from each discovered node to its parent
     * @param targetId the discovered node to move to
     */
    static void moveToDiscoveredNode(
        ExplorationState state,
        Map<Long, Long> parentMap,
        long targetId
    ) {
        long currentId = state.getCurrentLocation();
        if (currentId == targetId) {
            return;
        }

        List<Long> currentPath = pathToRoot(parentMap, currentId);
        List<Long> targetPath = pathToRoot(parentMap, targetId);

        int currentIndex = currentPath.size() - 1;
        int targetIndex = targetPath.size() - 1;
        while (currentIndex >= 0
            && targetIndex >= 0
            && currentPath.get(currentIndex).equals(targetPath.get(targetIndex))) {
            currentIndex--;
            targetIndex--;
        }

        // Walk from the current node up to the lowest common ancestor.
        for (int i = 1; i <= currentIndex + 1; i++) {
            state.moveTo(currentPath.get(i));
        }

        // Walk from the common ancestor down to the target node.
        for (int i = targetIndex; i >= 0; i--) {
            state.moveTo(targetPath.get(i));
        }
    }
}
