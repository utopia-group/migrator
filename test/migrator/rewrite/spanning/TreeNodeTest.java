package migrator.rewrite.spanning;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

public class TreeNodeTest {

    @Test
    public void testTreeRooting() {
        Graph<Integer, Integer> graph = new Graph<>();
        Vertex<Integer, Integer> a, b, c, d;
        a = graph.addVertex(1);
        b = graph.addVertex(2);
        c = graph.addVertex(3);
        d = graph.addVertex(4);
        Set<Edge<Integer, Integer>> edges = new HashSet<>();
        edges.add(graph.addEdge(a, b, 101));
        edges.add(graph.addEdge(a, c, 102));
        edges.add(graph.addEdge(a, d, 103));
        TreeNode<Integer, Integer> root = TreeNode.buildRootedTree(edges);
        Assert.assertEquals(a, root.vertex);
        Assert.assertEquals(
                Stream.of(b, c, d).collect(Collectors.toSet()),
                root.children.stream().map(node -> node.vertex).collect(Collectors.toSet()));
        Assert.assertEquals(
                Stream.of(101, 102, 103).collect(Collectors.toSet()),
                root.edgeData.stream().collect(Collectors.toSet()));
        for (TreeNode<Integer, Integer> child : root.children) {
            Assert.assertEquals(Collections.emptyList(), child.children);
            Assert.assertEquals(Collections.emptyList(), child.edgeData);
        }
    }

}
