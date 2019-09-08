package migrator.sketch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sat4j.specs.ContradictionException;

import migrator.LoggerWrapper;
import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.program.Invocation;
import migrator.rewrite.sql.EquivCheckResult;
import migrator.rewrite.sql.EquivalenceChecker;
import migrator.rewrite.sql.ISqlObject;
import migrator.rewrite.sql.SqlProgram;
import migrator.sketch.SATSolver.Clause;
import migrator.sketch.SATSolver.ExactOneClause;
import migrator.sketch.SATSolver.SolverStatus;
import migrator.util.ImmutablePair;

/**
 * A solver for instantiating holes in the sketch.
 */
public class SketchSolver implements ISketchSolver {

    public static enum HoleKind {
        /**
         * Hole for choices
         */
        JOINCHAIN,
        /**
         * Hole for table lists in compound deletion query
         */
        TABLELIST,
        /**
         * Hole for columns
         */
        COLUMN,
    }

    /**
     * Variable used in the sketch.
     */
    public static class SketchVariable extends BoolVariable {
        /**
         * The kind of hole that this sketch variable correspond to.
         */
        public final HoleKind holeKind;
        /**
         * The hole associated with the variable. It could either be an instance of
         * {@link ChooseQuery}, {@link CompoundDeleteQuery}, or {@link ColumnHole}.
         */
        public final IAstNode hole;
        /**
         * Index of the method that this variable is in.
         */
        public final int methodIndex;
        /**
         * The candidate associated with the variable.
         */
        public final Object candidate;

        SketchVariable(int index, IAstNode hole, Object candidate, int methodIndex, HoleKind holeKind) {
            super(index);
            this.hole = hole;
            this.candidate = candidate;
            this.methodIndex = methodIndex;
            this.holeKind = holeKind;
        }
    }

    /**
     * Factory that creates sketch variables.
     */
    public static class SketchVariableFactory {
        /**
         * Unique index to be assigned for sketch variables.
         */
        private int index = 1;
        /**
         * Map for finding sketch variables given a hole and a candidate.
         */
        private final Map<ImmutablePair<IAstNode, Object>, SketchVariable> variableMap;

        public SketchVariableFactory() {
            variableMap = new HashMap<>();
        }

        /**
         * Creates a sketch variable for join chain choices.
         *
         * @param hole        the hole for join chain choice
         * @param joinchain   one possible candidate join chain for the hole
         * @param methodIndex index of method that the variable is in
         * @return a sketch variable meaning the hole is assigned to the join chain
         */
        public SketchVariable createJoinChainSketchVariable(ChooseQuery hole, List<IQuery> joinchain, int methodIndex) {
            SketchVariable variable = new SketchVariable(index++, hole, joinchain, methodIndex, HoleKind.JOINCHAIN);
            ImmutablePair<IAstNode, Object> pair = new ImmutablePair<>(hole, joinchain);
            assert !variableMap.containsKey(pair);
            variableMap.put(pair, variable);
            return variable;
        }

        /**
         * Creates a sketch variable for compound delete queries.
         *
         * @param hole        the hole for table lists in the compound delete query
         * @param list        one possible candidate table list
         * @param methodIndex index of method that the variable is in
         * @return a sketch variable meaning the hole is assigned to the table list
         */
        public SketchVariable createDeleteSketchVariable(CompoundDeleteQuery hole, List<String> list, int methodIndex) {
            SketchVariable variable = new SketchVariable(index++, hole, list, methodIndex, HoleKind.TABLELIST);
            ImmutablePair<IAstNode, Object> pair = new ImmutablePair<>(hole, list);
            assert !variableMap.containsKey(pair);
            variableMap.put(pair, variable);
            return variable;
        }

        /**
         * Creates a sketch variable for column holes.
         *
         * @param hole        the hole for columns
         * @param column      one possible candidate column
         * @param methodIndex index of method that the variable is in
         * @return a sketch variable meaning the hole is assigned to the column
         */
        public SketchVariable createColumnSketchVariable(ColumnHole hole, ColumnRef column, int methodIndex) {
            SketchVariable variable = new SketchVariable(index++, hole, column, methodIndex, HoleKind.COLUMN);
            ImmutablePair<IAstNode, Object> pair = new ImmutablePair<>(hole, column);
            assert !variableMap.containsKey(pair);
            variableMap.put(pair, variable);
            return variable;
        }

