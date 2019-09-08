package migrator.rewrite.ast;

import java.util.Objects;

/**
 * The type of a column of a table in a schema.
 */
public final class ColumnType implements IAstNode {
    /**
     * The raw type of the column in SQL syntax.
     */
    public final String sqlType;

    /**
     * Constructs a type with the given SQL type.
     *
     * @param sqlType the type of the column in SQL syntax
     */
    public ColumnType(String sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public String toSqlString() {
        return sqlType;
    }

    @Override
    public String toString() {
        return String.format("ColumnType(%s)", sqlType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sqlType);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ColumnType))
            return false;
        return Objects.equals(sqlType, ((ColumnType) other).sqlType);
    }
}
