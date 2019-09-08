package migrator.rewrite.ast;

/**
 * A node in the abstract syntax tree.
 */
public interface IAstNode {
    /**
     * Returns a textual representation of this node
     * formatted as it would appear in SQL.
     *
     * @return SQL representation of this node
     */
    public String toSqlString();
}
