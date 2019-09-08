package migrator.bench.parser.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import migrator.bench.BenchmarkAST.AstNode;
import migrator.bench.BenchmarkAST.AttrDecl;
import migrator.bench.BenchmarkAST.RelDecl;
import migrator.bench.BenchmarkAST.Schema;
import migrator.bench.parser.BenchmarkParser;

public class AntlrAstGenTest {

    private AstNode astGen(String input) {
        List<RelDecl> relDeclList = new ArrayList<>();
        RelDecl A = new RelDecl("A", new ArrayList<>());
        RelDecl B = new RelDecl("B", new ArrayList<>());
        AttrDecl c = new AttrDecl("int", "c");
        AttrDecl d = new AttrDecl("int", "d");
        d.setForeignKey(true);
        d.setReferenceAttrDecl(c);
        RelDecl C = new RelDecl("C", Collections.singletonList(c));
        RelDecl D = new RelDecl("D", Collections.singletonList(d));
        // backward pointers
        c.setRelDecl(C);
        d.setRelDecl(D);

        AttrDecl aid = new AttrDecl("int", "aid");
        AttrDecl afk = new AttrDecl("int", "afk");
        afk.setForeignKey(true);
        afk.setReferenceAttrDecl(aid);
        RelDecl Address = new RelDecl("Address", Collections.singletonList(aid));
        RelDecl Member = new RelDecl("Member", Collections.singletonList(afk));
        // backward pointers
        aid.setRelDecl(Address);
        afk.setRelDecl(Member);

        relDeclList.addAll(Arrays.asList(new RelDecl[] { A, B, C, D, Member, Address }));
        Schema schema = new Schema(relDeclList);
        BenchmarkParser parser = new BenchmarkParser();
        return parser.parseStatement(input, schema);
    }

    private boolean equalsWithWhiteSpace(String src, String tgt) {
        return src.replaceAll("\\s+", "").equals(tgt.replaceAll("\\s+", ""));
    }

    @Test
    public void testProjectNode() {
        String input = "pi([a, b, c, d], A)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testSelectNode() {
        String input = "sigma(a=b, A)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testInPredNode() {
        String input = "sigma(in(aid, pi([afk], B)), A)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testAndPredNode() {
        String input = "sigma(and(a=b, c=d), A)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testOrPredNode() {
        String input = "sigma(or(a=b, c=d), A)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testNotPredNode() {
        String input = "sigma(not(a=b), A)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testInsertNode() {
        String input = "ins(A, (a, aa, aaa))";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testDeleteNode() {
        String input = "del(A, a=b)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testUpdateNode() {
        String input = "upd(A, a=b, c, d)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testCombination1() {
        String input = "del(Address, in(aid, pi([afk], sigma(manme=name, Member))))";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testCombination2() {
        String input = "upd(Address, in(aid, pi([afk], sigma(manme=name, Member))), maddr, addr)";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

    @Test
    public void testCombination3() {
        String input = "pi([maddr], sigma(mname=name, join(Member, Address)))";
        AstNode ast = astGen(input);
        Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
    }

}
