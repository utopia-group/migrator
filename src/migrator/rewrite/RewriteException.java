package migrator.rewrite;

/**
 * Indicates that rewriting of a query was unable to be performed.
 */
public final class RewriteException extends RuntimeException {

    private static final long serialVersionUID = -4039155348430957007L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message the message of this exception
     */
    public RewriteException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and cause.
     *
     * @param message the message of this exception
     * @param cause   the cause of this exception
     */
    public RewriteException(String message, Throwable cause) {
        super(message, cause);
    }

}
