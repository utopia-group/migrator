package migrator.rewrite.spanning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Contains utility functions used for generating join correspondences.
 */
public class GraphUtil {
    // unfortunately we must make this public
    // so that callers can pass in a parameterized constructor for the queue
    /**
     * A node used in topological sorting.
     * Holds a vertex and some metadata.
     *
     * @param <T> the type of vertex in this node
     * @param <K> the type of the unique key associated with a vertex
     *
     * @see GraphUtil#topologicalSort
     * @see GraphUtil#topologicalSortReversed
     */
    public static final class TopSortNode<T, K> {
        /**
         * The vertex held by this node.
         */
        private T vertex;
        /**
         * The key corresponding to this node's vertex.
         */
        private K key;
        /**
         * The outgoing edges from this node's vertex.
         */
        private Collection<K> edges;
        /**
         * The number of edges that terminate at this vertex.
         */
        private int refCount;

        /**
         * Constructs a new node with the given fields.
         *
         * @param vertex   the vertex corresponding to this node
         * @param key      the key corresponding to the vertex
         * @param edges    the outgoing edges of this vertex,
         *                 or an empty list to be filled later
         * @param refCount initially 0
         */
        public TopSortNode(T vertex, K key, Collection<K> edges, int refCount) {
            this.vertex = vertex;
            this.key = key;
            this.edges = edges;
            this.refCount = refCount;
        }

        /**
         * Gets the vertex associated with this node.
         *
         * @return the vertex, as passed into
         *         {@link GraphUtil#topologicalSort GraphUtil.topologicalSort}
         *         or {@link GraphUtil#topologicalSortReversed GraphUtil.topologicalSortReversed}.
         */
        public T getVertex() {
            return vertex;
        }

        /**
         * Gets the key associated with this vertex.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns a supplier of priority queues
         * comparing {@code TopSortNode} objects
         * using a comparator on the vertices.
         *
         * @param vertexComparator the comparator to use for vertices
         * @param <T>              the type of the vertices to be compared
         * @param <K>              the type of the keys generated
         * @return a supplier of priority queues
         */
        public static <T, K> Supplier<Queue<TopSortNode<T, K>>> priorityQueueSupplier(Comparator<T> vertexComparator) {
            return () -> new PriorityQueue<>(Comparator.comparing(TopSortNode::getVertex, vertexComparator));
        }
    }

    /**
     * Performs a topological sort given the computed nodes.
     *
     * @param nodes         a map from key to node
     * @param queueSupplier a supplier that provides the queue to use when selecting the next node to process
     * @param <T>           the type of the elements in the returned list; must be a supertype of U
     * @param <U>           the type of the vertices
     * @param <K>           the type of the keys corresponding to the vertices
     * @return the vertices corresponding to the nodes, in topological order
     */
    private static <T, U extends T, K> List<T> topSortHelper(
            HashMap<K, TopSortNode<U, K>> nodes,
            Supplier<Queue<TopSortNode<U, K>>> queueSupplier) {
        List<T> result = new ArrayList<>();
        Queue<TopSortNode<U, K>> queue = queueSupplier.get();
        for (TopSortNode<U, K> node : nodes.values()) {
            if (node.refCount == 0) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            TopSortNode<U, K> cur = queue.remove();
            result.add(cur.getVertex());
            for (K neighborKey : cur.edges) {
                TopSortNode<U, K> neighbor = nodes.get(neighborKey);
                neighbor.refCount--;
                if (neighbor.refCount == 0) {
                    queue.add(neighbor);
                }
            }
        }
        if (result.size() != nodes.size()) {
            throw new IllegalArgumentException("cycle detected");
        }
        return result;
    }

