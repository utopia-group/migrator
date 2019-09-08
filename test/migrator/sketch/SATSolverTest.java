package migrator.sketch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sat4j.specs.ContradictionException;

import migrator.sketch.SATSolver.Clause;
import migrator.sketch.SATSolver.ExactOneClause;
import migrator.sketch.SATSolver.SolverStatus;

public class SATSolverTest {

    class Variable extends BoolVariable {
        public Variable(int index) {
            super(index);
        }
    }

    @Test
    public void testSatUnsat() throws ContradictionException {
        Variable x1 = new Variable(1);
        Variable x2 = new Variable(2);

        List<Variable> empty = new ArrayList<>();
        List<Variable> l1 = Collections.singletonList(x1);
        List<Variable> l2 = Collections.singletonList(x2);
        List<Variable> l12 = new ArrayList<>();
        l12.add(x1);
        l12.add(x2);

        SATSolver<Variable> solver = new SATSolver<>();
        solver.addClause(new Clause<Variable>(empty, l12));
        solver.addClause(new Clause<Variable>(l12, empty));
        solver.addClause(new Clause<Variable>(l1, l2));
        Assert.assertEquals(SolverStatus.SAT, solver.solve());

        solver.addClause(new Clause<Variable>(l2, l1));
        Assert.assertEquals(SolverStatus.UNSAT, solver.solve());
    }

    @Test
    public void testExactOne() throws ContradictionException {
        Variable x1 = new Variable(1);
        Variable x2 = new Variable(2);
        Variable x3 = new Variable(3);
        List<Variable> list = new ArrayList<>();
        list.add(x1);
        list.add(x2);
        list.add(x3);
        List<Variable> empty = new ArrayList<>();

        SATSolver<Variable> solver = new SATSolver<>();
        solver.addClause(new ExactOneClause<Variable>(list));
        Assert.assertEquals(SolverStatus.SAT, solver.solve());

        solver.addClause(new Clause<Variable>(empty, Collections.singletonList(x1)));
        solver.addClause(new Clause<Variable>(empty, Collections.singletonList(x2)));
        Assert.assertEquals(SolverStatus.SAT, solver.solve());
        List<Variable> posVars = solver.findModel();
        Assert.assertEquals(1, posVars.size());
        Assert.assertEquals(x3, posVars.get(0));
    }

}
