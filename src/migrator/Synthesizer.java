package migrator;

import java.util.logging.Level;
import java.util.logging.Logger;

import migrator.corr.ISimilarityStrategy;
import migrator.corr.IValueCorrSupplier;
import migrator.corr.MixedColumnRefSimilarity;
import migrator.corr.OptimValueCorrSupplier;
import migrator.corr.ValueCorrespondence;
import migrator.rewrite.IJoinCorrSupplier;
import migrator.rewrite.JoinCorrSupplier;
import migrator.rewrite.ResolveColumnVisitor;
import migrator.rewrite.RewriteException;
import migrator.rewrite.SketchGenerator;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;
import migrator.sketch.ISketchSolver;
import migrator.sketch.Sketch;
import migrator.sketch.SketchSolver;

/**
 * Synthesizer for database programs.
 */
public final class Synthesizer {

    private static final Logger LOGGER = LoggerWrapper.getInstance();

    /**
     * Top-level procedure to synthesize equivalent database programs in the
     * presence of schema change.
     *
     * @param sourceProg   source program
     * @param sourceSchema database schema for the source program
     * @param targetSchema database schema for the target program
     * @return an equivalent target program or {@code null} for failure
     */
    public static SqlProgram synthesize(
            SqlProgram sourceProg,
            SchemaDef sourceSchema,
            SchemaDef targetSchema) {
        // first resolve the source program
        resolveSqlProgram(sourceProg, sourceSchema);
        // then start to synthesize
        ISimilarityStrategy similarity = new MixedColumnRefSimilarity();
        IValueCorrSupplier supplier = new OptimValueCorrSupplier(sourceSchema, targetSchema, sourceProg, similarity);
        int iter = 0;
        while (supplier.hasNext()) {
            ValueCorrespondence valueCorr = supplier.getNext();
            LOGGER.info("=== Value Correspondence: " + (++iter));
            LOGGER.fine(valueCorr.toString());
            try {
                SqlProgram targetProg = synthesize(sourceProg, sourceSchema, targetSchema, valueCorr);
                if (targetProg != null) {
                    return targetProg;
                }
            } catch (RewriteException e) {
                // count the rewrite exception as one iteration
                LOGGER.info("--- Iteration: 0");
                LOGGER.log(Level.FINE, "Rewrite Exception", e);
            }
        }
        return null;
    }

    /**
     * Synthesize equivalent database programs in the presence of schema change
     * given a value correspondence.
     * <p>
     * The source program needs to be resolved before calling this method.
     *
     * @param sourceProg   source program
     * @param sourceSchema database schema for the source program
     * @param targetSchema database schema for the target program
     * @param valueCorr    value correspondence
     * @return an equivalent target program or {@code null} for failure
     */
    public static SqlProgram synthesize(
            SqlProgram sourceProg,
            SchemaDef sourceSchema,
            SchemaDef targetSchema,
            ValueCorrespondence valueCorr) {
        IJoinCorrSupplier supplier = new JoinCorrSupplier(sourceSchema, targetSchema);
        return synthesize(sourceProg, sourceSchema, targetSchema, valueCorr, supplier);
    }

    /**
     * Synthesize equivalent database programs in the presence of schema change
     * given a value correspondence and join correspondence supplier.
     * <p>
     * The source program needs to be resolved before calling this method.
     *
     * @param sourceProg   source program
     * @param sourceSchema database schema for the source program
     * @param targetSchema database schema for the target program
     * @param valueCorr    value correspondence
     * @param supplier     join correspondence supplier
     * @return an equivalent target program or {@code null} for failure
     */
    public static SqlProgram synthesize(
            SqlProgram sourceProg,
            SchemaDef sourceSchema,
            SchemaDef targetSchema,
            ValueCorrespondence valueCorr,
            IJoinCorrSupplier supplier) {
        Sketch sketch = SketchGenerator.generateSketch(sourceSchema, targetSchema, sourceProg, valueCorr, supplier);
        ISketchSolver solver = new SketchSolver(sourceSchema, targetSchema);
        return solver.completeSketch(sketch, sourceProg);
    }

    /**
     * Resolve the column references in the program.
     * <p>
     * This method modifies the provided program directly.
     *
     * @param program the program to be resolved
     * @param schema  the schema underlying the program
     */
    public static void resolveSqlProgram(SqlProgram program, SchemaDef schema) {
        ResolveColumnVisitor visitor = new ResolveColumnVisitor(schema);
        for (MethodImplementation method : program.methods) {
            for (IQuery query : method.queries) {
                query.accept(visitor);
            }
        }
    }
}
