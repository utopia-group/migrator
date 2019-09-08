package migrator.bench;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import migrator.bench.BenchmarkAST.AndPredNode;
import migrator.bench.BenchmarkAST.AstNode;
import migrator.bench.BenchmarkAST.AttrDecl;
import migrator.bench.BenchmarkAST.AttrListNode;
import migrator.bench.BenchmarkAST.DeleteNode;
import migrator.bench.BenchmarkAST.EquiJoinNode;
import migrator.bench.BenchmarkAST.InPredNode;
import migrator.bench.BenchmarkAST.InsertNode;
import migrator.bench.BenchmarkAST.LopPredNode;
import migrator.bench.BenchmarkAST.NotPredNode;
import migrator.bench.BenchmarkAST.OrPredNode;
import migrator.bench.BenchmarkAST.Program;
import migrator.bench.BenchmarkAST.ProjectNode;
import migrator.bench.BenchmarkAST.RelDecl;
import migrator.bench.BenchmarkAST.RelationNode;
import migrator.bench.BenchmarkAST.Schema;
import migrator.bench.BenchmarkAST.SelectNode;
import migrator.bench.BenchmarkAST.Signature;
import migrator.bench.BenchmarkAST.Transaction;
import migrator.bench.BenchmarkAST.TupleNode;
import migrator.bench.BenchmarkAST.UpdateNode;

/**
 * Convert legacy benchmarks to the new representation (close to SQL).
 */
public class ProgramConverter implements IASTVisitor<String> {

