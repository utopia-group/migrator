package migrator.rewrite.ast;

/**
 * A visitor that accepts queries.
 *
 * @param <T> the type of the return value of this operation
 * @see IQuery
 */
public interface IQueryVisitor<T> {
    /**
     * Visits a {@code SELECT} query.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(SelectQuery query);

    /**
     * Visits an {@code INSERT} query.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(InsertQuery query);

    /**
     * Visits an {@code UPDATE} query.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(UpdateQuery query);

    /**
     * Visits a {@code DELETE} query.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(DeleteQuery query);

    /**
     * Visits a compound {@code INSERT} query.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(CompoundInsertQuery query);

    /**
     * Visits a {@code DELETE} query with joins.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(CompoundDeleteQuery query);

    /**
     * Visits an {@code UPDATE} query with joins.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(CompoundUpdateQuery query);

    /**
     * Visits a nondeterministic choice query.
     *
     * @param query the query to visit
     * @return the value of this operation
     * @see IQuery#accept(IQueryVisitor)
     */
    T visit(ChooseQuery query);
}
