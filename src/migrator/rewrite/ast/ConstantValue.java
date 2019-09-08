package migrator.rewrite.ast;

/**
 * A value representing a constant value.
 */
public final class ConstantValue implements IValue {
    /**
     * The value in SQL syntax.
     */
    public final String value;

    /**
     * Constructs a value representing the given constant
     * in SQL syntax.
     *
     * @param value the constant value
     */
    public ConstantValue(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("ConstantValue(%s)", value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ConstantValue))
            return false;
        ConstantValue other = (ConstantValue) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
