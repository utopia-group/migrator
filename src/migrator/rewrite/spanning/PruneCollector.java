package migrator.rewrite.spanning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Consumer that collects trees after pruning them.
 * <p>
 * The pruning algorithm successively removes leaves
 * that do not satisfy a given predicate to produce
 * a tree for which all leaves meet the predicate
 * and for which all nodes that do satisfy the predicate
 * are contained.
 *
 * @param <V> the type of the vertex data
 * @param <E> the type of the edge data
 */
public final class PruneCollector<V, E> implements Consumer<List<Edge<V, E>>> {
    /**
     * Predicate used to determine which leaves to keep.
     */
    private Predicate<Vertex<V, E>> shouldKeep;
    /**
     * Set of collected trees.
     */
    private Set<Set<Edge<V, E>>> trees;
    /**
     * The last tree visited; used to rule out trees
     * in which the core pruned tree is the same.
     */
    private Set<Edge<V, E>> lastEdges;

    /**
     * Constructs a new prune collector using the given predicate.
     *
     * @param shouldKeep the predicate to determine which leaves to keep
     */
    public PruneCollector(Predicate<Vertex<V, E>> shouldKeep) {
        this.shouldKeep = shouldKeep;
        lastEdges = null;
        trees = new HashSet<>();
    }

    /**
     * Returns the collected trees.
     *
     * @return the set of collected trees, represented as a set of edges
     */
    public Set<Set<Edge<V, E>>> getTrees() {
        return trees;
    }

    @Override
    public void accept(List<Edge<V, E>> tree) {
        if (lastEdges != null && tree.stream().filter(lastEdges::contains).count() == lastEdges.size()) {
            // will reduce to same tree
            return;
        }
        // root tree
        RootedTree<V, E> root;
        Map<Vertex<V, E>, RootedTree<V, E>> vertices = new IdentityHashMap<>();
        Function<Vertex<V, E>, RootedTree<V, E>> getTreeFor = vertex -> vertices.computeIfAbsent(vertex,
                v -> new RootedTree<>(shouldKeep.test(v)));
        for (Edge<V, E> edge : tree) {
            Vertex<V, E> v1, v2;
            v1 = edge.mate.originalTip;
            v2 = edge.originalTip;
            RootedTree<V, E> t1, t2;
            t1 = getTreeFor.apply(v1);
            t2 = getTreeFor.apply(v2);
            t1.edges.add(edge);
            t2.edges.add(edge.mate);
        }
        // dfs to find children
        try {
            root = vertices.values().stream().filter(rt -> rt.shouldKeep).findFirst().get();
        } catch (NoSuchElementException e) {
            lastEdges = Collections.emptySet();
            trees.add(lastEdges);
            return;
        }
        Set<Edge<V, E>> pruned = Collections.newSetFromMap(new IdentityHashMap<>());
        root.prune(pruned, vertices, null, null);
        trees.add(pruned);
        lastEdges = pruned;
    }

    /**
     * Auxiliary class used to store the tree structure.
     *
     * @param <V> the type of vertex data
     * @param <E> the type of element data
     */
    private static final class RootedTree<V, E> {
        /**
         * List of edges adjacent to the root of this subtree.
         */
        public List<Edge<V, E>> edges;
        /**
         * Whether this tree should be kept if it has no children
         * (i.e. is a leaf).
         */
        public boolean shouldKeep;

        /**
         * Constructs a new empty subtree.
         *
         * @param shouldKeep whether this tree should be kept if it has no children
         */
        public RootedTree(boolean shouldKeep) {
            this.shouldKeep = shouldKeep;
            edges = new ArrayList<>();
        }

        /**
         * Prunes this tree. Should be called initially
         * where the root is a kept node.
         *
         * @param edgeSet  the set of edges to keep (holds result)
         * @param vertices a map from graph vertex to {@link RootedTree} objects
         * @param parent   the parent of this node
         * @param incoming the incoming edge; from {@code parent} to {@code this}
         * @return whether this node was ultimately kept
         */
        public boolean prune(Set<Edge<V, E>> edgeSet, Map<Vertex<V, E>, RootedTree<V, E>> vertices, RootedTree<V, E> parent, Edge<V, E> incoming) {
            boolean isLeaf = true;
            // a rooted (sub-)tree is a leaf iff it has no children
            for (Edge<V, E> edge : edges) {
                RootedTree<V, E> neighbor = vertices.get(edge.originalTip);
                if (neighbor == parent) {
                    continue;
                }
                isLeaf &= !neighbor.prune(edgeSet, vertices, this, edge);
            }
            if (!isLeaf || shouldKeep) {
                if (incoming != null) {
                    edgeSet.add(incoming);
                }
                return true;
            }
            return false;
        }
    }
}
