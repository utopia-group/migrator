package migrator.rewrite.spanning;

import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A doubly-linked list with raw access to individual nodes.
 * <p>
 * Each node is linked to the next and previous nodes,
 * with a special "header" node at the front of the list.
 * The next field of the header node points to the first element in the list,
 * and the previous field of the header points to the last element in the list.
 * If the list is empty, the header node points to itself.
 * <p>
 * The methods of this class obey the following invariants
 * (where {@code l}[0] refers to the header,
 * {@code l}[1] to the first element, and so on):
 * <ul>
 * <li>&forall; a, b. a.{@code next} == b iff b.{@code prev} == a</li>
 * <li>&forall; a. a.{@code next} != {@code null}</li>
 * <li>&forall; a. a.{@code prev} != {@code null}</li>
 * <li>&forall; n &isin; <b>Z</b>. {@code l}[n].{@code isHeader()}
 * iff ({@code cachedSize} | n)</li>
 * </ul>
 *
 * @param <T> the type of element stored in this list
 */
public final class RawLinkedList<T> extends AbstractSequentialList<T> {
    /**
     * A node in a linked list. Contains data and next/prev pointers.
     * May be moved between lists by splicing.
     *
     * @param <T> the type of element stored in this list
     */
    public static final class Node<T> {
        /**
         * The data stored in this node.
         */
        public T element;
        /**
         * A pointer to the next node in this list,
         * or the header if this is the last node.
         */
        public Node<T> next;
        /**
         * A pointer to the previous node in this list,
         * or the header if this is the first node.
         */
        public Node<T> prev;

        /**
         * Constructs a new header node.
         * Both {@code next} and {@code prev} point to itself.
         */
        private Node() {
            element = null;
            next = this;
            prev = this;
        }

        /**
         * Constructs a new node with the given element.
         * Both {@code next} and {@code prev} are initially
         * {@code null} and should be filled in.
         *
         * @param element the element stored in this node
         */
        private Node(T element) {
            if (element == null) {
                throw new NullPointerException();
            }
            this.element = element;
            next = null;
            prev = null;
        }

        /**
         * Constructs a new header node.
         *
         * @param <T> the type of the element in the list
         *            to contain the created node
         * @return the constructed header node
         */
        private static <T> Node<T> header() {
            return new Node<T>();
        }

        /**
         * Returns whether or not this node is a header node.
         *
         * @return {@code true} if this node is a header node
         */
        public boolean isHeader() {
            return element == null;
        }

        /**
         * Adds a new node containing the given element
         * before this node.
         *
         * @param element the element in the node to be added
         * @return the added node
         */
        private Node<T> addBefore(T element) {
            Node<T> node = new Node<>(element);
            Node<T> prev = this.prev;
            // prev <-> node <-> this
            prev.next = node;
            node.prev = prev;
            node.next = this;
            this.prev = node;
            return node;
        }

        /**
         * Adds a new node containing the given element
         * after this node.
         *
         * @param element the element in the node to be added
         * @return the added node
         */
        private Node<T> addAfter(T element) {
            Node<T> node = new Node<>(element);
            Node<T> next = this.next;
            // this <-> node <-> next
            this.next = node;
            node.prev = this;
            node.next = next;
            next.prev = node;
            return node;
        }

        /**
         * Removes this node from the list.
         *
         * @return the node in this node's place,
         *         which is the node originally following this node.
         */
        private Node<T> removeSelf() {
            Node<T> prev = this.prev;
            Node<T> next = this.next;
            // prev <-> next
            prev.next = next;
            next.prev = prev;
            this.next = this;
            this.prev = this;
            return next;
        }

        /**
         * Computes the distance to the given node, i.e.
         * the smallest nonnegative <i>n</i> such that
         * {@code this}({@code .next})<sup><i>n</i></sup> == {@code end}
         *
         * @param end the node to compute the distance to
         * @return the distance from this node to the given node
         */
        public int distanceTo(Node<T> end) {
            int dist = 0;
            for (Node<T> cur = this; cur != end; cur = cur.next, dist++) {
                assert dist == 0 || cur != this; // loop -> not in list
            }
            return dist;
        }
    }

    /**
     * A bidirectional iterator for a linked list.
     * All operations are permitted.
     *
     * @param <T> the type of element stored in the list
     */
    private static final class Iterator<T> implements ListIterator<T> {
        /**
         * The linked list over which we are iterating.
         */
        private RawLinkedList<T> container;
        /**
         * The next node to visit;
         * the data will be returned by {@link #next()}.
         */
        private Node<T> next;
        /**
         * The last visited node;
         * the node containing the element returned by
         * the last call to {@link #previous()} or {@link #next()}.
         */
        private Node<T> lastVisited;
        /**
         * The index of {@link #next}.
         */
        private int index;

