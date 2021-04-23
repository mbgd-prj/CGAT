package test;

import junit.framework.*;

/**
 * TestSuite that runs all the sample tests
 *
 */
public class AllTests {

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	public static Test suite ( ) {
		TestSuite suite = new TestSuite(DNASequenceTest.class);
		suite.addTest(new TestSuite(ProteinSequenceTest.class));
		suite.addTest(new TestSuite(DPAlignTest.class));
		suite.addTest(new TestSuite(AlphabetTest.class));
		suite.addTest(new TestSuite(FastaFileTest.class));
		suite.addTest(new TestSuite(SubSeqListTest.class));
		suite.addTest(new TestSuite(GeneticCodeTest.class));
	    return suite;
	}
}
