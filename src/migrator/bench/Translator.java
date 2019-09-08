package migrator.bench;

import migrator.bench.parser.BenchmarkParser;
import migrator.bench.BenchmarkAST.Program;
import migrator.util.FileUtil;

public class Translator {

    public static void main(String[] args) {
        // read arguments
        if (args.length != 4) {
            System.out.println("Usage: Translator <bench.json> <src-schema-path> <tgt-schema-path> <src-prog-path>");
            System.exit(1);
        }
        String benchmarkPath = args[0];
        String srcSchemaPath = args[1];
        String tgtSchemaPath = args[2];
        String srcProgPath = args[3];
        // parse the legacy benchmark in Json format
        Benchmark benchmark = FileUtil.readFromJsonFile(benchmarkPath, Benchmark.class);
        BenchmarkParser parser = new BenchmarkParser();
        Program srcProgAndSchema = parser.parse(benchmark.getSource());
        Program tgtProgAndSchema = parser.parse(benchmark.getTarget());
        // convert it to the new representation
        String srcSchemaText = ProgramConverter.convertSchema(srcProgAndSchema.schema);
        String tgtSchemaText = ProgramConverter.convertSchema(tgtProgAndSchema.schema);
        String srcProgText = ProgramConverter.convertTransactions(srcProgAndSchema.transactions);
        // dump to files
        FileUtil.writeStringToFile(srcSchemaPath, srcSchemaText);
        FileUtil.writeStringToFile(tgtSchemaPath, tgtSchemaText);
        FileUtil.writeStringToFile(srcProgPath, srcProgText);
    }

}