        /**
         * Constructs a new iterator over the given linked list,
         * starting at the given node and index.
         *
         * @param container the linked list over which to iterate
         * @param next      the next node to visit
         * @param index     the index of the given ndoe
         */
        public Iterator(RawLinkedList<T> container, Node<T> next, int index) {
            this.container = container;
            this.next = next;
            this.index = index;
            lastVisited = null;
        }

        /**
         * Asserts that the state of this iterator
         * appears to be valid.
         */
        private void assertValid() {
            assert index >= 0 && index <= container.cachedSize &&
                    (index == 0) == next.prev.isHeader() &&
                    (index == container.cachedSize) == next.isHeader();
        }

        @Override
        public void add(T element) {
            next = next.addBefore(element);
            index++;
            container.cachedSize++;
            lastVisited = null;
        }

        @Override
        public boolean hasNext() {
            assertValid();
            return index != container.cachedSize;
        }

        @Override
        public boolean hasPrevious() {
            assertValid();
            return index != 0;
        }

        @Override
        public T next() {
            assertValid();
            if (next.isHeader()) {
                throw new NoSuchElementException();
            }
            lastVisited = next;
            T ret = next.element;
            next = next.next;
            index++;
            return ret;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public T previous() {
            assertValid();
            if (next.prev.isHeader()) {
                throw new NoSuchElementException();
            }
            next = next.prev;
            lastVisited = next;
            index--;
            return next.element;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            assertValid();
            if (lastVisited == null) {
                throw new NoSuchElementException();
            }
            if (lastVisited != next) {
                // from next
                assert lastVisited == next.prev;
                index--;
            }
            next = lastVisited.removeSelf();
            lastVisited = null;
        }

        @Override
        public void set(T e) {
            assertValid();
            if (lastVisited == null) {
                throw new NoSuchElementException();
            }
            assert !lastVisited.isHeader();
            lastVisited.element = e;
        }
    }

    /**
     * The header node in this list.
     * This will not be changed for the lifetime of this list,
     * so past-the-end iterators will not be invalidated.
     */
    private Node<T> header;
    /**
     * The size of this list, as computing it is
     * an <i>O</i>(<i>n</i>) operation.
     */
    private int cachedSize;

    /**
     * Constructs a new empty linked list.
     */
    public RawLinkedList() {
        cachedSize = 0;
        header = Node.header();
    }

    @Override
    public int size() {
        return cachedSize;
    }

    /**
     * Calculates the size of this list.
     * Should be equal to {@link #size()}.
     *
     * @return the calculated size
     */
    public int calculateSize() {
        int calculatedSize = header.next.distanceTo(header);
        assert cachedSize == calculatedSize;
        return calculatedSize;
    }

    @Override
    public Iterator<T> listIterator(int index) {
        Node<T> node;
        if (index < 0 || index > cachedSize) {
            throw new IndexOutOfBoundsException();
        }
        if (index < cachedSize / 2) {
            int i;
            for (node = header.next, i = 0; i < index; i++, node = node.next)
                ;
        } else {
            int i;
            for (node = header, i = cachedSize; i > index; i--, node = node.prev)
                ;
        }
        return new Iterator<T>(this, node, index);
    }

    /**
     * Returns a reference to the node at the beginning of this list.
     *
     * @return the node at the beginning of this list
     */
    public Node<T> begin() {
        return header.next;
    }

    /**
     * Returns a reference to the past-the-end node of this list; i.e. the header.
     *
     * @return the header of this list
     */
    public Node<T> end() {
        return header;
    }

    /**
     * Adds the given element before the given node.
     *
     * @param node    the node before which to insert the given element
     * @param element the element to insert
     * @return the node containing the inserted element
     */
    public Node<T> addBefore(Node<T> node, T element) {
        cachedSize++;
        return node.addBefore(element);
    }

    /**
     * Adds the given element after the given node.
     *
     * @param node    the node after which to insert the given element
     * @param element the element to insert
     * @return the node containing the inserted element
     */
    public Node<T> addAfter(Node<T> node, T element) {
        cachedSize++;
        return node.addAfter(element);
    }

