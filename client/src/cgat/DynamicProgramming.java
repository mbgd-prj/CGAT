package cgat;

import java.util.*;

public class DynamicProgramming {
    cgat.seq.DPAlign dp;
    cgat.seq.SequenceAlignment ali;
    int MATCH = 5, MISMATCH = -4, OPENGAP = -12, EXTGAP = -4, EDGEGAP = -12;

    ///////////////////////////////////////////////////////////////////////////
    public DynamicProgramming() {
		_dynamicProgramming(MATCH, MISMATCH, OPENGAP, EXTGAP, EDGEGAP);
    }
    ///////////////////////////////////////////////////////////////////////////
    public DynamicProgramming(int match, int mismatch, int opengap, int extgap, int edgegap) {
		_dynamicProgramming(match, mismatch, opengap, extgap, edgegap);
    }
    ///////////////////////////////////////////////////////////////////////////
    protected void _dynamicProgramming(int match, int mismatch, int opengap, int extgap, int edgegap) {
		if (dp == null) {
			cgat.seq.ScoreMat sMat = new cgat.seq.IdentityScoreMat(
				match, mismatch);
			sMat.setGaps(opengap, extgap, edgegap);
			dp = new cgat.seq.DPAlign();
			dp.setScoreMat(sMat);
		}
    }
    ///////////////////////////////////////////////////////////////////////////
    public String getAlignedSubject() {
        return(ali.getAlignedSeq(0));
    }
    ///////////////////////////////////////////////////////////////////////////
    public String getAlignedQuery() {
        return(ali.getAlignedSeq(1));
    }
    ///////////////////////////////////////////////////////////////////////////
    public void alignment(String seq1, String seq2) throws InterruptedException{
        ali = dp.align(seq1, seq2);
    }
}
