package migrator.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.utexas.equidb.Mediator;
import migrator.LoggerWrapper;
import migrator.Migrator;
import migrator.bench.Benchmark;
import migrator.bench.BenchmarkAST.AstNode;
import migrator.bench.BenchmarkAST.InsertNode;
import migrator.bench.BenchmarkAST.RelDecl;
import migrator.bench.BenchmarkAST.Schema;
import migrator.bench.BenchmarkAST.Transaction;
import migrator.bench.Prog;
import migrator.bench.Tran;
import migrator.rewrite.ast.ForeignKeyDef;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.ast.TableDef;
import migrator.rewrite.sql.SqlProgram;

/**
 * A wrapper for the Mediator verifier.
 */
public final class MediatorVerifier {

    private static final Logger LOGGER = LoggerWrapper.getInstance();

    // Z3 query timeout in milliseconds
    private static final int Z3_TIMEOUT = Migrator.Z3_QUERY_TIMEOUT;

    /**
     * Verify if the target program is equivalent to the source program.
     *
     * @param srcSchema source schema
     * @param srcProg   source program
     * @param tgtSchema target schema
     * @param tgtProg   target program
     * @return {@code true} if two programs are equivalent
     */
    public static boolean verify(SchemaDef srcSchema, SqlProgram srcProg, SchemaDef tgtSchema, SqlProgram tgtProg) {
        List<Transaction> srcTrans;
        List<Transaction> tgtTrans;
        try {
            srcTrans = genMediatorTransactions(srcSchema, srcProg);
            tgtTrans = genMediatorTransactions(tgtSchema, tgtProg);
        } catch (TranslationException e) {
            LOGGER.log(Level.SEVERE, "Translation Error", e);
            return false;
        }
        normalizeMediatorTransactions(srcTrans, tgtTrans);
        Prog source = genMediatorProg(srcSchema, srcTrans);
        Prog target = genMediatorProg(tgtSchema, tgtTrans);
        Benchmark mediatorBenchmark = new Benchmark(source, target);
        String json = benchmarkToJson(mediatorBenchmark);
        return verifyEquivalence(json);
    }

