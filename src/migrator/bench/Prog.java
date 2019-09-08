package migrator.bench;

import java.util.List;

/**
 * Json model for programs in benchmarks.
 */
public class Prog {

    private final List<String> relations;
    private final List<String> primarykeys;
    private final List<String> foreignkeys;
    private final List<Tran> transactions;

    public Prog(List<String> relations, List<String> primaryKeys,
            List<String> foreignKeys, List<Tran> transactions) {
        this.relations = relations;
        this.primarykeys = primaryKeys;
        this.foreignkeys = foreignKeys;
        this.transactions = transactions;
    }

    public List<String> getRelations() {
        return relations;
    }

    public List<String> getPrimarykeys() {
        return primarykeys;
    }

    public List<String> getForeignkeys() {
        return foreignkeys;
    }

    public List<Tran> getTransactions() {
        return transactions;
    }

}
