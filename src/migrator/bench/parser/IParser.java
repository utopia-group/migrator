package migrator.bench.parser;

import migrator.bench.BenchmarkAST.Program;
import migrator.bench.Prog;

/**
 * Interface for legacy benchmark parser.
 */
public interface IParser {

    /**
     * Parse a program in json format to BenchmarkAST
     *
     * @param prog program in json format
     * @return program AST
     */
    public Program parse(Prog prog);

}
