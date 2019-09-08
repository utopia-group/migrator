package migrator.rewrite.ast;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a nondeterministic choice between multiple query lists.
 */
public class ChooseQuery implements IQuery {
    /**
     * The query lists to choose from.
     */
    public final List<List<IQuery>> queryLists;

    /**
     * Constructs a new query that chooses one among
     * the given query lists.
     *
     * @param queryLists the query lists to choose from
     */
    public ChooseQuery(List<List<IQuery>> queryLists) {
        this.queryLists = queryLists;
    }

    @Override
    public <T> T accept(IQueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return "choose {\n"
                + queryLists.stream()
                        .map(list -> list.stream()
                                .map(query -> String.format("    %s", query.toSqlString()))
                                .collect(Collectors.joining("\n")))
                        .collect(Collectors.joining("\n} or {\n"))
                + "\n}";
    }

    @Override
    public String toString() {
        return String.format("ChooseQuery(%s)", queryLists);
    }
}
