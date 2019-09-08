package migrator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.parser.AntlrParser;
import migrator.rewrite.sql.SqlProgram;

public class SynthesizerTest {

    private SchemaDef buildExampleSourceSchema() {
        String schemaText = "CREATE TABLE Class (ClassId INT, InstId INT, TaId INT, PRIMARY KEY (ClassId),"
                + "    FOREIGN KEY (InstId) REFERENCES Instructor (InstId),"
                + "    FOREIGN KEY (TaId) REFERENCES TA (TaId));"
                + "CREATE TABLE Instructor (InstId INT, IName VARCHAR, IPic BINARY, PRIMARY KEY (InstId));"
                + "CREATE TABLE TA (TaId INT, TName VARCHAR, TPic BINARY, PRIMARY KEY (TaId));";
        return AntlrParser.parseSchema(schemaText);
    }

    private SchemaDef buildExampleTargetSchema() {
        String schemaText = "CREATE TABLE Class (ClassId INT, InstId INT, TaId INT, PRIMARY KEY (ClassId),"
                + "    FOREIGN KEY (InstId) REFERENCES Instructor (InstId),"
                + "    FOREIGN KEY (TaId) REFERENCES TA (TaId));"
                + "CREATE TABLE Instructor (InstId INT, IName VARCHAR, PicId INT, PRIMARY KEY (InstId), FOREIGN KEY (PicId) REFERENCES Picture (PicId));"
                + "CREATE TABLE TA (TaId INT, TName VARCHAR, PicId INT, PRIMARY KEY (TaId), FOREIGN KEY (PicId) REFERENCES Picture (PicId));"
                + "CREATE TABLE Picture (PicId INT, Pic BINARY, PRIMARY KEY (PicId));";
        return AntlrParser.parseSchema(schemaText);
    }

    private SqlProgram buildExampleSourceProgram() {
        String progText = "query getTAInfo(INT id) { SELECT TName, TPic FROM TA WHERE TaId = <id>; }"
                + "query getInstructorInfo(INT id) { SELECT IName, IPic FROM Instructor WHERE InstId = <id>; }"
                + "update addInstructor(INT id, String name, Binary pic) {"
                + "INSERT INTO Instructor (InstId, IName, IPic) VALUES (<id>, <name>, <pic>); }"
                + "update addTA(INT id, String name, Binary pic) {"
                + "INSERT INTO TA (TaId, TName, TPic) VALUES (<id>, <name>, <pic>); }"
                + "update deleteInstructor(INT id) { DELETE FROM Instructor WHERE InstId = <id>; }"
                + "update deleteTA(INT id) { DELETE FROM TA WHERE TaId = <id>; }";
        return AntlrParser.parseProgram(progText);
    }

    private ValueCorrespondence buildExampleValueCorrespondence() {
        String text = Stream.of(
                "Instructor.IPic -> Picture.Pic",
                "Instructor.InstId -> Instructor.InstId",
                "Instructor.IName -> Instructor.IName",
                "Class.InstId -> Class.InstId",
                "Class.ClassId -> Class.ClassId",
                "Class.TaId -> Class.TaId",
                "TA.TPic -> Picture.Pic",
                "TA.TName -> TA.TName",
                "TA.TaId -> TA.TaId")
                .collect(Collectors.joining(System.lineSeparator()));
        return AntlrParser.parserValueCorr(text);
    }

    @Test
    public void testExample() {
        SchemaDef sourceSchema = buildExampleSourceSchema();
        SchemaDef targetSchema = buildExampleTargetSchema();
        SqlProgram sourceProg = buildExampleSourceProgram();
        SqlProgram targetProg = Synthesizer.synthesize(sourceProg, sourceSchema, targetSchema);
        Assert.assertNotNull(targetProg);
        System.out.println("Synthesized program:");
        System.out.println(targetProg.toSqlString());
    }

    @Test
    public void testExampleWithValueCorr() {
        SchemaDef sourceSchema = buildExampleSourceSchema();
        SchemaDef targetSchema = buildExampleTargetSchema();
        SqlProgram sourceProg = buildExampleSourceProgram();
        ValueCorrespondence valueCorr = buildExampleValueCorrespondence();
        Synthesizer.resolveSqlProgram(sourceProg, sourceSchema);
        SqlProgram targetProg = Synthesizer.synthesize(sourceProg, sourceSchema, targetSchema, valueCorr);
        Assert.assertNotNull(targetProg);
        System.out.println("Synthesized program:");
        System.out.println(targetProg.toSqlString());
    }

