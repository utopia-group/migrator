package migrator.corr;

/**
 * Interface for value correspondence suppliers.
 */
public interface IValueCorrSupplier {

    /**
     * Check if there is another value correspondence.
     *
     * @return {@code true} if there is another value correspondence
     */
    public boolean hasNext();

    /**
     * Get the next value correspondence.
     *
     * @return next value correspondence
     */
    public ValueCorrespondence getNext();

}
