package migrator.rewrite.ast;

/**
 * A boolean predicate on a row.
 *
 * @see IPredVisitor
 */
public interface IPredicate extends IAstNode {
    /**
     * Precedence of top-level predicates
     * such as {@link ConstantValue}.
     *
     * @see #getPrecedence
     */
    static int PRECEDENCE_TOP = 40;
    /**
     * Precedence of {@link NotPred}.
     *
     * @see #getPrecedence
     */
    static int PRECEDENCE_NOT = 30;
    /**
     * Precedence of {@link AndPred}.
     *
     * @see #getPrecedence
     */
    static int PRECEDENCE_AND = 20;
    /**
     * Precedence of {@link OrPred}.
     *
     * @see #getPrecedence
     */
    static int PRECEDENCE_OR = 10;

    /**
     * Returns the precedence of this node,
     * used to insert parentheses if necessary.
     *
     * @return the precedence of this node
     * @see #PRECEDENCE_TOP
     * @see #PRECEDENCE_NOT
     * @see #PRECEDENCE_AND
     * @see #PRECEDENCE_OR
     */
    int getPrecedence();

    /**
     * Accepts a visitor, dispatching to the correct
     * {@code visit} method.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the value returned by {@code visit}.
     * @see IPredVisitor#visit(AndPred)
     * @see IPredVisitor#visit(OrPred)
     * @see IPredVisitor#visit(NotPred)
     * @see IPredVisitor#visit(OpPred)
     * @see IPredVisitor#visit(InPred)
     */
    <T> T accept(IPredVisitor<T> visitor);

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);
}
