package migrator.post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import migrator.rewrite.RewriteUtil;
import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.ColumnValue;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.CompoundInsertQuery;
import migrator.rewrite.ast.CompoundUpdateQuery;
import migrator.rewrite.ast.DeleteQuery;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.IValue;
import migrator.rewrite.ast.InsertQuery;
import migrator.rewrite.ast.Join;
import migrator.rewrite.ast.OpPred;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.SelectQuery;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.ast.TableRef;
import migrator.rewrite.ast.UpdateQuery;

/**
 * A visitor to simplify SQL program. Specifically,
 * (1) it turns {@code CompoundDeleteQuery} to {@code DeleteQuery} if possible;
 * (2) it turns {@code CompoundUpdateQuery} to {@code UpdateQuery} if possible.
 * <p>
 * This visitor does not modify the original program.
 */
public final class ProgramSimplifyVisitor implements IQueryVisitor<IQuery> {

    private SchemaDef schema;

    public ProgramSimplifyVisitor(SchemaDef schema) {
        this.schema = schema;
    }

    @Override
    public SelectQuery visit(SelectQuery query) {
        return query;
    }

    @Override
    public InsertQuery visit(InsertQuery query) {
        return query;
    }

    @Override
    public UpdateQuery visit(UpdateQuery query) {
        return query;
    }

    @Override
    public DeleteQuery visit(DeleteQuery query) {
        return query;
    }

    @Override
    public CompoundInsertQuery visit(CompoundInsertQuery query) {
        return query;
    }

    @Override
    public IQuery visit(CompoundDeleteQuery query) {
        return canSimplifyCompoundDelete(query) ? simplifyCompoundDelete(query) : query;
    }

    @Override
    public IQuery visit(CompoundUpdateQuery query) {
        query = tryRemoveRedundantTables(query);
        query = tryReplacePredicate(query);
        return trySimplifyCompoundUpdate(query);
    }

    @Override
    public ChooseQuery visit(ChooseQuery query) {
        // Note: {@code ChooseQuery} should not appear in concrete programs
        List<List<IQuery>> newQueryLists = new ArrayList<>();
        for (List<IQuery> queryList : query.queryLists) {
            newQueryLists.add(queryList.stream()
                    .map(q -> q.accept(this))
                    .collect(Collectors.toList()));
        }
        return new ChooseQuery(newQueryLists);
    }

    private boolean canSimplifyCompoundDelete(CompoundDeleteQuery query) {
        if (query.deletedTables == null) {
            throw new IllegalArgumentException("Delete query is not concrete: " + query.toSqlString());
        }
        if (query.joins.isEmpty() && query.deletedTables.size() == 1) {
            return query.table.getReference().equals(query.deletedTables.get(0));
        }
        return false;
    }

    private DeleteQuery simplifyCompoundDelete(CompoundDeleteQuery query) {
        return new DeleteQuery(query.table, query.predicate);
    }

    private IQuery trySimplifyCompoundUpdate(CompoundUpdateQuery query) {
        if (query.joins.isEmpty()) {
            return new UpdateQuery(query.table, query.pairs, query.predicate);
        } else if (query.joins.size() == 1) {
            ColumnRef updateColumn = PostprocessUtil.getUpdateColumn(query.pairs.get(0));
            ColumnRef predColumn = PostprocessUtil.getOpPredLhsColumn(query.predicate);
            if (updateColumn.tableName.equals(predColumn.tableName)) {
                return new UpdateQuery(new TableRef(updateColumn.tableName), query.pairs, query.predicate);
            } else {
                return query;
            }
        } else {
            return query;
        }
    }

    private CompoundUpdateQuery tryRemoveRedundantTables(CompoundUpdateQuery query) {
        // Only consider the three table case
        if (query.joins.size() != 2 || query.pairs.size() != 1) {
            return query;
        }
        ColumnRef updateColumn = PostprocessUtil.getUpdateColumn(query.pairs.get(0));
        ColumnRef predColumn = PostprocessUtil.getOpPredLhsColumn(query.predicate);
        TableDef updateTable = schema.tables.get(updateColumn.tableName);
        TableDef predTable = schema.tables.get(predColumn.tableName);
        Join join = findJoinBetweenTwoTables(updateTable, predTable);
        if (join == null) {
            return query;
        }
        TableRef table = new TableRef(updateTable.name);
        List<Join> joins = Collections.singletonList(join);
        return new CompoundUpdateQuery(table, joins, query.pairs, query.predicate);
    }

    private CompoundUpdateQuery tryReplacePredicate(CompoundUpdateQuery query) {
        if (query.pairs.size() != 1 || !(query.predicate instanceof OpPred)) {
            throw new IllegalArgumentException();
        }
        OpPred pred = (OpPred) query.predicate;
        ColumnRef updateColumn = PostprocessUtil.getUpdateColumn(query.pairs.get(0));
        TableDef updateTable = schema.tables.get(updateColumn.tableName);
        ColumnRef predColumn = PostprocessUtil.getOpPredLhsColumn(query.predicate);
        if (RewriteUtil.isForeignKey(predColumn, schema)) {
            ColumnRef referenceKey = RewriteUtil.getReferenceKey(predColumn, schema);
            if (referenceKey.tableName.equals(updateColumn.tableName)) {
                IValue lhs = new ColumnValue(referenceKey);
                OpPred newPred = new OpPred(lhs, pred.rhs, pred.op);
                return new CompoundUpdateQuery(query.table, query.joins, query.pairs, newPred);
            }
        } else {
            ColumnRef foreignKey = RewriteUtil.getForeignKey(predColumn, Collections.singletonList(updateTable));
            if (foreignKey != null && foreignKey.tableName.equals(updateColumn.tableName)) {
                IValue lhs = new ColumnValue(foreignKey);
                OpPred newPred = new OpPred(lhs, pred.rhs, pred.op);
                return new CompoundUpdateQuery(query.table, query.joins, query.pairs, newPred);
            }
        }
        return query;
    }

    /**
     * Find a join between two tables. Table1 is the main table.
     *
     * @param table1 main table
     * @param table2 another table
     * @return the join between two tables, or {@code null} if there is no join.
     */
    private Join findJoinBetweenTwoTables(TableDef table1, TableDef table2) {
        for (Map.Entry<String, ForeignKeyDef> entry : table1.foreignKeys.entrySet()) {
            String columnName = entry.getKey();
            ForeignKeyDef fkDef = entry.getValue();
            if (fkDef.destTable.equals(table2.name)) {
                TableRef destTable = new TableRef(table2.name);
                ColumnRef srcColumn = new ColumnRef(columnName, table1.name);
                ColumnRef destColumn = new ColumnRef(fkDef.destColumn, fkDef.destTable);
                return new Join(destTable, srcColumn, destColumn);
            }
        }
        for (Map.Entry<String, ForeignKeyDef> entry : table2.foreignKeys.entrySet()) {
            String columnName = entry.getKey();
            ForeignKeyDef fkDef = entry.getValue();
            if (fkDef.destTable.equals(table1.name)) {
                TableRef destTable = new TableRef(table2.name);
                ColumnRef srcColumn = new ColumnRef(fkDef.destColumn, table1.name);
                ColumnRef destColumn = new ColumnRef(columnName, table2.name);
                return new Join(destTable, srcColumn, destColumn);
            }
        }
        return null;
    }

}
