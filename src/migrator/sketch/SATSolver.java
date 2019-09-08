package migrator.sketch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/**
 * A wrapper for sat4j.
 *
 * @param <T> type for boolean variables used by the solver
 */
public final class SATSolver<T extends BoolVariable> {

    public static final int MAXVAR = 100000;
    public static final int TIMEOUT = 3600;

    public static enum SolverStatus {
        /**
         * The constraint is satisfiable.
         */
        SAT,
        /**
         * The constraint is unsatisfiable.
         */
        UNSAT,
        /**
         * The solver times out.
         */
        TIMEOUT,
    }

    /**
     * Clause that contains a list of positive and negative examples.
     *
     * @param <T> type for boolean variables used in the clause
     */
    public static class Clause<T extends BoolVariable> {
        public List<T> posVars;
        public List<T> negVars;

        public Clause(List<T> posVars, List<T> negVars) {
            assert posVars != null || negVars != null;
            this.posVars = (posVars == null) ? new ArrayList<>() : posVars;
            this.negVars = (negVars == null) ? new ArrayList<>() : negVars;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (BoolVariable variable : posVars) {
                builder.append(variable.index).append(" ");
            }
            for (BoolVariable variable : negVars) {
                builder.append("-").append(variable.index).append(" ");
            }
            return builder.toString();
        }
    }

    /**
     * Clause where only one variable evaluates to {@code true}.
     *
     * @param <T> type for boolean variables used in the clause
     */
    public static class ExactOneClause<T extends BoolVariable> {
        public List<T> variables;

        public ExactOneClause(List<T> variables) {
            this.variables = variables;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (BoolVariable variable : variables) {
                builder.append(variable.index).append(" ");
            }
            return builder.toString();
        }
    }

    // solver instance
    private ISolver solver;
    // problem instance
    private IProblem problem;
    // index -> variable
    private Map<Integer, T> indexToVars;

    public SATSolver() {
        reset();
    }

    /**
     * Reset the SAT solver by creating a new solver instance.
     */
    public void reset() {
        solver = SolverFactory.newDefault();
        solver.newVar(MAXVAR);
        solver.setTimeout(TIMEOUT);
        problem = solver;
        indexToVars = new HashMap<>();
    }

    /**
     * Register all boolean variables to the solver.
     *
     * @param variables boolean variables
     */
    private void registerVariables(List<T> variables) {
        for (T variable : variables) {
            indexToVars.put(variable.index, variable);
        }
    }

    /**
     * Add a clause to the SAT solver.
     *
     * @param clause the clause to add
     * @throws ContradictionException if the added clause contradicts with existing clauses
     */
    public void addClause(Clause<T> clause) throws ContradictionException {
        List<T> posVars = clause.posVars;
        List<T> negVars = clause.negVars;
        registerVariables(posVars);
        registerVariables(negVars);

        int[] solverClause = new int[posVars.size() + negVars.size()];
        for (int i = 0; i < posVars.size(); ++i) {
            solverClause[i] = posVars.get(i).index;
        }
        for (int i = 0; i < negVars.size(); ++i) {
            solverClause[i + posVars.size()] = -(negVars.get(i).index);
        }
        solver.addClause(new VecInt(solverClause));
    }

    /**
     * Add an ExactOne clause to the SAT solver.
     *
     * @param clause a clause where exactly one variable evaluates to {@code true}
     * @throws ContradictionException if the added clause contradicts with existing clauses
     */
    public void addClause(ExactOneClause<T> clause) throws ContradictionException {
        List<T> vars = clause.variables;
        registerVariables(vars);

        int[] solverClause = new int[vars.size()];
        for (int i = 0; i < vars.size(); ++i) {
            solverClause[i] = vars.get(i).index;
        }
        solver.addExactly(new VecInt(solverClause), 1);
    }

    /**
     * Solve constraints in the current solver instance.
     *
     * @return {@code SAT}, {@code UNSAT}, or {@code TIMEOUT}
     */
    public SolverStatus solve() {
        try {
            return problem.isSatisfiable() ? SolverStatus.SAT : SolverStatus.UNSAT;
        } catch (TimeoutException e) {
            return SolverStatus.TIMEOUT;
        }
    }

    /**
     * Find a model for the current solver instance.
     *
     * @return variables that evaluate to {@code true}
     */
    public List<T> findModel() {
        try {
            List<T> ret = new ArrayList<>();
            int[] model = problem.findModel();
            for (int index : model) {
                if (index > 0) {
                    assert indexToVars.containsKey(index) : "Cannot find index " + index;
                    ret.add(indexToVars.get(index));
                }
            }
            return ret;
        } catch (TimeoutException e) {
            throw new RuntimeException("Time out when finding model");
        }
    }
}
