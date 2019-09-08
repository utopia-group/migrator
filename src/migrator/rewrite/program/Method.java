package migrator.rewrite.program;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.sql.GeneratedSqlObject;
import migrator.rewrite.sql.ISqlObject;
import migrator.util.ImmutableLinkedList;

/**
 * A method in a program. Has a name and a list of parameters.
 */
public final class Method implements IAstNode {
    /**
     * The name of this method.
     */
    public String name;
    /**
     * The list of parameters to this method.
     */
    public List<Parameter> parameters;
    /**
     * The type of this method.
     */
    public MethodType type;

    /**
     * Constructs a new method with the given name and parameters.
     *
     * @param name       the name of this method
     * @param parameters the list of parameters to this method
     * @param type       the type of method of this method
     */
    public Method(String name, List<Parameter> parameters, MethodType type) {
        this.name = name;
        this.parameters = parameters;
        this.type = type;
    }

    /**
     * Gets the name of this method.
     *
     * @return the name of this method
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of parameters to this method.
     *
     * @return the list of parameters to this method
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Gets the type of this method.
     *
     * @return the type of this method
     */
    public MethodType getType() {
        return type;
    }

    /**
     * Returns a stream of the names of the parameters to this method.
     *
     * @return a stream of the the names of the parameters
     */
    public Stream<String> parameterNames() {
        return parameters.stream().map(Parameter::getName);
    }

    /**
     * Returns a stream of all the possible invocations of this method
     * using the values supplied by the given supplier.
     *
     * @param valueSupplier a supplier of values for a given parameter
     * @param <T>           the type of the arguments in the invocations
     * @return a stream of all possible invocations of this method
     */
    public <T> Stream<Invocation<T>> invocations(Parameter.ValueSupplier<? extends T> valueSupplier) {
        Stream<ImmutableLinkedList<T>> argumentsStream = Stream.of(ImmutableLinkedList.empty());
        // essentially argumentsStream = fmap Invocation $ traverse valueSupplier parameters
        // so we perform a right fold
        for (ListIterator<Parameter> it = parameters.listIterator(parameters.size()); it.hasPrevious();) {
            Parameter param = it.previous();
            argumentsStream = argumentsStream.flatMap(rest -> valueSupplier.values(param).map(value -> ImmutableLinkedList.cons(value, rest)));
        }
        return argumentsStream.map(args -> new Invocation<>(name, args));
    }

    public Stream<Invocation<ISqlObject>> incrementingInvocations(int base) {
        ImmutableLinkedList<ISqlObject> list = ImmutableLinkedList.empty();
        for (ListIterator<Parameter> it = parameters.listIterator(parameters.size()); it.hasPrevious();) {
            int i = it.previousIndex() + base;
            it.previous();
            list = ImmutableLinkedList.cons(new GeneratedSqlObject(i), list);
        }
        return Stream.of(new Invocation<>(name, list));
    }

    @Override
    public String toSqlString() {
        return String.format("%s %s(%s)", type.toSqlString(), name,
                parameters.stream().map(parameter -> parameter.toSqlString()).collect(Collectors.joining(", ")));
    }

    @Override
    public String toString() {
        return String.format("Method(%s, %s, %s)", name, parameters, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters, type);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Method)) {
            return false;
        }
        Method m = (Method) other;
        return Objects.equals(name, m.name)
                && Objects.equals(parameters, m.parameters)
                && Objects.equals(type, m.type);
    }
}
