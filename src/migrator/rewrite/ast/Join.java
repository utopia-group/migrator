package migrator.rewrite.ast;

/**
 * A equi-join that gives rows for which the destination column
 * matches the source column.
 */
public final class Join implements IAstNode {
    /**
     * The table to join.
     */
    private final TableRef dest;
    /**
     * The source column.
     */
    private final ColumnRef srcColumn;
    /**
     * The destination column.
     */
    private final ColumnRef destColumn;

    /**
     * Constructs a join with the given table that returns rows for which the values
     * in the given source and destination columns are equal.
     * The destination column should be a column in the destination table.
     *
     * @param dest       the table to join
     * @param srcColumn  the source column
     * @param destColumn the destination column
     */
    public Join(TableRef dest, ColumnRef srcColumn, ColumnRef destColumn) {
        if (destColumn != null && !destColumn.tableName.equals(dest.getReference())) {
            throw new IllegalArgumentException(
                    String.format("destination column (%s) does not belong to table (%s)", destColumn, dest));
        }
        this.dest = dest;
        this.srcColumn = srcColumn;
        this.destColumn = destColumn;
    }

    /**
     * Returns the table to be joined.
     *
     * @return the destination table
     */
    public TableRef getDest() {
        return dest;
    }

    /**
     * Returns the column in the source tables that corresponds
     * to a column in the destination table.
     *
     * @return the source column
     */
    public ColumnRef getSrcColumn() {
        return srcColumn;
    }

    /**
     * Returns the column in the destination table
     * that corresponds to a source column.
     *
     * @return the destination column
     */
    public ColumnRef getDestColumn() {
        return destColumn;
    }

    @Override
    public String toSqlString() {
        return String.format("JOIN %s ON %s = %s",
                dest.toSqlString(),
                srcColumn.toSqlString(),
                destColumn.toSqlString());
    }

    @Override
    public String toString() {
        return String.format("Join(%s, %s, %s)", dest, srcColumn, destColumn);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dest == null) ? 0 : dest.hashCode());
        result = prime * result + ((destColumn == null) ? 0 : destColumn.hashCode());
        result = prime * result + ((srcColumn == null) ? 0 : srcColumn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Join))
            return false;
        Join other = (Join) obj;
        if (dest == null) {
            if (other.dest != null)
                return false;
        } else if (!dest.equals(other.dest))
            return false;
        if (destColumn == null) {
            if (other.destColumn != null)
                return false;
        } else if (!destColumn.equals(other.destColumn))
            return false;
        if (srcColumn == null) {
            if (other.srcColumn != null)
                return false;
        } else if (!srcColumn.equals(other.srcColumn))
            return false;
        return true;
    }
}
