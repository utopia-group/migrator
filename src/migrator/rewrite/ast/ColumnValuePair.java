package migrator.rewrite.ast;

/**
 * A pair of a column and a value,
 * used in {@code INSERT} and {@code UPDATE} queries.
 *
 * @see InsertQuery
 * @see UpdateQuery
 * @see CompoundUpdateQuery
 */
public class ColumnValuePair implements IAstNode {
    /**
     * The column in this pair
     */
    public final IColumn column;
    /**
     * The value in this pair
     */
    public final IValue value;

    /**
     * Constructs a pair of the given column and value.
     *
     * @param column the column
     * @param value  the value
     */
    public ColumnValuePair(IColumn column, IValue value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public String toSqlString() {
        return String.format("%s = %s", column.toSqlString(), value.toSqlString());
    }

    @Override
    public String toString() {
        return String.format("ColumnValuePair(%s, %s)", column, value);
    }
}
