
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DotPlotRegionInfo extends Observable {
    static DotPlotRegionInfo _instance = null;

    // DotPlot 拡大縮小対象
    boolean targetX = true;
    boolean targetY = true;

    int xViewRegStart;       //
    int xViewRegWidth;       //
    int xViewRegMax;         //

    int yViewRegStart;       //
    int yViewRegWidth;       //
    int yViewRegMax;         //

    int xSupportLine;       // 画面座標ではなく、Region
    int ySupportLine;
    int wSupportLine;
    int hSupportLine;

    ///////////////////////////////////////////////////////////////////////////
    //
    static public DotPlotRegionInfo Instance() {
        if (_instance == null) {
            _instance = new DotPlotRegionInfo();
        }

        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected DotPlotRegionInfo() {
        setXViewRegion(1, 1, 1);
        setYViewRegion(1, 1, 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTargetX(boolean f) {
        targetX = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTargetY(boolean f) {
        targetY = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setXViewRegion(int s, int w, int m) {
        xViewRegMax   = m;
        setXViewRegion(s, w);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setXViewRegion(int s, int w) {
        xViewRegStart = s;
        xViewRegWidth = w;

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setYViewRegion(int s, int w, int m) {
        yViewRegMax   = m;
        setYViewRegion(s, w);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setYViewRegion(int s, int w) {
        yViewRegStart = s;
        yViewRegWidth = w;

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getXViewRegionStart() {
        return(xViewRegStart);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getXViewRegionWidth() {
        return(xViewRegWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getXViewRegionMax() {
        return(xViewRegMax);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getYViewRegionStart() {
        return(yViewRegStart);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getYViewRegionWidth() {
        return(yViewRegWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getYViewRegionMax() {
        return(yViewRegMax);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSupportLine(int x, int y, int w, int h) {
        xSupportLine = x;
        ySupportLine = y;
        wSupportLine = w;
        hSupportLine = h;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getXSupportLine() {
        return(xSupportLine);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getYSupportLine() {
        return(ySupportLine);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getWSupportLine() {
        return(wSupportLine);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getHSupportLine() {
        return(hSupportLine);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewZoomUp() {
        if (targetX) {
            viewXZoomUp();
        }

        if (targetY) {
            viewYZoomUp();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewXZoomUp() {
        int xstart = getXViewRegionStart();
        int xwidth = getXViewRegionWidth();
        int xmax   = getXViewRegionMax();
        int newXStart;
        int newXWidth;

        // 表示領域を半分にする（拡大表示）
        xwidth /= 2;
        newXWidth = xwidth;
        if (newXWidth < 50) {
            newXWidth = 50;
        }

        // 現在表示している領域を中心に表示する
        newXStart = xSupportLine + wSupportLine / 2 - newXWidth / 2;
        if (newXStart < 0) {
            newXStart = 0;
        }
        if (xmax < newXStart + newXWidth) {
            newXStart = xmax - newXWidth;
        }

        setXViewRegion(newXStart, newXWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewYZoomUp() {
        int ystart = getYViewRegionStart();
        int ywidth = getYViewRegionWidth();
        int ymax   = getYViewRegionMax();
        int newYStart;
        int newYWidth;

        // 表示領域を半分にする（拡大表示）
        ywidth /= 2;
        newYWidth = ywidth;
        if (newYWidth < 50) {
            newYWidth = 50;
        }

        // 現在表示している領域を中心に表示する
        newYStart = ySupportLine + hSupportLine / 2 - newYWidth / 2;
        if (newYStart < 0) {
            newYStart = 0;
        }
        if (ymax < newYStart + newYWidth) {
            newYStart = ymax - newYWidth;
        }



        setYViewRegion(newYStart, newYWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewZoomDown() {
        if (targetX) {
            viewXZoomDown();
        }

        if (targetY) {
            viewYZoomDown();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewXZoomDown() {
        int xstart = getXViewRegionStart();
        int xwidth = getXViewRegionWidth();
        int xmax   = getXViewRegionMax();
        int newXStart;
        int newXWidth;

        // 表示領域を２倍にする（縮小表示）
        xwidth *= 2;
        newXWidth = xwidth;
        if (xmax < newXWidth) {
            newXWidth = xmax;
        }

        // 現在表示している領域を中心に表示する
        newXStart = xSupportLine + wSupportLine / 2 - newXWidth / 2;
        if (newXStart < 0) {
            newXStart = 0;
        }
        if (xmax < newXStart + newXWidth) {
            newXStart = xmax - newXWidth;
        }

        setXViewRegion(newXStart, newXWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewYZoomDown() {
        int ystart = getYViewRegionStart();
        int ywidth = getYViewRegionWidth();
        int ymax   = getYViewRegionMax();
        int newYStart;
        int newYWidth;

        // 表示領域を２倍にする（縮小表示）
        ywidth *= 2;
        newYWidth = ywidth;
        if (ymax < newYWidth) {
            newYWidth = ymax;
        }

        // 現在表示している領域を中心に表示する
        newYStart = ySupportLine + hSupportLine / 2 - newYWidth / 2;
        if (newYStart < 0) {
            newYStart = 0;
        }
        if (ymax < newYStart + newYWidth) {
            newYStart = ymax - newYWidth;
        }

        setYViewRegion(newYStart, newYWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void exchangeSpec() {
        int wk;
        wk = xViewRegStart; xViewRegStart = yViewRegStart; yViewRegStart = wk;
        wk = xViewRegWidth; xViewRegWidth = yViewRegWidth; yViewRegWidth = wk;
        wk = xViewRegMax;   xViewRegMax   = yViewRegMax;   yViewRegMax   = wk;
    }



}
