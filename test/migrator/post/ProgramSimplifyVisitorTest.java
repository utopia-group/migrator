package migrator.post;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.UpdateQuery;
import migrator.rewrite.parser.AntlrParser;

public class ProgramSimplifyVisitorTest {

    private SchemaDef buildSchema() {
        String text = ""
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
        return AntlrParser.parseSchema(text);
    }

    @Test
    public void testSimplifyCompoundDelete() {
        SchemaDef schema = buildSchema();
        String queryText = "DELETE A FROM A WHERE A.a = 10;";
        CompoundDeleteQuery query = (CompoundDeleteQuery) AntlrParser.parseQuery(queryText);
        IQuery simplified = query.accept(new ProgramSimplifyVisitor(schema));
        Assert.assertEquals("DELETE FROM A WHERE A.a = 10;", simplified.toSqlString());
    }

    @Test
    public void testSimplifyCompoundUpdate1() {
        SchemaDef schema = buildSchema();
        String queryText = "UPDATE A SET A.b = 20 WHERE A.a = 10;";
        UpdateQuery query = (UpdateQuery) AntlrParser.parseQuery(queryText);
        List<Join> joins = Collections.emptyList();
        CompoundUpdateQuery compound = new CompoundUpdateQuery(query.table, joins, query.pairs, query.predicate);
        UpdateQuery simplified = (UpdateQuery) compound.accept(new ProgramSimplifyVisitor(schema));
        Assert.assertEquals(queryText, simplified.toSqlString());
    }

    @Test
    public void testSimplifyCompoundUpdate2() {
        SchemaDef schema = buildSchema();
        String queryText = "UPDATE A JOIN B ON A.c = B.d SET A.b = 20 WHERE A.a = 10;";
        CompoundUpdateQuery query = (CompoundUpdateQuery) AntlrParser.parseQuery(queryText);
        IQuery simplified = query.accept(new ProgramSimplifyVisitor(schema));
        Assert.assertEquals("UPDATE A SET A.b = 20 WHERE A.a = 10;", simplified.toSqlString());
    }

    @Test
    public void testSimplifyCompoundUpdate3() {
        SchemaDef schema = buildSchema();
        String queryText = "UPDATE A JOIN B ON A.c = B.d SET A.b = 20 WHERE B.d = 10;";
        CompoundUpdateQuery query = (CompoundUpdateQuery) AntlrParser.parseQuery(queryText);
        IQuery simplified = query.accept(new ProgramSimplifyVisitor(schema));
        Assert.assertEquals("UPDATE A SET A.b = 20 WHERE A.c = 10;", simplified.toSqlString());
    }
}
