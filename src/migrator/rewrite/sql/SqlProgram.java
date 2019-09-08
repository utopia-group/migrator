package migrator.rewrite.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.program.Invocation;
import migrator.rewrite.program.Program;
import migrator.rewrite.program.Signature;

/**
 * A {@link Program} implementation in which
 * methods are sequences of SQL queries.
 */
public final class SqlProgram implements Program<ISqlObject, ProgramState>, IAstNode {
    /**
     * The signature of the program.
     */
    public Signature signature;
    /**
     * The list of methods, matching the order of the signature.
     */
    public List<MethodImplementation> methods;

    /**
     * Constructs a new program with the given methods.
     *
     * @param methods the methods in this program
     */
    public SqlProgram(List<MethodImplementation> methods) {
        this.signature = new Signature(methods.stream().map(MethodImplementation::getSignature).collect(Collectors.toList()));
        this.methods = methods;
    }

    @Override
    public QueryResult execute(Invocation<ISqlObject> invocation, ProgramState state, FreshSqlObject.Factory factory) {
        MethodImplementation method = methods.get(invocation.methodIndex);
        assert method.signature.name.equals(invocation.methodName);
        return method.execute(invocation.arguments, state, factory);
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public void reshape(Signature newSignature) {
        assert newSignature.getMethods().size() == signature.getMethods().size();
        assert newSignature.getMethods().size() == methods.size();
        List<MethodImplementation> newMethods = new ArrayList<>(methods.size());
        for (int i = 0; i < methods.size(); i++) {
            MethodImplementation impl = methods.get(signature.getMethodIndex(newSignature.getMethods().get(i).getName()));
            assert impl != null;
            newMethods.add(impl);
        }
        methods = newMethods;
        signature = newSignature;
    }

    @Override
    public String toSqlString() {
        return methods.stream().map(MethodImplementation::toSqlString).collect(Collectors.joining("\n\n"));
    }
}
