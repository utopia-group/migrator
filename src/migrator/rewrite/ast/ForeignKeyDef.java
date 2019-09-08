package migrator.rewrite.ast;

/**
 * A foreign key definition.
 * A foreign key references a column in another table.
 */
public class ForeignKeyDef implements IAstNode {
    /**
     * The table that this foreign key references.
     */
    public final String destTable;
    /**
     * The column that this foreign key references.
     */
    public final String destColumn;

    /**
     * Constructs a foreign key definition that references
     * the given table and column.
     *
     * @param destTable  the table to reference
     * @param destColumn the column to reference
     */
    public ForeignKeyDef(String destTable, String destColumn) {
        this.destTable = destTable;
        this.destColumn = destColumn;
    }

    /**
     * Constructs a reference to the referenced column.
     *
     * @return a reference to the column referenced by this foreign key
     */
    public ColumnRef toColumnRef() {
        return new ColumnRef(destColumn, destTable);
    }

    @Override
    public String toSqlString() {
        return String.format("REFERENCES %s (%s)", destTable, destColumn);
    }

    @Override
    public String toString() {
        return String.format("ForeignKeyDef(%s, %s)", destTable, destColumn);
    }
}
