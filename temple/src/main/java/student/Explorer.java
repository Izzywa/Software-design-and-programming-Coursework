package student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import student.escape.EscapeKnapsackDFSBnB;
import student.escape.EscapePath;
import student.escape.EscapeStrategy;

public class Explorer {

    private Set<Long> discovered;

    public Explorer() {
        discovered = new HashSet<>();
    }

    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        depthFirstSearch(state);
    }

    /**
     * Depth-first search algorithm
     * Reference: <a href="https://en.wikipedia.org/wiki/Depth-first_search">Wikipedia DFS</a>
     *
     * <pre>
     * procedure DFS(G, v) is
     *     label v as discovered
     *     for all directed edges from v to w that are in G.adjacentEdges(v) do
     *         if vertex w is not labeled as discovered then
     *             recursively call DFS(G, w)
     * </pre>
     *
     * @param state the exploration state
     * @return true if the orb is found, false otherwise
     */
    private boolean depthFirstSearch(ExplorationState state) {
        long current = state.getCurrentLocation();
        discovered.add(current);

        if (state.getDistanceToTarget() == 0) {
            return true;
        }

        List<NodeStatus> neighbours = new ArrayList<>(state.getNeighbours());
        Collections.sort(neighbours);
        for (var neighbour : neighbours) {
            long neighbourId = neighbour.nodeID();
            if (!discovered.contains(neighbourId)) {
                state.moveTo(neighbourId);
                if (depthFirstSearch(state)) {
                    return true;
                }
                state.moveTo(current);
            }
        }

        return false;
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out

        // Create an escape strategy with the corresponding algorithm and get the escape path
        EscapeStrategy strategy = new EscapeKnapsackDFSBnB(state, state.getCurrentNode(), state.getExit());
        EscapePath path = strategy.findEscapePath();
         
        // Pick up gold on the starting node if it exists
        if(path.getPath().get(0).getTile().getGold() > 0) {
                state.pickUpGold();
        }

        // Follow the path to the exit, picking up gold along the way
        for (int i = 1; i < path.getPath().size(); i++) {
            state.moveTo(path.getPath().get(i));
            if(state.getCurrentNode().getTile().getGold() > 0) {
                state.pickUpGold();
            }

            if (state.getTimeRemaining() <= 0) {
                throw new RuntimeException("Time ran out before escaping!");
            }
        }
    }
}
