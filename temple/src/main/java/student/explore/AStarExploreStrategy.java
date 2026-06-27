package student.explore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;

/**
 * Explores the cavern using an A*-style frontier ordering on the discovered graph.
 *
 * <p>The strategy keeps track of tiles it has already seen, remembers how each tile
 * was reached, and stores discovered tiles in a priority queue ordered by
 * {@code g(n) + h(n)}:
 * <ul>
 *   <li>{@code g(n)} is the number of steps taken from the start to the tile</li>
 *   <li>{@code h(n)} is the tile's reported distance to the orb</li>
 * </ul>
 * This lets the explorer move toward the most promising discovered tile next.
 */
public class AStarExploreStrategy implements ExploreStrategy {
    /**
     * A discovered node paired with its A*-style priority.
     *
     * @param nodeId the node identifier
     * @param priority the estimated total cost for reaching the orb through this node,
     * basically f(n) = g(n) + h(n)
     */
    private record FrontierNode(long nodeId, int priority) {
    }

    /** Nodes that have already been seen and recorded. */
    private final Set<Long> discovered = new HashSet<>();

    /** Parent pointers used to reconstruct paths through discovered nodes. */
    private final HashMap<Long, Long> parentMap = new HashMap<>();

    /** Number of steps from the start to each discovered node, g(n). */
    private final HashMap<Long, Integer> depthMap = new HashMap<>();

    /** Frontier nodes ordered by lowest estimated total cost first.
     *  Cost function: f(n) = g(n) + h(n)
     */
    private final PriorityQueue<FrontierNode> frontier = new PriorityQueue<>(
        (left, right) -> Integer.compare(left.priority(), right.priority())
    );

    /** The starting location for the current exploration run. */
    private long rootId;

    /**
     * Explore the cavern until the orb is reached.
     *
     * <p>The current location is expanded, newly discovered neighbors are added to the
     * frontier, and the explorer repeatedly moves to the most promising discovered tile
     * until the orb is found.
     *
     * @param state the live exploration state supplied by the game
     */
    @Override
    public void explore(ExplorationState state) {
        /*
         * Starting node
         * 1. Add starting node to set of nodes seen and recorded
         * 2. Set parent to null (since it is the 1st node)
         * 3. Set depth, g(n) = 0 (no depth)
         * 4. Search for its neighboring nodes and add them to the queue of Frontier nodes
         */
        rootId = state.getCurrentLocation();
        discovered.add(rootId);
        parentMap.put(rootId, null);
        depthMap.put(rootId, 0);
        if (discoverNeighbours(state)) {
            return;
        }

        /*
         * While there are still frontiers nodes in the queue, iterate over each frontier node:
         * 1. Get the frontier node with the lowest cost and remove it from queue
         * 2. Expand the lowest cost frontier node: if the lowest cost frontier node is
         * previously discovered, and we're not standing on it, move to it
         * 3. If we end up standing on the orb, we end the search
         * 4. If a neighboring tile on the frontier is the orb and we've moved to it in
         * the discoverNeighbours function, we end the search
         */
        while (!frontier.isEmpty()) {
            FrontierNode next = frontier.remove();
            if (discovered.contains(next.nodeId()) && next.nodeId() != state.getCurrentLocation()) {
                ExploreTraversalUtils.moveToDiscoveredNode(state, parentMap, next.nodeId());
            }
            if (state.getDistanceToTarget() == 0) {
                return;
            }
            if (discoverNeighbours(state)) {
                return;
            }
        }
    }

    /**
     * Discover all neighbors visible from the current location and add new ones to the frontier.
     *
     * <p>Each unseen neighbor is recorded with a parent pointer, a depth from the start,
     * and an A*-style priority of {@code depth + heuristicDistanceToOrb}.
     *
     * @param state the live exploration state supplied by the game
     * @return {@code true} if a neighboring tile is the orb and the explorer moved onto it
     */
    private boolean discoverNeighbours(ExplorationState state) {
        /*
         * 1. Get the depth g(n) of the current node
         * 2. For each neighboring node, if the neighboring node is recently added to the set of
         * nodes seen:
         *   a. Add the current node as the parent node of the neighboring node
         *   b. Record the depth of the neighboring node as g(n') = g(n) + 1
         *   c. Add the neighboring node to the frontier queue with
         *   f(n') = g(n') + h(n') = g(n) + 1 + h(n')
         * 3. If the neighbor has the orb, move to it and return True
         * 4. Otherwise, return False
         */
        int currentDepth = depthMap.getOrDefault(state.getCurrentLocation(), 0);
        Collection<NodeStatus> neighbours = state.getNeighbours();
        for (NodeStatus neighbour : neighbours) {
            long neighbourId = neighbour.nodeID();
            if (discovered.add(neighbourId)) {
                parentMap.put(neighbourId, state.getCurrentLocation());
                depthMap.put(neighbourId, currentDepth + 1);
                frontier.add(new FrontierNode(
                    neighbourId,
                    currentDepth + 1 + neighbour.distanceToTarget()
                ));
            }
            if (neighbour.distanceToTarget() == 0) {
                state.moveTo(neighbourId);
                return true;
            }
        }
        return false;
    }
}
