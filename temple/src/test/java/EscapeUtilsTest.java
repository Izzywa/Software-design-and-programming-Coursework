import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

import game.MockGameState;
import student.escape.EscapeDijkstra;
import student.escape.EscapeGraph;
import student.escape.EscapePath;
import student.escape.EscapeStrategy;
import game.Node;
import game.Edge;

/**
 * Unit tests for the EscapeGraph and EscapePath utility classes
 */
public class EscapeUtilsTest {

    /**
     * Verifies the return value of the total amount of gold available on the map.
     */
    @Test
    public void testEscapeGraphTotalGold() { 
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/gold_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        EscapeGraph graph = new EscapeGraph(state);
        assertEquals(10, graph.getTotalGold());
    }

    /**
     * Verifies the correctness of the gold map representation of the graph.
     */
    @Test
    public void testEscapeGraphGetGoldMap() {    
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(86L, 0);
        expected.put(81L, 0);
        expected.put(82L, 0);
        expected.put(83L, 5);
        expected.put(84L, 5);
        expected.put(85L, 0);
        expected.put(87L, 0);
        expected.put(88L, 0);
        expected.put(89L, 0);
        expected.put(90L, 0);
        expected.put(91L, 0);

        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/gold_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        EscapeGraph graph = new EscapeGraph(state);
        Map<Node, Integer> goldMap = graph.getGoldMap();
        Map<Long, Integer> actual = new HashMap<>();
        for (Node node : goldMap.keySet()) {
            actual.put(node.getId(), goldMap.get(node));
        }

        assertTrue(expected.equals(actual)); 
    }

    /**
     * Verifies the correctness of the unweighted representation of the graph.
     */
    @Test
    public void testEscapeGraphUnweighted() {
        Map<Long, Collection<Long>> expected = new HashMap<>();
        expected.put(86L, new ArrayList<Long>(Arrays.asList(88L)));
        expected.put(81L, new ArrayList<Long>(Arrays.asList(87L, 88L)));
        expected.put(82L, new ArrayList<Long>(Arrays.asList(87L, 86L, 85L)));
        expected.put(83L, new ArrayList<Long>(Arrays.asList(89L, 90L)));
        expected.put(84L, new ArrayList<Long>(Arrays.asList(90L, 91L)));
        expected.put(85L, new ArrayList<Long>(Arrays.asList(82L, 91L)));
        expected.put(87L, new ArrayList<Long>(Arrays.asList(81L, 82L)));
        expected.put(88L, new ArrayList<Long>(Arrays.asList(86L, 81L, 82L, 89L)));
        expected.put(89L, new ArrayList<Long>(Arrays.asList(86L, 83L)));
        expected.put(90L, new ArrayList<Long>(Arrays.asList(83L, 84L)));
        expected.put(91L, new ArrayList<Long>(Arrays.asList(84L, 85L)));

        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/gold_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        EscapeGraph graph = new EscapeGraph(state);
        Map<Node, Collection<Node>> unweighted = graph.getUnweighted();
        Map<Long, Collection<Long>> actual = new HashMap<>();
        for (Node node : unweighted.keySet()) {
            Collection<Long> neighbours = new ArrayList<>();
            for (Node neighbour : unweighted.get(node)) {
                neighbours.add(neighbour.getId());
            }
            actual.put(node.getId(), neighbours);       
        }

        assertTrue(expected.keySet().equals(actual.keySet()));
        for (Long id : expected.keySet()) {
            assertTrue(expected.get(id).size() == actual.get(id).size());
            assertTrue(expected.get(id).containsAll(actual.get(id)));
            assertTrue(actual.get(id).containsAll(expected.get(id)));
        }
    }

    /**
     * Verifies the correctness of the inverted weighted representation of the graph.
     * This is achieved by inverting the already inverted weighted graph,
     * then checking if this equals the original weighted graph before inverting it.
     */
    @Test
    public void testEscapeGraphInverted() {
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/gold_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        EscapeGraph graph = new EscapeGraph(state);
        Map<Node, Collection<Edge>> weighted = graph.getWeighted();
        Map<Node, Collection<Edge>> inverted = graph.getInvertedWeighted();
        Map<Node, Collection<Edge>> doubleInverted = EscapeGraph.createInvertedGraph(inverted);

        Map<Long, Collection<String>> expected = new HashMap<>();
        for (Node node : weighted.keySet()) {
            Collection<String> edges = new ArrayList<>();
            for (Edge edge : weighted.get(node)) {
                edges.add(new String(edge.getSource().getId() + "," + edge.getDest().getId() + "," + edge.length()));
            }
            expected.put(node.getId(), edges);
        }

        Map<Long, Collection<String>> actual = new HashMap<>();
        for (Node node : doubleInverted.keySet()) {
            Collection<String> edges = new ArrayList<>();
            for (Edge edge : doubleInverted.get(node)) {
                edges.add(new String(edge.getSource().getId() + "," + edge.getDest().getId() + "," + edge.length()));
            }
            actual.put(node.getId(), edges);
        }

        assertTrue(expected.keySet().equals(actual.keySet()));
        for (Long id : expected.keySet()) {
            assertTrue(expected.get(id).size() == actual.get(id).size());
            assertTrue(expected.get(id).containsAll(actual.get(id)));
            assertTrue(actual.get(id).containsAll(expected.get(id)));
        }
    }

