package migrator.corr;

import java.util.List;

import migrator.rewrite.ast.ColumnRef;
import migrator.util.ListMultiMap;

/**
 * Data structure for value correspondence.
 */
public class ValueCorrespondence {
    /**
     * Mapping from column in source schema to column in target schema.
     */
    public final ListMultiMap<ColumnRef, ColumnRef> mapping;

    /**
     * Create a value correspondence from a list multi-map.
     *
     * @param mapping the list multi-map
     */
    public ValueCorrespondence(ListMultiMap<ColumnRef, ColumnRef> mapping) {
        this.mapping = mapping;
    }

    /**
     * Check if the value correspondence has the specified column in the domain
     *
     * @param key the specified column
     * @return {@code true} if the value correspondence has the specified column in the domain
     */
    public boolean containsKey(ColumnRef key) {
        return mapping.containsKey(key);
    }

    /**
     * Get all columns that the value correspondence can map the specified column to.
     *
     * @param key the specified column
     * @return a list of columns that the specified column can map to
     */
    public List<ColumnRef> get(ColumnRef key) {
        return mapping.get(key);
    }

    /**
     * Check if the value correspondence is injective.
     *
     * @return {@code true} if the value correspondence is injective
     */
    public boolean isInjective() {
        for (ColumnRef key : mapping.keySet()) {
            List<ColumnRef> values = mapping.get(key);
            if (values.size() != 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ColumnRef key : mapping.keySet()) {
            for (ColumnRef val : mapping.get(key)) {
                builder.append(key.toSqlString()).append(" -> ").append(val.toSqlString());
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
