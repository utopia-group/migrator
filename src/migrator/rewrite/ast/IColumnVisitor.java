package migrator.rewrite.ast;

/**
 * A visitor that accepts columns.
 *
 * @param <T> the type of the return value of this operation
 * @see IColumn
 */
public interface IColumnVisitor<T> {

    /**
     * Visits a column reference node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IColumn#accept(IColumnVisitor)
     */
    T visit(ColumnRef node);

    /**
     * Visits a column hole node.
     *
     * @param node the node to visit
     * @return the value of this operation
     * @see IColumn#accept(IColumnVisitor)
     */
    T visit(ColumnHole node);

}
