
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
public class DrawRegionCgat extends DrawRegion {
    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionCgat(int type, MbgdDataMng dataMng, ViewWindow vWin) {
        super(type, dataMng, vWin);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionCgat(int type, MbgdDataMng dataMng, ViewWindow vWin, int w) {
        super(type, dataMng, vWin);

        setWindowWidth(w);
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewInfoGene(MouseEvent e) {
        int dataType = getDataType();
        int side = this.getSide(dataType);
        int regWidth = viewWin.getRegWidth(dataType);
        int regStart = viewWin.getRegCenter(dataType) - regWidth / 2;
        int regMax   = mbgdDataMng.getGenomeLength(dataType);
        boolean regDir = viewWin.getRegDir(dataType);
        if (regStart < 0) {
            regStart += regMax;
        }
        RegionInfoList regInfoList = mbgdDataMng.getRegionInfoList(dataType);
        if (regInfoList == null) {
            return;
        }
        int idxName    = regInfoList.getAttrIndex(RegionInfoList.ATTR_name);
        int idxProduct = regInfoList.getAttrIndex(RegionInfoList.ATTR_product);

        int clickedX = e.getX();
        int clickedY = e.getY();

        // max �졼���
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = getWindowHeight() / 2 / maxLane;   // �졼�󤴤Ȥι⤵
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }
        int yPos = getWindowHeight() / 2;

        // ����å����֤� Region ������
        int loopMax = mbgdDataMng.getRegionSize(dataType);
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = mbgdDataMng.getRegionInfo(dataType, i);
            int from = r.getFrom();
            int to   = r.getTo() + 1;

            //
            if (regMax < regStart + regWidth) {
                // �ľ��� 0bp ��ޤ������
                if (((regStart + regWidth) % regMax < from) && (to < regStart)) {
                    continue;
                }
            }
            else {
                // �̾�ξ��
                if ((to < regStart) || (regStart + regWidth < from)) {
                    continue;
                }
            }

            // region �β��̾�Ǥΰ���
            int x, y, w, h;
            int zoomCount = viewWin.getZoomCount();
            h = (int)((float)heightLane * r.getWeight());
            if (ViewWindow.DRAWMODE_LEV3 < zoomCount) {
                w = (int)((float)(to - from) / (float)regWidth * (float)winWidth);
                x = (int)((float)(from - regStart) / (float)regWidth * (float)winWidth);
            }
            else { // gap ���θ����ɬ�פ���
                // �ƥ��饤���ȷ��
                AlignmentSequence alignSeq = viewWin.getAlignSequence();
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
            }

            if (x + w < 0) {
                // genome ����Ƭ�˶ᤤ region
                x = (int)((float)(from - regStart + regMax) / (float)regWidth * (float)winWidth);
            }
            if (r.getDir() == 1) {
                y = yPos - (r.getLane() - 1) * heightLane - h;
            }
            else {
                y = yPos + (r.getLane() - 1) * heightLane;
            }

            //
            if (! regDir) {
                x = winWidth - x - w;
                y = winHeight - y - heightLane;
            }

            //
            if ((x <= clickedX) && (clickedX <= x + w) &&
                (y <= clickedY) && (clickedY <= y + h)) {
                String menuText;

                // Popup Menu
                popup = new JPopupMenu();

                JMenuItem jMenuItem;
                Nop nop = new Nop();

                // ORF ̾���˥塼���ɲ�
                menuText = r.getAttr(idxName);
                jMenuItem = new JMenuItem(menuText);
                String sUrl = mbgdDataMng.getRegInfoUrl(dataType);
                StringBuffer sbUrl = new StringBuffer(sUrl);

                //
                if (! "".equals(sUrl)) {
                    String key;
                    int keyIdx;
                    key = "%SPEC%";
                    keyIdx = sUrl.indexOf(key);
                    if (0 <= keyIdx) {
                        sbUrl = sbUrl.replace(keyIdx, keyIdx + key.length(), mbgdDataMng.getSpecName(dataType));
                        sUrl = sbUrl.toString();
                    }
                    key = "%NAME%";
                    keyIdx = sUrl.indexOf(key);
                    if (0 <= keyIdx) {
                        sbUrl = sbUrl.replace(keyIdx, keyIdx + key.length(), r.getAttr(idxName));
                        sUrl = sbUrl.toString();
                    }
                    BrowsGeneInfoCommand cmd1 = new BrowsGeneInfoCommand(sUrl, "_blank");
                    jMenuItem.addActionListener(cmd1);
                }
                popup.add(jMenuItem);

                // Region(from-to)���˥塼���ɲ�
                menuText = "View Sequence : " + r.getFrom() + " - " + r.getTo();
                jMenuItem = new JMenuItem(menuText);
                DispRawSequenceCommand cmd2 = new DispRawSequenceCommand();
                cmd2.setSpName(mbgdDataMng.getSpecName(dataType));
                cmd2.setFrom(r.getFrom());
                cmd2.setTo(r.getTo());
                jMenuItem.addActionListener(cmd2);
                popup.add(jMenuItem);

                // ��˥塼��ɽ��
                popup.show(e.getComponent(), clickedX, clickedY);
                popup.setVisible(true);

                break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ����å����줿���֤���ˡ�Alignment �����뤫�򸡺���ɽ������
    public void searchAlignment(MouseEvent e) {
        int dataType = getDataType();
        int regWidth = viewWin.getRegWidth(dataType);
        int regStart = viewWin.getRegCenter(dataType) - regWidth / 2;
        int regMax   = mbgdDataMng.getGenomeLength(dataType);
        if (regStart < 0) {
            regStart += regMax;
        }
        int clickedX = e.getX();
        int clickedY = e.getY();

        // ����å����֤��� Gene �� Position �����
        int from = 0;
        int to = 0;
        int center;

        // ����å����줿 Genome ���֤򻻽�
        int clickedPos = getClickedGenomePos(clickedX, clickedY);
        if (clickedPos == 0) {
            return;
        }
        if (regMax < clickedPos) {
            clickedPos = clickedPos % regMax;
        }

        // Popup Menu
        popup = new JPopupMenu();

        JMenuItem jMenuItem;
        String menuText;

        //
        menuText = "Clicked Pos : " + clickedPos;
        jMenuItem = new JMenuItem(menuText);
        popup.add(jMenuItem);

        // ���ΰ��֤��濴�˺����褹��
        menuText = "Redraw (Set Center = " + clickedPos + ")";
        jMenuItem = new JMenuItem(menuText);

        ViewPositionCommand cmd3 = new ViewPositionCommand(viewWin);
        cmd3.setSide(getSide(dataType));
        cmd3.setPos(clickedPos);
        jMenuItem.addActionListener(cmd3);
        popup.add(jMenuItem);

        if (viewWin.getDrawMode() != ViewWindowRegion.MODE_SEQVIEW) {
            // Region(from-to)���˥塼���ɲ�
            from = clickedPos - regWidth / 10;
            to   = clickedPos + regWidth / 10;
            menuText = "Search Alignment " + from + " - " + to;
            jMenuItem = new JMenuItem(menuText);
            DispAlignmentListCommand cmd4 = new DispAlignmentListCommand(dataType, mbgdDataMng, viewWin);
            cmd4.setRegion(mbgdDataMng.getSpecName(dataType), from, to);
            jMenuItem.addActionListener(cmd4);
            popup.add(jMenuItem);
        }

        // Legend(colorTab) ���˥塼���ɲ�
        menuText = "Color Legend";
        jMenuItem = new JMenuItem(menuText);
        MessageWindow cmd4 = new MessageWindow();
        String typ = mbgdDataMng.getType(getDataType());
        String url;
        if (typ != null) {
            if (typ.equalsIgnoreCase("float")) {
                // Ϣ³�ͥǡ���
                String html = makeColorTabHelp();
                cmd4.setContents("text/html", html);
            }
            else {
                url = mbgdDataMng.getBasePath() + "cgi-bin/helpColorTab.cgi?type="+typ;
                cmd4.setContents(url);
            }
            jMenuItem.addActionListener(cmd4);
            popup.add(jMenuItem);
        }

        popup.show(e.getComponent(), clickedX, clickedY);
        popup.setVisible(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getClickedGenomePos(int clickedX, int clickedY) {
        int mode = viewWin.getDrawMode();
        int pos = 0;

        switch (mode) {
        case ViewWindowRegion.MODE_SEQVIEW:
            pos = getClickedGenomePosSequence(clickedX, clickedY);
            break;

        case ViewWindowRegion.MODE_SEQUENCE:
            pos = getClickedGenomePosSequence(clickedX, clickedY);
            break;

        case ViewWindowRegion.MODE_SEGMENT:
            pos = getClickedGenomePosSegment(clickedX, clickedY);
            break;

        default:
            break;
        }

        return pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getClickedGenomePosSequence(int clickedX, int clickedY) {
        int dataType  = getDataType();
        int regCenter = viewWin.getRegCenter(dataType);
        int regWidth  = viewWin.getRegWidth(dataType);
        int regStart  = regCenter - regWidth / 2;
        int regMax    = mbgdDataMng.getGenomeLength(dataType);
        boolean regDir = viewWin.getRegDir(dataType);
        int zoomCount = viewWin.getZoomCount();
        if (regStart < 0) {
            regStart += regMax;
        }
        int pos = 0;

        if (ViewWindow.DRAWMODE_LEV3 < zoomCount) {
            // ��ά����ޤ��˻���
            if (regDir) {
                // ������������
                pos = (int)((float)regWidth * (float)clickedX / (float)winWidth);
            }
            else {
                // ������������
                pos = (int)((float)regWidth * (float)(winWidth - clickedX) / (float)winWidth);
            }
            pos += regStart;
return pos;
        }


        // �ƥ��饤���ȷ��
        AlignmentSequence alignSeq = viewWin.getAlignSequence();

        if (ViewWindow.DRAWMODE_LEV4 < zoomCount) {
            // ����
            String seq;    // ����ܺ�
            seq = viewWin.getAlignSequence(dataType);

            double dw = (double)winWidth / (double)seq.length();    // �����𤢤����ɽ����
            double n  = (double)clickedX / dw;  // ��ü����α����

            // GAP �򥫥���Ȥ��ʤ��� genome ��ΰ��֤򻻽�
            pos = regStart;
            for(int i = 0; i < (int)n; i++) {
                if (seq.charAt(i) != '-') {
                    // GAP �ǤϤʤ�
if (regDir) {
                        pos++;
}
else {
                        pos--;
}
                }
            }
        }
        else {
            // �ܺ١�ɽ�����Ƥ˴�Ť�����
            String seq;    // ����ܺ�
            seq = viewWin.getAlignSequence(dataType);
            int wid = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);

            double dw = (double)winWidth / (double)wid;    // 1bp�������ɽ����

            // ��ü���� n ���ܤα��𤬥���å����줿�Ȼפ���
            double n  = (double)clickedX / dw;
int rStart = regStart;
if (! regDir) {
//n = wid - n;
rStart += wid;
}

            // �ƥ��饤���Ȥˤ�� GAP ���������줿
            // ��ü�� regStart �Ȥϸ¤�ʤ�
            // ���饤���ȷ�̤��������줿 Gap �ο�ʬ������regStart ������Ƥ���
            pos = rStart;
            int loopMax = seq.length();
            for(int i = 0; i < loopMax; i++) {
                if (seq.charAt(i) == '-') {
                    // GAP
if (regDir) {
                    pos++;
}
else {
                        pos--;
}
                }
            }

            // GAP �򥫥���Ȥ��ʤ��� genome ��ΰ��֤򻻽�
            for(int i = 0; i < (int)n; i++) {
                if (seq.charAt(i) != '-') {
                    // GAP �ǤϤʤ�
if (regDir) {
                        pos++;
}
else {
                        pos--;
}
                }
            }
        }

        return pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getClickedGenomePosSegment(int clickedX, int clickedY) {
        int dataType = getDataType();
        int regWidth = viewWin.getRegWidth(dataType);
        int regStart = viewWin.getRegCenter(dataType) - regWidth / 2;
        int regMax   = mbgdDataMng.getGenomeLength(dataType);
        int zoomCount = viewWin.getZoomCount();
        if (regStart < 0) {
            regStart += regMax;
        }
        int pos = 0;

        int side = this.getSide(dataType);
        if (side == 0) {
            //
            pos = (int)(clickedX / (double)winWidth * (double)regWidth + (double)regStart);
            pos %= regMax;

            return pos;
        }

        // ɽ���оݤΥǡ���
        AlignmentSegment alignSeg = viewWin.getAlignSegment();
        if (alignSeg == null) {
            return 0;
        }
        //
        ArrayList segPosList = alignSeg.getAlignSegment();
        if (segPosList.size() == 0) {
            // ɽ���оݤΥǡ����ʤ�
            return 0;
        }

        // ����å����֤Υ������Ȥ�Ƚ��
        int loopMax = segPosList.size();
        for(int i = 0; i < loopMax; i++) {
            SegmentPos segPos = (SegmentPos)segPosList.get(i);
            double sFrom   = segPos.getScreenFrom() * getWindowWidth();
            double sTo     = segPos.getScreenTo()   * getWindowWidth();
            double regFrom = (double)segPos.getRegionFrom2();
            double regTo   = (double)segPos.getRegionTo2();
            int alignDir   = segPos.getRegionDir();

            if ((sFrom <= clickedX) && (clickedX <= sTo)) {
                if (alignDir == 1) {
                    pos = (int)((clickedX - sFrom) / (sTo - sFrom) * (regTo - regFrom) + regFrom);
                }
                else {
                    pos = (int)(regTo - (clickedX - sFrom) / (sTo - sFrom) * (regTo - regFrom));
                }
            }
        }

        return pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
        int mod = e.getModifiers();
        if ((mod & MouseEvent.BUTTON1_MASK) != 0) {
            // left ����å�
            viewInfoGene(e);
        }
        if ((mod & MouseEvent.BUTTON2_MASK) != 0) {
            // center ����å�
        }
        if ((mod & MouseEvent.BUTTON3_MASK) != 0) {
            // right ����å�
                searchAlignment(e);
        }
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
                String name = info.getAttr(idxName);
                String prod = info.getAttr(idxProduct);
                if ((prod != null) && (! prod.equals(""))) {
                    name += " :: " + prod;
                }

                setToolTipText(name);
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
    public void setModeSearchAlignment(boolean f) {
        modeSearchAlignment = f;
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


}
