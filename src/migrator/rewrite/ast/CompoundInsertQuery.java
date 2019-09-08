package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a group of related insertions.
 *
 * @see InsertQuery
 */
public class CompoundInsertQuery implements IQuery {
    /**
     * The list of insertions grouped together.
     */
    public final List<InsertQuery> insertions;

    /**
     * Constructs a compound insertion consisting
     * of the given queries.
     *
     * @param queries the insertions to be grouped
     */
    public CompoundInsertQuery(List<InsertQuery> queries) {
        this.insertions = queries;
    }

    @Override
    public <T> T accept(IQueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT ");
        for (InsertQuery insertion : insertions) {
            builder.append(String.format("INTO %s (%s) VALUES (%s)",
                    insertion.table.toSqlString(),
                    insertion.pairs.stream().map(pair -> pair.column.toSqlString()).collect(Collectors.joining(", ")),
                    insertion.pairs.stream().map(pair -> pair.value.toSqlString()).collect(Collectors.joining(", "))));
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(";");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("CompoundInsertQuery(%s)", insertions);
    }
}