    /**
     * Removes the given node from this list.
     *
     * @param node the node to remove
     * @return the node that replaced the removed node,
     *         which is the node that originally followed the node
     */
    public Node<T> removeNode(Node<T> node) {
        if (node.isHeader()) {
            throw new IndexOutOfBoundsException();
        }
        cachedSize--;
        return node.removeSelf();
    }

    /**
     * Adds the given element to the front of the list.
     *
     * @param element the element to insert
     * @return the node containing the inserted element
     */
    public Node<T> pushFront(T element) {
        return addAfter(header, element);
    }

    /**
     * Adds the given element to the back of the list.
     *
     * @param element the element to insert
     * @return the node containing the inserted element
     */
    public Node<T> pushBack(T element) {
        return addBefore(header, element);
    }

    /**
     * Removes and returns the front of this list.
     *
     * @return the element at the front of this list
     */
    public T removeFront() {
        Node<T> node = header.next;
        removeNode(node);
        return node.element;
    }

    /**
     * Removes and returns the back of this list.
     *
     * @return the element at the back of this list
     */
    public T removeBack() {
        Node<T> node = header.prev;
        removeNode(node);
        return node.element;
    }

    /**
     * Splices out the sub-list starting at begin and ending at end.
     * Begin is inclusive and end is exclusive.
     * This method is not guaranteed to check if the given length is correct,
     * so this method could potentially violate the invariants.
     *
     * @param begin  the start of the sub-list to splice
     * @param end    the end of the sub-list to splice
     * @param length the length of the sub-list to splice
     * @return the first node of the spliced list, linked circularly,
     *         or {@code null} if length is zero.
     */
    public Node<T> unsafeSpliceOut(Node<T> begin, Node<T> end, int length) {
        assert (length == 0) == (begin == end);
        if (length == 0) {
            return null;
        }
        cachedSize -= length;
        Node<T> last = end.prev;
        Node<T> prev = begin.prev;
        // prev <-> end
        prev.next = end;
        end.prev = prev;
        // last <-> begin ... last
        last.next = begin;
        begin.prev = last;
        return begin;
    }

    /**
     * Splices out all nodes in this list.
     *
     * @return the first node of the spliced list, or {@code null} if empty
     */
    public Node<T> spliceOutAll() {
        return unsafeSpliceOut(header.next, header, cachedSize);
    }

    /**
     * Splices in the given splice before the given node.
     * This method is not guaranteed to check if the provided length
     * is correct, so this method could potentially violate the invariants.
     *
     * @param splice the splice to insert
     * @param node   the node before which to insert the splice
     * @param length the length of the splice to insert
     * @return the node following the splice, i.e. the given node
     */
    public Node<T> unsafeSpliceInBefore(Node<T> splice, Node<T> node, int length) {
        if (splice == null) {
            return node;
        }
        cachedSize += length;
        Node<T> prev = node.prev;
        Node<T> last = splice.prev;
        // prev <-> splice ... last <-> node
        prev.next = splice;
        splice.prev = prev;
        last.next = node;
        node.prev = last;
        return node;
    }

    /**
     * Splices in the given splice after the given node.
     * This method is not guaranteed to check if the provided length
     * is correct, so this method could potentially violate the invariants.
     *
     * @param splice the splice to insert
     * @param node   the node after which to insert the splice
     * @param length the length of the splice to insert
     * @return the node preceding the splice, i.e. the given node
     */
    public Node<T> unsafeSpliceInAfter(Node<T> splice, Node<T> node, int length) {
        unsafeSpliceInBefore(splice, node.next, length);
        return node;
    }

    /**
     * Splices the entirety of the given list before the given node.
     * This method is safe because the size of the splice is known.
     *
     * @param list the list to splice in
     * @param node the node before which to splice in the list
     * @return the node following the splice, i.e. the given node
     */
    public Node<T> spliceAllInBefore(RawLinkedList<T> list, Node<T> node) {
        if (list.cachedSize == 0) {
            return node;
        }
        int length = list.cachedSize;
        list.cachedSize = 0;
        Node<T> splice = list.header.removeSelf();
        return unsafeSpliceInBefore(splice, node, length);
    }

    /**
     * Splices the entirety of the given list after the given node.
     * This method is safe because the size of the splice is known.
     *
     * @param list the list to splice in
     * @param node the node after which to splice in the list
     * @return the node preceding the splice, i.e. the given node
     */
    public Node<T> spliceAllInAfter(RawLinkedList<T> list, Node<T> node) {
        return spliceAllInBefore(list, node.next);
    }
}
