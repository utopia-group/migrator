package migrator.corr;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.UpdateQuery;

public class InsertValueExtractVisitor implements IQueryVisitor<Map<ColumnRef, IValue>> {

    @Override
    public Map<ColumnRef, IValue> visit(SelectQuery query) {
        return Collections.emptyMap();
    }

    @Override
    public Map<ColumnRef, IValue> visit(InsertQuery query) {
        Map<ColumnRef, IValue> ret = new HashMap<>();
        for (ColumnValuePair pair : query.pairs) {
            if (pair.column instanceof ColumnRef) {
                ColumnRef columnRef = (ColumnRef) pair.column;
                ret.put(columnRef, pair.value);
            }
        }
        return ret;
    }

    @Override
    public Map<ColumnRef, IValue> visit(UpdateQuery query) {
        return Collections.emptyMap();
    }

    @Override
    public Map<ColumnRef, IValue> visit(DeleteQuery query) {
        return Collections.emptyMap();
    }

    @Override
    public Map<ColumnRef, IValue> visit(CompoundInsertQuery query) {
        Map<ColumnRef, IValue> ret = new HashMap<>();
        for (InsertQuery ins : query.insertions) {
            Map<ColumnRef, IValue> map = ins.accept(this);
            for (ColumnRef key : map.keySet()) {
                ret.put(key, map.get(key));
            }
        }
        return ret;
    }

    @Override
    public Map<ColumnRef, IValue> visit(CompoundDeleteQuery query) {
        return Collections.emptyMap();
    }

    @Override
    public Map<ColumnRef, IValue> visit(CompoundUpdateQuery query) {
        return Collections.emptyMap();
    }

    @Override
    public Map<ColumnRef, IValue> visit(ChooseQuery query) {
        Map<ColumnRef, IValue> ret = new HashMap<>();
        for (List<IQuery> queryList : query.queryLists) {
            for (IQuery q : queryList) {
                Map<ColumnRef, IValue> map = q.accept(this);
                for (ColumnRef key : map.keySet()) {
                    ret.put(key, map.get(key));
                }
            }
        }
        return ret;
    }

}
