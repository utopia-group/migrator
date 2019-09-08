package migrator.rewrite.ast;

import java.util.List;

/**
 * An SQL subquery. May be used as a value or in an {@code IN} predicate.
 *
 * @see SubqueryValue
 * @see InPred
 * @see SelectQuery
 */
public class Subquery implements IAstNode, ISelectQuery {
    /**
     * The column to be used as the value returned by this subquery.
     */
    public final ColumnRef column;
    /**
     * The root table to select from.
     */
    public final TableRef table;
    /**
     * A list of joins of tables in this subquery.
     */
    public final List<Join> joins;
    /**
     * The predicate of this subquery.
     * May be {@code null} if none is specified (i.e. select all rows).
     */
    public final IPredicate predicate; // may be null

    /**
     * Constructs a subquery that selects the given column
     * from the given table joined using the given joins
     * that match the given predicate (or all rows if {@code null}).
     *
     * @param column    the column to be returned
     * @param table     the root table to select from
     * @param joins     the joins to be processed by this subquery
     * @param predicate a predicate that filters the returned rows,
     *                  or {@code null} if all rows are to be selected
     */
    public Subquery(ColumnRef column, TableRef table, List<Join> joins, IPredicate predicate) {
        this.column = column;
        this.table = table;
        this.joins = joins;
        this.predicate = predicate;
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
        builder.append("(");
        builder.append("SELECT ");
        builder.append(column.toSqlString());
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
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("Subquery(%s, %s, %s, %s)", column, table, joins, predicate);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((joins == null) ? 0 : joins.hashCode());
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Subquery))
            return false;
        Subquery other = (Subquery) obj;
        if (column == null) {
            if (other.column != null)
                return false;
        } else if (!column.equals(other.column))
            return false;
        if (joins == null) {
            if (other.joins != null)
                return false;
        } else if (!joins.equals(other.joins))
            return false;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        if (table == null) {
            if (other.table != null)
                return false;
        } else if (!table.equals(other.table))
            return false;
        return true;
    }
}
