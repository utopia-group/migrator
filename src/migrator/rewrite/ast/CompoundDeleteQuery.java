package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An SQL {@code DELETE} query with joins.
 *
 * Note: this class implements {@link ISelectQuery}
 * because it is structurally similar.
 */
public final class CompoundDeleteQuery implements IQuery, ISelectQuery {
    /**
     * The root table to delete from.
     */
    public final TableRef table;
    /**
     * The tables to join in this query.
     */
    public final List<Join> joins;
    /**
     * The predicate of this query, or {@code null}
     * if not specified (i.e. delete all rows).
     */
    public final IPredicate predicate;
    /**
     * The (references to) tables to delete.
     * {@code null} means that it is a hole.
     */
    public final List<String> deletedTables;

    /**
     * Construct a compound delete query with unknown table list from the given
     * table and joins with the given predicate.
     *
     * @param table     the root table of this delete
     * @param joins     the joins of this delete
     * @param predicate the predicate of this delete
     */
    public CompoundDeleteQuery(TableRef table, List<Join> joins, IPredicate predicate) {
        this(table, joins, predicate, null);
    }

    /**
     * Construct a compound delete query from the given table
     * and joins with the given predicate.
     *
     * @param table         the root table of this delete
     * @param joins         the joins of this delete
     * @param predicate     the predicate of this delete
     * @param deletedTables the table list to delete from
     */
    public CompoundDeleteQuery(TableRef table, List<Join> joins, IPredicate predicate, List<String> deletedTables) {
        this.table = table;
        this.joins = joins;
        this.predicate = predicate;
        this.deletedTables = deletedTables;
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
        builder.append("DELETE ");
        if (deletedTables == null) {
            builder.append("??");
        } else {
            builder.append(deletedTables.stream().collect(Collectors.joining(", ")));
        }
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
        return String.format("CompoundDeleteQuery(%s, %s, %s)", table, joins, predicate);
    }
}
