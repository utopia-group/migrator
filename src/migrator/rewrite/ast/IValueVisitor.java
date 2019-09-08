package migrator.rewrite.ast;

/**
 * A visitor that accepts values.
 *
 * @param <T> the type of the return value of this operation
 * @see IValue
 */
public interface IValueVisitor<T> {
    /**
     * Visits a constant value node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IValue#accept(IValueVisitor)
     */
    T visit(ConstantValue node);

    /**
     * Visits a parameter value node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IValue#accept(IValueVisitor)
     */
    T visit(ParameterValue node);

    /**
     * Visits a column value node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IValue#accept(IValueVisitor)
     */
    T visit(ColumnValue node);

    /**
     * Visits a subquery value node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IValue#accept(IValueVisitor)
     */
    T visit(SubqueryValue node);

    /**
     * Visits a fresh value node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IValue#accept(IValueVisitor)
     */
    T visit(FreshValue node);

}
