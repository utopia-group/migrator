package migrator.rewrite;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.parser.AntlrParser;
import migrator.rewrite.spanning.Graph;
import migrator.rewrite.spanning.Vertex;
import migrator.util.ListMultiMap;

public class JoinCorrSupplierTest {

    private SchemaDef buildSourceSchema() {
        String text = ""
                + "CREATE TABLE A ("
                + "    a INT,"
                + "    b INT,"
                + "    c INT,"
                + "    d INT,"
                + "    PRIMARY KEY (a));";
        return AntlrParser.parseSchema(text);
    }

    private SchemaDef buildTargetSchema() {
        String text = ""
                + "CREATE TABLE A ("
                + "    a INT,"
                + "    e INT,"
                + "    f INT,"
                + "    g INT,"
                + "    FOREIGN KEY (e) REFERENCES B (e),"
                + "    FOREIGN KEY (f) REFERENCES C (f),"
                + "    FOREIGN KEY (g) REFERENCES D (g),"
                + "    PRIMARY KEY (a));"
                + "CREATE TABLE B ("
                + "    e INT,"
                + "    b INT,"
                + "    PRIMARY KEY (e));"
                + "CREATE TABLE C ("
                + "    f INT,"
                + "    c INT,"
                + "    PRIMARY KEY (f));"
                + "CREATE TABLE D ("
                + "    g INT,"
                + "    d INT,"
                + "    PRIMARY KEY (g));";
        return AntlrParser.parseSchema(text);
    }

    private ValueCorrespondence buildValueCorrespondence() {
        ListMultiMap<ColumnRef, ColumnRef> mapping = new ListMultiMap<>();
        mapping.put(new ColumnRef("a", "A"), new ColumnRef("a", "A"));
        mapping.put(new ColumnRef("b", "A"), new ColumnRef("b", "B"));
        mapping.put(new ColumnRef("c", "A"), new ColumnRef("c", "C"));
        mapping.put(new ColumnRef("d", "A"), new ColumnRef("d", "D"));
        return new ValueCorrespondence(mapping);
    }

    private void assertMapTo(Set<ColumnRef> srcColumns, Set<JoinChain> expectedChains) {
        SchemaDef srcSchema = buildSourceSchema();
        SchemaDef tgtSchema = buildTargetSchema();
        ValueCorrespondence valueCorr = buildValueCorrespondence();
        IJoinCorrSupplier supplier = new JoinCorrSupplier(srcSchema, tgtSchema);
        List<JoinChain> tgtChains = supplier.getJoinChainsForColumns(valueCorr, null, srcColumns);
        Assert.assertEquals(expectedChains.size(), tgtChains.size());
        Set<JoinChain> tgtChainSet = new HashSet<>(tgtChains);
        Assert.assertEquals(expectedChains, tgtChainSet);
    }

    @Test
    public void testJoinCorr1() {
        Set<ColumnRef> srcColumns = Stream.of(
                new ColumnRef("a", "A"))
                .collect(Collectors.toSet());
        JoinChain tgtChain = new JoinChain(new TableRef("A"), Collections.emptyList());
        assertMapTo(srcColumns, Collections.singleton(tgtChain));
    }

    @Test
    public void testJoinCorr2() {
        Set<ColumnRef> srcColumns = Stream.of(
                new ColumnRef("a", "A"),
                new ColumnRef("b", "A"))
                .collect(Collectors.toSet());
        JoinChain tgtChain = new JoinChain(new TableRef("A"), Stream.of(
                new Join(new TableRef("B"), new ColumnRef("e", "A"), new ColumnRef("e", "B")))
                .collect(Collectors.toList()));
        assertMapTo(srcColumns, Collections.singleton(tgtChain));
    }

    @Test
    public void testJoinCorr3() {
        Set<ColumnRef> srcColumns = Stream.of(
                new ColumnRef("a", "A"),
                new ColumnRef("b", "A"),
                new ColumnRef("c", "A"))
                .collect(Collectors.toSet());
        JoinChain tgtChain = new JoinChain(new TableRef("A"), Stream.of(
                new Join(new TableRef("B"), new ColumnRef("e", "A"), new ColumnRef("e", "B")),
                new Join(new TableRef("C"), new ColumnRef("f", "A"), new ColumnRef("f", "C")))
                .collect(Collectors.toList()));
        assertMapTo(srcColumns, Collections.singleton(tgtChain));
    }

