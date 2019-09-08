package migrator.bench.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import migrator.bench.BenchmarkAST.AndPredNode;
import migrator.bench.BenchmarkAST.AstNode;
import migrator.bench.BenchmarkAST.AstTerm;
import migrator.bench.BenchmarkAST.AttrDecl;
import migrator.bench.BenchmarkAST.AttrListNode;
import migrator.bench.BenchmarkAST.DeleteNode;
import migrator.bench.BenchmarkAST.EquiJoinNode;
import migrator.bench.BenchmarkAST.InPredNode;
import migrator.bench.BenchmarkAST.InsertNode;
import migrator.bench.BenchmarkAST.LopPredNode;
import migrator.bench.BenchmarkAST.NotPredNode;
import migrator.bench.BenchmarkAST.OrPredNode;
import migrator.bench.BenchmarkAST.Predicate;
import migrator.bench.BenchmarkAST.ProjectNode;
import migrator.bench.BenchmarkAST.RelDecl;
import migrator.bench.BenchmarkAST.RelationNode;
import migrator.bench.BenchmarkAST.Schema;
import migrator.bench.BenchmarkAST.SelectNode;
import migrator.bench.BenchmarkAST.TupleNode;
import migrator.bench.BenchmarkAST.UpdateNode;
import migrator.bench.parser.AntlrDslParser.AndPredContext;
import migrator.bench.parser.AntlrDslParser.AttrListContext;
import migrator.bench.parser.AntlrDslParser.DelStmtContext;
import migrator.bench.parser.AntlrDslParser.InPredContext;
import migrator.bench.parser.AntlrDslParser.InsStmtContext;
import migrator.bench.parser.AntlrDslParser.JoinExprContext;
import migrator.bench.parser.AntlrDslParser.LopPredContext;
import migrator.bench.parser.AntlrDslParser.NotPredContext;
import migrator.bench.parser.AntlrDslParser.OrPredContext;
import migrator.bench.parser.AntlrDslParser.ProjExprContext;
import migrator.bench.parser.AntlrDslParser.SelExprContext;
import migrator.bench.parser.AntlrDslParser.StmtRootContext;
import migrator.bench.parser.AntlrDslParser.ToDelStmtContext;
import migrator.bench.parser.AntlrDslParser.ToIdContext;
import migrator.bench.parser.AntlrDslParser.ToInsStmtContext;
import migrator.bench.parser.AntlrDslParser.ToQueryExprContext;
import migrator.bench.parser.AntlrDslParser.ToUpdStmtContext;
import migrator.bench.parser.AntlrDslParser.TupleContext;
import migrator.bench.parser.AntlrDslParser.UpdStmtContext;

/**
 * Visitor for generating AST using Antlr.
 */
public class AntlrAstGenVisitor extends AbstractParseTreeVisitor<AstNode> implements AntlrDslVisitor<AstNode> {

    // pass in schema to put RelDecl on RelationNode and to determine
    // primary and foreign keys for join
    private Schema schema;

    public AntlrAstGenVisitor(Schema schema) {
        this.schema = schema;
    }

    @Override
    public AstNode visitStmtRoot(StmtRootContext ctx) {
        return visit(ctx.stmt());
    }

    @Override
    public AstNode visitToQueryExpr(ToQueryExprContext ctx) {
        return visit(ctx.queryExpr());
    }

    @Override
    public AstNode visitToInsStmt(ToInsStmtContext ctx) {
        return visit(ctx.insStmt());
    }

    @Override
    public AstNode visitToDelStmt(ToDelStmtContext ctx) {
        return visit(ctx.delStmt());
    }

    @Override
    public AstNode visitToUpdStmt(ToUpdStmtContext ctx) {
        return visit(ctx.updStmt());
    }

    @Override
    public EquiJoinNode visitJoinExpr(JoinExprContext ctx) {
        AstNode lhs = visit(ctx.queryExpr(0));
        assert lhs instanceof AstTerm;
        AstTerm lhsTerm = (AstTerm) lhs;
        AstNode rhs = visit(ctx.queryExpr(1));
        assert rhs instanceof AstTerm;
        AstTerm rhsTerm = (AstTerm) rhs;

        // collect all tables in left and right hand sides
        Set<RelDecl> lhsRels = new HashSet<>();
        findAllRelationsInTerm(lhsTerm).forEach((rel) -> lhsRels.add(rel.relDecl));
        Set<RelDecl> rhsRels = new HashSet<>();
        findAllRelationsInTerm(rhsTerm).forEach((rel) -> rhsRels.add(rel.relDecl));
        // find left and right attributes for equi-joins
        for (RelDecl rel : lhsRels) {
            for (AttrDecl attr : rel.attrDecls) {
                if (attr.isForeignKey()) {
                    AttrDecl refAttr = attr.getReferenceAttrDecl();
                    if (rhsRels.contains(refAttr.getRelDecl())) {
                        String leftAttr = attr.name;
                        String rightAttr = refAttr.name;
                        RelDecl leftRelDecl = attr.getRelDecl();
                        RelDecl rightRelDecl = refAttr.getRelDecl();
                        return new EquiJoinNode(lhsTerm, rhsTerm, leftAttr, rightAttr, leftRelDecl, rightRelDecl);
                    }
                }
            }
        }
        for (RelDecl rel : rhsRels) {
            for (AttrDecl attr : rel.attrDecls) {
                if (attr.isForeignKey()) {
                    AttrDecl refAttr = attr.getReferenceAttrDecl();
                    if (lhsRels.contains(refAttr.getRelDecl())) {
                        String leftAttr = refAttr.name;
                        String rightAttr = attr.name;
                        RelDecl leftRelDecl = refAttr.getRelDecl();
                        RelDecl rightRelDecl = attr.getRelDecl();
                        return new EquiJoinNode(lhsTerm, rhsTerm, leftAttr, rightAttr, leftRelDecl, rightRelDecl);
                    }
                }
            }
        }
        throw new RuntimeException("Cannot find attributes for equi-join: " + ctx.getText());
    }

