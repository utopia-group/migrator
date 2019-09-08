package migrator.sketch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import migrator.rewrite.ast.AndPred;
import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.ConstantValue;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.FreshValue;
import migrator.rewrite.ast.IColumn;
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IPredVisitor;
import migrator.rewrite.ast.IPredicate;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.IValueVisitor;
import migrator.rewrite.ast.InPred;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.NotPred;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.OrPred;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.Subquery;
import migrator.rewrite.ast.SubqueryValue;
import migrator.rewrite.ast.UpdateQuery;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;

/**
 * A visitor for instantiating holes in the sketch.
 * <p>
 * This visitor does not modify the original sketch.
 */
public class SketchInstantiator implements
        IQueryVisitor<List<IQuery>>, IPredVisitor<IPredicate>,
        IColumnVisitor<ColumnRef>, IValueVisitor<IValue> {

    private final Sketch sketch;
    private final SketchModel model;

    /**
     * Create a sketch instantiator.
     *
     * @param sketch the sketch
     * @param model  assignment for holes in the sketch
     */
    public SketchInstantiator(Sketch sketch, SketchModel model) {
        this.sketch = sketch;
        this.model = model;
    }

    /**
     * Instantiate holes in the sketch.
     *
     * @return instantiated SQL program
     */
    public SqlProgram instantiate() {
        List<MethodImplementation> instantiatedMethods = new ArrayList<>();
        for (MethodImplementation method : sketch.sketchProgram.methods) {
            List<IQuery> instantiatedQueries = new ArrayList<>();
            for (IQuery query : method.queries) {
                instantiatedQueries.addAll(query.accept(this));
            }
            instantiatedMethods.add(new MethodImplementation(method.signature, instantiatedQueries));
        }
        return new SqlProgram(instantiatedMethods);
    }

    @Override
    public List<IQuery> visit(SelectQuery query) {
        List<IColumn> columns = query.columns.stream()
                .map(column -> column.accept(this))
                .collect(Collectors.toList());
        IPredicate pred = query.predicate.accept(this);
        return Collections.singletonList(new SelectQuery(columns, query.table, query.joins, pred));
    }

    @Override
    public List<IQuery> visit(InsertQuery query) {
        return Collections.singletonList(query);
    }

    @Override
    public List<IQuery> visit(UpdateQuery query) {
        List<ColumnValuePair> pairs = new ArrayList<>();
        for (ColumnValuePair pair : query.pairs) {
            pairs.add(new ColumnValuePair(pair.column.accept(this), pair.value));
        }
        IPredicate pred = query.predicate.accept(this);
        return Collections.singletonList(new UpdateQuery(query.table, pairs, pred));
    }

    @Override
    public List<IQuery> visit(DeleteQuery query) {
        IPredicate pred = query.predicate.accept(this);
        return Collections.singletonList(new DeleteQuery(query.table, pred));
    }

    @Override
    public List<IQuery> visit(CompoundInsertQuery query) {
        return Collections.singletonList(query);
    }

    @Override
    public List<IQuery> visit(CompoundDeleteQuery query) {
        List<String> tableList = model.getDeletionAssignment(query);
        IPredicate pred = query.predicate.accept(this);
        CompoundDeleteQuery newQuery = new CompoundDeleteQuery(query.table, query.joins, pred, tableList);
        return Collections.singletonList(newQuery);
    }

    @Override
    public List<IQuery> visit(CompoundUpdateQuery query) {
        List<ColumnValuePair> pairs = new ArrayList<>();
        for (ColumnValuePair pair : query.pairs) {
            pairs.add(new ColumnValuePair(pair.column.accept(this), pair.value));
        }
        IPredicate pred = query.predicate.accept(this);
        return Collections.singletonList(new CompoundUpdateQuery(query.table, query.joins, pairs, pred));
    }

    @Override
    public List<IQuery> visit(ChooseQuery query) {
        List<IQuery> queryList = model.getViewAssignment(query);
        List<IQuery> instantiatedQueries = new ArrayList<>();
        for (IQuery q : queryList) {
            instantiatedQueries.addAll(q.accept(this));
        }
        return instantiatedQueries;
    }

    @Override
    public ConstantValue visit(ConstantValue node) {
        return node;
    }

    @Override
    public ParameterValue visit(ParameterValue node) {
        return node;
    }

    @Override
    public ColumnValue visit(ColumnValue node) {
        ColumnRef columnRef = node.column.accept(this);
        return new ColumnValue(columnRef);
    }

    @Override
    public SubqueryValue visit(SubqueryValue node) {
        Subquery query = visitSubquery(node.query);
        return new SubqueryValue(query);
    }

    @Override
    public FreshValue visit(FreshValue node) {
        return node;
    }

    @Override
    public ColumnRef visit(ColumnRef node) {
        return node;
    }

    @Override
    public ColumnRef visit(ColumnHole node) {
        ColumnRef instantiation = model.getColumnAssignment(node);
        return instantiation;
    }

    @Override
    public AndPred visit(AndPred node) {
        IPredicate lhs = node.lhs.accept(this);
        IPredicate rhs = node.rhs.accept(this);
        return new AndPred(lhs, rhs);
    }

    @Override
    public OrPred visit(OrPred node) {
        IPredicate lhs = node.lhs.accept(this);
        IPredicate rhs = node.rhs.accept(this);
        return new OrPred(lhs, rhs);
    }

    @Override
    public NotPred visit(NotPred node) {
        IPredicate pred = node.predicate.accept(this);
        return new NotPred(pred);
    }

    @Override
    public OpPred visit(OpPred node) {
        IValue lhs = node.lhs.accept(this);
        IValue rhs = node.rhs.accept(this);
        return new OpPred(lhs, rhs, node.op);
    }

    @Override
    public InPred visit(InPred node) {
        IValue lhs = node.lhs.accept(this);
        Subquery rhs = visitSubquery(node.rhs);
        return new InPred(lhs, rhs);
    }

    private Subquery visitSubquery(Subquery node) {
        throw new UnsupportedOperationException();
    }

}
