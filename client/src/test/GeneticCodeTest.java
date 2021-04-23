package test;
import cgat.seq.*;
import junit.framework.*;
import java.io.*;

public class GeneticCodeTest extends TestCase {
	public GeneticCodeTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(GeneticCodeTest.class);
	}
	public void testGeneticCode() throws Exception {
		GeneticCode g = GeneticCode.generate(1);
		assertEquals('I', g.getAmino("ATA"));
		assertEquals(true, g.isStart("ATG"));
		assertEquals('*', g.getAmino("TGA"));
		g = GeneticCode.generate(1);
		assertEquals('*', g.getAmino("TGA"));
	}
}