    @Override
    public SelectNode visitSelExpr(SelExprContext ctx) {
        AstNode pred = visit(ctx.pred());
        assert pred instanceof Predicate;
        AstNode term = visit(ctx.queryExpr());
        assert term instanceof AstTerm;
        return new SelectNode((Predicate) pred, (AstTerm) term);
    }

    @Override
    public ProjectNode visitProjExpr(ProjExprContext ctx) {
        AstNode attrList = visit(ctx.attrList());
        assert attrList instanceof AttrListNode;
        AstNode term = visit(ctx.queryExpr());
        assert term instanceof AstTerm;
        return new ProjectNode((AttrListNode) attrList, (AstTerm) term);
    }

    @Override
    public RelationNode visitToId(ToIdContext ctx) {
        return mkRelationNode(ctx.ID().getText());
    }

    @Override
    public InsertNode visitInsStmt(InsStmtContext ctx) {
        RelationNode relation = mkRelationNode(ctx.ID().getText());
        AstNode tuple = visit(ctx.tuple());
        assert tuple instanceof TupleNode;
        return new InsertNode(relation, (TupleNode) tuple);
    }

    @Override
    public DeleteNode visitDelStmt(DelStmtContext ctx) {
        RelationNode relation = mkRelationNode(ctx.ID().getText());
        AstNode pred = visit(ctx.pred());
        assert pred instanceof Predicate;
        return new DeleteNode(relation, (Predicate) pred);
    }

    @Override
    public UpdateNode visitUpdStmt(UpdStmtContext ctx) {
        RelationNode relation = mkRelationNode(ctx.ID(0).getText());
        AstNode pred = visit(ctx.pred());
        assert pred instanceof Predicate;
        String attr = ctx.ID(1).getText();
        String value = ctx.ID(2).getText();
        return new UpdateNode(relation, (Predicate) pred, attr, value);
    }

    @Override
    public AttrListNode visitAttrList(AttrListContext ctx) {
        List<String> attrList = new ArrayList<>();
        for (TerminalNode node : ctx.ID()) {
            attrList.add(node.getText());
        }
        return new AttrListNode(attrList);
    }

    @Override
    public TupleNode visitTuple(TupleContext ctx) {
        List<String> values = new ArrayList<>();
        for (TerminalNode node : ctx.ID()) {
            values.add(node.getText());
        }
        return new TupleNode(values);
    }

    @Override
    public NotPredNode visitNotPred(NotPredContext ctx) {
        AstNode pred = visit(ctx.pred());
        assert pred instanceof Predicate;
        return new NotPredNode((Predicate) pred);
    }

    @Override
    public AndPredNode visitAndPred(AndPredContext ctx) {
        AstNode lhs = visit(ctx.pred(0));
        assert lhs instanceof Predicate;
        AstNode rhs = visit(ctx.pred(1));
        assert rhs instanceof Predicate;
        return new AndPredNode((Predicate) lhs, (Predicate) rhs);
    }

    @Override
    public OrPredNode visitOrPred(OrPredContext ctx) {
        AstNode lhs = visit(ctx.pred(0));
        assert lhs instanceof Predicate;
        AstNode rhs = visit(ctx.pred(1));
        assert rhs instanceof Predicate;
        return new OrPredNode((Predicate) lhs, (Predicate) rhs);
    }

    @Override
    public InPredNode visitInPred(InPredContext ctx) {
        AstNode term = visit(ctx.queryExpr());
        assert term instanceof AstTerm;
        return new InPredNode(ctx.ID().getText(), (AstTerm) term);
    }

    @Override
    public LopPredNode visitLopPred(LopPredContext ctx) {
        return new LopPredNode(ctx.LOP().getText(), ctx.ID(0).getText(), ctx.ID(1).getText());
    }

    private RelationNode mkRelationNode(String name) {
        RelDecl rel = schema.getRelDeclByName(name);
        return new RelationNode(name, rel);
    }

    private Set<RelationNode> findAllRelationsInTerm(AstTerm term) {
        // TODO what if projection changes the available
        // attributes for a term in general
        Set<RelationNode> ret = new HashSet<>();
        if (term instanceof RelationNode) {
            ret.add((RelationNode) term);
        } else if (term instanceof ProjectNode) {
            ret.addAll(findAllRelationsInTerm(((ProjectNode) term).relation));
        } else if (term instanceof SelectNode) {
            ret.addAll(findAllRelationsInTerm(((SelectNode) term).relation));
        } else if (term instanceof EquiJoinNode) {
            ret.addAll(findAllRelationsInTerm(((EquiJoinNode) term).lhs));
            ret.addAll(findAllRelationsInTerm(((EquiJoinNode) term).rhs));
        } else {
            throw new RuntimeException("Unknown subtype of AstTerm");
        }
        return ret;
    }

}
