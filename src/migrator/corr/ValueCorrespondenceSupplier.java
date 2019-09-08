package migrator.corr;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import migrator.LoggerWrapper;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnType;
import migrator.rewrite.ast.SchemaDef;
import migrator.util.ListMultiMap;

/**
 * Supplies value correspondences in order of rank. Purely using MaxSAT solvers.
 */
public final class ValueCorrespondenceSupplier implements IValueCorrSupplier {

    private static final Logger LOGGER = LoggerWrapper.getInstance();

    /**
     * The weight associated with duplication. This does not scale linearly with the
     * number of duplications. For <i>n</i> duplications, the actual weight will be
     * (<i>n</i> + 1 choose 2) times {@code DUPLICATION_WEIGHT}.
     */
    public static final int DUPLICATION_WEIGHT = 100;
    /**
     * Solver time out in seconds.
     */
    public static final int TIMEOUT = 600;

    private SchemaDef srcSchema;
    private SchemaDef destSchema;
    private WeightedMaxSatDecorator solver;
    private ISimilarityStrategy similarityStrategy;
    private ISolver modelIterator;
    // variables[srcIndex][destIndex]
    private List<List<Variable>> variables;

    /**
     * Constructs a new value correspondence supplier using the given schemata and
     * similarity strategy.
     *
     * @param srcSchema          the source schema
     * @param destSchema         the destination schema
     * @param similarityStrategy the strategy to use when computing similarities
     *                           between columns
     */
    public ValueCorrespondenceSupplier(SchemaDef srcSchema, SchemaDef destSchema,
            ISimilarityStrategy similarityStrategy) {
        this.srcSchema = srcSchema;
        this.destSchema = destSchema;
        this.similarityStrategy = similarityStrategy;
        solver = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        init();
    }

    /**
     * Data structure for typed columns. Essentially an immutable pair.
     */
    private static final class TypedColumn {
        public final ColumnRef columnRef;
        public final ColumnType columnType;

        public TypedColumn(ColumnRef columnRef, ColumnType columnType) {
            this.columnRef = columnRef;
            this.columnType = columnType;
        }
    }

    /**
     * Collect all columns in the schema.
     *
     * @param schema the schema
     * @return a list of typed columns
     */
    private static List<TypedColumn> getAllColumns(SchemaDef schema) {
        return schema.tables.entrySet().stream()
                .flatMap(table -> table.getValue().columns.entrySet().stream().map(
                        column -> new TypedColumn(new ColumnRef(column.getKey(), table.getKey()), column.getValue())))
                .collect(Collectors.toList());
    }

    /**
     * Abstraction of variables used in the MaxSAT solver.
     */
    private static final class Variable {
        public ColumnRef srcColumn;
        public ColumnRef destColumn;
        public int index;

        public Variable(ColumnRef srcColumn, ColumnRef destColumn, int index) {
            this.srcColumn = srcColumn;
            this.destColumn = destColumn;
            this.index = index;
        }
    }

    private static IVecInt listToVecInt(List<Variable> literals) {
        int[] lits = new int[literals.size()];
        for (ListIterator<Variable> it = literals.listIterator(); it.hasNext();) {
            int i = it.nextIndex();
            lits[i] = it.next().index;
        }
        return new VecInt(lits);
    }

    private void init() {
        List<TypedColumn> srcColumns = getAllColumns(srcSchema);
        List<TypedColumn> destColumns = getAllColumns(destSchema);

        variables = new ArrayList<>();
        int numVariables = 0;
        for (TypedColumn srcColumn : srcColumns) {
            List<Variable> srcVars = new ArrayList<>();
            for (TypedColumn destColumn : destColumns) {
                if (!srcColumn.columnType.equals(destColumn.columnType)) {
                    continue;
                }
                // index starting from 1
                srcVars.add(new Variable(srcColumn.columnRef, destColumn.columnRef, ++numVariables));
            }
            variables.add(srcVars);
        }
        solver.newVar(numVariables);
        solver.setTimeout(TIMEOUT);
        try {
            // hard constraints
            // each source thing must be mapped to something
            for (List<Variable> srcVars : variables) {
                if (!srcVars.isEmpty()) {
                    solver.addHardClause(listToVecInt(srcVars));
                }
            }
            // soft constraints
            // discourage duplication
            for (List<Variable> srcVars : variables) {
                for (ListIterator<Variable> it1 = srcVars.listIterator(); it1.hasNext();) {
                    int i = it1.nextIndex();
                    Variable var1 = it1.next();
                    for (ListIterator<Variable> it2 = srcVars.listIterator(i + 1); it2.hasNext();) {
                        Variable var2 = it2.next();
                        solver.addSoftClause(DUPLICATION_WEIGHT, new VecInt(new int[] { -var1.index, -var2.index }));
                    }
                }
            }
            // similarity weights
            for (List<Variable> srcVars : variables) {
                for (Variable var : srcVars) {
                    solver.addSoftClause(similarityStrategy.similarity(var.srcColumn, var.destColumn),
                            new VecInt(new int[] { var.index }));
                }
            }
        } catch (ContradictionException e) {
            e.printStackTrace();
            throw new RuntimeException("Contradiction found");
        }

        // set up the model iterator
        modelIterator = new ModelIterator(solver);
    }

    @Override
    public boolean hasNext() {
        try {
            return modelIterator.isSatisfiable();
        } catch (TimeoutException e) {
            LOGGER.log(Level.SEVERE, "MaxSAT solver time out", e);
            return false;
        }
    }

    @Override
    public ValueCorrespondence getNext() {
        ListMultiMap<ColumnRef, ColumnRef> valueCorr = new ListMultiMap<>();
        int[] model = modelIterator.model();
        for (List<Variable> srcVars : variables) {
            for (Variable var : srcVars) {
                if (inArray(model, var.index)) {
                    valueCorr.put(var.srcColumn, var.destColumn);
                }
            }
        }
        return new ValueCorrespondence(valueCorr);
    }

    private boolean inArray(int[] array, int elem) {
        for (int value : array) {
            if (value == elem) {
                return true;
            }
        }
        return false;
    }

}
