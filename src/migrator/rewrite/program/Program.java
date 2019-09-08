package migrator.rewrite.program;

import migrator.rewrite.sql.ErrorQueryResult;
import migrator.rewrite.sql.FreshSqlObject;
import migrator.rewrite.sql.QueryExecutionException;
import migrator.rewrite.sql.QueryResult;
import migrator.util.ImmutableLinkedList;

/**
 * A concrete implementation of a signature.
 *
 * @param <Arg>   the type of the arguments to methods
 * @param <State> the type of state used by the program
 */
public interface Program<Arg, State> {
    /**
     * Executes the given invocation.
     *
     * @param invocation the invocation to run
     * @param state      the state to use
     * @return the result of the query, or {@code null} if the method is an update
     * @throws IllegalArgumentException if the invocation is invalid
     */
    QueryResult execute(Invocation<Arg> invocation, State state, FreshSqlObject.Factory factory);

    /**
     * Executes the given list of invocations, returning
     * the result of the last invocation.
     *
     * @param invocations the list of invocations to execute
     * @param state       the state to use
     * @return the result of the last invocation
     * @throws IllegalArgumentException if any invocation is invalid
     *                                  or if an invocation that is not the last is a query
     *                                  or if the last invocation is an update
     *                                  or if the list is empty
     */
    default QueryResult executeList(ImmutableLinkedList<Invocation<Arg>> invocations, State state) {
        if (invocations.isEmpty()) {
            throw new IllegalArgumentException("empty list of invocations");
        }
        FreshSqlObject.Factory freshFactory = new FreshSqlObject.Factory();
        while (!invocations.cdr().isEmpty()) {
            Invocation<Arg> invocation = invocations.car();
            invocations = invocations.cdr();
            try {
                QueryResult result = execute(invocation, state, freshFactory);
                if (result != null) {
                    throw new IllegalArgumentException("query not last in list of invocations");
                }
            } catch (QueryExecutionException e) {
                return new ErrorQueryResult(e);
            }
        }
        Invocation<Arg> queryInvocation = invocations.car();
        assert invocations.cdr().isEmpty();
        QueryResult result = execute(queryInvocation, state, freshFactory);
        if (result == null) {
            throw new IllegalArgumentException("last invocation not a query");
        }
        return result;
    }

    /**
     * Gets the signature of this program.
     *
     * @return the signature of this program
     */
    Signature getSignature();

    /**
     * Reshapes this program to use the given signature,
     * which must have the same methods, except in a different order.
     * <p>
     * This is an optional operation.
     *
     * @param newSignature the new signature
     * @throws UnsupportedOperationException if this program cannot be reshaped
     */
    void reshape(Signature newSignature);
}