    /**
     * Verifies the correctness of the weighted representation of the graph.
     */
    @Test
    public void testEscapeGraphWeighted() {
        Map<Long, Collection<String>> expected = new HashMap<>();
        expected.put(86L, new ArrayList<String>(Arrays.asList("86,88,0")));
        expected.put(81L, new ArrayList<String>(Arrays.asList("81,87,1", "81,88,1")));
        expected.put(82L, new ArrayList<String>(Arrays.asList("82,87,1", "82,86,7", "82,85,3")));
        expected.put(83L, new ArrayList<String>(Arrays.asList("83,89,2", "83,90,2")));
        expected.put(84L, new ArrayList<String>(Arrays.asList("84,90,3", "84,91,3")));
        expected.put(85L, new ArrayList<String>(Arrays.asList("85,82,3", "85,91,3")));
        expected.put(87L, new ArrayList<String>(Arrays.asList("87,81,1", "87,82,1")));
        expected.put(88L, new ArrayList<String>(Arrays.asList("88,86,0", "88,81,1", "88,82,7", "88,89,2")));
        expected.put(89L, new ArrayList<String>(Arrays.asList("89,86,2", "89,83,2")));
        expected.put(90L, new ArrayList<String>(Arrays.asList("90,83,2", "90,84,3")));
        expected.put(91L, new ArrayList<String>(Arrays.asList("91,84,3", "91,85,3")));

        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/gold_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.escape();
        EscapeGraph graph = new EscapeGraph(state);
        Map<Node, Collection<Edge>> weighted = graph.getWeighted();
        Map<Long, Collection<String>> actual = new HashMap<>();
        for (Node node : weighted.keySet()) {
            Collection<String> edges = new ArrayList<>();
            for (Edge edge : weighted.get(node)) {
                edges.add(new String(edge.getSource().getId() + "," + edge.getDest().getId() + "," + edge.length()));
            }
            actual.put(node.getId(), edges);
        }

        assertTrue(expected.keySet().equals(actual.keySet()));
        for (Long id : expected.keySet()) {
            assertTrue(expected.get(id).size() == actual.get(id).size());
            assertTrue(expected.get(id).containsAll(actual.get(id)));
            assertTrue(actual.get(id).containsAll(expected.get(id)));
        }
    }

    /**
     * Verifies the return value of the total amount of gold found along the path.
     */
    @Test
    public void testEscapePathTotalGold() { 
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/one_path_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.setEscapeStage();
        EscapeStrategy strategy = new EscapeDijkstra();
        EscapePath path = strategy.findEscapePath(state);
        assertEquals(15, path.getTotalGold());
    }

    /**
     * Verifies the return value of the total cost of traversing the path.
     */
    @Test
    public void testEscapePathTotalCost() { 
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/one_path_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.setEscapeStage();
        EscapeStrategy strategy = new EscapeDijkstra();
        EscapePath path = strategy.findEscapePath(state);
        assertEquals(12, path.getTotalCost());
    }

    /**
     * Verifies the return value of the first and last nodes of the path.
     */
    @Test
    public void testEscapePathFirstAndLastNode() { 
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/one_path_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.setEscapeStage();
        EscapeStrategy strategy = new EscapeDijkstra();
        EscapePath path = strategy.findEscapePath(state);
        assertEquals(1L, path.getFirstNode().getId());
        assertEquals(8L, path.getLastNode().getId());
    }

    /**
     * Verifies the return value of the list representing the path.
     */
    @Test
    public void testEscapePathGetPath() {
        ArrayList<Long> expected = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/one_path_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.setEscapeStage();
        EscapeStrategy strategy = new EscapeDijkstra();
        EscapePath path = strategy.findEscapePath(state);
        ArrayList<Long> actual = new ArrayList<>();
        for (Node node : path.getPath()) {
            actual.add(node.getId());
        }

        assertTrue(expected.size() == actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    /**
     * Verifies the correct traversal and gold collection along the path.
     */
    @Test
    public void testEscapePathTraverseAndCollect() { 
        Path exploreCavernPath = Path.of(
            "src/test/resources/dummy_explore.txt"
        );
        Path escapeCavernPath = Path.of(
            "src/test/resources/one_path_escape.txt"
        );
        MockGameState state = new MockGameState(
            exploreCavernPath,
            escapeCavernPath,
            false
        );
        state.setExploreSucceeded(true);
        state.setEscapeStage();
        EscapeStrategy strategy = new EscapeDijkstra();
        EscapePath path = strategy.findEscapePath(state);
        path.traverseAndCollect();

        assertEquals(false, state.getEscapeErrored());
        assertEquals(8L, state.getCurrentNode().getId());
        assertEquals(15, state.getGoldCollected());
    }

}
