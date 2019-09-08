package migrator.corr;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.UpdateQuery;

/**
 * A visitor that extract all queried columns in the program.
 */
public class QueryColumnExtractVisitor
        implements IQueryVisitor<Set<ColumnRef>>, IColumnVisitor<Set<ColumnRef>> {

    @Override
    public Set<ColumnRef> visit(SelectQuery query) {
        return query.columns.stream()
                .flatMap(column -> column.accept(this).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ColumnRef> visit(InsertQuery query) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(UpdateQuery query) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(DeleteQuery query) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(CompoundInsertQuery query) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(CompoundDeleteQuery query) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(CompoundUpdateQuery query) {
        return Collections.emptySet();
    }

    @Override
    public Set<ColumnRef> visit(ChooseQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ColumnRef> visit(ColumnRef node) {
        return Collections.singleton(node);
    }

    @Override
    public Set<ColumnRef> visit(ColumnHole node) {
        throw new UnsupportedOperationException();
    }

}
