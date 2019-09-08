package migrator.bench;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AST for benchmarks.
 */
public class BenchmarkAST {

    /**
     * Abstract class for AST nodes in transaction bodies.
     */
    public static abstract class AstNode {

        /**
         * Accept a visitor and dispatch it to the method for the corresponding type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public abstract <T> T accept(IASTVisitor<T> visitor);

    }

    /**
     * Abstract class for terms in {@code QUERY} statements.
     */
    public static abstract class AstTerm extends AstNode {
    }

    /**
     * AST node for relations (tables).
     */
    public static class RelationNode extends AstTerm {

        public final String name;
        public final RelDecl relDecl;

        public RelationNode(String name, RelDecl relDecl) {
            this.name = name;
            this.relDecl = relDecl;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof RelationNode)
                return name.equals(((RelationNode) o).name);
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for projections.
     */
    public static class ProjectNode extends AstTerm {

        public final AttrListNode attrList;
        public final AstTerm relation;

        public ProjectNode(AttrListNode attrList, AstTerm relation) {
            this.attrList = attrList;
            this.relation = relation;
        }

        @Override
        public String toString() {
            return String.format("pi(%s, %s)", attrList, relation);
        }

        @Override
        public int hashCode() {
            return (17 * attrList.hashCode()) ^ (19 * relation.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof ProjectNode) {
                return attrList.equals(((ProjectNode) o).attrList) && relation.equals(((ProjectNode) o).relation);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for attribute lists.
     */
    public static class AttrListNode extends AstNode {

        public final List<String> attributes;

        public AttrListNode(List<String> attributes) {
            this.attributes = attributes;
        }

        @Override
        public String toString() {
            return attributes.toString();
        }

        @Override
        public int hashCode() {
            return attributes.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof AttrListNode) {
                return attributes.equals(((AttrListNode) o).attributes);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for filters.
     */
    public static class SelectNode extends AstTerm {

        public final Predicate pred;
        public final AstTerm relation;

        public SelectNode(Predicate rowPred, AstTerm relation) {
            this.pred = rowPred;
            this.relation = relation;
        }

        @Override
        public String toString() {
            return String.format("sigma(%s, %s)", pred, relation);
        }

        @Override
        public int hashCode() {
            return (23 * pred.hashCode()) ^ (29 * relation.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof SelectNode) {
                return pred.equals(((SelectNode) o).pred) && relation.equals(((SelectNode) o).relation);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for equi-joins.
     */
    public static class EquiJoinNode extends AstTerm {

        public final AstTerm lhs;
        public final AstTerm rhs;
        public final String leftAttr;
        public final String rightAttr;
        public final RelDecl leftRelDecl;
        public final RelDecl rightRelDecl;

        public EquiJoinNode(AstTerm lhs, AstTerm rhs, String leftAttr, String rightAttr, RelDecl leftRelDecl,
                RelDecl rightRelDecl) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.leftAttr = leftAttr;
            this.rightAttr = rightAttr;
            this.leftRelDecl = leftRelDecl;
            this.rightRelDecl = rightRelDecl;
        }

        @Override
        public String toString() {
            return String.format("join(%s, %s)", lhs, rhs);
        }

        @Override
        public int hashCode() {
            return (2 * lhs.hashCode()) ^ (3 * rhs.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof EquiJoinNode) {
                return lhs.equals(((EquiJoinNode) o).lhs) && rhs.equals(((EquiJoinNode) o).rhs);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for {@code INSERT} statements.
     */
    public static class InsertNode extends AstNode {

        public final RelationNode relation;
        public final TupleNode tuple;

        public InsertNode(RelationNode relation, TupleNode tuple) {
            this.relation = relation;
            this.tuple = tuple;
        }

        @Override
        public String toString() {
            return String.format("ins(%s, %s)", relation, tuple);
        }

        @Override
        public int hashCode() {
            return (5 * relation.hashCode()) ^ (11 * tuple.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof InsertNode) {
                return relation.equals(((InsertNode) o).relation) && tuple.equals(((InsertNode) o).tuple);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for a tuple of values.
     */
    public static class TupleNode extends AstNode {

        public final List<String> values;

        public TupleNode(List<String> values) {
            this.values = values;
        }

        @Override
        public String toString() {
            return String.format("(%s)", values.stream().collect(Collectors.joining(", ")));
        }

        @Override
        public int hashCode() {
            int hash = 1;
            for (String value : values) {
                hash = hash * 31 + value.hashCode();
            }
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof TupleNode) {
                return values.equals(((TupleNode) o).values);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for {@code DELETE} statements.
     */
    public static class DeleteNode extends AstNode {

        public final RelationNode relation;
        public final Predicate pred;

        public DeleteNode(RelationNode relation, Predicate pred) {
            this.relation = relation;
            this.pred = pred;
        }

        @Override
        public String toString() {
            return String.format("del(%s, %s)", relation, pred);
        }

        @Override
        public int hashCode() {
            return (7 * relation.hashCode()) ^ (13 * pred.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof DeleteNode) {
                return relation.equals(((DeleteNode) o).relation) && pred.equals(((DeleteNode) o).pred);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for {@code UPDATE} statements.
     */
    public static class UpdateNode extends AstNode {

        public final RelationNode relation;
        public final Predicate pred;
        public final String attr;
        public final String value;

        public UpdateNode(RelationNode relation, Predicate pred, String attr, String value) {
            this.relation = relation;
            this.pred = pred;
            this.attr = attr;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("upd(%s, %s, %s, %s)", relation, pred, attr, value);
        }

        @Override
        public int hashCode() {
            return (11 * relation.hashCode()) ^ (19 * pred.hashCode()) ^ attr.hashCode() ^ value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof UpdateNode) {
                return relation.equals(((UpdateNode) o).relation) && pred.equals(((UpdateNode) o).pred)
                        && attr.equals(((UpdateNode) o).attr) && value.equals(((UpdateNode) o).attr);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    /**
     * Abstract class for predicates.
     */
    public static abstract class Predicate extends AstNode {
    }

    /**
     * AST node for predicates with binary operators.
     */
    public static class LopPredNode extends Predicate {

        public final Operator op;
        public final String lhs;
        public final String rhs;

        public LopPredNode(String op, String lhs, String rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.op = BenchmarkAST.stringToOperator(op);
        }

        @Override
        public String toString() {
            return lhs + BenchmarkAST.operatorToString(op) + rhs;
        }

        @Override
        public int hashCode() {
            return (lhs.hashCode() ^ rhs.hashCode()) + BenchmarkAST.operatorToString(op).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof LopPredNode) {
                return lhs.equals(((LopPredNode) o).lhs) && rhs.equals(((LopPredNode) o).rhs)
                        && op == ((LopPredNode) o).op;
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for predicates with {@code IN}.
     */
    public static class InPredNode extends Predicate {

        public final String lhs;
        public final AstTerm rhsExpr;

        public InPredNode(String lhs, AstTerm rhsExpr) {
            this.lhs = lhs;
            this.rhsExpr = rhsExpr;
        }

        @Override
        public String toString() {
            return String.format("in(%s, %s)", lhs, rhsExpr);
        }

        @Override
        public int hashCode() {
            return lhs.hashCode() ^ (7 * rhsExpr.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof InPredNode) {
                return lhs.equals(((InPredNode) o).lhs) && rhsExpr.equals(((InPredNode) o).rhsExpr);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for predicates connected with {@code AND}.
     */
    public static class AndPredNode extends Predicate {

        public final Predicate lhs;
        public final Predicate rhs;

        public AndPredNode(Predicate lhs, Predicate rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public String toString() {
            return String.format("and(%s, %s)", lhs, rhs);
        }

        @Override
        public int hashCode() {
            return lhs.hashCode() & rhs.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof AndPredNode) {
                return lhs.equals(((AndPredNode) o).lhs) && rhs.equals(((AndPredNode) o).rhs);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for predicates connected with {@code OR}.
     */
    public static class OrPredNode extends Predicate {

        public final Predicate lhs;
        public final Predicate rhs;

        public OrPredNode(Predicate lhs, Predicate rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public String toString() {
            return String.format("or(%s, %s)", lhs, rhs);
        }

        @Override
        public int hashCode() {
            return lhs.hashCode() | rhs.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof OrPredNode) {
                return lhs.equals(((OrPredNode) o).lhs) && rhs.equals(((OrPredNode) o).rhs);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * AST node for predicates connected with {@code NOT}.
     */
    public static class NotPredNode extends Predicate {

        public final Predicate pred;

        public NotPredNode(Predicate pred) {
            this.pred = pred;
        }

        @Override
        public String toString() {
            return String.format("not(%s)", pred);
        }

        @Override
        public int hashCode() {
            return ~pred.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof NotPredNode) {
                return pred.equals(((NotPredNode) o).pred);
            }
            return false;
        }

        @Override
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * Data structure for programs.
     */
    public static class Program {

        public final Schema schema;
        public final List<Transaction> transactions;
        private Map<String, Transaction> lookup;

        public Program(Schema schema, List<Transaction> trans) {
            this.schema = schema;
            this.transactions = trans;
            lookup = buildLookupTable(trans);
        }

        public boolean containsTransaction(String name) {
            return lookup.containsKey(name);
        }

        public Transaction getTransactionByName(String name) {
            assert lookup.containsKey(name);
            return lookup.get(name);
        }

        @Override
        public String toString() {
            return schema + "\n" + transactions;
        }

        @Override
        public int hashCode() {
            return schema.hashCode() + transactions.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Program) {
                return schema.equals(((Program) o).schema) && transactions.equals(((Program) o).transactions);
            }
            return false;
        }

        /**
         * Accept a visitor and dispatch it to the method for the {@code Program} type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

        private Map<String, Transaction> buildLookupTable(List<Transaction> trans) {
            Map<String, Transaction> map = new HashMap<>();
            for (Transaction tran : trans) {
                map.put(tran.signature.name, tran);
            }
            return map;
        }

    }

    /**
     * Data structure for attribute (column) declarations.
     */
    public static class AttrDecl {

        public final String type;
        public final String name;
        private RelDecl relDecl;
        private boolean primaryKey;
        private boolean foreignKey;
        private AttrDecl referenceAttrDecl;

        public AttrDecl(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public RelDecl getRelDecl() {
            return relDecl;
        }

        public void setRelDecl(RelDecl relDecl) {
            this.relDecl = relDecl;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }

        public boolean isForeignKey() {
            return foreignKey;
        }

        public void setForeignKey(boolean foreignKey) {
            this.foreignKey = foreignKey;
        }

        public AttrDecl getReferenceAttrDecl() {
            return referenceAttrDecl;
        }

        public void setReferenceAttrDecl(AttrDecl referenceAttrDecl) {
            this.referenceAttrDecl = referenceAttrDecl;
        }

        @Override
        public String toString() {
            return String.format("%s %s", type, name);
        }

        @Override
        public int hashCode() {
            return type.hashCode() + name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof AttrDecl) {
                return type.equals(((AttrDecl) o).type) && name.equals(((AttrDecl) o).name)
                        && relDecl.equals(((AttrDecl) o).relDecl);
            }
            return false;
        }

        /**
         * Accept a visitor and dispatch it to the method for the {@code AttrDecl} type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * Data structure for relation (table) declarations.
     */
    public static class RelDecl {

        public final String name;
        public final List<AttrDecl> attrDecls;
        private Map<String, AttrDecl> lookup;

        public RelDecl(String name, List<AttrDecl> attrDecls) {
            this.name = name;
            this.attrDecls = attrDecls;
            lookup = buildLookupTable(attrDecls);
        }

        public boolean containsAttrName(String name) {
            return lookup.containsKey(name);
        }

        public AttrDecl getAttrDeclByName(String name) {
            assert lookup.containsKey(name) : "Unknown attribute: " + name;
            return lookup.get(name);
        }

        public int getAttrNum() {
            return attrDecls.size();
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", name, attrDecls.stream()
                    .map(AttrDecl::toString)
                    .collect(Collectors.joining(", ")));
        }

        @Override
        public int hashCode() {
            return name.hashCode() + attrDecls.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof RelDecl) {
                return name.equals(((RelDecl) o).name) && attrDecls.equals(((RelDecl) o).attrDecls);
            }
            return false;
        }

        private Map<String, AttrDecl> buildLookupTable(List<AttrDecl> attrDecls) {
            Map<String, AttrDecl> map = new HashMap<>();
            for (AttrDecl attrDecl : attrDecls) {
                map.put(attrDecl.name, attrDecl);
            }
            return map;
        }

        /**
         * Accept a visitor and dispatch it to the method for the {@code RelDecl} type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * Data structure for database schemas.
     */
    public static class Schema {

        public final List<RelDecl> relDecls;
        private Map<String, RelDecl> lookup;

        public Schema(List<RelDecl> relDecls) {
            this.relDecls = relDecls;
            lookup = buildLookupTable(relDecls);
        }

        public boolean containsRelName(String name) {
            return lookup.containsKey(name);
        }

        public RelDecl getRelDeclByName(String name) {
            assert lookup.containsKey(name) : "Unknown relation: " + name;
            return lookup.get(name);
        }

        @Override
        public String toString() {
            return relDecls.toString();
        }

        @Override
        public int hashCode() {
            return relDecls.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Schema) {
                return relDecls.equals(((Schema) o).relDecls);
            }
            return false;
        }

        private Map<String, RelDecl> buildLookupTable(List<RelDecl> relDecls) {
            Map<String, RelDecl> map = new HashMap<>();
            for (RelDecl relDecl : relDecls) {
                map.put(relDecl.name, relDecl);
            }
            return map;
        }

        /**
         * Accept a visitor and dispatch it to the method for the {@code Schema} type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * Data structure for transaction signatures.
     */
    public static class Signature {

        public final String returnType;
        public final String name;
        public final List<String> argumentTypes;
        public final List<String> arguments;

        public Signature(String returnType, String name, List<String> argumentTypes, List<String> arguments) {
            this.returnType = returnType;
            this.name = name;
            this.argumentTypes = argumentTypes;
            this.arguments = arguments;
        }

        public int getArgumentNum() {
            return arguments.size();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(returnType).append(" ");
            builder.append(name).append("(");
            for (int i = 0; i < arguments.size(); ++i) {
                builder.append(argumentTypes.get(i)).append(" ").append(arguments.get(i)).append(", ");
            }
            if (arguments.size() > 0)
                builder.delete(builder.length() - 2, builder.length());
            builder.append(")");
            return builder.toString();
        }

        @Override
        public int hashCode() {
            return (returnType.hashCode() ^ name.hashCode()) + (argumentTypes.hashCode() ^ arguments.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Signature) {
                Signature obj = (Signature) o;
                return returnType.equals(obj.returnType) && name.equals(obj.name)
                        && argumentTypes.equals(obj.argumentTypes) && arguments.equals(obj.arguments);
            }
            return false;
        }

        /**
         * Accept a visitor and dispatch it to the method for the {@code Signature}
         * type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Transaction {

        public final Signature signature;
        public final List<AstNode> statements;

        public Transaction(Signature signature, List<AstNode> statements) {
            this.signature = signature;
            this.statements = statements;
        }

        @Override
        public String toString() {
            return "\n" + signature + "\n" + statements;
        }

        @Override
        public int hashCode() {
            return signature.hashCode() + statements.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Transaction) {
                return signature.equals(((Transaction) o).signature) && statements.equals(((Transaction) o).statements);
            }
            return false;
        }

        /**
         * Accept a visitor and dispatch it to the method for the {@code Transaction}
         * type.
         *
         * @param<T> return type
         *
         * @param visitor the visitor
         * @return what the visitor returns
         */
        public <T> T accept(IASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    /**
     * Binary logical operators used in the predicate.
     */
    public enum Operator {
        EQ, NE, LT, LE, GT, GE
    };

    public static Operator stringToOperator(String op) {
        switch (op) {
        case "=":
            return Operator.EQ;
        case "!=":
            return Operator.NE;
        case "<":
            return Operator.LT;
        case "<=":
            return Operator.LE;
        case ">":
            return Operator.GT;
        case ">=":
            return Operator.GE;
        default:
            throw new RuntimeException("Unknown operator string");
        }
    }

    public static String operatorToString(Operator op) {
        switch (op) {
        case EQ:
            return "=";
        case NE:
            return "!=";
        case LT:
            return "<";
        case LE:
            return "<=";
        case GT:
            return ">";
        case GE:
            return ">=";
        default:
            throw new RuntimeException("Unknown operator");
        }
    }

}
