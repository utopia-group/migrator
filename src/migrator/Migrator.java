package migrator;

import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import migrator.post.MediatorVerifier;
import migrator.post.ProgramSimplifier;
import migrator.rewrite.ast.SchemaDef;
import migrator.rewrite.parser.AntlrParser;
import migrator.rewrite.sql.SqlProgram;
import migrator.util.FileUtil;

public class Migrator {

    private final static Logger LOGGER = LoggerWrapper.getInstance();

    @Option(name = "-srcSchema", metaVar = "<file>", usage = "Path to the source schema", required = true)
    private String srcSchemaPath;

    @Option(name = "-tgtSchema", metaVar = "<file>", usage = "Path to the target schema", required = true)
    private String tgtSchemaPath;

    @Option(name = "-srcProg", metaVar = "<file>", usage = "Path to the source program", required = true)
    private String srcProgPath;

    @Option(name = "-output", metaVar = "<file>", usage = "Path to the output target program", required = true)
    private String tgtProgPath;

    @Option(name = "-verify", usage = "[Optional] Flag for verifying the synthesized program")
    private boolean verify = false;

    @Option(name = "-z3-timeout", metaVar = "<N>", usage = "[Optional] Time limit in milliseconds for each Z3 query")
    public static int Z3_QUERY_TIMEOUT = 2000;

    public void parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println("Usage: ./migrator.sh <options> <optional-options>");
            parser.printUsage(System.out);
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        Migrator migrator = new Migrator();
        migrator.parseArgs(args);
        migrator.run();
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        // parse the source program and schemas
        SchemaDef sourceSchema = AntlrParser.parseSchema(FileUtil.readFromFile(srcSchemaPath));
        SchemaDef targetSchema = AntlrParser.parseSchema(FileUtil.readFromFile(tgtSchemaPath));
        SqlProgram sourceProg = AntlrParser.parseProgram(FileUtil.readFromFile(srcProgPath));
        // synthesize the target program
        SqlProgram targetProg = Synthesizer.synthesize(sourceProg, sourceSchema, targetSchema);
        // write target program to the file
        String content = "failure";
        if (targetProg != null) {
            // simplify the target program
            SqlProgram simplifiedTargetProg = ProgramSimplifier.simplify(targetProg, targetSchema);
            content = simplifiedTargetProg.toSqlString();
            long synthesisEndTime = System.currentTimeMillis();
            LOGGER.info("=== Synthesis Time: " + (synthesisEndTime - startTime) + "ms");

            if (verify) {
                // verify the target program
                long verificationStartTime = System.currentTimeMillis();
                boolean equivalent = MediatorVerifier.verify(sourceSchema, sourceProg, targetSchema, simplifiedTargetProg);
                long verificationEndTime = System.currentTimeMillis();
                LOGGER.info("=== Verification Time: " + (verificationEndTime - verificationStartTime) + "ms");
                LOGGER.info("=== Verification Result: " + equivalent);
            }
        }
        FileUtil.writeStringToFile(tgtProgPath, content);

        long endTime = System.currentTimeMillis();
        LOGGER.info("=== Elapsed Time: " + (endTime - startTime) + "ms");
    }

}
