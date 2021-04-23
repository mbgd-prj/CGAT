
 /**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawRegion extends BaseDraw implements MouseMotionListener, MouseListener {
    static public final int TYPE_SBJ = 0;
    static public final int TYPE_QRY = 1;

    protected int dataType;                           //
    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow  viewWin;

    protected boolean modeSearchAlignment = false;

    protected Font font;
    protected int HEIGHT_RECT = 12;

    protected JPopupMenu popup;

    // reiogn ��˥ޥ����������뤬�褿�Ȥ���name ��ϥ��饤��ɽ��
    protected ArrayList dispRegInfoList = null;           // RegionInfo
    protected ArrayList dispRegRectList = null;           // Region �����ΰ�
    protected DrawStringPosition orfnamePos = null;       // name ɽ������

    protected int geneSetYofs = 3;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegion(int type, MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        int yofs = dataMng.getPropertyInt(MbgdDataMng.OPT_PANEL_GENESET_YOFS);
        if (0 < yofs) {
            geneSetYofs = yofs;
        }

        _init(type, dataMng, vWin);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegion(int type, MbgdDataMng dataMng, ViewWindow vWin, int w) {
        super();

        _init(type, dataMng, vWin);

        setWindowWidth(w);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void _init(int type, MbgdDataMng dataMng, ViewWindow vWin) {
        setDataType(type);
        mbgdDataMng = dataMng;
        viewWin = vWin;

//        font = new Font("Symbol", Font.PLAIN, 1);
//        font = new Font("Symbol", Font.PLAIN, 10);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataType(int type) {
        dataType = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDataType() {
        return(dataType);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint(Graphics g) {
	winWidth = getWidth(); winHeight = getHeight();
        clear(g);
        drawRegion(g);
        drawFrame(g);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void print(Graphics g) {
	winWidth = getWidth();
	winHeight = getHeight();
	g.setClip(0,0,winWidth,winHeight);
        clearWhite(g);
        drawRegion(g);
        drawFrame(g);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawFrame(Graphics g) {
	int winWidth = getWidth(), winHeight = getHeight();
        g.setColor(getFgColor());

        //
        g.drawRect(0, 0, winWidth-1, winHeight-1);

        //
        g.drawLine(0, winHeight / 2, winWidth, winHeight / 2);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region �ǡ���(Gene/Segment)�������Ԥ�
    //   ɽ���� '�ľ�' �Ȥ������褹��
    public void drawRegion(Graphics g) {
        //
        dispRegInfoList = new ArrayList();
        dispRegRectList = new ArrayList();

        int mode = viewWin.getDrawMode();
        switch (mode) {
        case ViewWindowRegion.MODE_SEQVIEW:             // SequenceView
            //
            drawRegionSequence(g);
            drawRegionSeqView(g);
            break;

        case ViewWindowRegion.MODE_SEQUENCE:
            // �����٥�Υ��饤����
            drawRegionSequence(g);
            break;

        case ViewWindowRegion.MODE_SEGMENT:
            // �������ȥ�٥�Υ��饤����
            drawRegionSegment(g);
            break;

        default:
            break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawRegionSeqView(Graphics g) {
        int zoomCount = viewWin.getZoomCount();

        if (ViewWindow.DRAWMODE_LEV3 < zoomCount) {
            return;
        }
        else {      // �������Ƥ����褹��
            // �����ϰ�����������Ƥμ���
            String seq;
            int regCenter = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            int regWidth  = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
            int regMax    = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
            int regFrom   = regCenter - regWidth / 2;
            int regTo     = regCenter + regWidth / 2;
            if (regFrom <= 0) {
                seq  = mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, regFrom + regMax, regMax);
                seq += mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, 0, regTo);
            }
            else if (regMax < regCenter + regWidth / 2) {
                seq  = mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, regFrom, regMax);
                seq += mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, 0, regTo - regMax);
            }
            else {
                seq = mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, regFrom, regTo);
            }

            // �������Ƥ�����
            double w = (double)winWidth / (double)regWidth;
g.setFont(new Font("Serif", Font.PLAIN, 8));
            for(int i = 0; i < regWidth; i++) {
                drawChar(g, (int)(w * (double)i), winHeight, (int)w, seq.charAt(i));
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawRegionSequence(Graphics g) {

/**/
        int zoomCount = viewWin.getZoomCount();
	int regWidth = viewWin.getRegWidth();
	int winWidth = getWindowWidth();
    float ratio = seqLenPerPixel(viewWin);

