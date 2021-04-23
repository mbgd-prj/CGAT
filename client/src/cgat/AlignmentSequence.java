
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// �����ΰ������Υ��饤���Ȥ�Ԥ�
// ���饤���ȷ�̤Ȳ��̾�ΰ��֤Ȥ��б���Ĥ��뤳�Ȥ������褦�ˤ���
//     ���̾�ǥ���å��������֤� Genome ��ΰ��֤��Ѵ�����ɬ�פ�����
//     �Ѵ�����ݡ�GAP ���θ���ư��֤򻻽Ф���ɬ�פ�����
//public class AlignmentSequence extends Observable {
public class AlignmentSequence {
    public static final int SBJ = 0;
    public static final int QRY = 1;

    //
    private boolean dbgMode;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    //
    private int posCenter;             // ��������(Sbj)���濴����
    private int seqlen[];              // ��������Ĺ(Sbj/Qry)

    // ���饤���ȷ�̡�Gap ����
    private String alignedSeq[];       // Subject/Query ����

    private SegmentPos segPos[];

    // �ܺ�ɽ���κݤˡ��ݥ�������ɽ�����֤˴ؤ��������Ǽ����
    // GAP �������ˤ�ꡢGenome ���֤�Ϣ³���Ƥ���ȸ¤�ʤ��ʤä�����
    // Genome ���֤���� Gene �������褹��ݤ�ɬ�פȤʤ�
    // ��
    //     000 00  000   01111111  11 122 2222  22223  �����ΰ�(Genome��)
    //     123 45  678   90123456  78 901 2345  67890    ���ΰ�(Genome��)
    //     TTT-GC--ATT---TGTTCTCC--TA-AAT-GGAT--TTCCA
    //     222 22  222   23333333  33 344 4444  44445  �����ΰ�(����:Genome��)
    //     123 45  678   90123456  78 901 2345  67890    ���ΰ�(����:Genome��)
    //     000000000111111111122222222223333333333444  �����ΰ�(����:���̾�)
    //     123456789012345678901234567890123456789012    ���ΰ�(����:���̾�)
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
    // Alignment �μ¹�
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

        // �����ϰϤ����(Base)
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

        // ���饤�����о�����μ���(Base)
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

        // �����ϰϤ����(Oppo)
        regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
        regWidth2  = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        regMax2    = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        regDir2    = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);

        //
        padLen = (int)((double)regWidth2 * rateWider);

        // ���饤�����о�����μ���(Oppo)
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

        // DynamicProgramming �ѥ�᡼��
        int match    = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_MATCH);
        int mismatch = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_MISMATCH);
        int opengap  = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_OPENGAP);
        int extgap   = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_EXTGAP);
        int edgegap  = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_EDGEGAP);

        // ���饤���ȼ¹�
        dp = new DynamicProgramming(match, mismatch, opengap, extgap, edgegap);
        dp.alignment(seq1, seq2);

        // �����о��ϰϤΥ��饤��������򸡺�
        String alignedSeqBase = dp.getAlignedSubject();
        String alignedSeqOppo = dp.getAlignedQuery();

        int n = 0;
        int idx;
        int loopMax = alignedSeqBase.length();
        for(idx = 0; idx < loopMax; idx++) {
            char cBase = alignedSeqBase.charAt(idx);
            if (cBase != '-') { // GAP �ǤϤʤ�����
                n++;
                if (n == alignWidth) {
                    // ɽ���оݤ����(Base)
                    break;
                }
            }
        }

        // Gap ���θ������ɽ�����ϰ���
        regPosStart[0] = from1;
        if (! regDir1) {
            regPosStart[0] = to1;
        }
        for(int i = 0; i < idx - regWidth1 / 2; i++) {
            char cBase = alignedSeqBase.charAt(i);
            if (cBase != '-') { // GAP �ǤϤʤ�����
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
            if (cOppo != '-') { // GAP �ǤϤʤ�����
                if (regDir2) {
                    regPosStart[1]++;
                }
                else {
                    regPosStart[1]--;
                }
            }
        }

        // �����оݤΥ��饤���ȷ�̤����/��Ǽ
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
            // �����ϰϤ����(Base)
            regCenter = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            regWidth  = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
            d         = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        }
        else {
            // �����ϰϤ����(Oppo)
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
            // ���̤γ��ˤϤ߽Ф��Ƥ�����
            return regPosOfs[type][0] - 1;
        }
        else if (regPosOfs[type].length <= idx) {
            // ���̤γ��ˤϤ߽Ф��Ƥ�����
            return regPosOfs[type][regPosOfs[type].length - 1] + 1;
        }
}
catch (NullPointerException npe) {
        // ���֤��׻�����Ƥ��ʤ�����SegBase�ξ��ʤɡ�
        return pos - regCenter - 1;
}

        return regPosOfs[type][idx];
    }

}
