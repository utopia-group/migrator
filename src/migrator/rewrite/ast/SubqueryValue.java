package migrator.rewrite.ast;

/**
 * A value representing the singular value returned by a subquery.
 * The subquery contained must return exactly one row.
 */
public class SubqueryValue implements IValue {
    /**
     * The subquery that returns the value to be used.
     */
    public final Subquery query;

    /**
     * Constructs a value of the given subquery.
     *
     * @param value the subquery to use
     */
    public SubqueryValue(Subquery value) {
        this.query = value;
    }

    @Override
    public <T> T accept(IValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return query.toSqlString();
    }

    @Override
    public String toString() {
        return String.format("SubqueryValue(%s)", query);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SubqueryValue))
            return false;
        SubqueryValue other = (SubqueryValue) obj;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        return true;
    }
}
