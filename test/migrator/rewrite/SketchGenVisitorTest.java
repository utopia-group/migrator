package migrator.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.QueryList;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.parser.AntlrParser;
import migrator.util.ListMultiMap;

public class SketchGenVisitorTest {

    private static class MockAJoinCorrSupplier implements IJoinCorrSupplier {

        protected final SchemaDef srcSchema;
        protected final SchemaDef tgtSchema;

        public MockAJoinCorrSupplier(SchemaDef srcSchema, SchemaDef tgtSchema) {
            this.srcSchema = srcSchema;
            this.tgtSchema = tgtSchema;
            assert this.srcSchema != null && this.tgtSchema != null;
        }

        @Override
        public List<JoinChain> getJoinChains(ValueCorrespondence valueCorr, JoinChain chain) {
            Set<ColumnRef> columns = RewriteUtil.allColumnsInTable(chain.table, srcSchema);
            for (Join join : chain.joins) {
                columns.addAll(RewriteUtil.allColumnsInTable(join.getDest(), srcSchema));
            }
            return getJoinChainsForColumns(valueCorr, chain, columns);
        }

        @Override
        public List<JoinChain> getJoinChainsForColumns(ValueCorrespondence valueCorr, JoinChain chain, Set<ColumnRef> columns) {
            List<JoinChain> ret = new ArrayList<>();
            {
                TableRef table = new TableRef("B");
                Join join = new Join(new TableRef("C"), new ColumnRef("e", "B"), new ColumnRef("e", "C"));
                ret.add(new JoinChain(table, Collections.singletonList(join)));
            }
            return ret;
        }

    }

    private SchemaDef buildSourceSchema() {
        String text = ""
                + "CREATE TABLE A ("
                + "    a INT,"
                + "    b INT,"
                + "    c INT,"
                + "    d INT,"
                + "    PRIMARY KEY (a));";
        return AntlrParser.parseSchema(text);
    }

    private SchemaDef buildTargetSchema() {
        String text = ""
                + "CREATE TABLE B ("
                + "    a INT,"
                + "    b INT,"
                + "    e INT,"
                + "    FOREIGN KEY (e) REFERENCES C (e),"
                + "    PRIMARY KEY (a));"
                + "CREATE TABLE C ("
                + "    e INT,"
                + "    c INT,"
                + "    d INT,"
                + "    PRIMARY KEY (e));";
        return AntlrParser.parseSchema(text);
    }

    private ValueCorrespondence buildValueCorrespondence() {
        ListMultiMap<ColumnRef, ColumnRef> mapping = new ListMultiMap<>();
        mapping.put(new ColumnRef("a", "A"), new ColumnRef("a", "B"));
        mapping.put(new ColumnRef("b", "A"), new ColumnRef("b", "B"));
        mapping.put(new ColumnRef("c", "A"), new ColumnRef("c", "C"));
        mapping.put(new ColumnRef("d", "A"), new ColumnRef("d", "C"));
        return new ValueCorrespondence(mapping);
    }