    /**
     * Convert legacy transactions to the new representation.
     *
     * @param transactions a list of legacy transactions
     * @return new representation string
     */
    public static String convertTransactions(List<Transaction> transactions) {
        ProgramConverter converter = new ProgramConverter();
        StringBuilder builder = new StringBuilder();
        for (Transaction transaction : transactions) {
            builder.append(converter.visit(transaction));
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    /**
     * Convert legacy schemas to the new representation.
     *
     * @param schema legacy schema
     * @return new representation string
     */
    public static String convertSchema(Schema schema) {
        return new ProgramConverter().visit(schema);
    }

    private final String newLine = System.lineSeparator();

    @Override
    public String visit(Program program) {
        StringBuilder builder = new StringBuilder();
        builder.append(program.schema.accept(this)).append(newLine);
        for (Transaction transaction : program.transactions) {
            builder.append(transaction.accept(this)).append(newLine);
        }
        return builder.toString();
    }

    @Override
    public String visit(Schema schema) {
        StringBuilder builder = new StringBuilder();
        for (RelDecl relDecl : schema.relDecls) {
            builder.append(relDecl.accept(this)).append(newLine);
        }
        return builder.toString();
    }

    @Override
    public String visit(RelDecl relDecl) {
        StringBuilder builder = new StringBuilder();
        AttrDecl primaryKey = null;
        List<AttrDecl> foreignKeys = new ArrayList<>();
        builder.append("CREATE TABLE ").append(relDecl.name).append(" (").append(newLine);
        for (AttrDecl attrDecl : relDecl.attrDecls) {
            if (attrDecl.isPrimaryKey()) {
                assert primaryKey == null : "Duplicated primary key: " + attrDecl.name + " and " + primaryKey.name;
                primaryKey = attrDecl;
            }
            if (attrDecl.isForeignKey()) {
                foreignKeys.add(attrDecl);
            }
            builder.append("    ").append(attrDecl.accept(this)).append(",").append(newLine);
        }
        // foreign keys
        for (AttrDecl foreignKey : foreignKeys) {
            builder.append("    FOREIGN KEY (").append(convertAttrDeclName(foreignKey.name)).append(") REFERENCES ");
            AttrDecl refAttr = foreignKey.getReferenceAttrDecl();
            RelDecl refRel = refAttr.getRelDecl();
            builder.append(refRel.name).append(" (").append(convertAttrDeclName(refAttr.name)).append("),").append(newLine);
        }
        // primary key
        if (primaryKey != null) {
            builder.append("    PRIMARY KEY (").append(convertAttrDeclName(primaryKey.name)).append(")").append(newLine);
        } else {
            builder.delete(builder.length() - newLine.length() - 1, builder.length() - newLine.length());
        }
        builder.append(");").append(newLine);
        return builder.toString();
    }

    @Override
    public String visit(AttrDecl attrDecl) {
        StringBuilder builder = new StringBuilder();
        // convert int to INT, String to VARCHAR
        builder.append(convertAttrDeclName(attrDecl.name)).append(" ");
        if (attrDecl.type.equals("int")) {
            builder.append("INT");
        } else if (attrDecl.type.equals("String")) {
            builder.append("VARCHAR");
        } else {
            throw new RuntimeException("Unknown attribute type: " + attrDecl.type);
        }
        return builder.toString();
    }

    @Override
    public String visit(Transaction tran) {
        if (isDelTransaction(tran)) {
            return convertDelTrans(tran);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(tran.signature.accept(this)).append(" {").append(newLine);
        for (AstNode statement : tran.statements) {
            builder.append(statement.accept(this));
        }
        builder.append("}").append(newLine);
        return builder.toString();
    }

    // parameters in the current transaction
    private Set<String> currParams;

    @Override
    public String visit(Signature sig) {
        // reset the set of current parameters
        currParams = sig.arguments.stream().collect(Collectors.toSet());

        // translate the signature
        StringBuilder builder = new StringBuilder();
        if (sig.returnType.equals("void")) {
            builder.append("update");
        } else if (sig.returnType.equals("List<Tuple>")) {
            builder.append("query");
        } else {
            throw new RuntimeException("Unknown return type: " + sig.toString());
        }
        builder.append(" ").append(sig.name).append("(");
        assert sig.argumentTypes.size() == sig.arguments.size();
        for (int i = 0; i < sig.argumentTypes.size(); ++i) {
            builder.append(sig.argumentTypes.get(i)).append(" ");
            builder.append(convertParam(sig.arguments.get(i))).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visit(InsertNode ins) {
        StringBuilder builder = new StringBuilder();
        builder.append("    INSERT INTO ").append(ins.relation.accept(this));
        // explicitly list all attributes
        builder.append(" (");
        for (AttrDecl attrDecl : ins.relation.relDecl.attrDecls) {
            builder.append(convertAttrName(attrDecl.name)).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(") VALUES ").append(ins.tuple.accept(this));
        builder.append(";").append(newLine);
        return builder.toString();
    }

    /**
     * Only handle simple delete statements.
     *
     * @param del a simple delete statement
     * @return new representation string
     */
    @Override
    public String visit(DeleteNode del) {
        assert !isCompoundDelete(del) : del.toString();
        StringBuilder builder = new StringBuilder();
        builder.append("    DELETE FROM ").append(del.relation.accept(this));
        builder.append(" WHERE ").append(del.pred.accept(this));
        builder.append(";").append(newLine);
        return builder.toString();
    }

    @Override
    public String visit(UpdateNode upd) {
        if (isCompoundUpdate(upd)) {
            return convertCompoundUpdate(upd);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("    UPDATE ").append(upd.relation.accept(this));
        builder.append(" SET ").append(convertAttrName(upd.attr)).append(" = ").append(convertValue(upd.value));
        builder.append(" WHERE ").append(upd.pred.accept(this));
        builder.append(";").append(newLine);
        return builder.toString();
    }

    @Override
    public String visit(TupleNode tuple) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (String value : tuple.values) {
            builder.append(convertValue(value)).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visit(AttrListNode attrList) {
        StringBuilder builder = new StringBuilder();
        for (String attr : attrList.attributes) {
            builder.append(convertAttrName(attr)).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    @Override
    public String visit(RelationNode rel) {
        return rel.name;
    }

    @Override
    public String visit(ProjectNode proj) {
        StringBuilder builder = new StringBuilder();
        builder.append("    SELECT ").append(proj.attrList.accept(this));
        builder.append(" FROM ").append(proj.relation.accept(this));
        builder.append(";").append(newLine);
        return builder.toString();
    }

    @Override
    public String visit(SelectNode sel) {
        StringBuilder builder = new StringBuilder();
        builder.append(sel.relation.accept(this));
        builder.append(" WHERE ").append(sel.pred.accept(this));
        return builder.toString();
    }

    @Override
    public String visit(EquiJoinNode join) {
        StringBuilder builder = new StringBuilder();
        builder.append(join.lhs.accept(this));
        builder.append(" JOIN ").append(join.rhs.accept(this));
        builder.append(" ON ");
        builder.append(convertJoinPred(join.leftRelDecl.name, join.leftAttr, join.rightRelDecl.name, join.rightAttr));
        return builder.toString();
    }

    @Override
    public String visit(LopPredNode lop) {
        StringBuilder builder = new StringBuilder();
        builder.append(convertValue(lop.lhs)).append(" ");
        builder.append(BenchmarkAST.operatorToString(lop.op)).append(" ");
        builder.append(convertValue(lop.rhs));
        return builder.toString();
    }

    @Override
    public String visit(InPredNode in) {
        throw new RuntimeException("Unreachable: " + in.toString());
    }

    @Override
    public String visit(AndPredNode and) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(and.lhs.accept(this)).append(") AND (");
        builder.append(and.rhs.accept(this)).append(")");
        return builder.toString();
    }

    @Override
    public String visit(OrPredNode or) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(or.lhs.accept(this)).append(") OR (");
        builder.append(or.rhs.accept(this)).append(")");
        return builder.toString();
    }

    @Override
    public String visit(NotPredNode not) {
        StringBuilder builder = new StringBuilder();
        builder.append("NOT (").append(not.pred.accept(this)).append(")");
        return builder.toString();
    }

    private String convertAttrName(String attrName) {
        if (attrName.contains(".")) {
            String[] tokens = attrName.split("\\.");
            assert tokens.length == 2 : "Multiple dots in " + attrName;
            return tokens[0] + "." + escapeKeyWords(tokens[1]);
        } else {
            return attrName;
        }
    }

    private String convertAttrDeclName(String attrDeclName) {
        if (attrDeclName.contains(".")) {
            String[] tokens = attrDeclName.split("\\.");
            assert tokens.length == 2 : "Multiple dots in " + attrDeclName;
            return escapeKeyWords(tokens[1]);
        } else {
            return attrDeclName;
        }
    }

    private String convertJoinPred(String lhsRel, String lhsAttr, String rhsRel, String rhsAttr) {
        lhsAttr = convertAttrName(lhsAttr);
        rhsAttr = convertAttrName(rhsAttr);
        StringBuilder builder = new StringBuilder();
        if (lhsAttr.contains(".")) {
            builder.append(lhsAttr);
        } else {
            builder.append(lhsRel).append(".").append(lhsAttr);
        }
        builder.append(" = ");
        if (rhsAttr.contains(".")) {
            builder.append(rhsAttr);
        } else {
            builder.append(rhsRel).append(".").append(rhsAttr);
        }
        return builder.toString();
    }

    private String escapeKeyWords(String word) {
        if (word.equals("primary")) {
            return "_primary";
        } else if (word.equals("key")) {
            return "_key";
        } else {
            return word;
        }
    }

    private String convertParam(String param) {
        return escapeKeyWords(param);
    }

    private String convertValue(String value) {
        if (value.startsWith("UUID_x")) {
            // fresh values
            String index = value.substring(6); // "UUID_x".length == 6
            return "FRESH(" + index + ")";
        } else if (value.startsWith("UUID_f_")) {
            // special parameters in legacy benchmarks
            String param = value.substring(7); // "UUID_f_".length == 7
            return "<" + convertParam(param) + ">";
        } else if (currParams.contains(value)) {
            // parameters
            return "<" + convertParam(value) + ">";
        } else {
            // attributes or constants
            // convertAttrName would not do anything for constants
            return convertAttrName(value);
        }
    }

    private boolean isCompoundUpdate(UpdateNode upd) {
        return upd.pred instanceof InPredNode;
    }

    private String convertCompoundUpdate(UpdateNode upd) {
        assert upd.pred instanceof InPredNode : upd.toString() + " is not compound";
        InPredNode inPred = (InPredNode) upd.pred;
        String leftAttr = convertAttrName(inPred.lhs);
        assert inPred.rhsExpr instanceof ProjectNode : inPred.rhsExpr.toString() + " is not projection";
        ProjectNode proj = (ProjectNode) inPred.rhsExpr;
        assert proj.attrList.attributes.size() == 1;
        String rightAttr = convertAttrName(proj.attrList.attributes.get(0));
        assert proj.relation instanceof SelectNode : proj.relation.toString() + " is not filter";
        SelectNode sel = (SelectNode) proj.relation;
        assert sel.relation instanceof RelationNode : sel.relation.toString() + " is not a table";
        RelationNode rel = (RelationNode) sel.relation;

        StringBuilder builder = new StringBuilder();
        builder.append("    UPDATE ").append(upd.relation.name).append(" JOIN ");
        builder.append(rel.name).append(" ON ");
        builder.append(convertJoinPred(upd.relation.name, leftAttr, rel.name, rightAttr));
        builder.append(" SET ").append(convertAttrName(upd.attr)).append(" = ").append(convertValue(upd.value));
        builder.append(" WHERE ").append(sel.pred.accept(this));
        builder.append(";").append(newLine);
        return builder.toString();
    }

    private boolean isCompoundDelete(DeleteNode del) {
        return del.pred instanceof InPredNode;
    }

    /**
     * Check if the transaction is a delete transaction.
     *
     * @param tran the transaction
     * @return {@code true} if it is a delete transaction
     */
    private boolean isDelTransaction(Transaction tran) {
        // assume: if the first statement is delete, then all statements are deletes
        return tran.statements.get(0) instanceof DeleteNode;
    }

    /**
     * Translate the delete transaction in a special way in order to merge multiple
     * delete statements.
     *
     * @param tran the delete transaction
     * @return the new representation string
     */
    private String convertDelTrans(Transaction tran) {
        List<List<DeleteNode>> delGroups = groupDeleteStmts(tran.statements);

        StringBuilder builder = new StringBuilder();
        builder.append(tran.signature.accept(this)).append(" {").append(newLine);
        for (List<DeleteNode> delGroup : delGroups) {
            builder.append(convertDelGroup(delGroup));
        }
        builder.append("}").append(newLine);
        return builder.toString();
    }

    private List<List<DeleteNode>> groupDeleteStmts(List<AstNode> stmts) {
        // TODO: sophisticated heuristics
        List<List<DeleteNode>> groups = new ArrayList<>();
        for (AstNode stmt : stmts) {
            assert stmt instanceof DeleteNode : stmt.toString();
            DeleteNode del = (DeleteNode) stmt;
            if (!isCompoundDelete(del)) {
                List<DeleteNode> group = new ArrayList<>();
                group.add(del);
                for (AstNode groupStmt : stmts) {
                    DeleteNode groupDel = (DeleteNode) groupStmt;
                    if (isCompoundDelete(groupDel)) {
                        RelationNode rel = extractRelationNode((InPredNode) groupDel.pred);
                        if (rel.equals(del.relation)) {
                            group.add(groupDel);
                        }
                    }
                }
                groups.add(group);
            }
        }
        return groups;
    }

    private String convertDelGroup(List<DeleteNode> delGroup) {
        assert delGroup.size() > 0;
        // assume: the first delete in the group is a simple delete
        DeleteNode simpleDel = delGroup.get(0);
        if (delGroup.size() == 1) {
            return visit(simpleDel);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("    DELETE ").append(simpleDel.relation.name);
        for (int i = 1; i < delGroup.size(); ++i) {
            DeleteNode del = delGroup.get(i);
            assert isCompoundDelete(del);
            builder.append(", ").append(del.relation.name);
        }
        builder.append(" FROM ").append(simpleDel.relation.name);
        // join chain
        for (int i = 1; i < delGroup.size(); ++i) {
            DeleteNode del = delGroup.get(i);
            builder.append(" JOIN ").append(del.relation.name);
            builder.append(" ON ");
            InPredNode inPred = (InPredNode) del.pred;
            assert inPred.rhsExpr instanceof ProjectNode;
            ProjectNode proj = (ProjectNode) inPred.rhsExpr;
            builder.append((convertJoinPred(
                    simpleDel.relation.name,
                    proj.attrList.attributes.get(0),
                    del.relation.name,
                    inPred.lhs)));
        }
        builder.append(" WHERE ").append(simpleDel.pred.accept(this));
        builder.append(";").append(newLine);
        return builder.toString();
    }

    private RelationNode extractRelationNode(InPredNode inPred) {
        assert inPred.rhsExpr instanceof ProjectNode;
        ProjectNode proj = (ProjectNode) inPred.rhsExpr;
        assert proj.relation instanceof SelectNode;
        SelectNode sel = (SelectNode) proj.relation;
        assert sel.relation instanceof RelationNode;
        return (RelationNode) sel.relation;
    }

}
