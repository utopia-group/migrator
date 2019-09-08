package migrator.rewrite.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import migrator.rewrite.ast.AndPred;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.ConstantValue;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.FreshValue;
import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.ast.IPredicate;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.InPred;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.NotPred;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.Operator;
import migrator.rewrite.ast.OrPred;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.QueryList;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.Subquery;
import migrator.rewrite.ast.SubqueryValue;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.ast.UpdateQuery;
import migrator.rewrite.parser.RewriteAntlrDslParser.ColumnContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ColumnDefContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ColumnStmtContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.CompoundDeleteQueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.CompoundUpdateQueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.DeleteQueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.FkDefContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.InsertQueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.JoinContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.MappingContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.MethodContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PairContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ParameterContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PkDefContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredAndContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredEqContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredInContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredIneqContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredNegateContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredNotContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredOrContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredParenContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredPositiveContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.PredicateContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ProgramContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.QueryListContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.SchemaContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.SelectQueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.SetContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.SignatureContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.SubqueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.TableContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.TableDefContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.TableMappingContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.TupleContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.TupleListContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.UpdateQueryContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ValueContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ValueFreshContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ValueIdentifierContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ValueNumberContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ValueParameterContext;
import migrator.rewrite.parser.RewriteAntlrDslParser.ValueSubqueryContext;
import migrator.rewrite.program.Method;
import migrator.rewrite.program.MethodType;
import migrator.rewrite.program.Parameter;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;

/**
 * Parse tree visitor that generates AST nodes.
 * <p>
 * This class does not handle nodes within table definitions
 * (column, primary key, foreign key definitions);
 * see {@link TableDefVisitor} for the implementation of those nodes.
 */
public class AstGenVisitor extends AbstractParseTreeVisitor<IAstNode> implements RewriteAntlrDslVisitor<IAstNode> {

    @Override
    public QueryList visitQueryList(QueryListContext ctx) {
        return new QueryList(ctx.query().stream().map(queryCtx -> (IQuery) queryCtx.accept(this)).collect(Collectors.toList()));
    }

    @Override
    public SelectQuery visitSelectQuery(SelectQueryContext ctx) {
        return new SelectQuery(
                ctx.column().stream().map(colCtx -> (ColumnRef) colCtx.accept(this)).collect(Collectors.toList()),
                (TableRef) ctx.table().accept(this),
                ctx.join().stream().map(joinCtx -> (Join) joinCtx.accept(this)).collect(Collectors.toList()),
                Optional.ofNullable(ctx.predicate()).map(predCtx -> (IPredicate) predCtx.accept(this)).orElse(null));
    }

    @Override
    public InsertQuery visitInsertQuery(InsertQueryContext ctx) {
        TableRef tableRef = (TableRef) ctx.table().accept(this);
        List<ColumnValuePair> pairs = new ArrayList<>();
        if (ctx.column().size() != ctx.value().size()) {
            throw new IllegalArgumentException(String.format(
                    "number of columns (%d) does not match number of values (%d) in table (%s)",
                    ctx.column().size(), ctx.value().size(), tableRef.getTable()));
        }
        Iterator<ColumnContext> colIt = ctx.column().iterator();
        Iterator<ValueContext> valIt = ctx.value().iterator();
        while (colIt.hasNext() && valIt.hasNext()) {
            // columnRef.tableName may be null
            ColumnRef columnRef = (ColumnRef) colIt.next().accept(this);
            IValue value = (IValue) valIt.next().accept(this);
            pairs.add(new ColumnValuePair(columnRef, value));
        }
        assert colIt.hasNext() == false && valIt.hasNext() == false;
        return new InsertQuery(tableRef, pairs);
    }

    @Override
    public DeleteQuery visitDeleteQuery(DeleteQueryContext ctx) {
        return new DeleteQuery(
                (TableRef) ctx.table().accept(this),
                Optional.ofNullable(ctx.predicate()).map(predCtx -> (IPredicate) predCtx.accept(this)).orElse(null));
    }

