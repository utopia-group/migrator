package migrator.rewrite.ast;

/**
 * A value, used in predicates, insertions, and updates.
 *
 * @see IValueVisitor
 */
public interface IValue extends IAstNode {
    /**
     * Accepts a visitor, dispatching to the correct
     * {@code visit} method.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the value returned by {@code visit}.
     * @see IValueVisitor#visit(ConstantValue)
     * @see IValueVisitor#visit(ParameterValue)
     * @see IValueVisitor#visit(ColumnValue)
     * @see IValueVisitor#visit(SubqueryValue)
     * @see IValueVisitor#visit(FreshValue)
     */
    <T> T accept(IValueVisitor<T> visitor);

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);
}
