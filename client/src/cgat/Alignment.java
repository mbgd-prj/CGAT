
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

///////////////////////////////////////////////////////////////////////////////
// １アライメント情報
//     From1-To1, From2-To2
//     Direction(-1, 0, +1)
//     Ident
//     Match/Length
//     Score
public class Alignment {
    public static final String TYPE_best1 = "1";
    public static final String TYPE_best2 = "2";
    public static final String TYPE_ortholog = "3";
    public static final byte DIR_INV = -1;
    public static final byte DIR_NON =  0;
    public static final byte DIR_DIR =  1;

    // Alignment データフィルタリング条件として設定可能な項目
    // ident, match length, score, type
    public static final int MaxAttrNum = 5;

    private boolean filter;
    private int     from1;
    private int     to1;
    private int     from2;
    private int     to2;
    private byte    dir;
    private float   ident;
    private int     match;
    private int     length;
    private float   score;
    private String  type;
    private String  alignSeq1;
    private String  alignSeq2;
    private AlignSeq  alignSeq = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment(int f1, int t1, int f2, int t2, byte d, float i, int m, int l, float s, String t) {
        _init(true, f1, t1, f2, t2, d, i, m, l, s, t, "", "");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment(Alignment align) {
        _init(align.getFilter(),
              align.getFrom1(),
              align.getTo1(),
              align.getFrom2(),
              align.getTo2(),
              align.getDir(),
              align.getIdent(),
              align.getMatch(),
              align.getLength(),
              align.getScore(),
              align.getType(),
              align.getAlignSeq1(),
              align.getAlignSeq2());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(boolean f, int f1, int t1, int f2, int t2, byte d, float i, int m, int l, float s, String t, String seq1, String seq2) {
        setFilter(f);
        setFrom1(f1);
        setTo1(t1);
        setFrom2(f2);
        setTo2(t2);
        setDir(d);
        setIdent(i);
        setMatch(m);
        setLength(l);
        setScore(s);
        setType(t);
        setAlignSeq1(seq1);
        setAlignSeq2(seq2);
    }
    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean equals(Alignment ali2) {
	if (ali2 == null) {
		return false;
	} else {
		return (from1 == ali2.from1 && to1 == ali2.to1 &&
			from2 == ali2.from2 && to2 == ali2.to2 &&
			dir == ali2.dir);
	}
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFilter(boolean f) {
        filter   = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrom1(int f) {
        from1   = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTo1(int t) {
        to1     = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrom2(int f) {
        from2   = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTo2(int t) {
        to2     = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDir(byte d) {
        dir   = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setIdent(float i) {
        ident   = i;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMatch(int m) {
        match   = m;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setLength(int l) {
        length  = l;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScore(float s) {
        score   = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setType(String t) {
        type   = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean haveAlignSeq() {
//        if (alignSeq1.equals("")) {
//            return false;
//        }

        if (alignSeq == null) {
            return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignSeq1(String s) {
        alignSeq1 = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignSeq2(String s) {
        alignSeq2 = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignSeq(AlignSeq as) {
        alignSeq = as;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getFilter() {
        return(filter);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getFrom1() {
        return(from1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getTo1() {
        return(to1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getFrom2() {
        return(from2);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getTo2() {
        return(to2);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public byte getDir() {
        return(dir);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getIdent() {
        return(ident);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getMatch() {
        return(match);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getLength() {
        return(length);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getScore() {
        return(score);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getType() {
        return type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isAlignSeq() {
        if (alignSeq1 != null) {
            return true;
        }
        else {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignSeq1() {
        return alignSeq1;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignSeq2() {
        return alignSeq2;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignSeq getAlignSeq() {
        return alignSeq;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 基準生物種の入れ替え
    //   以下の項目を入れ替える
    //     from1     <--> from2
    //     to1       <--> to2
    //     alignSeq1 <--> alignSeq2
    public void exchangeSpec() {
        int iWork;
        String sWork;

        // from1 <--> from2
        iWork = getFrom1();
        setFrom1(getFrom2());
        setFrom2(iWork);

        // to1 <--> to2
        iWork = getTo1();
        setTo1(getTo2());
        setTo2(iWork);

        // alignSeq1 <--> alignSeq2
        sWork = getAlignSeq1();
        setAlignSeq1(getAlignSeq2());
        setAlignSeq2(sWork);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isSameAlignment(Alignment align) {
        if (getFrom1() != align.getFrom1()) {
            return false;
        }
        if (getTo1() != align.getTo1()) {
            return false;
        }
        if (getFrom2() != align.getFrom2()) {
            return false;
        }
        if (getTo2() != align.getTo2()) {
            return false;
        }
        if (getDir() != align.getDir()) {
            return false;
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String toString() {
        return getFrom1() + " - " + getTo1() + " , " + getFrom2() + " - " + getTo2() + " :: " + getDir() + " [" + getType() + "]";
    }

}
