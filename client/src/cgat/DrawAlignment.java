
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import cgat.seq.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawAlignment extends BaseDraw implements MouseListener {
    final char  CHAR_SAMEBASE   = '|';
    protected double PadHeightAlignmentRate = 0.25;

    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow  viewWin;

    protected PrintTextWindow alignDataWin;

    protected Color colMat = Color.black;
    protected Color colMis = Color.red;
    protected Color colGap = Color.green;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignment(MbgdDataMng dataMng, ViewWindow vWin) {
        super();
        _init(dataMng, vWin, HEIGHT, WIDTH);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignment(MbgdDataMng dataMng, ViewWindow vWin, int w, int h) {
        super();
        _init(dataMng, vWin, w, h);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(MbgdDataMng dataMng, ViewWindow vWin, int w, int h) {
        mbgdDataMng = dataMng;
        viewWin = vWin;

        setWindowWidth(w);
        setWindowHeight(h);

        Dimension d = new Dimension(w, h);
        setSize(d);
        setMinimumSize(d);
        setPreferredSize(d);        // �侩������������

        try {
            addMouseListener(this);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint(Graphics g) {
	winWidth = getWidth();
	winHeight = getHeight();
        updBgColor();

        super.paint(g);

        drawAlignment(g);
        drawFrame(g);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawFrame(Graphics g) {
        g.setColor(getFgColor());
        g.drawRect(0, 0, winWidth-1, winHeight-1);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawAlignment(boolean autoAdjust) {
        Graphics g = getGraphics();
        if (g != null) {
            super.paint(g);
            drawFrame(g);
            drawAlignment(g);
        }
        else {
            Dbg.println(1, "getGraphics() returns null");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // �濴ɽ���ǡ����θ�����ưĴ����ɽ������
    public void drawAlignment(Graphics g) {
        if (mbgdDataMng.getAlignmentSize() == 0) {
            return;
        }

        int mode = viewWin.getDrawMode();

        if (mode == ViewWindowRegion.MODE_SEQUENCE) {
            // �����٥�Υ��饤����
            drawAlignmentSequence(g);
        }
        else if (mode == ViewWindowRegion.MODE_SEGMENT) {
            // �������ȥ�٥�Υ��饤����
            drawAlignmentSegment(g);
        }
        else {
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // �濴ɽ���ǡ����θ�����ưĴ����ɽ������
    public void drawAlignmentSequence(Graphics g) {
	float ratio = seqLenPerPixel(viewWin);

        int zoomCount = viewWin.getZoomCount();

        if (ratio > 1) {
            // �̾�⡼�ɤǤ�ɽ��
            drawAlignmentSequenceZoomLev0(g);
        }
        else if (ratio > 0.25) {
            // �����ץ�٥��ɽ��
            drawAlignmentSequenceZoomLev1(g);
        }
        else {
            // �����٥��ɽ��
            drawAlignmentSequenceZoomLev2(g);
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // �����٥�Υ��饤���Ȥ�Ȥä�ɽ�����������Ƥ� CHAR_SAMEBASE ɽ����
    public void drawAlignmentSequenceZoomLev2(Graphics g) {
        drawAlignmentSequenceZoomLev1(g);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �����٥�Υ��饤���Ȥ�Ȥä�ɽ�����������Ƥ� '.' ɽ����
    public void drawAlignmentSequenceZoomLev1(Graphics g) {
        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();

        int xArrowDir[] = {0, -5, -5};
        int yArrowDir[] = {0, -5,  5};
        int xArrowInv[] = {0,  5,  5};
        int yArrowInv[] = {0, -5,  5};
        int i;
        int j;

        // Alignment ��̤˴ؤ������
        int virtualWinWidthSbj = 1;     // Alignment ��̤β��۲�����
        int virtualWinWidthQry = 1;     // Alignment ��̤β��۲�����
        virtualWinWidthSbj = viewWin.getAlignSequence(MbgdDataMng.BASE_SPEC).length();
        virtualWinWidthQry = viewWin.getAlignSequence(MbgdDataMng.OPPO_SPEC).length();

        // ɽ���о��ΰ�
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        boolean regDir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        boolean regDir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }

        // ɽ���оݥǡ��������
        Alignment alignList[] = mbgdDataMng.selectAlignList(regStart1, regWidth1, regMax1,
                                                            regStart2, regWidth2, regMax2);

        //
        AlignmentSequence alignSeq = viewWin.getAlignSequence();

        // �������Ƥ�ɽ��
        String alignSeqPair[];
        g.setColor(Color.black);
        alignSeqPair = drawSequence(g);
        if (alignSeqPair == null) {
            return;
        }

        // ����
        int x1;                         // spec1��x��ɸ��
        int y1;                         // spec1��y��ɸ��
        int w1;                         // spec1������
        int xArrow1[] = new int[3];     // spec1 �������x��ɸ��
        int yArrow1[] = new int[3];     // spec1 �������y��ɸ��

        int x2;                         // spec2��x��ɸ��
        int y2;                         // spec2��y��ɸ��
        int w2;                         // spec2������
        int xArrow2[] = new int[3];     // spec2 �������x��ɸ��
        int yArrow2[] = new int[3];     // spec2 �������y��ɸ��

        int myType;

        virtualWinWidthSbj = regWidth1;
        virtualWinWidthQry = regWidth2;

        int loopMax = alignList.length;
        for(i = 0; i < loopMax; i++) {
            Alignment align = alignList[i];
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> ɽ�����ʤ�
                continue;
            }

            int from1 = align.getFrom1();
            int to1   = align.getTo1();
            int from2 = align.getFrom2();
            int to2   = align.getTo2();
            int dir   = align.getDir();
            double from, to;

            String type  = align.getType();

            // �ľ����Υ�ɽ�����б�
            if (regMax1 < (regStart1 + regWidth1)) {
                if (to1 < regStart1) {
                    from1 += regMax1;
                    to1   += regMax1;
                }
            }
            if (regMax2 < (regStart2 + regWidth2)) {
                if (to2 < regStart2) {
                    from2 += regMax2;
                    to2   += regMax2;
                }
            }

            // spec1 ��������֤�׻�(pos ---> dot)
            // �����о��ϰ�����������Ƥ��Ȥ�������֤򻻽Ф���
            int f1, t1;

            //
            from1 = alignSeq.getGappedRegPosOfs(AlignmentSequence.SBJ, from1);
            to1   = alignSeq.getGappedRegPosOfs(AlignmentSequence.SBJ, to1) + 1;

            x1 = (int)((float)from1         / (float)regWidth1 * (float)winWidth);
            y1 = (int)((double)winHeight * PadHeightAlignmentRate);
            w1 = (int)((float)(to1 - from1) / (float)regWidth1 * (float)winWidth);
            if (w1 < 0) {
                x1 = x1 + w1;
                w1 = - w1;
            }

            // spec1 �����������֤�׻�
            if (regDir1) {
                j = 0;
                xArrow1[j] = x1+w1+xArrowDir[j]; yArrow1[j] = y1+yArrowDir[j]; j++;
                xArrow1[j] = x1+w1+xArrowDir[j]; yArrow1[j] = y1+yArrowDir[j]; j++;
                xArrow1[j] = x1+w1+xArrowDir[j]; yArrow1[j] = y1+yArrowDir[j]; j++;
            }
            else { // spec1 ��ո���ɽ��������
                j = 0;
                xArrow1[j] = x1+xArrowInv[j]; yArrow1[j] = y1+yArrowInv[j]; j++;
                xArrow1[j] = x1+xArrowInv[j]; yArrow1[j] = y1+yArrowInv[j]; j++;
                xArrow1[j] = x1+xArrowInv[j]; yArrow1[j] = y1+yArrowInv[j]; j++;
            }

            // ������֤�׻�(spec2)
            int f2, t2;

            //
            if (regDir2) {
                from2 = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, from2);
                to2   = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, to2) + 1;
            }
            else {
int oldFrom2 =
                from2 = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, from2- 1) + 1;
int oldTo2 =
                to2   = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, to2 - 1);
            }
            if (to2 < from2) { //
                int wk = from2;
                from2 = to2;
                to2 = wk;
            }

            x2 = (int)((float)from2         / (float)regWidth2 * (float)winWidth);
            y2 = (int)((double)winHeight * (1 - PadHeightAlignmentRate));
            w2 = (int)((float)(to2 - from2) / (float)regWidth2 * (float)winWidth);
            if (w2 < 0) {
                x2 = x2 + w2;
                w2 = -w2;
            }

            // spec2 �����������֤�׻�
            j = 0;
            if (dir == 1) {     // sp1 ��Ʊ�������λ�
                if (regDir2) {
                    xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                    xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                    xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                }
                else {
                    xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                    xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                    xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                }
            }
            else {
                if (regDir2) {
                    xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                    xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                    xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                }
                else {
                    xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                    xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                    xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                }
            }

            // ɽ����
            Color color = mbgdDataMng.getAlignColor(MbgdDataMng.BASE_ALIGN, type, ColorTab.TYPE_INT, align);
            g.setColor(color);

            g.drawLine(x1, y1, x1 + w1, y1); // spec1 ����
            g.drawLine(x2, y2, x2 + w2, y2); // spec2 ����

            int zoomCount = viewWin.getZoomCount();
//            if (zoomCount <= ViewWindow.DRAWMODE_LEV5) {
//                boolean staArrowHead1 = true;
//                boolean staArrowHead2 = true;
//
//                // ���ܤ��� alignment �����å�
//                Alignment lAlign = getNextAlignL(alignList, i, loopMax);
//                Alignment rAlign = getNextAlignR(alignList, i, loopMax);
//                if (regDir1) {
//                    staArrowHead1 = isDrawAlignmentSequenceArrowHead(align,
//                                                                    rAlign,
//                                                                    lAlign,
//                                                                    true);
//                    staArrowHead2 = isDrawAlignmentSequenceArrowHead(align,
//                                                                    rAlign,
//                                                                    lAlign,
//                                                                    false);
//                }
//
//                if (staArrowHead1) { // ���������
//                    g.fillPolygon(xArrow1, yArrow1, 3);
//                }
//                if (staArrowHead2) { // ���������
//                    g.fillPolygon(xArrow2, yArrow2, 3);
//                }
//            }

            // spec1 �� spec2 �Ȥ��б���������
            if (dir == 1) {     // alignment Ʊ������
                if (regDir1 == regDir2) {
                    g.drawLine(x1, y1, x2, y2);
                    g.drawLine(x1 + w1, y1, x2 + w2, y2);
                }
                else {
                    g.drawLine(x1, y1, x2 + w2, y2);
                    g.drawLine(x1 + w1, y1, x2, y2);
                }
            }
            else {
                if (regDir1 == regDir2) {
                    g.drawLine(x1, y1, x2 + w2, y2);
                    g.drawLine(x1 + w1, y1, x2, y2);
                }
                else {
                    g.drawLine(x1, y1, x2, y2);
                    g.drawLine(x1 + w1, y1, x2 + w2, y2);
                }
            }
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected Alignment getNextAlignL(Alignment alignList[], int i, int loopMax) {
        Alignment align = alignList[i];
        Alignment a = null;
        for(--i; 0 <= i; i--) {
            if (! alignList[i].getFilter()) {
                continue;
            }
            if (align.getType().equals(alignList[i].getType())) {
                a = alignList[i];
                break;
            }
        }

        return a;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected Alignment getNextAlignR(Alignment alignList[], int i, int loopMax) {
        Alignment align = alignList[i];
        Alignment a = null;
        for(++i; i < loopMax; i++) {
            if (! alignList[i].getFilter()) {
                continue;
            }
            if (align.getType().equals(alignList[i].getType())) {
                a = alignList[i];
                break;
            }
        }

        return a;
    }


    ///////////////////////////////////////////////////////////////////////////
    // �̾�⡼�ɤ�ɽ��
    public void drawAlignmentSequenceZoomLev0(Graphics g) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        boolean regDir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        boolean regDir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();
        if (regStart1 < 0) {
            regStart1 += regMax1;
        }
        if (regStart2 < 0) {
            regStart2 += regMax2;
        }

        int xArrowDir[] = {0, -5, -5};
        int yArrowDir[] = {0, -5,  5};
        int xArrowInv[] = {0,  5,  5};
        int yArrowInv[] = {0, -5,  5};
        int i;
        int j;

        // ɽ���оݥǡ��������
        Alignment alignList[] = mbgdDataMng.selectAlignList(regStart1, regWidth1, regMax1,
                                                            regStart2, regWidth2, regMax2);
        // �������Ƥ�ɽ��
        String alignSeqPair[];
        g.setColor(Color.black);
        alignSeqPair =  drawSequence(g);
        if (alignSeqPair == null) {
            return;
        }

        // ����
        int x1;                         // spec1��x��ɸ��
        int y1;                         // spec1��y��ɸ��
        int w1;                         // spec1������
        int xArrow1[] = new int[3];     // spec1 �������x��ɸ��
        int yArrow1[] = new int[3];     // spec1 �������y��ɸ��

        int x2;                         // spec2��x��ɸ��
        int y2;                         // spec2��y��ɸ��
        int w2;                         // spec2������
        int xArrow2[] = new int[3];     // spec2 �������x��ɸ��
        int yArrow2[] = new int[3];     // spec2 �������y��ɸ��

        int loopMax = alignList.length;
        Dbg.println(3, "n alignment :: " + loopMax);
        for(i = 0; i < loopMax; i++) {
            Alignment align = alignList[i];
            if (! align.getFilter()) {
                // �ե��륿��󥰤��줿 alignment �ǡ��� ----> ɽ�����ʤ�
                continue;
            }

            int from1 = align.getFrom1();
            int to1   = align.getTo1() + 1;
            int from2 = align.getFrom2();
            int to2   = align.getTo2() + 1;
            int dir   = align.getDir();
            String type  = align.getType();

            // ����ɽ����٥�Ǥϡ�������Τ��������ʤ� ---> ���֤�Ĵ���ϹԤ�ʤ�

            // ������֤�׻�(spec1)
            x1 = getWinPosX(from1, regStart1, regWidth1, regMax1, winWidth);
            y1 = (int)((double)winHeight * PadHeightAlignmentRate);
            w1 = (int)((float)(to1 - from1) / (float)regWidth1 * (float)winWidth);

            // spec1 �����������֤�׻�
            j = 0;
            xArrow1[j] = x1+w1+xArrowDir[j]; yArrow1[j] = y1+yArrowDir[j]; j++;
            xArrow1[j] = x1+w1+xArrowDir[j]; yArrow1[j] = y1+yArrowDir[j]; j++;
            xArrow1[j] = x1+w1+xArrowDir[j]; yArrow1[j] = y1+yArrowDir[j]; j++;

            // ������֤�׻�(spec2)
            x2 = getWinPosX(from2, regStart2, regWidth2, regMax2, winWidth);
            y2 = (int)((double)winHeight * (1 - PadHeightAlignmentRate));
            w2 = (int)((float)(to2   - from2)     / (float)regWidth2 * (float)winWidth);

            // spec2 �����������֤�׻�
            if (dir == 1) {     // sp1 ��Ʊ�������λ�
                j = 0;
                xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
                xArrow2[j] = x2+w2+xArrowDir[j]; yArrow2[j] = y2+yArrowDir[j]; j++;
            }
            else {
                j = 0;
                xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
                xArrow2[j] = x2+xArrowInv[j]; yArrow2[j] = y2+yArrowInv[j]; j++;
            }

            // ɽ����������
            if (! regDir1) { // spec1 ��ո���ɽ��������
                x1 = revDispPos(x1, w1, winWidth);
                j = 0;
                xArrow1[j] = revDispPos(xArrow1[j], 0, winWidth); j++;
                xArrow1[j] = revDispPos(xArrow1[j], 0, winWidth); j++;
                xArrow1[j] = revDispPos(xArrow1[j], 0, winWidth); j++;
            }
            if (! regDir2) { // spec2 ��ո���ɽ��������
                x2 = revDispPos(x2, w2, winWidth);
                j = 0;
                xArrow2[j] = revDispPos(xArrow2[j], 0, winWidth); j++;
                xArrow2[j] = revDispPos(xArrow2[j], 0, winWidth); j++;
                xArrow2[j] = revDispPos(xArrow2[j], 0, winWidth); j++;
            }

            // ɽ����
            Color color = mbgdDataMng.getAlignColor(MbgdDataMng.BASE_ALIGN, type, ColorTab.TYPE_INT, align);
//	    if (color.equals(Color.white)) {
//		color = defaultFgColor;	// black when print mode 
//	    }
            g.setColor(color);

            g.drawLine(x1, y1, x1 + w1, y1); // spec1 ����
            g.drawLine(x2, y2, x2 + w2, y2); // spec2 ����

            int zoomCount = viewWin.getZoomCount();
//            if (zoomCount <= ViewWindow.DRAWMODE_LEV5) {
//                boolean staArrowHead1 = true;
//                boolean staArrowHead2 = true;
//
//                // ���ܤ��� alignment �����å�
//                Alignment lAlign = getNextAlignL(alignList, i, loopMax);
//                Alignment rAlign = getNextAlignR(alignList, i, loopMax);
//                if (regDir1) {
//                    staArrowHead1 = isDrawAlignmentSequenceArrowHead(align,
//                                                                     rAlign,
//                                                                     lAlign,
//                                                                     true);
//                    staArrowHead2 = isDrawAlignmentSequenceArrowHead(align,
//                                                                     rAlign,
//                                                                     lAlign,
//                                                                     false);
//                }
//
//                if (staArrowHead1) { // ���������
//                    g.fillPolygon(xArrow1, yArrow1, 3);
//                }
//                if (staArrowHead2) { // ���������
//                    g.fillPolygon(xArrow2, yArrow2, 3);
//                }
//            }

            // spec1 �� spec2 �Ȥ��б���������
            if (dir == 1) {     // alignment Ʊ������
                if (regDir1 == regDir2) {
                    g.drawLine(x1, y1, x2, y2);
                    g.drawLine(x1 + w1, y1, x2 + w2, y2);
                }
                else {
                    g.drawLine(x1, y1, x2 + w2, y2);
                    g.drawLine(x1 + w1, y1, x2, y2);
                }
            }
            else {
                if (regDir1 == regDir2) {
                    g.drawLine(x1, y1, x2 + w2, y2);
                    g.drawLine(x1 + w1, y1, x2, y2);
                }
                else {
                    g.drawLine(x1, y1, x2, y2);
                    g.drawLine(x1 + w1, y1, x2 + w2, y2);
                }
            }
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isDrawAlignmentSequenceArrowHead(Alignment cAlign,
                                                    Alignment rAlign,
                                                    Alignment lAlign,
                                                    boolean headSp1) {
        boolean drawArrowhead1 = true;
        boolean drawArrowhead2 = true;

        //
        int minAlignGap  = mbgdDataMng.getMinAlignGaps();
        int d;

        if (cAlign.getDir() == 1) {
            // sp1/sp2 ��Ʊ������
            if ((rAlign != null) && (rAlign.getDir() == 1)) {
                // rAlign �� sp1/sp2 ��Ʊ������
                if ((cAlign.getTo1() < rAlign.getFrom1()) &&
                    (cAlign.getTo2() < rAlign.getFrom2())) {
                    // rAlign �� sp1/sp2 �Ȥ� cAlign �α��˰��֤��Ƥ���

                    d = rAlign.getFrom1() - cAlign.getTo1();
                    if (d <= minAlignGap) {
                        // ���ܤ��Ƥ���
                        drawArrowhead1 = false;
                    }

                    d = rAlign.getFrom2() - cAlign.getTo2();
                    if (d <= minAlignGap) {
                        // ���ܤ��Ƥ���
                        drawArrowhead2 = false;
                    }
                }
            }
        }
        else {
            // sp1/sp2 ���ո���
            if ((rAlign != null) && (rAlign.getDir() != 1)) {
                // rAlign �� sp1 ��Ʊ������

                if ((cAlign.getTo1() < rAlign.getFrom1()) &&
                    (rAlign.getTo2() < cAlign.getFrom2())) {
                    // rAlign �� sp1/sp2 �Ȥ� cAlign �α��˰��֤��Ƥ���
                    d = rAlign.getFrom1() - cAlign.getTo1();
                    if (d <= minAlignGap) {
                        // ���ܤ��Ƥ���
                        drawArrowhead1 = false;
                    }

                    d = cAlign.getFrom2() - rAlign.getTo2();
                    if (d <= minAlignGap) {
                        // ���ܤ��Ƥ���
                        drawArrowhead2 = false;
                    }
                }
            }

            if ((lAlign != null) && (lAlign.getDir() != 1)) {
                if ((lAlign.getTo1() < cAlign.getFrom1()) &&
                    (lAlign.getTo2() < cAlign.getFrom2())) {
                    // lAlign �� sp1/sp2 �Ȥ� lAlign �κ��˰��֤��Ƥ���
                    // lAlign �� sp2 ��Ʊ������
                    d = cAlign.getFrom2() - lAlign.getTo2();
                    if (d <= minAlignGap) {
                        // ���ܤ��Ƥ���
                        drawArrowhead2 = false;
                    }
                }
            }
        }

if (headSp1) {
    return drawArrowhead1;
}
else {
    return drawArrowhead2;
}
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawAlignmentSegment(Graphics g) {
        // ɽ���оݤΥǡ���
        AlignmentSegment alignSeg = viewWin.getAlignSegment();
        if (alignSeg == null) {
            drawAlignmentSequence(g);
            return;
        }
        boolean regDir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);

        // ���迧
        updateRawElmColor();

        //
        ArrayList segPosList = alignSeg.getAlignSegment();

        // ���饤���Ȥ���������
        double regFrom[], regTo[];
        regFrom = new double[2];
        regTo   = new double[2];
        double regCenter = (double)viewWin.getRegCenter(MbgdDataMng.BASE_ALIGN);
        double regWidth = (double)viewWin.getRegWidth(MbgdDataMng.BASE_ALIGN);
        double regStart = regCenter - regWidth / 2;

        // �������ɽ����
        double dx = (double)winWidth / regWidth;

        //
        int zoomCount = viewWin.getZoomCount();

        for(int i = 0; i < segPosList.size(); i++) {
            //
            SegmentPos segPos = (SegmentPos)segPosList.get(i);
            int x1 = (int)(segPos.getScreenFrom() * (double)winWidth);
            int x2 = (int)(segPos.getScreenTo()   * (double)winWidth);
            int y1 = (int)((double)winHeight * PadHeightAlignmentRate);
            int y2 = (int)((double)winHeight * (1 - PadHeightAlignmentRate));
            Alignment align = segPos.getAlignment();
            String type = align.getType();

            // Alignment ����������
            Color color = mbgdDataMng.getAlignColor(MbgdDataMng.BASE_ALIGN, type, ColorTab.TYPE_INT, align);
            g.setColor(color);
            if (regDir1) {
                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x1, y2, x2, y2);
                g.setColor(Color.orange);
                g.drawLine(x1, 0, x1, winHeight);
                g.drawLine(x2, 0, x2, winHeight);
            }
            else {
                // �����ʪ��θ��������դȤʤäƤ���
                g.drawLine(winWidth - x1, y1, winWidth - x2, y1);
                g.drawLine(winWidth - x1, y2, winWidth - x2, y2);
                g.setColor(Color.orange);
                g.drawLine(winWidth - x1, 0, winWidth - x1, winHeight);
                g.drawLine(winWidth - x2, 0, winWidth - x2, winHeight);
            }


            // �������Ƥ�ɽ��
            String seq1 = segPos.getAlignSeq1();
            String seq2 = segPos.getAlignSeq2();
            if (! regDir1) {
                // ���亿
                cgat.seq.DNASequence seq;
                seq = new cgat.seq.DNASequence("rev", seq1);
                seq1 =  seq.getReverse().getSeqString().toUpperCase();
                seq = new cgat.seq.DNASequence("rev", seq2);
                seq2 =  seq.getReverse().getSeqString().toUpperCase();

                // ���褹��ݡ������դ��������ᡢreverse() ����
                StringBuffer sb;
                sb = new StringBuffer(seq1);
                seq1 = sb.reverse().toString();
                sb = new StringBuffer(seq2);
                seq2 = sb.reverse().toString();
            }

            if (x1 < 0) {
                x1 = 0;
            }
            for(int idx = 0; idx < seq1.length(); idx++) {
                int xx, yy;
                char c1, c2;

                c1 = seq1.charAt(idx);
                c2 = seq2.charAt(idx);

                if ((c1 == '-') || (c2 == '-')) {
                    g.setColor(colGap);
                }
                else if (c1 == c2) {
                    g.setColor(colMat);
                }
                else {
                    g.setColor(colMis);
                }

				float ratio = seqLenPerPixel(viewWin);
				if (ratio > 1) {
                    continue;
                }
				else if (ratio > 0.25) {
                    // �������Ƥ� CHAR_SAMEBASE �����褹��
                    c1 = c2 = CHAR_SAMEBASE;
                }
                else {
                }

                //
                xx = x1 + (int)((double)idx * dx);
                if (regDir1) {
                    yy = fh;
                    drawChar(g, xx, yy, (int)dx, c1);
                    yy = getWindowHeight() - 4;
                    drawChar(g, xx, yy, (int)dx, c2);
                }
                else {
                    yy = fh;
                    drawChar(g, winWidth - xx - (int)dx, yy, (int)dx, c1);
                    yy = getWindowHeight() - 4;
                    drawChar(g, winWidth - xx - (int)dx, yy, (int)dx, c2);
                }
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // �������Ƥ��֤�
    public String []  drawRawSequence(Graphics g, int level) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }
	char c1, c2;
        int i;

        // ���迧
        updateRawElmColor();

        // ��������Ƥ�����
        g.setColor(Color.black);

        // ���褹���ΰ�������������
        String seq1 = viewWin.getAlignSequence(MbgdDataMng.BASE_SPEC);
        String seq2 = viewWin.getAlignSequence(MbgdDataMng.OPPO_SPEC);

        float dx = (float)winWidth / (float)regWidth1;       // �������ɽ����
        for(i = 0; i < regWidth1; i++) {
            c1 = Character.toUpperCase(seq1.charAt(i));
            c2 = Character.toUpperCase(seq2.charAt(i));

            if ((c1 == '-') || (c2 == '-')) {
              // gap
              g.setColor(colGap);
            }
            else if (c1 == c2) {
              // mismatch
              g.setColor(colMat);
            }
            else {
              // match
              g.setColor(colMis);
            }

            int dd = 0;
            int xx = (int)((float)i * dx);
            int yy = fh;

	    if (level == 1) {
		if (c1 == '-') {
			c1 = '.';
		} else {
			c1 = CHAR_SAMEBASE;
		}
		if (c2 == '-') {
			c2 = '.';
		} else {
			c2 = CHAR_SAMEBASE;
		}
	    }
            drawChar(g, xx, fh,         (int)dx, c1);
            drawChar(g, xx, getWindowHeight() - 4, (int)dx, c2);
        }

        //
        String alignSeqPair[];
        alignSeqPair = new String[2];
        alignSeqPair[0] = seq1;
        alignSeqPair[1] = seq2;

        return alignSeqPair;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // �������Ƥ��֤�
    public String [] drawNoSequence(Graphics g) {
        String alignSeqPair[];

        alignSeqPair = new String[2];
        alignSeqPair[0] = "";
        alignSeqPair[1] = "";

        return alignSeqPair;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String [] drawSequence(Graphics g) {
        // ��������Ƥ�����
        // Gene ��̵���Ȥ��������
        int zoomCount = viewWin.getZoomCount();
	float ratio = seqLenPerPixel(viewWin);

	if (ratio > 1.0) {
            return drawNoSequence(g);
	} else if (ratio > 0.25) {
            // �������Ƥ� CHAR_SAMEBASE �����褹��
            return drawRawSequence(g, 1);
        } else {
            return drawRawSequence(g, 2);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawSegmentElmZoomLev2(Graphics g, SegmentPos[] segSeqPos) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }
        String seqImg[];
        float dx = (float)winWidth / (float)regWidth1;       // �������ɽ����
    }

    ///////////////////////////////////////////////////////////////////////////
    // �������ȤΥ��饤���ȥǡ�����ɽ��
    // �������ȴ֤Ϻƥ��饤���Ȥ�����������ɽ��
    //      �ʤ����������ȴ֤ϡ���Υ�ˤ�äƤ� GAP ɽ�����뤳�Ȥ�����
    public void drawSegmentElmZoomLev1(Graphics g, SegmentPos[] segSeqPos) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }
        String seqImg[];
        float dx = (float)winWidth / (float)regWidth1;       // �������ɽ����
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawSegmentElmZoomLev0(Graphics g, SegmentPos[] segSeqPos) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // �������Ƥ����褹��
    public void drawElmSeq(Graphics g, String seq1, String seq2, double dx, int y1, int y2, boolean flagSkipSame) {
        Color c;
        char c1, c2;

        // ���迧
        updateRawElmColor();

        //
        int loopMax = seq1.length();
        for(int i = 0; i < loopMax; i++) {
            c1 = seq1.charAt(i);
            try {
                c2 = seq2.charAt(i);
            }
            catch (IndexOutOfBoundsException e) {
                // seq2 �� seq1 ��ꡢû�����
                c2 = '-';
            }

            if ((c1 == '-') || (c2 == '-')) {
                c = colGap;
            }
            else if (c1 == c2) {
                if (flagSkipSame) {
                    continue;
                }
                c = colMat;
            }
            else {
                c = colMis;
            }

            if (flagSkipSame) {
                continue;
            }

            g.setColor(c);
            drawChar(g, (int)((double)i * dx), y1, (int)dx, c1);
            drawChar(g, (int)((double)i * dx), y2, (int)dx, c2);
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // �������Ƥ����褹��
    public void drawElmSeq(Graphics g, String seq1, String seq2, double dx, int y1, int y2) {
        drawElmSeq(g, seq1, seq2, dx, y1, y2, false);
        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // �������ƤΤ��������Ƥ��ۤʤ��Τ�ɽ������
    public void drawElmDif(Graphics g, String seq1, String seq2, double dx, int y1, int y2) {
        drawElmSeq(g, seq1, seq2, dx, y1, y2, true);
        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawSegmentElmZoom(Graphics g) {
        // ɽ���оݤΥǡ���
        AlignmentSegment alignSeg = viewWin.getAlignSegment();
        if (alignSeg == null) {
            drawAlignmentSequence(g);
            return;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int searchCenterAlignData(ArrayList dispAlignList) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        int idxCenter = -1;
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }

        // �����濴��ɽ������ǡ�����Ƚ��
        int centerPos = regStart1 + regWidth1 / 2;
        int minDist = 9999999;          //
        int dist = 0;
        int loopMax = dispAlignList.size();
        for(int i = 0; i < loopMax; i++) {
            int idx = ((Integer)dispAlignList.get(i)).intValue();
            Alignment align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, idx);

            String type  = align.getType();
            if (type.equals("3") != true) {
                // orthologue �ʳ��Υǡ����ϡ�Skip
                continue;
            }

            int from1 = align.getFrom1();
            int to1   = align.getTo1();

            // ɽ���濴�Ȥε�Υ�����
            if (to1 < centerPos) {
                dist = Math.abs(centerPos - ((from1 + to1) / 2 + regMax1));
            }
            else {
                dist = Math.abs(centerPos - (from1 + to1) / 2);
            }
            if (dist < minDist) {
                // ��ꡢ�濴�˶ᤤ�ǡ�����ȯ��
                minDist = dist;
                idxCenter = idx;
            }
        }

        return(idxCenter);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
        displayClickedAlignment(e);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // ����å����줿 Alignment �ڥ���õ���Ф������Υڥ����濴��ɽ������
    public void displayClickedAlignment(MouseEvent e) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        int minDistPairIdx;
        int from, to;
        int from1, to1;
        int from2, to2;
        int dir;
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }

        // ɽ�� window ������
        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();

        // ����å�����
        int x = e.getX();
        int y = e.getY();

        // ����å����֤˺Ǥ�ᤤ alignment �ǡ�����õ��
        minDistPairIdx = getClickedAlignment(x, y);
        if (minDistPairIdx < 0) {
            // �ǡ��������Ĥ���ʤ��ä�
            Dbg.println(1, "DBG :: not found clicked align");
            return;
        }

        // ���� alignment �ǡ������濴��ɽ��
        Alignment align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, minDistPairIdx);
        from1 = align.getFrom1();
        to1   = align.getTo1();
        from2 = align.getFrom2();
        to2   = align.getTo2();
        dir   = align.getDir();

        // ����å����֤��濴�����褹��
        int sp1Reg = (from1 + to1) / 2;
        if (sp1Reg <= 0) {
            sp1Reg += regMax1;
        }

        int sp2Reg = (from2 + to2) / 2;
        if (sp2Reg <= 0) {
            sp2Reg += regMax2;
        }

        // Alignment �ǡ����θ������碌��ɽ������
        viewWin.setRegDir(MbgdDataMng.BASE_SPEC, true);
        if (dir == 1) {
            viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, true);
        }
        else {
            viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, false);
        }

        viewWin.viewPos(sp1Reg, sp2Reg);

    }

    ///////////////////////////////////////////////////////////////////////////
    // ����å����줿 alignment �����ꤹ��
    protected int getClickedAlignment(int x, int y) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        boolean regDir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        boolean regDir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        int dispStart = -1;
        int dispEnd   = -2;
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }

        double minDist = Double.MAX_VALUE;
        double dist;
        int minDistPairIdx = -1;
        Alignment align;
        int from,  to;
        int from1, to1;
        int from2, to2;
        int fromX, toX, X, Y;

        // sp1 �� alignment �򥯥�å���
        // sp2 �� alignment �򥯥�å���
        int loopMax = mbgdDataMng.getAlignmentSize();
        for(int i = 0; i < loopMax; i++) {
            align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, i);
            if (! align.getFilter()) {
                continue;
            }

            from1 = align.getFrom1();
            to1   = align.getTo1();
            from2 = align.getFrom2();
            to2   = align.getTo2();

            if (regStart1 + regWidth1 < regMax1) {
                if ((to1 < regStart1) || (regStart1 + regWidth1 < from1)) {
                    // out of display-range
                    continue;
                }
            }
            else {
                if (((regStart1 + regWidth1) % regMax1 < from1) && (to1 < regStart1)) {
                    // out of display-range
                    continue;
                }
            }

            if (regStart2 + regWidth2 < regMax2) {
                if ((to2 < regStart2) || (regStart2 + regWidth2 < from2)) {
                    // out of display-range
                    continue;
                }
            }
            else {
                if (((regStart2 + regWidth2) % regMax2 < from2) && (to2 < regStart2)) {
                    // out of display-range
                    continue;
                }
            }

            if (dispStart < 0) {
                dispStart = i;
            }
            dispEnd = i;

            // sp1
            if (from1 < to1) {
                from = from1;
                to = to1;
            }
            else {
                from = to1;
                to = from1;
            }

            // region ���֤���̰��֤��Ѵ�
            fromX = getWinPosX(from, regStart1, regWidth1, regMax1, winWidth);
            toX = getWinPosX(to, regStart1, regWidth1, regMax1, winWidth);
            Y = winHeight / 3;

            if (regDir1) {
                if (x < fromX) {
                    // from �ȥ���å����֤Ȥε�Υ�����
                    X = fromX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else if (toX < x) {
                    // to �ȥ���å����֤Ȥε�Υ�����
                    X = toX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else {
                    // alignment ���Ȥε�Υ�����
                    dist = getLineDistance(fromX, Y, toX, Y, x, y);
                }
            }
            else {
                if (x < toX) {
                    // from �ȥ���å����֤Ȥε�Υ�����
                    X = winWidth - toX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else if (fromX < x) {
                    // to �ȥ���å����֤Ȥε�Υ�����
                    X = winWidth - fromX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else {
                    // alignment ���Ȥε�Υ�����
                    dist = getLineDistance(winWidth - fromX, Y, winWidth - toX, Y, x, y);
                }
            }

            if (dist <= minDist) {
                minDist = dist;
                minDistPairIdx = i;
            }

            // sp2
            if (from2 < to2) {
                from = from2;
                to = to2;
            }
            else {
                from = to2;
                to = from2;
            }

            // region ���֤���̰��֤��Ѵ�
            fromX = getWinPosX(from, regStart2, regWidth2, regMax2, winWidth);
            toX = getWinPosX(to, regStart2, regWidth2, regMax2, winWidth);
            Y = winHeight * 2 / 3;

            if (regDir2) {
                if (x < fromX) {
                    // from �ȥ���å����֤Ȥε�Υ�����
                    X = fromX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else if (toX < x) {
                    // to �ȥ���å����֤Ȥε�Υ�����
                    X = toX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else {
                    // alignment ���Ȥε�Υ�����
                    dist = getLineDistance(fromX, Y, toX, Y, x, y);
                }
            }
            else {
                if (x < toX) {
                    // from �ȥ���å����֤Ȥε�Υ�����
                    X = winWidth - toX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else if (fromX < x) {
                    // to �ȥ���å����֤Ȥε�Υ�����
                    X = winWidth - fromX;
                    dist = getPointDistance(X, Y, x, y);
                }
                else {
                    // alignment ���Ȥε�Υ�����
                    dist = getLineDistance(winWidth - fromX, Y, winWidth - toX, Y, x, y);
                }
            }

            if (dist <= minDist) {
                minDist = dist;
                minDistPairIdx = i;
            }
        }

        // sp1-sp2 �� ��Ϣ�դ����� �򥯥�å���
        int x1, y1, x2, y2;
        y1 = (int)((double)winHeight * PadHeightAlignmentRate);
        y2 = (int)((double)winHeight * (1 - PadHeightAlignmentRate));
        for(int i = dispStart; i <= dispEnd; i++) {
            align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, i);
            from1 = align.getFrom1();
            to1   = align.getTo1();
            from2 = align.getFrom2();
            to2   = align.getTo2();

            if ((to1 < regStart1) || (regStart1 + regWidth1 < from1)) {
                // out of display-range
                continue;
            }
            if ((to2 < regStart2) || (regStart2 + regWidth2 < from2)) {
                // out of display-range
                continue;
            }

            // ���̺�ɸ���Ѵ�
            fromX = getWinPosX(from1, regStart1, regWidth1, regMax1, winWidth);
            toX = getWinPosX(to1, regStart1, regWidth1, regMax1, winWidth);
            x1 = (fromX + toX) / 2;

            fromX = getWinPosX(from2, regStart2, regWidth2, regMax2, winWidth);
            toX = getWinPosX(to2, regStart2, regWidth2, regMax2, winWidth);
            x2 = (fromX + toX) / 2;
            if (regDir1) {
                if (regDir2) {
                    dist = getLineDistance(x1, y1, x2, y2, x, y);
                }
                else {
                    dist = getLineDistance(x1, y1, winWidth - x2, y2, x, y);
                }
            }
            else {
                if (regDir2) {
                    dist = getLineDistance(winWidth - x1, y1, x2, y2, x, y);
                }
                else {
                    dist = getLineDistance(winWidth - x1, y1, winWidth - x2, y2, x, y);
                }
            }

            if (dist <= minDist) {
                minDist = dist;
                minDistPairIdx = i;
            }
        }

        return(minDistPairIdx);
    }

    ///////////////////////////////////////////////////////////////////////////
    // (X0,Y0) �� (X,Y) �Ȥε�Υ�����
    protected double getPointDistance(int x0, int y0, int x, int y) {
        return(getPointDistance((double)x0, (double)y0, x, y));
    }

    ///////////////////////////////////////////////////////////////////////////
    // (X0,Y0) �� (X,Y) �Ȥε�Υ�����
    protected double getPointDistance(double x0, double y0, int x, int y) {
        double dist, distX, distY;

        // ��Υ�����
        distX = x0 - (double)x;
        distY = y0 - (double)y;
        dist = Math.sqrt(distX * distX + distY * distY);

        return(dist);
    }

    ///////////////////////////////////////////////////////////////////////////
    // (X1,Y1)-(X2,Y2) �� (X,Y) �Ȥε�Υ�����
    protected double getLineDistance( int x1, int y1,
                                    int x2, int y2,
                                    int x, int y) {
        double a, b;   // ľ��(X1,Y1)-(X2,Y2) : y =   a  * x + b
        double c;      // ����ľ��           : y = -1/a * x + c

        double x0, y0; // ������             : (X0, Y0)
        double dist;

        // X1 �� X2 �Ȥ��ᤤ��� (�ۤܿ�ľ��ľ��)
        if (Math.abs(x2 - x1) < 2.0) {
            dist = Math.abs(x - (x1+x2)/2);
            return(dist);
        }

        // Y1 �� Y2 �Ȥ��ᤤ��� (�ۤܿ�ʿ��ľ��)
        if (Math.abs(y2 - y1) < 2.0) {
            dist = Math.abs(y - (y2+y1)/2);
            return(dist);
        }

        // ľ��
        a = ((double)y2 - (double)y1) / ((double)x2 - (double)x1);
        b = (double)y1 - a * (double)x1;
        c = (double)y + (double)x / a;

        // ���������
        x0 = (c - b) / (a + 1/a);
        y0 = a * (double)x0 + b;

        // ��Υ���֤�
        dist = getPointDistance(x0, y0, x, y);
        return(dist);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected int revDispPos(int oldPos, int width, int winWidth) {
        int newPos;

        newPos = winWidth - oldPos - width;

        return(newPos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updBgColor() {
        String colFg;
        String colBg;
        if (mbgdDataMng.getUseColor() == ColorTab.USE_COLOR_LIGHT) {
            colFg = mbgdDataMng.getProperty(MbgdDataMng.OPT_BG_DARK);
            colBg = mbgdDataMng.getProperty(MbgdDataMng.OPT_BG_LIGHT);
        }
        else {
            colFg = mbgdDataMng.getProperty(MbgdDataMng.OPT_BG_LIGHT);
            colBg = mbgdDataMng.getProperty(MbgdDataMng.OPT_BG_DARK);
        }
        if (colFg == null) {
            colFg = "ffffff";
        }
        if (colBg == null) {
            colBg = "0";
        }
        int rgbFg = Integer.parseInt(colFg, 16);
        int rgbBg = Integer.parseInt(colBg, 16);

        setFgColor(new Color(rgbFg));
        setBgColor(new Color(rgbBg));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updateRawElmColor() {
        // ���迧
        String keyMat, keyMis, keyGap;
        if (mbgdDataMng.getUseColor() == ColorTab.USE_COLOR_LIGHT) {
            keyMat = MbgdDataMng.OPT_AL_SEQ_MAT_LIGHT;
            keyMis = MbgdDataMng.OPT_AL_SEQ_MIS_LIGHT;
            keyGap = MbgdDataMng.OPT_AL_SEQ_GAP_LIGHT;
        }
        else {
            keyMat = MbgdDataMng.OPT_AL_SEQ_MAT_DARK;
            keyMis = MbgdDataMng.OPT_AL_SEQ_MIS_DARK;
            keyGap = MbgdDataMng.OPT_AL_SEQ_GAP_DARK;
        }
        int rgbMat = mbgdDataMng.getPropertyInt(keyMat, 16);
        int rgbMis = mbgdDataMng.getPropertyInt(keyMis, 16);
        int rgbGap = mbgdDataMng.getPropertyInt(keyGap, 16);

        //
        colMat = new Color(rgbMat);
        colMis = new Color(rgbMis);
        colGap = new Color(rgbGap);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getWinPosX(int x0, int regSta, int regWid, int regMax, int winWid) {
        int x;

        if ((regMax < regSta + regWid) &&
            (x0 % regMax < regSta)) {
            // 0bp ��ޤ�����褹����
            x = (int)((float)(regMax - regSta + x0) / (float)regWid * (float)winWid);
        }
        else {
            x = (int)((float)(x0 - regSta) / (float)regWid * (float)winWid);
        }

        return x;
    }

}
