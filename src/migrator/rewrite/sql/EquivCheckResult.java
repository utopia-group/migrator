package migrator.rewrite.sql;

import java.util.List;

import migrator.rewrite.program.Invocation;

/**
 * Data structure for storing results of equivalence check.
 */
public class EquivCheckResult {
    /**
     * Query result of the source program.
     */
    public final QueryResult sourceResult;
    /**
     * Query result of the target program.
     */
    public final QueryResult targetResult;
    /**
     * State (database instance) of the source program.
     */
    public final ProgramState sourceState;
    /**
     * State (database instance) of the target program.
     */
    public final ProgramState targetState;
    /**
     * Invocation sequence.
     */
    public final List<Invocation<ISqlObject>> invocations;

    public EquivCheckResult(QueryResult sourceResult, ProgramState sourceState, QueryResult targetResult,
            ProgramState targetState, List<Invocation<ISqlObject>> invocations) {
        this.sourceResult = sourceResult;
        this.sourceState = sourceState;
        this.targetResult = targetResult;
        this.targetState = targetState;
        this.invocations = invocations;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("mismatch for invocation: " + invocations).append(System.lineSeparator());
        builder.append("source result:").append(System.lineSeparator());
        builder.append(sourceResult.print(sourceState)).append(System.lineSeparator());
        builder.append("target result:").append(System.lineSeparator());
        builder.append(targetResult.print(targetState)).append(System.lineSeparator());
        return builder.toString();
    }

}
