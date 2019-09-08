package migrator.sketch;

import java.util.ArrayList;
import java.util.List;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.SchemaDef;

/**
 * A naive sketch solver that symbolically enumerates the model.
 *
 * @author rushi on 9/6/18
 */
public class NaiveSketchSolver extends SketchSolver {

    public NaiveSketchSolver(SchemaDef sourceSchema, SchemaDef targetSchema) {
        super(sourceSchema, targetSchema);
    }

    /**
     * Block the complete assignment.
     *
     * @param model current assignment for holes
     * @param seq   the invocation sequence
     * @return the constraint that blocks the complete assignment
     */
    @Override
    public SketchConstraint block(SketchModel model, InvocationSequence seq) {
        SketchConstraint result = new SketchConstraint(new ArrayList<>(), new ArrayList<>());
        List<SketchVariable> pos = new ArrayList<>(); // stays empty
        List<SketchVariable> negs = new ArrayList<>(); // will fill with everything that was positive before

        for (ChooseQuery chooseQuery : model.joinAssignment.keySet()) {
            List<IQuery> iQueries = model.joinAssignment.get(chooseQuery);
            SketchVariable sketchVariable = factory.getSketchVariable(chooseQuery, iQueries);
            negs.add(sketchVariable);
        }
        for (CompoundDeleteQuery compoundDeleteQuery : model.deletionAssignment.keySet()) {
            List<String> values = model.deletionAssignment.get(compoundDeleteQuery);
            SketchVariable sketchVariable = factory.getSketchVariable(compoundDeleteQuery, values);
            negs.add(sketchVariable);
        }
        for (ColumnHole columnHole : model.columnAssignment.keySet()) {
            ColumnRef columnRef = model.columnAssignment.get(columnHole);
            SketchVariable sketchVariable = factory.getSketchVariable(columnHole, columnRef);
            negs.add(sketchVariable);
        }
        result.clauses.add(new SATSolver.Clause<SketchVariable>(pos, negs));
        return result;
    }
}
