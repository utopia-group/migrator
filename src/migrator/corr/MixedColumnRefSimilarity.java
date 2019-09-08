package migrator.corr;

import migrator.rewrite.ast.ColumnRef;

/**
 * Mixed similarity strategy between column references.
 * Each edit in column names decreases the similarity by one.
 * Every two edits in table names decreases the similarity by one.
 */
public class MixedColumnRefSimilarity implements ISimilarityStrategy {
    /**
     * The maximum similarity between two column references. This occurs iff the two column
     * references are equal.
     */
    public static final int MAX_SIMILARITY = 100;

    @Override
    public int similarity(ColumnRef first, ColumnRef second) {
        return MAX_SIMILARITY - StringDistance.levenshteinDistance(first.column, second.column)
                - StringDistance.levenshteinDistance(first.tableName, second.tableName) / 2;
    }

}
