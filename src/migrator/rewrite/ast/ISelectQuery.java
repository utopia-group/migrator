package migrator.rewrite.ast;

import java.util.List;

/**
 * A node that behaves like a {@code SELECT} query,
 * including {@link SelectQuery} and {@link Subquery}.
 */
public interface ISelectQuery {
    /**
     * Returns the root table of this query.
     *
     * @return the root table
     */
    TableRef getTable();

    /**
     * Returns the joins in this query.
     *
     * @return the joins
     */
    List<Join> getJoins();
}