        /**
         * Check whether the factory has made a sketch variable for the given hole and candidate.
         *
         * @param hole      the hole
         * @param candidate the candidate
         * @return {@code true} if the factory has made the sketch variable
         */
        public boolean hasSketchVariable(IAstNode hole, Object candidate) {
            ImmutablePair<IAstNode, Object> pair = new ImmutablePair<>(hole, candidate);
            return variableMap.containsKey(pair);
        }

        /**
         * Find the sketch variable made by the factory for the given hole and candidate.
         *
         * @param hole      the hole
         * @param candidate the candidate
         * @return the sketch variable or {@code null} if the variable does not exist
         */
        public SketchVariable getSketchVariable(IAstNode hole, Object candidate) {
            ImmutablePair<IAstNode, Object> pair = new ImmutablePair<>(hole, candidate);
            return variableMap.get(pair);
        }
    }

    /**
     * Instance of factory for sketch variables.
     */
    protected final SketchVariableFactory factory;
    /**
     * Database schema for source program.
     */
    protected final SchemaDef sourceSchema;
    /**
     * Database schema for target program.
     */
    protected final SchemaDef targetSchema;

    protected static final Logger LOGGER = LoggerWrapper.getInstance();

    public SketchSolver(SchemaDef sourceSchema, SchemaDef targetSchema) {
        this.factory = new SketchVariableFactory();
        this.sourceSchema = sourceSchema;
        this.targetSchema = targetSchema;
    }

    @Override
    public SqlProgram completeSketch(Sketch sketch, SqlProgram sourceProg) {
        LOGGER.fine("Sketch:" + System.lineSeparator() + sketch);
        int iter = 1;
        SketchConstraint constraint = encode(sketch);
        LOGGER.info("--- Iteration: " + (iter++));
        LOGGER.fine("Constraints: " + System.lineSeparator() + constraint);
        long startTime = System.currentTimeMillis();
        SketchModel model;
        while ((model = findModel(constraint)) != null) {
            SqlProgram targetProg = instantiate(sketch, model);
            LOGGER.fine("Program:" + System.lineSeparator() + targetProg.toSqlString());
            InvocationSequence seq = checkEquivalence(sourceProg, targetProg);
            long endTime = System.currentTimeMillis();
            LOGGER.info("--- Time in iteration: " + (endTime - startTime) + "ms");

            if (seq == null) {
                return targetProg;
            }
            LOGGER.fine("Invocation Sequence:" + System.lineSeparator() + seq.toString());
            constraint.mergeConstraint(block(model, seq));

            startTime = System.currentTimeMillis();
            LOGGER.info("--- Iteration: " + (iter++));
            LOGGER.fine("Constraints: " + System.lineSeparator() + constraint);
        }
        return null;
    }

    /**
     * Encode the sketch into constraints.
     *
     * @param sketch the sketch to encode
     * @return constraint over the sketch
     */
    public SketchConstraint encode(Sketch sketch) {
        List<Clause<SketchVariable>> clauses = new ArrayList<>(); // empty list
        List<ExactOneClause<SketchVariable>> exactOneClauses = new ArrayList<>();
        // exact one clauses for join chain holes
        for (ChooseQuery query : sketch.joinHoles.keySet()) {
            int methodIndex = sketch.getMethodIndexOfHole(query);
            List<SketchVariable> variables = new ArrayList<>();
            List<List<IQuery>> candidates = sketch.joinHoles.get(query);
            for (List<IQuery> candidate : candidates) {
                SketchVariable variable = factory.createJoinChainSketchVariable(query, candidate, methodIndex);
                variables.add(variable);
            }
            exactOneClauses.add(new ExactOneClause<SketchVariable>(variables));
        }
        // exact one clauses for deletion holes
        for (CompoundDeleteQuery query : sketch.deletionHoles.keySet()) {
            int methodIndex = sketch.getMethodIndexOfHole(query);
            List<SketchVariable> variables = new ArrayList<>();
            List<List<String>> candidates = sketch.deletionHoles.get(query);
            for (List<String> candidate : candidates) {
                SketchVariable variable = factory.createDeleteSketchVariable(query, candidate, methodIndex);
                variables.add(variable);
            }
            exactOneClauses.add(new ExactOneClause<SketchVariable>(variables));
        }
        // exact one clauses for column holes
        for (ColumnHole columnHole : sketch.columnHoles.keySet()) {
            int methodIndex = sketch.getMethodIndexOfHole(columnHole);
            List<SketchVariable> variables = new ArrayList<>();
            List<ColumnRef> candidates = sketch.columnHoles.get(columnHole);
            for (ColumnRef candidate : candidates) {
                SketchVariable variable = factory.createColumnSketchVariable(columnHole, candidate, methodIndex);
                variables.add(variable);
            }
            exactOneClauses.add(new ExactOneClause<SketchVariable>(variables));
        }
        return new SketchConstraint(clauses, exactOneClauses);
    }

