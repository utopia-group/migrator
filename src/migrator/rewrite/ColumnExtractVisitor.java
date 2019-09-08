package migrator.rewrite;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IPredVisitor;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
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

/**
 * A visitor that extracts all columns used in the program construct.
 * <p>
 * This visitor does not modify the visited objects.
 */
public final class ColumnExtractVisitor implements
        IQueryVisitor<Set<ColumnRef>>, IPredVisitor<Set<ColumnRef>>,
        IColumnVisitor<Set<ColumnRef>>, IValueVisitor<Set<ColumnRef>> {

    @Override
    public Set<ColumnRef> visit(SelectQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        ret.addAll(query.columns.stream()
                .flatMap(x -> x.accept(this).stream())
                .collect(Collectors.toSet()));
        ret.addAll(query.predicate.accept(this));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(InsertQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (ColumnValuePair pair : query.pairs) {
            ret.addAll(pair.column.accept(this));
        }
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(DeleteQuery query) {
        // does not include all columns in the table
        return query.predicate.accept(this);
    }

    @Override
    public Set<ColumnRef> visit(UpdateQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (ColumnValuePair pair : query.pairs) {
            ret.addAll(pair.column.accept(this));
        }
        ret.addAll(query.predicate.accept(this));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(CompoundInsertQuery query) {
        return query.insertions.stream()
                .flatMap(ins -> ins.accept(this).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ColumnRef> visit(CompoundDeleteQuery query) {
        // does not include all columns in the table list
        return query.predicate.accept(this);
    }

    @Override
    public Set<ColumnRef> visit(CompoundUpdateQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (ColumnValuePair pair : query.pairs) {
            ret.addAll(pair.column.accept(this));
        }
        ret.addAll(query.predicate.accept(this));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(ChooseQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (List<IQuery> queryList : query.queryLists) {
            for (IQuery q : queryList) {
                ret.addAll(q.accept(this));
            }
        }
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(AndPred node) {
        Set<ColumnRef> ret = new HashSet<>();
        ret.addAll(node.lhs.accept(this));
        ret.addAll(node.rhs.accept(this));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(OrPred node) {
        Set<ColumnRef> ret = new HashSet<>();
        ret.addAll(node.lhs.accept(this));
        ret.addAll(node.rhs.accept(this));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(NotPred node) {
        return node.accept(this);
    }

    @Override
    public Set<ColumnRef> visit(OpPred node) {
        Set<ColumnRef> ret = new HashSet<>();
        ret.addAll(node.lhs.accept(this));
        ret.addAll(node.rhs.accept(this));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(InPred node) {
        Set<ColumnRef> ret = new HashSet<>();
        ret.addAll(node.lhs.accept(this));
        ret.addAll(visitSubquery(node.rhs));
        return ret;
    }

    @Override
    public Set<ColumnRef> visit(ColumnRef node) {
        return Collections.singleton(node);
    }

    @Override
    public Set<ColumnRef> visit(ColumnHole node) {
        return node.candidates.stream()
                .flatMap(column -> column.accept(this).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ColumnRef> visit(ConstantValue node) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(ParameterValue node) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(ColumnValue node) {
        return node.column.accept(this);
    }

    @Override
    public Set<ColumnRef> visit(SubqueryValue node) {
        return visitSubquery(node.query);
    }

    @Override
    public Set<ColumnRef> visit(FreshValue node) {
        return Collections.emptySet();
    }

    private Set<ColumnRef> visitSubquery(Subquery node) {
        Set<ColumnRef> ret = new HashSet<>();
        ret.add(node.column);
        ret.addAll(node.predicate.accept(this));
        return ret;
    }
}
