package migrator.rewrite.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import migrator.rewrite.ResolveColumnVisitor;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.parser.AntlrParser;
import migrator.rewrite.program.Invocation;
import migrator.util.ImmutableLinkedList;

public class QueryExecutionTest {

    /**
     * Read all lines from a given file and join them using new lines
     *
     * @param filePath path of the file
     * @return text in the file
     */
    private String readAndJoinLines(String filePath) {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File reading error: " + filePath);
        }
    }

    @Test
    public void test() {
        // Read and process the first program
        String programText = readAndJoinLines("testfiles/test-program.txt");
        SqlProgram program = AntlrParser.parseProgram(programText);
        String schemaText = readAndJoinLines("testfiles/test-schema.txt");
        SchemaDef schema = AntlrParser.parseSchema(schemaText);
        ResolveColumnVisitor rcVisitor = new ResolveColumnVisitor(schema);
        for (MethodImplementation method : program.methods) {
            for (IQuery query : method.queries) {
                query.accept(rcVisitor);
            }
        }
        System.out.println("program 1 done.");

        // Read and process the second program
        String programText2 = readAndJoinLines("testfiles/test-program-2.txt");
        SqlProgram program2 = AntlrParser.parseProgram(programText2);
        String schemaText2 = readAndJoinLines("testfiles/test-schema-2.txt");
        SchemaDef schema2 = AntlrParser.parseSchema(schemaText2);
        ResolveColumnVisitor rcVisitor2 = new ResolveColumnVisitor(schema2);
        for (MethodImplementation method : program2.methods) {
            for (IQuery query : method.queries) {
                query.accept(rcVisitor2);
            }
        }
        program2.reshape(program.getSignature());
        System.out.println("program 2 done.");

        List<TableDef> tables = schema.tables.values().stream().collect(Collectors.toList());
        List<TableDef> tables2 = schema2.tables.values().stream().collect(Collectors.toList());
        System.out.println("generating test cases...");
        long start1 = System.nanoTime();
        List<List<Invocation<ISqlObject>>> testCases = program.getSignature()
                .testCases(new GeneratedSqlObject.ArraySupplier(0, 1, 2, 3), 5);
        long end1 = System.nanoTime();
        System.out.println("generated " + testCases.size() + " tests in " + (end1 - start1) / 1.0e9 + "s ("
                + (double) (end1 - start1) / testCases.size() / 1.0e6 + "ms/test).");
        System.out.println("running test cases...");

        long start = System.nanoTime();

        // execute the test cases
        Optional<EquivCheckResult> result = testCases.parallelStream().map(test -> {
            ProgramState sourceState = new ProgramState(tables);
            ProgramState targetState = new ProgramState(tables2);
            ImmutableLinkedList<Invocation<ISqlObject>> castedTest = (ImmutableLinkedList<Invocation<ISqlObject>>) test;
            QueryResult srcResult = program.executeList(castedTest, sourceState);
            QueryResult tgtResult = program2.executeList(castedTest, targetState);
            if (!srcResult.matches(sourceState, tgtResult, targetState)) {
                return new EquivCheckResult(srcResult, sourceState, tgtResult, targetState, test);
            }
            return null;
        }).filter(Objects::nonNull).findFirst();

        // print the result
        if (result.isPresent()) {
            System.out.println(result.get());
        } else {
            System.out.println("Equivalent");
        }

        long end = System.nanoTime();
        System.out.println(testCases.size() + " tests executed in " + (end - start) / 1.0e9 + "s ("
                + (double) (end - start) / testCases.size() / 1.0e6 + "ms/test).");

    }
}