//        if (ViewWindow.DRAWMODE_LEV3 < zoomCount) {
        if (ratio > 1) {
            // �̾�⡼�ɤǤ�ɽ��
            drawRegionZoomLev0(g);
        }
//        else if (ViewWindow.DRAWMODE_LEV4 < zoomCount) {
        else if (ratio > 0.25) {
            // �����ץ�٥��ɽ��
            drawRegionZoomLev1(g);
        }
        else {
            // �����٥��ɽ��
            drawRegionZoomLev1(g);
        }
/**/

    }

    ///////////////////////////////////////////////////////////////////////////
    // �������ȥ�٥�Υ��饤����
    //     Sbj ����ΰ��֤ˤ��碌��ɽ������
    public void drawRegionSegment(Graphics g) {
        int dataType = getDataType();
        if (dataType < 10) {
            // ���Ȥʤ���ʪ��ϡ�segment ��ռ������������褹��
            drawRegionZoomLev0(g);
            return;
        }

        // ���Ȥʤ���ʪ��θ���
        boolean regDir0 = viewWin.getRegDir(dataType % 10);

        int regWidth = viewWin.getRegWidth(dataType);
        int regStart = viewWin.getRegCenter(dataType) - regWidth / 2;
        int regMax   = mbgdDataMng.getGenomeLength(dataType);
        boolean regDir = viewWin.getRegDir(dataType);
        int zoomCount = viewWin.getZoomCount();
        if (regStart < 0) {
            regStart += regMax;
        }
        RegionInfoList regInfoList = mbgdDataMng.getRegionInfoList(dataType);
        if (regInfoList == null) {
            return;
        }
        int idxName    = regInfoList.getAttrIndex(RegionInfoList.ATTR_name);
        int idxProduct = regInfoList.getAttrIndex(RegionInfoList.ATTR_product);

        int x, y, w, h;

        // ORF ̾ɽ������
        orfnamePos = new DrawStringPosition(g.getFontMetrics());
        orfnamePos.setHeight(HEIGHT_RECT);

        //
        double winWidth = (double)getWindowWidth();
        int yPos = getWindowHeight() / 2;

        // ɽ���оݤΥǡ���
        AlignmentSegment alignSeg = viewWin.getAlignSegment();
        if (alignSeg == null) {
            return;
        }

        // �����ϰ���� Alignment �ǡ���
        ArrayList segPosList = alignSeg.getAlignSegment();
        if (segPosList.size() == 0) {
            Dbg.println(3, "No segment data");
            return;
        }

        // max �졼���
        int winHeight = getWindowHeight();
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = winHeight / 2 / maxLane;   // �졼�󤴤Ȥι⤵
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }

        // SegmentSet
        HashMap segNameHash = new HashMap();
        String segSetName = regInfoList.getSetName();
        int idxSegSet  = regInfoList.getAttrIndex(segSetName);

        // ɽ���оݤΥǡ��������
        int side = this.getSide(dataType);
        double regFrom[], regTo[];
        int alignDir;
        regFrom = new double[2];
        regTo   = new double[2];
        int loopMax;
        for(int i = 0; i < segPosList.size(); i++) {
            // �ʲ����ϰ���Υǡ��������褹��
            SegmentPos segPos = (SegmentPos)segPosList.get(i);
            regFrom[0] = (double)segPos.getRegionFrom1();
            regTo[0]   = (double)segPos.getRegionTo1();
            regFrom[1] = (double)segPos.getRegionFrom2();
            regTo[1]   = (double)segPos.getRegionTo2();
            alignDir   = segPos.getRegionDir();

            double alignWidth = regTo[side] - regFrom[side];

            // ���̾�Ǥ� Alignment ���������
            double sFrom  = segPos.getScreenFrom() * (double)winWidth;
            double sTo    = segPos.getScreenTo()   * (double)winWidth;
            double sWidth = sTo - sFrom;

            loopMax = mbgdDataMng.getRegionSize(dataType);
            for(int rIdx = 0; rIdx < loopMax; rIdx++) {
                RegionInfo r = mbgdDataMng.getRegionInfo(dataType, rIdx);
                if (r == null) {
                    break;
                }

                if (! r.getFilter()) {
                    // �ե��륿��󥰤���Ƥ���ǡ��� ----> ɽ�����ʤ�
                    continue;
                }

                double from = r.getFrom();
                double to   = r.getTo() + 1;
                byte   dir  = r.getDir();

                if (to < regFrom[side]) {
                    // ɽ���ϰϳ�(�оݥǡ�������)
                    continue;
                }
                if (regTo[side] < from) {
                    // ɽ���ϰϳ�(�⤦�оݥǡ������ʤ�)
                    break;
                }

                //
                double f = from;
                double t = to;
if (10 < dataType) {
                if (f < regFrom[side]) {
                    f = regFrom[side];
                }
                if (regTo[side] < t) {
                    t = regTo[side];
                }
}

                // window ��κ�ɸ���Ѵ�
                double wf, wt;
                wf = (f - regFrom[side]) / alignWidth * sWidth + sFrom;
                wt = (t - regFrom[side]) / alignWidth * sWidth + sFrom;
                if ((side != 0) && (alignDir != 1)) {
                    // ���饤���Ȥθ������� ---> ���饤���Ȥ������ϰ���� X ��ɸ�������ؤ�
                    double wkWidth = wt - wf;
                    wf = sTo - (wf - sFrom) - wkWidth;
                    wt = sTo - (wt - sFrom) + wkWidth;
                }

                x = (int)wf;
                w = (int)(wt - wf);
                h = (int)((float)heightLane * r.getWeight());
                if (((side == 0) && (dir == 1)) ||
                    ((side != 0) && (dir == alignDir))) {
                    y = yPos - (r.getLane() - 1) * heightLane - h;
                }
                else {
                    y = yPos + (r.getLane() - 1) * heightLane;
                }

                //
                if (w < 1) {
                    w = 1;
                }
                if (! regDir0) {
                    x = (int)winWidth - x - w;
                    y = winHeight - y - h;
                }

                // ɽ���оݤΥǡ���
                dispRegInfoList.add(r);
                Rectangle rect = new Rectangle(x, y, w, h);
                dispRegRectList.add(rect);

                //
                if (segSetName != null) {
                    String segName = r.getAttr(idxSegSet);
                    if (! segNameHash.containsKey(segName)) {
                        segNameHash.put(segName, new ArrayList());
                    }
                    ArrayList segSetList = (ArrayList)segNameHash.get(segName);
                    segSetList.add(rect);
                }
            }
        }

        // ����
        String colorType = mbgdDataMng.getColorType(dataType);
        loopMax = dispRegRectList.size();

        for(int i = 0; i < loopMax; i++) {
            RegionInfo r   = (RegionInfo)dispRegInfoList.get(i);
            Rectangle rect = (Rectangle)dispRegRectList.get(i);
            String color;
            color = mbgdDataMng.getGeneAttr(dataType, r.getAttr(idxName));
            if (color == null) {
                color   = r.getColor();
            }

            // ���迧
            Color c;
            c = getFillRectColor(dataType, color, colorType);
            g.setColor(c);

            g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());

            //
            orfnamePos.addRectangle(rect);
            if (zoomCount <= ViewWindow.DRAWMODE_LEV2) {
                // ɽ���ΰ���Ȥ�Ĥ���
                //     �������������ΰ��ɽ������ݤϡ�(���פ��Τ���)�Ȥ�Ĥ��ʤ�
                if (3 < (int)rect.getWidth()) {
                    g.setColor(getFgColor());
                    g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());

                    // �ѥ����������
                    drawPattern(g, (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(),
                                mbgdDataMng.getPattern(dataType, color, colorType));


                }
            }
        }

        if (zoomCount <= ViewWindow.DRAWMODE_LEV1) {
            // SegmentSet ��Ϣ������
            if (segSetName != null) {
                drawGeneSet(g, segNameHash);
            }

            loopMax = dispRegInfoList.size();
            for(int i = 0; i < loopMax; i++) {
                RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
                String name = r.getAttr(idxName);

                // ̾��ɽ��
                Rectangle rect;
                rect = (Rectangle)dispRegRectList.get(i);
                x = (int)rect.getX();
                y = (int)rect.getY();
                h = (int)rect.getHeight();

                if (regDir) {
                    if (0 < r.getDir()) {
                        // ������
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                }
                else {
                    if (0 < r.getDir()) {
                        // ������
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                }

                if (rect == null) {
                    continue;
                }
                if ((0 <= rect.getY()) && (rect.getY() <= winHeight - h)) {
                    g.setColor(getFgColor());
                    g.drawString(name, (int)rect.getX(), (int)(rect.getY() + rect.getHeight()));
                }
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // �̾�⡼�ɤǤ�ɽ��
    public void drawRegionZoomLev0(Graphics g) {
        int dataType = getDataType();
        int regWidth = viewWin.getRegWidth(dataType);
        int regStart = viewWin.getRegCenter(dataType) - regWidth / 2;
        int regMax   = mbgdDataMng.getGenomeLength(dataType);
        boolean regDir = viewWin.getRegDir(dataType);
        int zoomCount = viewWin.getZoomCount();
        if (regStart < 0) {
            regStart += regMax;
        }
        RegionInfoList regInfoList = mbgdDataMng.getRegionInfoList(dataType);
        if (regInfoList == null) {
            return;
        }
        int idxName    = regInfoList.getAttrIndex(RegionInfoList.ATTR_name);
        int idxProduct = regInfoList.getAttrIndex(RegionInfoList.ATTR_product);

        // SegmentSet
        String segSetName = regInfoList.getSetName();
        int idxSegSet  = regInfoList.getAttrIndex(segSetName);

        orfnamePos = new DrawStringPosition(g.getFontMetrics());
        orfnamePos.setHeight(HEIGHT_RECT);

        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();
        int x, y, w, h;

        // max �졼���
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = getWindowHeight() / 2 / maxLane;   // �졼�󤴤Ȥι⤵
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }

        // ɽ���оݤΥǡ��������
        int loopMax = mbgdDataMng.getRegionSize(dataType);
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = mbgdDataMng.getRegionInfo(dataType, i);

            if (! r.getFilter()) {
                // �ե��륿��󥰤���Ƥ���ǡ��� ----> ɽ�����ʤ�
                continue;
            }

            int from = r.getFrom();
            int to   = r.getTo() + 1;

            if (regMax < regStart + regWidth) {
                //
                int regTo = regStart + regWidth - regMax;
                if (from <= regTo) {
                    from += regMax;
                    to += regMax;
                }
            }
            if ((to < regStart) || (regStart + regWidth < from)) {
                    // ɽ���ϰϳ�
                    continue;
            }
            w = (int)((float)(to  - from) / (float)regWidth * (float)winWidth);
            x = (int)((float)(from - regStart) / (float)regWidth * (float)winWidth);
            h = (int)((float)heightLane * r.getWeight());
            if (r.getDir() > 0) {
                y = winHeight / 2 - heightLane * (r.getLane() - 1) - h;
            }
            else {
                y = winHeight / 2 + heightLane * (r.getLane() - 1);
            }

            //
            if (! regDir) {
                x = winWidth - x - w;
                y = winHeight - y - h;
            }

            // ɬ��ɽ�������ä����褹��
            if (w < 1) {
                w = 1;
            }

            // ɽ���оݤΥǡ���
            dispRegInfoList.add(r);
            dispRegRectList.add(new Rectangle(x, y, w, h));
        }

        // Region ������
        HashMap segNameHash = new HashMap();
        String colorType = mbgdDataMng.getColorType(dataType);
        loopMax = dispRegInfoList.size();
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
            String name = r.getAttr(idxName);
            String color;
            color = mbgdDataMng.getGeneAttr(dataType, r.getAttr(idxName));
            if (color == null) {
                color   = r.getColor();
            }

            // ���迧
            Color c;
            c = getFillRectColor(dataType, color, colorType);
            Rectangle rect = (Rectangle)dispRegRectList.get(i);
//g.drawRect(0, 0, (int)winWidth, winHeight);

            g.setColor(c);
            g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());


            // ɽ�����֤��Ťʤ�ʤ��褦 ORF ��ɽ��������֤�׻�����
            orfnamePos.addRectangle(rect);
            if (zoomCount <= ViewWindow.DRAWMODE_LEV2) {
                // ɽ���ΰ���Ȥ�Ĥ���
                //     �������������ΰ��ɽ������ݤϡ�(���פ��Τ���)�Ȥ�Ĥ��ʤ�
                if (3 < (int)rect.getWidth()) {
                    g.setColor(getFgColor());
                    g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
                }
            }

            //
            if (segSetName != null) {
                String segName = r.getAttr(idxSegSet);
                if (! segNameHash.containsKey(segName)) {
                    segNameHash.put(segName, new ArrayList());
                }
                ArrayList segSetList = (ArrayList)segNameHash.get(segName);
                segSetList.add(rect);
            }
        }

        if (zoomCount <= ViewWindow.DRAWMODE_LEV1) {
            // SegmentSet ��Ϣ������
            if (segSetName != null) {
                drawGeneSet(g, segNameHash);
            }

            loopMax = dispRegInfoList.size();
            for(int i = 0; i < loopMax; i++) {
                RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
                String name = r.getAttr(idxName);

                // ̾��ɽ��
                Rectangle rect;
                rect = (Rectangle)dispRegRectList.get(i);
                x = (int)rect.getX();
                y = (int)rect.getY();
                h = (int)rect.getHeight();

                if (regDir) {
                    if (0 < r.getDir()) {
                        // ������
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                    if (rect != null &&
			  (0 <= rect.getY()) && (rect.getY() <= winHeight - h)) {
                    	g.setColor(getFgColor());
                        g.drawString(name, (int)rect.getX(), (int)(rect.getY() + rect.getHeight()));
                    }
                }
                else {
                    if (0 < r.getDir()) {
                        // ������
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                    if (rect != null &&
			  (0 <= rect.getY()) && (rect.getY() <= winHeight - h)) {
                    g.setColor(getFgColor());
                        g.drawString(name, (int)rect.getX(), (int)(rect.getY() + rect.getHeight()));
                    }
                }
            }
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ����/�ܺ٥⡼�ɤǤ�ɽ���ʺƥ��饤���ȷ�̤��θ��������֤�Ĵ�������
    public void drawRegionZoomLev1(Graphics g) {
        int dataType = getDataType();
        int regWidth = viewWin.getRegWidth(dataType);
        int regStart = viewWin.getRegCenter(dataType) - regWidth / 2;
        int regMax   = mbgdDataMng.getGenomeLength(dataType);
        boolean regDir = viewWin.getRegDir(dataType);
        int zoomCount = viewWin.getZoomCount();
        if (regStart < 0) {
            regStart += regMax;
        }
        RegionInfoList regInfoList = mbgdDataMng.getRegionInfoList(dataType);
        if (regInfoList == null) {
            return;
        }
        int idxName    = regInfoList.getAttrIndex(RegionInfoList.ATTR_name);
        int idxProduct = regInfoList.getAttrIndex(RegionInfoList.ATTR_product);

        // SegmentSet
        String segSetName = regInfoList.getSetName();
        int idxSegSet  = regInfoList.getAttrIndex(segSetName);

        // �ƥ��饤���ȷ��
        AlignmentSequence alignSeq = viewWin.getAlignSequence();

        orfnamePos = new DrawStringPosition(g.getFontMetrics());
        orfnamePos.setHeight(HEIGHT_RECT);

        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();
        int x, y, w, h;

        // max �졼���
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = getWindowHeight() / 2 / maxLane;   // �졼�󤴤Ȥι⤵
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }

        // ɽ���оݤΥǡ��������
        int loopMax = mbgdDataMng.getRegionSize(dataType);
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = mbgdDataMng.getRegionInfo(dataType, i);

            if (! r.getFilter()) {
                // �ե��륿��󥰤���Ƥ���ǡ��� ----> ɽ�����ʤ�
                continue;
            }

            int from = r.getFrom();
            int to   = r.getTo() + 1;

            if (regMax < regStart + regWidth) {
				int regTo = regStart + regWidth - regMax;
				if (from <= regTo) {
					from += regMax;
					to += regMax;
				}
            }
            if ((to < regStart) || (regStart + regWidth < from)) {
                    // ɽ���ϰϳ�
                    continue;
            }

            int newFrom, newTo;
            if (dataType < 10) {
                if (regDir) {
                    newFrom = alignSeq.getGappedRegPosOfs(AlignmentSequence.SBJ, from);
                    newTo   = alignSeq.getGappedRegPosOfs(AlignmentSequence.SBJ, to);
                }
                else {
                    newFrom = alignSeq.getGappedRegPosOfs(AlignmentSequence.SBJ, to - 1);
                    newTo   = alignSeq.getGappedRegPosOfs(AlignmentSequence.SBJ, from - 1);
                }
            }
            else {
                if (regDir) {
                    newFrom = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, from);
                    newTo   = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, to) + 1;
                }
                else {
                    newFrom = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, to - 1) + 1;
                    newTo   = alignSeq.getGappedRegPosOfs(AlignmentSequence.QRY, from - 1) + 1;
                }
            }
            from = newFrom;
            to   = newTo;

            w = (int)((float)(to  - from) / (float)regWidth * (float)winWidth);
            x = (int)((float)(from) / (float)regWidth * (float)winWidth);
            h = (int)((float)heightLane * r.getWeight());
            if (r.getDir() > 0) {
                y = winHeight / 2 - heightLane * (r.getLane() - 1) - h;
            }
            else {
                y = winHeight / 2 + heightLane * (r.getLane() - 1);
            }

            //
            if (! regDir) {
//                x = winWidth - x - w;
                y = winHeight - y - h;
            }

            // ɬ��ɽ�������ä����褹��
            if (w < 1) {
                w = 1;
            }

            // ɽ���оݤΥǡ���
            dispRegInfoList.add(r);
            dispRegRectList.add(new Rectangle(x, y, w, h));
        }

        // Region ������
        HashMap segNameHash = new HashMap();
        String colorType = mbgdDataMng.getColorType(dataType);
        loopMax = dispRegInfoList.size();
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
            String name = r.getAttr(idxName);
            String color;
            color = mbgdDataMng.getGeneAttr(dataType, r.getAttr(idxName));
            if (color == null) {
                color   = r.getColor();
            }

            // ���迧
            Color c;
            c = getFillRectColor(dataType, color, colorType);
            Rectangle rect = (Rectangle)dispRegRectList.get(i);

            g.setColor(c);
            g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());


            // ɽ�����֤��Ťʤ�ʤ��褦 ORF ��ɽ��������֤�׻�����
            orfnamePos.addRectangle(rect);
            if (zoomCount <= ViewWindow.DRAWMODE_LEV2) {
                // ɽ���ΰ���Ȥ�Ĥ���
                //     �������������ΰ��ɽ������ݤϡ�(���פ��Τ���)�Ȥ�Ĥ��ʤ�
                if (3 < (int)rect.getWidth()) {
                    g.setColor(getFgColor());
                    g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
                }
            }

            //
            if (segSetName != null) {
                String segName = r.getAttr(idxSegSet);
                if (! segNameHash.containsKey(segName)) {
                    segNameHash.put(segName, new ArrayList());
                }
                ArrayList segSetList = (ArrayList)segNameHash.get(segName);
                segSetList.add(rect);
            }
        }

        // SegmentSet ��Ϣ������
        if (segSetName != null) {
            drawGeneSet(g, segNameHash);
        }

        //
        if (zoomCount <= ViewWindow.DRAWMODE_LEV1) {
            loopMax = dispRegInfoList.size();
            for(int i = 0; i < loopMax; i++) {
                RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
                String name = r.getAttr(idxName);

                // ̾��ɽ��
                Rectangle rect;
                rect = (Rectangle)dispRegRectList.get(i);
                x = (int)rect.getX();
                y = (int)rect.getY();
                h = (int)rect.getHeight();

                if (regDir) {
                    if (0 < r.getDir()) {
                        // ������
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                    if (rect != null &&
			  (0 <= rect.getY()) && (rect.getY() <= winHeight - h)) {
                    	g.setColor(getFgColor());
                        g.drawString(name, (int)rect.getX(), (int)(rect.getY() + rect.getHeight()));
                    }
                }
                else {
                    if (0 < r.getDir()) {
                        // ������
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                    if (rect != null &&
			  (0 <= rect.getY()) && (rect.getY() <= winHeight - h)) {
                    g.setColor(getFgColor());
                        g.drawString(name, (int)rect.getX(), (int)(rect.getY() + rect.getHeight()));
                    }
                }
            }
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // �٤�礦 GeneSet �����Ƿ��
    public void drawGeneSet(Graphics g, HashMap segNameHash) {
        int maxHeight = getWindowHeight();
        Set keySet = segNameHash.keySet();
        Iterator it = keySet.iterator();
        while(it.hasNext()) {
            String segName = (String)it.next();
            ArrayList segSetList = (ArrayList)segNameHash.get(segName);
            if (segSetList.size() <= 1) {
                continue;
            }

            // y, x �ν���¤��ؤ�
            Object rectList[] = segSetList.toArray();
            Arrays.sort(rectList, new SegmentSetComparator());

            // �٤�礦 segmentSet �����Ƿ��
            for(int i = 0; i < rectList.length - 1; i++) {
                Rectangle r1 = (Rectangle)rectList[i];
                Rectangle r2 = (Rectangle)rectList[i + 1];
                if (r1.getY() == r2.getY()) {
                    int x0, y0, x1, y1, x2, y2;
                    x0 = (int)(r1.getX() + r1.getWidth() + r2.getX()) / 2;
                    x1 = (int)(r1.getX() + r1.getWidth());
                    y1 = (int)r1.getY();
                    x2 = (int)r2.getX();
                    y2 = (int)r2.getY();
                    if (r1.getY() < maxHeight / 2) {
                        y0 = geneSetYofs;
                    }
                    else {
                        y0 = maxHeight - geneSetYofs;
                        y1 += (int)r1.getHeight();
                        y2 += (int)r2.getHeight();
                    }
                    g.setColor(Color.black);
                    g.drawLine(x0, y0, x1, y1);
                    g.drawLine(x0, y0, x2, y2);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
        setToolTipText("");
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
    // reiogn ��˥ޥ����������뤬�褿�Ȥ���name ��ϥ��饤��ɽ��
    public void mouseMoved(MouseEvent e) {
        int x, y;
        int i;

        if (dispRegRectList == null) {
            return;
        }

        RegionInfoList regInfoList = mbgdDataMng.getRegionInfoList(dataType);
        if (regInfoList == null) {
            return;
        }
        int idxName    = regInfoList.getAttrIndex(RegionInfoList.ATTR_name);
        int idxProduct = regInfoList.getAttrIndex(RegionInfoList.ATTR_product);
        x = e.getX();
        y = e.getY();

        int loopMax = dispRegRectList.size();
        for(i = 0; i < loopMax; i++) {
            RegionInfo info = (RegionInfo)dispRegInfoList.get(i);
            Rectangle rect = (Rectangle)dispRegRectList.get(i);
            if (rect.contains(x, y)) {
                // gene �����ΰ���˥ޥ��������äƤ���
                setToolTipText(info.getAttr(idxName));
                return;
            }
        }

        // �ɤ� ORF ��ˤ⤤�ʤ�
        setToolTipText("");

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseDragged(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // �ɤ�Ĥ֤��ѥ�����
    //   �����ѥ�����
    //     �ʤ����������ܡ��������ܡ��������ܡ���������
    //   �����ѥ�����
    //     �ʤ�������
    public void drawPattern(Graphics g, int x, int y, int w, int h, int pat) {
        int dy;
        int dh;

        // ����
        switch(pat % 5) {
        case 0:     // �ʤ�
        default:
            break;
        case 1:     // ��������
            dy = h / 2;
            g.drawLine(x, y + dy, x + w, y + dy);
            break;
        case 2:     // ��������
            dy = h / 3;
            g.drawLine(x, y + dy,     x + w, y + dy);
            g.drawLine(x, y + dy * 2, x + w, y + dy * 2);
            break;
        case 3:     // ��������
            dy = h / 3;
            g.drawRect(x, y + dy, w, dy);
            break;
        case 4:     // ��������
            dy = h / 5;
            g.drawRect(x, y + dy,     w, dy);
            g.drawRect(x, y + dy * 3, w, dy);
            break;
        }

        // ����
        switch(pat % 2) {
        case 0:     // �ʤ�
        default:
            break;
        case 1:     // ��������
            dh = 3;
            for(int i = 1; i < w / dh; i++) {
                g.drawLine(x + dh * i, y, x + dh * i, y + h);
            }
            break;
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getFillRectColor(int dataType, String color, String colorType) {
        Color c;

        if (color.startsWith("#")) {
            c = new Color(Integer.parseInt(color.substring(1), 16));
        }
        else {
            c = mbgdDataMng.getColor(dataType, color, colorType);
        }

        return c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSide(int dataType) {
        int side;
        switch(dataType) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = 0;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = 1;
            break;
        }

        return  side;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Ϣ³���Ѥ� ColorTab �� HELP ���������
    public String makeColorTabHelp() {
        String html;
        Color c;

        float min = mbgdDataMng.getGeneAttrMin(getDataType());
        float max = mbgdDataMng.getGeneAttrMax(getDataType());
        float dif = max - min;

        html = "<h3>Gene Attribute</h3>";
        html += "<table border>";

        // Max
        c = mbgdDataMng.getGeneAttrColor(getDataType(), max);
        html += "<tr><td bgcolor=\"#";
        html += Integer.toHexString(c.getRGB() & 0xFFFFFF);
        html += "\" width=\"100\"><br></td>";
        html += "<td>" + max + "</td></tr>";

        int keta = (int)(Math.log(dif) / Math.log(10));
        double dx = Math.pow(10, (double)keta);
        for(int i = (int)(max / dx); ; i--) {
            float v = (float)(dx * (double)i);

            if (v <= min) {
                break;
            }

            c = mbgdDataMng.getGeneAttrColor(getDataType(), v);
            html += "<tr><td bgcolor=\"#";
            html += Integer.toHexString(c.getRGB() & 0xFFFFFF);
            html += "\" width=\"100\"><br></td>";
            html += "<td>" + v + "</td></tr>";
        }

        // Min
        c = mbgdDataMng.getGeneAttrColor(getDataType(), min);
        html += "<tr><td bgcolor=\"#";
        html += Integer.toHexString(c.getRGB() & 0xFFFFFF);
        html += "\" width=\"100\"><br></td>";
        html += "<td>" + min + "</td></tr>";

        html += "</table>";

        return html;
    }

    public class SegmentSetComparator implements java.util.Comparator {
        public int compare(Object o1, Object o2) {
            Rectangle r1 = (Rectangle)o1;
            Rectangle r2 = (Rectangle)o2;
            int y = (int)(r1.getY() - r2.getY());
            if (y != 0) {
                return y;
            }

            int x = (int)(r1.getX() - r2.getX());
            return x;
        }
    }

}
