
/**
 * ????:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DotPlot extends JComponent implements MouseListener, Observer/*, Printable*/ {
    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow viewWin;

    // Region 補助線
    private int     AuxLine_x = -1;
    private int     AuxLine_y = -1;
    private int     AuxLine_w =  0;
    private int     AuxLine_h =  0;

    static int WIDTH  = 350;
    static int HEIGHT = 350;

    BitSet drawAlignSta;

    int winWidth;           //
    int winHeight;          //

    Color frameColor;
    Color bgColor;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DotPlot(MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        mbgdDataMng = dataMng;
        viewWin = vWin;

        drawAlignSta = new BitSet(0);
        setDrawAlignSta(false);

        setWindowSize(WIDTH, HEIGHT);

        setFrameColor(Color.black);

        updDotPlotBgColor();
        updDotPlotFrameColor();

        //
        try {
            //
            addMouseListener(this);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint() {
        Graphics g = getGraphics();
        if (g != null) {
            paint(g);
        }
        else {
            Dbg.println(1, "DBG :: Can not get Graphics at DotPlot::paint()");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint(Graphics g) {
        super.paint(g);

        // 画面を初期化
        clear(g);

        //
        drawFrame(g);

        // アライメントデータを描画
        drawAlignment(g);

        // Region 枠を描画
        drawAuxLine(g);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        Graphics g = getGraphics();
        if (g != null) {
            clear(g);
        }
        else {
            Dbg.println(1, "getGraphics() returns null");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear(Graphics g) {
        updDotPlotBgColor();
        updDotPlotFrameColor();

        g.setColor(bgColor);
        g.fillRect(0, 0, winWidth, winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawFrame(Graphics g) {
//        g.setColor(frameColor);
//        g.drawRect(0, 0, winWidth, winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawAlignment(Graphics g) {
        if (mbgdDataMng.getAlignmentSize() == 0) {
            //
            setDrawAlignSta(false);
            return;
        }

        setDrawAlignSta(true);

        int regXWidth = viewWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        int regXStart = viewWin.getDpCenter(MbgdDataMng.BASE_SPEC) - regXWidth / 2;
        int xwidth    = getWindowWidth() - 1;

        int regYWidth = viewWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        int regYStart = viewWin.getDpCenter(MbgdDataMng.OPPO_SPEC) - regYWidth / 2;
        int ywidth    = getWindowHeight() - 1;

        for(int i = 0; i < mbgdDataMng.getAlignmentSize(); i++) {
            Alignment align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, i);

            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 表示しない
                continue;
            }

            int fromX = align.getFrom1();
            int toX   = align.getTo1();
            int fromY = align.getFrom2();
            int toY   = align.getTo2();

            if ((toX < regXStart) || (regXStart + regXWidth < fromX)) {
                // 表示範囲外
                continue;
            }
            if ((toY < regYStart) || (regYStart + regYWidth < fromY)) {
                // 表示範囲外
                continue;
            }

            if (align.getDir() < 0) {
                // 逆向き
                int work;
                work = fromY; fromY = toY; toY = work;
            }

            //
            String colorType = align.getType();
//            Color color = mbgdDataMng.getColor(MbgdDataMng.BASE_ALIGN, colorType, ColorTab.TYPE_INT);
            Color color = mbgdDataMng.getAlignColor(MbgdDataMng.BASE_ALIGN, colorType, ColorTab.TYPE_INT, align);
            g.setColor(color);

            //
            int x1 = (int)((float)(fromX - regXStart) / (float)regXWidth * (float)xwidth);
            int y1 = (int)((float)(fromY - regYStart) / (float)regYWidth * (float)ywidth);
            int x2 = (int)((float)(toX -   regXStart) / (float)regXWidth * (float)xwidth);
            int y2 = (int)((float)(toY -   regYStart) / (float)regYWidth * (float)ywidth);

            // 描画領域は、原点が左上である
            // 原点左下で描画する
            g.drawLine(x1, ywidth - y1, x2, ywidth - y2);
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDrawAlignSta(boolean sta) {
        if (sta) {
            drawAlignSta.set(0);
        }
        else {
            drawAlignSta.clear(0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getDrawAlignSta() {
        return(drawAlignSta.get(0));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowSize(int w, int h) {
        winWidth  = w;
        winHeight = h;
        setSize(winWidth, winHeight);
        Dimension d = new Dimension(winWidth, winHeight);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowWidth(int w) {
        winWidth  = w;
        setSize(winWidth, winHeight);
        Dimension d = new Dimension(winWidth, winHeight);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowHeight(int h) {
        winHeight = h;
        setSize(winWidth, winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getWindowWidth() {
        return(winWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getWindowHeight() {
        return(winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setXViewRegion(int center, int width) {
        viewWin.setDpCenter(MbgdDataMng.BASE_SPEC, center);
        viewWin.setDpWidth(MbgdDataMng.BASE_SPEC, width);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setYViewRegion(int center, int width) {
        viewWin.setDpCenter(MbgdDataMng.OPPO_SPEC, center);
        viewWin.setDpWidth(MbgdDataMng.OPPO_SPEC, width);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getViewRegionWidth() {
        int xViewRegWidth = viewWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        int yViewRegWidth = viewWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        if (xViewRegWidth < yViewRegWidth) {
            return(yViewRegWidth);
        }
        else {
            return(xViewRegWidth);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrameColor(Color c) {
        frameColor = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setBgColor(Color c) {
        bgColor = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region 描画領域の枠
    //      通常の描画領域の範囲外に置く
    //
    //          X < 0
    //          Y < 0
    public void initAuxLine() {
        AuxLine_x = -10;
        AuxLine_y = -10;
        AuxLine_w =   1;
        AuxLine_h =   1;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAuxLine(int x, int y, int w, int h) {
        AuxLine_x = x;
        AuxLine_y = y;
        AuxLine_w = w;
        AuxLine_h = h;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region 描画領域の枠
    public void clearAuxLine() {
        // 前の枠を消去
        Graphics g = getGraphics();
        clearAuxLine(g);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region 描画領域の枠
    public void clearAuxLine(Graphics g) {
        // 前の枠を消去
        drawAuxLine(g, AuxLine_x, AuxLine_y, AuxLine_w, AuxLine_h, "clear");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region 描画領域の枠
    public void drawAuxLine() {
        Graphics g = getGraphics();
        drawAuxLine(g);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region 描画領域の枠
    public void drawAuxLine(Graphics g) {
        //
        int regWidth2  = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regMax2    = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

        // Region 描画の表示領域を取得
        int regWidth1  = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        int regStart1  = regCenter1 - regWidth1 / 2;
        int regMax1    = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
        int regStart2  = regCenter2 - regWidth2 / 2;

        // DotPlot 描画の表示範囲
        int dpWidth1   = viewWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        int dpCenter1  = viewWin.getDpCenter(MbgdDataMng.BASE_SPEC);
        int dpStart1   = dpCenter1 - dpWidth1 / 2;
        int dpCenter2  = viewWin.getDpCenter(MbgdDataMng.OPPO_SPEC);
        int dpWidth2   = viewWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        int dpStart2   = dpCenter2 - dpWidth2 / 2;

        // 補助線の描画位置を算出
        int w = (int)((double)(winWidth-1)  * (double)regWidth1 / (double)dpWidth1);
        int h = (int)((double)(winHeight-1) * (double)regWidth2 / (double)dpWidth2);
        int x0 = (int)((double)(winWidth-1)  * ((double)regStart1 - (double)dpStart1) / (double)dpWidth1);
        int y0 = (int)((double)(winHeight-1) * ((double)regStart2 - (double)dpStart2) / (double)dpWidth2);

        // 枠サイズが小さい場合
        if (w < 5) {
            x0 = x0 + w / 2 - 2;
            w = 5;
        }
        if (h < 5) {
            y0 = y0 + h / 2 - 2;
            h = 5;
        }

        // 座標系を変換（Graphics は、左上が原点）
        int windowHeight = getWindowHeight();
        y0 = windowHeight - y0;

        // 枠描画
       	drawAuxLine(g, x0, y0, w, h, "draw");

        // 描画枠の位置を保持する
        setAuxLine(x0, y0, w, h);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region 描画領域の枠の描画
    public void drawAuxLine(Graphics g, int x, int y, int w, int h, String msg) {
        g.setColor(bgColor);
        g.setXORMode(frameColor);
        g.drawRect(x, y - h, w, h);
        g.setXORMode(bgColor);
    }

    ///////////////////////////////////////////////////////////////////////////
    // genome の位置から画面の座標に変換
    public int xConvRegPos2WinPos(int xGenome) {
        int xWidth = viewWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        int xStart = viewWin.getDpCenter(MbgdDataMng.BASE_SPEC) - xWidth / 2;

        int winWidth  = getWindowWidth();
        int winHeight = getWindowHeight();

        int x = (int)(((double)xGenome - (double)xStart) / (double)xWidth * (double)winWidth);

        return(x);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int yConvRegPos2WinPos(int yGenome) {
        int yWidth = viewWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        int yStart = viewWin.getDpCenter(MbgdDataMng.OPPO_SPEC) - yWidth / 2;

        int winWidth  = getWindowWidth();
        int winHeight = getWindowHeight();

        int y = (int)(((double)yGenome - (double)yStart) / (double)yWidth * (double)winHeight);

        return(y);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 画面の座標から genome の位置に変換
    public int xConvWinPos2RegPos(int xWin) {
        int xWidth = viewWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        int xStart = viewWin.getDpCenter(MbgdDataMng.BASE_SPEC) - xWidth / 2;

        int winWidth  = getWindowWidth();
        int winHeight = getWindowHeight();

        int x = (int)(((double)xWin) / (double)winWidth * xWidth + xStart);

        return(x);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int yConvWinPos2RegPos(int yWin) {
        int yWidth = viewWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        int yStart = viewWin.getDpCenter(MbgdDataMng.OPPO_SPEC) - yWidth / 2;

        int winWidth  = getWindowWidth();
        int winHeight = getWindowHeight();

        int y = (int)(((double)yWin) / (double)winWidth * yWidth + yStart);

        return(y);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updateDispArea(int regCenter1, int regCenter2,
                                int regWidth1, int regWidth2) {
        // Region の表示枠が DotPlot の表示領域外である場合、
        // Region の表示枠が中心になるように DotPlot の表示領域を変更する
        //
        int xViewRegWidth = viewWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        int xViewRegStart = viewWin.getDpCenter(MbgdDataMng.BASE_SPEC) - xViewRegWidth / 2;
        int xViewRegMax   = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        //
        int yViewRegWidth = viewWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        int yViewRegStart = viewWin.getDpCenter(MbgdDataMng.OPPO_SPEC) - yViewRegWidth / 2;
        int yViewRegMax   = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        boolean isRepaint = false;

        if (((regCenter1 - regWidth1 / 2) < xViewRegStart) ||
            ((regCenter2 - regWidth2 / 2) < yViewRegStart)) {
            // 表示枠外
            isRepaint = true;
        }
        if ((xViewRegStart + xViewRegWidth < (regCenter1 + regWidth1 / 2)) ||
            (yViewRegStart + yViewRegWidth < (regCenter2 + regWidth2 / 2))) {
            // 表示枠外
            isRepaint = true;
        }
        if (isRepaint) {
            // DotPlot の描画位置を再設定
            if (regCenter1 - xViewRegWidth < 0) {
                regCenter1 = xViewRegWidth / 2;
            }
            else if (xViewRegMax <= regCenter1 + xViewRegWidth) {
                regCenter1 = xViewRegMax - xViewRegWidth / 2;
            }
            setXViewRegion(regCenter1, xViewRegWidth);

            if (regCenter2 - yViewRegWidth < 0) {
                regCenter2 = yViewRegWidth / 2;
            }
            else if (yViewRegMax <= regCenter2 + yViewRegWidth) {
                regCenter2 = yViewRegMax - yViewRegWidth / 2;
            }
            setYViewRegion(regCenter2, yViewRegWidth);

            paint();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
        // クリックした位置を中心に region を表示する
        int xRegPos = xConvWinPos2RegPos(e.getX());
        int yRegPos = yConvWinPos2RegPos(getWindowHeight() - e.getY());

        viewWin.viewPos(xRegPos, yRegPos);
    }

    public synchronized void redrawAuxLine() {
            // 前の枠を消去
            clearAuxLine();

            // 枠を描画
            drawAuxLine();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(Observable o, Object arg) {
        if (! isShowing()) {
            // DotPlot が隠れている状態
            return;
        }

        if ((arg instanceof String) &&
            ViewWindow.CHANGE_COLOR.equals((String)arg)) {
            paint();
        }
        else if ((arg instanceof String) &&
            ViewWindow.CHANGE_BASESPEC.equals((String)arg)) {
            paint();
        }
        else if (o instanceof ViewWindow) {
            update((ViewWindow)o, arg);
        }
        else if (o instanceof MbgdDataMng) {
            update((MbgdDataMng)o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(ViewWindow vWin, Object arg) {
        String mode = "null";

        if (arg != null) {
            mode = (String)arg;
        }

        int regWidth1  = vWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regCenter1 = vWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        int regWidth2  = vWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regCenter2 = vWin.getRegCenter(MbgdDataMng.OPPO_SPEC);

        // Region の表示枠が DotPlot の表示領域外である場合、
        // Region の表示枠が中心になるように DotPlot の表示領域を変更する
        updateDispArea(regCenter1, regCenter2, regWidth1, regWidth2);

        if (mode.equalsIgnoreCase(ViewWindow.CHANGE_DOTPLOT)) {
            repaint();
        }
        else {
	    redrawAuxLine();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(MbgdDataMng dataMng, Object arg) {
        // DotPlot
        int sp1Length = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int sp2Length = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

        viewWin.setDpWidth(MbgdDataMng.BASE_SPEC, sp1Length);
        viewWin.setDpWidth(MbgdDataMng.OPPO_SPEC, sp2Length);
        viewWin.setDpCenter(MbgdDataMng.BASE_SPEC, sp1Length / 2);
        viewWin.setDpCenter(MbgdDataMng.OPPO_SPEC, sp2Length / 2);

        paint();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updDotPlotBgColor() {
        String col;
        if (mbgdDataMng.getUseColor() == ColorTab.USE_COLOR_LIGHT) {
            col = mbgdDataMng.getProperty(MbgdDataMng.OPT_BG_LIGHT);
        }
        else {
            col = mbgdDataMng.getProperty(MbgdDataMng.OPT_BG_DARK);
        }
        if (col == null) {
            col = "0";
        }
        int rgb = Integer.parseInt(col, 16);

        setBgColor(new Color(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updDotPlotFrameColor() {
        String col;
        if (mbgdDataMng.getUseColor() == ColorTab.USE_COLOR_LIGHT) {
            col = mbgdDataMng.getProperty(MbgdDataMng.OPT_DP_FRAME_LIGHT);
        }
        else {
            col = mbgdDataMng.getProperty(MbgdDataMng.OPT_DP_FRAME_DARK);
        }
        if (col == null) {
            col = "ff0000";
        }
        int rgb = Integer.parseInt(col, 16);

        setFrameColor(new Color(rgb));
    }

/*
    ///////////////////////////////////////////////////////////////////////////
    //
    public int print(Graphics g, PageFormat fmt, int pageIdx) {
        if(pageIdx > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(fmt.getImageableX(), fmt.getImageableY());
        double pageWidth = fmt.getImageableWidth();
        double pageHeight = fmt.getImageableHeight();
        Dimension size = getSize();
        double scale = 1.0;

        if(size.width > pageWidth) {
            scale = pageWidth / size.width;
        }
        if(pageHeight / size.height < scale) {
            scale = pageHeight / size.height;
        }
        g2.scale(scale,scale);
        pageWidth /= scale; pageHeight /= scale;

        g2.translate((pageWidth - size.width) / 2,
                     (pageHeight - size.height)/2);
        printAll(g2);

        return Printable.PAGE_EXISTS;
    }
*/
}
