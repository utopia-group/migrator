package migrator.sketch;

import migrator.rewrite.sql.SqlProgram;

/**
 * Interface for sketch solvers.
 */
public interface ISketchSolver {

    /**
     * Instantiate holes in the sketch such that the result is equivalent to the
     * source program.
     *
     * @param sketch     the sketch to complete
     * @param sourceProg source program
     * @return target program or {@code null} for failure
     */
    public SqlProgram completeSketch(Sketch sketch, SqlProgram sourceProg);

}
