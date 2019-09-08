package migrator.bench;

import java.util.List;

/**
 * Json model for transactions in benchmarks.
 */
public class Tran {

    private final String signature;
    private final List<String> body;

    public Tran(String signature, List<String> body) {
        this.signature = signature;
        this.body = body;
    }

    public String getSignature() {
        return signature;
    }

    public List<String> getBody() {
        return body;
    }

}