    private IQuery parseAndResolve(String text, SchemaDef schema) {
        IQuery query = AntlrParser.parseQuery(text);
        query.accept(new ResolveColumnVisitor(schema));
        return query;
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

    private void testInEnvironmentA(String queryText, String sketchText) {
        SchemaDef srcSchema = buildSourceSchema();
        SchemaDef tgtSchema = buildTargetSchema();
        ValueCorrespondence valueCorr = buildValueCorrespondence();
        IJoinCorrSupplier jcSupplier = new MockAJoinCorrSupplier(srcSchema, tgtSchema);
        SketchGenVisitor visitor = new SketchGenVisitor(srcSchema, tgtSchema, valueCorr, jcSupplier);
        IQuery query = parseAndResolve(queryText, srcSchema);
        IQuery sketch = query.accept(visitor);
        Assert.assertEquals(sketchText, sketch.toSqlString());
    }

    @Test
    public void testSelectQuery1() {
        String query = "SELECT a, b, c, d FROM A WHERE a = <param>;";
        String sketch = Stream.of(
                "choose {",
                "    SELECT B.a, B.b, C.c, C.d FROM B JOIN C ON B.e = C.e WHERE B.a = <param>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentA(query, sketch);
    }

    @Test
    public void testInsertQuery1() {
        String query = "INSERT INTO A (a, b, c, d) VALUES (<a>, <b>, <c>, <d>);";
        String sketch = Stream.of(
                "choose {",
                "    INSERT INTO B (B.a, B.b, B.e) VALUES (<a>, <b>, FRESH(0));",
                "    INSERT INTO C (C.c, C.d, C.e) VALUES (<c>, <d>, FRESH(0));",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentA(query, sketch);
    }

    @Test
    public void testDeleteQuery1() {
        String query = "DELETE FROM A WHERE d = <param>;";
        String sketch = Stream.of(
                "choose {",
                "    DELETE ?? FROM B JOIN C ON B.e = C.e WHERE C.d = <param>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentA(query, sketch);
    }

    @Test
    public void testUpdateQuery1() {
        String query = "UPDATE A SET b = <b> WHERE d = <d>;";
        String sketch = Stream.of(
                "choose {",
                "    UPDATE B JOIN C ON B.e = C.e SET B.b = <b> WHERE C.d = <d>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentA(query, sketch);
    }

    private static class MockBJoinCorrSupplier extends MockAJoinCorrSupplier {

        public MockBJoinCorrSupplier(SchemaDef srcSchema, SchemaDef tgtSchema) {
            super(srcSchema, tgtSchema);
        }

        @Override
        public List<JoinChain> getJoinChainsForColumns(ValueCorrespondence valueCorr, JoinChain chain, Set<ColumnRef> columns) {
            List<JoinChain> ret = new ArrayList<>();
            {
                TableRef table = new TableRef("B");
                ret.add(new JoinChain(table, Collections.emptyList()));
            }
            {
                TableRef table = new TableRef("B");
                Join join = new Join(new TableRef("C"), new ColumnRef("e", "B"), new ColumnRef("e", "C"));
                ret.add(new JoinChain(table, Collections.singletonList(join)));
            }
            return ret;
        }

    }

    private void testInEnvironmentB(String queryText, String sketchText) {
        SchemaDef srcSchema = buildSourceSchema();
        SchemaDef tgtSchema = buildTargetSchema();
        ValueCorrespondence valueCorr = buildValueCorrespondence();
        IJoinCorrSupplier jcSupplier = new MockBJoinCorrSupplier(srcSchema, tgtSchema);
        SketchGenVisitor visitor = new SketchGenVisitor(srcSchema, tgtSchema, valueCorr, jcSupplier);
        IQuery query = parseAndResolve(queryText, srcSchema);
        IQuery sketch = query.accept(visitor);
        Assert.assertEquals(sketchText, sketch.toSqlString());
    }

    @Test
    public void testSelectQuery2() {
        String query = "SELECT a, b, c, d FROM A WHERE a = <param>;";
        String sketch = Stream.of(
                "choose {",
                "    SELECT B.a, B.b, C.c, C.d FROM B WHERE B.a = <param>;",
                "} or {",
                "    SELECT B.a, B.b, C.c, C.d FROM B JOIN C ON B.e = C.e WHERE B.a = <param>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentB(query, sketch);
    }

    @Test
    public void testInsertQuery2() {
        String query = "INSERT INTO A (a, b, c, d) VALUES (<a>, <b>, <c>, <d>);";
        // The fresh values in the third choice should be 2-3-3 or 3-3-3?
        // Admit 2-3-3 for now.
        String sketch = Stream.of(
                "choose {",
                "    INSERT INTO B (B.a, B.b, B.e) VALUES (<a>, <b>, FRESH(0));",
                "    INSERT INTO C (C.c, C.d, C.e) VALUES (<c>, <d>, FRESH(0));",
                "} or {",
                "    INSERT INTO B (B.a, B.b, B.e) VALUES (<a>, <b>, FRESH(1));",
                "} or {",
                "    INSERT INTO B (B.a, B.b, B.e) VALUES (<a>, <b>, FRESH(2));",
                "    INSERT INTO B (B.a, B.b, B.e) VALUES (<a>, <b>, FRESH(3));",
                "    INSERT INTO C (C.c, C.d, C.e) VALUES (<c>, <d>, FRESH(3));",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentB(query, sketch);
    }

    @Test
    public void testDeleteQuery2() {
        String query = "DELETE FROM A WHERE d = <param>;";
        String sketch = Stream.of(
                "choose {",
                "    DELETE ?? FROM B JOIN C ON B.e = C.e WHERE C.d = <param>;",
                "} or {",
                "    DELETE ?? FROM B WHERE C.d = <param>;",
                "} or {",
                "    DELETE ?? FROM B WHERE C.d = <param>;",
                "    DELETE ?? FROM B JOIN C ON B.e = C.e WHERE C.d = <param>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentB(query, sketch);
    }

    @Test
    public void testUpdateQuery2() {
        String query = "UPDATE A SET b = <b> WHERE d = <d>;";
        String sketch = Stream.of(
                "choose {",
                "    UPDATE B JOIN C ON B.e = C.e SET B.b = <b> WHERE C.d = <d>;",
                "} or {",
                "    UPDATE B SET B.b = <b> WHERE C.d = <d>;",
                "} or {",
                "    UPDATE B SET B.b = <b> WHERE C.d = <d>;",
                "    UPDATE B JOIN C ON B.e = C.e SET B.b = <b> WHERE C.d = <d>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentB(query, sketch);
    }

    private static class MockCJoinCorrSupplier extends MockAJoinCorrSupplier {

        public MockCJoinCorrSupplier(SchemaDef srcSchema, SchemaDef tgtSchema) {
            super(srcSchema, tgtSchema);
        }

        @Override
        public List<JoinChain> getJoinChainsForColumns(ValueCorrespondence valueCorr, JoinChain chain, Set<ColumnRef> columns) {
            List<JoinChain> ret = new ArrayList<>();
            {
                TableRef table = new TableRef("A");
                ret.add(new JoinChain(table, Collections.emptyList()));
            }
            return ret;
        }

    }

    private ValueCorrespondence buildValueCorrespondence3() {
        ListMultiMap<ColumnRef, ColumnRef> mapping = new ListMultiMap<>();
        mapping.put(new ColumnRef("a", "B"), new ColumnRef("a", "A"));
        mapping.put(new ColumnRef("b", "B"), new ColumnRef("b", "A"));
        mapping.put(new ColumnRef("c", "C"), new ColumnRef("c", "A"));
        mapping.put(new ColumnRef("d", "C"), new ColumnRef("d", "A"));
        return new ValueCorrespondence(mapping);
    }

    private void testInEnvironmentC(String queryText, String sketchText) {
        // note that source and target schemas are switched
        SchemaDef srcSchema = buildTargetSchema();
        SchemaDef tgtSchema = buildSourceSchema();
        ValueCorrespondence valueCorr = buildValueCorrespondence3();
        IJoinCorrSupplier jcSupplier = new MockCJoinCorrSupplier(srcSchema, tgtSchema);
        SketchGenVisitor visitor = new SketchGenVisitor(srcSchema, tgtSchema, valueCorr, jcSupplier);
        IQuery query = parseAndResolve(queryText, srcSchema);
        IQuery sketch = query.accept(visitor);
        Assert.assertEquals(sketchText, sketch.toSqlString());
    }

    private void testQueryListInEnvironmentC(String queryListText, String sketchText) {
        // note that source and target schemas are switched
        SchemaDef srcSchema = buildTargetSchema();
        SchemaDef tgtSchema = buildSourceSchema();
        ValueCorrespondence valueCorr = buildValueCorrespondence3();
        IJoinCorrSupplier jcSupplier = new MockCJoinCorrSupplier(srcSchema, tgtSchema);
        SketchGenVisitor visitor = new SketchGenVisitor(srcSchema, tgtSchema, valueCorr, jcSupplier);
        QueryList queryList = parseResolveMergeQueryList(queryListText, srcSchema);
        List<IQuery> sketchList = new ArrayList<>();
        for (IQuery query : queryList.queries) {
            sketchList.add(query.accept(visitor));
        }
        QueryList sketch = new QueryList(sketchList);
        Assert.assertEquals(sketchText, sketch.toSqlString());
    }

    @Test
    public void testSelectQuery3() {
        String query = "SELECT a, b, c, d FROM B JOIN C ON B.e = C.e WHERE a = <param>;";
        String sketch = Stream.of(
                "choose {",
                "    SELECT A.a, A.b, A.c, A.d FROM A WHERE A.a = <param>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentC(query, sketch);
    }

    @Test
    public void testInsertQuery3() {
        String query = Stream.of(
                "INSERT INTO B (a, b, e) VALUES (<a>, <b>, FRESH(0));",
                "INSERT INTO C (e, c, d) VALUES (FRESH(0), <c>, <d>);")
                .collect(Collectors.joining(System.lineSeparator()));
        String sketch = Stream.of(
                "choose {",
                "    INSERT INTO A (A.a, A.b, A.c, A.d) VALUES (<a>, <b>, <c>, <d>);",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testQueryListInEnvironmentC(query, sketch);
    }

    @Test
    public void testDeleteQuery3() {
        String query = "DELETE B, C FROM B JOIN C ON B.e = C.e WHERE d = <param>;";
        String sketch = Stream.of(
                "choose {",
                "    DELETE ?? FROM A WHERE A.d = <param>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentC(query, sketch);
    }

    @Test
    public void testUpdateQuery3() {
        String query = "UPDATE B JOIN C ON B.e = C.e SET b = <b> WHERE d = <d>;";
        String sketch = Stream.of(
                "choose {",
                "    UPDATE A SET A.b = <b> WHERE A.d = <d>;",
                "}").collect(Collectors.joining(System.lineSeparator()));
        testInEnvironmentC(query, sketch);
    }
}
