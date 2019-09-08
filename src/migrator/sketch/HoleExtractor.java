package migrator.sketch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.ast.IColumn;
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IPredVisitor;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValueVisitor;
import migrator.rewrite.ast.InPred;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.Join;
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
import migrator.util.ListMultiMap;
import migrator.util.PermUtil;

/**
 * A visitor for extracting holes from a sketch.
 * <p>
 * This visitor does not modify the original sketch.
 */
public class HoleExtractor
        implements IQueryVisitor<Void>, IPredVisitor<Void>, IColumnVisitor<Void>, IValueVisitor<Void> {
    /**
     * A SQL program possibly with holes
     */
    public SqlProgram sketchProgram;
    /**
     * view hole -> list of candidates
     */
    private final ListMultiMap<ChooseQuery, List<IQuery>> joinHoles = new ListMultiMap<>();
    /**
     * table list hole in compound deletion -> list of candidates
     */
    private final ListMultiMap<CompoundDeleteQuery, List<String>> deletionHoles = new ListMultiMap<>();
    /**
     * column hole -> list of candidates
     */
    private final ListMultiMap<ColumnHole, ColumnRef> columnHoles = new ListMultiMap<>();
    /**
     * join/deletion/column hole -> method index
     */
    private final Map<IAstNode, Integer> holeToMethodIndex = new HashMap<>();
    /**
     * current method index that the visitor is working on
     */
    private int currMethodIndex = 0;

    public HoleExtractor(SqlProgram sketchProgram) {
        this.sketchProgram = sketchProgram;
    }

    /**
     * Extract holes from the sketch program.
     */
    public void extract() {
        for (int i = 0; i < sketchProgram.methods.size(); ++i) {
            currMethodIndex = i;
            MethodImplementation method = sketchProgram.methods.get(i);
            for (IQuery query : method.queries) {
                query.accept(this);
            }
        }
    }

    /**
     * Find all join chain holes in the query list. Must be called after
     * {@link HoleExtractor#extract()}.
     *
     * @return a map from join holes to candidates
     */
    public ListMultiMap<ChooseQuery, List<IQuery>> getJoinHoles() {
        return joinHoles;
    }

    /**
     * Find all holes in compound deletion queries. Must be called after
     * {@link HoleExtractor#extract()}.
     *
     * @return a map from holes to candidate table lists
     */
    public ListMultiMap<CompoundDeleteQuery, List<String>> getDeletionHoles() {
        return deletionHoles;
    }

    /**
     * Find all column holes in the query list. Must be called after
     * {@link HoleExtractor#extract()}.
     *
     * @return a map from column holes to candidates
     */
    public ListMultiMap<ColumnHole, ColumnRef> getColumnHoles() {
        return columnHoles;
    }

    /**
     * Find the mapping from holes to their method indices. Must be called after
     * {@link HoleExtractor#extract()}.
     *
     * @return a map from holes to candidate table lists
     */
    public Map<IAstNode, Integer> getHoleToMethodIndex() {
        return holeToMethodIndex;
    }

    @Override
    public Void visit(SelectQuery query) {
        for (IColumn column : query.columns) {
            column.accept(this);
        }
        for (Join join : query.joins) {
            visitJoin(join);
        }
        query.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(InsertQuery query) {
        for (ColumnValuePair pair : query.pairs) {
            pair.column.accept(this);
            pair.value.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(UpdateQuery query) {
        for (ColumnValuePair pair : query.pairs) {
            pair.column.accept(this);
            pair.value.accept(this);
        }
        query.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(DeleteQuery query) {
        query.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(CompoundInsertQuery query) {
        for (InsertQuery ins : query.insertions) {
            ins.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(CompoundDeleteQuery query) {
        assert query.table != null;
        // get all tables used in the deletion
        List<String> tableNames = new ArrayList<>();
        tableNames.add(query.table.getTable());
        for (Join join : query.joins) {
            tableNames.add(join.getDest().getTable());
        }
        // generate the power set of all tables
        List<List<String>> tableNameLists = PermUtil.nonEmptyPowerSet(tableNames);
        // update the map for deletion holes
        deletionHoles.putAll(query, tableNameLists);
        // update the map for method index
        assert !holeToMethodIndex.containsKey(query) : "duplicated key: " + query;
        holeToMethodIndex.put(query, currMethodIndex);

        // visit it children
        for (Join join : query.joins) {
            visitJoin(join);
        }
        query.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(CompoundUpdateQuery query) {
        for (Join join : query.joins) {
            visitJoin(join);
        }
        for (ColumnValuePair pair : query.pairs) {
            pair.column.accept(this);
        }
        query.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(ChooseQuery query) {
        // update the map for join chain holes
        joinHoles.putAll(query, query.queryLists);
        // update the map for method index
        assert !holeToMethodIndex.containsKey(query) : "duplicated key: " + query;
        holeToMethodIndex.put(query, currMethodIndex);

        // visit its children
        for (List<IQuery> queryList : query.queryLists) {
            for (IQuery q : queryList) {
                q.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(AndPred node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        return null;
    }

    @Override
    public Void visit(OrPred node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        return null;
    }

    @Override
    public Void visit(NotPred node) {
        node.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(OpPred node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        return null;
    }

    @Override
    public Void visit(InPred node) {
        node.lhs.accept(this);
        visitSubquery(node.rhs);
        return null;
    }

    @Override
    public Void visit(ColumnRef node) {
        return null;
    }

    @Override
    public Void visit(ColumnHole node) {
        // update the map for column holes
        columnHoles.putAll(node, node.candidates);
        // update the map for method index
        assert !holeToMethodIndex.containsKey(node) : "duplicated key: " + node;
        holeToMethodIndex.put(node, currMethodIndex);
        return null;
    }

    @Override
    public Void visit(ConstantValue node) {
        return null;
    }

    @Override
    public Void visit(ParameterValue node) {
        return null;
    }

    @Override
    public Void visit(ColumnValue node) {
        node.column.accept(this);
        return null;
    }

    @Override
    public Void visit(SubqueryValue node) {
        visitSubquery(node.query);
        return null;
    }

    @Override
    public Void visit(FreshValue node) {
        return null;
    }

    private void visitJoin(Join node) {
        // do nothing
    }

    private void visitSubquery(Subquery node) {
        throw new UnsupportedOperationException();
    }
}
