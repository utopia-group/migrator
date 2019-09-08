package migrator.post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import migrator.bench.BenchmarkAST.AstNode;
import migrator.bench.BenchmarkAST.AttrDecl;
import migrator.bench.BenchmarkAST.RelDecl;
import migrator.bench.BenchmarkAST.Schema;
import migrator.bench.BenchmarkAST.Signature;
import migrator.bench.BenchmarkAST.Transaction;
import migrator.rewrite.ast.ColumnType;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.program.Method;
import migrator.rewrite.program.MethodType;
import migrator.rewrite.program.Parameter;
import migrator.rewrite.sql.MethodImplementation;

/**
 * A translator from our language to Mediator language.
 */
public final class MediatorTranslator {

    /**
     * Translate the schema definition without primary keys and foreign keys
     *
     * @param schemaDef the schema definition
     * @return the schema in Mediator language
     */
    public static Schema translateSchemaWithoutKeys(SchemaDef schemaDef) {
        List<RelDecl> relDecls = new ArrayList<>();
        for (String tableName : schemaDef.tableNames) {
            TableDef tableDef = schemaDef.tables.get(tableName);
            relDecls.add(translateTableDef(tableDef));
        }
        return new Schema(relDecls);
    }

    private static RelDecl translateTableDef(TableDef tableDef) {
        String tableName = tableDef.name;
        List<AttrDecl> attrDecls = new ArrayList<>();
        for (String columnName : tableDef.columnNames) {
            ColumnType columnType = tableDef.columns.get(columnName);
            attrDecls.add(new AttrDecl(columnType.sqlType, translateColumn(tableName, columnName)));
        }
        return new RelDecl(tableName, attrDecls);
    }

    /**
     * Translate a method to a transaction in Mediator.
     *
     * @param method    the method
     * @param schemaDef the schema definition
     * @return the transaction in Mediator language
     */
    public static Transaction translateMethod(MethodImplementation method, SchemaDef schemaDef) {
        Signature signature = translateMethodSig(method.signature);
        MediatorTranslateVisitor visitor = new MediatorTranslateVisitor(schemaDef);
        List<AstNode> statements = method.queries.stream()
                .flatMap(query -> query.accept(visitor).stream())
                .collect(Collectors.toList());
        return new Transaction(signature, statements);
    }

    private static Signature translateMethodSig(Method signature) {
        String retType = translateMethodType(signature.type);
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        for (Parameter param : signature.parameters) {
            paramTypes.add(param.type);
            paramNames.add(param.name);
        }
        return new Signature(retType, signature.name, paramTypes, paramNames);
    }

    private static String translateMethodType(MethodType type) {
        if (type == MethodType.UPDATE) {
            return "void";
        } else if (type == MethodType.QUERY) {
            return "List<Tuple>";
        } else {
            throw new RuntimeException("Unknown method type");
        }
    }

    static String translateColumn(String tableName, String columnName) {
        return String.format("%s.%s", tableName, columnName);
    }
}
