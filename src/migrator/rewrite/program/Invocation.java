package migrator.rewrite.program;

import java.util.List;

/**
 * An invocation of a method.
 *
 * @param <T> the type of the values of the parameters
 */
public final class Invocation<T> {
    /**
     * The name of the method to be invoked.
     * If {@code sig} is the signature of the program,
     * it should hold that {@code sig.methodList.get(methodIndex).name.equals(methodName)}.
     *
     * @see #methodIndex
     */
    public String methodName;
    /**
     * The index of the method to be invoked.
     * If {@code sig} is the signature of the program,
     * it should hold that {@code sig.methodList.get(methodIndex).name.equals(methodName)}.
     * Can be -1 if unknown.
     *
     * @see #methodName
     */
    public int methodIndex;
    /**
     * The arguments (actual parameters) to the method.
     * The number of arguments should match the number of parameters.
     */
    public List<T> arguments;

    /**
     * Constructs a new invocation of the given method with the given arguments.
     *
     * @param methodName  the name of the method to invoke
     * @param methodIndex the index of the method to invoke
     * @param arguments   the arguments in the method invocation
     */
    public Invocation(String methodName, int methodIndex, List<T> arguments) {
        this.methodName = methodName;
        this.methodIndex = methodIndex;
        this.arguments = arguments;
    }

    /**
     * Constructs a new invocation of the given method with unspecified index
     * and the given arguments.
     *
     * @param methodName the name of the method to invoke
     * @param arguments  the arguments in the method invocation
     */
    public Invocation(String methodName, List<T> arguments) {
        this(methodName, -1, arguments);
    }

    /**
     * Looks up the index of this invocation's method name
     * and stores it in {@link #methodIndex}.
     *
     * @param signature the signature to use in lookup
     * @return {@code this}
     */
    public Invocation<T> lookup(Signature signature) {
        this.methodIndex = signature.getMethodIndex(methodName);
        return this;
    }

    @Override
    public String toString() {
        return String.format("Invocation(%s, %s, %s)", methodName, methodIndex, arguments);
    }
}
