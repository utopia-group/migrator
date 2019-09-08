package migrator.sketch;

/**
 * Base class for boolean variables accepted by the solver.
 */
public abstract class BoolVariable {
    public final int index;

    /**
     * Create a boolean variable.
     *
     * @param index A positive index used for the SAT solver
     */
    public BoolVariable(int index) {
        if (index <= 0) {
            throw new IllegalStateException("Index for boolean variables must be positive");
        }
        this.index = index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoolVariable) {
            return this.index == ((BoolVariable) obj).index;
        }
        return false;
    }
}
