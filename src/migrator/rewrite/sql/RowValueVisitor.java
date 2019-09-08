package migrator.rewrite.sql;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import migrator.rewrite.ast.AndPred;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ConstantValue;
import migrator.rewrite.ast.FreshValue;
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IPredVisitor;
import migrator.rewrite.ast.IValueVisitor;
import migrator.rewrite.ast.InPred;
import migrator.rewrite.ast.NotPred;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.Operator;
import migrator.rewrite.ast.OrPred;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.SubqueryValue;

/**
 * Visitor that computes values, which may be associated with a row.
 * Used during execution of queries.
 * <p>
 * For example, in the query {@code SELECT ... WHERE a = 5},
 * the value {@code a} (which is a {@link ColumnValue})
 * is associated with the row which is being processed.
 */
public class RowValueVisitor implements IColumnVisitor<ISqlObject>, IPredVisitor<Boolean>, IValueVisitor<ISqlObject> {
    /**
     * Map from table (alias) to index
     * in tableStates and rowIndices.
     */
    public Map<String, Integer> tables;
    /**
     * Array of table states.
     */
    public TableState[] tableStates;
    /**
     * Row indices for each table.
     */
    public int[] rowIndices;

    /**
     * Visitor used to execute subqueries.
     */
    public QueryExecuteVisitor queryExecuteVisitor;

    /**
     * Map from parameter name to argument value.
     */
    public Map<String, ISqlObject> arguments;

    /**
     * Constructs a new row value visitor.
     *
     * @param tables              map from table reference to index in tableStates and rowIndices
     * @param tableStates         array of table states (duplicate references should be duplicated)
     * @param rowIndices          array of row indices
     * @param queryExecuteVisitor visitor for subqueries
     * @param arguments           map from parameter name to argument value
     */
    public RowValueVisitor(Map<String, Integer> tables, TableState[] tableStates, int[] rowIndices,
            QueryExecuteVisitor queryExecuteVisitor, Map<String, ISqlObject> arguments) {
        this.tables = tables;
        this.tableStates = tableStates;
        this.rowIndices = rowIndices;
        this.queryExecuteVisitor = queryExecuteVisitor;
        this.arguments = arguments;
    }

    /**
     * Reshapes the rows referenced by this visitor to the given shape.
     *
     * @param shape the shape (columns) of the result
     * @return a new array in which the elements correspond to the row indices for the column in the shape
     */
    public int[] reshape(List<ColumnRef> shape) {
        int[] rows = new int[shape.size()];
        for (ListIterator<ColumnRef> it = shape.listIterator(); it.hasNext();) {
            int i = it.nextIndex();
            ColumnRef ref = it.next();
            rows[i] = rowIndices[tables.get(ref.tableName)];
        }
        return rows;
    }

    @Override
    public ConstantSqlObject visit(ConstantValue node) {
        return new ConstantSqlObject(node.value);
    }

    @Override
    public ISqlObject visit(ParameterValue node) {
        return arguments.get(node.name);
    }

    @Override
    public ISqlObject visit(ColumnValue node) {
        return node.column.accept(this);
    }

    @Override
    public ISqlObject visit(SubqueryValue node) {
        QueryResult result = queryExecuteVisitor.visit(node.query);
        List<ISqlObject[]> results = result.toArray(queryExecuteVisitor.state);
        if (results.size() != 1) {
            throw new QueryExecutionException("subquery used as a value returned " + results.size() + " rows");
        }
        ISqlObject[] row = results.get(0);
        assert row.length == 1;
        return row[0];
    }

    @Override
    public ISqlObject visit(FreshValue node) {
        throw new UnsupportedOperationException("fresh values should not appear in queries");
    }

    @Override
    public Boolean visit(AndPred node) {
        return node.lhs.accept(this) && node.rhs.accept(this);
    }

    @Override
    public Boolean visit(OrPred node) {
        return node.lhs.accept(this) || node.rhs.accept(this);
    }

    @Override
    public Boolean visit(NotPred node) {
        return !node.predicate.accept(this);
    }

    @Override
    public Boolean visit(OpPred node) {
        ISqlObject lhs = node.lhs.accept(this);
        ISqlObject rhs = node.rhs.accept(this);
        return lhs.compare(node.op, rhs);
    }

    @Override
    public Boolean visit(InPred node) {
        ISqlObject lhs = node.lhs.accept(this);
        QueryResult result = queryExecuteVisitor.visit(node.rhs);
        List<ISqlObject[]> results = result.toArray(queryExecuteVisitor.state);
        return results.stream().map(row -> {
            assert row.length == 1;
            return row[0];
        }).anyMatch(value -> lhs.compare(Operator.EQ, value));
    }

    @Override
    public ISqlObject visit(ColumnRef node) {
        int index = tables.get(node.tableName);
        TableState tableState = tableStates[index];
        return tableState.rows.get(rowIndices[index])[tableState.columns.get(node.column)];
    }

    @Override
    public ISqlObject visit(ColumnHole node) {
        throw new UnsupportedOperationException();
    }

}
