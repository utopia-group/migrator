package migrator.rewrite.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import migrator.rewrite.ast.TableDef;

/**
 * The state of a table. Holds a list of rows.
 */
public final class TableState {
    /**
     * The definition of this table.
     */
    public TableDef definition;
    /**
     * Map from column name to index in row.
     */
    public Map<String, Integer> columns;
    /**
     * List of rows.
     */
    public List<ISqlObject[]> rows;

    /**
     * Constructs an empty state for the given table.
     *
     * @param definition the definition of this table
     */
    public TableState(TableDef definition) {
        this.definition = definition;
        List<String> columnNames = definition.canonicalColumnNames;
        this.columns = new HashMap<>();
        for (ListIterator<String> it = columnNames.listIterator(); it.hasNext();) {
            int i = it.nextIndex();
            String el = it.next();
            this.columns.put(el, i);
        }
        this.rows = new ArrayList<>();
    }
}
