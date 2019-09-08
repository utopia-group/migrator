package migrator.rewrite.sql;

public final class ErrorQueryResult extends QueryResult {
    public QueryExecutionException exception;

    public ErrorQueryResult(QueryExecutionException e) {
        super(null, null);
        exception = e;
    }

    @Override
    public String print(ProgramState state) {
        return "<ERROR: " + exception.getMessage() + ">";
    }
}
