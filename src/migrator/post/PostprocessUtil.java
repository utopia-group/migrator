package migrator.post;

import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.IColumn;
import migrator.rewrite.ast.IPredicate;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.OpPred;

/**
 * Utility functions for post-processing.
 */
public final class PostprocessUtil {

    public static ColumnRef getUpdateColumn(ColumnValuePair pair) {
        assert pair.column instanceof ColumnRef;
        return (ColumnRef) pair.column;
    }

    public static ColumnRef getOpPredLhsColumn(IPredicate pred) {
        if (!(pred instanceof OpPred)) {
            throw new IllegalArgumentException();
        }
        IValue lhs = ((OpPred) pred).lhs;
        assert lhs instanceof ColumnValue;
        IColumn column = ((ColumnValue) lhs).column;
        assert column instanceof ColumnRef;
        return (ColumnRef) column;
    }

}
