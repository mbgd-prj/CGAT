package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// AlignmentList ���顢������˥ޥå����� Alignment ����Ф���
public class AlignmentListFilter {

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentListFilter() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����ʪ����濴ɽ���� Alignment-data ��õ��
    // ������б����� spec2 �� Alignment-data �򸫤Ĥ���
    // sp2 �� alignment �ǡ������濴��ɽ������ݤ� sp2 ��ɽ��������֤�׻�����
    // Alignment �ǡ��������Ĥ���ʤ��ä���硢sp2 ��ɽ���ΰ�ϡ����� lr �ˤ���Ѳ�����
    public static int serarchPosOppositeSpec(MbgdDataMng dataMng, ViewWindow viewWin, boolean lr) {
//	Alignment ali = searchAlignOppositeSpec(dataMng, viewWin, null);
	Alignment ali = searchAlignOppositeSpec(dataMng, viewWin, null, true);
	return getPosFromAlign(ali, dataMng, viewWin, lr);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Alignment searchAlignOppositeSpecBack(MbgdDataMng dataMng, ViewWindow viewWin, ArrayList currentAlignmentArrayList) {
        int regCenter1, regCenter2;
        int regStart1, regWidth1, regMax1;
        int regMax2;
        Alignment align;
        int i;

        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart1 = regCenter1 - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

        // Region �����ϰ���Ρ������ʪ��� Alignment �ǡ���������
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1);

        int loopMax = alignList.length;
        Alignment alignCenter = null;         // �濴�Ȥ��� Alignment
        double minDiffOrth = Double.MAX_VALUE;  // Ortholog �ǡ����θ���
        Alignment alignCenterOrth = null;
        double minDiffBest = Double.MAX_VALUE;  // Best Hit �ǡ����θ���
        Alignment alignCenterBest = null;
        for(i = 0; i < loopMax; i++) {
            align = alignList[i];
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                continue;
            }

            // ɽ���ϰ���� Alignment �ǡ���������å�
            Iterator it = currentAlignmentArrayList.iterator();
            while(it.hasNext()) {
                Alignment currentAlignment = (Alignment)it.next();
                if (align.equals(currentAlignment)) {
                    // Always choose the current alignment when available
                    alignCenter = align;
                    break;
                }
            }
            if (alignCenter != null) {
                break;
            }

            // ɽ�����֤��濴
            double cPos1, cPos2;
            cPos1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            cPos2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);

            // �оݤ� align �Ȥε�Υ
            double diffFrom1 = Math.abs(align.getFrom1() - cPos1);
            double diffTo1   = Math.abs(align.getTo1()   - cPos1);
            double diffFrom2 = Math.abs(align.getFrom2() - cPos2);
            double diffTo2   = Math.abs(align.getTo2()   - cPos2);
            double diff1 = diffTo1;
            if (diffFrom1 < diffTo1) {
                diff1 = diffFrom1;
            }
            double diff2 = diffTo2;
            if (diffFrom2 < diffTo2) {
                diff2 = diffFrom2;
            }
            
