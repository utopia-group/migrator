package migrator.post;

/**
 * Exception class for the Mediator translator.
 */
public class TranslationException extends RuntimeException {

    private static final long serialVersionUID = -8043043840813606332L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message the message of this exception
     */
    public TranslationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and cause.
     *
     * @param message the message of this exception
     * @param cause   the cause of this exception
     */
    public TranslationException(String message, Throwable cause) {
        super(message, cause);
    }

}
