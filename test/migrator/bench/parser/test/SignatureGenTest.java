package migrator.bench.parser.test;

import org.junit.Assert;
import org.junit.Test;

import migrator.bench.BenchmarkAST.Signature;
import migrator.bench.parser.BenchmarkParser;

public class SignatureGenTest {

    @Test
    public void testSignature1() {
        String input = "int tran()";
        Signature signature = new BenchmarkParser().parseSignature(input);
        Assert.assertEquals(input, signature.toString());
    }

    @Test
    public void testSignature2() {
        String input = "int tran(String a, int b, String c)";
        Signature signature = new BenchmarkParser().parseSignature(input);
        Assert.assertEquals(input, signature.toString());
    }

    @Test
    public void testSignature3() {
        String input = "List<String> tran(int a, String b, int c)";
        Signature signature = new BenchmarkParser().parseSignature(input);
        Assert.assertEquals(input, signature.toString());
    }

}
