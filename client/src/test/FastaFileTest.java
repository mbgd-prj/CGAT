package test;
import cgat.seq.*;
import junit.framework.*;
import java.io.*;

public class FastaFileTest extends TestCase {
	public FastaFileTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(FastaFileTest.class);
	}
	public void testFastaFile1() throws Exception {
		FastaFile ff = null;
		Sequence seq = null;
		int cnt = 0;
		ff = new FastaFile("testseq");
		while ( (seq = ff.readSeq()) != null ) {
			cnt++;
		}
		assertEquals(5, cnt);
		ff.close();
	}
	public void testFastaFile2() throws Exception {
		FastaFile ff = null;
		ff = new FastaFile(
			// access to MBGD server
			"http://mbgd.genome.ad.jp/htbin/getGeneSequence.pl?" +
				"sp=eco&orf=B0002&type=protein&format=fasta"
		);
		Sequence seq = ff.readSeq();
		assertEquals(820, seq.length());
		ff.close();
	}
}