    /**
     * Get a model for the sketch constraint if it is satisfiable.
     *
     * @param constraint the sketch constraint
     * @return the model or {@code null} if the constraint is unsatisfiable.
     * @see SATSolver
     */
    public SketchModel findModel(SketchConstraint constraint) {
        SATSolver<SketchVariable> solver = new SATSolver<>();
        // add constraints to the solver
        try {
            for (ExactOneClause<SketchVariable> clause : constraint.exactOneClauses) {
                solver.addClause(clause);
            }
            for (Clause<SketchVariable> clause : constraint.clauses) {
                solver.addClause(clause);
            }
        } catch (ContradictionException e) {
            LOGGER.log(Level.FINE, "SAT solver contracdition, treated as UNSAT", e);
            // return null if found contradiction (UNSAT)
            return null;
        }

        // return null if UNSAT
        if (solver.solve() != SolverStatus.SAT) {
            return null;
        }

        // find the model if SAT
        List<SketchVariable> posVars = solver.findModel();
        // ensure that every hole is assigned
        assert posVars.size() == constraint.exactOneClauses.size();
        return new SketchModel(posVars);
    }

    /**
     * Instantiate the sketch given a model.
     *
     * @param sketch the sketch to instantiate
     * @param model  assignment for all holes
     * @return instantiated program
     */
    public SqlProgram instantiate(Sketch sketch, SketchModel model) {
        SketchInstantiator instantiator = new SketchInstantiator(sketch, model);
        return instantiator.instantiate();
    }

    /**
     * Check if two provided programs are equivalent by finding an invocation
     * sequence as the witness of dis-equivalence.
     *
     * @param sourceProg the source program
     * @param targetProg the target program
     * @return an invocation sequence if two programs are not equivalent, or
     *         {@code null} if two programs are equivalent
     */
    public InvocationSequence checkEquivalence(SqlProgram sourceProg, SqlProgram targetProg) {
        EquivalenceChecker checker = new EquivalenceChecker(sourceSchema, targetSchema);
        EquivCheckResult result = checker.check(sourceProg, targetProg);
        if (result != null) {
            // not equivalent
            LOGGER.fine(result.toString());
        }
        return (result == null) ? null : new InvocationSequence(result.invocations);
    }

    /**
     * Block the partial assignment for holes in the provided invocation sequence.
     *
     * @param model current assignment for holes
     * @param seq   the invocation sequence
     * @return the constraint that blocks the partial assignment
     */
    public SketchConstraint block(SketchModel model, InvocationSequence seq) {
        // collect indices of methods appeared in the invocation sequence
        Set<Integer> visitedMethodIndices = new HashSet<>();
        for (Invocation<ISqlObject> invocation : seq.invocations) {
            visitedMethodIndices.add(invocation.methodIndex);
        }
        // generate the blocking clause
        List<SketchVariable> negVars = new ArrayList<>();
        for (SketchVariable variable : model.posVars) {
            if (visitedMethodIndices.contains(variable.methodIndex)) {
                negVars.add(variable);
            }
        }
        Clause<SketchVariable> clause = new Clause<SketchVariable>(null, negVars);
        List<Clause<SketchVariable>> clauses = Stream.of(clause).collect(Collectors.toList());
        return new SketchConstraint(clauses, null);
    }
}
