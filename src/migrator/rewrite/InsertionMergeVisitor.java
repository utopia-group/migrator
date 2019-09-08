package migrator.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.QueryList;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.UpdateQuery;

/**
 * <p>
 * A visitor that merges adjacent insertions according to pk-fk relationships.
 * Insertions are only merged if the values are syntactically identical.
 * For example, the following sequence of insertions would be merged,
 * assuming {@code B.a REFERENCES A.a}:
 *
 * <pre>
 * INSERT INTO A (a, b) VALUES (1, 2)
 * INSERT INTO B (a, c) VALUES (1, 3)
 * produces: INSERT INTO A (a, b) VALUES (1, 2), INTO B (a, c) VALUES (1, 3)
 * </pre>
 * <p>
 * Use this class by visiting the queries you want to merge in order
 * then retrieving the accumulated result by calling {@link #getQueryList}.
 * <p>
 * This visitor does not handle
 * {@link CompoundInsertQuery}, or
 * {@link ChooseQuery} objects.
 * When instances of these classes are visited, an
 * {@link UnsupportedOperationException} will be thrown.
 * <p>
 * This visitor does not modify the visited objects directly.
 */
public final class InsertionMergeVisitor implements IQueryVisitor<Void> {
    /**
     * The database schema.
     */
    private final SchemaDef schema;
    /**
     * accumulated query list by visiting.
     */
    private final QueryList queryList;
    /**
     * Values assigned to columns.
     */
    private final Map<ColumnRef, IValue> assignedValues;

    /**
     * Constructs a new {@code InsertionMergeVisitor} using the given schema.
     *
     * @param schema the schema to use to determine pk-fk relationships
     */
    public InsertionMergeVisitor(SchemaDef schema) {
        this.schema = schema;
        this.queryList = new QueryList(new ArrayList<>());
        this.assignedValues = new HashMap<>();
    }

    /**
     * Returns the constructed query list.
     * The query list is accumulated through visiting queries.
     *
     * @return the constructed query list
     */
    public QueryList getQueryList() {
        return queryList;
    }

    @Override
    public Void visit(SelectQuery query) {
        queryList.queries.add(query);
        return null;
    }

