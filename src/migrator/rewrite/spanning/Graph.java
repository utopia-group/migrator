package migrator.rewrite.spanning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A graph containing vertices and edges.
 *
 * @param <V> the type of the data stored in the vertices of the graph
 * @param <E> the type of the data stored in the edges of the graph
 */
public final class Graph<V, E> {
    /**
     * The list of vertices stored in this graph.
     */
    public final List<Vertex<V, E>> vertices;
    /**
     * The list of edges stored in this graph.
     * For each edge added via {@link #addEdge},
     * there are two edge objects created,
     * pointing in opposite directions.
     */
    public final List<Edge<V, E>> edges;

    /**
     * The set of vertices that have not been shrunk.
     * When an edge is shrunk, one end of that edge is
     * collapsed into the other, reducing the number of vertices.
     */
    final Set<Vertex<V, E>> unshrunkVertices;

    // holds shrunken edges and near trees
    // at level l, (one-indexed) 1...l-1 holds shrunken edges to include
    // and l+1...n holds the near tree
    /**
     * Working array to hold data during spanning tree enumeration.
     * Holds shrunken edges and near trees.
     * At level {@code l}, the (one-indexed) range 1...{@code l}-1
     * holds shrunken edges to include,
     * and {@code l}+1...n holds the near tree for this level.
     */
    Edge<V, E>[] arr;

    /**
     * The tree consumer. Called for each spanning tree generated.
     */
    Consumer<List<Edge<V, E>>> treeConsumer;

