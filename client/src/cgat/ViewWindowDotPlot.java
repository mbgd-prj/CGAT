package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

///////////////////////////////////////////////////////////////////////////////
//
public class ViewWindowDotPlot {
    public static final int MIN_WIDTH = 50;
    public static final String STR_X = "X";
    public static final String STR_Y = "Y";

    protected static ViewWindowDotPlot _instance = null;

    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow  viewWin;

    protected int dpCenter[];                    // 描画中心
    protected int dpWidth[];                     // 描画幅

    protected boolean zoomX;                  // 拡大/縮小の対象
    protected boolean zoomY;                  // 拡大/縮小の対象

    ///////////////////////////////////////////////////////////////////////////
    //
    public static ViewWindowDotPlot Instance(MbgdDataMng dataMng, ViewWindow vWin) {
        if (_instance != null) {
        }

        //
        _instance = new ViewWindowDotPlot(dataMng, vWin);

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static ViewWindowDotPlot Instance() {
        if (_instance == null) {
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected ViewWindowDotPlot(MbgdDataMng dataMng, ViewWindow vWin) {
        mbgdDataMng = dataMng;
        viewWin     = vWin;

        dpCenter = new int[2];
        dpWidth = new int[2];

        setDpCenter(MbgdDataMng.BASE_SPEC, 0);
        setDpWidth(MbgdDataMng.BASE_SPEC, 0);
        setDpCenter(MbgdDataMng.OPPO_SPEC, 0);
        setDpWidth(MbgdDataMng.OPPO_SPEC, 0);

        setTargetXSta(true);
        setTargetYSta(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDpCenter(boolean basespec, int pos) {
        int side;

        if (basespec) {
            side = 0;
        }
        else {
            side = 1;
        }
        dpCenter[side] = pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDpCenter(boolean basespec) {
        int side;

        if (basespec) {
            side = 0;
        }
        else {
            side = 1;
        }

        return dpCenter[side];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDpWidth(boolean basespec, int wid) {
        int side;

        if (basespec) {
            side = 0;
        }
        else {
            side = 1;
        }

        dpWidth[side] = wid;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDpWidth(boolean basespec) {
        int side;

        if (basespec) {
            side = 0;
        }
        else {
            side = 1;
        }

        return dpWidth[side];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTargetXSta(boolean sta) {
        zoomX = sta;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getTargetXSta() {
        return zoomX;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTargetYSta(boolean sta) {
        zoomY = sta;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getTargetYSta() {
        return zoomY;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 拡大表示する
    public boolean zoomUp() {
        int width;
        boolean retSta = false;

        //
        if (getTargetXSta()) {
            width = getDpWidth(MbgdDataMng.BASE_SPEC);
            width /= 2;
            if (width < MIN_WIDTH) {
                width = MIN_WIDTH;
            }
            setDpWidth(MbgdDataMng.BASE_SPEC, width);
            retSta = true;
        }
        else {
            Dbg.println(3, "DBG :: SKIP zoomUpDotPlotX");
        }

        //
        if (getTargetYSta()) {
            width = getDpWidth(MbgdDataMng.OPPO_SPEC);
            width /= 2;
            if (width < MIN_WIDTH) {
                width = MIN_WIDTH;
            }
            setDpWidth(MbgdDataMng.OPPO_SPEC, width);
            retSta = true;
        }
        else {
            Dbg.println(3, "DBG :: SKIP zoomUpDotPlotY");
        }

        return retSta;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 縮小表示する
    public boolean zoomDown() {
        boolean retSta = false;
        int maxWidth1;
        int maxWidth2;
        int center1;
        int center2;
        int width1;
        int width2;

        maxWidth1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        maxWidth2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        center1 = getDpCenter(MbgdDataMng.BASE_SPEC);
        center2 = getDpCenter(MbgdDataMng.OPPO_SPEC);
        width1 = getDpWidth(MbgdDataMng.BASE_SPEC);
        width2 = getDpWidth(MbgdDataMng.OPPO_SPEC);


        //
        if (getTargetXSta()) {
            if (! (maxWidth1 <= width1)) {
                width1 *= 2;

                if (maxWidth1 < width1) {
                    width1 = maxWidth1;
                }

                if (center1 - width1 / 2 < 0) {
                    setDpCenter(MbgdDataMng.BASE_SPEC, width1 / 2);
                }
                else if (maxWidth1 < center1 + width1 / 2) {
                    setDpCenter(MbgdDataMng.BASE_SPEC, maxWidth1 - width1 / 2);
                }

                setDpWidth(MbgdDataMng.BASE_SPEC, width1);
                retSta = true;
            }
        }
        else {
            Dbg.println(3, "DBG :: SKIP zoomDownDotPlotX");
        }

        //
        if (getTargetYSta()) {
            if (! (maxWidth2 <= width2)) {
                width2 *= 2;

                if (maxWidth2 < width2) {
                    width2 = maxWidth2;
                }

                if (center2 - width2 / 2 < 0) {
                    setDpCenter(MbgdDataMng.OPPO_SPEC, width2 / 2);
                }
                else if (maxWidth2 < center2 + width2 / 2) {
                    setDpCenter(MbgdDataMng.OPPO_SPEC, maxWidth2 - width2 / 2);
                }

                setDpWidth(MbgdDataMng.OPPO_SPEC, width2);
                retSta = true;
            }
        }
        else {
            Dbg.println(3, "DBG :: SKIP zoomDownDotPlotY");
        }

        return retSta;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void exchangeBaseSpec() {
        int iWk;
        boolean bWk;

        iWk = dpCenter[0];
        dpCenter[0] = dpCenter[1];
        dpCenter[1] = iWk;

        iWk = dpWidth[0];
        dpWidth[0] = dpWidth[1];
        dpWidth[1] = iWk;

        bWk = zoomX;
        zoomX = zoomY;
        zoomY = bWk;

    }

}