    /**
     * Computes a topological sort of the given elements.
     *
     * @param list          the objects (vertices) to sort
     * @param edgeSupplier  a function that gives the outgoing edges from a vertex (i.e. its dependents)
     * @param keySupplier   an injective function that gives a unique key for each vertex
     * @param queueSupplier a function that gives the queue to use when choosing the next node to process
     * @param <T>           the type of the elements in the returned list; must be a supertype of U
     * @param <U>           the type of the vertices
     * @param <K>           the type of the keys corresponding to the vertices
     * @return the given objects sorted in topological order
     * @see #topologicalSortReversed
     */
    public static <T, U extends T, K> List<T> topologicalSort(
            Collection<U> list,
            Function<U, Collection<K>> edgeSupplier,
            Function<U, K> keySupplier,
            Supplier<Queue<TopSortNode<U, K>>> queueSupplier) {
        HashMap<K, TopSortNode<U, K>> nodes = new HashMap<>();
        for (U obj : list) {
            K key = keySupplier.apply(obj);
            Collection<K> edges = edgeSupplier.apply(obj);
            if (edges == null) {
                edges = Collections.emptyList();
            }
            nodes.put(key, new TopSortNode<>(obj, key, edges, 0));
        }
        for (TopSortNode<U, K> node : nodes.values()) {
            for (K neighborKey : node.edges) {
                nodes.get(neighborKey).refCount++;
            }
        }
        return topSortHelper(nodes, queueSupplier);
    }

    /**
     * Computes a topological sort of the given elements.
     * The edges are specified in reversed order
     * as compared to {@link #topologicalSort topologicalSort(...)}.
     *
     * @param list             the objects (vertices) to sort
     * @param backEdgeSupplier a function that gives the incoming edges from a vertex (i.e. its dependencies)
     * @param keySupplier      an injective function that gives a unique key for each vertex
     * @param queueSupplier    a function that gives the queue to use when choosing the next node to process
     * @param <T>              the type of the elements in the returned list; must be a supertype of U
     * @param <U>              the type of the vertices
     * @param <K>              the type of the keys corresponding to the vertices
     * @return the given objects sorted in topological order
     * @see #topologicalSort(Collection, Function, Function, Supplier)
     */
    public static <T, U extends T, K> List<T> topologicalSortReversed(
            Collection<U> list,
            Function<U, Collection<K>> backEdgeSupplier,
            Function<U, K> keySupplier,
            Supplier<Queue<TopSortNode<U, K>>> queueSupplier) {
        HashMap<K, TopSortNode<U, K>> nodes = new HashMap<>();
        for (U obj : list) {
            K key = keySupplier.apply(obj);
            nodes.put(key, new TopSortNode<>(obj, key, new ArrayList<>(), 0));
        }
        for (TopSortNode<U, K> node : nodes.values()) {
            Collection<K> backEdges = backEdgeSupplier.apply(node.getVertex());
            if (backEdges == null) {
                continue;
            }
            for (K neighborKey : backEdges) {
                nodes.get(neighborKey).edges.add(node.getKey());
                node.refCount++;
            }
        }
        return topSortHelper(nodes, queueSupplier);
    }

    /**
     * A pair of two objects.
     *
     * @param <U> the type of the first object
     * @param <V> the type of the second object
     */
    public static final class Pair<U, V> {
        /**
         * The first object in this pair.
         */
        public U first;
        /**
         * The second object in this pair.
         */
        public V second;

        /**
         * Constructs a new pair of the given objects.
         *
         * @param first  the first object
         * @param second the second object
         */
        public Pair(U first, V second) {
            this.first = first;
            this.second = second;
        }

        /**
         * Constructs a new pair as a clone of the given pair.
         *
         * @param other the pair to clone
         */
        public Pair(Pair<? extends U, ? extends V> other) {
            this(other.first, other.second);
        }

        @Override
        public String toString() {
            return String.format("<%s, %s>", first, second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof Pair)) {
                return false;
            } else {
                @SuppressWarnings("unchecked")
                Pair<U, V> other = (Pair<U, V>) obj;
                return Objects.equals(first, other.first) &&
                        Objects.equals(second, other.second);
            }
        }

