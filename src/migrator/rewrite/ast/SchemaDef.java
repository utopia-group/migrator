package migrator.rewrite.ast;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The definition of a database schema.
 * A schema contains table definitions.
 */
public class SchemaDef implements IAstNode {
    /**
     * The tables contained in this schema.
     * This is a map from table name to definition.
     */
    public final Map<String, TableDef> tables;
    /**
     * List of table names in this schema. It is in the order of definition.
     */
    public final List<String> tableNames;

    /**
     * Constructs a schema with the given tables,
     * creating a table map.
     *
     * @param tables the list of tables in this schema
     */
    public SchemaDef(List<TableDef> tables) {
        tableNames = tables.stream().map(table -> table.name).collect(Collectors.toList());
        this.tables = mapFromTableList(tables);
    }

    /**
     * Constructs a table map from the given tables.
     *
     * @param tables the tables to be mapped
     * @return a map from table name to definition
     */
    public static Map<String, TableDef> mapFromTableList(List<TableDef> tables) {
        Map<String, TableDef> map = new HashMap<>();
        for (TableDef table : tables) {
            map.put(table.name, table);
        }
        return map;
    }

    @Override
    public String toSqlString() {
        return tables.values().stream()
                .sorted(Comparator.comparing(tableDef -> tableDef.name))
                .map(TableDef::toSqlString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String toString() {
        return String.format("SchemaDef(%s)", tables);
    }
}