    @Override
    public CompoundDeleteQuery visitCompoundDeleteQuery(CompoundDeleteQueryContext ctx) {
        int tableCount = ctx.table().size();
        List<String> tables = new ArrayList<>();
        for (int i = 0; i < tableCount - 1; ++i) {
            TableRef ref = (TableRef) ctx.table(i).accept(this);
            tables.add(ref.getReference());
        }
        TableRef tableRef = (TableRef) ctx.table(tableCount - 1).accept(this);
        List<Join> joins = ctx.join().stream().map(joinCtx -> (Join) joinCtx.accept(this)).collect(Collectors.toList());
        IPredicate pred = Optional.ofNullable(ctx.predicate()).map(predCtx -> (IPredicate) predCtx.accept(this)).orElse(null);
        return new CompoundDeleteQuery(tableRef, joins, pred, tables);
    }

    @Override
    public UpdateQuery visitUpdateQuery(UpdateQueryContext ctx) {
        return new UpdateQuery(
                (TableRef) ctx.table().accept(this),
                ctx.set().stream().map(setCtx -> (ColumnValuePair) setCtx.accept(this)).collect(Collectors.toList()),
                Optional.ofNullable(ctx.predicate()).map(predCtx -> (IPredicate) predCtx.accept(this)).orElse(null));
    }

    @Override
    public CompoundUpdateQuery visitCompoundUpdateQuery(CompoundUpdateQueryContext ctx) {
        TableRef tableRef = (TableRef) ctx.table().accept(this);
        List<Join> joins = ctx.join().stream().map(joinCtx -> (Join) joinCtx.accept(this)).collect(Collectors.toList());
        List<ColumnValuePair> pairs = new ArrayList<>();
        for (SetContext setCtx : ctx.set()) {
            // TODO: ensure all columns are qualified
            ColumnRef columnRef = (ColumnRef) setCtx.column().accept(this);
            IValue value = (IValue) setCtx.value().accept(this);
            pairs.add(new ColumnValuePair(columnRef, value));
        }
        IPredicate pred = Optional.ofNullable(ctx.predicate()).map(predCtx -> (IPredicate) predCtx.accept(this)).orElse(null);
        return new CompoundUpdateQuery(tableRef, joins, pairs, pred);
    }

    @Override
    public ColumnRef visitColumn(ColumnContext ctx) {
        return ColumnRef.from(ctx.ID().getText());
    }

    @Override
    public TableRef visitTable(TableContext ctx) {
        return new TableRef(ctx.name.getText(),
                Optional.ofNullable(ctx.reference).map(tok -> tok.getText()).orElse(null));
    }

    @Override
    public Join visitJoin(JoinContext ctx) {
        TableRef dest = (TableRef) ctx.table().accept(this);
        ColumnRef col1 = (ColumnRef) ctx.column(0).accept(this);
        ColumnRef col2 = (ColumnRef) ctx.column(1).accept(this);
        ColumnRef srcColumn, destColumn;
        if (col2.tableName.equals(dest.getReference())) {
            srcColumn = col1;
            destColumn = col2;
        } else if (col1.tableName.equals(dest.getReference())) {
            srcColumn = col2;
            destColumn = col1;
        } else {
            throw new IllegalArgumentException(String.format(
                    "join condition (%s = %s) does not involve destination table (%s)",
                    col1.toSqlString(), col2.toSqlString(), dest.toSqlString()));
        }
        return new Join(dest, srcColumn, destColumn);
    }

    @Override
    public ColumnValuePair visitSet(SetContext ctx) {
        return new ColumnValuePair(
                (ColumnRef) ctx.column().accept(this),
                (IValue) ctx.value().accept(this));
    }

    @Override
    public IPredicate visitPredicate(PredicateContext ctx) {
        return (IPredicate) ctx.predOr().accept(this);
    }

    @Override
    public IPredicate visitPredOr(PredOrContext ctx) {
        // unfortunately we don't have foldl1'
        Iterator<PredAndContext> it = ctx.predAnd().iterator();
        IPredicate lhs = (IPredicate) it.next().accept(this);
        while (it.hasNext())
            lhs = new OrPred(lhs, (IPredicate) it.next().accept(this));
        return lhs;
    }

    @Override
    public IPredicate visitPredAnd(PredAndContext ctx) {
        Iterator<PredNotContext> it = ctx.predNot().iterator();
        IPredicate lhs = (IPredicate) it.next().accept(this);
        while (it.hasNext())
            lhs = new AndPred(lhs, (IPredicate) it.next().accept(this));
        return lhs;
    }

