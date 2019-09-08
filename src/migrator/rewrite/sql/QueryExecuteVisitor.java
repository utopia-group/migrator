package migrator.rewrite.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.ColumnValuePair;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.IColumn;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.Operator;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.Subquery;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.ast.UpdateQuery;
import migrator.util.ImmutableLinkedList;

/**
 * Visitor that executes queries.
 * The result of a {@code SELECT} query is a {@link QueryResult} object
 * representing the returned rows.
 * The result of any other type of query is {@code null}.
 */
public class QueryExecuteVisitor implements
        IQueryVisitor<QueryResult> {
    /**
     * The state to use when executing queries.
     */
    public ProgramState state;
    /**
     * Map from parameter name to argument value.
     */
    public Map<String, ISqlObject> arguments;
    /**
     * Factory for producing fresh values.
     */
    private FreshSqlObject.Factory freshFactory;
    /**
     * Used to cache fresh values with the same index in the same invocation.
     */
    private Map<Integer, ISqlObject> freshObjects = new HashMap<>();

    /**
     * Constructs a new query execute visitor with the given state
     * and argument map.
     *
     * @param state     the state to use when executing queries
     * @param arguments map from parameter name to argument value
     */
    public QueryExecuteVisitor(ProgramState state, Map<String, ISqlObject> arguments, FreshSqlObject.Factory freshFactory) {
        this.state = state;
        this.arguments = arguments;
        this.freshFactory = freshFactory;
    }

    /**
     * Visits a subquery by visiting an equivalent {@code SELECT} query.
     *
     * @param query the subquery to visit
     * @return the result of visiting the equivalent {@code SELECT} query
     * @see #visit(SelectQuery)
     */
    public QueryResult visit(Subquery query) {
        SelectQuery select = new SelectQuery(Collections.singletonList(query.column), query.table, query.joins, query.predicate);
        return visit(select);
    }

    @Override
    public QueryResult visit(SelectQuery query) {
        List<TableRef> allTables = new ArrayList<>();
        allTables.add(query.table);
        allTables.addAll(query.joins.stream().map(Join::getDest).collect(Collectors.toList()));
        // compute cartesian product of each table
        Stream<ImmutableLinkedList<Integer>> temp = Stream.of(ImmutableLinkedList.empty());
        // perform a right fold on allTables
        // to compute traversal
        for (ListIterator<TableRef> it = allTables.listIterator(allTables.size()); it.hasPrevious();) {
            TableRef ref = it.previous();
            temp = temp.flatMap(rows -> IntStream.range(0,
                    state.tables.get(ref.getTable()).rows.size())
                    .mapToObj(rows::prepend));
        }
        Map<String, Integer> tableMap = new HashMap<>();
        for (int i = 0; i < allTables.size(); i++) {
            tableMap.put(allTables.get(i).getReference(), i);
        }
        TableState[] tableStates = new TableState[allTables.size()];
        for (int i = 0; i < allTables.size(); i++) {
            tableStates[i] = state.tables.get(allTables.get(i).getTable());
        }
        Stream<RowValueVisitor> rowExecutors = temp
                .map(rows -> new RowValueVisitor(
                        tableMap,
                        tableStates,
                        rows.stream().mapToInt(x -> x).toArray(),
                        this,
                        arguments));
        Stream<RowValueVisitor> joined = rowExecutors.filter(visitor -> query.joins.stream()
                .allMatch(join -> visitor.visit(new OpPred(
                        new ColumnValue(join.getSrcColumn()),
                        new ColumnValue(join.getDestColumn()),
                        Operator.EQ))));
        if (query.predicate != null) {
            joined = joined.filter(query.predicate::accept);
        }
        // check there is no holes
        List<ColumnRef> columnRefs = new ArrayList<>();
        for (IColumn column : query.columns) {
            if (!(column instanceof ColumnRef)) {
                throw new UnsupportedOperationException(column.toSqlString());
            }
            columnRefs.add((ColumnRef) column);
        }
        return new QueryResult(columnRefs, joined.map(visitor -> visitor.reshape(columnRefs)).collect(Collectors.toList()));
    }

    @Override
    public QueryResult visit(InsertQuery query) {
        return visit(new CompoundInsertQuery(Collections.singletonList(query)));
    }

    @Override
    public QueryResult visit(UpdateQuery query) {
        return visit(new CompoundUpdateQuery(query.table, Collections.emptyList(), query.pairs, query.predicate));
    }

    @Override
    public QueryResult visit(DeleteQuery query) {
        List<String> deletedTables = Collections.singletonList(query.table.getReference());
        List<Join> joins = Collections.emptyList();
        CompoundDeleteQuery compound = new CompoundDeleteQuery(query.table, joins, query.predicate, deletedTables);
        return visit(compound);
    }

    @Override
    public QueryResult visit(CompoundInsertQuery query) {
        InsertionValueVisitor visitor = new InsertionValueVisitor(freshFactory, this, arguments, freshObjects);
        for (InsertQuery insert : query.insertions) {
            TableState tableState = state.tables.get(insert.table.getTable());
            int numColumns = tableState.columns.size();
            if (insert.pairs.size() != numColumns) {
                throw new IllegalArgumentException("number of pairs does not match number of columns in table");
            }
            ISqlObject[] row = new ISqlObject[numColumns];
            for (ColumnValuePair pair : insert.pairs) {
                if (!(pair.column instanceof ColumnRef)) {
                    throw new IllegalArgumentException(String.format("%s is not a column reference", pair.column));
                }
                row[tableState.columns.get(((ColumnRef) pair.column).column)] = pair.value.accept(visitor);
            }
            String pk = tableState.definition.primaryKey;
            if (pk != null) {
                // verify that primary key is unique
                int pkIndex = tableState.columns.get(pk);
                ISqlObject insertedValue = row[pkIndex];
                for (ISqlObject[] existingRow : tableState.rows) {
                    if (existingRow[pkIndex].compare(Operator.EQ, insertedValue) == true) {
                        throw new QueryExecutionException("duplicate primary key (" + pk + "): " + insertedValue);
                    }
                }
            }
            tableState.rows.add(row);
        }
        return null;
    }

    @Override
    public QueryResult visit(CompoundDeleteQuery query) {
        if (query.deletedTables == null) {
            throw new UnsupportedOperationException("compound delete query missing deleted table list");
        }
        if (query.deletedTables.size() == 0) return null;
        // generate a select query to get results to delete
        // need to look up reference -> real table
        Map<String, String> referenceLookup = Stream.concat(
                Stream.of(query.table),
                query.joins.stream()
                        .map(Join::getDest))
                .collect(Collectors.toMap(
                        TableRef::getReference,
                        TableRef::getTable));
        // for each table to delete, get an arbitrary column
        // to put in select (so index is in the row)
        List<IColumn> columns = query.deletedTables.stream()
                .map(tableName -> {
                    String realTable = referenceLookup.get(tableName);
                    return new ColumnRef(
                            state.tables.get(realTable).columns.keySet().iterator().next(),
                            tableName,
                            realTable,
                            1);
                })
                .collect(Collectors.toList());
        // used to map from index in row -> table name for deletion
        List<String> realTables = query.deletedTables.stream()
                .map(referenceLookup::get)
                .collect(Collectors.toList());
        SelectQuery select = new SelectQuery(columns, query.table, query.joins, query.predicate);
        QueryResult result = visit(select);
        // need to delete at end so we don't modify
        Map<String, Set<Integer>> rowsToDelete = new HashMap<>();
        rowsToDelete.put(query.table.getTable(), new HashSet<>());
        for (Join join : query.getJoins()) {
            String table = join.getDest().getTable();
            rowsToDelete.computeIfAbsent(table, x -> new HashSet<>());
        }
        for (int[] row : result.rows) {
            assert row.length == columns.size() &&
                    row.length == realTables.size();
            for (int i = 0; i < row.length; i++) {
                rowsToDelete.get(realTables.get(i)).add(row[i]);
            }
        }
        // delete the rows
        for (Map.Entry<String, Set<Integer>> table : rowsToDelete.entrySet()) {
            List<ISqlObject[]> rows = state.tables.get(table.getKey()).rows;
            if (rows instanceof ArrayList<?>) {
                // this wouldn't be faster unless random access is O(1)
                for (Integer i : table.getValue()) {
                    rows.set(i, null);
                }
                rows.removeIf(Objects::isNull);
            } else {
                ListIterator<ISqlObject[]> it = rows.listIterator();
                int i = 0;
                while (it.hasNext()) {
                    it.next();
                    if (table.getValue().contains(i)) {
                        it.remove();
                    }
                    i++;
                }
            }
        }
        return null;
    }

    @Override
    public QueryResult visit(CompoundUpdateQuery query) {
        if (query.pairs.size() == 0) return null;
        // generate a select query to get results to update
        // need to look up reference -> real table
        List<TableRef> allTables = Stream.concat(
                Stream.of(query.table),
                query.joins.stream().map(Join::getDest))
                .collect(Collectors.toList());
        Map<String, String> referenceLookup = allTables.stream()
                .collect(Collectors.toMap(TableRef::getReference, TableRef::getTable));
        Map<String, Integer> refs = new HashMap<>();
        TableState[] tableStates = new TableState[allTables.size()];
        for (ListIterator<TableRef> it = allTables.listIterator(); it.hasNext();) {
            int i = it.nextIndex();
            TableRef ref = it.next();
            refs.put(ref.getReference(), i);
            tableStates[i] = state.tables.get(ref.getTable());
        }
        List<IColumn> columns = query.pairs.stream().map(pair -> pair.column).collect(Collectors.toList());
        SelectQuery select = new SelectQuery(columns, query.table, query.joins, query.predicate);
        QueryResult result = visit(select);
        ListIterator<ColumnValuePair> it = query.pairs.listIterator();
        while (it.hasNext()) {
            int i = it.nextIndex();
            ColumnValuePair pair = it.next();
            if (!(pair.column instanceof ColumnRef)) {
                throw new UnsupportedOperationException();
            }
            ColumnRef columnRef = (ColumnRef) pair.column;
            TableState table = state.tables.get(referenceLookup.get(columnRef.tableName));
            int columnIndex = table.columns.get(columnRef.column);
            for (int[] row : result.rows) {
                RowValueVisitor visitor = new UpdateValueVisitor(refs, tableStates, row, freshFactory, freshObjects, this, arguments);
                state.tables.get(referenceLookup.get(columnRef.tableName)).rows.get(i)[columnIndex] = pair.value.accept(visitor);
            }
        }
        return null;
    }

    @Override
    public QueryResult visit(ChooseQuery query) {
        throw new UnsupportedOperationException("choose queries should not be in concrete programs");
    }
}
