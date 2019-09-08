package migrator.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import migrator.LoggerWrapper;
import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.AndPred;
import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.ConstantValue;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.FreshValue;
import migrator.rewrite.ast.IColumn;
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IPredVisitor;
import migrator.rewrite.ast.IPredicate;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.IValueVisitor;
import migrator.rewrite.ast.InPred;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.NotPred;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.OrPred;
import migrator.rewrite.ast.ParameterValue;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.Subquery;
import migrator.rewrite.ast.SubqueryValue;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.ast.UpdateQuery;
import migrator.util.ListMultiMap;
import migrator.util.PermUtil;

/**
 * A visitor that generates sketch from queries.
 * This visitor does not support
 * {@link ColumnHole}, or
 * {@link ChooseQuery} objects
 * and will throw an {@link UnsupportedOperationException} if one is visited.
 * <p>
 * This visitor does not modify the visited nodes.
 */
public final class SketchGenVisitor
        implements IQueryVisitor<IQuery>, IPredVisitor<IPredicate>, IColumnVisitor<IColumn>, IValueVisitor<IValue> {
    /**
     * Source schema.
     */
    public final SchemaDef srcSchema;
    /**
     * Target schema.
     */
    public final SchemaDef tgtSchema;
    /**
     * Value correspondence.
     */
    public final ValueCorrespondence valueCorr;
    /**
     * Join correspondence supplier.
     */
    public final IJoinCorrSupplier jcSupplier;
    /**
     * The visitor to extract all columns.
     */
    private final ColumnExtractVisitor columnExtractor;
    /**
     * The logger.
     */
    private final static Logger LOGGER = LoggerWrapper.getInstance();
    /**
     * Index for fresh values.
     */
    private int freshValueIndex = 0;

    /**
     * Create a sketch generation visitor.
     *
     * @param srcSchema        source schema
     * @param tgtSchema        target schema
     * @param valueCorr        value correspondence
     * @param joinCorrSupplier join correspondence supplier
     */
    public SketchGenVisitor(
            SchemaDef srcSchema,
            SchemaDef tgtSchema,
            ValueCorrespondence valueCorr,
            IJoinCorrSupplier joinCorrSupplier) {
        this.srcSchema = srcSchema;
        this.tgtSchema = tgtSchema;
        this.valueCorr = valueCorr;
        this.jcSupplier = joinCorrSupplier;
        this.columnExtractor = new ColumnExtractVisitor();
    }

    @Override
    public IQuery visit(SelectQuery query) {
        // collect all columns used in the query
        Set<ColumnRef> usedColumns = query.accept(columnExtractor);
        // generate all target join chains
        JoinChain srcChain = new JoinChain(query.table, query.joins);
        List<JoinChain> tgtChains = jcSupplier.getJoinChainsForColumns(valueCorr, srcChain, usedColumns);
        // generate sketch
        List<List<IQuery>> queryLists = new ArrayList<>();
        for (JoinChain tgtChain : tgtChains) {
            List<IColumn> newColumns = new ArrayList<>();
            for (IColumn column : query.columns) {
                newColumns.add(column.accept(this));
            }
            IPredicate newPred = query.predicate.accept(this);
            SelectQuery newQuery = new SelectQuery(newColumns, tgtChain.table, tgtChain.joins, newPred);
            queryLists.add(Collections.singletonList(newQuery));
        }
        return new ChooseQuery(queryLists);
    }

    @Override
    public IQuery visit(InsertQuery query) {
        List<InsertQuery> insertions = Collections.singletonList(query);
        CompoundInsertQuery compoundQuery = new CompoundInsertQuery(insertions);
        return compoundQuery.accept(this);
    }

    @Override
    public IQuery visit(DeleteQuery query) {
        TableRef table = query.table;
        List<Join> joins = Collections.emptyList();
        IPredicate pred = query.predicate;
        List<String> deletedTables = Collections.singletonList(table.getTable());
        CompoundDeleteQuery compoundQuery = new CompoundDeleteQuery(table, joins, pred, deletedTables);
        return compoundQuery.accept(this);
    }

    @Override
    public IQuery visit(UpdateQuery query) {
        TableRef table = query.table;
        List<Join> joins = Collections.emptyList();
        List<ColumnValuePair> pairs = query.pairs;
        IPredicate pred = query.predicate;
        CompoundUpdateQuery compoundQuery = new CompoundUpdateQuery(table, joins, pairs, pred);
        return compoundQuery.accept(this);
    }

    @Override
    public IQuery visit(CompoundInsertQuery query) {
        // compute the reverse mapping for all column value pairs
        ListMultiMap<ColumnRef, ColumnValuePair> mapping = getReverseMapping(query);
        // the reverse mapping is only used for this sanity check
        reverseMappingSanityCheck(mapping);
        // collect all columns used in the insertion, excluding the foreign keys and reference keys
        Set<ColumnRef> usedColumns = query.accept(columnExtractor);
        Set<ColumnRef> fkAndRefKeys = new HashSet<>();
        for (InsertQuery ins : query.insertions) {
            String tableName = ins.table.getTable();
            for (Map.Entry<String, ForeignKeyDef> entry : srcSchema.tables.get(tableName).foreignKeys.entrySet()) {
                String columnName = entry.getKey();
                ColumnRef refKey = entry.getValue().toColumnRef();
                fkAndRefKeys.add(new ColumnRef(columnName, tableName));
                fkAndRefKeys.add(refKey);
            }
        }
        usedColumns.removeAll(fkAndRefKeys);
        // generate the power set of target join chains
        JoinChain srcChain = findJoinChain(query);
        List<JoinChain> tgtChains = jcSupplier.getJoinChainsForColumns(valueCorr, srcChain, usedColumns);
        powersetSanityCheck(tgtChains);
        List<List<JoinChain>> tgtChainPowerset = PermUtil.nonEmptyPowerSet(tgtChains);
        // generate sketch
        List<List<IQuery>> queryLists = new ArrayList<>();
        for (List<JoinChain> chainList : tgtChainPowerset) {
            List<IQuery> queryList = new ArrayList<>();
            for (JoinChain chain : chainList) {
                Map<ColumnRef, IValue> valueMapping = genTargetValueMapping(query, chain);
                // generate a list of simple inserts instead of a compound insert
                {
                    InsertQuery newQuery = genInsertQuery(chain.table, valueMapping);
                    queryList.add(newQuery);
                }
                for (Join join : chain.joins) {
                    InsertQuery newQuery = genInsertQuery(join.getDest(), valueMapping);
                    queryList.add(newQuery);
                }
            }
            queryLists.add(queryList);
        }
        return new ChooseQuery(queryLists);
    }

    @Override
    public IQuery visit(CompoundDeleteQuery query) {
        // collect all columns used in the delete, including those in the table list
        // excluding foreign keys and reference keys
        Set<ColumnRef> usedColumns = query.accept(columnExtractor);
        for (String table : query.deletedTables) {
            TableRef tableRef = new TableRef(table);
            usedColumns.addAll(RewriteUtil.allColumnsInTable(tableRef, srcSchema));
        }
        Set<ColumnRef> fkAndRefKeys = new HashSet<>();
        for (Join join : query.joins) {
            fkAndRefKeys.add(join.getSrcColumn());
            fkAndRefKeys.add(join.getDestColumn());
        }
        usedColumns.removeAll(fkAndRefKeys);
        // generate the power set of target join chains
        JoinChain srcChain = new JoinChain(query.table, query.joins);
        List<JoinChain> tgtChains = jcSupplier.getJoinChainsForColumns(valueCorr, srcChain, usedColumns);
        powersetSanityCheck(tgtChains);
        List<List<JoinChain>> tgtChainPowerset = PermUtil.nonEmptyPowerSet(tgtChains);
        // generate sketch
        List<List<IQuery>> queryLists = new ArrayList<>();
        for (List<JoinChain> chainList : tgtChainPowerset) {
            List<IQuery> queryList = new ArrayList<>();
            for (JoinChain chain : chainList) {
                TableRef table = chain.table;
                List<Join> joins = chain.joins;
                IPredicate pred = query.predicate.accept(this);
                CompoundDeleteQuery newQuery = new CompoundDeleteQuery(table, joins, pred, null);
                queryList.add(newQuery);
            }
            queryLists.add(queryList);
        }
        return new ChooseQuery(queryLists);
    }

    @Override
    public IQuery visit(CompoundUpdateQuery query) {
        // collect all columns used in the update
        Set<ColumnRef> usedColumns = query.accept(columnExtractor);
        // generate the power set of target join chains
        JoinChain srcChain = new JoinChain(query.table, query.joins);
        List<JoinChain> tgtChains = jcSupplier.getJoinChainsForColumns(valueCorr, srcChain, usedColumns);
        powersetSanityCheck(tgtChains);
        List<List<JoinChain>> tgtChainPowerset = PermUtil.nonEmptyPowerSet(tgtChains);
        // generate sketch
        List<List<IQuery>> queryLists = new ArrayList<>();
        for (List<JoinChain> chainList : tgtChainPowerset) {
            List<IQuery> queryList = new ArrayList<>();
            for (JoinChain chain : chainList) {
                TableRef table = chain.table;
                List<Join> joins = chain.joins;
                List<ColumnValuePair> pairs = query.pairs.stream()
                        .map(pair -> visitColumnValuePair(pair))
                        .collect(Collectors.toList());
                IPredicate pred = query.predicate.accept(this);
                CompoundUpdateQuery newQuery = new CompoundUpdateQuery(table, joins, pairs, pred);
                queryList.add(newQuery);
            }
            queryLists.add(queryList);
        }
        return new ChooseQuery(queryLists);
    }

    @Override
    public IQuery visit(ChooseQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AndPred visit(AndPred node) {
        IPredicate lhs = node.lhs.accept(this);
        IPredicate rhs = node.rhs.accept(this);
        return new AndPred(lhs, rhs);
    }

    @Override
    public OrPred visit(OrPred node) {
        IPredicate lhs = node.lhs.accept(this);
        IPredicate rhs = node.rhs.accept(this);
        return new OrPred(lhs, rhs);
    }

    @Override
    public NotPred visit(NotPred node) {
        return new NotPred(node.predicate.accept(this));
    }

    @Override
    public OpPred visit(OpPred node) {
        IValue lhs = node.lhs.accept(this);
        IValue rhs = node.rhs.accept(this);
        return new OpPred(lhs, rhs, node.op);
    }

    @Override
    public InPred visit(InPred node) {
        IValue lhs = node.lhs.accept(this);
        Subquery rhs = visitSubquery(node.rhs);
        return new InPred(lhs, rhs);
    }

    @Override
    public IColumn visit(ColumnRef node) {
        if (!valueCorr.containsKey(node)) {
            throw new RewriteException("Value correspondence cannot map " + node.toSqlString());
        }
        List<ColumnRef> columns = valueCorr.get(node);
        if (columns.size() == 1) {
            return columns.get(0);
        } else {
            return new ColumnHole(columns);
        }
    }

    @Override
    public IColumn visit(ColumnHole node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstantValue visit(ConstantValue node) {
        return node;
    }

    @Override
    public ParameterValue visit(ParameterValue node) {
        return node;
    }

    @Override
    public ColumnValue visit(ColumnValue node) {
        return new ColumnValue(node.column.accept(this));
    }

    @Override
    public SubqueryValue visit(SubqueryValue node) {
        return new SubqueryValue(visitSubquery(node.query));
    }

    @Override
    public FreshValue visit(FreshValue node) {
        return node;
    }

    private ColumnValuePair visitColumnValuePair(ColumnValuePair node) {
        IColumn column = node.column.accept(this);
        IValue value = node.value.accept(this);
        return new ColumnValuePair(column, value);
    }

    private Subquery visitSubquery(Subquery node) {
        throw new UnsupportedOperationException();
    }

    private ListMultiMap<ColumnRef, ColumnValuePair> getReverseMapping(CompoundInsertQuery query) {
        ListMultiMap<ColumnRef, ColumnValuePair> mapping = new ListMultiMap<>();
        for (InsertQuery ins : query.insertions) {
            for (ColumnValuePair pair : ins.pairs) {
                assert pair.column instanceof ColumnRef;
                ColumnRef columnRef = (ColumnRef) pair.column;
                if (!valueCorr.containsKey(columnRef)) {
                    // value correspondence does not have this column in the domain
                    continue;
                }
                for (ColumnRef tgtColumnRef : valueCorr.get(columnRef)) {
                    mapping.put(tgtColumnRef, pair);
                }
            }
        }
        return mapping;
    }

    private void reverseMappingSanityCheck(ListMultiMap<ColumnRef, ColumnValuePair> mapping) {
        for (ColumnRef tgtColumn : mapping.keySet()) {
            List<ColumnValuePair> srcPairs = mapping.get(tgtColumn);
            assert srcPairs != null && !srcPairs.isEmpty();
            if (srcPairs.size() > 1) {
                IValue value = srcPairs.get(0).value;
                for (int i = 1; i < srcPairs.size(); ++i) {
                    IValue currValue = srcPairs.get(i).value;
                    if (!currValue.equals(value)) {
                        throw new RewriteException(String.format("Value (%s) for column (%s) is ambiguous, was (%s)",
                                value.toSqlString(), tgtColumn.toSqlString(), currValue.toSqlString()));
                    }
                }
            }
        }
    }

    private JoinChain findJoinChain(CompoundInsertQuery query) {
        // TODO: should do topological sort on graphs
        // special case: only one insert in the compound insert statement
        if (query.insertions.size() == 1) {
            TableRef tableRef = query.insertions.get(0).table;
            List<Join> joins = Collections.emptyList();
            return new JoinChain(tableRef, joins);
        }
        // collect all tables
        Set<String> tables = new LinkedHashSet<>();
        query.insertions.forEach(ins -> tables.add(ins.table.getTable()));
        Set<String> visited = new LinkedHashSet<>();
        // find a table that has a foreign key
        TableRef tableRef = null;
        for (InsertQuery ins : query.insertions) {
            Map<String, ForeignKeyDef> fks = srcSchema.tables.get(ins.table.getTable()).foreignKeys;
            for (ForeignKeyDef fkDef : fks.values()) {
                if (tables.contains(fkDef.destTable)) {
                    visited.add(ins.table.getReference());
                    tableRef = ins.table;
                    break;
                }
            }
            if (tableRef != null) {
                break;
            }
        }
        if (tableRef == null) {
            LOGGER.severe("Cannot find a root table in " + query.toSqlString());
            throw new IllegalStateException("Cannot find a root table");
        }
        // fill in all joins
        List<Join> joins = new ArrayList<>();
        int prevSize = -1;
        while (prevSize != visited.size()) {
            prevSize = visited.size();
            for (String table : visited) {
                Map<String, ForeignKeyDef> fks = srcSchema.tables.get(table).foreignKeys;
                for (Map.Entry<String, ForeignKeyDef> entry : fks.entrySet()) {
                    String column = entry.getKey();
                    ForeignKeyDef dest = entry.getValue();
                    if (tables.contains(dest.destTable) && !visited.contains(dest.destTable)) {
                        visited.add(dest.destTable);
                        joins.add(new Join(new TableRef(dest.destTable), new ColumnRef(column, table), dest.toColumnRef()));
                    }
                }
            }
        }
        if (visited.size() != tables.size()) {
            LOGGER.severe("Cannot cover all tables in " + query.toSqlString());
            throw new IllegalStateException("Cannot cover all tables");
        }
        return new JoinChain(tableRef, joins);
    }

    private FreshValue mkFreshValue() {
        return new FreshValue(freshValueIndex++);
    }

    private Map<ColumnRef, IValue> genTargetValueMapping(CompoundInsertQuery srcInsert, JoinChain chain) {
        Map<ColumnRef, IValue> mapping = new HashMap<>();
        // assign values known from the source insertion
        for (InsertQuery ins : srcInsert.insertions) {
            for (ColumnValuePair pair : ins.pairs) {
                assert pair.column instanceof ColumnRef;
                ColumnRef srcColumn = (ColumnRef) pair.column;
                if (valueCorr.containsKey(srcColumn)) {
                    for (ColumnRef tgtColumn : valueCorr.get(srcColumn)) {
                        // ensure the value is fresh in the target program
                        // if it is fresh in the source program
                        IValue value = pair.value instanceof FreshValue ? mkFreshValue() : pair.value;
                        updateWithFKRK(mapping, tgtColumn, value, chain);
                    }
                }
            }
        }
        // assign fresh values for other columns in the target insertion
        List<TableRef> tableRefs = RewriteUtil.getTablesFromJoinChain(chain);
        for (TableRef tableRef : tableRefs) {
            TableDef tableDef = tgtSchema.tables.get(tableRef.getTable());
            for (String columnName : tableDef.columns.keySet()) {
                ColumnRef tgtColumn = new ColumnRef(columnName, tableDef.name);
                if (!mapping.containsKey(tgtColumn)) {
                    IValue value = mkFreshValue();
                    updateWithFKRK(mapping, tgtColumn, value, chain);
                }
            }
        }
        return mapping;
    }

    private void updateWithFKRK(Map<ColumnRef, IValue> map, ColumnRef key, IValue value, JoinChain chain) {
        if (map.containsKey(key)
                && !map.get(key).equals(value)
                && !(map.get(key) instanceof FreshValue && value instanceof FreshValue)) {
            throw new RewriteException(String.format("Found key (%s) has a different value (%s), was (%s)",
                    key.toSqlString(), value, map.get(key)));
        }

        map.put(key, value);
        for (Join join : chain.joins) {
            ColumnRef fk = join.getSrcColumn();
            ColumnRef rk = join.getDestColumn();
            if (key.equals(fk)) {
                map.put(rk, value);
            } else if (key.equals(rk)) {
                map.put(fk, value);
            }
        }
    }

    /**
     * Generate an {@code INSERT} statement for the specified table
     *
     * @param table        the specified table
     * @param valueMapping mapping from target columns to their values
     * @return the insert statement
     */
    private InsertQuery genInsertQuery(TableRef tableRef, Map<ColumnRef, IValue> valueMapping) {
        TableDef tableDef = tgtSchema.tables.get(tableRef.getTable());
        List<ColumnValuePair> pairs = new ArrayList<>();
        for (String columnName : tableDef.columns.keySet()) {
            ColumnRef columnRef = new ColumnRef(columnName, tableDef.name);
            assert valueMapping.containsKey(columnRef) : "Value mapping does not contain " + columnRef;
            IValue value = valueMapping.get(columnRef);
            pairs.add(new ColumnValuePair(columnRef, value));
        }
        return new InsertQuery(tableRef, pairs);
    }

    private <T> void powersetSanityCheck(List<T> list) {
        if (list.size() > 10) {
            LOGGER.fine(String.format("Warning: large (%s) list to compute power set", list.size()));
        }
    }
}
