package migrator.rewrite.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import migrator.corr.ValueCorrespondence;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.ast.QueryList;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.sql.SqlProgram;
import migrator.util.ListMultiMap;

/**
 * Contains functions used to parse objects for rewriting.
 */
public class AntlrParser {
    /**
     * Parse a query list.
     *
     * @param queries a string containing the queries to parse, in SQL syntax
     * @return the parsed query list
     */
    public static QueryList parseQueryList(String queries) {
        ANTLRInputStream inputStream = new ANTLRInputStream(queries);
        RewriteAntlrDslLexer lexer = new RewriteAntlrDslLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RewriteAntlrDslParser parser = new RewriteAntlrDslParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());
        ParseTree tree = parser.queryList();
        AstGenVisitor astGenVisitor = new AstGenVisitor();
        return (QueryList) astGenVisitor.visit(tree);
    }

    /**
     * Parse a single query.
     *
     * @param query a string containing the query to parse, in SQL syntax
     * @return the parsed query
     */
    public static IQuery parseQuery(String query) {
        ANTLRInputStream inputStream = new ANTLRInputStream(query);
        RewriteAntlrDslLexer lexer = new RewriteAntlrDslLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RewriteAntlrDslParser parser = new RewriteAntlrDslParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());
        ParseTree tree = parser.query();
        AstGenVisitor astGenVisitor = new AstGenVisitor();
        return (IQuery) astGenVisitor.visit(tree);
    }

    /**
     * Parse a schema definition.
     *
     * @param schema a string containing the schema to parse, in SQL syntax
     * @return the parsed schema
     */
    public static SchemaDef parseSchema(String schema) {
        ANTLRInputStream inputStream = new ANTLRInputStream(schema);
        RewriteAntlrDslLexer lexer = new RewriteAntlrDslLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RewriteAntlrDslParser parser = new RewriteAntlrDslParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());
        ParseTree tree = parser.schema();
        AstGenVisitor astGenVisitor = new AstGenVisitor();
        return (SchemaDef) astGenVisitor.visit(tree);
    }

    /**
     * Parse a value correspondence.
     *
     * @param mapping a string containing one mapping per line in the form
     *
     *                <pre>
     * SrcTable1.col1 -&gt; TgtTable1.col1
     * SrcTable2.col2 -&gt; TgtTable2.col2
     *                </pre>
     *
     * @return value correspondence mapping a source column to a list of target columns
     */
    public static ValueCorrespondence parserValueCorr(String mapping) {
        ListMultiMap<ColumnRef, ColumnRef> ret = new ListMultiMap<>();
        for (String line : mapping.split("[\r\n]+")) {
            if (line.length() == 0) {
                continue;
            }
            String[] parts = line.split("->");
            if (parts.length != 2) {
                throw new IllegalArgumentException(String.format("line [%s] has %d parts, expected 2", line, parts.length));
            }
            ColumnRef src = ColumnRef.from(parts[0].trim());
            ColumnRef tgt = ColumnRef.from(parts[1].trim());
            ret.put(src, tgt);
        }
        return new ValueCorrespondence(ret);
    }

    /**
     * Parse a program.
     *
     * @param program a program containing a list of methods in the form
     *
     *                <pre>
     * (query|update) methodName(INT arg1, VARCHAR arg2) {
     *     SELECT/INSERT/...;
     * }
     *                </pre>
     *
     * @return the parsed program
     */
    public static SqlProgram parseProgram(String program) {
        ANTLRInputStream inputStream = new ANTLRInputStream(program);
        RewriteAntlrDslLexer lexer = new RewriteAntlrDslLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RewriteAntlrDslParser parser = new RewriteAntlrDslParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());
        ParseTree tree = parser.program();
        AstGenVisitor astGenVisitor = new AstGenVisitor();
        return (SqlProgram) astGenVisitor.visit(tree);
    }

    private static final class ThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int column,
                String msg,
                RecognitionException e)
                throws ParseCancellationException {
            throw new ParseCancellationException(String.format("%d:%d: %s (offending symbol: %s [%s])", line, column, msg,
                    offendingSymbol, offendingSymbol.getClass().getName()));
        }
    }
}
