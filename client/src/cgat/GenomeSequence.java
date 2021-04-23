
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;
import java.io.*;

///////////////////////////////////////////////////////////////////////////////
//
//public class GenomeSequence extends CgatSequence {
public class GenomeSequence {
    private String dataFilename;
    private boolean flagDataLoad = false;
    cgat.seq.DNASequence _seq;

    ///////////////////////////////////////////////////////////////////////////
    //
    public GenomeSequence(String name, String seq) {
//        super(nam, seq);
	_seq = new cgat.seq.DNASequence(name, seq, true);

        // データファイル名を保持
        setDataFilename("");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String nam) {
        dataFilename = nam;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFlagDataLoad(boolean f) {
        flagDataLoad = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getFlagDataLoad() {
        return(flagDataLoad);
    }
    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
        String fname = "";

        if (dataFilename.equals(filename)) {
            // 前回読み込んだデータと同じ ---> 再度読み込む必要がない
//System.err.println("already loaded : "+specname);
            setFlagDataLoad(false);
            return(true);
        }

        // データファイル名を保持
        setDataFilename(filename);
	cgat.seq.FastaFile ff = null;
	try  {
		ff = new cgat.seq.FastaFile(filename);
		ff.asDNA();
		// do not consider " " as a name separator
		ff.setTitSepChar("\t\n");
		_seq.setSequence(ff.readSeq());
	} catch (IOException e) {
		return false;
	}

	return true;
    }
    public int getLength() {
	return(_seq.length());
    }
    public String getName() {
	return(_seq.getName());
    }
    public String getSequence() {
	return(_seq.getSeqString());
    }
    public String getSequence(int from, int to) {
        cgat.seq.SubSequence sub = new cgat.seq.SubSequence(_seq, from+1, to+1);
        return sub.getSeqString();
    }
    static public String getReverseComplement(String oldSeq) {
        cgat.seq.DNASequence seq = new cgat.seq.DNASequence("rev", oldSeq);
        String retseq =  seq.getReverse().getSeqString();
        return retseq.toLowerCase();
    }

}
