package migrator.rewrite.sql;

/**
 * Represents a fresh SQL object;
 * can only be created by the {@link Factory} inner class.
 * Fresh SQL objects are distinct from every other object
 * besides themselves.
 */
public final class FreshSqlObject implements ISqlObject {
    /**
     * Fresh SQL object factory. Creates fresh objects
     * with increasing indices.
     */
    public static final class Factory {
        private int nextIndex;

        /**
         * Creates a new factory.
         */
        public Factory() {
            nextIndex = 0;
        }

        /**
         * Gets the index of the next object to be created.
         *
         * @return the index of the next object
         */
        public int getNextIndex() {
            return nextIndex;
        }

        /**
         * Creates a new fresh object.
         *
         * @return an object with a distinct index from all others
         */
        public FreshSqlObject create() {
            return new FreshSqlObject(nextIndex++);
        }
    }

    /**
     * The index of this object.
     * Fresh objects with different indices are distinct.
     */
    public final int index;

    private FreshSqlObject(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return String.format("FreshSqlObject(%s)", index);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FreshSqlObject))
            return false;
        return this.index == ((FreshSqlObject) other).index;
    }

    @Override
    public int intValue() {
        return index;
    }
}
