package migrator.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import migrator.LoggerWrapper;
import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.spanning.Edge;
import migrator.rewrite.spanning.Graph;
import migrator.rewrite.spanning.PruneCollector;
import migrator.rewrite.spanning.TreeNode;
import migrator.rewrite.spanning.Vertex;

/**
 * Supplier for join correspondences.
 */
public final class JoinCorrSupplier implements IJoinCorrSupplier {
    /**
     * Source schema.
     */
    private final SchemaDef srcSchema;
    /**
     * Target schema.
     */
    private final SchemaDef tgtSchema;
    /**
     * A map from table names to their corresponding vertices in the graph.
     */
    private final Map<String, Vertex<String, ColumnRef>> tableToVertices;
    /**
     * A map from table names to their corresponding connected components in the graph.
     */
    private final Map<String, Graph<String, ColumnRef>> tableToComponents;
    /**
     * The logger.
     */
    private final static Logger LOGGER = LoggerWrapper.getInstance();
    /**
     * The maximum number of vertices to perform safe pruning.
     */
    private final static int SAFE_PRUNE_BOUND = 20;
    /**
     * The maximum length of paths for unsafe pruning.
     */
    private final static int PATH_LENGTH_BOUND = 3;

    /**
     * Create a join correspondence supplier.
     *
     * @param srcSchema source schema
     * @param tgtSchema target schema
     */
    public JoinCorrSupplier(SchemaDef srcSchema, SchemaDef tgtSchema) {
        this.srcSchema = srcSchema;
        this.tgtSchema = tgtSchema;
        assert this.srcSchema != null && this.tgtSchema != null;
        tableToVertices = new HashMap<>();
        tableToComponents = new HashMap<>();
        buildTargetSchemaGraph();
    }

    @Override
    public List<JoinChain> getJoinChains(ValueCorrespondence valueCorr, JoinChain chain) {
        Set<ColumnRef> columns = RewriteUtil.allColumnsInTable(chain.table, srcSchema);
        for (Join join : chain.joins) {
            columns.addAll(RewriteUtil.allColumnsInTable(join.getDest(), srcSchema));
        }
        return getJoinChainsForColumns(valueCorr, chain, columns);
    }

    @Override
    public List<JoinChain> getJoinChainsForColumns(ValueCorrespondence valueCorr, JoinChain chain, Set<ColumnRef> columns) {
        // TODO: check all columns belong to chain
        Set<String> tableNames = tablesMappedTo(valueCorr, columns);
        if (tableNames.size() == 1) {
            TableRef tableRef = new TableRef(tableNames.iterator().next());
            List<Join> joins = Collections.emptyList();
            return Collections.singletonList(new JoinChain(tableRef, joins));
        } else {
            return new ArrayList<>(getJoinChainsForTables(tableNames));
        }
    }

    private Set<JoinChain> getJoinChainsForTables(Set<String> tableNames) {
        assert tableNames.size() > 0;
        // check if all tables belong to the same component
        Graph<String, ColumnRef> component = tableToComponents.get(tableNames.iterator().next());
        for (String tableName : tableNames) {
            // directly compare the address
            if (tableToComponents.get(tableName) != component) {
                throw new RewriteException("Target tables cannot join together");
            }
        }
        // collect all join chains
        Set<JoinChain> ret = new HashSet<>();
        Graph<String, ColumnRef> prunedComponent;
        if (component.vertices.size() <= SAFE_PRUNE_BOUND) {
            prunedComponent = pruneGraphSafe(component, tableNames);
        } else {
            LOGGER.fine(String.format("Start unsafe pruning because of too many vertices (%s)", component.vertices.size()));
            prunedComponent = pruneGraphUnsafe(component, tableNames);
            LOGGER.fine(String.format("After unsafe pruning:\n%s vertices: %s\n%s edges: %s\ntable names: %s",
                    prunedComponent.vertices.size(), prunedComponent.vertices,
                    prunedComponent.edges.size(), prunedComponent.edges, tableNames));
        }
        PruneCollector<String, ColumnRef> collector = new PruneCollector<>(v -> tableNames.contains(v.data));
        prunedComponent.generateSpanningTrees(collector);
        for (Set<Edge<String, ColumnRef>> tree : collector.getTrees()) {
            ret.add(buildJoinChainFromSpanningTree(tree));
        }
        return ret;
    }

    private JoinChain buildJoinChainFromSpanningTree(Set<Edge<String, ColumnRef>> tree) {
        TreeNode<String, ColumnRef> root = TreeNode.buildRootedTree(tree);
        List<Join> joins = convertEdgesToJoins(root);
        TableRef tableRef = new TableRef(root.vertex.data);
        return new JoinChain(tableRef, joins);
    }

