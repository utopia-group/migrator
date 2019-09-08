package migrator.rewrite.ast;

/**
 * An SQL query (e.g.&nbsp;{@code SELECT}, {@code INSERT}, ...)
 *
 * @see IQueryVisitor
 */
public interface IQuery extends IAstNode {
    /**
     * Accepts a visitor, dispatching to the correct
     * {@code visit} method.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the value returned by {@code visit}.
     * @see IQueryVisitor#visit(SelectQuery)
     * @see IQueryVisitor#visit(InsertQuery)
     * @see IQueryVisitor#visit(UpdateQuery)
     * @see IQueryVisitor#visit(DeleteQuery)
     * @see IQueryVisitor#visit(CompoundInsertQuery)
     * @see IQueryVisitor#visit(CompoundDeleteQuery)
     * @see IQueryVisitor#visit(CompoundUpdateQuery)
     * @see IQueryVisitor#visit(ChooseQuery)
     */
    <T> T accept(IQueryVisitor<T> visitor);
}
