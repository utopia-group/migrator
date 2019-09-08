package migrator.rewrite.spanning;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import migrator.rewrite.spanning.GraphUtil.Pair;

public class PruneCollectorTest {

    private static <U, V> Pair<U, V> p(U a, V b) {
        return new Pair<>(a, b);
    }

    private static Object[] getBenchmarkByName(String name) {
        for (Object[] benchmark : SpanningTreeTest.data()) {
            if (benchmark[0].equals(name)) {
                return benchmark;
            }
        }
        return null;
    }

    private List<List<Pair<Integer, Integer>>> convertTrees(Set<Set<Edge<Integer, Integer>>> treeSet) {
        return treeSet.stream()
                .map(set -> set.stream()
                        .map(edge -> p(edge.mate.originalTip.data, edge.originalTip.data))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @Test
    public void testBig() {
        Object[] bigRow = getBenchmarkByName("big");
        assertEquals("big", bigRow[0]);
        @SuppressWarnings("unchecked")
        Graph<Integer, Integer> big = (Graph<Integer, Integer>) bigRow[1];
        Set<Integer> keepVertices = new HashSet<>(Arrays.asList(1, 7, 3));
        PruneCollector<Integer, Integer> collector = new PruneCollector<>(vx -> keepVertices.contains(vx.data));
        big.generateSpanningTrees(collector);
        List<List<Pair<Integer, Integer>>> expected = Arrays.asList(
                Arrays.asList(p(1, 7), p(1, 2), p(2, 3)),
                Arrays.asList(p(1, 7), p(1, 2), p(2, 4), p(4, 5), p(5, 3)),
                Arrays.asList(p(1, 7), p(7, 6), p(6, 5), p(5, 3)),
                Arrays.asList(p(1, 7), p(7, 6), p(6, 5), p(5, 4), p(4, 2), p(2, 3)),
                Arrays.asList(p(1, 2), p(2, 3), p(3, 5), p(5, 6), p(6, 7)),
                Arrays.asList(p(1, 2), p(2, 3), p(2, 4), p(4, 5), p(5, 6), p(6, 7)),
                Arrays.asList(p(1, 2), p(2, 4), p(4, 5), p(5, 3), p(5, 6), p(6, 7)));
        assertEquals(
                SpanningTreeTest.normalizeTreeList(expected),
                SpanningTreeTest.normalizeTreeList(convertTrees(collector.getTrees())));
    }

    @Test
    public void testTape() {
        Object[] tapeRow = getBenchmarkByName("tape");
        assertEquals("tape", tapeRow[0]);
        @SuppressWarnings("unchecked")
        Graph<Integer, Integer> tape = (Graph<Integer, Integer>) tapeRow[1];
        Set<Integer> keepVertices = new HashSet<>(Arrays.asList(1, 2));
        PruneCollector<Integer, Integer> collector = new PruneCollector<>(vx -> keepVertices.contains(vx.data));
        tape.generateSpanningTrees(collector);
        List<List<Pair<Integer, Integer>>> expected = Arrays.asList(
                Arrays.asList(p(1, 2)),
                Arrays.asList(p(1, 4), p(2, 3), p(3, 4)),
                Arrays.asList(p(1, 4), p(2, 3), p(3, 6), p(4, 5), p(5, 6)),
                Arrays.asList(p(1, 4), p(2, 6), p(3, 4), p(3, 6)),
                Arrays.asList(p(1, 4), p(2, 6), p(4, 5), p(5, 6)),
                Arrays.asList(p(1, 5), p(2, 3), p(3, 4), p(4, 5)),
                Arrays.asList(p(1, 5), p(2, 3), p(3, 6), p(5, 6)),
                Arrays.asList(p(1, 5), p(2, 6), p(3, 4), p(3, 6), p(4, 5)),
                Arrays.asList(p(1, 5), p(2, 6), p(5, 6)));
        assertEquals(
                SpanningTreeTest.normalizeTreeList(expected),
                SpanningTreeTest.normalizeTreeList(convertTrees(collector.getTrees())));
    }

}
