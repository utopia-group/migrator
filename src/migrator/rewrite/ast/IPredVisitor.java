package migrator.rewrite.ast;

/**
 * A visitor that accepts predicates.
 *
 * @param <T> the type of the return value of this operation
 * @see IPredicate
 */
public interface IPredVisitor<T> {
    // logical operators
    /**
     * Visits an AND predicate node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IPredicate#accept(IPredVisitor)
     */
    T visit(AndPred node);

    /**
     * Visits an OR predicate node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IPredicate#accept(IPredVisitor)
     */
    T visit(OrPred node);

    /**
     * Visits a NOT predicate node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IPredicate#accept(IPredVisitor)
     */
    T visit(NotPred node);

    // relational operators
    /**
     * Visits a relational operation predicate node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IPredicate#accept(IPredVisitor)
     */
    T visit(OpPred node);

    /**
     * Visits an IN predicate node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IPredicate#accept(IPredVisitor)
     */
    T visit(InPred node);

}
