package migrator.rewrite.ast;

/**
 * A "fresh" value generated during rewriting.
 * A fresh value should be distinct from any other value
 * in the table.
 */
public class FreshValue implements IValue {
    /**
     * A unique index for this fresh value.
     * Fresh values with the same index should have the same value,
     * and fresh values with different indices should have distinct values.
     */
    public final int index;

    /**
     * Constructs a fresh value with the given index.
     *
     * @param index the index of this fresh value
     */
    public FreshValue(int index) {
        this.index = index;
    }

    @Override
    public <T> T accept(IValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return String.format("FRESH(%d)", index);
    }

    @Override
    public String toString() {
        return String.format("FreshValue(%d)", index);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof FreshValue))
            return false;
        FreshValue other = (FreshValue) obj;
        if (index != other.index)
            return false;
        return true;
    }
}
