package migrator.sketch;

import java.util.List;
import java.util.stream.Collectors;

import migrator.rewrite.program.Invocation;
import migrator.rewrite.sql.ISqlObject;

/**
 * Invocation sequence for database programs.
 */
public final class InvocationSequence {
    /**
     * A sequence of invocations.
     */
    public final List<Invocation<ISqlObject>> invocations;

    public InvocationSequence(List<Invocation<ISqlObject>> invocations) {
        this.invocations = invocations;
    }

    @Override
    public String toString() {
        return invocations.stream().map(Invocation::toString).collect(Collectors.joining("\n"));
    }
}
