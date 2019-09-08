package migrator.rewrite.spanning;

/**
 * A vertex in a graph. Contains a data field and a list of edges (adjacency list).
 *
 * @param <V> the type of the data stored in the vertices of the graph
 * @param <E> the type of the data stored in the edges of the graph
 */
public final class Vertex<V, E> {
    /**
     * The data stored in this vertex.
     */
    public final V data;
    /**
     * The list of edges going out from this vertex.
     */
    final RawLinkedList<Edge<V, E>> edges;

    /**
     * Whether this vertex has been visited.
     * Auxiliary field used in spanning tree enumeration.
     */
    boolean visited;

    /**
     * Constructs a new vertex with the given data.
     *
     * @param data the data associated with this vertex
     */
    public Vertex(V data) {
        this.data = data;
        edges = new RawLinkedList<>();
    }

    /**
     * The degree of this vertex, equal to the number of edges adjacent to this vertex.
     *
     * @return the degree of this vertex
     */
    public int degree() {
        return edges.size();
    }

    @Override
    public String toString() {
        return String.format("Vertex(%s)", data);
    }
}
