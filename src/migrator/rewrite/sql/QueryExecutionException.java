package migrator.rewrite.sql;

/**
 * Represents an error that occurs during query execution.
 */
public final class QueryExecutionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message the message associated with this exception
     */
    public QueryExecutionException(String message) {
        super(message);
    }
}
