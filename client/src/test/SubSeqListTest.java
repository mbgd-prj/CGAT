package test;
import cgat.seq.*;
import junit.framework.*;
import java.io.*;

public class SubSeqListTest extends TestCase {
	public SubSeqListTest(String name) {
		super(name);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(SubSeqListTest.class);
	}
	public void testSubSeqList1() throws Exception {
		BufferedReader in = new BufferedReader(
				new FileReader("testtab") );
		FastaFile ff = new FastaFile("testgenome");
		ff.asDNA();
		DNASequence genome = (DNASequence) ff.readSeq();
		SubSequenceList slist = new SubSequenceList(genome);

		String buf;
		while ((buf = in.readLine()) != null) {
			String[] field = Utils.split(buf, "\t");
			int from, to, dir;
			from = Integer.valueOf(field[1]).intValue();
			to = Integer.valueOf(field[2]).intValue();
			dir = Integer.valueOf(field[3]).intValue();
			SeqRegion reg = new SeqRegion(from, to, dir);
			slist.add(reg);
		}
		Sequence subseq = slist.get(6);
		System.out.println(subseq.getSeqString());

		subseq = slist.get(500);
		System.out.println(subseq.getSeqString());

		SubSequenceList slist2 = slist.getOverlappingSubSequences(
				new SeqRegion(1000, 4000));
		assertEquals(3, slist2.size());
		SubSequence s = slist2.get(2);
		assertEquals(2801, slist2.get(1).getRegion().getFrom());
		assertEquals(3733, slist2.get(1).getRegion().getTo());
	}
}
