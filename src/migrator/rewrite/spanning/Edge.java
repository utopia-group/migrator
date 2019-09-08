package migrator.rewrite.spanning;

/**
 * An edge in a graph. Contains a data field
 * and an original tip, along with many other auxiliary fields.
 *
 * @param <V> the type of the data stored in the vertices of the graph
 * @param <E> the type of the data stored in the edges of the graph
 */
public final class Edge<V, E> {
    /**
     * The data stored in this edge.
     */
    public final E data;
    /**
     * The tip of this edge, i.e. the vertex to which this points.
     * <p>
     * For example, if E is an edge from vertex A to B, then B is E's tip.
     */
    Vertex<V, E> tip;
    /**
     * The original tip of this edge. The tip field may be modified during
     * spanning tree enumeration, so this field is used to keep the original tip.
     */
    public final Vertex<V, E> originalTip;
    /**
     * The linked list node in which this edge is contained.
     */
    RawLinkedList.Node<Edge<V, E>> it;
    /**
     * The mate of this edge, which is the automatically created edge pointing opposite this edge.
     */
    Edge<V, E> mate;

    /**
     * An edge associated with this edge.
     * Used to keep track of the position of deleted edges;
     * usually this field is set to the edge following this edge.
     */
    Edge<V, E> other;

    /**
     * Constructs a new edge with the given data and tip.
     *
     * @param data the data associated with this edge
     * @param tip  the tip of this edge
     */
    public Edge(E data, Vertex<V, E> tip) {
        this.data = data;
        this.tip = tip;
        this.originalTip = tip;
    }

    /**
     * Return the original opposite tip of this edge.
     *
     * @return the original opposite tip
     */
    public Vertex<V, E> oppositeTip() {
        return mate.originalTip;
    }

    @Override
    public String toString() {
        return String.format("Edge(%s)", data);
    }
}
