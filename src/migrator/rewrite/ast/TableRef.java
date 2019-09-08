package migrator.rewrite.ast;

/**
 * A reference to a table with an optional alias.
 * The alias (or reference) is the same as the table name
 * if none is given.
 */
public final class TableRef implements IAstNode {
    /**
     * The name of the table to reference.
     */
    private final String table;
    /**
     * The name used within the query to refer to the
     * table referenced by {@link #table}.
     */
    private final String reference;

    /**
     * Constructs a reference to the given table with no alias.
     * The reference will be the same as the table name.
     *
     * @param table the name of the table to reference
     */
    public TableRef(String table) {
        this(table, table);
    }

    /**
     * Constructs a reference to the given table
     * with the given alias.
     *
     * @param table     the name of the table to reference
     * @param reference the name used within the query to reference this table
     */
    public TableRef(String table, String reference) {
        if (reference == null)
            reference = table;
        this.table = table;
        this.reference = reference;
    }

    /**
     * Returns the name of the table referenced by this reference.
     *
     * @return the name of the table
     * @see #getReference
     */
    public String getTable() {
        return table;
    }

    /**
     * Returns the name (alias) used within the query to reference this table.
     *
     * @return the name of the alias
     * @see #getTable
     */
    public String getReference() {
        return reference;
    }

    @Override
    public String toSqlString() {
        if (table.equals(reference))
            return table;
        else
            return String.format("%s AS %s", table, reference);
    }

    @Override
    public String toString() {
        return String.format("TableRef(%s, %s)", table, reference);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TableRef))
            return false;
        TableRef other = (TableRef) obj;
        if (reference == null) {
            if (other.reference != null)
                return false;
        } else if (!reference.equals(other.reference))
            return false;
        if (table == null) {
            if (other.table != null)
                return false;
        } else if (!table.equals(other.table))
            return false;
        return true;
    }
}
