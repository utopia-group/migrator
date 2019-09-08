package migrator.rewrite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.TableRef;

public final class RewriteUtil {

    /**
     * Find all columns in the table.
     *
     * @param table  the table
     * @param schema the schema
     * @return a set of columns
     */
    public static Set<ColumnRef> allColumnsInTable(TableRef table, SchemaDef schema) {
        if (!schema.tables.containsKey(table.getTable())) {
            throw new IllegalArgumentException(String.format("Table (%s) is not defined", table.getTable()));
        }
        Set<ColumnRef> resolvedColumnRefs = new HashSet<>();
        TableDef tableDef = schema.tables.get(table.getTable());
        for (String column : tableDef.columns.keySet()) {
            resolvedColumnRefs.add(new ColumnRef(column, table.getReference()));
        }
        return resolvedColumnRefs;
    }

    /**
     * Check if a resolved column reference is a foreign key in the schema.
     *
     * @param columnRef the resolved column reference
     * @param schema    the schema
     * @return {@code true} if it is a foreign key
     */
    public static boolean isForeignKey(ColumnRef columnRef, SchemaDef schema) {
        if (columnRef.tableName == null) {
            throw new IllegalArgumentException("Unresolved column reference");
        }
        TableDef tableDef = schema.tables.get(columnRef.tableName);
        return tableDef.foreignKeys.containsKey(columnRef.column);
    }

    /**
     * Get the reference key of a given foreign key.
     *
     * @param fk     the foreign key
     * @param schema the schema
     * @return the reference key
     */
    public static ColumnRef getReferenceKey(ColumnRef fk, SchemaDef schema) {
        if (fk.tableName == null) {
            throw new IllegalArgumentException("Unresolved column reference");
        }
        TableDef tableDef = schema.tables.get(fk.tableName);
        ForeignKeyDef fkDef = tableDef.foreignKeys.get(fk.column);
        if (fkDef == null) {
            throw new IllegalStateException(fk.toSqlString() + " is not a foreign key");
        }
        return fkDef.toColumnRef();
    }

    /**
     * Get the foreign key of a given reference key.
     *
     * @param refKey    the reference key
     * @param tableDefs a list of table definitions
     * @return the reference key, or {@code null} if it does not exist
     */
    public static ColumnRef getForeignKey(ColumnRef refKey, List<TableDef> tableDefs) {
        if (refKey.tableName == null) {
            throw new IllegalArgumentException("Unresolved column reference");
        }
        ColumnRef ret = null;
        for (TableDef tableDef : tableDefs) {
            for (Map.Entry<String, ForeignKeyDef> entry : tableDef.foreignKeys.entrySet()) {
                String columnName = entry.getKey();
                ColumnRef destKey = entry.getValue().toColumnRef();
                if (refKey.equals(destKey)) {
                    if (ret != null) {
                        throw new IllegalStateException("Ambiguous reference key");
                    }
                    ret = new ColumnRef(columnName, tableDef.name);
                }
            }
        }
        return ret;
    }

    /**
     * Get all tables from a given join chain.
     *
     * @param chain the join chain
     * @return all tables in the join chain
     */
    public static List<TableRef> getTablesFromJoinChain(JoinChain chain) {
        List<TableRef> tables = new ArrayList<>();
        tables.add(chain.table);
        for (Join join : chain.joins) {
            tables.add(join.getDest());
        }
        return tables;
    }
}
