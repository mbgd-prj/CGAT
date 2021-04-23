package test;
import cgat.seq.*;
import junit.framework.*;

public class ProteinSequenceTest extends TestCase {
	public ProteinSequenceTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(ProteinSequenceTest.class);
	}
	public void testProteinSeq() throws Exception {
		ProteinSequence aa = new ProteinSequence("name",
				"MGDVEKGKKIFIMKCSQCHTV");

		Sequence sub = new SubSequence(aa, 5, 10);
		assertEquals("EKGKKI", sub.getSeqString());
		Sequence rev = sub.getReverse();
		assertEquals("GluLysGlyLysLysIle", sub.getAltNameSequence());
		assertEquals("IKKGKE", rev.getSeqString());
System.out.println(rev.getSeqString());
		assertEquals(6, rev.length());
		assertEquals('K', rev.charAt(4));
		assertEquals('G', rev.symbolAt(4));
/*
		assertEquals("IleLysLysGlyLysGlu", rev.get3LetterSeq());
*/
	}
}
