package migrator.rewrite.ast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The definition of a table in a schema.
 */
public class TableDef implements IAstNode {
    /**
     * The name of this table.
     */
    public final String name;
    /**
     * The columns in this table.
     * This is a map from column name to column type.
     */
    public final Map<String, ColumnType> columns;
    /**
     * List of columns in this table.
     * It is in the order of definition.
     */
    public final List<String> columnNames;
    /**
     * Canonical list of column names (sorted).
     * {@code null} initially.
     */
    public List<String> canonicalColumnNames;
    /**
     * The primary key of this table,
     * or {@code null} if none is present.
     */
    public String primaryKey;
    /**
     * The foreign keys in this table.
     * This is a map from column name to foreign key definition.
     */
    public final Map<String, ForeignKeyDef> foreignKeys;

    /**
     * Constructs the definition of a table with the given name
     * and columns with the given primary and foreign keys.
     *
     * @param name        the name of the table
     * @param columns     the columns in the table
     * @param primaryKey  the primary key of the table, or {@code null} if none
     * @param foreignKeys the foreign keys in the table
     */
    public TableDef(
            String name,
            List<String> columnNames,
            Map<String, ColumnType> columns,
            String primaryKey,
            Map<String, ForeignKeyDef> foreignKeys) {
        this.name = name;
        this.columnNames = columnNames;
        this.columns = columns;
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(name);
        builder.append(" (");
        List<String> statements = new ArrayList<>();
        for (String columnName : columnNames) {
            statements.add(String.format("%s %s", columnName, columns.get(columnName).toSqlString()));
        }
        statements.sort(Comparator.naturalOrder());
        if (primaryKey != null) {
            statements.add(String.format("PRIMARY KEY (%s)", primaryKey));
        }
        statements.addAll(foreignKeys.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, ForeignKeyDef>::getKey))
                .map(fk -> String.format("FOREIGN KEY (%s) %s",
                        fk.getKey(), fk.getValue().toSqlString()))
                .collect(Collectors.toList()));
        builder.append(statements.stream().collect(Collectors.joining(", ")));
        builder.append(");");
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("TableDef(%s, %s, %s, %s)", name, columns, primaryKey, foreignKeys);
    }
}
