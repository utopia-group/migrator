package migrator.post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import migrator.bench.BenchmarkAST.AndPredNode;
import migrator.bench.BenchmarkAST.AstNode;
import migrator.bench.BenchmarkAST.AstTerm;
import migrator.bench.BenchmarkAST.AttrListNode;
import migrator.bench.BenchmarkAST.DeleteNode;
import migrator.bench.BenchmarkAST.EquiJoinNode;
import migrator.bench.BenchmarkAST.InPredNode;
import migrator.bench.BenchmarkAST.InsertNode;
import migrator.bench.BenchmarkAST.LopPredNode;
import migrator.bench.BenchmarkAST.NotPredNode;
import migrator.bench.BenchmarkAST.OrPredNode;
import migrator.bench.BenchmarkAST.Predicate;
import migrator.bench.BenchmarkAST.ProjectNode;
import migrator.bench.BenchmarkAST.RelationNode;
import migrator.bench.BenchmarkAST.SelectNode;
import migrator.bench.BenchmarkAST.TupleNode;
import migrator.bench.BenchmarkAST.UpdateNode;
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
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.IValueVisitor;
import migrator.rewrite.ast.InPred;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.NotPred;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.OrPred;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.Subquery;
import migrator.rewrite.ast.SubqueryValue;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.ast.UpdateQuery;

/**
 * A visitor for translating SQL programs to the format of Mediator.
 */
