package migrator.rewrite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.TableRef;

/**
 * Data structure for join chains.
 */
public final class JoinChain {

    public final TableRef table;

    public final List<Join> joins;

    // internal representation for hashcode and equals functions
    private List<TableRef> allTables;

    private List<ColumnRef> allColumns;

    public JoinChain(TableRef table, List<Join> joins) {
        this.table = table;
        this.joins = joins;
        buildInternalForm();
    }

    private void buildInternalForm() {
        // build a sorted table list
        allTables = new ArrayList<>();
        allTables.add(table);
        joins.forEach(join -> allTables.add(join.getDest()));
        allTables.sort((x, y) -> x.getTable().compareTo(y.getTable()));
        // build a sorted column list
        allColumns = new ArrayList<>();
        for (Join join : joins) {
            allColumns.add(join.getSrcColumn());
            allColumns.add(join.getDestColumn());
        }
        allColumns.sort((x, y) -> (x.column + x.tableName).compareTo(y.column + y.tableName));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JoinChain(").append(table.getReference());
        for (Join join : joins) {
            builder.append(" JOIN ").append(join.getDest().getReference());
            builder.append(" ON ").append(join.getSrcColumn().toSqlString());
            builder.append(" = ").append(join.getDestColumn().toSqlString());
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return allTables.hashCode() + allColumns.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            JoinChain other = (JoinChain) obj;
            return Objects.equals(allTables, other.allTables)
                    && Objects.equals(allColumns, other.allColumns);
        }
    }

}
