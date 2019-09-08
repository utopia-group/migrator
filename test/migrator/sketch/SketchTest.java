package migrator.sketch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import migrator.Synthesizer;
import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.Operator;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.parser.AntlrParser;
import migrator.rewrite.program.Method;
import migrator.rewrite.program.MethodType;
import migrator.rewrite.program.Parameter;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;

public class SketchTest {

    private SchemaDef buildSourceSchema() {
        String schemaText = "CREATE TABLE Member (mid INT, name VARCHAR, addr VARCHAR, PRIMARY KEY (mid));";
        return AntlrParser.parseSchema(schemaText);
    }

    private SchemaDef buildTargetSchema() {
        String schemaText = "CREATE TABLE Address (aid INT, addr VARCHAR, PRIMARY KEY (aid));"
                + "CREATE TABLE Member (mid INT, name VARCHAR, afk INT, PRIMARY KEY(mid), FOREIGN KEY (afk) REFERENCES Address (aid));";
        return AntlrParser.parseSchema(schemaText);
    }

    private SqlProgram buildSourceProgram(SchemaDef schemaDef) {
        String programText = "update addMember(INT mid, VARCHAR name, VARCHAR addr) {"
                + "INSERT INTO Member (mid, name, addr) VALUES (<mid>, <name>, <addr>); }"
                + "update deleteMember(INT mid) { DELETE FROM Member WHERE mid = <mid>; }"
                + "query getAddress(INT mid) { SELECT addr FROM Member WHERE mid = <mid>; }";
        SqlProgram program = AntlrParser.parseProgram(programText);
        Synthesizer.resolveSqlProgram(program, schemaDef);
        return program;
    }

    private Sketch buildSketch(SchemaDef schemaDef) {
        List<MethodImplementation> methods = new ArrayList<>();
        // update addMember(INT mid, VARCHAR name, VARCHAR addr)
        {
            List<Parameter> params = new ArrayList<>();
            params.add(new Parameter("mid", "INT"));
            params.add(new Parameter("name", "VARCHAR"));
            params.add(new Parameter("addr", "VARCHAR"));
            Method signature = new Method("addMember", params, MethodType.UPDATE);
            List<IQuery> queries = new ArrayList<>();
            queries.add(
                    AntlrParser.parseQuery("INSERT INTO Member (mid, name, afk) VALUES (<mid>, <name>, FRESH(0));"));
            queries.add(AntlrParser.parseQuery("INSERT INTO Address (aid, addr) VALUES (FRESH(0), <addr>);"));
            for (IQuery query : queries) {
                System.out.println(query);
            }
            MethodImplementation method = new MethodImplementation(signature, queries);
            methods.add(method);
        }
        // update deleteMember(INT mid)
        {
            List<Parameter> params = new ArrayList<>();
            params.add(new Parameter("mid", "INT"));
            Method signature = new Method("deleteMember", params, MethodType.UPDATE);
            List<IQuery> queries = new ArrayList<>();
            {
                TableRef tableRef = new TableRef("Member", "Member");
                List<Join> joins = new ArrayList<>();
                joins.add(new Join(new TableRef("Address", "Address"), new ColumnRef("afk", "Member"),
                        new ColumnRef("aid", "Address")));
                OpPred predicate = new OpPred(new ColumnValue(new ColumnRef("mid", "Member")),
                        new ParameterValue("mid"), Operator.EQ);
                CompoundDeleteQuery query = new CompoundDeleteQuery(tableRef, joins, predicate);
                queries.add(query);
            }
            MethodImplementation method = new MethodImplementation(signature, queries);
            methods.add(method);
        }
        // query getAddress(INT mid)
        {
            List<Parameter> params = new ArrayList<>();
            params.add(new Parameter("mid", "INT"));
            Method signature = new Method("getAddress", params, MethodType.QUERY);
            List<IQuery> queries = new ArrayList<>();
            {
                List<List<IQuery>> queryLists = new ArrayList<>();
                {
                    IQuery query;
                    query = AntlrParser.parseQuery("SELECT addr FROM Address WHERE aid = <mid>;");
                    queryLists.add(Collections.singletonList(query));
                    query = AntlrParser.parseQuery(
                            "SELECT addr FROM Member JOIN Address ON Member.afk = Address.aid WHERE mid = <mid>;");
                    queryLists.add(Collections.singletonList(query));
                }
                queries.add(new ChooseQuery(queryLists));
            }
            MethodImplementation method = new MethodImplementation(signature, queries);
            methods.add(method);
        }
        SqlProgram sketchProgram = new SqlProgram(methods);
        Synthesizer.resolveSqlProgram(sketchProgram, schemaDef);
        return new Sketch(sketchProgram);
    }

    @Test
    public void sketchSolverTest() {
        SchemaDef sourceSchema = buildSourceSchema();
        SchemaDef targetSchema = buildTargetSchema();
        SqlProgram sourceProg = buildSourceProgram(sourceSchema);
        System.out.println("Source Schema:\n" + sourceSchema.toSqlString());
        System.out.println("\nSource Program:\n" + sourceProg.toSqlString());
        System.out.println("\nTarget Schema:\n" + targetSchema.toSqlString());

        Sketch sketch = buildSketch(targetSchema);
        System.out.println("\nSketch:\n" + sketch);
        ISketchSolver solver = new SketchSolver(sourceSchema, targetSchema);
        SqlProgram targetProg = solver.completeSketch(sketch, sourceProg);
        Assert.assertNotNull(targetProg);
        System.out.println("Synthesized program:\n" + targetProg.toSqlString());
    }

    @Test
    public void naiveSketchSolverTest() {
        SchemaDef sourceSchema = buildSourceSchema();
        SchemaDef targetSchema = buildTargetSchema();
        SqlProgram sourceProg = buildSourceProgram(sourceSchema);
        System.out.println("Source Schema:\n" + sourceSchema.toSqlString());
        System.out.println("\nSource Program:\n" + sourceProg.toSqlString());
        System.out.println("\nTarget Schema:\n" + targetSchema.toSqlString());

        Sketch sketch = buildSketch(targetSchema);
        System.out.println("\nSketch:\n" + sketch);
        ISketchSolver solver = new NaiveSketchSolver(sourceSchema, targetSchema);
        SqlProgram targetProg = solver.completeSketch(sketch, sourceProg);
        Assert.assertNotNull(targetProg);
        System.out.println("Synthesized program:\n" + targetProg.toSqlString());
    }

}