public final class MediatorTranslateVisitor implements
        IQueryVisitor<List<AstNode>>, IPredVisitor<Predicate>,
        IColumnVisitor<String>, IValueVisitor<String> {
    /**
     * Underlying database schema.
     */
    private SchemaDef schema;

    public MediatorTranslateVisitor(SchemaDef schema) {
        this.schema = schema;
    }

    @Override
    public List<AstNode> visit(SelectQuery query) {
        AttrListNode attrList = new AttrListNode(query.columns.stream()
                .map(column -> column.accept(this))
                .collect(Collectors.toList()));
        AstTerm relation = translateTableRef(query.table);
        for (Join join : query.joins) {
            // Note that only LHS and RHS of EquiJoinNode are useful for now
            RelationNode rhs = translateTableRef(join.getDest());
            relation = new EquiJoinNode(relation, rhs, null, null, null, null);
        }
        Predicate pred = query.predicate.accept(this);
        SelectNode filter = new SelectNode(pred, relation);
        ProjectNode proj = new ProjectNode(attrList, filter);
        return Collections.singletonList(proj);
    }

    @Override
    public List<AstNode> visit(InsertQuery query) {
        RelationNode table = translateTableRef(query.table);
        Map<String, IValue> columnNameToValues = new HashMap<>();
        for (ColumnValuePair pair : query.pairs) {
            String columnName = pair.column.accept(this);
            columnNameToValues.put(columnName, pair.value);
        }
        TableDef tableDef = schema.tables.get(query.table.getTable());
        // order of columnNames are supposed to be same as those in the definition
        List<String> values = tableDef.columnNames.stream()
                .map(columnName -> columnNameToValues.get(MediatorTranslator.translateColumn(tableDef.name, columnName)))
                .map(value -> value.accept(this))
                .collect(Collectors.toList());
        TupleNode tuple = new TupleNode(values);
        InsertNode ins = new InsertNode(table, tuple);
        return Collections.singletonList(ins);
    }

    @Override
    public List<AstNode> visit(UpdateQuery query) {
        if (query.pairs.size() != 1) {
            throw new IllegalArgumentException("Mutiple columns to update in: " + query.toSqlString());
        }
        RelationNode table = translateTableRef(query.table);
        Predicate pred = query.predicate.accept(this);
        String attr = query.pairs.get(0).column.accept(this);
        String value = query.pairs.get(0).value.accept(this);
        UpdateNode upd = new UpdateNode(table, pred, attr, value);
        return Collections.singletonList(upd);
    }

    @Override
    public List<AstNode> visit(DeleteQuery query) {
        RelationNode table = translateTableRef(query.table);
        Predicate pred = query.predicate.accept(this);
        DeleteNode del = new DeleteNode(table, pred);
        return Collections.singletonList(del);
    }

    @Override
    public List<AstNode> visit(CompoundInsertQuery query) {
        return query.insertions.stream()
                .flatMap(ins -> ins.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<AstNode> visit(CompoundDeleteQuery query) {
        assert !query.joins.isEmpty();
        if (canSplitJoinedDelete(query)) {
            return splitJoinedDelete(query);
        }
        List<AstNode> dels = new ArrayList<>();
        String innerTableName = getInnerTableName(query);
        RelationNode innerTable = new RelationNode(innerTableName, null);
        Predicate pred = query.predicate.accept(this);
        for (Join join : query.joins) {
            String mainTableName;
            if (join.getSrcColumn().tableName.equals(innerTableName)) {
                mainTableName = join.getDestColumn().tableName;
            } else if (join.getDestColumn().tableName.equals(innerTableName)) {
                mainTableName = join.getSrcColumn().tableName;
            } else {
                throw new TranslationException(query.toSqlString());
            }
            RelationNode table = new RelationNode(mainTableName, null);
            InPredNode inPred = genInPredNode(join, pred, mainTableName);
            dels.add(new DeleteNode(table, inPred));
        }
        dels.add(new DeleteNode(innerTable, pred));
        return dels;
    }

    private boolean canSplitJoinedDelete(CompoundDeleteQuery query) {
        if (query.joins.size() == 1) {
            Join join = query.joins.get(0);
            ColumnRef predColumn = PostprocessUtil.getOpPredLhsColumn(query.predicate);
            return predColumn.equals(join.getSrcColumn()) || predColumn.equals(join.getDestColumn());
        }
        return false;
    }

    private List<AstNode> splitJoinedDelete(CompoundDeleteQuery query) {
        if (!(query.predicate instanceof OpPred && query.joins.size() == 1)) {
            throw new IllegalArgumentException();
        }
        OpPred pred = (OpPred) query.predicate;
        Join join = query.joins.get(0);
        List<AstNode> dels = new ArrayList<>();
        // joined table
        {
            RelationNode table = new RelationNode(join.getDest().getTable(), null);
            IValue lhs = new ColumnValue(join.getDestColumn());
            OpPred newPred = new OpPred(lhs, pred.rhs, pred.op);
            dels.add(new DeleteNode(table, newPred.accept(this)));
        }
        // main table
        {
            RelationNode table = new RelationNode(query.table.getTable(), null);
            IValue lhs = new ColumnValue(join.getSrcColumn());
            OpPred newPred = new OpPred(lhs, pred.rhs, pred.op);
            dels.add(new DeleteNode(table, newPred.accept(this)));
        }
        return dels;
    }

    @Override
    public List<AstNode> visit(CompoundUpdateQuery query) {
        if (!(query.joins.size() == 1 && query.pairs.size() == 1)) {
            throw new IllegalArgumentException("Verifier does not support: " + query.toSqlString());
        }
        Predicate pred = query.predicate.accept(this);
        ColumnValuePair pair = query.pairs.get(0);
        assert pair.column instanceof ColumnRef;
        String mainTableName = ((ColumnRef) pair.column).tableName;
        RelationNode table = new RelationNode(mainTableName, null);
        InPredNode inPred = genInPredNode(query.joins.get(0), pred, mainTableName);
        String attr = pair.column.accept(this);
        String value = pair.value.accept(this);
        UpdateNode upd = new UpdateNode(table, inPred, attr, value);
        return Collections.singletonList(upd);
    }

    @Override
    public List<AstNode> visit(ChooseQuery query) {
        throw new UnsupportedOperationException("Program is not concrete: " + query.toSqlString());
    }

    @Override
    public LopPredNode visit(OpPred node) {
        String lhs = node.lhs.accept(this);
        String rhs = node.rhs.accept(this);
        String op = translateOperatorString(node.op.toSqlString());
        return new LopPredNode(op, lhs, rhs);
    }

    @Override
    public AndPredNode visit(AndPred node) {
        Predicate lhs = node.lhs.accept(this);
        Predicate rhs = node.rhs.accept(this);
        return new AndPredNode(lhs, rhs);
    }

    @Override
    public OrPredNode visit(OrPred node) {
        Predicate lhs = node.lhs.accept(this);
        Predicate rhs = node.rhs.accept(this);
        return new OrPredNode(lhs, rhs);
    }

    @Override
    public NotPredNode visit(NotPred node) {
        Predicate pred = node.predicate.accept(this);
        return new NotPredNode(pred);
    }

    @Override
    public InPredNode visit(InPred node) {
        String lhs = node.lhs.accept(this);
        AstTerm rhs = visitSubquery(node.rhs);
        return new InPredNode(lhs, rhs);
    }

    @Override
    public String visit(ColumnRef node) {
        return MediatorTranslator.translateColumn(node.tableName, node.column);
    }

    @Override
    public String visit(ColumnHole node) {
        throw new UnsupportedOperationException("Program is not concrete: " + node.toSqlString());
    }

    @Override
    public String visit(ConstantValue node) {
        return node.value;
    }

    @Override
    public String visit(FreshValue node) {
        return "UUID_x" + node.index;
    }

    @Override
    public String visit(ParameterValue node) {
        return node.name;
    }

    @Override
    public String visit(ColumnValue node) {
        return node.column.accept(this).toString();
    }

    @Override
    public String visit(SubqueryValue node) {
        return visitSubquery(node.query).toString();
    }

    private AstTerm visitSubquery(Subquery subquery) {
        throw new UnsupportedOperationException();
    }

    private RelationNode translateTableRef(TableRef tableRef) {
        return new RelationNode(tableRef.getTable(), null);
    }

    private String translateOperatorString(String op) {
        if (op.equals("<>")) {
            return "!=";
        } else {
            return op;
        }
    }

    private InPredNode genInPredNode(Join join, Predicate pred, String mainTable) {
        if (join.getSrcColumn().tableName == null || join.getDestColumn().tableName == null) {
            throw new IllegalArgumentException(String.format("Unqualified column in join: %s", join.toSqlString()));
        }
        ColumnRef mainColumn;
        ColumnRef innerColumn;
        if (join.getSrcColumn().tableName.equals(mainTable)) {
            mainColumn = join.getSrcColumn();
            innerColumn = join.getDestColumn();
        } else if (join.getDestColumn().tableName.equals(mainTable)) {
            innerColumn = join.getSrcColumn();
            mainColumn = join.getDestColumn();
        } else {
            throw new TranslationException(String.format("Cannot find table (%s) in join (%s)", mainTable, join.toSqlString()));
        }
        RelationNode table = new RelationNode(innerColumn.tableName, null);
        AttrListNode attrList = new AttrListNode(Collections.singletonList(
                MediatorTranslator.translateColumn(innerColumn.tableName, innerColumn.column)));
        SelectNode filter = new SelectNode(pred, table);
        ProjectNode proj = new ProjectNode(attrList, filter);
        return new InPredNode(MediatorTranslator.translateColumn(mainColumn.tableName, mainColumn.column), proj);
    }

    private String getInnerTableName(CompoundDeleteQuery query) {
        if (query.predicate instanceof OpPred) {
            IValue lhs = ((OpPred) query.predicate).lhs;
            assert lhs instanceof ColumnValue;
            ColumnValue columnValue = (ColumnValue) lhs;
            assert columnValue.column instanceof ColumnRef;
            return ((ColumnRef) columnValue.column).tableName;
        } else {
            throw new RuntimeException("Unknown predicate type: " + query.predicate);
        }
    }
}
