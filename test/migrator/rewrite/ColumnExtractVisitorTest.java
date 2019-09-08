package migrator.rewrite;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.UpdateQuery;
import migrator.rewrite.parser.AntlrParser;

public class ColumnExtractVisitorTest {

    @Test
    public void testExtractSelectQuery() {
        String text = "SELECT a, b, c, d FROM A JOIN B ON A.c = B.d WHERE e = <param>;";
        SelectQuery query = (SelectQuery) AntlrParser.parseQuery(text);
        Set<ColumnRef> columns = query.accept(new ColumnExtractVisitor());
        Assert.assertTrue(columns.contains(new ColumnRef("a")));
        Assert.assertTrue(columns.contains(new ColumnRef("b")));
        Assert.assertTrue(columns.contains(new ColumnRef("c")));
        Assert.assertTrue(columns.contains(new ColumnRef("d")));
        Assert.assertTrue(columns.contains(new ColumnRef("e")));
    }

    @Test
    public void testExtractInsertQuery() {
        String text = "INSERT INTO A (a, b, c) VALUES (<a>, <b>, <c>);";
        InsertQuery query = (InsertQuery) AntlrParser.parseQuery(text);
        Set<ColumnRef> columns = query.accept(new ColumnExtractVisitor());
        Assert.assertTrue(columns.contains(new ColumnRef("a")));
        Assert.assertTrue(columns.contains(new ColumnRef("b")));
        Assert.assertTrue(columns.contains(new ColumnRef("c")));
    }

    @Test
    public void testResolveDeleteQuery() {
        String text = "DELETE FROM A WHERE a = <param>;";
        DeleteQuery query = (DeleteQuery) AntlrParser.parseQuery(text);
        Set<ColumnRef> columns = query.accept(new ColumnExtractVisitor());
        Assert.assertTrue(columns.contains(new ColumnRef("a")));
    }

    @Test
    public void testResolveCompoundDeleteQuery() {
        String text = "DELETE A, B FROM A JOIN B ON A.c = B.d WHERE e = <param>;";
        CompoundDeleteQuery query = (CompoundDeleteQuery) AntlrParser.parseQuery(text);
        Set<ColumnRef> columns = query.accept(new ColumnExtractVisitor());
        Assert.assertTrue(columns.contains(new ColumnRef("e")));
    }

    @Test
    public void testResolveUpdateQuery() {
        String text = "UPDATE A SET b = <b>, c = <c> WHERE a = <a>;";
        UpdateQuery query = (UpdateQuery) AntlrParser.parseQuery(text);
        Set<ColumnRef> columns = query.accept(new ColumnExtractVisitor());
        Assert.assertTrue(columns.contains(new ColumnRef("a")));
        Assert.assertTrue(columns.contains(new ColumnRef("b")));
        Assert.assertTrue(columns.contains(new ColumnRef("c")));
    }

    @Test
    public void testResolveCompoundUpdateQuery() {
        String text = "UPDATE A JOIN B ON A.c = B.d SET b = <b> WHERE e = <e>;";
        CompoundUpdateQuery query = (CompoundUpdateQuery) AntlrParser.parseQuery(text);
        Set<ColumnRef> columns = query.accept(new ColumnExtractVisitor());
        Assert.assertTrue(columns.contains(new ColumnRef("b")));
        Assert.assertTrue(columns.contains(new ColumnRef("e")));
    }

}
