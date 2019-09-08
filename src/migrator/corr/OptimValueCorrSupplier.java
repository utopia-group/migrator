package migrator.corr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.sql.MethodImplementation;
import migrator.rewrite.sql.SqlProgram;
import migrator.util.ListMultiMap;

/**
 * Supplies value correspondences in order of rank. Determine the unchanged
 * tables first, and then apply the MaxSAT approach.
 */
public final class OptimValueCorrSupplier implements IValueCorrSupplier {

    private static final Logger LOGGER = LoggerWrapper.getInstance();

    /**
     * The weight associated with duplication.
     */
    public static final int DUPLICATION_WEIGHT = 100;
    /**
     * Solver time out in seconds.
     */
    public static final int TIMEOUT = 86400;

    private SchemaDef srcSchema;
    private SchemaDef destSchema;
    private SqlProgram srcProg;
    private WeightedMaxSatDecorator solver;
    private ISimilarityStrategy similarityStrategy;
    private ISolver modelIterator;
    // variables[srcIndex][destIndex]
    private List<List<Variable>> variables;

    /**
     * Unchanged table definitions in the schema. source -> target
     */
    private Map<TableDef, TableDef> unchangedTables;

    /**
     * Constructs a new value correspondence supplier using the schemas and
     * similarity strategy. Determine the unchanged tables first, and then apply the
     * MaxSAT approach.
     *
     * @param srcSchema          the source schema
     * @param destSchema         the destination schema
     * @param srcProg            the source program
     * @param similarityStrategy the strategy to use when computing similarities
     *                           between columns
     */
    public OptimValueCorrSupplier(
            SchemaDef srcSchema,
            SchemaDef destSchema,
            SqlProgram srcProg,
            ISimilarityStrategy similarityStrategy) {
        this.srcSchema = srcSchema;
        this.destSchema = destSchema;
        this.srcProg = srcProg;
        this.similarityStrategy = similarityStrategy;
        unchangedTables = findUnchangedTables(srcSchema, destSchema);
        LOGGER.fine("Unchanged tables: " + System.lineSeparator() + unchangedTables.keySet().stream()
                .map(TableDef::toString).collect(Collectors.joining(System.lineSeparator())));
        solver = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        init();
    }

