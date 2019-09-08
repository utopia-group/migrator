package migrator.bench;

/**
 * Json model for benchmarks.
 */
public class Benchmark {

    private final Prog source;
    private final Prog target;

    public Benchmark(Prog source, Prog target) {
        this.source = source;
        this.target = target;
    }

    public Prog getSource() {
        return source;
    }

    public Prog getTarget() {
        return target;
    }

}
