package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An SQL {@code UPDATE} query.
 */
public class CompoundUpdateQuery implements IQuery, ISelectQuery {
    /**
     * The root table to be updated.
     */
    public final TableRef table;
    /**
     * The tables to join in this query.
     */
    public final List<Join> joins;
    /**
     * The pairs of values to be changed.
     */
    public final List<ColumnValuePair> pairs;
    /**
     * A predicate that specifies which rows
     * should be affected by this update.
     */
    public final IPredicate predicate;

    /**
     * Constructs a query which updates the rows
     * in the given table which match the given predicate
     * and sets the columns in the given list of pairs.
     *
     * @param table     the table to be updated
     * @param pairs     a list of columns to be updated along with their new values
     * @param predicate a predicate that specifies which rows should be updated
     */
    public CompoundUpdateQuery(TableRef table, List<Join> joins, List<ColumnValuePair> pairs,
            IPredicate predicate) {
        if (pairs.size() == 0) {
            throw new IllegalArgumentException("no column/value pairs in update");
        }
        this.table = table;
        this.joins = joins;
        this.pairs = pairs;
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
        builder.append("UPDATE ");
        builder.append(table.toSqlString());
        for (Join join : joins) {
            builder.append(" ");
            builder.append(join.toSqlString());
        }
        builder.append(" SET ");
        builder.append(pairs.stream().map(ColumnValuePair::toSqlString).collect(Collectors.joining(", ")));
        if (predicate != null) {
            builder.append(" WHERE ");
            builder.append(predicate.toSqlString());
        }
        builder.append(";");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("UpdateQuery(%s, %s, %s, %s)", table, joins, pairs, predicate);
    }
}