    /**
     * Find unchanged table definitions by comparing the {@code toSqlString} result.
     *
     * @param srcSchema  source schema
     * @param destSchema target schema
     * @return a map of unchanged table definitions, from source to target.
     */
    private Map<TableDef, TableDef> findUnchangedTables(SchemaDef srcSchema, SchemaDef destSchema) {
        Map<TableDef, TableDef> ret = new HashMap<>();
        Map<String, TableDef> destTableStrs = new HashMap<>();
        for (TableDef destTableDef : destSchema.tables.values()) {
            destTableStrs.put(destTableDef.toSqlString(), destTableDef);
        }
        for (String srcTableName : srcSchema.tables.keySet()) {
            TableDef srcTableDef = srcSchema.tables.get(srcTableName);
            String srcTableStr = srcTableDef.toSqlString();
            if (destTableStrs.containsKey(srcTableStr)) {
                assert !ret.containsKey(srcTableDef);
                ret.put(srcTableDef, destTableStrs.get(srcTableStr));
            }
        }
        return ret;
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
     * Abstraction of variables used in the MaxSAT solver.
     */
    private static final class Variable {
        public final ColumnRef srcColumn;
        public final ColumnRef destColumn;
        public final int index;

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

    private List<TypedColumn> getChangedColumns(SchemaDef srcSchema, Set<TableDef> unchangedTableDefs) {
        List<TypedColumn> ret = new ArrayList<>();
        for (Map.Entry<String, TableDef> tableEntry : srcSchema.tables.entrySet()) {
            String tableName = tableEntry.getKey();
            TableDef tableDef = tableEntry.getValue();
            if (!unchangedTableDefs.contains(tableDef)) {
                for (Map.Entry<String, ColumnType> columnEntry : tableDef.columns.entrySet()) {
                    String columnName = columnEntry.getKey();
                    ColumnType columnType = columnEntry.getValue();
                    ret.add(new TypedColumn(new ColumnRef(columnName, tableName), columnType));
                }
            }
        }
        return ret;
    }

    private Set<ColumnRef> extractQueriedColumns(SqlProgram program) {
        Set<ColumnRef> ret = new HashSet<>();
        QueryColumnExtractVisitor visitor = new QueryColumnExtractVisitor();
        for (MethodImplementation method : program.methods) {
            for (IQuery query : method.queries) {
                ret.addAll(query.accept(visitor));
            }
        }
        return ret;
    }

    private Map<ColumnRef, IValue> extractInsertValues(SqlProgram program) {
        Map<ColumnRef, IValue> ret = new HashMap<>();
        InsertValueExtractVisitor visitor = new InsertValueExtractVisitor();
        for (MethodImplementation method : program.methods) {
            for (IQuery query : method.queries) {
                Map<ColumnRef, IValue> map = query.accept(visitor);
                // TODO: handle duplicated keys
                for (ColumnRef columnRef : map.keySet()) {
                    ret.put(columnRef, map.get(columnRef));
                }
            }
        }
        return ret;
    }

    private void init() {
        List<TypedColumn> srcColumns = getChangedColumns(srcSchema, unchangedTables.keySet());
        List<TypedColumn> destColumns = getChangedColumns(destSchema, new HashSet<>(unchangedTables.values()));
        Set<ColumnRef> queriedColumn = extractQueriedColumns(srcProg);
        Map<ColumnRef, IValue> columnToValues = extractInsertValues(srcProg);

        // variables[srcIndex][destIndex]
        variables = new ArrayList<>();
        // target column reference -> variables with that target column reference
        ListMultiMap<ColumnRef, Variable> destToSrcVars = new ListMultiMap<>();

        int numVariables = 0;
        for (TypedColumn srcColumn : srcColumns) {
            List<Variable> srcVars = new ArrayList<>();
            for (TypedColumn destColumn : destColumns) {
                if (!srcColumn.columnType.equals(destColumn.columnType)) {
                    continue;
                }
                // index starting from 1
                Variable variable = new Variable(srcColumn.columnRef, destColumn.columnRef, ++numVariables);
                srcVars.add(variable);
                destToSrcVars.put(destColumn.columnRef, variable);
            }
            variables.add(srcVars);
        }
        solver.newVar(numVariables);
        solver.setTimeout(TIMEOUT);
        try {
            // hard constraints
            // each queried source column must be mapped to something
            for (List<Variable> srcVars : variables) {
                List<Variable> queriedVars = srcVars.stream()
                        .filter(variable -> queriedColumn.contains(variable.srcColumn))
                        .collect(Collectors.toList());
                if (!queriedVars.isEmpty()) {
                    solver.addHardClause(listToVecInt(queriedVars));
                }
            }
            // source columns with different values in INSERTs cannot map to the same target column
            for (ColumnRef destColumnRef : destToSrcVars.keySet()) {
                List<Variable> destVars = destToSrcVars.get(destColumnRef);
                for (ListIterator<Variable> it1 = destVars.listIterator(); it1.hasNext();) {
                    int i = it1.nextIndex();
                    Variable var1 = it1.next();
                    IValue value1 = columnToValues.get(var1.srcColumn);
                    for (ListIterator<Variable> it2 = destVars.listIterator(i + 1); it2.hasNext();) {
                        Variable var2 = it2.next();
                        IValue value2 = columnToValues.get(var2.srcColumn);
                        if (value1 != null && value2 != null && !value1.equals(value2)) {
                            solver.addHardClause(new VecInt(new int[] { -var1.index, -var2.index }));
                        }
                    }
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
        // unchanged tables
        for (TableDef table : unchangedTables.keySet()) {
            for (String column : table.columns.keySet()) {
                ColumnRef columnRef = new ColumnRef(column, table.name);
                valueCorr.put(columnRef, columnRef);
            }
        }
        // changed tables
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
