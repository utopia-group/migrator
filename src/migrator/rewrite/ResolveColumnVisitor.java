package migrator.rewrite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

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
import migrator.rewrite.ast.FreshValue;
import migrator.rewrite.ast.IColumn;
import migrator.rewrite.ast.IColumnVisitor;
import migrator.rewrite.ast.IPredVisitor;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.IQueryVisitor;
import migrator.rewrite.ast.ISelectQuery;
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

/**
 * A visitor that resolves column names to the referenced tables.
 * For each column reference visited, this visitor sets the following fields if
 * not already set:
 * <ul>
 * <li>{@link ColumnRef#tableName}</li>
 * <li>{@link ColumnRef#scopeIndex}</li>
 * <li>{@link ColumnRef#realTableName}</li>
 * </ul>
 * This visitor modifies the visited objects directly.
 */
public final class ResolveColumnVisitor
        implements IColumnVisitor<Void>, IPredVisitor<Void>, IQueryVisitor<Void>, IValueVisitor<Void> {
    /**
     * Stack of contexts, with the innermost being at the top.
     * A context is a list of tables used in a query
     * that can be used to resolve columns.
     * A context corresponds to a query or subquery.
     */
    private Deque<List<TableRef>> contextStack;
    private SchemaDef schemaDef;

    /**
     * Constructs a new visitor that uses the given schema definition to resolve
     * columns.
     *
     * @param schemaDef the schema definition to use
     */
    public ResolveColumnVisitor(SchemaDef schemaDef) {
        this.schemaDef = schemaDef;
        contextStack = new ArrayDeque<>();
    }

    /**
     * "Enters" the given {@code SELECT} query by pushing it onto the context stack.
     *
     * @param query the query to enter
     * @return the list of tables referenced by the query
     * @see #enterSingleTable
     */
    private List<TableRef> enterQuery(ISelectQuery query) {
        List<TableRef> refs = new ArrayList<>();
        refs.add(query.getTable());
        for (Join join : query.getJoins()) {
            refs.add(join.getDest());
        }
        contextStack.push(refs);
        return refs;
    }

    /**
     * Enters a single table by pushing it onto the context stack.
     *
     * @param table the table to enter
     * @return a list containing the single table
     * @see #enterQuery
     */
    private List<TableRef> enterSingleTable(TableRef table) {
        List<TableRef> refs = Collections.singletonList(table);
        contextStack.push(refs);
        return refs;
    }

    private void resolve(ColumnRef column) {
        if (column.tableName != null) {
            if (column.realTableName != null) {
                return;
            }
            int i = 0;
            Iterator<List<TableRef>> it = contextStack.iterator();
            for (; it.hasNext(); i++) {
                List<TableRef> context = it.next();
                for (TableRef tableRef : context) {
                    if (tableRef.getReference().equals(column.tableName)) {
                        if (column.realTableName != null) {
                            throw new IllegalArgumentException(
                                    String.format("duplicate table reference named %s", column.tableName));
                        }
                        column.realTableName = tableRef.getTable();
                        column.scopeIndex = i + 1;
                    }
                }
                if (column.realTableName != null) {
                    return;
                }
            }
            throw new IllegalArgumentException(String.format(
                    "could not find table with reference named %s", column.tableName));
        }
        String columnName = column.column;
        int i = 0;
        Iterator<List<TableRef>> it = contextStack.iterator();
        for (; it.hasNext(); i++) {
            List<TableRef> context = it.next();
            for (TableRef tableRef : context) {
                TableDef tableDef = schemaDef.tables.get(tableRef.getTable());
                if (tableDef.columns.containsKey(columnName)) {
                    if (column.tableName != null) {
                        throw new IllegalArgumentException(
                                String.format("column name is ambiguous: %s", columnName));
                    }
                    column.tableName = tableRef.getReference();
                    column.realTableName = tableRef.getTable();
                    column.scopeIndex = i + 1;
                }
            }
            if (column.tableName != null) {
                break;
            }
        }
        if (column.tableName == null) {
            throw new IllegalArgumentException(String.format(
                    "could not find column with name %s", columnName));
        }
    }

    private void visitSubquery(Subquery query) {
        List<TableRef> refs = enterQuery(query);
        resolve(query.column);
        query.predicate.accept(this);
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
    }

    @Override
    public Void visit(SelectQuery query) {
        List<TableRef> refs = enterQuery(query);
        for (IColumn column : query.columns) {
            column.accept(this);
        }
        if (query.predicate != null)
            query.predicate.accept(this);
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
        return null;
    }

    @Override
    public Void visit(InsertQuery query) {
        List<TableRef> refs = enterSingleTable(query.table);
        for (ColumnValuePair pair : query.pairs) {
            pair.column.accept(this);
            pair.value.accept(this);
        }
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
        return null;
    }

    @Override
    public Void visit(UpdateQuery query) {
        List<TableRef> refs = enterSingleTable(query.table);
        for (ColumnValuePair pair : query.pairs) {
            pair.column.accept(this);
            pair.value.accept(this);
        }
        query.predicate.accept(this);
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
        return null;
    }

    @Override
    public Void visit(DeleteQuery query) {
        List<TableRef> refs = enterSingleTable(query.table);
        query.predicate.accept(this);
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
        return null;
    }

    @Override
    public Void visit(CompoundInsertQuery query) {
        for (InsertQuery insertion : query.insertions) {
            insertion.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(CompoundDeleteQuery query) {
        List<TableRef> refs = enterQuery(query);
        if (query.predicate != null)
            query.predicate.accept(this);
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
        return null;
    }

    @Override
    public Void visit(CompoundUpdateQuery query) {
        List<TableRef> refs = enterQuery(query);
        for (ColumnValuePair pair : query.pairs) {
            pair.column.accept(this);
            pair.value.accept(this);
        }
        if (query.predicate != null) {
            query.predicate.accept(this);
        }
        if (contextStack.pop() != refs) {
            throw new IllegalStateException("popped query did not match pushed query");
        }
        return null;
    }

    @Override
    public Void visit(ChooseQuery query) {
        for (List<IQuery> list : query.queryLists) {
            for (IQuery subQuery : list) {
                subQuery.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(AndPred node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        return null;
    }

    @Override
    public Void visit(OrPred node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        return null;
    }

    @Override
    public Void visit(NotPred node) {
        node.predicate.accept(this);
        return null;
    }

    @Override
    public Void visit(OpPred node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        return null;
    }

    @Override
    public Void visit(InPred node) {
        node.lhs.accept(this);
        visitSubquery(node.rhs);
        return null;
    }

    @Override
    public Void visit(ConstantValue node) {
        return null;
    }

    @Override
    public Void visit(ParameterValue node) {
        return null;
    }

    @Override
    public Void visit(FreshValue node) {
        return null;
    }

    @Override
    public Void visit(ColumnValue node) {
        node.column.accept(this);
        return null;
    }

    @Override
    public Void visit(SubqueryValue node) {
        visitSubquery(node.query);
        return null;
    }

    @Override
    public Void visit(ColumnRef node) {
        resolve(node);
        return null;
    }

    @Override
    public Void visit(ColumnHole node) {
        // should not reach this function
        // because there is no column hole in the source program
        for (ColumnRef column : node.candidates) {
            column.accept(this);
        }
        return null;
    }
}