    private List<Join> convertEdgesToJoins(TreeNode<String, ColumnRef> node) {
        List<Join> ret = new ArrayList<>();
        for (int i = 0; i < node.children.size(); ++i) {
            TreeNode<String, ColumnRef> child = node.children.get(i);
            String tableName = node.vertex.data;
            ColumnRef fk = node.edgeData.get(i);
            if (fk.tableName.equals(tableName)) {
                ColumnRef refKey = RewriteUtil.getReferenceKey(fk, tgtSchema);
                assert refKey.tableName.equals(child.vertex.data);
                ret.add(new Join(new TableRef(refKey.tableName), fk, refKey));
            } else {
                assert child.vertex.data.equals(fk.tableName);
                ColumnRef refKey = RewriteUtil.getReferenceKey(fk, tgtSchema);
                assert refKey.tableName.equals(tableName);
                ret.add(new Join(new TableRef(fk.tableName), refKey, fk));
            }
            ret.addAll(convertEdgesToJoins(child));
        }
        return ret;
    }

    private void buildTargetSchemaGraph() {
        // vertex data: table name, edge data: foreign key
        Graph<String, ColumnRef> graph = new Graph<>();
        // create a vertex for each table
        for (TableDef tableDef : tgtSchema.tables.values()) {
            String tableName = tableDef.name;
            assert !tableToVertices.containsKey(tableName);
            Vertex<String, ColumnRef> vertex = graph.addVertex(tableName);
            tableToVertices.put(tableName, vertex);
        }
        // create an edge for each foreign key
        for (TableDef tableDef : tgtSchema.tables.values()) {
            for (Map.Entry<String, ForeignKeyDef> entry : tableDef.foreignKeys.entrySet()) {
                ColumnRef fk = new ColumnRef(entry.getKey(), tableDef.name);
                ColumnRef refKey = entry.getValue().toColumnRef();
                Vertex<String, ColumnRef> fkTable = tableToVertices.get(fk.tableName);
                Vertex<String, ColumnRef> refKeyTable = tableToVertices.get(refKey.tableName);
                if (fkTable == null || refKeyTable == null) {
                    throw new IllegalStateException("Cannot find vertex");
                }
                graph.addEdge(fkTable, refKeyTable, fk);
            }
        }
        // decompose to connected components and update the map
        List<Graph<String, ColumnRef>> components = graph.splitComponents();
        for (Graph<String, ColumnRef> component : components) {
            for (Vertex<String, ColumnRef> vertex : component.vertices) {
                String tableName = vertex.data;
                if (tableToComponents.containsKey(tableName)) {
                    throw new IllegalStateException("Overlapped components");
                }
                tableToComponents.put(tableName, component);
            }
        }
    }

    private Set<String> tablesMappedTo(ValueCorrespondence valueCorr, Set<ColumnRef> columns) {
        // TODO: handle the existential quantifier
        Set<String> ret = new HashSet<>();
        for (ColumnRef column : columns) {
            if (!valueCorr.containsKey(column)) {
                throw new RewriteException("Value correspondence cannot map " + column.toSqlString());
            }
            for (ColumnRef tgtColumn : valueCorr.get(column)) {
                ret.add(tgtColumn.tableName);
            }
        }
        return ret;
    }

    /**
     * Prune the graph. First compute all vertices on any path between each pair of the provided vertices.
     * Then create a new graph that only contains these vertices and edges between them.
     *
     * @param <V>     type of vertex data
     * @param <E>     type of edge data
     * @param graph   the graph to prune
     * @param dataSet provided vertices, represented by their DISTINCT data
     * @return pruned graph
     */
    static <V, E> Graph<V, E> pruneGraphSafe(Graph<V, E> graph, Set<V> dataSet) {
        if (dataSet == null || dataSet.size() < 2) {
            throw new IllegalArgumentException("Need at least two vertices");
        }
        // build a map from vertex data to original vertices
        Map<V, Vertex<V, E>> dataToOriginalVertices = buildDataToVerticesMap(graph);
        // compute the used vertices
        Set<Vertex<V, E>> usedVertices = new HashSet<>();
        List<V> dataList = new ArrayList<>(dataSet);
        for (int i = 0; i < dataList.size(); ++i) {
            Vertex<V, E> from = dataToOriginalVertices.get(dataList.get(i));
            for (int j = i + 1; j < dataList.size(); ++j) {
                Vertex<V, E> to = dataToOriginalVertices.get(dataList.get(j));
                Set<Vertex<V, E>> vertices = computeVerticesInAllPaths(graph, from, to);
                usedVertices.addAll(vertices);
            }
        }
        // build the pruned graph
        return buildPrunedGraph(graph, usedVertices);
    }

    private static <V, E> Map<V, Vertex<V, E>> buildDataToVerticesMap(Graph<V, E> graph) {
        Map<V, Vertex<V, E>> dataToVertices = new HashMap<>();
        for (Vertex<V, E> vertex : graph.vertices) {
            if (dataToVertices.containsKey(vertex.data)) {
                throw new IllegalStateException("Vertex data are not distinct");
            }
            dataToVertices.put(vertex.data, vertex);
        }
        return dataToVertices;
    }

