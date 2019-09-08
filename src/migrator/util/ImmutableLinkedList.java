package migrator.util;

import java.util.AbstractSequentialList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An immutable singly-linked list.
 * Immutability allows two list which share a common "tail"
 * to use the same object for both.
 * <p>
 * For example, the list {@code [1, 2, 3, 4, 5]}
 * and {@code [8, 3, 4, 5]}
 * share the "tail" {@code [3, 4, 5]}.
 * If {@code tail} holds this tail, then
 * the lists {@code Cons(1, Cons(2, tail))} and {@code Cons(8, tail)}
 * correspond to the respective lists.
 * <p>
 * A list is represented as an inductive datatype with the following variants:
 * <ul>
 * <li>{@code Nil}, the empty list</li>
 * <li>{@code Cons(car, cdr)}, the list that begins with {@code car} and whose
 * remaining elements are {@code cdr}</li>
 * </ul>
 * <p>
 * Note that the iterator returned by {@link #listIterator}
 * has significant overhead to support moving backwards.
 * {@link #iterator} should be preferred when possible.
 *
 * @param <T> the type of object to store in the list
 */
public abstract class ImmutableLinkedList<T> extends AbstractSequentialList<T> {
    // two concrete subclasses
    @Override
    public abstract boolean isEmpty();

    /**
     * Retrieves the element at the head of this list.
     *
     * @return the first element of this list
     */
    public abstract T car();

    /**
     * Retrieves the elements of this list after the first.
     *
     * @return the second and following elements of this list
     */
    public abstract ImmutableLinkedList<T> cdr();

    /**
     * Constructs a new empty list.
     *
     * @param <T> the type of the elements in the returned list
     * @return a new empty list
     */
    public static <T> ImmutableLinkedList<T> empty() {
        return new Nil<>();
    }

    /**
     * Constructs a new list containing {@code car} at the head
     * and the rest of the elements being {@code cdr}.
     *
     * @param car the first element of the new list
     * @param cdr the elements following the first element in the new list
     * @param <T> the type of the elements in the returned list
     * @return the constructed list
     */
    public static <T> ImmutableLinkedList<T> cons(T car, ImmutableLinkedList<T> cdr) {
        return new Cons<>(car, cdr);
    }

    /**
     * Constructs a new list by prepending the given element.
     *
     * @param car the element to prepend
     * @return a new list {@code ret} satisfying
     *         {@code ret.car() == car && ret.cdr() == this }
     */
    public ImmutableLinkedList<T> prepend(T car) {
        return cons(car, this);
    }

    /**
     * Constructs a new list containing only the given element.
     * <p>
     * Note: this is the {@code return} function and as such
     * fulfills the functional interface {@link KleisliArrow}.
     *
     * @param object the object to include in the list
     * @param <T>    the type of the element to include
     * @return the constructed list
     */
    public static <T> ImmutableLinkedList<T> singleton(T object) {
        return cons(object, empty());
    }

    /**
     * Constructs a new list from the given list.
     *
     * @param list the list containing the elements to be contained in this list
     * @param <T>  the type of the elements in the list
     * @return a new list containing the elements of the given list in order
     */
    public static <T> ImmutableLinkedList<T> fromList(List<? extends T> list) {
        java.util.ListIterator<? extends T> it = list.listIterator(list.size());
        ImmutableLinkedList<T> ret = empty();
        while (it.hasPrevious()) {
            T el = it.previous();
            ret = cons(el, ret);
        }
        return ret;
    }

    /**
     * Returns a new list by applying the given function to each element of this
     * list.
     *
     * @param function the function to apply
     * @param <U>      the type of the elements in the returned list
     * @return a new list mapping the given function to each element
     */
    public <U> ImmutableLinkedList<U> map(Function<T, ? extends U> function) {
        if (isEmpty()) {
            return empty();
        }
        return cons(function.apply(car()), cdr().map(function));
    }

    /**
     * Monadic bind. Equivalent to {@link #join join} composed with {@link #map
     * map}.
     *
     * @param function the function to apply
     * @param <U>      the type of the elements in the returned list
     * @return a new list formed by joining the results of applying the given
     *         function to each element in this list
     */
    public <U> ImmutableLinkedList<U> bind(KleisliArrow<T, U> function) {
        if (isEmpty()) {
            return empty();
        }
        return function.apply(car()).append(cdr().bind(function));
    }

    /**
     * Monadic {@code join}. Flattens a list.
     * <p>
     * For example, {@code join} applied to
     * the list {@code [[1, 2, 3], [4, 5]]}
     * produces {@code [1, 2, 3, 4, 5]} as a result.
     *
     * @param list the list to flatten
     * @param <T>  the type of the elements in the flattened list
     * @return the given list with all elements appended together
     */
    public static <T> ImmutableLinkedList<T> join(ImmutableLinkedList<ImmutableLinkedList<T>> list) {
        if (list.isEmpty()) {
            return empty();
        }
        return list.car().append(join(list.cdr()));
    }

    /**
     * Appends the given list after this list.
     *
     * @param after the list to append
     * @return a new list consisting of
     *         the elements of this list
     *         followed by the elements of the given list
     */
    public ImmutableLinkedList<T> append(ImmutableLinkedList<T> after) {
        if (this.isEmpty())
            return after;
        return cons(car(), cdr().append(after));
    }

    /**
     * Performs a right fold.
     *
     * @param operation the operation that folds a value from the list into the
     *                  accumulator
     * @param initial   the initial value of the accumulator
     * @param <U>       the type of the accumulator
     * @return the final value of the accumulator
     */
    public <U> U foldr(BiFunction<T, U, U> operation, U initial) {
        if (this.isEmpty())
            return initial;
        return operation.apply(car(), cdr().foldr(operation, initial));
    }

    /**
     * Performs a left fold.
     *
     * @param operation the operation that folds a value from the list into the
     *                  accumulator
     * @param initial   the initial value of the accumulator
     * @param <U>       the type of the accumulator
     * @return the final value of the accumulator
     */
    public <U> U foldl(BiFunction<U, T, U> operation, U initial) {
        ImmutableLinkedList<T> cur = this;
        U acc = initial;
        while (!cur.isEmpty()) {
            acc = operation.apply(acc, cur.car());
            cur = cur.cdr();
        }
        return acc;
    }

    @Override
    public java.util.ListIterator<T> listIterator(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return new ListIterator<>(this);
    }

    @Override
    public Iterator<T> iterator() {
        return new SingleDirectionIterator<>(this);
    }

    private static class SingleDirectionIterator<T> implements Iterator<T> {
        private ImmutableLinkedList<T> list;

        public SingleDirectionIterator(ImmutableLinkedList<T> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public T next() {
            T ret = list.car();
            list = list.cdr();
            return ret;
        }
    }

    private static class ListIterator<T> implements java.util.ListIterator<T> {
        // holds stack of previous elements; top is to be returned by next()
        private Deque<ImmutableLinkedList<T>> stack;

        public ListIterator(ImmutableLinkedList<T> list) {
            stack = new ArrayDeque<>();
            stack.push(list);
        }

        @Override
        public void add(T e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return !stack.peek().isEmpty();
        }

        @Override
        public boolean hasPrevious() {
            return stack.size() > 1;
        }

        @Override
        public T next() {
            ImmutableLinkedList<T> list = stack.peek();
            if (list.isEmpty()) {
                throw new NoSuchElementException();
            }
            stack.push(list.cdr());
            return list.car();
        }

        @Override
        public int nextIndex() {
            return stack.size();
        }

        @Override
        public T previous() {
            if (stack.size() == 1) {
                throw new NoSuchElementException();
            }
            stack.pop();
            return stack.peek().car();
        }

        @Override
        public int previousIndex() {
            return stack.size() - 1;
        }
    }

    private static class Nil<T> extends ImmutableLinkedList<T> {
        public Nil() {}

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public T car() {
            throw new NoSuchElementException();
        }

        @Override
        public ImmutableLinkedList<T> cdr() {
            throw new NoSuchElementException();
        }
    }

    private static class Cons<T> extends ImmutableLinkedList<T> {
        private T _car;
        private ImmutableLinkedList<T> _cdr;

        public Cons(T car, ImmutableLinkedList<T> cdr) {
            this._car = car;
            this._cdr = cdr;
        }

        @Override
        public int size() {
            return 1 + _cdr.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public T car() {
            return _car;
        }

        @Override
        public ImmutableLinkedList<T> cdr() {
            return _cdr;
        }
    }

    /**
     * Kleisli arrow {@code a -> m b},
     * where {@code m} is {@link ImmutableLinkedList}.
     * <p>
     * Note: {@code return} ({@code a -> m a}) is given by
     * {@link #singleton}.
     *
     * @param <A> the type of the input
     * @param <B> the type of the output
     */
    @FunctionalInterface
    public static interface KleisliArrow<A, B> {
        /**
         * Applies this function to the given argument.
         *
         * @param a the argument to the function
         * @return the result of this function
         */
        ImmutableLinkedList<B> apply(A a);

        /**
         * Composes two Kleisli arrows (i.e. the "left fish".)
         *
         * @param after  the function to apply afterwards
         * @param before the function to apply first
         * @param <A>    the type of the input
         * @param <B>    the type of the intermediate value
         * @param <C>    the type of the output
         * @return {@code after <=< before}
         */
        static <A, B, C> KleisliArrow<A, C> compose(KleisliArrow<B, C> after, KleisliArrow<A, B> before) {
            return a -> before.apply(a).bind(after);
        }
    }
}
