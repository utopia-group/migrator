package migrator.rewrite.ast;

/**
 * An SQL operator.
 * In particular, it is different from the Operator class in benchmarks.
 */
public enum Operator implements IAstNode {
    /**
     * Equality (A {@code =} B).
     */
    EQ,
    /**
     * Inequality (A {@code <>} B).
     */
    NE,
    /**
     * Strictly less-than (A {@code <} B).
     */
    LT,
    /**
     * Less-than or equal (A {@code <=} B).
     */
    LE,
    /**
     * Strictly greater-than (A {@code >} B).
     */
    GT,
    /**
     * Greater-than or equal (A {@code >=} B).
     */
    GE;

    /**
     * Converts this operator to the appropriate SQL operator.
     *
     * @return this operator in SQL syntax
     */
    @Override
    public String toSqlString() {
        switch (this) {
        case EQ:
            return "=";
        case NE:
            return "<>";
        case LT:
            return "<";
        case LE:
            return "<=";
        case GT:
            return ">";
        case GE:
            return ">=";
        default:
            throw new RuntimeException("unknown operator");
        }
    }

    /**
     * Converts the given operator in SQL syntax
     * to an {@link Operator} object.
     *
     * @param text the operator in SQL syntax
     * @return the operator as an {@link Operator} object
     * @throws RuntimeException if the given operator is invalid
     */
    public static Operator fromSqlString(String text) {
        switch (text) {
        case "=":
            return Operator.EQ;
        case "<>":
            return Operator.NE;
        case "<":
            return Operator.LT;
        case "<=":
            return Operator.LE;
        case ">":
            return Operator.GT;
        case ">=":
            return Operator.GE;
        default:
            throw new RuntimeException("unknown operator");
        }
    }
}
