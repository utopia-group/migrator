package migrator.rewrite;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.UpdateQuery;
import migrator.rewrite.parser.AntlrParser;

public class ResolveColumnVisitorTest {

    private SchemaDef schema;
    private ResolveColumnVisitor resolver;

    @Before
    public void init() {
        String schemaText = ""
                + "CREATE TABLE A ("
                + "    a INT,"
                + "    b INT,"
                + "    c INT,"
                + "    FOREIGN KEY (c) REFERENCES B (d),"
                + "    PRIMARY KEY (a));"
                + "CREATE TABLE B ("
                + "    d INT,"
                + "    e INT,"
                + "    PRIMARY KEY (d));";
        schema = AntlrParser.parseSchema(schemaText);
        resolver = new ResolveColumnVisitor(schema);
    }

    @Test
    public void testResolveSelectQuery() {
        String text = "SELECT a, b, c, d, e FROM A JOIN B ON A.c = B.d WHERE e = <param>;";
        SelectQuery query = (SelectQuery) AntlrParser.parseQuery(text);
        query.accept(resolver);
        List<ColumnRef> columns = query.columns.stream().map(x -> (ColumnRef) x).collect(Collectors.toList());
        Assert.assertEquals("A", columns.get(0).tableName);
        Assert.assertEquals("A", columns.get(1).tableName);
        Assert.assertEquals("A", columns.get(2).tableName);
        Assert.assertEquals("B", columns.get(3).tableName);
        Assert.assertEquals("B", columns.get(4).tableName);
        ColumnRef predLhs = getLhsColumnRefFromOpPred((OpPred) query.predicate);
        Assert.assertEquals("B", predLhs.tableName);
    }

    @Test
    public void testResolveInsertQuery() {
        String text = "INSERT INTO A (a, b, c) VALUES (<a>, <b>, <c>);";
        InsertQuery query = (InsertQuery) AntlrParser.parseQuery(text);
        query.accept(resolver);
        List<ColumnRef> columns = query.pairs.stream().map(x -> (ColumnRef) x.column).collect(Collectors.toList());
        Assert.assertEquals("A", columns.get(0).tableName);
        Assert.assertEquals("A", columns.get(1).tableName);
        Assert.assertEquals("A", columns.get(2).tableName);
    }

    @Test
    public void testResolveDeleteQuery() {
        String text = "DELETE FROM A WHERE a = <param>;";
        DeleteQuery query = (DeleteQuery) AntlrParser.parseQuery(text);
        query.accept(resolver);
        ColumnRef predLhs = getLhsColumnRefFromOpPred((OpPred) query.predicate);
        Assert.assertEquals("A", predLhs.tableName);
    }

    @Test
    public void testResolveCompoundDeleteQuery() {
        String text = "DELETE A, B FROM A JOIN B ON A.c = B.d WHERE e = <param>;";
        CompoundDeleteQuery query = (CompoundDeleteQuery) AntlrParser.parseQuery(text);
        query.accept(resolver);
        ColumnRef predLhs = getLhsColumnRefFromOpPred((OpPred) query.predicate);
        Assert.assertEquals("B", predLhs.tableName);
    }

    @Test
    public void testResolveUpdateQuery() {
        String text = "UPDATE A SET b = <b>, c = <c> WHERE a = <a>;";
        UpdateQuery query = (UpdateQuery) AntlrParser.parseQuery(text);
        query.accept(resolver);
        List<ColumnRef> columns = query.pairs.stream().map(x -> (ColumnRef) x.column).collect(Collectors.toList());
        Assert.assertEquals("A", columns.get(0).tableName);
        Assert.assertEquals("A", columns.get(1).tableName);
        ColumnRef predLhs = getLhsColumnRefFromOpPred((OpPred) query.predicate);
        Assert.assertEquals("A", predLhs.tableName);
    }

    @Test
    public void testResolveCompoundUpdateQuery() {
        String text = "UPDATE A JOIN B ON A.c = B.d SET b = <b> WHERE e = <e>;";
        CompoundUpdateQuery query = (CompoundUpdateQuery) AntlrParser.parseQuery(text);
        query.accept(resolver);
        List<ColumnRef> columns = query.pairs.stream().map(x -> (ColumnRef) x.column).collect(Collectors.toList());
        Assert.assertEquals("A", columns.get(0).tableName);
        ColumnRef predLhs = getLhsColumnRefFromOpPred((OpPred) query.predicate);
        Assert.assertEquals("B", predLhs.tableName);
    }

    private ColumnRef getLhsColumnRefFromOpPred(OpPred pred) {
        return (ColumnRef) ((ColumnValue) pred.lhs).column;
    }
}