    @Test
    public void testJoinCorr4() {
        Set<ColumnRef> srcColumns = Stream.of(
                new ColumnRef("a", "A"),
                new ColumnRef("b", "A"),
                new ColumnRef("c", "A"),
                new ColumnRef("d", "A"))
                .collect(Collectors.toSet());
        JoinChain tgtChain = new JoinChain(new TableRef("A"), Stream.of(
                new Join(new TableRef("B"), new ColumnRef("e", "A"), new ColumnRef("e", "B")),
                new Join(new TableRef("C"), new ColumnRef("f", "A"), new ColumnRef("f", "C")),
                new Join(new TableRef("D"), new ColumnRef("g", "A"), new ColumnRef("g", "D")))
                .collect(Collectors.toList()));
        assertMapTo(srcColumns, Collections.singleton(tgtChain));
    }

    private Graph<String, Integer> buildGraph1() {
        Graph<String, Integer> graph = new Graph<>();
        Vertex<String, Integer> a, b, c, d, e;
        a = graph.addVertex("a");
        b = graph.addVertex("b");
        c = graph.addVertex("c");
        d = graph.addVertex("d");
        e = graph.addVertex("e");
        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 2);
        graph.addEdge(c, a, 3);
        graph.addEdge(a, d, 4);
        graph.addEdge(b, e, 5);
        return graph;
    }

    @Test
    public void testGraphPruningSafe1() {
        Graph<String, Integer> graph = buildGraph1();
        Set<String> dataSet = Stream.of("a", "b").collect(Collectors.toSet());
        Graph<String, Integer> prunedGraph = JoinCorrSupplier.pruneGraphSafe(graph, dataSet);
        Set<String> expectedVertices = Stream.of("a", "b", "c").collect(Collectors.toSet());
        Set<Integer> expectedEdges = Stream.of(1, 2, 3).collect(Collectors.toSet());
        Assert.assertEquals(expectedVertices, prunedGraph.vertices.stream()
                .map(v -> v.data).collect(Collectors.toSet()));
        Assert.assertEquals(expectedEdges, prunedGraph.edges.stream()
                .map(edge -> edge.data).collect(Collectors.toSet()));
    }

    @Test
    public void testGraphPruningUnsafe1() {
        Graph<String, Integer> graph = buildGraph1();
        Set<String> dataSet = Stream.of("a", "b").collect(Collectors.toSet());
        Graph<String, Integer> prunedGraph = JoinCorrSupplier.pruneGraphUnsafe(graph, dataSet);
        Set<String> expectedVertices = Stream.of("a", "b", "c").collect(Collectors.toSet());
        Set<Integer> expectedEdges = Stream.of(1, 2, 3).collect(Collectors.toSet());
        Assert.assertEquals(expectedVertices, prunedGraph.vertices.stream()
                .map(v -> v.data).collect(Collectors.toSet()));
        Assert.assertEquals(expectedEdges, prunedGraph.edges.stream()
                .map(edge -> edge.data).collect(Collectors.toSet()));
    }

    private Graph<String, Integer> buildGraph2() {
        Graph<String, Integer> graph = new Graph<>();
        Vertex<String, Integer> a, b, c, d;
        a = graph.addVertex("a");
        b = graph.addVertex("b");
        c = graph.addVertex("c");
        d = graph.addVertex("d");
        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 2);
        graph.addEdge(c, d, 3);
        graph.addEdge(d, a, 4);
        return graph;
    }

    @Test
    public void testGraphPruningSafe2() {
        Graph<String, Integer> graph = buildGraph2();
        Set<String> dataSet = Stream.of("a", "b").collect(Collectors.toSet());
        Graph<String, Integer> prunedGraph = JoinCorrSupplier.pruneGraphSafe(graph, dataSet);
        Set<String> expectedVertices = Stream.of("a", "b", "c", "d").collect(Collectors.toSet());
        Set<Integer> expectedEdges = Stream.of(1, 2, 3, 4).collect(Collectors.toSet());
        Assert.assertEquals(expectedVertices, prunedGraph.vertices.stream()
                .map(v -> v.data).collect(Collectors.toSet()));
        Assert.assertEquals(expectedEdges, prunedGraph.edges.stream()
                .map(edge -> edge.data).collect(Collectors.toSet()));
    }

    @Test
    public void testGraphPruningUnsafe2() {
        Graph<String, Integer> graph = buildGraph2();
        Set<String> dataSet = Stream.of("a", "b").collect(Collectors.toSet());
        Graph<String, Integer> prunedGraph = JoinCorrSupplier.pruneGraphUnsafe(graph, dataSet);
        Set<String> expectedVertices = Stream.of("a", "b").collect(Collectors.toSet());
        Set<Integer> expectedEdges = Stream.of(1).collect(Collectors.toSet());
        Assert.assertEquals(expectedVertices, prunedGraph.vertices.stream()
                .map(v -> v.data).collect(Collectors.toSet()));
        Assert.assertEquals(expectedEdges, prunedGraph.edges.stream()
                .map(edge -> edge.data).collect(Collectors.toSet()));
    }
}
