package test;
import cgat.seq.*;
import junit.framework.*;

public class DNASequenceTest extends TestCase {
	public DNASequenceTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(DNASequenceTest.class);
	}
	public void testComplement() throws Exception {
		DNASequence dna = new DNASequence("name",
				"YACATRSWKNNG");
		Sequence rev = dna.getReverse();
		assertEquals("CNNMWSYATGTR", rev.getSeqString());
		assertEquals(12, rev.length());
		assertEquals('W', rev.charAt(4));
		assertEquals('M', rev.symbolAt(4));
		String revstr = DNASequence.reverseComplement("ACANRGTA");
		assertEquals("TACYNTGT", revstr);
	}
	public void testTranslate() throws Exception {
		DNASequence dna = new DNASequence("name",
			"ATGCTGATTCTTATTTCACCTGCGAAAACG");
		GeneticCode gtab = GeneticCode.generate(1);
		ProteinSequence aaseq = DNASequence.translate(dna, gtab);
		assertEquals("MLILISPAKT", aaseq.getSeqString());

		Sequence sub = new SubSequence(dna, 4, 9);
		aaseq = DNASequence.translate(sub);
		assertEquals("LI", aaseq.getSeqString());

		Sequence rev = sub.getReverse();
		aaseq = DNASequence.translate(rev);
		assertEquals("NQ", aaseq.getSeqString());
	}
	public void testLinearDNASeq() throws Exception {
		DNASequence dna = new DNASequence("name",
				"ACGTAacgtaAGATA");
		assertEquals("ACGTAacgtaAGATA", dna.getSeqString());
		assertEquals("GTAac",(new SubSequence(dna,3,7)).getSeqString());
		int flag = 0;
		try {
			Sequence sub = new SubSequence(dna, 8, 3);
			sub.getSeqString();	// out of bounds!!
		} catch (IndexOutOfBoundsException e) {
			flag = 1;
		}
		assertEquals(1, flag);
	}
	public void testCircularDNASeq() throws Exception {
		DNASequence dna = new DNASequence("name",
				"ACGTAacgtaAGATA", true);
		assertEquals("ACGTAacgtaAGATA", dna.getSeqString());
		assertEquals(15, dna.length());

//		assertEquals("GTAacgtaAGATA", dna.getSeqString()); //fail!!

		/* reverse sequence */
		assertEquals("TATCTTACGTTACGT", dna.getReverse().getSeqString());
		assertEquals("ACGTAacgtaAGATA", dna.getSeqString());
		/* subsequence extraction */
		Sequence sub = new SubSequence(dna, 3, 8);
		assertEquals("GTAacg", sub.getSeqString());

		/* subsequence of circular sequence */
		sub = new SubSequence(dna, 8, 3);
		assertEquals("gtaAGATAACG", sub.getSeqString());
		assertEquals(sub, sub.getReverse().getReverse());
		assertEquals("CGTTATCTTAC", sub.getReverse().getSeqString());
		assertEquals(11, sub.length());
		assertEquals(11, sub.getReverse().length());

		/* substring of sub=gtaAGATAACG */
		SubSequence subsub = new SubSequence(sub, 2, 6);
		assertEquals("taAGA", subsub.getSeqString());
		SubSequence subsubrev = new SubSequence(sub, 2, 5, -1);
		assertEquals("CTTA", subsubrev.getSeqString());
		assertEquals("taAG", subsubrev.getReverse().getSeqString());
	}
}
