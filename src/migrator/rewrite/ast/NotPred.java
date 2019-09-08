package migrator.rewrite.ast;

/**
 * The negation of a predicate.
 */
public final class NotPred implements IPredicate {
    /**
     * The predicate to negate
     */
    public final IPredicate predicate;

    /**
     * Constructs the negation of the given predicate.
     *
     * @param predicate the predicate to negate
     */
    public NotPred(IPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public int getPrecedence() {
        return IPredicate.PRECEDENCE_NOT;
    }

    @Override
    public <T> T accept(IPredVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NOT ");
        if (predicate.getPrecedence() < getPrecedence()) {
            builder.append("(");
            builder.append(predicate.toSqlString());
            builder.append(")");
        } else {
            builder.append(predicate.toSqlString());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("NotPred(%s)", predicate);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof NotPred))
            return false;
        NotPred other = (NotPred) obj;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        return true;
    }
}
