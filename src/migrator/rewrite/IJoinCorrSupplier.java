package migrator.rewrite;

import java.util.List;
import java.util.Set;

import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.ColumnRef;

/**
 * Interface for join correspondence suppliers.
 */
public interface IJoinCorrSupplier {

    /**
     * Generate all corresponding join chains in the target for the given join chain
     * in the source schema.
     *
     * @param valueCorr value correspondence
     * @param chain     join chain in the source schema
     * @return corresponding join chains in the target schema
     */
    public List<JoinChain> getJoinChains(ValueCorrespondence valueCorr, JoinChain chain);

    /**
     * Generate all corresponding join chains in the target for the given join chain
     * and its columns in the source schema.
     *
     * @param valueCorr value correspondence
     * @param chain     join chain in the source schema
     * @param columns   a set of columns in chain
     * @return corresponding join chains in the target schema
     */
    public List<JoinChain> getJoinChainsForColumns(ValueCorrespondence valueCorr, JoinChain chain, Set<ColumnRef> columns);

}
