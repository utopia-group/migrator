package migrator.sketch;

import java.util.ArrayList;
import java.util.List;

import migrator.sketch.SATSolver.Clause;
import migrator.sketch.SATSolver.ExactOneClause;
import migrator.sketch.SketchSolver.SketchVariable;

/**
 * Constraint over sketches.
 */
public final class SketchConstraint {
    /**
     * A list of clauses that are disjunction of positive and negative boolean
     * variables.
     */
    public final List<Clause<SketchVariable>> clauses;
    /**
     * A list of clauses where each of them only contains one variable that
     * evaluates to {@code true}.
     */
    public final List<ExactOneClause<SketchVariable>> exactOneClauses;

    public SketchConstraint(List<Clause<SketchVariable>> clauses,
            List<ExactOneClause<SketchVariable>> exactOneClauses) {
        this.clauses = (clauses == null) ? new ArrayList<>() : clauses;
        this.exactOneClauses = (exactOneClauses == null) ? new ArrayList<>() : exactOneClauses;
    }

    /**
     * Merge another sketch constraint into this one by appending the clauses.
     *
     * @param constraint the sketch constraint to merge
     */
    public void mergeConstraint(SketchConstraint constraint) {
        this.clauses.addAll(constraint.clauses);
        this.exactOneClauses.addAll(constraint.exactOneClauses);
    }

    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        builder.append("Clauses:").append(newLine);
        for (Clause<SketchVariable> clause : clauses) {
            builder.append(clause).append(newLine);
        }
        builder.append("ExactOneClauses:").append(newLine);
        for (ExactOneClause<SketchVariable> clause : exactOneClauses) {
            builder.append(clause).append(newLine);
        }
        return builder.toString();
    }
}
