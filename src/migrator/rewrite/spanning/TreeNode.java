package migrator.rewrite.spanning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Data structure for nodes in a rooted tree, obtained from an unrooted tree.
 *
 * @param <V> type of the data stored in the vertices of the unrooted tree
 * @param <E> type of the data stored in the edges of the unrooted tree
 */
public final class TreeNode<V, E> {
    /**
     * Corresponding vertex in the unrooted tree.
     */
    public final Vertex<V, E> vertex;
    /**
     * Children nodes.
     */
    public final List<TreeNode<V, E>> children;
    /**
     * Edge data, corresponding to those in the unrooted tree.
     * edgeData.get(i) is the edge data between vertex and children.get(i).
     */
    public final List<E> edgeData;

    public TreeNode(Vertex<V, E> vertex) {
        this.vertex = vertex;
        this.children = new ArrayList<>();
        this.edgeData = new ArrayList<>();
    }

    /**
     * Build a rooted tree from an unrooted tree and return the root. This function cannot handle
     * the case that there is only one node in the tree.
     *
     * @param <V>   type of the data stored in the vertices of the unrooted tree
     * @param <E>   type of the data stored in the edges of the unrooted tree
     * @param edges an unrooted tree, represented as a collection of edges
     * @return the root of the rooted tree
     */
    public static <V, E> TreeNode<V, E> buildRootedTree(Collection<Edge<V, E>> edges) {
        if (edges == null || edges.isEmpty()) {
            throw new IllegalArgumentException("Empty unrooted tree");
        }
        // find a node with maximum degree
        TreeNode<V, E> root = new TreeNode<>(findVertexWithMaximumDegree(edges));
        // BFS
        Set<Vertex<V, E>> visited = new HashSet<>();
        Queue<TreeNode<V, E>> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode<V, E> node = queue.poll();
            if (!visited.contains(node.vertex)) {
                visited.add(node.vertex);
                for (Edge<V, E> edge : edges) {
                    TreeNode<V, E> child = null;
                    if (edge.originalTip.equals(node.vertex)) {
                        child = new TreeNode<>(edge.mate.originalTip);
                    } else if (edge.mate.originalTip.equals(node.vertex)) {
                        child = new TreeNode<>(edge.originalTip);
                    }
                    if (child != null && !visited.contains(child.vertex)) {
                        node.children.add(child);
                        node.edgeData.add(edge.data);
                        if (!visited.contains(child.vertex)) {
                            queue.offer(child);
                        }
                    }
                }
            }
        }
        return root;
    }

    private static <V, E> Vertex<V, E> findVertexWithMaximumDegree(Collection<Edge<V, E>> edges) {
        int maxDegree = -1;
        Vertex<V, E> maxDegreeVertex = null;
        for (Edge<V, E> edge : edges) {
            if (edge.originalTip.degree() > maxDegree) {
                maxDegree = edge.originalTip.degree();
                maxDegreeVertex = edge.originalTip;
            }
            if (edge.mate.originalTip.degree() > maxDegree) {
                maxDegree = edge.mate.originalTip.degree();
                maxDegreeVertex = edge.mate.originalTip;
            }
        }
        assert maxDegreeVertex != null;
        return maxDegreeVertex;
    }

    @Override
    public String toString() {
        return String.format("TreeNode(%s)", vertex);
    }
}
