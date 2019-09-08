package migrator.rewrite.ast;

/**
 * Interface for column references.
 */
public interface IColumn extends IAstNode {

    /**
     * Accepts a visitor, dispatching to the correct {@code visit} method.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the value returned by {@code visit}.
     * @see IColumnVisitor#visit(ColumnRef)
     * @see IColumnVisitor#visit(ColumnHole)
     */
    public <T> T accept(IColumnVisitor<T> visitor);

    @Override
    public String toSqlString();

}
