package migrator.rewrite.ast;

/**
 * A predicate that matches rows where the values
 * of the left- and right-hand sides are related
 * by a relational operator.
 */
public final class OpPred implements IPredicate {
    /**
     * The left-hand side of this predicate.
     */
    public final IValue lhs;
    /**
     * The right-hand side of this predicate.
     */
    public final IValue rhs;
    /**
     * The operator that relates the sides of this predicate.
     */
    public final Operator op;

    /**
     * Constructs a predicate that relates the given
     * values by an operator.
     *
     * @param lhs the left-hand side
     * @param rhs the right-hand side
     * @param op  the operator that relates the two sides
     */
    public OpPred(IValue lhs, IValue rhs, Operator op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
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
        return String.format("%s %s %s", lhs.toSqlString(), op.toSqlString(), rhs.toSqlString());
    }

    @Override
    public String toString() {
        return String.format("OpPred(%s, %s, %s)", lhs, rhs, op);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
        result = prime * result + ((op == null) ? 0 : op.hashCode());
        result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof OpPred))
            return false;
        OpPred other = (OpPred) obj;
        if (lhs == null) {
            if (other.lhs != null)
                return false;
        } else if (!lhs.equals(other.lhs))
            return false;
        if (op != other.op)
            return false;
        if (rhs == null) {
            if (other.rhs != null)
                return false;
        } else if (!rhs.equals(other.rhs))
            return false;
        return true;
    }
}
