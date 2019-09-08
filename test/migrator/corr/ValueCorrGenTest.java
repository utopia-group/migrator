package migrator.corr;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.parser.AntlrParser;
import migrator.rewrite.sql.SqlProgram;

public class ValueCorrGenTest {

    @Test
    public void testMaxSATValueCorrSupplier() {
        SchemaDef srcSchema = AntlrParser.parseSchema("CREATE TABLE A(id INT, b INT);");
        SchemaDef destSchema = AntlrParser.parseSchema("CREATE TABLE B(id INT, d INT);");
        ISimilarityStrategy strategy = new TableColumnSimilarity();
        IValueCorrSupplier supplier = new ValueCorrespondenceSupplier(srcSchema, destSchema, strategy);
        int index = 0;
        while (supplier.hasNext()) {
            ++index;
            supplier.getNext();
        }
        Assert.assertEquals(9, index);
    }

    @Test
    public void testOptimValueCorrSupplier() {
        SchemaDef srcSchema = AntlrParser.parseSchema("CREATE TABLE A(id INT, b INT); CREATE TABLE C(id INT);");
        SchemaDef destSchema = AntlrParser.parseSchema("CREATE TABLE B(id INT, d INT); CREATE TABLE C(id INT);");
        SqlProgram srcProg = AntlrParser.parseProgram(Stream.of(
                "query q1(int id) {",
                "    SELECT id, b FROM A WHERE id = <id>;",
                "}",
                "query q2() {",
                "    SELECT id FROM C;",
                "}").collect(Collectors.joining(System.lineSeparator())));
        ISimilarityStrategy strategy = new ColumnSimilarity();
        IValueCorrSupplier supplier = new OptimValueCorrSupplier(srcSchema, destSchema, srcProg, strategy);
        int index = 0;
        while (supplier.hasNext()) {
            ++index;
            supplier.getNext();
        }
        Assert.assertEquals(16, index);
    }
}
