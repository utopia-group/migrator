package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a hole for columns.
 */
public final class ColumnHole implements IColumn {
    /**
     * Candidates that can instantiate this hole.
     */
    public final List<ColumnRef> candidates;

    /**
     * Create a hole for columns.
     *
     * @param candidates candidates that can instantiate this hole
     */
    public ColumnHole(List<ColumnRef> candidates) {
        this.candidates = candidates;
    }

    @Override
    public <T> T accept(IColumnVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("?? {");
        builder.append(candidates.stream()
                .map(ColumnRef::toSqlString)
                .collect(Collectors.joining(", ")));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("ColumnHole(%s)", candidates.stream()
                .map(ColumnRef::toSqlString)
                .collect(Collectors.joining(", ")));
    }

    // NOTE: use the default hashcode() and equals(Object obj) function
    // because column holes at different location may be instantiated with different columns
}
