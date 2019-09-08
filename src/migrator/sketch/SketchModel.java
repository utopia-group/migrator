package migrator.sketch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.IQuery;
import migrator.sketch.SketchSolver.HoleKind;
import migrator.sketch.SketchSolver.SketchVariable;

/**
 * Hole assignments for a sketch.
 */
public final class SketchModel {
    /**
     * All sketch variables to evaluate to {@code true}
     */
    public final List<SketchVariable> posVars;
    /**
     * join chain hole -> instantiation
     */
    public final Map<ChooseQuery, List<IQuery>> joinAssignment;
    /**
     * table list hole in compound deletion -> instantiation
     */
    public final Map<CompoundDeleteQuery, List<String>> deletionAssignment;
    /**
     * column hole -> instantiation
     */
    public final Map<ColumnHole, ColumnRef> columnAssignment;

    public SketchModel(List<SketchVariable> posVars) {
        this.posVars = posVars;
        joinAssignment = new HashMap<>();
        deletionAssignment = new HashMap<>();
        columnAssignment = new HashMap<>();
        // populate the maps for join/deletion/column assignments
        for (SketchVariable posVar : posVars) {
            if (posVar.holeKind == HoleKind.JOINCHAIN) {
                ChooseQuery hole = (ChooseQuery) posVar.hole;
                @SuppressWarnings("unchecked")
                List<IQuery> value = (List<IQuery>) posVar.candidate;
                assert !joinAssignment.containsKey(hole);
                joinAssignment.put(hole, value);
            } else if (posVar.holeKind == HoleKind.TABLELIST) {
                CompoundDeleteQuery hole = (CompoundDeleteQuery) posVar.hole;
                @SuppressWarnings("unchecked")
                List<String> value = (List<String>) posVar.candidate;
                assert !deletionAssignment.containsKey(hole);
                deletionAssignment.put(hole, value);
            } else if (posVar.holeKind == HoleKind.COLUMN) {
                ColumnHole hole = (ColumnHole) posVar.hole;
                ColumnRef value = (ColumnRef) posVar.candidate;
                assert !columnAssignment.containsKey(hole);
                columnAssignment.put(hole, value);
            } else {
                throw new RuntimeException("Unknown hole kind");
            }
        }
    }

    /**
     * Get the assignment given a join chain hole.
     *
     * @param query the join chain hole
     * @return list of queries as the assignment
     */
    public List<IQuery> getViewAssignment(ChooseQuery query) {
        assert joinAssignment.containsKey(query);
        return joinAssignment.get(query);
    }

    /**
     * Get the assignment given a deletion hole.
     *
     * @param query the deletion hole
     * @return list of table names as the assignment
     */
    public List<String> getDeletionAssignment(CompoundDeleteQuery query) {
        assert deletionAssignment.containsKey(query);
        return deletionAssignment.get(query);
    }

    /**
     * Get the assignment given a column hole.
     *
     * @param hole the column hole
     * @return column reference as the assignment
     */
    public ColumnRef getColumnAssignment(ColumnHole hole) {
        assert columnAssignment.containsKey(hole);
        return columnAssignment.get(hole);
    }
}
