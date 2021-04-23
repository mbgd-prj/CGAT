
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// �������ȥ�٥�Ǥ�ɽ���򥵥ݡ��Ȥ���
//
// �����ϰ���� Alignment �����򤹤�
// �����ʪ���������֤򻻽Ф���
// �����ʪ��/¾������ʪ��� From-To/Dir ���Ǽ����
//
// �����ξ�����Ȥ� Alignment �����褹��
// �����ξ�����Ȥ� Gene/Segment �ϡ������оݤ� GeneSeg �򸡺�����
//
public class AlignmentSegment extends Observable {
    public static final int SBJ = 0;
    public static final int QRY = 1;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    private ArrayList       segPosList;        // �����ϰ���� Alignment

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentSegment(MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        mbgdDataMng = dataMng;
        viewWin     = vWin;

        segPosList = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        segPosList.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ArrayList getAlignSegment() {
        return segPosList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSize() {
      return segPosList.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void alignment() {
        int from, to;
        int regStart;
        int regCenter;
        int regWidth;
        int regMax;

        // ���ߤ����� Window
        regCenter = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart = regCenter - regWidth / 2;
        regMax   = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);

        // ���ߤ������ϰ�
        from = regCenter - regWidth / 2;
        if (from < 0) {
            from += regMax;
        }
        to   = regCenter + regWidth / 2;
        if (regMax < to) {
            to -= regMax;
        }

        //
        int zoomCount = viewWin.getZoomCount();

        // �����ϰϤ� Alignment �����
        ArrayList alignList = selectAlignmentList(from, to, regMax);
        segPosList.clear();
        for(int i = 0; i < alignList.size(); i++) {
            Alignment align = (Alignment)alignList.get(i);

            SegmentPos segPos = new SegmentPos();

            // �����оݤ� Alignment
            segPos.setRegionPos(align.getFrom1(), align.getTo1(),
                                align.getFrom2(), align.getTo2(),
                                align.getDir(), align);

            // ������֡ʲ��̡ˡ�window width �������ʤΤǳ��
            double x1, x2;
            int f1 = align.getFrom1();
            int t1 = align.getTo1();
            int f2 = align.getFrom2();
            int t2 = align.getTo2();

            if (regStart + regWidth < f1) {
                // 0bp �򶴤���ǡ���
                x1 = ((double)f1 - (double)regStart - (double)regMax)     / (double)regWidth;
                x2 = ((double)t1 - (double)regStart - (double)regMax + 1) / (double)regWidth;
            }
            else {
                x1 = ((double)f1 - (double)regStart)     / (double)regWidth;
                x2 = ((double)t1 - (double)regStart + 1) / (double)regWidth;
            }
            segPos.setScreenPos(x1, x2);

            segPosList.add(segPos);


            // �����٥�� Alignment ��̤�ɬ�פǤ��뤫��
            if (ViewWindow.DRAWMODE_LEV3 < zoomCount) {
                // �̾�⡼�ɤǤ�ɽ�� ---> ��������ɤ��ʤ�
            }
            else {
                // �����٥�� Alignment ��̤� download ����
                mbgdDataMng.loadAlignmentSeq(f1, t1, f2, t2);

                // ������������
                String seq1 = mbgdDataMng.getAlignmentSeq(f1, t1, f2, t2, MbgdDataMng.BASE_SPEC);
                String seq2 = mbgdDataMng.getAlignmentSeq(f1, t1, f2, t2, MbgdDataMng.OPPO_SPEC);

                // �����ϰϤ���ʬ����
                // Gap ���ޤޤ���礬�ͤ�����
                //
                int idxSt, idxEd;
                if (from < to) {        // �����ΰ褬Ϣ³���Ƥ�����
                    if (f1 < from) {    // ��������椫�������о�
                        idxSt = from - f1;
                        if (to < t1) {  // ���������ޤ������о�
                            idxEd = to - f1 + 1;
                        }
                        else {          // ����ν����ޤǤ������о�
                            idxEd = t1 - f1 + 1;
                        }
                    }
                    else {              // �������Ƭ���������о�
                        idxSt = 0;
                        if (to < t1) {
                            idxEd = to - f1 + 1;
                        }
                        else {
                            idxEd = t1 - f1 + 1;
                        }
                    }
                }
                else {                  // �����ΰ褬ʬ�Ǥ��Ƥ������0bp �򶴤��
                    if (from < t1) {
                        if (f1 < from) {
                            idxSt = from - f1;
                            if (regMax < t1) {
                                idxEd = regMax - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                        else {
                            idxSt = 0;
                            if (regMax < t1) {
                                idxEd = regMax - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                    }
                    else {
                        if (f1 < 0) {
                            idxSt = 0 - f1;
                            if (to < t1) {
                                idxEd = t1 - to - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                        else {
                            idxSt = 0;
                            if (to < t1) {
                                idxEd = t1 - to - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                    }
                }

                // �����оݤ���ʬ����
                String subseq1, subseq2;
                try {
                    subseq1 = seq1.substring(idxSt, idxEd);
                    subseq2 = seq2.substring(idxSt, idxEd);
                }
                catch (Exception e) {
                    subseq1 = "";
                    subseq2 = "";
                }
                segPos.setAlignSeq1(subseq1);
                segPos.setAlignSeq2(subseq2);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // �����оݤȤʤ� Alignment �����
    public ArrayList selectAlignmentList(int from, int to, int regMax) {
        // �����о��ϰ���� Alignment �ǡ����μ���
        ArrayList alignList0;
        ArrayList alignList1;

        //
        String spnameInit = mbgdDataMng.getSpecNameInit(MbgdDataMng.SIDE0);
        String spnameBase = mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        String best;
        if (spnameInit.equals(spnameBase)) {
            // �����ʪ��ϡ�download ���Τޤ�
            best = Alignment.TYPE_best1;
        }
        else {
            // �����ʪ�郎�����ؤ���줿����
            best = Alignment.TYPE_best2;
        }

        if (from < to) {
            alignList0 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, to,
                                                    Alignment.TYPE_ortholog);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, to,
                                                    best);
            alignList0.addAll(alignList1);
        }
        else {
            // 0 bp �򶴤���
            alignList0 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, regMax,
                                                    Alignment.TYPE_ortholog);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, regMax,
                                                    best);
            alignList0.addAll(alignList1);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    0, to,
                                                    Alignment.TYPE_ortholog);
            alignList0.addAll(alignList1);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    0, to,
                                                    best);
            alignList0.addAll(alignList1);
        }

        return alignList0;
    }

}
