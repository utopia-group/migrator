package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An SQL {@code SELECT} query.
 */
public class SelectQuery implements IQuery, ISelectQuery {
    /**
     * The list of columns returned by this query.
     */
    public final List<IColumn> columns;
    /**
     * The root table to be selected from.
     */
    public final TableRef table;
    /**
     * A list of joins of tables in this query.
     */
    public final List<Join> joins;
    /**
     * The predicate of this query.
     * May be {@code null} if none is specified (i.e. select all rows).
     */
    public final IPredicate predicate; // may be null

    /**
     * Constructs a query that selects the given columns
     * from the given table joined using the given joins
     * that match the given predicate (or all rows if {@code null}).
     *
     * @param columns   the columns to be returned
     * @param table     the root table to select from
     * @param joins     the joins to be processed by this query
     * @param predicate a predicate that filters the returned rows,
     *                  or {@code null} if all rows are to be selected
     */
    public SelectQuery(List<IColumn> columns, TableRef table, List<Join> joins, IPredicate predicate) {
        if (columns.size() == 0) {
            throw new IllegalArgumentException("empty column list");
        }
        this.columns = columns;
        this.table = table;
        this.joins = joins;
        this.predicate = predicate;
    }

    @Override
    public <T> T accept(IQueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public TableRef getTable() {
        return table;
    }

    @Override
    public List<Join> getJoins() {
        return joins;
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(columns.stream().map(IColumn::toSqlString).collect(Collectors.joining(", ")));
        builder.append(" FROM ");
        builder.append(table.toSqlString());
        for (Join join : joins) {
            builder.append(" ");
            builder.append(join.toSqlString());
        }
        if (predicate != null) {
            builder.append(" WHERE ");
            builder.append(predicate.toSqlString());
        }
        builder.append(";");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("SelectQuery(%s, %s, %s, %s)", columns, table, joins, predicate);
    }
}
