package migrator.rewrite.ast;

/**
 * A value representing the value of a column in a row.
 */
public final class ColumnValue implements IValue {
    /**
     * The column referenced by this value.
     */
    public final IColumn column;

    /**
     * Constructs a value referencing the given column.
     *
     * @param column the column to reference
     */
    public ColumnValue(IColumn column) {
        this.column = column;
    }

    @Override
    public <T> T accept(IValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return column.toSqlString();
    }

    @Override
    public String toString() {
        return String.format("ColumnValue(%s)", column);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ColumnValue))
            return false;
        ColumnValue other = (ColumnValue) obj;
        if (column == null) {
            if (other.column != null)
                return false;
        } else if (!column.equals(other.column))
            return false;
        return true;
    }
}
