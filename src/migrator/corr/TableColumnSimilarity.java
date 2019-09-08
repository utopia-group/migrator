package migrator.corr;

import migrator.rewrite.ast.ColumnRef;

/**
 * Similarity strategy that computes the similarity between table and column
 * names using the Levenshtein edit distance.
 * Each edit in the concatenation of table names and column names decreases the similarity by one.
 */
public class TableColumnSimilarity implements ISimilarityStrategy {
    /**
     * The maximum similarity between two strings.
     * This occurs iff the two strings are equal.
     */
    public static final int MAX_SIMILARITY = 100;

    @Override
    public int similarity(ColumnRef first, ColumnRef second) {
        String firstTableName = first.realTableName;
        if (firstTableName == null) {
            firstTableName = first.tableName;
        }
        String secondTableName = second.realTableName;
        if (secondTableName == null) {
            secondTableName = second.tableName;
        }
        String firstString = firstTableName + first.column;
        String secondString = secondTableName + second.column;
        return MAX_SIMILARITY - StringDistance.levenshteinDistance(firstString, secondString);
    }

}
