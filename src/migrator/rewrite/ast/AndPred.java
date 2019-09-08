package migrator.rewrite.ast;

/**
 * An {@code AND} predicate.
 */
public final class AndPred implements IPredicate {
    /**
     * The left-hand side of this predicate.
     */
    public final IPredicate lhs;
    /**
     * The right-hand side of this predicate.
     */
    public final IPredicate rhs;

    /**
     * Constructs a predicate representing the
     * logical AND of the given predicates.
     *
     * @param lhs the left-hand side of this predicate
     * @param rhs the right-hand side of this predicate
     */
    public AndPred(IPredicate lhs, IPredicate rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public int getPrecedence() {
        return IPredicate.PRECEDENCE_AND;
    }

    @Override
    public <T> T accept(IPredVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        if (lhs.getPrecedence() < getPrecedence()) {
            builder.append("(");
            builder.append(lhs.toSqlString());
            builder.append(")");
        } else {
            builder.append(lhs.toSqlString());
        }
        builder.append(" AND ");
        if (rhs.getPrecedence() <= getPrecedence()) {
            builder.append("(");
            builder.append(rhs.toSqlString());
            builder.append(")");
        } else {
            builder.append(rhs.toSqlString());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("AndPred(%s, %s)", lhs, rhs);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 2; // distinguish from OrPred
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
        if (!(obj instanceof AndPred))
            return false;
        AndPred other = (AndPred) obj;
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
