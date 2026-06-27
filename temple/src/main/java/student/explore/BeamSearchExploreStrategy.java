package student.explore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;

/**
 * Explores the cavern using a beam search over the discovered graph.
 *
 * <p>The strategy keeps a bounded frontier of the most promising discovered nodes.
 * Nodes are ranked by their reported distance to the orb, and only the best
 * {@value #BEAM_WIDTH} candidates are kept after each expansion. This makes the
 * search more selective than breadth-first search while remaining deterministic.
 */
public class BeamSearchExploreStrategy implements ExploreStrategy {
    /**
     * A discovered node paired with its heuristic score.
     *
     * @param nodeId the node identifier
     * @param score the heuristic score used to rank the beam frontier
     */
    private record FrontierNode(long nodeId, int score) {
    }

    /** Maximum number of frontier nodes kept at any time. */
    private static final int BEAM_WIDTH = 32;

    /** Nodes that have already been seen and recorded. */
    private final Set<Long> discovered = new HashSet<>();

    /** Undirected adjacency between discovered nodes. */
    private final Map<Long, Set<Long>> adjacency = new HashMap<>();

    /** Frontier nodes ordered by lowest heuristic score first. */
    private final PriorityQueue<FrontierNode> frontier = new PriorityQueue<>(
        Comparator
            .comparingInt(FrontierNode::score)
            .thenComparingLong(FrontierNode::nodeId)
    );

    /**
     * Explore the cavern until the orb is reached.
     *
     * <p>The current location is expanded, newly discovered neighbors are added to the
     * bounded frontier, and the explorer repeatedly moves to the most promising discovered
     * tile that remains inside the beam.
     *
     * @param state the live exploration state supplied by the game
     */
    @Override
    public void explore(ExplorationState state) {
        long startId = state.getCurrentLocation();
        discovered.add(startId);
        adjacency.computeIfAbsent(startId, ignored -> new HashSet<>());

        if (discoverNeighbours(state)) {
            return;
        }

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
     * Discover all neighbors visible from the current location and add new ones to the beam.
     *
     * <p>Each unseen neighbor is recorded in the discovered graph, scored by its reported
     * distance to the orb, and inserted into the frontier. If the orb is adjacent, the
     * strategy moves onto it immediately.
     *
     * @param state the live exploration state supplied by the game
     * @return {@code true} if a neighboring tile is the orb and the explorer moved onto it
     */
    private boolean discoverNeighbours(ExplorationState state) {
        long currentId = state.getCurrentLocation();
        adjacency.computeIfAbsent(currentId, ignored -> new HashSet<>());
        Collection<NodeStatus> neighbours = state.getNeighbours();
        for (NodeStatus neighbour : neighbours) {
            long neighbourId = neighbour.nodeID();
            ExploreTraversalUtils.connectNodes(adjacency, currentId, neighbourId);
            if (discovered.add(neighbourId)) {
                frontier.add(new FrontierNode(neighbourId, neighbour.distanceToTarget()));
                trimFrontier();
            }
            if (neighbour.distanceToTarget() == 0) {
                state.moveTo(neighbourId);
                return true;
            }
        }
        return false;
    }

    /**
     * Keep only the best-scoring frontier nodes.
     */
    private void trimFrontier() {
        if (frontier.size() <= BEAM_WIDTH) {
            return;
        }

        List<FrontierNode> kept = new ArrayList<>(BEAM_WIDTH);
        for (int i = 0; i < BEAM_WIDTH && !frontier.isEmpty(); i++) {
            kept.add(frontier.remove());
        }

        frontier.clear();
        frontier.addAll(kept);
    }
}
