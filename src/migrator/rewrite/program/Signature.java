package migrator.rewrite.program;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import migrator.rewrite.sql.ISqlObject;
import migrator.util.ImmutableLinkedList;

/**
 * The signature of a program. Has a list of methods.
 */
public final class Signature {
    /**
     * List of methods.
     */
    private List<Method> methodList;
    /**
     * Map from method name to index in {@link #methodList}.
     */
    private Map<String, Integer> methodMap;

    /**
     * Constructs a new signature with the given methods.
     *
     * @param methods the list of methods in this signature
     */
    public Signature(List<Method> methods) {
        methodList = methods;
        methodMap = new HashMap<>();
        for (int i = 0; i < methods.size(); i++) {
            Method m = methods.get(i);
            Integer old = methodMap.put(m.name, i);
            if (old != null) {
                throw new IllegalArgumentException(
                        String.format("method with duplicate name: %s (previous: %s)",
                                m, methodList.get(i)));
            }
        }
    }

    /**
     * Gets the list of methods in this signature.
     *
     * @return the list of methods
     */
    public List<Method> getMethods() {
        return methodList;
    }

    /**
     * Returns the index of the method with the given name, or {@code null} if not found.
     *
     * @param name the name of the method
     * @return the index of the method with the given name, or {@code null} if not found
     */
    public Integer getMethodIndex(String name) {
        return methodMap.get(name);
    }

    /**
     * Returns a list of all test cases with a given bound on the number of updates.
     *
     * @param bound              the maximum number of updates in a test case
     * @param invocationSupplier a function that gives the invocations for a method
     * @param <T>                the type of the value supplied by the value supplier
     * @return a list of all test cases up to the given bound
     */
    public <T> List<List<Invocation<T>>> testCases(int bound, Function<Method, Stream<Invocation<T>>> invocationSupplier) {
        // this is pretty slow and doesn't lend itself well to parallelism
        List<Method> updates = methodList.stream()
                .filter(method -> method.type == MethodType.UPDATE)
                .collect(Collectors.toList());
        List<ImmutableLinkedList<Invocation<T>>> tests = methodList.stream()
                .filter(method -> method.type == MethodType.QUERY)
                .flatMap(method -> invocationSupplier.apply(method))
                .map(invocation -> {
                    invocation.methodIndex = getMethodIndex(invocation.methodName);
                    return invocation;
                })
                .map(invocation -> ImmutableLinkedList.singleton(invocation))
                .collect(Collectors.toList());
        int start = 0;
        // in each iteration, append each update to the last generation
        for (int i = 0; i < bound; i++) {
            int nextStart = tests.size();
            tests.addAll(tests.subList(start, tests.size()).stream()
                    .flatMap(rest -> updates.stream()
                            .flatMap(update -> invocationSupplier.apply(update)
                                    .map(invocation -> {
                                        invocation.methodIndex = getMethodIndex(invocation.methodName);
                                        return invocation;
                                    })
                                    .map(invocation -> ImmutableLinkedList.cons(invocation, rest))))
                    .collect(Collectors.toList()));
            start = nextStart;
        }
        // upcast
        return tests.stream().collect(Collectors.toList());
    }

    /**
     * Returns a list of all test cases with a given bound
     * on the number of updates.
     *
     * @param valueSupplier the value supplier to use for all methods
     * @param bound         the maximum number of updates in a test case
     * @param <T>           the type of the value supplied by the value supplier
     * @return a list of all test cases up to the given bound
     */
    public <T> List<List<Invocation<T>>> testCases(Parameter.ValueSupplier<? extends T> valueSupplier, int bound) {
        return testCases(bound, method -> method.invocations(valueSupplier));
    }

    /**
     * Returns a list of all test cases with a given bound
     * on the number of updates. Arguments are sequential within each invocation.
     *
     * @param bound the maximum number of updates in a test case
     * @param <T>   the type of the value supplied by the value supplier
     * @return a list of all test cases up to the given bound
     */
    public List<List<Invocation<ISqlObject>>> incrementingTestCases(int bound) {
        return testCases(bound, method -> method.incrementingInvocations(0));
    }

    @Override
    public String toString() {
        return String.format("Signature(%s)", methodList);
    }
}
