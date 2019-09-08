package migrator.rewrite.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import migrator.rewrite.ast.ColumnType;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.parser.RewriteAntlrDslParser.*;

/**
 * A visitor used by {@link AstGenVisitor}
 * to accumulate a table definition.
 * This class only handles
 * column, primary key, and foreign key definitions;
 * other nodes will cause a {@link UnsupportedOperationException}
 * to be thrown.
 * <p>
 * Use this class by visiting the definitions <em>within</em>
 * the table definition (not the table definition itself)
 * in order, then using {@link #getTableDef()} to retrieve the result.
 *
 * @see AstGenVisitor
 */
public class TableDefVisitor extends AbstractParseTreeVisitor<Void> implements RewriteAntlrDslVisitor<Void> {
    // accumulated by visiting
    private TableDef tableDef;

    /**
     * Construct a new visitor for a table with the given name.
     * @param name the name of the table
     */
    public TableDefVisitor(String name) {
        tableDef = new TableDef(name, new ArrayList<>(), new HashMap<>(), null, new HashMap<>());
    }

    /**
     * Returns the accumulated table definition.
     * @return the accumulated table definition
     */
    public TableDef getTableDef() {
        tableDef.canonicalColumnNames = tableDef.columns.keySet().stream()
                .sorted().collect(Collectors.toList());
        return tableDef;
    }

    @Override
    public Void visitQueryList(QueryListContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitSelectQuery(SelectQueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitInsertQuery(InsertQueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitDeleteQuery(DeleteQueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitCompoundDeleteQuery(CompoundDeleteQueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitUpdateQuery(UpdateQueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitCompoundUpdateQuery(CompoundUpdateQueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitColumn(ColumnContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitTable(TableContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitJoin(JoinContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitSet(SetContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredicate(PredicateContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredOr(PredOrContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredAnd(PredAndContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredPositive(PredPositiveContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredNegate(PredNegateContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredParen(PredParenContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredIneq(PredIneqContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredEq(PredEqContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPredIn(PredInContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitValueParameter(ValueParameterContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitValueIdentifier(ValueIdentifierContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitValueNumber(ValueNumberContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitValueSubquery(ValueSubqueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitValueFresh(ValueFreshContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitSubquery(SubqueryContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitSchema(SchemaContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitTableDef(TableDefContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitTuple(TupleContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitPair(PairContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitTupleList(TupleListContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitMapping(MappingContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitTableMapping(TableMappingContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitColumnDef(ColumnDefContext ctx) {
        String columnName = ctx.name.getText();
        tableDef.columnNames.add(columnName);
        tableDef.columns.put(columnName, new ColumnType(ctx.type.getText()));
        return null;
    }

    @Override
    public Void visitPkDef(PkDefContext ctx) {
        String newPK = ctx.columnName.getText();
        if (tableDef.primaryKey != null) {
            throw new IllegalArgumentException(String.format("duplicate primary key: %s (was %s)", newPK, tableDef.primaryKey));
        }
        tableDef.primaryKey = newPK;
        return null;
    }

    @Override
    public Void visitFkDef(FkDefContext ctx) {
        tableDef.foreignKeys.put(ctx.columnName.getText(),
                new ForeignKeyDef(ctx.destTable.getText(), ctx.destColumn.getText()));
        return null;
    }

    @Override
    public Void visitProgram(ProgramContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitSignature(SignatureContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitMethod(MethodContext ctx) { throw new UnsupportedOperationException(); }

    @Override
    public Void visitParameter(ParameterContext ctx) { throw new UnsupportedOperationException(); }
}
