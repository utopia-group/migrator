package migrator.rewrite.parser;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class AntlrParserTest {
    private static void assertQueryParsesTo(String expected, String query) {
        assertEquals(expected, AntlrParser.parseQuery(query).toSqlString());
    }

    private static void assertQueryParsesToSame(String query) {
        assertQueryParsesTo(query, query);
    }

    private static void assertSchemaParsesTo(String expected, String schema) {
        assertEquals(expected, AntlrParser.parseSchema(schema).toSqlString());
    }

    private static void assertSchemaParsesToSame(String schema) {
        assertSchemaParsesTo(schema, schema);
    }

    @Test
    public void testParseSelectQuery() {
        assertQueryParsesToSame("SELECT A.a FROM A;");
        assertQueryParsesToSame("SELECT a FROM A;");
        assertQueryParsesToSame("SELECT a, b, c FROM A JOIN B ON A.b = B.b JOIN C ON A.c = C.c;");
        assertQueryParsesToSame("SELECT A.a, b, C.c FROM A JOIN B ON A.b = B.b JOIN C ON A.c = C.c;");
        assertQueryParsesTo("SELECT A.a FROM A JOIN B ON A.b = B.b;", "SELECT A.a FROM A JOIN B ON B.b = A.b;");
        assertQueryParsesToSame("SELECT a FROM A WHERE b = 5;");
    }

    @Test
    public void testParseInsertQuery() {
        assertQueryParsesToSame("INSERT INTO A (a) VALUES (5);");
        assertQueryParsesToSame("INSERT INTO A (a, b, c) VALUES (5, parameter1, 7);");
    }

    @Test
    public void testParseUpdateQuery() {
        assertQueryParsesToSame("UPDATE A SET a = 5;");
        assertQueryParsesToSame("UPDATE A SET a = 5, b = 7, c = parameter;");
        assertQueryParsesToSame("UPDATE A SET a = 5, b = 7 WHERE b = 8;");
    }

    @Test
    public void testParseCompoundUpdateQuery() {
        assertQueryParsesToSame("UPDATE A JOIN B ON A.b = B.b SET A.a = 5;");
        assertQueryParsesToSame("UPDATE A JOIN B ON A.b = B.b SET A.a = 5, A.c = <parameter>;");
        assertQueryParsesToSame("UPDATE A JOIN B ON A.b = B.b SET A.a = 5, A.c = 7 WHERE B.d = 8;");
    }

    @Test
    public void testParseDeleteQuery() {
        assertQueryParsesToSame("DELETE FROM A;");
        assertQueryParsesToSame("DELETE FROM A WHERE a = 2;");
    }

    @Test
    public void testParseCompoundDeleteQuery() {
        assertQueryParsesToSame("DELETE A FROM A;");
        assertQueryParsesToSame("DELETE A FROM A WHERE a = 2;");
        assertQueryParsesToSame("DELETE A, B FROM A JOIN B ON A.b = B.b WHERE a = 2;");
    }

    @Test
    public void testParseTableAs() {
        assertQueryParsesToSame("SELECT B.a FROM A AS B;");
        assertQueryParsesToSame("SELECT B.a FROM A JOIN C AS B ON A.b = B.b;");
    }

    @Test
    public void testParseValue() {
        assertQueryParsesToSame("SELECT a FROM A WHERE b = 2;");
        assertQueryParsesToSame("SELECT a FROM A WHERE b = <hello>;");
        assertQueryParsesToSame("SELECT a FROM A WHERE b = c;");
    }

    @Test
    public void testParsePredicate() {
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 OR A.b = 7;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 AND A.b = 7;");
        assertQueryParsesToSame("SELECT a FROM A WHERE NOT A.a = 5;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a <> 5;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a <= 5;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a > 5;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 AND A.b = 7 AND A.c = 8;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 AND A.b = 7 OR A.c = 8;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.c = 8 OR A.a = 5 AND A.b = 7;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.c = 8 OR A.a = 5 OR A.b = 7;");
        assertQueryParsesToSame("SELECT a FROM A WHERE (A.c = 8 OR A.a = 5) AND A.b = 7;");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 AND (A.b = 7 OR A.c = 8);");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 AND (A.b = 7 AND A.c = 8);");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 OR (A.b = 7 OR A.c = 8);");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = 5 AND NOT (A.b = 7 OR A.c = 8);");
        assertQueryParsesToSame("SELECT a FROM A WHERE NOT (A.a = 5 AND NOT (A.b = 7 OR A.c = 8));");
        assertQueryParsesToSame("SELECT a FROM A WHERE NOT NOT NOT A.a = 5;");
    }

    @Test
    public void testParseSubquery() {
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a = (SELECT B.b FROM B WHERE B.c = A.c);");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a IN (SELECT B.b FROM B WHERE B.c = A.c);");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a IN (SELECT B.b FROM B);");
        assertQueryParsesToSame(
                "SELECT a FROM A WHERE A.a IN (SELECT B.b FROM B JOIN C ON B.a = C.d WHERE C.c = A.c);");
        assertQueryParsesToSame("SELECT a FROM A WHERE A.a IN ("
                + "SELECT B.b FROM B JOIN C ON B.a = C.d JOIN D ON A.f = D.f WHERE C.c = A.c);");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalJoinSelectQuery() {
        assertQueryParsesToSame("SELECT a FROM A JOIN B ON A.a = C.a;");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalColumn() {
        assertQueryParsesToSame("SELECT A.b.c FROM A;");
    }

    @Test
    public void testParseSchema() {
        assertSchemaParsesToSame("");
        assertSchemaParsesToSame("CREATE TABLE A ();");
        assertSchemaParsesToSame("CREATE TABLE A (id INT);");
        assertSchemaParsesTo("CREATE TABLE A (id INT);", "CREATE TABLE A (id INT,);");
        assertSchemaParsesToSame("CREATE TABLE A (id INT, PRIMARY KEY (id));");
        assertSchemaParsesToSame(
                "CREATE TABLE A (b_id INT, PRIMARY KEY (b_id), FOREIGN KEY (b_id) REFERENCES B (id));");
    }

    @Test
    public void testParseValueCorr() {
        String text = Stream.of(
                "A.a -> B.a",
                "A.b -> B.b",
                "A.b -> B.c")
                .collect(Collectors.joining(System.lineSeparator()));
        assertEquals(text, AntlrParser.parserValueCorr(text).toString().trim());
    }
}
