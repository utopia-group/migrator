package migrator.rewrite.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.program.Method;
import migrator.rewrite.program.MethodType;
import migrator.rewrite.program.Parameter;

/**
 * An implementation of a method.
 * Consists of a method signature and the body, a list of queries.
 */
public final class MethodImplementation implements IAstNode {
    /**
     * The signature of this method.
     */
    public Method signature;
    /**
     * The queries in the body of this method.
     */
    public List<IQuery> queries;

    /**
     * Constructs a new method implementation with the given signature and body.
     *
     * @param signature the signature of the method
     * @param queries   the body of the method
     */
    public MethodImplementation(Method signature, List<IQuery> queries) {
        this.signature = signature;
        this.queries = queries;
    }

    /**
     * Executes this method with the given arguments and program state
     *
     * @param arguments the arguments, in the order specified by the signature
     * @param state     the state to use and modify
     * @return the result of the query, or {@code null} if it is an update
     */
    public QueryResult execute(List<ISqlObject> arguments, ProgramState state, FreshSqlObject.Factory factory) {
        Map<String, ISqlObject> argumentMap = new HashMap<>();
        Iterator<ISqlObject> argIt = arguments.iterator();
        Iterator<Parameter> paramIt = signature.parameters.iterator();
        while (argIt.hasNext()) {
            assert paramIt.hasNext();
            Parameter param = paramIt.next();
            ISqlObject arg = argIt.next();
            argumentMap.put(param.name, arg);
        }
        QueryExecuteVisitor visitor = new QueryExecuteVisitor(state, argumentMap, factory);
        if (signature.type == MethodType.QUERY) {
            if (queries.size() != 1) {
                throw new IllegalStateException("query-type methods should only have one query");
            }
            QueryResult result = queries.get(0).accept(visitor);
            if (result == null) {
                throw new IllegalStateException("query in query-type method is not a query");
            }
            return result;
        }
        for (IQuery query : queries) {
            QueryResult result = query.accept(visitor);
            if (result != null) {
                throw new IllegalStateException("query in update-type method is a query");
            }
        }
        return null;
    }

    @Override
    public String toSqlString() {
        return signature.toSqlString() + " {\n" +
                queries.stream().map(query -> "    " + query.toSqlString() + "\n").collect(Collectors.joining())
                + "}";
    }

    /**
     * Gets the signature of this method
     *
     * @return the signature of this method
     */
    public Method getSignature() {
        return signature;
    }
}
