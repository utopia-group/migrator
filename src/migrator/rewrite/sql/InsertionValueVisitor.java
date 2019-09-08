package migrator.rewrite.sql;

import java.util.List;
import java.util.Map;

import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ConstantValue;
import migrator.rewrite.ast.FreshValue;
import migrator.rewrite.ast.IValueVisitor;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.SubqueryValue;

/**
 * Visitor that generates concrete objects from values,
 * for use in insertions and updates.
 */
public class InsertionValueVisitor implements IValueVisitor<ISqlObject> {
    /**
     * Factory used to produce fresh values.
     */
    public FreshSqlObject.Factory freshFactory;
    /**
     * Visitor used to execute subqueries.
     */
    public QueryExecuteVisitor queryExecuteVisitor;
    /**
     * Map from parameter name to argument value.
     */
    public Map<String, ISqlObject> arguments;
    /**
     * Used to cache fresh values with the same index in the same invocation.
     */
    private Map<Integer, ISqlObject> freshObjects;

    /**
     * Constructs a new insertion value visitor using the given fresh object factory, query executor,
     * and argument map.
     *
     * @param freshFactory        factory to use when constructing new fresh values
     * @param queryExecuteVisitor visitor to use when executing subqueries
     * @param arguments           map from parameter name to argument value
     * @param freshObjects        map from fresh index to fresh object, used to cache within an invocation
     */
    public InsertionValueVisitor(FreshSqlObject.Factory freshFactory, QueryExecuteVisitor queryExecuteVisitor,
            Map<String, ISqlObject> arguments, Map<Integer, ISqlObject> freshObjects) {
        this.freshFactory = freshFactory;
        this.queryExecuteVisitor = queryExecuteVisitor;
        this.arguments = arguments;
        this.freshObjects = freshObjects;
    }

    @Override
    public ISqlObject visit(ConstantValue node) {
        return new ConstantSqlObject(node.value);
    }

    @Override
    public ISqlObject visit(ParameterValue node) {
        return arguments.get(node.name);
    }

    @Override
    public ISqlObject visit(ColumnValue node) {
        // if we support updates, we should delegate to a RowValueVisitor or something
        throw new UnsupportedOperationException("column references cannot appear in insertions");
    }

    /**
     * {@inheritDoc}
     *
     * @see RowValueVisitor#visit(SubqueryValue)
     */
    @Override
    public ISqlObject visit(SubqueryValue node) {
        // might want to abstract this away at some point
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
        return freshObjects.computeIfAbsent(node.index, i -> freshFactory.create());
    }

}