    @Override
    public Void visit(InsertQuery query) {
        boolean merged = false;
        for (int i = 0; i < queryList.queries.size(); ++i) {
            IQuery q = queryList.queries.get(i);
            if (q instanceof CompoundInsertQuery) {
                CompoundInsertQuery ins = (CompoundInsertQuery) q;
                Set<ColumnRef> pks = findAllPrimaryKeys(ins);
                Set<ColumnRef> refKeys = findAllReferenceKeys(ins);
                for (ColumnRef queryPK : findAllPrimaryKeys(query)) { // zero or one queryPK
                    if (refKeys.contains(queryPK)) {
                        List<TableDef> tableDefs = new ArrayList<>();
                        ins.insertions.forEach(insertion -> tableDefs.add(schema.tables.get(insertion.table.getTable())));
                        ColumnRef fk = RewriteUtil.getForeignKey(queryPK, tableDefs);
                        IValue refValue = assignedValues.get(fk);
                        IValue queryValue = getValueOfColumn(query.pairs, queryPK);
                        if (refValue != null && refValue.equals(queryValue)) {
                            ins.insertions.add(query);
                            merged = true;
                            break;
                        }
                    }
                }
                for (ColumnRef queryFK : findAllForeignKeys(query)) {
                    ColumnRef refKey = RewriteUtil.getReferenceKey(queryFK, schema);
                    if (pks.contains(refKey)) {
                        IValue refValue = assignedValues.get(refKey);
                        IValue queryValue = getValueOfColumn(query.pairs, queryFK);
                        if (refValue != null && refValue.equals(queryValue)) {
                            ins.insertions.add(query);
                            merged = true;
                            break;
                        }
                    }
                }
            } else if (q instanceof InsertQuery) {
                InsertQuery ins = (InsertQuery) q;
                Set<ColumnRef> pks = findAllPrimaryKeys(ins);
                Set<ColumnRef> refKeys = findAllReferenceKeys(ins);
                for (ColumnRef queryPK : findAllPrimaryKeys(query)) { // zero or one queryPK
                    if (refKeys.contains(queryPK)) {
                        List<TableDef> tableDefs = Collections.singletonList(schema.tables.get(ins.table.getTable()));
                        ColumnRef fk = RewriteUtil.getForeignKey(queryPK, tableDefs);
                        IValue refValue = assignedValues.get(fk);
                        IValue queryValue = getValueOfColumn(query.pairs, queryPK);
                        if (refValue != null && refValue.equals(queryValue)) {
                            List<InsertQuery> insertions = new ArrayList<>();
                            insertions.add(ins);
                            insertions.add(query);
                            queryList.queries.set(i, new CompoundInsertQuery(insertions));
                            merged = true;
                            break;
                        }
                    }
                }
                for (ColumnRef queryFK : findAllForeignKeys(query)) {
                    ColumnRef refKey = RewriteUtil.getReferenceKey(queryFK, schema);
                    if (pks.contains(refKey)) {
                        IValue refValue = assignedValues.get(refKey);
                        IValue queryValue = getValueOfColumn(query.pairs, queryFK);
                        if (refValue != null && refValue.equals(queryValue)) {
                            List<InsertQuery> insertions = new ArrayList<>();
                            insertions.add(ins);
                            insertions.add(query);
                            queryList.queries.set(i, new CompoundInsertQuery(insertions));
                            merged = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!merged) {
            queryList.queries.add(query);
        }
        // keep record of values assigned to every columns
        recordAllColumnValues(query);
        return null;
    }

    @Override
    public Void visit(UpdateQuery query) {
        queryList.queries.add(query);
        return null;
    }

    @Override
    public Void visit(DeleteQuery query) {
        queryList.queries.add(query);
        return null;
    }

    @Override
    public Void visit(CompoundInsertQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visit(CompoundDeleteQuery query) {
        queryList.queries.add(query);
        return null;
    }

    @Override
    public Void visit(CompoundUpdateQuery query) {
        queryList.queries.add(query);
        return null;
    }

    @Override
    public Void visit(ChooseQuery query) {
        throw new UnsupportedOperationException();
    }

    private Set<ColumnRef> findAllPrimaryKeys(CompoundInsertQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (InsertQuery ins : query.insertions) {
            TableDef tableDef = schema.tables.get(ins.table.getTable());
            if (tableDef.primaryKey != null) {
                ret.add(new ColumnRef(tableDef.primaryKey, tableDef.name));
            }
        }
        return ret;
    }

    private Set<ColumnRef> findAllPrimaryKeys(InsertQuery query) {
        return findAllPrimaryKeys(new CompoundInsertQuery(Collections.singletonList(query)));
    }

    private Set<ColumnRef> findAllForeignKeys(CompoundInsertQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (InsertQuery ins : query.insertions) {
            TableDef tableDef = schema.tables.get(ins.table.getTable());
            for (String columnName : tableDef.foreignKeys.keySet()) {
                ret.add(new ColumnRef(columnName, tableDef.name));
            }
        }
        return ret;
    }

    private Set<ColumnRef> findAllForeignKeys(InsertQuery query) {
        return findAllForeignKeys(new CompoundInsertQuery(Collections.singletonList(query)));
    }

    private Set<ColumnRef> findAllReferenceKeys(CompoundInsertQuery query) {
        Set<ColumnRef> ret = new HashSet<>();
        for (InsertQuery ins : query.insertions) {
            TableDef tableDef = schema.tables.get(ins.table.getTable());
            for (ForeignKeyDef fkDef : tableDef.foreignKeys.values()) {
                ret.add(new ColumnRef(fkDef.destColumn, fkDef.destTable));
            }
        }
        return ret;
    }

    private Set<ColumnRef> findAllReferenceKeys(InsertQuery query) {
        return findAllReferenceKeys(new CompoundInsertQuery(Collections.singletonList(query)));
    }

    private void recordAllColumnValues(InsertQuery query) {
        for (ColumnValuePair pair : query.pairs) {
            assert pair.column instanceof ColumnRef;
            ColumnRef columnRef = (ColumnRef) pair.column;
            if (columnRef.tableName == null) {
                columnRef.tableName = query.table.getTable();
            }
            // TODO: handle potentially multiple values for a column reference
            assignedValues.put(columnRef, pair.value);
        }
    }

    private IValue getValueOfColumn(List<ColumnValuePair> pairs, ColumnRef column) {
        for (ColumnValuePair pair : pairs) {
            if (pair.column.equals(column)) {
                return pair.value;
            }
        }
        throw new IllegalStateException(String.format(
                "Cannot find column (%s) in column value pairs (%s)", column.toSqlString(), pairs.toString()));
    }

}
