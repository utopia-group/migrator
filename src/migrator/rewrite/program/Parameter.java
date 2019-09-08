package migrator.rewrite.program;

import java.util.Objects;
import java.util.stream.Stream;

import migrator.rewrite.ast.IAstNode;

/**
 * A parameter to a method. Has a name and type.
 */
public final class Parameter implements IAstNode {
    /**
     * Supplier of values for a given parameter.
     *
     * @param <T> the type of the values
     */
    @FunctionalInterface
    public static interface ValueSupplier<T> {
        /**
         * Returns a stream of the possible values to the given parameter.
         *
         * @param param the parameter for which to generate values
         * @return a stream of the possible values for this parameter
         */
        Stream<T> values(Parameter param);
    }

    /**
     * The name of this parameter.
     */
    public String name;
    /**
     * The type of this parameter.
     */
    public String type;

    /**
     * Constructs a new parameter with the given name and type.
     *
     * @param name the name of this parameter
     * @param type the type of this parameter
     */
    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of this parameter.
     *
     * @return the name of this parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of this parameter.
     *
     * @return the type of this parameter
     */
    public String getType() {
        return type;
    }

    @Override
    public String toSqlString() {
        return String.format("%s %s", type, name);
    }

    @Override
    public String toString() {
        return String.format("Parameter(%s, %s)", name, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Parameter)) {
            return false;
        }
        Parameter p = (Parameter) other;
        return Objects.equals(name, p.name) && Objects.equals(type, p.type);
    }
}
