
 /**
 * タイトル:     cgat<p>
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

    // reiogn 上にマウスカーソルが来たとき、name をハイライト表示
    protected ArrayList dispRegInfoList = null;           // RegionInfo
    protected ArrayList dispRegRectList = null;           // Region 描画領域
    protected DrawStringPosition orfnamePos = null;       // name 表示位置

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
    // Region データ(Gene/Segment)の描画を行う
    //   表示上 '環状' として描画する
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
            // 配列レベルのアライメント
            drawRegionSequence(g);
            break;

        case ViewWindowRegion.MODE_SEGMENT:
            // セグメントレベルのアライメント
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
        else {      // 配列内容を描画する
            // 描画範囲内の配列内容の取得
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

            // 配列内容を描画
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
            // 通常モードでの表示
            drawRegionZoomLev0(g);
        }
//        else if (ViewWindow.DRAWMODE_LEV4 < zoomCount) {
        else if (ratio > 0.25) {
            // 配列概要レベルの表示
            drawRegionZoomLev1(g);
        }
        else {
            // 配列レベルの表示
            drawRegionZoomLev1(g);
        }
/**/

    }

    ///////////////////////////////////////////////////////////////////////////
    // セグメントレベルのアライメント
    //     Sbj 配列の位置にあわせて表示する
    public void drawRegionSegment(Graphics g) {
        int dataType = getDataType();
        if (dataType < 10) {
            // 基準となる生物種は、segment を意識せず全て描画する
            drawRegionZoomLev0(g);
            return;
        }

        // 基準となる生物種の向き
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

        // ORF 名表示位置
        orfnamePos = new DrawStringPosition(g.getFontMetrics());
        orfnamePos.setHeight(HEIGHT_RECT);

        //
        double winWidth = (double)getWindowWidth();
        int yPos = getWindowHeight() / 2;

        // 表示対象のデータ
        AlignmentSegment alignSeg = viewWin.getAlignSegment();
        if (alignSeg == null) {
            return;
        }

        // 描画範囲内の Alignment データ
        ArrayList segPosList = alignSeg.getAlignSegment();
        if (segPosList.size() == 0) {
            Dbg.println(3, "No segment data");
            return;
        }

        // max レーン数
        int winHeight = getWindowHeight();
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = winHeight / 2 / maxLane;   // レーンごとの高さ
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }

        // SegmentSet
        HashMap segNameHash = new HashMap();
        String segSetName = regInfoList.getSetName();
        int idxSegSet  = regInfoList.getAttrIndex(segSetName);

        // 表示対象のデータを抽出
        int side = this.getSide(dataType);
        double regFrom[], regTo[];
        int alignDir;
        regFrom = new double[2];
        regTo   = new double[2];
        int loopMax;
        for(int i = 0; i < segPosList.size(); i++) {
            // 以下の範囲内のデータを描画する
            SegmentPos segPos = (SegmentPos)segPosList.get(i);
            regFrom[0] = (double)segPos.getRegionFrom1();
            regTo[0]   = (double)segPos.getRegionTo1();
            regFrom[1] = (double)segPos.getRegionFrom2();
            regTo[1]   = (double)segPos.getRegionTo2();
            alignDir   = segPos.getRegionDir();

            double alignWidth = regTo[side] - regFrom[side];

            // 画面上での Alignment の描画位置
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
                    // フィルタリングされているデータ ----> 表示しない
                    continue;
                }

                double from = r.getFrom();
                double to   = r.getTo() + 1;
                byte   dir  = r.getDir();

                if (to < regFrom[side]) {
                    // 表示範囲外(対象データあり)
                    continue;
                }
                if (regTo[side] < from) {
                    // 表示範囲外(もう対象データがない)
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

                // window 上の座標に変換
                double wf, wt;
                wf = (f - regFrom[side]) / alignWidth * sWidth + sFrom;
                wt = (t - regFrom[side]) / alignWidth * sWidth + sFrom;
                if ((side != 0) && (alignDir != 1)) {
                    // アライメントの向きが逆 ---> アライメントの描画範囲内で X 座標を入れ替え
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

                // 表示対象のデータ
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

        // 描画
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

            // 描画色
            Color c;
            c = getFillRectColor(dataType, color, colorType);
            g.setColor(c);

            g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());

            //
            orfnamePos.addRectangle(rect);
            if (zoomCount <= ViewWindow.DRAWMODE_LEV2) {
                // 表示領域に枠をつける
                //     ただし、広い領域を表示する際は、(見易さのため)枠をつけない
                if (3 < (int)rect.getWidth()) {
                    g.setColor(getFgColor());
                    g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());

                    // パターンを描く
                    drawPattern(g, (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(),
                                mbgdDataMng.getPattern(dataType, color, colorType));


                }
            }
        }

        if (zoomCount <= ViewWindow.DRAWMODE_LEV1) {
            // SegmentSet を連結描画
            if (segSetName != null) {
                drawGeneSet(g, segNameHash);
            }

            loopMax = dispRegInfoList.size();
            for(int i = 0; i < loopMax; i++) {
                RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
                String name = r.getAttr(idxName);

                // 名称表示
                Rectangle rect;
                rect = (Rectangle)dispRegRectList.get(i);
                x = (int)rect.getX();
                y = (int)rect.getY();
                h = (int)rect.getHeight();

                if (regDir) {
                    if (0 < r.getDir()) {
                        // 正方向
                        rect = orfnamePos.setString(name, x, y + h * 2, false);
                    }
                    else {
                        rect = orfnamePos.setString(name, x, y, true);
                    }
                }
                else {
                    if (0 < r.getDir()) {
                        // 正方向
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
    // 通常モードでの表示
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

        // max レーン数
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = getWindowHeight() / 2 / maxLane;   // レーンごとの高さ
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }

        // 表示対象のデータを抽出
        int loopMax = mbgdDataMng.getRegionSize(dataType);
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = mbgdDataMng.getRegionInfo(dataType, i);

            if (! r.getFilter()) {
                // フィルタリングされているデータ ----> 表示しない
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
                    // 表示範囲外
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

            // 必ず表示幅をもって描画する
            if (w < 1) {
                w = 1;
            }

            // 表示対象のデータ
            dispRegInfoList.add(r);
            dispRegRectList.add(new Rectangle(x, y, w, h));
        }

        // Region の描画
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

            // 描画色
            Color c;
            c = getFillRectColor(dataType, color, colorType);
            Rectangle rect = (Rectangle)dispRegRectList.get(i);
//g.drawRect(0, 0, (int)winWidth, winHeight);

            g.setColor(c);
            g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());


            // 表示位置が重ならないよう ORF を表示する位置を計算する
            orfnamePos.addRectangle(rect);
            if (zoomCount <= ViewWindow.DRAWMODE_LEV2) {
                // 表示領域に枠をつける
                //     ただし、広い領域を表示する際は、(見易さのため)枠をつけない
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
            // SegmentSet を連結描画
            if (segSetName != null) {
                drawGeneSet(g, segNameHash);
            }

            loopMax = dispRegInfoList.size();
            for(int i = 0; i < loopMax; i++) {
                RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
                String name = r.getAttr(idxName);

                // 名称表示
                Rectangle rect;
                rect = (Rectangle)dispRegRectList.get(i);
                x = (int)rect.getX();
                y = (int)rect.getY();
                h = (int)rect.getHeight();

                if (regDir) {
                    if (0 < r.getDir()) {
                        // 正方向
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
                        // 正方向
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
    // 概要/詳細モードでの表示（再アライメント結果を考慮し描画位置を調整する）
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

        // 再アライメント結果
        AlignmentSequence alignSeq = viewWin.getAlignSequence();

        orfnamePos = new DrawStringPosition(g.getFontMetrics());
        orfnamePos.setHeight(HEIGHT_RECT);

        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();
        int x, y, w, h;

        // max レーン数
        int maxLane = mbgdDataMng.getRegionMaxLane(dataType);
        int heightLane = getWindowHeight() / 2 / maxLane;   // レーンごとの高さ
        if (maxLane == 1) {
            heightLane = HEIGHT_RECT;
        }

        // 表示対象のデータを抽出
        int loopMax = mbgdDataMng.getRegionSize(dataType);
        for(int i = 0; i < loopMax; i++) {
            RegionInfo r = mbgdDataMng.getRegionInfo(dataType, i);

            if (! r.getFilter()) {
                // フィルタリングされているデータ ----> 表示しない
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
                    // 表示範囲外
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

            // 必ず表示幅をもって描画する
            if (w < 1) {
                w = 1;
            }

            // 表示対象のデータ
            dispRegInfoList.add(r);
            dispRegRectList.add(new Rectangle(x, y, w, h));
        }

        // Region の描画
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

            // 描画色
            Color c;
            c = getFillRectColor(dataType, color, colorType);
            Rectangle rect = (Rectangle)dispRegRectList.get(i);

            g.setColor(c);
            g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());


            // 表示位置が重ならないよう ORF を表示する位置を計算する
            orfnamePos.addRectangle(rect);
            if (zoomCount <= ViewWindow.DRAWMODE_LEV2) {
                // 表示領域に枠をつける
                //     ただし、広い領域を表示する際は、(見易さのため)枠をつけない
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

        // SegmentSet を連結描画
        if (segSetName != null) {
            drawGeneSet(g, segNameHash);
        }

        //
        if (zoomCount <= ViewWindow.DRAWMODE_LEV1) {
            loopMax = dispRegInfoList.size();
            for(int i = 0; i < loopMax; i++) {
                RegionInfo r = (RegionInfo)dispRegInfoList.get(i);
                String name = r.getAttr(idxName);

                // 名称表示
                Rectangle rect;
                rect = (Rectangle)dispRegRectList.get(i);
                x = (int)rect.getX();
                y = (int)rect.getY();
                h = (int)rect.getHeight();

                if (regDir) {
                    if (0 < r.getDir()) {
                        // 正方向
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
                        // 正方向
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
    // 隣り合う GeneSet を線で結ぶ
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

            // y, x の順に並べ替え
            Object rectList[] = segSetList.toArray();
            Arrays.sort(rectList, new SegmentSetComparator());

            // 隣り合う segmentSet を線で結ぶ
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
    // reiogn 上にマウスカーソルが来たとき、name をハイライト表示
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
                // gene 描画領域内にマウスが入っている
                setToolTipText(info.getAttr(idxName));
                return;
            }
        }

        // どの ORF 上にもいない
        setToolTipText("");

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseDragged(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // 塗りつぶしパターン
    //   横線パターン
    //     なし、細線１本、太線１本、細線２本、太線２本
    //   縦線パターン
    //     なし、細線
    public void drawPattern(Graphics g, int x, int y, int w, int h, int pat) {
        int dy;
        int dh;

        // 横線
        switch(pat % 5) {
        case 0:     // なし
        default:
            break;
        case 1:     // 細線１本
            dy = h / 2;
            g.drawLine(x, y + dy, x + w, y + dy);
            break;
        case 2:     // 細線２本
            dy = h / 3;
            g.drawLine(x, y + dy,     x + w, y + dy);
            g.drawLine(x, y + dy * 2, x + w, y + dy * 2);
            break;
        case 3:     // 太線１本
            dy = h / 3;
            g.drawRect(x, y + dy, w, dy);
            break;
        case 4:     // 太線２本
            dy = h / 5;
            g.drawRect(x, y + dy,     w, dy);
            g.drawRect(x, y + dy * 3, w, dy);
            break;
        }

        // 縦線
        switch(pat % 2) {
        case 0:     // なし
        default:
            break;
        case 1:     // 細線１本
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
    // 連続値用の ColorTab の HELP を作成する
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
