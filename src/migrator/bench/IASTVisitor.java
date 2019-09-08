package migrator.bench;

import migrator.bench.BenchmarkAST.AndPredNode;
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
 * Visitor inference for benchmark AST.
 */
public interface IASTVisitor<T> {

    public T visit(Program program);

    public T visit(Schema schema);

    public T visit(RelDecl relDecl);

    public T visit(AttrDecl attrDecl);

    public T visit(Transaction tran);

    public T visit(Signature sig);

    public T visit(InsertNode ins);

    public T visit(DeleteNode del);

    public T visit(UpdateNode upd);

    public T visit(TupleNode tuple);

    public T visit(AttrListNode attrList);

    public T visit(RelationNode rel);

    public T visit(ProjectNode proj);

    public T visit(SelectNode sel);

    public T visit(EquiJoinNode join);

    public T visit(LopPredNode lop);

    public T visit(InPredNode in);

    public T visit(AndPredNode and);

    public T visit(OrPredNode or);

    public T visit(NotPredNode not);

}
