package migrator.rewrite.spanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import migrator.rewrite.spanning.GraphUtil.Pair;

@RunWith(Parameterized.class)
public class SpanningTreeTest {
    private static<T> Comparator<List<T>> lexicographicComparator(Comparator<T> child) {
        return (first, second) -> {
            Iterator<T> it1, it2;
            it1 = first.iterator();
            it2 = second.iterator();
            while (it1.hasNext()) {
                if (!it2.hasNext()) {
                    return 1;
                }
                T el1 = it1.next();
                T el2 = it2.next();
                int cmp = child.compare(el1, el2);
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (it2.hasNext()) {
                return -1;
            }
            return 0;
        };
    }

    public static List<List<Pair<Integer, Integer>>> normalizeTreeList(List<List<Pair<Integer, Integer>>> trees) {
        return trees.stream()
                .map(tree -> tree.stream()
                        .map(Pair::sorted)
                        .sorted(Pair.lexicographicComparator()).collect(Collectors.toList()))
                .sorted(lexicographicComparator(Pair.lexicographicComparator()))
                .collect(Collectors.toList());
    }

    @Parameters(name = "spanning trees of {0}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> ret = new ArrayList<>();
        // box
        {
            Graph<Integer, Integer> graph = new Graph<>();
            Vertex<Integer, Integer> a, b, c, d;
            a = graph.addVertex(1);
            b = graph.addVertex(2);
            c = graph.addVertex(3);
            d = graph.addVertex(4);
            graph.addEdge(a, b, 1);
            graph.addEdge(b, c, 2);
            graph.addEdge(c, d, 3);
            graph.addEdge(d, a, 4);
            graph.addEdge(a, c, 5);
            ret.add(new Object[] {"box", graph});
        }
        // big
        {
            Graph<Integer, Integer> graph = new Graph<>();
            @SuppressWarnings("unchecked")
            Vertex<Integer, Integer>[] v = new Vertex[15];
            for (int i = 1; i < v.length; i++) {
                v[i] = graph.addVertex(i);
            }
            int edgeCounter = 1;
            graph.addEdge(v[ 9], v[10], edgeCounter++);
            graph.addEdge(v[10], v[ 1], edgeCounter++);
            graph.addEdge(v[14], v[12], edgeCounter++);
            graph.addEdge(v[14], v[13], edgeCounter++);
            graph.addEdge(v[ 1], v[ 7], edgeCounter++);
            graph.addEdge(v[ 7], v[12], edgeCounter++);
            graph.addEdge(v[12], v[13], edgeCounter++);
            graph.addEdge(v[ 1], v[ 2], edgeCounter++);
            graph.addEdge(v[ 7], v[ 6], edgeCounter++);
            graph.addEdge(v[ 2], v[ 4], edgeCounter++);
            graph.addEdge(v[ 4], v[ 5], edgeCounter++);
            graph.addEdge(v[ 5], v[ 6], edgeCounter++);
            graph.addEdge(v[ 2], v[ 3], edgeCounter++);
            graph.addEdge(v[ 5], v[ 3], edgeCounter++);
            graph.addEdge(v[ 6], v[ 8], edgeCounter++);
            graph.addEdge(v[11], v[ 3], edgeCounter++);
            ret.add(new Object[] {"big", graph});
        }
        // tape
        {
            Graph<Integer, Integer> graph = new Graph<>();
            Vertex<Integer, Integer> a, b, c, d, e, f;
            a = graph.addVertex(1);
            b = graph.addVertex(2);
            c = graph.addVertex(3);
            d = graph.addVertex(4);
            e = graph.addVertex(5);
            f = graph.addVertex(6);
            graph.addEdge(a, b, 1);
            graph.addEdge(b, c, 2);
            graph.addEdge(c, d, 3);
            graph.addEdge(d, a, 4);
            graph.addEdge(a, e, 5);
            graph.addEdge(b, f, 6);
            graph.addEdge(c, f, 7);
            graph.addEdge(d, e, 8);
            graph.addEdge(e, f, 9);
            ret.add(new Object[] {"tape", graph});
        }
        // K_5
        {
            Graph<Integer, Integer> graph = new Graph<>();
            @SuppressWarnings("unchecked")
            Vertex<Integer, Integer>[] v = new Vertex[5];
            for (int i = 0; i < v.length; i++) {
                v[i] = graph.addVertex(i + 1);
            }
            int edgeCounter = 1;
            for (int i = 0; i < v.length; i++) {
                for (int j = i + 1; j < v.length; j++) {
                    graph.addEdge(v[i], v[j], edgeCounter++);
                }
            }
            ret.add(new Object[] {"K_5", graph});
        }
        // K_7
        {
            Graph<Integer, Integer> graph = new Graph<>();
            @SuppressWarnings("unchecked")
            Vertex<Integer, Integer>[] v = new Vertex[7];
            for (int i = 0; i < v.length; i++) {
                v[i] = graph.addVertex(i + 1);
            }
            int edgeCounter = 1;
            for (int i = 0; i < v.length; i++) {
                for (int j = i + 1; j < v.length; j++) {
                    graph.addEdge(v[i], v[j], edgeCounter++);
                }
            }
            ret.add(new Object[] {"K_7", graph});
        }
        // K_{3,3}
        {
            Graph<Integer, Integer> graph = new Graph<>();
            @SuppressWarnings("unchecked")
            Vertex<Integer, Integer>[] u = new Vertex[3];
            @SuppressWarnings("unchecked")
            Vertex<Integer, Integer>[] v = new Vertex[3];
            for (int i = 0; i < v.length; i++) {
                u[i] = graph.addVertex(i + 1);
                v[i] = graph.addVertex(i + 4);
            }
            int edgeCounter = 1;
            for (int i = 0; i < u.length; i++) {
                for (int j = 0; j < v.length; j++) {
                    graph.addEdge(u[i], v[j], edgeCounter++);
                }
            }
            ret.add(new Object[] {"K_{3,3}", graph});
        }
        return ret;
    }

    @Parameter(0)
    public String testName;

    @Parameter(1)
    public Graph<Integer, Integer> graph;

    private List<List<Pair<Integer, Integer>>> trees;

    @Before
    public void setup() {
        trees = new ArrayList<>();
        graph.generateSpanningTrees(tree -> {
            trees.add(tree.stream().map(edge -> new Pair<>(edge.mate.originalTip.data, edge.originalTip.data)).collect(Collectors.toList()));
        });
        trees = normalizeTreeList(trees);
    }

    @Test
    public void testNumSpanningTrees() {
        assertEquals(graph.calculateNumSpanningTrees(), trees.size());
    }

    @Test
    public void testTreesUnique() {
        List<Pair<Integer, Integer>> prev = null;
        for (List<Pair<Integer, Integer>> tree : trees) {
            if (prev != null) {
                assertNotEquals(prev, tree);
            }
            prev = tree;
        }
    }

    @Test
    public void testTreesSpanning() {
        Set<Integer> vertices = new HashSet<>();
        for (Vertex<Integer, Integer> vertex : graph.vertices) {
            vertices.add(vertex.data);
        }
        for (List<Pair<Integer, Integer>> tree : trees) {
            Set<Integer> seen = tree.stream().flatMap(pair -> Stream.of(pair.first, pair.second)).collect(Collectors.toSet());
            assertEquals(vertices, seen);
        }
    }

    private void dfsAssertAcyclic(Map<Integer, List<Integer>> edges, Set<Integer> seen, int start, Integer parent) {
        assertFalse(seen.contains(start));
        seen.add(start);
        for (Integer neighbor : edges.get(start)) {
            if (neighbor.equals(parent)) continue;
            dfsAssertAcyclic(edges, seen, neighbor, start);
        }
    }

    @Test
    public void testTreesAcyclic() {
        for (List<Pair<Integer, Integer>> tree : trees) {
            Set<Integer> seen = new HashSet<>();
            Map<Integer, List<Integer>> edges = new HashMap<>();
            for (Vertex<Integer, Integer> vertex : graph.vertices) {
                edges.put(vertex.data, new ArrayList<>());
            }
            for (Pair<Integer, Integer> pair : tree) {
                edges.get(pair.first).add(pair.second);
                edges.get(pair.second).add(pair.first);
            }
            dfsAssertAcyclic(edges, seen, graph.vertices.iterator().next().data, null);
        }
    }
}
