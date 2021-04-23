package test;
import cgat.seq.*;
import junit.framework.*;
import java.io.*;

public class DPAlignTest extends TestCase {
	public DPAlignTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(DPAlignTest.class);
	}
	public void testDP1() throws Exception {
		DPAlign dp = new DPAlign();
		ScoreMat smat = new IdentityScoreMat(5, -4);
		smat.setGaps(-12, -2, -10);
		dp.setScoreMat(smat);

		Sequence seq1 = new RawSequence("name1", "CCA");
		dp.setSequences(seq1, seq1);
		int maxScore = dp.calcMaxScore();
		assertEquals(15,maxScore);

		Sequence seq2 = new RawSequence("name2", "CGA");
		dp.setSequences(seq1, seq2);
		maxScore = dp.calcMaxScore();
		assertEquals(6,maxScore);

		Sequence seq3 = new RawSequence("name3", "CGCA");
		SequenceAlignment ali = dp.align(seq1, seq3);
		assertEquals("C-CA",ali.getAlignedSeq(0));
	}
	public void testDP2() throws Exception {
		DPAlign dp = new DPAlign();
		ScoreMat smat = new ScoreMat("blosum62");
		smat.setGaps(-12, -2, -10);
		dp.setScoreMat(smat);

		FastaFile ff;
		String filename = "testseq";

		StringBuffer origout = new StringBuffer();
		StringBuffer testout = new StringBuffer();

		// delete the last new line
		BufferedReader in = new BufferedReader(
					new FileReader("testout"));
		int c;
		while ((c = in.read()) != -1) {
			origout.append((char) c);
		}
		in.close();

		try {
			ff = new FastaFile(filename);
		} catch (IOException e) {
			System.err.println("Can't open seqfile");
			throw e;
		}
		try {
			RawSequence seq1, seq2;
			SequenceAlignment ali;
			seq1 = ff.readSeq();
			while( (seq2 = ff.readSeq()) != null) {
				ali = dp.align(seq1, seq2);
				testout.append( ali.toString() );
			}
		} catch (IOException e) {
			System.err.println("Can't read seqfile");
			throw e;
		}
		assertEquals(origout.toString(),testout.toString());
	}
}
