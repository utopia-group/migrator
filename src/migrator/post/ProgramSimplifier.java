package migrator.post;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import migrator.rewrite.RewriteUtil;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.IPredicate;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.ast.UpdateQuery;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;

/**
 * A SQL program simplifier.
 */
public final class ProgramSimplifier {

    /**
     * Simplify the given SQL program.
     * (1) remove redundant delete statements
     * (2) remove redundant simple update statements
     * (3) convert compound deletes to simple deletes if possible
     * (4) convert compound updates to simple updates if possible
     *
     * @param program the given SQL program
     * @param schema  the database schema
     * @return simplified SQL program
     */
    public static SqlProgram simplify(SqlProgram program, SchemaDef schema) {
        program = removeRedundantDeletesAndUpdates(program, schema);
        ProgramSimplifyVisitor visitor = new ProgramSimplifyVisitor(schema);
        List<MethodImplementation> simplified = new ArrayList<>();
        for (MethodImplementation method : program.methods) {
            List<IQuery> queries = method.queries.stream()
                    .map(query -> query.accept(visitor))
                    .collect(Collectors.toList());
            simplified.add(new MethodImplementation(method.signature, queries));
        }
        SqlProgram simplifiedProg = new SqlProgram(simplified);
        // remove redundant deletes and updates again after simplification
        simplifiedProg = removeRedundantDeletesAndUpdates(simplifiedProg, schema);
        return simplifiedProg;
    }

    private static SqlProgram removeRedundantDeletesAndUpdates(SqlProgram program, SchemaDef schema) {
        List<MethodImplementation> simplified = new ArrayList<>();
        for (MethodImplementation method : program.methods) {
            if (method.queries.size() > 1) {
                List<IQuery> simplifiedQueries = new ArrayList<>();
                simplifiedQueries.add(method.queries.get(0));
                for (int i = 1; i < method.queries.size(); ++i) {
                    IQuery q1 = method.queries.get(i - 1);
                    IQuery q2 = method.queries.get(i);
                    if (!sameCompoundDelete(q1, q2)
                            && !dualCompoundDelete(q1, q2, schema)
                            && !sameSimpleDelete(q1, q2)
                            && !sameSimpleUpdate(q1, q2)) {
                        simplifiedQueries.add(q2);
                    }
                }
                simplified.add(new MethodImplementation(method.signature, simplifiedQueries));
            } else {
                simplified.add(method);
            }
        }
        return new SqlProgram(simplified);
    }

    static boolean sameCompoundDelete(IQuery query1, IQuery query2) {
        if (!(query1 instanceof CompoundDeleteQuery && query2 instanceof CompoundDeleteQuery)) {
            return false;
        }
        CompoundDeleteQuery del1 = (CompoundDeleteQuery) query1;
        CompoundDeleteQuery del2 = (CompoundDeleteQuery) query2;
        return del1.table.equals(del2.table)
                && del1.joins.equals(del2.joins)
                && del1.deletedTables.equals(del2.deletedTables)
                && sameOpPred(del1.predicate, del2.predicate); // only handle {@code OpPred}
    }

    static boolean dualCompoundDelete(IQuery query1, IQuery query2, SchemaDef schema) {
        if (!(query1 instanceof CompoundDeleteQuery && query2 instanceof CompoundDeleteQuery)) {
            return false;
        }
        CompoundDeleteQuery del1 = (CompoundDeleteQuery) query1;
        CompoundDeleteQuery del2 = (CompoundDeleteQuery) query2;
        Set<TableRef> tables1 = getTables(del1.table, del1.joins);
        Set<TableRef> tables2 = getTables(del2.table, del2.joins);
        return tables1.equals(tables2)
                && dualOpPred(del1.predicate, del2.predicate, schema);
    }

    static boolean sameSimpleDelete(IQuery query1, IQuery query2) {
        if (!(query1 instanceof DeleteQuery && query2 instanceof DeleteQuery)) {
            return false;
        }
        DeleteQuery del1 = (DeleteQuery) query1;
        DeleteQuery del2 = (DeleteQuery) query2;
        return del1.table.equals(del2.table)
                && sameOpPred(del1.predicate, del2.predicate);
    }

    static boolean sameSimpleUpdate(IQuery query1, IQuery query2) {
        if (!(query1 instanceof UpdateQuery && query2 instanceof UpdateQuery)) {
            return false;
        }
        UpdateQuery upd1 = (UpdateQuery) query1;
        UpdateQuery upd2 = (UpdateQuery) query2;
        return upd1.table.equals(upd2.table)
                && samePairList(upd1.pairs, upd2.pairs)
                && sameOpPred(upd1.predicate, upd2.predicate);
    }

    private static boolean samePairList(List<ColumnValuePair> pairs1, List<ColumnValuePair> pairs2) {
        if (pairs1.size() != pairs2.size()) {
            return false;
        }
        for (int i = 0; i < pairs1.size(); ++i) {
            ColumnValuePair pair1 = pairs1.get(i);
            ColumnValuePair pair2 = pairs2.get(i);
            if (!pair1.column.equals(pair2.column) || !pair1.value.equals(pair2.value)) {
                return false;
            }
        }
        return true;
    }

    private static boolean sameOpPred(IPredicate pred1, IPredicate pred2) {
        if (!(pred1 instanceof OpPred && pred2 instanceof OpPred)) {
            return false;
        }
        OpPred opPred1 = (OpPred) pred1;
        OpPred opPred2 = (OpPred) pred2;
        return opPred1.lhs.equals(opPred2.lhs)
                && opPred1.rhs.equals(opPred2.rhs)
                && opPred1.op.equals(opPred2.op);
    }

    private static boolean dualOpPred(IPredicate pred1, IPredicate pred2, SchemaDef schema) {
        if (!(pred1 instanceof OpPred && pred2 instanceof OpPred)) {
            return false;
        }
        OpPred opPred1 = (OpPred) pred1;
        OpPred opPred2 = (OpPred) pred2;
        ColumnRef predColumn1 = PostprocessUtil.getOpPredLhsColumn(pred1);
        ColumnRef predColumn2 = PostprocessUtil.getOpPredLhsColumn(pred2);
        return isFKRK(predColumn1, predColumn2, schema)
                && opPred1.rhs.equals(opPred2.rhs)
                && opPred1.op.equals(opPred2.op);
    }

    private static boolean isFKRK(ColumnRef column1, ColumnRef column2, SchemaDef schema) {
        if (RewriteUtil.isForeignKey(column1, schema)) {
            ColumnRef rk1 = RewriteUtil.getReferenceKey(column1, schema);
            if (rk1.equals(column2)) {
                return true;
            }
        }
        if (RewriteUtil.isForeignKey(column2, schema)) {
            ColumnRef rk2 = RewriteUtil.getReferenceKey(column2, schema);
            if (rk2.equals(column1)) {
                return true;
            }
        }
        return false;
    }

    private static Set<TableRef> getTables(TableRef table, List<Join> joins) {
        Set<TableRef> ret = new HashSet<>();
        ret.add(table);
        for (Join join : joins) {
            ret.add(join.getDest());
        }
        return ret;
    }

}
