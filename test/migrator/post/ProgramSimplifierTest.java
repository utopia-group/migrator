package migrator.post;

import org.junit.Assert;
import org.junit.Test;

import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.parser.AntlrParser;

public class ProgramSimplifierTest {

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
    public void testSameCompoundDelete1() {
        IQuery query1 = AntlrParser.parseQuery("DELETE A FROM A WHERE A.a = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE A FROM A WHERE A.a = 10;");
        Assert.assertTrue(ProgramSimplifier.sameCompoundDelete(query1, query2));
    }

    @Test
    public void testSameCompoundDelete2() {
        IQuery query1 = AntlrParser.parseQuery("DELETE A FROM A JOIN B ON A.c = B.d WHERE A.a = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE A FROM A JOIN B ON A.c = B.d WHERE A.a = 10;");
        Assert.assertTrue(ProgramSimplifier.sameCompoundDelete(query1, query2));
    }

    @Test
    public void testDualCompoundDelete1() {
        SchemaDef schema = buildSchema();
        IQuery query1 = AntlrParser.parseQuery("DELETE A FROM A JOIN B ON A.c = B.d WHERE A.c = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE A FROM A JOIN B ON A.c = B.d WHERE B.d = 10;");
        Assert.assertTrue(ProgramSimplifier.dualCompoundDelete(query1, query2, schema));
    }

    @Test
    public void testDualCompoundDelete2() {
        SchemaDef schema = buildSchema();
        IQuery query1 = AntlrParser.parseQuery("DELETE A FROM A JOIN B ON A.c = B.d WHERE B.d = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE A FROM A JOIN B ON A.c = B.d WHERE A.c = 10;");
        Assert.assertTrue(ProgramSimplifier.dualCompoundDelete(query1, query2, schema));
    }

    @Test
    public void testDualCompoundDelete3() {
        SchemaDef schema = buildSchema();
        IQuery query1 = AntlrParser.parseQuery("DELETE A FROM B JOIN A ON B.d = A.c WHERE B.d = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE A FROM B JOIN A ON B.d = A.c WHERE A.c = 10;");
        Assert.assertTrue(ProgramSimplifier.dualCompoundDelete(query1, query2, schema));
    }

    @Test
    public void testDualCompoundDelete4() {
        SchemaDef schema = buildSchema();
        IQuery query1 = AntlrParser.parseQuery("DELETE A FROM B JOIN A ON B.d = A.c WHERE A.c = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE A FROM B JOIN A ON B.d = A.c WHERE B.d = 10;");
        Assert.assertTrue(ProgramSimplifier.dualCompoundDelete(query1, query2, schema));
    }

    @Test
    public void testSameSimpleDelete() {
        IQuery query1 = AntlrParser.parseQuery("DELETE FROM A WHERE A.a = 10;");
        IQuery query2 = AntlrParser.parseQuery("DELETE FROM A WHERE A.a = 10;");
        Assert.assertTrue(ProgramSimplifier.sameSimpleDelete(query1, query2));
    }

    @Test
    public void testSameSimpleUpdate() {
        IQuery query1 = AntlrParser.parseQuery("UPDATE A SET A.b = 20 WHERE A.a = 10;");
        IQuery query2 = AntlrParser.parseQuery("UPDATE A SET A.b = 20 WHERE A.a = 10;");
        Assert.assertTrue(ProgramSimplifier.sameSimpleUpdate(query1, query2));
    }
}
