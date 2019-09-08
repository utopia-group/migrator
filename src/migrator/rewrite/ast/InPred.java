package migrator.rewrite.ast;

/**
 * A predicate that matches rows where
 * the value of the left-hand side is equal to
 * a value returned by the subquery in the right-hand side.
 */
public final class InPred implements IPredicate {
    /**
     * The left-hand side of this predicate.
     */
    public final IValue lhs;
    /**
     * The right-hand side of this predicate.
     */
    public final Subquery rhs;

    /**
     * Constructs a predicate that matches rows
     * where the value of the left-hand side
     * is returned by the right-hand side.
     *
     * @param lhs the left-hand side of this predicate
     * @param rhs the right-hand side of this predicate
     */
    public InPred(IValue lhs, Subquery rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public int getPrecedence() {
        return IPredicate.PRECEDENCE_TOP;
    }

    @Override
    public <T> T accept(IPredVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return String.format("%s IN %s", lhs.toSqlString(), rhs.toSqlString());
    }

    @Override
    public String toString() {
        return String.format("InPred(%s, %s)", lhs, rhs);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
        result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof InPred))
            return false;
        InPred other = (InPred) obj;
        if (lhs == null) {
            if (other.lhs != null)
                return false;
        } else if (!lhs.equals(other.lhs))
            return false;
        if (rhs == null) {
            if (other.rhs != null)
                return false;
        } else if (!rhs.equals(other.rhs))
            return false;
        return true;
    }
}
