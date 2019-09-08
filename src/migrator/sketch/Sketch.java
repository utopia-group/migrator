package migrator.sketch;

import java.util.List;
import java.util.Map;

import migrator.rewrite.ast.ChooseQuery;
import migrator.rewrite.ast.ColumnHole;
import migrator.rewrite.ast.ColumnRef;
import migrator.rewrite.ast.CompoundDeleteQuery;
import migrator.rewrite.ast.IAstNode;
import migrator.rewrite.ast.IQuery;
import migrator.rewrite.sql.SqlProgram;
import migrator.util.ListMultiMap;

/**
 * Data structure for sketches.
 */
public final class Sketch {
    /**
     * A SQL program possibly with holes
     */
    public SqlProgram sketchProgram;
    /**
     * join chain hole -> list of candidates
     */
    public ListMultiMap<ChooseQuery, List<IQuery>> joinHoles;
    /**
     * table list hole in compound deletion -> list of candidates
     */
    public ListMultiMap<CompoundDeleteQuery, List<String>> deletionHoles;
    /**
     * column hole -> list of candidates
     */
    public ListMultiMap<ColumnHole, ColumnRef> columnHoles;
    /**
     * join/deletion/column hole -> method index
     */
    public Map<IAstNode, Integer> holeToMethodIndex;

    /**
     * Create a sketch from a SQL program possibly with holes.
     *
     * @param sqlProgram SQL program possibly with holes
     */
    public Sketch(SqlProgram sqlProgram) {
        this.sketchProgram = sqlProgram;
        extractHoles(sqlProgram);
    }

    private void extractHoles(SqlProgram sqlProgram) {
        HoleExtractor holeExtractor = new HoleExtractor(sqlProgram);
        holeExtractor.extract();
        joinHoles = holeExtractor.getJoinHoles();
        deletionHoles = holeExtractor.getDeletionHoles();
        columnHoles = holeExtractor.getColumnHoles();
        holeToMethodIndex = holeExtractor.getHoleToMethodIndex();
    }

    /**
     * Find the index of method that the hole is in.
     *
     * @param hole the hole
     * @return method index
     */
    public int getMethodIndexOfHole(IAstNode hole) {
        assert holeToMethodIndex.containsKey(hole) : "Cannot find index for " + hole;
        return holeToMethodIndex.get(hole);
    }

    @Override
    public String toString() {
        return sketchProgram.toSqlString();
    }
}
