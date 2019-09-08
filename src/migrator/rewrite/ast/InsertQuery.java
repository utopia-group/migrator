package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An SQL {@code INSERT} query.
 */
public class InsertQuery implements IQuery {
    /**
     * The table to insert into.
     */
    public final TableRef table;
    /**
     * A list of pairs representing the values
     * of the inserted row.
     */
    public final List<ColumnValuePair> pairs;

    /**
     * Constructs a query that inserts a row
     * with values given by the given pairs
     * into the given table.
     *
     * @param table the table to insert into
     * @param pairs the pairs representing the values of the inserted row
     */
    public InsertQuery(TableRef table, List<ColumnValuePair> pairs) {
        this.table = table;
        this.pairs = pairs;
    }

    @Override
    public <T> T accept(IQueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return String.format("INSERT INTO %s (%s) VALUES (%s);",
                table.toSqlString(),
                pairs.stream().map(pair -> pair.column.toSqlString()).collect(Collectors.joining(", ")),
                pairs.stream().map(pair -> pair.value.toSqlString()).collect(Collectors.joining(", ")));
    }

    @Override
    public String toString() {
        return String.format("InsertQuery(%s, %s)", table, pairs);
    }
}
