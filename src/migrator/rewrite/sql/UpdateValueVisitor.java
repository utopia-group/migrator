package migrator.rewrite.sql;

import java.util.Map;

import migrator.rewrite.ast.FreshValue;

/**
 * Visitor that computes values, which may be associated with a row.
 * Used during execution of update queries.
 * Extends {@link RowValueVisitor} to support fresh values.
 * Delegates fresh values to an {@link InsertionValueVisitor}.
 */
public class UpdateValueVisitor extends RowValueVisitor {
    // delegate fresh values to insertion value visitor
    private InsertionValueVisitor insertionValueVisitor;

    /**
     * Constructs a new update value visitor.
     *
     * @see RowValueVisitor#RowValueVisitor
     * @see InsertionValueVisitor#InsertionValueVisitor
     * @param tables              map from table reference to index in tableStates and rowIndices
     * @param tableStates         array of table states (duplicate references should be duplicated)
     * @param rowIndices          array of row indices
     * @param freshFactory        factory to use when constructing new fresh values
     * @param freshObjects        map from fresh index to fresh object, used to cache within an invocation
     * @param queryExecuteVisitor visitor for subqueries
     * @param arguments           map from parameter name to argument value
     */
    public UpdateValueVisitor(
            // row value visitor parameters
            Map<String, Integer> tables, TableState[] tableStates, int[] rowIndices,
            // insertion value visitor parameters
            FreshSqlObject.Factory freshFactory, Map<Integer, ISqlObject> freshObjects,
            // common parameters
            QueryExecuteVisitor queryExecuteVisitor, Map<String, ISqlObject> arguments) {
        super(tables, tableStates, rowIndices, queryExecuteVisitor, arguments);
        insertionValueVisitor = new InsertionValueVisitor(freshFactory, queryExecuteVisitor, arguments, freshObjects);
    }

    @Override
    public ISqlObject visit(FreshValue node) {
        return insertionValueVisitor.visit(node);
    }
}
