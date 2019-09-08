package migrator.corr;

import migrator.rewrite.ast.ColumnRef;

/**
 * Similarity strategy between two column references.
 * Each edit in column names decreases the similarity by one.
 */
public class ColumnSimilarity implements ISimilarityStrategy {
    /**
     * The maximum similarity between two column references.
     * This occurs iff the two column references have the same column names.
     */
    public static final int MAX_SIMILARITY = 100;

    @Override
    public int similarity(ColumnRef first, ColumnRef second) {
        return MAX_SIMILARITY - StringDistance.levenshteinDistance(first.column, second.column);
    }

}