    private static Prog genMediatorProg(SchemaDef schemaDef, List<Transaction> transactions) {
        List<String> tables = genMediatorTables(schemaDef);
        List<String> primaryKeys = genMediatorPrimaryKeys(schemaDef);
        List<String> foreignKeys = genMediatorForeignKeys(schemaDef);
        List<Tran> trans = transactions.stream()
                .map(tx -> new Tran(tx.signature.toString(), tx.statements.stream()
                        .map(AstNode::toString)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
        return new Prog(tables, primaryKeys, foreignKeys, trans);
    }

    private static String benchmarkToJson(Benchmark benchmark) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gson.toJson(benchmark);
    }

    private static boolean verifyEquivalence(String json) {
        Mediator mediator = new Mediator(LoggerWrapper.getInstance(), Z3_TIMEOUT);
        return mediator.verify(json);
    }

    private static List<String> genMediatorTables(SchemaDef schemaDef) {
        Schema schema = MediatorTranslator.translateSchemaWithoutKeys(schemaDef);
        return schema.relDecls.stream().map(RelDecl::toString).collect(Collectors.toList());
    }

    private static List<String> genMediatorPrimaryKeys(SchemaDef schemaDef) {
        List<String> pks = new ArrayList<>();
        for (String tableName : schemaDef.tableNames) {
            TableDef tableDef = schemaDef.tables.get(tableName);
            if (tableDef.primaryKey != null) {
                String column = MediatorTranslator.translateColumn(tableName, tableDef.primaryKey);
                pks.add(String.format("%s(%s)", tableDef.name, column));
            }
        }
        return pks;
    }

    private static List<String> genMediatorForeignKeys(SchemaDef schemaDef) {
        List<String> fks = new ArrayList<>();
        for (String tableName : schemaDef.tableNames) {
            TableDef tableDef = schemaDef.tables.get(tableName);
            for (String columnName : tableDef.columnNames) {
                if (tableDef.foreignKeys.containsKey(columnName)) {
                    ForeignKeyDef fkDef = tableDef.foreignKeys.get(columnName);
                    String fk = MediatorTranslator.translateColumn(tableName, columnName);
                    String rk = MediatorTranslator.translateColumn(fkDef.destTable, fkDef.destColumn);
                    fks.add(String.format("%s(%s) -> %s(%s)", tableName, fk, fkDef.destTable, rk));
                }
            }
        }
        return fks;
    }

    private static List<Transaction> genMediatorTransactions(SchemaDef schemaDef, SqlProgram program) {
        return program.methods.stream()
                .map(impl -> MediatorTranslator.translateMethod(impl, schemaDef))
                .collect(Collectors.toList());
    }

    private static void normalizeMediatorTransactions(List<Transaction> srcTrans, List<Transaction> tgtTrans) {
        normalizeInsertOrder(srcTrans, tgtTrans);
        handleCreateAndUpdateTime(srcTrans, tgtTrans);
    }

    private static void normalizeInsertOrder(List<Transaction> srcTrans, List<Transaction> tgtTrans) {
        if (srcTrans.size() != tgtTrans.size()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < srcTrans.size(); ++i) {
            Transaction srcTran = srcTrans.get(i);
            Transaction tgtTran = tgtTrans.get(i);
            if (srcTran.statements.size() > 1
                    && isInsertStatement(srcTran.statements)
                    && isInsertStatement(tgtTran.statements)
                    && srcTran.statements.size() == tgtTran.statements.size()) {
                Map<String, InsertNode> srcTableToIns = buildInsertTableMap(srcTran.statements);
                Map<String, InsertNode> tgtTableToIns = buildInsertTableMap(tgtTran.statements);
                if (srcTableToIns.keySet().equals(tgtTableToIns.keySet())) {
                    List<AstNode> statements = new ArrayList<>();
                    for (AstNode stmt : srcTran.statements) {
                        InsertNode ins = (InsertNode) stmt;
                        String table = ins.relation.name;
                        statements.add(tgtTableToIns.get(table));
                    }
                    Transaction newTgtTran = new Transaction(tgtTran.signature, statements);
                    tgtTrans.set(i, newTgtTran);
                }
            }
        }
    }

    private static boolean isInsertStatement(List<AstNode> statements) {
        for (AstNode stmt : statements) {
            if (!(stmt instanceof InsertNode)) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, InsertNode> buildInsertTableMap(List<AstNode> statements) {
        Map<String, InsertNode> ret = new HashMap<>();
        for (AstNode stmt : statements) {
            if (!(stmt instanceof InsertNode)) {
                throw new IllegalStateException();
            }
            InsertNode ins = (InsertNode) stmt;
            ret.put(ins.relation.name, ins);
        }
        return ret;
    }

    private static void handleCreateAndUpdateTime(List<Transaction> srcTrans, List<Transaction> tgtTrans) {
        srcTrans.forEach(tran -> rewriteCreateAndUpdateTime(tran.statements));
        tgtTrans.forEach(tran -> rewriteCreateAndUpdateTime(tran.statements));
    }

    private static void rewriteCreateAndUpdateTime(List<AstNode> statements) {
        // hack to handle multiple occurrences of "created_at" and "updated_at" in one function
        if (!statements.isEmpty() && statements.get(0) instanceof InsertNode) {
            if (hasMultipleValue(statements, "created_at")) {
                replaceFirstValue(statements, "created_at", "UUID_f_created_at");
            }
            if (hasMultipleValue(statements, "updated_at")) {
                replaceFirstValue(statements, "updated_at", "UUID_f_updated_at");
            }
        }
    }

    private static boolean hasMultipleValue(List<AstNode> statements, String value) {
        int count = 0;
        for (AstNode stmt : statements) {
            if (stmt instanceof InsertNode) {
                InsertNode ins = (InsertNode) stmt;
                count += ins.tuple.values.stream().filter(v -> v.equals(value)).count();
            }
        }
        return count > 1;
    }

    private static void replaceFirstValue(List<AstNode> statements, String value, String newValue) {
        for (AstNode stmt : statements) {
            if (stmt instanceof InsertNode) {
                InsertNode ins = (InsertNode) stmt;
                for (int i = 0; i < ins.tuple.values.size(); ++i) {
                    if (ins.tuple.values.get(i).equals(value)) {
                        ins.tuple.values.set(i, newValue);
                        return;
                    }
                }
            }
        }
    }

}
