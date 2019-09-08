package migrator.rewrite.sql;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import migrator.rewrite.program.Parameter;

/**
 * A generated value used in generated test cases.
 */
public final class GeneratedSqlObject implements ISqlObject {
    /**
     * Supplies a fixed array of generated values.
     */
    public static final class ArraySupplier implements Parameter.ValueSupplier<GeneratedSqlObject> {
        /**
         * The values to supply.
         */
        public int[] values;

        /**
         * Constructs an array supplier from the given list.
         *
         * @param values the values to supply
         */
        public ArraySupplier(int... values) {
            this.values = values;
        }

        @Override
        public Stream<GeneratedSqlObject> values(Parameter param) {
            return IntStream.of(values).mapToObj(GeneratedSqlObject::new);
        }
    }

    /**
     * The integer value of this value.
     */
    public int value;

    /**
     * Constructs a generated value with the given integer value.
     *
     * @param value the integer value of this value
     */
    public GeneratedSqlObject(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("GeneratedValue(%s)", value);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof GeneratedSqlObject))
            return false;
        return value == ((GeneratedSqlObject) other).value;
    }

    @Override
    public int intValue() {
        return value;
    }
}