    /**
     * Constructs a new empty graph.
     */
    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        unshrunkVertices = Collections.newSetFromMap(new IdentityHashMap<>());
    }

    /**
     * Adds a new vertex to the graph with no edges.
     *
     * @param data the data associated with the new vertex
     * @return the created vertex
     */
    public Vertex<V, E> addVertex(V data) {
        Vertex<V, E> vertex = new Vertex<>(data);
        vertices.add(vertex);
        unshrunkVertices.add(vertex);
        return vertex;
    }

    /**
     * Adds a new edge between the two given vertices.
     *
     * @param a    the first vertex; the source of the non-conjugate edge
     * @param b    the second vertex; the tip of the non-conjugate edge
     * @param data the data associated with the new edge
     * @return the created edge
     */
    public Edge<V, E> addEdge(Vertex<V, E> a, Vertex<V, E> b, E data) {
        if (a == b) {
            throw new IllegalArgumentException("loops not allowed");
        }
        Edge<V, E> edge, mate;
        edge = new Edge<>(data, b);
        mate = new Edge<>(data, a);
        edges.add(edge);
        edges.add(mate);
        edge.mate = mate;
        mate.mate = edge;
        edge.it = a.edges.pushBack(edge);
        mate.it = b.edges.pushBack(mate);
        return edge;
    }

    /**
     * Compute all adjacent vertices of the given vertex.
     *
     * @param vertex the given vertex
     * @return all adjacent vertices
     */
    public Set<Vertex<V, E>> getAdjacentVertices(Vertex<V, E> vertex) {
        Set<Vertex<V, E>> ret = new HashSet<>();
        for (Edge<V, E> edge : edges) {
            if (edge.originalTip.equals(vertex)) {
                ret.add(edge.mate.originalTip);
            }
            if (edge.mate.originalTip.equals(vertex)) {
                ret.add(edge.originalTip);
            }
        }
        return ret;
    }

    /**
     * Performs a depth-first search starting from the given index,
     * writing the edges encountered in order to {@link #arr}.
     * Uses the {@link Vertex#visited} field to keep track
     * of which vertices have been visited.
     *
     * @param start the starting vertex
     * @param index the index to start at when writing
     * @return the index past the last written edge, or {@code index} if none
     */
    private int generateDfsTreeHelper(Vertex<V, E> start, int index) {
        if (start.visited) {
            return index;
        }
        start.visited = true;
        for (Edge<V, E> edge : start.edges) {
            if (edge.tip.visited) {
                continue;
            }
            if (arr != null) {
                arr[index] = edge;
            }
            index++;
            index = generateDfsTreeHelper(edge.tip, index);
        }
        return index;
    }

    /**
     * Generates all spanning trees of this graph,
     * calling the given consumer for each.
     * <p>
     * The generated trees are guaranteed to differ by exactly one edge
     * deleted and added.
     *
     * @param treeConsumer called for each generated tree with a list of edges contained in the tree
     */
    public void generateSpanningTrees(Consumer<List<Edge<V, E>>> treeConsumer) {
        this.treeConsumer = treeConsumer;
        // setup
        @SuppressWarnings("unchecked")
        Edge<V, E>[] theArr = new Edge[vertices.size()];
        arr = theArr;
        for (Vertex<V, E> v : vertices) {
            v.visited = false;
        }
        Vertex<V, E> start = vertices.get(0);
        if (generateDfsTreeHelper(start, 1) != vertices.size()) {
            throw new IllegalArgumentException("graph not connected");
        }
        for (Vertex<V, E> v : vertices) {
            assert v.visited;
            v.visited = false;
        }
        // end setup
        if (vertices.size() == 1) {
            this.treeConsumer.accept(Collections.emptyList());
        } else {
            generateSpanningTrees(1);
        }
        assert this.treeConsumer == treeConsumer;
        this.treeConsumer = null;
        arr = null;
    }

    /**
     * Checks if the given edge is a bridge,
     * i.e. an edge that when removed disconnects the graph.
     * Performs a breadth-first search.
     *
     * @param bridge the edge to check
     * @return whether the given edge is a bridge or not
     */
    private boolean isBridge(Edge<V, E> bridge) {
        if (bridge.tip.degree() == 1 || bridge.mate.tip.degree() == 1) {
            return true;
        }
        Queue<Vertex<V, E>> bfsQueue = new LinkedList<>();
        bfsQueue.add(bridge.mate.tip);
        while (!bfsQueue.isEmpty()) {
            Vertex<V, E> v = bfsQueue.remove();
            if (v.visited) {
                continue;
            }
            v.visited = true;
            for (Edge<V, E> edge : v.edges) {
                if (edge == bridge || edge == bridge.mate) {
                    continue;
                }
                Vertex<V, E> neighbor = edge.tip;
                if (!neighbor.visited) {
                    bfsQueue.add(neighbor);
                }
            }
        }
        boolean connected = true;
        for (Vertex<V, E> vertex : unshrunkVertices) {
            if (!vertex.visited) {
                connected = false;
            }
            vertex.visited = false;
        }
        return !connected;
    }

    /**
     * Visits the tree specified by
     * {@link #arr}[0...<i>n</i>-1]
     * where <i>n</i> is the number of vertices.
     */
    private void visitTree() {
        // creates set of 1..n-1 edges and calls consumer
        List<Edge<V, E>> edgeSet = Collections.unmodifiableList(
                Arrays.asList(arr).subList(0, vertices.size() - 1));
        treeConsumer.accept(edgeSet);
    }

    /**
     * Generates the spanning trees for the given level,
     * starting at level 1.
     *
     * @param level {@link #vertices}{@code .size()} + 1 - {@link #unshrunkVertices}{@code .size()}
     */
    private void generateSpanningTrees(int level) {
        assert level <= vertices.size() - 1;
        if (level == vertices.size() - 1) {
            // two vertices left
            // assume they are connected:
            // ensured by bridge check
            assert unshrunkVertices.size() == 2;
            Vertex<V, E> arb = unshrunkVertices.iterator().next();
            for (Edge<V, E> edge : arb.edges) {
                arr[level - 1] = edge;
                visitTree();
            }
            return;
        }
        Edge<V, E> shrunkEdge = arr[level];
        Vertex<V, E> maxDeg, minDeg;
        maxDeg = shrunkEdge.tip;
        minDeg = shrunkEdge.mate.tip;
        if (minDeg.degree() > maxDeg.degree()) {
            Vertex<V, E> temp = minDeg;
            minDeg = maxDeg;
            maxDeg = temp;
            shrunkEdge = shrunkEdge.mate;
        }
        boolean removed = unshrunkVertices.remove(minDeg);
        assert removed;
        Deque<Edge<V, E>> shrunkEdges = new LinkedList<>();
        for (RawLinkedList.Node<Edge<V, E>> it = minDeg.edges.end();;) {
            it = it.prev;
            if (it.isHeader()) {
                break;
            }
            Edge<V, E> edge = it.element;
            // delete edges that become loops
            // i.e. edges from min to max
            // use other to hold where it goes
            // as iterators may be invalidated during recursion
            if (edge.tip == maxDeg) {
                it = edge.it = minDeg.edges.removeNode(it);
                edge.other = edge.it.isHeader() ? null : edge.it.element;
                edge.mate.it = maxDeg.edges.removeNode(edge.mate.it);
                edge.mate.other = edge.mate.it.isHeader() ? null : edge.mate.it.element;
                shrunkEdges.push(edge);
            } else {
                // reassign edge to max
                edge.mate.tip = maxDeg;
            }
        }
        // merge adjacency list
        Edge<V, E> mergedEnd;
        if (maxDeg.edges.isEmpty()) {
            mergedEnd = null;
        } else {
            mergedEnd = maxDeg.edges.begin().element;
        }
        int spliceCount = minDeg.edges.size();
        maxDeg.edges.spliceAllInBefore(minDeg.edges, maxDeg.edges.begin());
        arr[level - 1] = shrunkEdge;
        generateSpanningTrees(level + 1);
        // unmerge adjacency list
        if (mergedEnd != null) {
            RawLinkedList.Node<Edge<V, E>> splice = maxDeg.edges.unsafeSpliceOut(maxDeg.edges.begin(), mergedEnd.it, spliceCount);
            minDeg.edges.unsafeSpliceInAfter(splice, minDeg.edges.end(), spliceCount);
            assert minDeg.edges.size() == minDeg.edges.calculateSize();
            assert maxDeg.edges.size() == maxDeg.edges.calculateSize();
        } else {
            minDeg.edges.spliceAllInAfter(maxDeg.edges, minDeg.edges.end());
        }
        for (Edge<V, E> edge : minDeg.edges) {
            edge.mate.tip = minDeg;
        }
        // undelete shrunk edges
        while (!shrunkEdges.isEmpty()) {
            Edge<V, E> edge = shrunkEdges.pop();
            Edge<V, E> mate = edge.mate;
            edge.it = minDeg.edges.addBefore(edge.other == null ? edge.it : edge.other.it, edge);
            mate.it = maxDeg.edges.addBefore(mate.other == null ? mate.it : mate.other.it, mate);
            edge.other = null;
            mate.other = null;
        }
        // unshrink vertex
        unshrunkVertices.add(minDeg);
        // delete edge if not a bridge
        if (!isBridge(shrunkEdge)) {
            Edge<V, E> edge, mate;
            edge = shrunkEdge;
            mate = edge.mate;
            assert edge.it.element == edge;
            edge.it = minDeg.edges.removeNode(edge.it);
            mate.it = maxDeg.edges.removeNode(mate.it);
            edge.other = edge.it.isHeader() ? null : edge.it.element;
            mate.other = mate.it.isHeader() ? null : mate.it.element;
            generateSpanningTrees(level);
            // undelete
            edge.it = minDeg.edges.addBefore(edge.other == null ? edge.it : edge.other.it, edge);
            mate.it = maxDeg.edges.addBefore(mate.other == null ? mate.it : mate.other.it, mate);
            edge.other = null;
            mate.other = null;
        }
    }

    /**
     * Splits this graph into connected components.
     * After calling this function, this graph will be empty.
     *
     * @return a list of the connected components of this graph.
     *         Will be empty if this graph is empty.
     */
    public List<Graph<V, E>> splitComponents() {
        List<Graph<V, E>> components = new ArrayList<>();
        assert arr == null;
        @SuppressWarnings("unchecked")
        Edge<V, E> theArr[] = new Edge[vertices.size()];
        arr = theArr;
        assert unshrunkVertices.size() == vertices.size();
        for (Vertex<V, E> v : vertices) {
            v.visited = false;
        }
        int startIndex = 0;
        while (true) {
            while (startIndex < vertices.size() && vertices.get(startIndex).visited) {
                startIndex++;
            }
            if (startIndex >= vertices.size()) {
                break; // no more components
            }
            Vertex<V, E> start = vertices.get(startIndex);
            assert arr[0] == null;
            int last = generateDfsTreeHelper(start, 1);
            Graph<V, E> component = new Graph<>();
            for (int i = 0; i < last; i++) {
                Edge<V, E> edge = arr[i];
                assert (i == 0) == (edge == null);
                Vertex<V, E> vertex = edge == null ? start : edge.originalTip;
                assert vertex != null;
                component.vertices.add(vertex);
                component.unshrunkVertices.add(vertex);
                for (Edge<V, E> out : vertex.edges) {
                    assert out.tip == out.originalTip;
                    assert out.tip.visited;
                    out.tip = null; // mark added edges
                    component.edges.add(out);
                }
                arr[i] = null;
            }
            components.add(component);
        }
        arr = null;
        for (Vertex<V, E> vertex : vertices) {
            assert vertex.visited;
            vertex.visited = false;
        }
        for (Edge<V, E> edge : edges) {
            assert edge.tip == null;
            edge.tip = edge.originalTip;
        }
        vertices.clear();
        edges.clear();
        return components;
    }

    /**
     * Computes the determinant of the given matrix.
     * <p>
     * Uses the Bareiss algorithm, which runs in
     * <i>O</i>(<i>n</i><sup>3</sup>) time
     * assuming constant time arithmetic,
     * where <i>n</i> is the size of the matrix.
     *
     * @param matrix the matrix of which to compute the determinant
     * @return the determinant of the matrix
     */
    public static long determinant(long[][] matrix) {
        int N = matrix.length;
        if (N == 1) {
            assert matrix[0].length == 1;
            return matrix[0][0];
        }
        if (N == 2) {
            assert matrix[0].length == 2 && matrix[1].length == 2;
            long a, b, c, d;
            a = matrix[0][0];
            b = matrix[0][1];
            c = matrix[1][0];
            d = matrix[1][1];
            return a * d - b * c;
        }
        // Bareiss algorithm
        long sign = 1;
        for (int i = 0; i < N - 1; i++) {
            if (i != 0 && matrix[i - 1][i - 1] == 0) {
                // pivot below
                boolean found = false;
                for (int j = i; j < N; j++) {
                    if (matrix[j][i - 1] != 0) {
                        found = true;
                        long[] row = matrix[i - 1];
                        matrix[i - 1] = matrix[j];
                        matrix[j] = row;
                        sign *= -1;
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("found zero in diagonal and could not find pivot row");
                }
            }
            for (int j = i + 1; j < N; j++) {
                for (int k = i + 1; k < N; k++) {
                    matrix[j][k] = Math.subtractExact(
                            Math.multiplyExact(matrix[j][k], matrix[i][i]),
                            Math.multiplyExact(matrix[j][i], matrix[i][k]));
                    if (i != 0) {
                        assert matrix[j][k] % matrix[i - 1][i - 1] == 0;
                        matrix[j][k] /= matrix[i - 1][i - 1];
                    }
                }
            }
        }
        return sign * matrix[N - 1][N - 1];
    }

    /**
     * Calculates the number of spanning trees in the graph
     * using Kirchhoff's theorem.
     *
     * @return the number of spanning trees in the graph
     */
    public int calculateNumSpanningTrees() {
        long[][] matrix = new long[vertices.size() - 1][vertices.size() - 1];
        Map<Vertex<V, E>, Integer> indices = new IdentityHashMap<>();
        for (ListIterator<Vertex<V, E>> it = vertices.listIterator(); it.hasNext();) {
            int index = it.nextIndex() - 1;
            Vertex<V, E> vertex = it.next();
            indices.put(vertex, index);
            if (index != -1)
                matrix[index][index] = vertex.degree();
        }
        for (Edge<V, E> edge : edges) {
            int v1, v2;
            v1 = indices.get(edge.mate.originalTip);
            v2 = indices.get(edge.originalTip);
            if (v1 == -1 || v2 == -1) {
                continue;
            }
            matrix[v1][v2]--;
        }
        return (int) determinant(matrix);
    }
}