    @Override
    public IPredicate visitPredPositive(PredPositiveContext ctx) {
        return (IPredicate) ctx.predTop().accept(this);
    }

    @Override
    public NotPred visitPredNegate(PredNegateContext ctx) {
        return new NotPred((IPredicate) ctx.predNot().accept(this));
    }

    @Override
    public IPredicate visitPredParen(PredParenContext ctx) {
        return (IPredicate) ctx.predicate().accept(this);
    }

    @Override
    public OpPred visitPredIneq(PredIneqContext ctx) {
        return new OpPred(
                (IValue) ctx.value(0).accept(this),
                (IValue) ctx.value(1).accept(this),
                Operator.fromSqlString(ctx.INEQ().getText()));
    }

    @Override
    public OpPred visitPredEq(PredEqContext ctx) {
        return new OpPred(
                (IValue) ctx.value(0).accept(this),
                (IValue) ctx.value(1).accept(this),
                Operator.EQ);
    }

    @Override
    public InPred visitPredIn(PredInContext ctx) {
        return new InPred(
                (IValue) ctx.value().accept(this),
                (Subquery) ctx.subquery().accept(this));
    }

    @Override
    public ParameterValue visitValueParameter(ValueParameterContext ctx) {
        // preserve syntax but remove whitespace
        return new ParameterValue(ctx.ID().getText());
    }

    @Override
    public ColumnValue visitValueIdentifier(ValueIdentifierContext ctx) {
        return new ColumnValue(ColumnRef.from(ctx.ID().getText()));
    }

    @Override
    public ConstantValue visitValueNumber(ValueNumberContext ctx) {
        return new ConstantValue(ctx.NUM().getText());
    }

    @Override
    public FreshValue visitValueFresh(ValueFreshContext ctx) {
        return new FreshValue(Integer.parseInt(ctx.NUM().getText()));
    }

    @Override
    public SubqueryValue visitValueSubquery(ValueSubqueryContext ctx) {
        return new SubqueryValue((Subquery) ctx.subquery().accept(this));
    }

    @Override
    public Subquery visitSubquery(SubqueryContext ctx) {
        return new Subquery(
                (ColumnRef) ctx.column().accept(this),
                (TableRef) ctx.table().accept(this),
                ctx.join().stream().map(joinCtx -> (Join) joinCtx.accept(this)).collect(Collectors.toList()),
                Optional.ofNullable(ctx.predicate()).map(predCtx -> (IPredicate) predCtx.accept(this)).orElse(null));
    }

    @Override
    public SchemaDef visitSchema(SchemaContext ctx) {
        return new SchemaDef(
                ctx.tableDef().stream().map(tableDefCtx -> (TableDef) tableDefCtx.accept(this)).collect(Collectors.toList()));
    }

    @Override
    public TableDef visitTableDef(TableDefContext ctx) {
        TableDefVisitor tableVisitor = new TableDefVisitor(ctx.tableName.getText());
        for (ColumnStmtContext columnStmtCtx : ctx.columnStmt()) {
            columnStmtCtx.accept(tableVisitor);
        }
        return tableVisitor.getTableDef();
    }

    @Override
    public IAstNode visitColumnDef(ColumnDefContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitPkDef(PkDefContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitFkDef(FkDefContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitTuple(TupleContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitPair(PairContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitTupleList(TupleListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitMapping(MappingContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAstNode visitTableMapping(TableMappingContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SqlProgram visitProgram(ProgramContext ctx) {
        return new SqlProgram(ctx.method().stream().map(this::visitMethod).collect(Collectors.toList()));
    }

    @Override
    public Method visitSignature(SignatureContext ctx) {
        MethodType type = MethodType.fromSqlString(ctx.type.getText());
        return new Method(ctx.name.getText(), ctx.parameter().stream().map(this::visitParameter).collect(Collectors.toList()), type);
    }

    @Override
    public MethodImplementation visitMethod(MethodContext ctx) {
        return new MethodImplementation(this.visitSignature(ctx.signature()), this.visitQueryList(ctx.queryList()).queries);
    }

    @Override
    public Parameter visitParameter(ParameterContext ctx) {
        return new Parameter(ctx.name.getText(), ctx.type.getText());
    }

}
