package migrator.rewrite.sql;

import java.util.Objects;

/**
 * A constant specified in the program.
 */
public final class ConstantSqlObject implements ISqlObject {
    /**
     * The value of this constant.
     */
    public String value;

    /**
     * Constructs a new constant with the given value.
     *
     * @param value the value of this constant
     */
    public ConstantSqlObject(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("ConstantSQLObject(%s)", value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConstantSqlObject)) return false;
        return Objects.equals(value, ((ConstantSqlObject) other).value);
    }

    @Override
    public int intValue() {
        return Integer.parseInt(value);
    }
}
