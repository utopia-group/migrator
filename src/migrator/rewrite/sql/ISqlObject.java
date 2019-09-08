package migrator.rewrite.sql;

import migrator.rewrite.ast.Operator;

/**
 * An abstract SQL object.
 */
public interface ISqlObject {
    /**
     * Compares this object to the given other object.
     * The type of comparison is given by the operator.
     *
     * @param op    the type of comparison to perform
     * @param other the object to compare to
     * @return the result of the comparison
     */
    default boolean compare(Operator op, ISqlObject other) {
        if (this instanceof ConstantSqlObject
                && other instanceof ConstantSqlObject
                && (op == Operator.EQ || op == Operator.NE)) {
            ConstantSqlObject lhs = (ConstantSqlObject) this;
            ConstantSqlObject rhs = (ConstantSqlObject) other;
            return (op == Operator.NE) ^ (lhs.value.equals(rhs.value));
        }
        if ((this instanceof FreshSqlObject) ^ (other instanceof FreshSqlObject)) {
            // fresh values are not equal to any other value
            if (op == Operator.EQ || op == Operator.NE) {
                return op == Operator.NE;
            }
            // queries should not depend on the values of fresh values
            throw new UnsupportedOperationException("queries should not depend on the values of fresh values");
        }
        long lhs = this.intValue();
        long rhs = other.intValue();
        switch (op) {
        case EQ:
            return lhs == rhs;
        case NE:
            return lhs != rhs;
        case LT:
            return lhs < rhs;
        case LE:
            return lhs <= rhs;
        case GT:
            return lhs > rhs;
        case GE:
            return lhs >= rhs;
        default:
            throw new IllegalArgumentException("unknown enum value " + op);
        }
    }

    /**
     * Converts this object into an integral value.
     *
     * @return this value as an integral value
     */
    int intValue();
}
