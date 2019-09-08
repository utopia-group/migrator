package migrator.rewrite.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import migrator.rewrite.ast.TableDef;

/**
 * The state of a program. Holds tables.
 */
public final class ProgramState {
    /**
     * Map from table name to table state.
     */
    public Map<String, TableState> tables;

    /**
     * Constructs an empty program state with the given tables.
     *
     * @param tables the tables in the program
     */
    public ProgramState(List<TableDef> tables) {
        this.tables = new HashMap<>();
        for (TableDef table : tables) {
            TableState old = this.tables.put(table.name, new TableState(table));
            if (old != null) {
                throw new IllegalArgumentException("duplicate table with name " + table.name);
            }
        }
    }
}