    private SchemaDef buildSourceSchema() {
        String schemaText = "CREATE TABLE Car (Cid INT, EngineId INT, TireId INT, PRIMARY KEY (Cid),"
                + "    FOREIGN KEY (EngineId) REFERENCES Engine (Eid),"
                + "    FOREIGN KEY (TireId) REFERENCES Tire (Tid));"
                + "CREATE TABLE Engine (Eid INT, Ename VARCHAR, EDesc VARCHAR, PRIMARY KEY (Eid));"
                + "CREATE TABLE Tire (Tid INT, Tname VARCHAR, TDesc VARCHAR, PRIMARY KEY (Tid));";
        return AntlrParser.parseSchema(schemaText);
    }

    private SchemaDef buildTargetSchema() {
        String schemaText = "CREATE TABLE Car (Cid INT, EngineId INT, TireId INT, PRIMARY KEY (Cid),"
                + "    FOREIGN KEY (EngineId) REFERENCES Engine (Eid),"
                + "    FOREIGN KEY (TireId) REFERENCES Tire (Tid));"
                + "CREATE TABLE Engine (Eid INT, Ename VARCHAR, DetailId INT, PRIMARY KEY (Eid), FOREIGN KEY (DetailId) REFERENCES Detail (Did));"
                + "CREATE TABLE Tire (Tid INT, Tname VARCHAR, DetailId INT, PRIMARY KEY (Tid), FOREIGN KEY (DetailId) REFERENCES Detail (Did));"
                + "CREATE TABLE Detail (Did INT, Desc VARCHAR, PRIMARY KEY (Did));";
        return AntlrParser.parseSchema(schemaText);
    }

    private SqlProgram buildSourceProgram() {
        String progText = "update addEngine(INT eid, String ename, String edesc) {"
                + "INSERT INTO Engine (Eid, Ename, EDesc) VALUES (<eid>, <ename>, <edesc>); }"
                + "update deleteEngine(INT eid) { DELETE FROM Engine WHERE Eid = <eid>; }"
                + "query getEngineInfo(INT eid) { SELECT Ename, EDesc FROM Engine WHERE Eid = <eid>; }"
                + "update addTire(INT tid, String tname, String tdesc) {"
                + "INSERT INTO Tire (Tid, Tname, TDesc) VALUES (<tid>, <tname>, <tdesc>); }"
                + "update deleteTire(INT tid) { DELETE FROM Tire WHERE Tid = <tid>; }"
                + "query getTireInfo(INT tid) { SELECT Tname, TDesc FROM Tire WHERE Tid = <tid>; }";
        return AntlrParser.parseProgram(progText);
    }

    private ValueCorrespondence buildValueCorrespondence() {
        String text = Stream.of(
                "Car.Cid -> Car.Cid",
                "Car.EngineId -> Car.EngineId",
                "Car.TireId -> Car.TireId",
                "Engine.Eid -> Engine.Eid",
                "Engine.Ename -> Engine.Ename",
                "Engine.EDesc -> Detail.Desc",
                "Tire.Tid -> Tire.Tid",
                "Tire.Tname -> Tire.Tname",
                "Tire.TDesc -> Detail.Desc")
                .collect(Collectors.joining(System.lineSeparator()));
        return AntlrParser.parserValueCorr(text);
    }

    @Test
    public void test() {
        SchemaDef sourceSchema = buildSourceSchema();
        SchemaDef targetSchema = buildTargetSchema();
        SqlProgram sourceProg = buildSourceProgram();
        ValueCorrespondence valueCorr = buildValueCorrespondence();
        Synthesizer.resolveSqlProgram(sourceProg, sourceSchema);
        SqlProgram targetProg = Synthesizer.synthesize(sourceProg, sourceSchema, targetSchema, valueCorr);
        Assert.assertNotNull(targetProg);
        System.out.println("Synthesized program:");
        System.out.println(targetProg.toSqlString());
    }
}
