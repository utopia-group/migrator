package migrator.rewrite.sql;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import migrator.LoggerWrapper;
import migrator.rewrite.ResolveColumnVisitor;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.program.Invocation;
import migrator.util.ImmutableLinkedList;

public final class EquivalenceChecker {

    private static final Logger LOGGER = LoggerWrapper.getInstance();

    /**
     * Maximum length of invocation sequence.
     */
    public static final int SEQUENCE_BOUND = 2;
    /**
     * Database schema for the source program.
     */
    public final SchemaDef sourceSchema;
    /**
     * Database schema for the target program.
     */
    public final SchemaDef targetSchema;

    public EquivalenceChecker(SchemaDef sourceSchema, SchemaDef targetSchema) {
        this.sourceSchema = sourceSchema;
        this.targetSchema = targetSchema;
    }

    /**
     * Check if the source program is equivalent to the target program. If they are
     * not equivalent, return an invocation sequence as the witness.
     *
     * @param sourceProg source program over the source schema
     * @param targetProg target program over the target schema
     * @return {@code null} if two programs are equivalent, or an
     *         {@link EquivCheckResult} object which includes program states and an
     *         invocation sequence if they are not equivalent
     */
    public EquivCheckResult check(SqlProgram sourceProg, SqlProgram targetProg) {
        resolveColumns(sourceProg, sourceSchema);
        resolveColumns(targetProg, targetSchema);
        targetProg.reshape(sourceProg.signature);

        List<TableDef> sourceTables = sourceSchema.tables.values().stream().collect(Collectors.toList());
        List<TableDef> targetTables = targetSchema.tables.values().stream().collect(Collectors.toList());

        // generate test cases
        List<List<Invocation<ISqlObject>>> testCases = sourceProg.getSignature()
                .testCases(new GeneratedSqlObject.ArraySupplier(1), SEQUENCE_BOUND);
        LOGGER.info("--- Test cases: " + testCases.size());

        // execute the test cases
        Optional<EquivCheckResult> result = testCases.parallelStream().map(test -> {
            ProgramState sourceState = new ProgramState(sourceTables);
            ProgramState targetState = new ProgramState(targetTables);
            ImmutableLinkedList<Invocation<ISqlObject>> castedTest = (ImmutableLinkedList<Invocation<ISqlObject>>) test;
            QueryResult srcResult = sourceProg.executeList(castedTest, sourceState);
            QueryResult tgtResult = targetProg.executeList(castedTest, targetState);
            if (!srcResult.matches(sourceState, tgtResult, targetState)) {
                return new EquivCheckResult(srcResult, sourceState, tgtResult, targetState, test);
            }
            return null;
        }).filter(Objects::nonNull).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    private void resolveColumns(SqlProgram program, SchemaDef schema) {
        ResolveColumnVisitor rcVisitor = new ResolveColumnVisitor(schema);
        for (MethodImplementation method : program.methods) {
            for (IQuery query : method.queries) {
                query.accept(rcVisitor);
            }
        }
    }

}
