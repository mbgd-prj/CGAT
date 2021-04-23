
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// 描画領域の配列のアライメントを行う
// アライメント結果と画面上の位置との対応をつけることが出来るようにする
//     画面上でクリックした位置を Genome 上の位置に変換する必要がある
//     変換する際、GAP を考慮して位置を算出する必要がある
//public class AlignmentSequence extends Observable {
public class AlignmentSequence {
    public static final int SBJ = 0;
    public static final int QRY = 1;

    //
    private boolean dbgMode;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    //
    private int posCenter;             // 入力配列(Sbj)の中心位置
    private int seqlen[];              // 入力配列長(Sbj/Qry)

    // アライメント結果、Gap 情報
    private String alignedSeq[];       // Subject/Query 配列

    private SegmentPos segPos[];

    // 詳細表示の際に、ポジションと表示位置に関する情報を格納する
    // GAP の挿入により、Genome 位置が連続していると限らなくなったため
    // Genome 位置を基準に Gene 等を描画する際に必要となる
    // 例
    //     000 00  000   01111111  11 122 2222  22223  １０の位(Genome順)
    //     123 45  678   90123456  78 901 2345  67890    １の位(Genome順)
    //     TTT-GC--ATT---TGTTCTCC--TA-AAT-GGAT--TTCCA
    //     222 22  222   23333333  33 344 4444  44445  １０の位(位置:Genome上)
    //     123 45  678   90123456  78 901 2345  67890    １の位(位置:Genome上)
    //     000000000111111111122222222223333333333444  １０の位(位置:画面上)
    //     123456789012345678901234567890123456789012    １の位(位置:画面上)
    private int regPosOfs[][];
    private int regPosStart[];

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentSequence(MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        _init(dataMng, vWin);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(MbgdDataMng dataMng, ViewWindow vWin) {
        setDbgMode(false);

        mbgdDataMng = dataMng;
        viewWin     = vWin;

        //
        seqlen     = new int[2];
        alignedSeq = new String[2];
        regPosOfs   = new int[2][];
        regPosStart = new int[2];

/*
        setPosCenter(-1);
        setSeqLen(SBJ, -1);
        setSeqLen(QRY, -1);
*/

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDbgMode(boolean sta) {
        dbgMode = sta;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getDbgMode() {
        return(dbgMode);
    }

/*
    ///////////////////////////////////////////////////////////////////////////
    //
    public void setPosCenter(int pos) {
        posCenter = pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getPosCenter() {
        return(posCenter);
    }
*/

/*
    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSeqLen(int type, int len) {
        seqlen[type] = len;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSeqLen(int type) {
        return(seqlen[type]);
    }
*/

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignedSeq(int type, String seq) {
        if (getDbgMode()) {
            Dbg.println(3, "DBG :: aligned "+type+" : " + seq);
        }
        alignedSeq[type] = seq;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignedSeq(int type) throws InterruptedException {
        if (alignedSeq[type] == null) {
            alignment();
        }

        return(alignedSeq[type]);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Alignment の実行
    public void alignment() throws InterruptedException {
        double rateWider = 0.3;         //
        DynamicProgramming dp;
        int regCenter1, regWidth1, regMax1;
        boolean regDir1;
        int from1, to1;
        int regCenter2, regWidth2, regMax2;
        int alignWidth;
        boolean regDir2;
        int from2, to2;
        int padLen;
        String seq1;
        String seq2;

        // 描画範囲を取得(Base)
        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1  = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regMax1    = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        regDir1    = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);

        //
        padLen = (int)((double)regWidth1 * rateWider);
        if (padLen < 100) {
                padLen = 100;
        }
        alignWidth = (int)(regWidth1/2+ padLen);

        // アライメント対象配列の取得(Base)
        from1 = regCenter1 - alignWidth;
        if (from1 < 0) {
            from1 += regMax1;
        }
        to1 = regCenter1 + alignWidth;
        if (regMax1 < to1) {
            to1 -= regMax1;
        }
        seq1 = mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, from1, to1);
        if (! regDir1) {
            seq1 = cgat.seq.DNASequence.reverseComplement(seq1).toLowerCase();
        }

        // 描画範囲を取得(Oppo)
        regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
        regWidth2  = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        regMax2    = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        regDir2    = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);

        //
        padLen = (int)((double)regWidth2 * rateWider);

        // アライメント対象配列の取得(Oppo)
        from2 = regCenter2 - alignWidth;
        if (from2 < 0) {
            from2 += regMax2;
        }
        to2 = regCenter2 + alignWidth;
        if (regMax2 < to2) {
            to2 -= regMax2;
        }
        seq2 = mbgdDataMng.getGenomeSequence(MbgdDataMng.OPPO_SPEC, from2, to2);
        if (! regDir2) {
            seq2 = cgat.seq.DNASequence.reverseComplement(seq2).toLowerCase();
        }

        // DynamicProgramming パラメータ
        int match    = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_MATCH);
        int mismatch = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_MISMATCH);
        int opengap  = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_OPENGAP);
        int extgap   = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_EXTGAP);
        int edgegap  = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_EDGEGAP);

        // アライメント実行
        dp = new DynamicProgramming(match, mismatch, opengap, extgap, edgegap);
        dp.alignment(seq1, seq2);

        // 描画対象範囲のアライメント中央を検索
        String alignedSeqBase = dp.getAlignedSubject();
        String alignedSeqOppo = dp.getAlignedQuery();

        int n = 0;
        int idx;
        int loopMax = alignedSeqBase.length();
        for(idx = 0; idx < loopMax; idx++) {
            char cBase = alignedSeqBase.charAt(idx);
            if (cBase != '-') { // GAP ではない要素
                n++;
                if (n == alignWidth) {
                    // 表示対象の中央(Base)
                    break;
                }
            }
        }

        // Gap を考慮した、表示開始位置
        regPosStart[0] = from1;
        if (! regDir1) {
            regPosStart[0] = to1;
        }
        for(int i = 0; i < idx - regWidth1 / 2; i++) {
            char cBase = alignedSeqBase.charAt(i);
            if (cBase != '-') { // GAP ではない要素
                if (regDir1) {
                    regPosStart[0]++;
                }
                else {
                    regPosStart[0]--;
                }
            }
        }
        if (regDir2) {
            regPosStart[1] = from2;
        }
        else {
            regPosStart[1] = to2;
        }
        for(int i = 0; i < idx - regWidth2 / 2; i++) {
            char cOppo = alignedSeqOppo.charAt(i);
            if (cOppo != '-') { // GAP ではない要素
                if (regDir2) {
                    regPosStart[1]++;
                }
                else {
                    regPosStart[1]--;
                }
            }
        }

        // 描画対象のアライメント結果を抽出/格納
        alignedSeqBase = alignedSeqBase.substring(idx - regWidth1 / 2, idx + regWidth1 / 2);
        setAlignedSeq(SBJ, alignedSeqBase);
        alignedSeqOppo = alignedSeqOppo.substring(idx - regWidth2 / 2, idx + regWidth2 / 2);
        setAlignedSeq(QRY, alignedSeqOppo);

        //
        makeOfsTab();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseRegion getWinPos(int type, int f, int t) {
        BaseRegion reg;
        boolean dir;

        int newFrom = getGappedRegPosOfs(type, f);
        int newTo   = getGappedRegPosOfs(type, t);
        reg = new BaseRegion(newFrom, newTo);
        return(reg);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void makeOfsTab() throws InterruptedException {
        String seq;
        int regWidth = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int i;

        //
        String aSeq[] = new String[2];
        aSeq[SBJ] = getAlignedSeq(SBJ);
        aSeq[QRY] = getAlignedSeq(QRY);
        int seqLen = aSeq[0].length();

        //
        regPosOfs[0] = new int[seqLen];
        regPosOfs[1] = new int[seqLen];
        for(i = 0; i < seqLen; i++) {
            regPosOfs[0][i] = regWidth;
            regPosOfs[1][i] = regWidth;
        }

        //
        int lastOfs = regWidth;

        int side = 0;
        int n = 0;
        seq = aSeq[side];
        for(i = 0; i < seqLen; i++) {
            if (seq.charAt(i) != '-') {
                regPosOfs[side][n] = i;
                n++;
            }
        }

        //
        side++;
        n = 0;
        seq = aSeq[side];
        for(i = 0; i < seqLen; i++) {
            if (seq.charAt(i) != '-') {
                regPosOfs[side][n] = i;
                n++;
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegPosStart(int type) {
		return regPosStart[type];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getGappedRegPosOfs(int type, int pos) {
        int regCenter;
        int regWidth;
        boolean d;
        int idx;

        if (type == SBJ) {
            // 描画範囲を取得(Base)
            regCenter = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            regWidth  = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
            d         = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        }
        else {
            // 描画範囲を取得(Oppo)
            regCenter = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
            regWidth  = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
            d         = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        }

        idx = pos - regPosStart[type] - 1;
        if (! d) {
            idx = regPosStart[type] - pos;
        }

try {
        if (idx < 0) {
            // 画面の外にはみ出している場合
            return regPosOfs[type][0] - 1;
        }
        else if (regPosOfs[type].length <= idx) {
            // 画面の外にはみ出している場合
            return regPosOfs[type][regPosOfs[type].length - 1] + 1;
        }
}
catch (NullPointerException npe) {
        // 位置が計算されていない場合（SegBaseの場合など）
        return pos - regCenter - 1;
}

        return regPosOfs[type][idx];
    }

}
