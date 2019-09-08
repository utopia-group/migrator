package migrator.rewrite.ast;

/**
 * An SQL {@code DELETE} query.
 */
public class DeleteQuery implements IQuery {
    /**
     * The table to delete from
     */
    public final TableRef table;
    /**
     * The predicate of this query, or {@code null}
     * if not specified (i.e. delete all rows).
     */
    public final IPredicate predicate;

    /**
     * Constructs a {@code DELETE} query that deletes
     * the rows from the given table that match
     * the given predicate.
     *
     * @param table     the table to delete from
     * @param predicate the predicate to select rows to delete
     */
    public DeleteQuery(TableRef table, IPredicate predicate) {
        this.table = table;
        this.predicate = predicate;
    }

    @Override
    public <T> T accept(IQueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        builder.append(table.toSqlString());
        if (predicate != null) {
            builder.append(" WHERE ");
            builder.append(predicate.toSqlString());
        }
        builder.append(";");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("DeleteQuery(%s, %s)", table, predicate);
    }
}
