package test;
import cgat.seq.*;
import junit.framework.*;

public class AlphabetTest extends TestCase {
	public AlphabetTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(AlphabetTest.class);
	}
	public void testAlpha() throws Exception {
		Alphabet a = new Alphabet("ABCDEFGH");
		assertEquals(8, a.charnum());
		assertEquals('C', a.get(2));
		assertEquals(4, a.toIdx('E'));
		assertEquals(false, a.contains('K'));
		assertEquals(true, a.contains('G'));
		assertEquals('H', a.complement('H'));
		assertEquals('H', a.complement('h'));
	}
	public void testAlpha2() throws Exception {
		String[] opt = {"complement=10"};
		Alphabet a = new Alphabet("01", opt);
		assertEquals('1', a.get(1));
		assertEquals('0', a.complement('1'));
	}
	public void testAminoAcids() throws Exception {
		Alphabet a = Alphabet.getAminoAcids();
		assertEquals('A', a.get(a.toIdx('A')));
		assertEquals(false, a.contains('O'));
		assertEquals('G', a.complement('G'));
	}
	public void testNucleotides() throws Exception {
		Alphabet a = Alphabet.getNucleotides();
		assertEquals(true, a.contains('K'));
		assertEquals(false, a.contains('P'));
		assertEquals('C', a.complement('g'));
	}
}