    /**
     * Build a new graph that only contains the retained vertices and edges between them.
     *
     * @param graph            the original graph
     * @param retainedVertices retained vertices in the original graph
     * @return a new pruned graph
     */
    private static <V, E> Graph<V, E> buildPrunedGraph(Graph<V, E> graph, Set<Vertex<V, E>> retainedVertices) {
        Graph<V, E> prunedGraph = new Graph<>();
        Map<V, Vertex<V, E>> dataToVertices = new HashMap<>();
        for (Vertex<V, E> vertex : retainedVertices) {
            Vertex<V, E> newVertex = prunedGraph.addVertex(vertex.data);
            dataToVertices.put(vertex.data, newVertex);
        }
        for (Edge<V, E> edge : graph.edges) {
            Vertex<V, E> v1 = edge.originalTip;
            Vertex<V, E> v2 = edge.oppositeTip();
            if (retainedVertices.contains(v1) && retainedVertices.contains(v2)) {
                prunedGraph.addEdge(dataToVertices.get(v1.data), dataToVertices.get(v2.data), edge.data);
            }
        }
        return prunedGraph;
    }

    /**
     * Compute all vertices of all paths between from and to in the graph.
     *
     * @param graph the graph
     * @param from  source vertex in the graph
     * @param to    target vertex in the graph
     * @return all vertices occurred in any path
     */
    private static <V, E> Set<Vertex<V, E>> computeVerticesInAllPaths(Graph<V, E> graph, Vertex<V, E> from, Vertex<V, E> to) {
        Set<Vertex<V, E>> ret = new HashSet<>();
        List<Vertex<V, E>> currentPath = new ArrayList<>();
        currentPath.add(from);
        computeVerticesInAllPathsImpl(graph, from, to, new HashSet<>(), currentPath, ret);
        return ret;
    }

    private static <V, E> void computeVerticesInAllPathsImpl(
            Graph<V, E> graph,
            Vertex<V, E> from,
            Vertex<V, E> to,
            Set<Vertex<V, E>> visited,
            List<Vertex<V, E>> currentPath,
            Set<Vertex<V, E>> ret) {
        visited.add(from);
        if (from.equals(to)) {
            ret.addAll(currentPath);
        }
        for (Vertex<V, E> v : graph.getAdjacentVertices(from)) {
            if (!visited.contains(v)) {
                currentPath.add(v);
                computeVerticesInAllPathsImpl(graph, v, to, visited, currentPath, ret);
                currentPath.remove(v);
            }
        }
        visited.remove(from);
    }

    /**
     * Prune the graph unsafely. First compute all vertices and their neighbors for the given data set.
     * Then create a new graph that only contains these vertices and edges between them.
     *
     * @param <V>     type of vertex data
     * @param <E>     type of edge data
     * @param graph   the graph to prune
     * @param dataSet provided vertices, represented by their DISTINCT data
     * @return a new pruned graph
     */
    static <V, E> Graph<V, E> pruneGraphUnsafe(Graph<V, E> graph, Set<V> dataSet) {
        if (dataSet == null || dataSet.size() < 2) {
            throw new IllegalArgumentException("Need at least two vertices");
        }
        // build a map from vertex data to original vertices
        Map<V, Vertex<V, E>> dataToOriginalVertices = buildDataToVerticesMap(graph);
        // compute the used vertices
        Set<Vertex<V, E>> usedVertices = new HashSet<>();
        List<V> dataList = new ArrayList<>(dataSet);
        for (int i = 0; i < dataList.size(); ++i) {
            Vertex<V, E> from = dataToOriginalVertices.get(dataList.get(i));
            for (int j = i + 1; j < dataList.size(); ++j) {
                Vertex<V, E> to = dataToOriginalVertices.get(dataList.get(j));
                Set<Vertex<V, E>> vertices = computeVerticesInBoundedPaths(graph, from, to, PATH_LENGTH_BOUND);
                usedVertices.addAll(vertices);
            }
        }
        // build the pruned graph
        return buildPrunedGraph(graph, usedVertices);
    }

    private static <V, E> Set<Vertex<V, E>> computeVerticesInBoundedPaths(
            Graph<V, E> graph, Vertex<V, E> from, Vertex<V, E> to, int maxLength) {
        Set<Vertex<V, E>> ret = new HashSet<>();
        List<Vertex<V, E>> currentPath = new ArrayList<>();
        currentPath.add(from);
        computeVerticesInBoundedPathsImpl(graph, from, to, maxLength, new HashSet<>(), currentPath, ret);
        return ret;
    }

    private static <V, E> void computeVerticesInBoundedPathsImpl(
            Graph<V, E> graph,
            Vertex<V, E> from,
            Vertex<V, E> to,
            int maxLength,
            Set<Vertex<V, E>> visited,
            List<Vertex<V, E>> currentPath,
            Set<Vertex<V, E>> ret) {
        if (maxLength == 0) {
            return;
        }
        visited.add(from);
        if (from.equals(to)) {
            ret.addAll(currentPath);
        }
        for (Vertex<V, E> v : graph.getAdjacentVertices(from)) {
            if (!visited.contains(v)) {
                currentPath.add(v);
                computeVerticesInBoundedPathsImpl(graph, v, to, maxLength - 1, visited, currentPath, ret);
                currentPath.remove(v);
            }
        }
        visited.remove(from);
    }
}
