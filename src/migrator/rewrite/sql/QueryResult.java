package migrator.rewrite.sql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import migrator.rewrite.ast.ColumnRef;

/**
 * The result of a query.
 * Contains rows, but the data in the rows
 * are stored as references (indices) to the
 * rows in the underlying tables.
 */
public class QueryResult {
    /**
     * The shape of this query result,
     * i.e. the list of columns in the result,
     * in order.
     */
    public List<ColumnRef> shape;
    /**
     * A list of rows in this query.
     * Each row is stored as an array of columns,
     * whose values are the indices of the rows
     * in the underlying table for that column.
     */
    public List<int[]> rows;

    /**
     * Constructs a new query result with the given shape and row indices.
     *
     * @param shape the shape (columns) of the result
     * @param rows  the indices of the rows corresponding to the columns
     */
    public QueryResult(List<ColumnRef> shape, List<int[]> rows) {
        this.shape = shape;
        this.rows = rows;
    }

    /**
     * Returns this result as a list of arrays of
     * concrete values using the given program state.
     *
     * @param state the program state holding the concrete values
     * @return a list of rows
     */
    public List<ISqlObject[]> toArray(ProgramState state) {
        @SuppressWarnings("unchecked")
        List<ISqlObject[]>[] tableRows = new List[shape.size()];
        int[] columnIndices = new int[shape.size()];
        for (ListIterator<ColumnRef> it = shape.listIterator(); it.hasNext();) {
            int i = it.nextIndex();
            ColumnRef col = it.next();
            TableState tableState = state.tables.get(col.realTableName);
            tableRows[i] = tableState.rows;
            columnIndices[i] = tableState.columns.get(col.column);
        }
        return rows.stream().map(row -> {
            assert row.length == shape.size();
            ISqlObject[] result = new ISqlObject[row.length];
            for (int i = 0; i < row.length; i++) {
                result[i] = tableRows[i].get(row[i])[columnIndices[i]];
            }
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * Print this query result to a string.
     * The output is of the form
     *
     * <pre>
     * column1  | column2
     * ---------+---------
     * Value(1) | Value(2)
     * ...      | ...
     * </pre>
     *
     * @param state the state to use when computing the values
     */
    public String print(ProgramState state) {
        StringBuilder outputBuilder = new StringBuilder();

        int[] columnLengths = new int[shape.size()];
        String[] headers = new String[shape.size()];
        Map<String, Integer> columnNameCount = new HashMap<>();
        for (ColumnRef col : shape) {
            columnNameCount.put(col.column, columnNameCount.getOrDefault(col.column, 0) + 1);
        }
        for (ListIterator<ColumnRef> it = shape.listIterator(); it.hasNext();) {
            int i = it.nextIndex();
            ColumnRef col = it.next();
            String header = columnNameCount.get(col.column) > 1 ? col.toSqlString() : col.column;
            columnLengths[i] = header.length();
            headers[i] = header;
        }
        List<ISqlObject[]> data = this.toArray(state);
        for (ISqlObject[] row : data) {
            assert row.length == shape.size();
            for (int i = 0; i < row.length; i++) {
                String value = row[i].toString();
                if (value.length() > columnLengths[i]) {
                    columnLengths[i] = value.length();
                }
            }
        }
        int maxColumnLength = 0;
        for (int length : columnLengths) {
            if (length > maxColumnLength) maxColumnLength = length;
        }
        // preprocessing done, do print
        StringBuilder builder = new StringBuilder(maxColumnLength);
        // headers
        for (int i = 0; i < headers.length; i++) {
            if (i != 0) outputBuilder.append('|');
            builder.setLength(0);
            builder.append(headers[i]);
            while (builder.length() < columnLengths[i]) {
                builder.append(' ');
            }
            outputBuilder.append(builder);
        }
        outputBuilder.append(System.lineSeparator());
        // separator
        for (int i = 0; i < headers.length; i++) {
            if (i != 0) outputBuilder.append('+');
            builder.setLength(0);
            while (builder.length() < columnLengths[i]) {
                builder.append('-');
            }
            outputBuilder.append(builder);
        }
        outputBuilder.append(System.lineSeparator());
        // rows
        for (ISqlObject[] row : data) {
            for (int i = 0; i < headers.length; i++) {
                if (i != 0) outputBuilder.append('|');
                builder.setLength(0);
                builder.append(row[i]);
                while (builder.length() < columnLengths[i]) {
                    builder.append(' ');
                }
                outputBuilder.append(builder);
            }
            outputBuilder.append(System.lineSeparator());
        }
        return outputBuilder.toString();
    }

    /**
     * Checks if this query result matches the other query result.
     *
     * @param thisState  the state to use for {@code this}
     * @param other      the other query result
     * @param otherState the state to use for {@code other}
     * @return if this query result matches the other
     */
    public boolean matches(ProgramState thisState, QueryResult other, ProgramState otherState) {
        if (other instanceof ErrorQueryResult || this instanceof ErrorQueryResult) {
            return this instanceof ErrorQueryResult && other instanceof ErrorQueryResult;
        }
        if (shape.size() != other.shape.size()) {
            return false;
        }
        if (rows.size() != other.rows.size()) {
            return false;
        }
        Iterator<ISqlObject[]> it1 = this.toArray(thisState).iterator();
        Iterator<ISqlObject[]> it2 = other.toArray(otherState).iterator();
        while (it1.hasNext()) {
            assert it2.hasNext();
            ISqlObject[] row1 = it1.next();
            ISqlObject[] row2 = it2.next();
            if (!Arrays.equals(row1, row2)) {
                return false;
            }
        }
        return true;
    }
}
