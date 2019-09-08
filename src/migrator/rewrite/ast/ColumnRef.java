package migrator.rewrite.ast;

/**
 * A reference to a column, with an optional table name
 * if qualified.
 * <p>
 * {@link equals} and {@link hashCode} take only
 * the column name and table name into account.
 */
public final class ColumnRef implements IColumn {
    /**
     * The name of the column
     */
    public final String column;
    /**
     * The name or alias of the table that contains this column.
     * May be {@code null} if not specified.
     *
     * @see migrator.rewrite.ResolveColumnVisitor
     */
    public String tableName;
    /**
     * The name of the table in the schema that contains this column.
     * Will be {@code null} initially.
     *
     * @see migrator.rewrite.ResolveColumnVisitor
     */
    public String realTableName;
    /**
     * Index of scope of this variable.
     * 1 represents the innermost scope,
     * and the index increases outwards.
     * 0 if unknown.
     *
     * @see migrator.rewrite.ResolveColumnVisitor
     */
    public int scopeIndex;

    /**
     * Constructs a reference to the given column
     * with no table name specified.
     *
     * @param column the column to reference
     */
    public ColumnRef(String column) {
        this(column, null);
    }

    /**
     * Constructs a reference to the given column
     * in the given table name or alias.
     *
     * @param column    the column to reference
     * @param tableName the name or alias of the table that contains this column
     */
    public ColumnRef(String column, String tableName) {
        this(column, tableName, null, 0);
    }

    /**
     * Constructs a reference to the given column
     * in the given table name or alias
     * with the given real table name and scope index.
     *
     * @param column        the column to reference
     * @param tableName     the name or alias of the table that contains this column
     * @param realTableName the name in the schema of the table
     * @param scopeIndex    the index of the scope in which the table is found
     */
    public ColumnRef(String column, String tableName, String realTableName, int scopeIndex) {
        this.column = column;
        this.tableName = tableName;
        this.realTableName = realTableName;
        this.scopeIndex = scopeIndex;
    }

    /**
     * Constructs a reference to the column as specified by the given string
     * in SQL notation.
     *
     * @param text the column to reference, in SQL notation
     *             such as {@code Table.column}
     * @return the constructed column reference
     */
    public static ColumnRef from(String text) {
        String[] parts = text.split("\\.");
        if (parts.length == 0 || parts.length > 2)
            throw new IllegalArgumentException("invalid column name: " + text);
        if (parts.length == 1) {
            return new ColumnRef(parts[0]);
        } else {
            return new ColumnRef(parts[1], parts[0]);
        }
    }

    @Override
    public <T> T accept(IColumnVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        if (tableName == null) {
            return column;
        }
        return String.format("%s.%s", tableName, column);
    }

    @Override
    public String toString() {
        if (realTableName != null || scopeIndex != 0)
            return String.format("ColumnRef(%s, %s, %s, %d)", column, tableName, realTableName, scopeIndex);
        if (tableName == null)
            return String.format("ColumnRef(%s)", column);
        else
            return String.format("ColumnRef(%s, %s)", column, tableName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ColumnRef))
            return false;
        ColumnRef other = (ColumnRef) obj;
        if (column == null) {
            if (other.column != null)
                return false;
        } else if (!column.equals(other.column))
            return false;
        if (tableName == null) {
            if (other.tableName != null)
                return false;
        } else if (!tableName.equals(other.tableName))
            return false;
        return true;
    }
}