            double diff = diff1 * diff1 + diff2 * diff2;
            if (align.getType().equalsIgnoreCase(Alignment.TYPE_ortholog)) {
                if (diff < minDiffOrth) {
                    // ����濴�˶ᤤ Alignment �ǡ���
                    minDiffOrth = diff;
                    alignCenterOrth = align;
                }
            }
            else {
                if (diff < minDiffBest) {
                    // ����濴�˶ᤤ Alignment �ǡ���
                    minDiffBest = diff;
                    alignCenterBest = align;
                }
            }

        }

        if (alignCenter == null) {
            if (alignCenterOrth != null) {
                // �Ǥ�ᤤ Ortholog �����
                alignCenter = alignCenterOrth;
            }
            else if (alignCenterBest != null) {
                // �Ǥ�ᤤ Best hit �����
                alignCenter = alignCenterBest;
            }
        }

        return alignCenter;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Alignment searchAlignOppositeSpec(MbgdDataMng dataMng, ViewWindow viewWin) {
        int regCenter1, regCenter2;
        int regStart1, regWidth1, regMax1;
        int regMax2;
        Alignment align;
        int i;

        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart1 = regCenter1 - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

        // Region �����ϰ���Ρ������ʪ��� Alignment �ǡ���������
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1);

        int loopMax = alignList.length;
        Alignment alignCenter = null;         // �濴�Ȥ��� Alignment
        double minDiffOrth = Double.MAX_VALUE;  // Ortholog �ǡ����θ���
        Alignment alignCenterOrth = null;
        double minDiffBest = Double.MAX_VALUE;  // Best Hit �ǡ����θ���
        Alignment alignCenterBest = null;
        for(i = 0; i < loopMax; i++) {
            align = alignList[i];
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                continue;
            }

            // ɽ�����֤��濴
            double cPos1, cPos2;
            cPos1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            cPos2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);

            // �оݤ� align �Ȥε�Υ
            double diffFrom1 = Math.abs(align.getFrom1() - cPos1);
            double diffTo1   = Math.abs(align.getTo1()   - cPos1);
            double diffFrom2 = Math.abs(align.getFrom2() - cPos2);
            double diffTo2   = Math.abs(align.getTo2()   - cPos2);
            double diff1 = diffTo1;
            if (diffFrom1 < diffTo1) {
                diff1 = diffFrom1;
            }
            double diff2 = diffTo2;
            if (diffFrom2 < diffTo2) {
                diff2 = diffFrom2;
            }
            
            double diff = diff1 * diff1 + diff2 * diff2;
            if (align.getType().equalsIgnoreCase(Alignment.TYPE_ortholog)) {
                if ((align.getFrom1() <= cPos1) && (cPos1 <= align.getTo1())) {
                    alignCenter = align;
                    break;
                }
                if (diff < minDiffOrth) {
                    // ����濴�˶ᤤ Alignment �ǡ���
                    minDiffOrth = diff;
                    alignCenterOrth = align;
                }
            }
            else {
                if (diff < minDiffBest) {
                    // ����濴�˶ᤤ Alignment �ǡ���
                    minDiffBest = diff;
                    alignCenterBest = align;
                }
            }

        }

        if (alignCenter == null) {
            if (alignCenterOrth != null) {
                // �Ǥ�ᤤ Ortholog �����
                alignCenter = alignCenterOrth;
            }
            else if (alignCenterBest != null) {
                // �Ǥ�ᤤ Best hit �����
                alignCenter = alignCenterBest;
            }
        }

        return alignCenter;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Alignment searchAlignOppositeSpec(MbgdDataMng dataMng,
                                                    ViewWindow viewWin,
                                                    Alignment prevAlign,
                                                    boolean lr) {
        Alignment alignCenter = null;
        int regCenter1, regCenter2;
        int regStart1, regWidth1, regMax1;
        int regStart2, regWidth2, regMax2;
        Alignment align;
        int i;

        // �����ʪ�︡���ϰ�
        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regWidth1 *= 0.05;
        if (regWidth1 < 30) {
            regWidth1 = 30;
        }
        regStart1 = regCenter1 - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        if (regStart1 < 0) {
            regStart1 += regMax1;
        }

        //
        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
        regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int moveSp2 = regWidth2 / 2;
        if (prevAlign != null) {
            double len1 = prevAlign.getTo1() - prevAlign.getFrom1();
            double len2 = prevAlign.getTo2() - prevAlign.getFrom2();
            moveSp2 = (int)((double)moveSp2 * len2 / len1);
        }
        if (lr) {
            regCenter2 += moveSp2;
            regCenter2 %= regMax2;
        }
        else {
            regCenter2 -= moveSp2;
            if (regCenter2 < 0) {
                regCenter2 += regMax2;
            }
        }

        int w = regWidth2 / 2;
        regStart2 = regCenter2 - w;
        if (regStart2 < 0) {
            regStart2 += regMax2;
        }

        // Region �����ϰ���Ρ������ʪ��� Alignment �ǡ���������
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1,
                                                        regStart2, w * 2, regMax2);


        double minDist = 999999;
        double maxAlignScore = 0;
        double minAlignDist = 99999;
        int loopMax = alignList.length;
        for(i = 0; i < loopMax; i++) {
            align = alignList[i];
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                continue;
            }

            if (prevAlign != null) {
                if (prevAlign.isSameAlignment(align)) {
                    return align;
                }
                if (prevAlign.getDir() != align.getDir()) {
                    // �������㤦
                    continue;
                }

                if (lr) {
                    // ���˰�ư�������
                    if ((w < Math.abs(prevAlign.getTo1() - align.getFrom1())) ||
                        (w < Math.abs(prevAlign.getTo2() - align.getFrom2()))) {
                        // Υ��Ƥ��� alignment �Ǥ���
                        continue;
                    }
                }
                else {
                    // ���˰�ư�������
                    if ((w < Math.abs(prevAlign.getFrom1() - align.getTo1())) ||
                        (w < Math.abs(prevAlign.getFrom2() - align.getTo2()))) {
                        // Υ��Ƥ��� alignment �Ǥ���
                        continue;
                    }
                }

                // align �������濴(��)�Ȥε�Υ
                double m = (align.getTo2() - align.getFrom2()) / (align.getTo1() - align.getFrom1());
                double n = align.getFrom1() - m * align.getFrom2();
                double dist = Math.abs(regCenter2 - m * regCenter1 - n) / Math.sqrt(1 + m * m);
                if (maxAlignScore < align.getScore()) {
                    alignCenter = align; 
                    maxAlignScore = align.getScore();
                    minAlignDist = dist;
                }
                else if (maxAlignScore == align.getScore()) {
                    if (dist < minAlignDist) {
                        alignCenter = align; 
                        minAlignDist = dist;
                    }
                }
            }
        }

        if (alignCenter == null) {
            // ���ܥ��饤����̵��
            alignList = dataMng.selectAlignList(regStart1, regWidth1, regMax1);
            Alignment alignOrtholog = null;
            double maxOrthologScore = 0;
            double minOrthologDist = 99999;
            Alignment alignNotOrtholog = null;
            double maxNotOrthologScore = 0;
            double minNotOrthologDist = 99999;
            loopMax = alignList.length;
            for(i = 0; i < loopMax; i++) {
                align = alignList[i];
                if (! align.getFilter()) {
                    // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                    continue;
                }

                double m = (align.getTo2() - align.getFrom2()) / (align.getTo1() - align.getFrom1());
                double n = align.getFrom1() - m * align.getFrom2();
                double dist = Math.abs(regCenter2 - m * regCenter1 - n) / Math.sqrt(1 + m * m);
                if (align.getType().equals(Alignment.TYPE_ortholog)) {
                    if (maxOrthologScore < align.getScore()) {
                        alignOrtholog = align;
                        maxOrthologScore = align.getScore();
                        minOrthologDist = dist;
                    }
                    else if (maxOrthologScore == align.getScore()) {
                        if (dist < minOrthologDist) {
                            alignOrtholog = align;
                            minOrthologDist = dist;
                        }
                    }
                }
                else {
                    if (maxNotOrthologScore < align.getScore()) {
                        alignNotOrtholog = align;
                        maxNotOrthologScore = align.getScore();
                        minNotOrthologDist = dist;
                    }
                    else if (maxNotOrthologScore == align.getScore()) {
                        if (dist < minNotOrthologDist) {
                            alignNotOrtholog = align;
                            minNotOrthologDist = dist;
                        }
                    }
                }
            }
            if (alignOrtholog != null) {
                alignCenter = alignOrtholog;
            }
            else if (alignNotOrtholog != null) {
                alignCenter = alignNotOrtholog;
            }
            else {
//Dbg.println(1, "not found");
            }
        }


        // ɽ�������濴��Ƚ�Ǥ��줿 alignment
        return alignCenter;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static int getPosFromAlign(Alignment alignCenter, MbgdDataMng dataMng, ViewWindow viewWin, boolean lr) {
        int regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        int regCenter2;
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        if (alignCenter == null) {
            // �������� Alignment �����Ĥ���ʤ��ä�
            // ���ߤ������������Ȥˡ�������(WIDTH/2)������ư����
            boolean dir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
            regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
            if (dir2) {
                if (lr) {
                    regCenter2 += regWidth1 / 2;
                }
                else {
                    regCenter2 -= regWidth1 / 2;
                }
            }
            else {
                if (lr) {
                    regCenter2 += regWidth1 / 2;
                }
                else {
                    regCenter2 -= regWidth1 / 2;
                }
            }
            return regCenter2;
        }

        //
        int from1 = alignCenter.getFrom1();
        int to1   = alignCenter.getTo1();
        int from2 = alignCenter.getFrom2();
        int to2   = alignCenter.getTo2();
        byte dir  = alignCenter.getDir();

        //
        // Genome ����θ���
        boolean regDir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        if (dir == Alignment.DIR_DIR) {
            regCenter2 = (from2 + to2) / 2 + (regCenter1 - (from1 + to1) / 2);
            double r = (double)(regCenter1 - from1) / (double)(to1 - from1);
            regCenter2 = from2 + (int)((to2 - from2) * r);
            if (regDir1) {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, true);
            }
            else {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, false);
            }
        }
        else {
            regCenter2 = (from2 + to2) / 2 - (regCenter1 - (from1 + to1) / 2);
            double r = (double)(regCenter1 - from1) / (double)(to1 - from1);
            regCenter2 = to2 - (int)((to2 - from2) * r);
            if (regDir1) {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, false);
            }
            else {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, true);
            }
        }

        if (regCenter2 < 0) {
            regCenter2 += regMax2;
        }
        else if (regMax2 <= regCenter2) {
            regCenter2 %= regMax2;
        }

        return regCenter2;
    }

    ///////////////////////////////////////////////////////////////////////////
    // spec�� ���濴ɽ���� Alignment-data ����ꤷ
    // ������б����� spec2 �� Alignment-data ��õ��
    // sp2 �� alignment �ǡ������濴��ɽ������ݤ� sp2 �γ��ϰ��֤�׻�����
    public static Alignment serarchSp2Pos(MbgdDataMng dataMng, ViewWindow viewWin) {
        int regStart1, regWidth1, regMax1;
        int regMax2;
        boolean sp1Dir, sp2Dir;

        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        sp1Dir  = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);

        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        sp2Dir  = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);

        // ɽ���о��ΰ���ˤ��� Alignment �ǡ��������򤹤�
//        ArrayList dispAlignList = selectDispAlignList(dataMng, viewWin, regStart1, regWidth1, regMax1);
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1);

        // �����濴��ɽ������ǡ�����Ƚ��
        int centerPos = regStart1 + regWidth1 / 2;
        centerPos %= regMax1;
        if (centerPos < 0) {
            centerPos += regMax1;
        }

        Alignment centerObj = null;     //
        int minDist = 9999999;          //
        int dist;
        int from1, to1;
        int from2, to2;
        int dir;

        int loopMax = alignList.length;
        for(int i = 0; i < loopMax; i++) {
            Alignment align = alignList[i];
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                continue;
            }

            String type  = align.getType();
            if (type.equals(Alignment.TYPE_ortholog) != true) {
                // orthologue �ʳ��Υǡ����ϡ�Skip
                continue;
            }

            // MbgdDataMng ����ǡ�����������������ǡ�from1/to1 �ϴ����ʪ��ξ���
            from1 = align.getFrom1();
            to1   = align.getTo1();

            // ɽ���濴�Ȥε�Υ�����
            if (centerPos < from1) {
                dist = centerPos + regMax1 - (from1 + to1) / 2;
            }
            else if (to1 < centerPos) {
                dist = regMax1 - centerPos + (from1 + to1) / 2;
            }
            else {
                dist = Math.abs(centerPos - ((from1 + to1) / 2 + regMax1));
            }

            if (dist < minDist) {
                // ����濴�˶ᤤ�ǡ�����ȯ��
                minDist = dist;
                centerObj = align;
            }
        }

        return(centerObj);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Ϳ����줿 range ���¸�ߤ��� alignment �ΰ������������
    public static ArrayList search(MbgdDataMng dataMng, ViewWindow viewWin,
                                    boolean basespec, int from, int to) {
        ArrayList dispAlignList = new ArrayList();

        // ����
        int loopMax = dataMng.getAlignmentSize();
        for(int i = 0; i < loopMax; i++) {
            Alignment align = dataMng.getAlignment(basespec, i);
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                continue;
            }

            int f, t;

            f = align.getFrom1();
            t = align.getTo1();

            if (t < f) {
                int w = f;
                f = t;
                t = w;
            }

            // �ΰ褬�Ťʤ뤫��Ƚ��
            if ((from <= f) && (t <= to)) {
                dispAlignList.add(align);
            }
            else if ((f <= from) && (from   <= t)) {
                dispAlignList.add(align);
            }
        }

        return(dispAlignList);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Ϳ����줿 range ���¸�ߤ��� alignment �ΰ������������
    // ���������оݤȤʤ�ǡ������̤�����ǽ�Ȥ���
    public static ArrayList search(MbgdDataMng dataMng, ViewWindow viewWin,
                                    boolean basespec, int from, int to,
                                    String type) {
        ArrayList dispAlignList = new ArrayList();

        // ����
        int loopMax = dataMng.getAlignmentSize();
        for(int i = 0; i < loopMax; i++) {
            Alignment align = dataMng.getAlignment(basespec, i);
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> �оݳ�
                continue;
            }

            int f, t;

            if (! align.getType().equalsIgnoreCase(type)) {
                // ���ꤵ�줿�ǡ������̤ǤϤʤ�
                continue;
            }

            f = align.getFrom1();
            t = align.getTo1();

            if (t < f) {
                int w = f;
                f = t;
                t = w;
            }

            // �ΰ褬�Ťʤ뤫��Ƚ��
            if ((from <= f) && (f <= to)) {
                dispAlignList.add(align);
            }
            else if ((from <= t) && (t <= to)) {
                dispAlignList.add(align);
            }
            else if ((f <= from) && (from   <= t)) {
                dispAlignList.add(align);
            }
        }

        return(dispAlignList);
    }

}

