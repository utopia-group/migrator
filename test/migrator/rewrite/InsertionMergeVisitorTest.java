package migrator.rewrite;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.QueryList;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.parser.AntlrParser;

public class InsertionMergeVisitorTest {

    private SchemaDef buildSchema() {
        String text = ""
                + "CREATE TABLE A ("
                + "    a INT,"
                + "    b INT,"
                + "    e INT,"
                + "    FOREIGN KEY (e) REFERENCES B (e),"
                + "    PRIMARY KEY (a));"
                + "CREATE TABLE B ("
                + "    e INT,"
                + "    c INT,"
                + "    d INT,"
                + "    PRIMARY KEY (e));";
        return AntlrParser.parseSchema(text);
    }

    private QueryList parseResolveMergeQueryList(String text, SchemaDef schema) {
        QueryList list = AntlrParser.parseQueryList(text);
        ResolveColumnVisitor resolveVisitor = new ResolveColumnVisitor(schema);
        for (IQuery query : list.queries) {
            query.accept(resolveVisitor);
        }
        InsertionMergeVisitor mergeVisitor = new InsertionMergeVisitor(schema);
        for (IQuery query : list.queries) {
            query.accept(mergeVisitor);
        }
        return mergeVisitor.getQueryList();
    }

    private void testMergeTo(String queryListText, String expectedText) {
        SchemaDef schema = buildSchema();
        QueryList queryList = parseResolveMergeQueryList(queryListText, schema);
        Assert.assertEquals(expectedText, queryList.toSqlString());
    }

    @Test
    public void testFKThenPK() {
        String query = Stream.of(
                "INSERT INTO A (a, b, e) VALUES (<a>, <b>, FRESH(0));",
                "INSERT INTO B (e, c, d) VALUES (FRESH(0), <c>, <d>);")
                .collect(Collectors.joining(System.lineSeparator()));
        String expected = Stream.of(
                "INSERT INTO A (A.a, A.b, A.e) VALUES (<a>, <b>, FRESH(0))",
                "INTO B (B.e, B.c, B.d) VALUES (FRESH(0), <c>, <d>);")
                .collect(Collectors.joining(", "));
        testMergeTo(query, expected);
    }

    @Test
    public void testPKThenFK() {
        String query = Stream.of(
                "INSERT INTO B (e, c, d) VALUES (FRESH(0), <c>, <d>);",
                "INSERT INTO A (a, b, e) VALUES (<a>, <b>, FRESH(0));")
                .collect(Collectors.joining(System.lineSeparator()));
        String expected = Stream.of(
                "INSERT INTO B (B.e, B.c, B.d) VALUES (FRESH(0), <c>, <d>)",
                "INTO A (A.a, A.b, A.e) VALUES (<a>, <b>, FRESH(0));")
                .collect(Collectors.joining(", "));
        testMergeTo(query, expected);
    }
}
