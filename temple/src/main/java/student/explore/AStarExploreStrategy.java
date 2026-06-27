package student.explore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map;

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

    /** Undirected adjacency between discovered nodes. */
    private final Map<Long, Set<Long>> adjacency = new HashMap<>();

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
         * 1. Add starting node to set of nodes seen
         * 2. Create an adjacency entry for the start
         * 3. Set depth, g(n) = 0 (no depth)
         * 4. Search for its neighboring nodes and add them to the queue of Frontier nodes
         */
        rootId = state.getCurrentLocation();
        discovered.add(rootId);
        adjacency.computeIfAbsent(rootId, ignored -> new HashSet<>());
        depthMap.put(rootId, 0);
        if (discoverNeighbours(state)) {
            return;
        }

        /*
         * While there are still frontiers nodes in the queue, iterate over each frontier node:
         * 1. Get the frontier node with the lowest cost and remove it from queue
         * 2. Expand the lowest cost frontier node: if  we're not standing on the lowest cost
         * frontier node, move to it
         * 3. If we end up standing on the orb, we end the search
         * 4. If a neighboring tile on the frontier is the orb and we've moved to it in
         * the discoverNeighbours function, we end the search
         */
        while (!frontier.isEmpty()) {
            FrontierNode next = frontier.remove();
            if (next.nodeId() != state.getCurrentLocation()) {
                ExploreTraversalUtils.moveToDiscoveredNode(state, adjacency, next.nodeId());
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
         * 2. For each neighboring node:
         *   a. Store the edge in both directions in the adjacency map
         *   b. If the neighbor is new, record its depth as g(n') = g(n) + 1
         *   c. Add the neighboring node to the frontier queue with
         *   f(n') = g(n') + h(n') = g(n) + 1 + h(n')
         * 3. If the neighbor has the orb, move to it and return True
         * 4. Otherwise, return False
         */
        long currentId = state.getCurrentLocation();
        int currentDepth = depthMap.getOrDefault(currentId, 0);
        adjacency.computeIfAbsent(currentId, ignored -> new HashSet<>());
        Collection<NodeStatus> neighbours = state.getNeighbours();
        for (NodeStatus neighbour : neighbours) {
            long neighbourId = neighbour.nodeID();
            ExploreTraversalUtils.connectNodes(adjacency, currentId, neighbourId);
            if (discovered.add(neighbourId)) {
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
