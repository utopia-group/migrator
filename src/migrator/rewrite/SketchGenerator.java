package migrator.rewrite;

import java.util.ArrayList;
import java.util.List;

import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.QueryList;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;
import migrator.sketch.Sketch;

/**
 * Program sketch generator that generates sketches over the target schema.
 */
public final class SketchGenerator {

    /**
     * Generate the sketch for a program.
     *
     * @param srcSchema        source schema
     * @param tgtSchema        target schema
     * @param srcProg          source program
     * @param valueCorr        value correspondence between source and target
     * @param joinCorrSupplier join correspondence supplier
     * @return the generated sketch over target schema
     */
    public static Sketch generateSketch(
            SchemaDef srcSchema,
            SchemaDef tgtSchema,
            SqlProgram srcProg,
            ValueCorrespondence valueCorr,
            IJoinCorrSupplier joinCorrSupplier) {
        List<MethodImplementation> sketchMethods = new ArrayList<>();
        {
            for (MethodImplementation method : srcProg.methods) {
                QueryList srcQuery = new QueryList(method.queries);
                QueryList tgtQuery = generate(srcSchema, tgtSchema, srcQuery, valueCorr, joinCorrSupplier);
                sketchMethods.add(new MethodImplementation(method.signature, tgtQuery.queries));
            }
        }
        Sketch sketch = new Sketch(new SqlProgram(sketchMethods));
        return sketch;
    }

    /**
     * Generate the sketch for a list of queries.
     *
     * @param srcSchema        source schema
     * @param tgtSchema        target schema
     * @param srcQueryList     a list of queries in the source schema
     * @param valueCorr        value correspondence between source and target
     * @param joinCorrSupplier join correspondence supplier
     * @return a list of sketch statements
     */
    public static QueryList generate(
            SchemaDef srcSchema,
            SchemaDef tgtSchema,
            QueryList srcQueryList,
            ValueCorrespondence valueCorr,
            IJoinCorrSupplier joinCorrSupplier) {
        QueryList mergedQueryList = mergeAdjacentInsertions(srcQueryList, srcSchema);
        QueryList sketch = genSketch(srcSchema, tgtSchema, mergedQueryList, valueCorr, joinCorrSupplier);
        return sketch;
    }

    private static QueryList mergeAdjacentInsertions(QueryList queryList, SchemaDef schema) {
        InsertionMergeVisitor visitor = new InsertionMergeVisitor(schema);
        for (IQuery query : queryList.queries) {
            query.accept(visitor);
        }
        return visitor.getQueryList();
    }

    private static QueryList genSketch(
            SchemaDef srcSchema,
            SchemaDef tgtSchema,
            QueryList srcQueryList,
            ValueCorrespondence valueCorr,
            IJoinCorrSupplier joinCorrSupplier) {
        List<IQuery> sketches = new ArrayList<>();
        SketchGenVisitor visitor = new SketchGenVisitor(srcSchema, tgtSchema, valueCorr, joinCorrSupplier);
        for (IQuery query : srcQueryList.queries) {
            sketches.add(query.accept(visitor));
        }
        return new QueryList(sketches);
    }
}
