package migrator.rewrite.program;

import migrator.rewrite.ast.IAstNode;

/**
 * The type of a method.
 * Can either be a query or an update.
 */
public enum MethodType implements IAstNode {
    /**
     * A query.
     * Queries return a list of rows as a result.
     */
    QUERY,
    /**
     * An update.
     * Updates do not return anything.
     */
    UPDATE,
    ;

    @Override
    public String toSqlString() {
        switch (this) {
        case QUERY:
            return "query";
        case UPDATE:
            return "update";
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * Converts the given type from a string to a method type.
     * Accepts the values {@code "query"} and {@code "update"}.
     *
     * @param text the string to convert
     * @return the string as a method type
     * @throws IllegalArgumentException if the string does not match any method type
     */
    public static MethodType fromSqlString(String text) {
        switch (text) {
        case "query":
            return QUERY;
        case "update":
            return UPDATE;
        default:
            throw new IllegalArgumentException();
        }
    }
}