        /**
         * Returns a lexicographic comparator for pairs.
         * The comparator compares pairs lexicographically using the given comparators
         * for the respective elements.
         *
         * @param compareFirst  the comparator to use for the first element
         * @param compareSecond the comparator to use for the second element
         * @param <U>           the type of the first element to compare
         * @param <V>           the type of the second element to compare
         * @return a lexicographic comparator for pairs
         */
        public static <U, V> Comparator<Pair<U, V>> lexicographicComparator(Comparator<? super U> compareFirst, Comparator<? super V> compareSecond) {
            return Comparator.comparing((Pair<U, V> pair) -> pair.first, compareFirst)
                    .thenComparing(pair -> pair.second, compareSecond);
        }

        /**
         * Returns a lexicographic comparator for pairs.
         * The comparator compares
         * pairs lexicographically using the elements'
         * respective natural ordering.
         *
         * @param <U> the type of the first element to compare
         * @param <V> the type of the second element to compare
         * @return a lexicographic comparator for pairs
         */
        public static <U extends Comparable<? super U>, V extends Comparable<? super V>> Comparator<Pair<U, V>> lexicographicComparator() {
            return lexicographicComparator(Comparator.naturalOrder(), Comparator.naturalOrder());
        }

        /**
         * Returns a pair by sorting the elements of the given source pair.
         * The elements are sorted
         * according to the given comparator.
         *
         * @param source     the pair to sort
         * @param comparator the comparator to use
         * @param <U>        the type of both elements in the pair to return
         * @param <V>        the type of both elements in the source pair
         * @return a new pair by sorting the elements of the given pair
         */
        public static <U, V extends U> Pair<U, U> sorted(Pair<V, V> source, Comparator<? super U> comparator) {
            if (comparator.compare(source.first, source.second) <= 0) {
                return new Pair<>(source.first, source.second);
            } else {
                return new Pair<>(source.second, source.first);
            }
        }

        /**
         * Returns a pair by sorting the element of the given source pair.
         * The elements are sorted
         * according to their natural order.
         *
         * @param source the pair to sort
         * @param <U>    the type of both elements in the pair to return
         * @param <V>    the type of both elements in the source pair
         * @return a new pair by sorting the elements of the given pair
         */
        public static <U extends Comparable<? super U>, V extends U> Pair<U, U> sorted(Pair<V, V> source) {
            return Pair.sorted(source, Comparator.naturalOrder());
        }

        /**
         * Applies the given function to the first element of this pair.
         *
         * @param function the function to apply
         * @param <W>      the return type of the function;
         *                 i.e. the type of the first element in the returned pair
         * @return a new pair consisting of {@code function} applied to the first element
         *         and the second element as is
         */
        public <W> Pair<W, V> mapFirst(Function<U, W> function) {
            return new Pair<>(function.apply(first), second);
        }

        /**
         * Applies the given function to the second element of this pair.
         *
         * @param function the function to apply
         * @param <W>      the return type of the function;
         *                 i.e. the type of the second element in the returned pair
         * @return a new pair consisting of {@code function} applied to the second element
         *         and the first element as is
         */
        public <W> Pair<U, W> mapSecond(Function<V, W> function) {
            return new Pair<>(first, function.apply(second));
        }

        /**
         * Applies the given function to both elements of the given pair.
         *
         * @param source   the pair to apply the function to
         * @param function the function to apply to both elements
         * @param <W>      the type of the argument to the function; must be a supertype of {@code U} and {@code V}
         * @param <Z>      the return type of the function and the type of both elements of the returned pair
         * @param <U>      the type of the first element of the source pair
         * @param <V>      the type of the second element of the source pair
         * @return a new pair consisting of {@code function} applied to both elements
         */
        public static <W, Z, U extends W, V extends W> Pair<Z, Z> mapBoth(Pair<U, V> source, Function<W, Z> function) {
            return new Pair<>(function.apply(source.first), function.apply(source.second));
        }

        /**
         * Returns a stream of the values in the given pair.
         *
         * @param source the pair to apply the function to
         * @param <W>    the type of the values in the stream; must be a supertype of {@code U} and {@code V}
         * @param <U>    the type of the first element of the pair
         * @param <V>    the type of the second element of the pair
         * @return a stream consisting of the values in the given pair in order
         */
        public static <W, U extends W, V extends W> Stream<W> stream(Pair<U, V> source) {
            return Stream.of(source.first, source.second);
        }
    }

}
