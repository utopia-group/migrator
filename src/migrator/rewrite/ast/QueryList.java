package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of sequential queries.
 */
public final class QueryList implements IAstNode {
    /**
     * The list of queries contained in this list.
     */
    public final List<IQuery> queries;

    /**
     * Constructs a list of the given queries.
     *
     * @param queries the queries to be contained
     */
    public QueryList(List<IQuery> queries) {
        this.queries = queries;
    }

    @Override
    public String toSqlString() {
        return queries.stream().map(IQuery::toSqlString).collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return String.format("QueryList(%s)", queries);
    }
}
