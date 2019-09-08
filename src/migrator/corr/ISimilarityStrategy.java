package migrator.corr;

import migrator.rewrite.ast.ColumnRef;

/**
 * Strategy for computing similarity between columns.
 */
public interface ISimilarityStrategy {
    /**
     * Computes the similarity between two columns and returns the result as a
     * weight.
     *
     * @param first  the first column to compare
     * @param second the second column to compare
     * @return the similarity between the two given strings
     */
    int similarity(ColumnRef first, ColumnRef second);
}
