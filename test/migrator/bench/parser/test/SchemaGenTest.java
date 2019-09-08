package migrator.bench.parser.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import migrator.bench.BenchmarkAST.AttrDecl;
import migrator.bench.BenchmarkAST.Schema;
import migrator.bench.parser.BenchmarkParser;

public class SchemaGenTest {

    @Test
    public void testSchema() {
        List<String> relations = new ArrayList<>();
        String relStrA = "A(int a, int aa, int b_fk)";
        String relStrB = "B(int b, String bb)";
        relations.add(relStrA);
        relations.add(relStrB);
        List<String> pks = new ArrayList<>();
        pks.add("A(a)");
        pks.add("B(b)");
        List<String> fks = new ArrayList<>();
        fks.add("A(b_fk) -> B(b)");
        Schema schema = new BenchmarkParser().parseSchema(relations, pks, fks);
        Assert.assertEquals(relStrA, schema.relDecls.get(0).toString());
        Assert.assertEquals(relStrB, schema.relDecls.get(1).toString());
        Assert.assertTrue(schema.relDecls.get(0).getAttrDeclByName("a").isPrimaryKey());
        Assert.assertTrue(schema.relDecls.get(1).getAttrDeclByName("b").isPrimaryKey());
        AttrDecl fkAttr = schema.getRelDeclByName("A").getAttrDeclByName("b_fk");
        Assert.assertTrue(fkAttr.isForeignKey());
        Assert.assertEquals("B", fkAttr.getReferenceAttrDecl().getRelDecl().name);
        Assert.assertEquals("b", fkAttr.getReferenceAttrDecl().name);
    }

}
