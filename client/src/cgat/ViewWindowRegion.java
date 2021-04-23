package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// Region 画面表示のための情報
//     Genome 上の位置
//     表示範囲
public class ViewWindowRegion {
    public static final int INIT_REGWIDTH = 10000;
    public static final int MIN_WIDTH     = 50;

    public static final String STR_SEQUENCE = "Align at center";
    public static final String STR_SEGMENT  = "Align against reference";
    public static final int MODE_SEQUENCE = 0;
    public static final int MODE_SEGMENT  = 1;
    public static final int MODE_SEQVIEW  = -1;             // SequenceViewer

    private static ViewWindowRegion _instance = null;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;
    private ZoomLevel   zoomLevel;
    private int drawMode;
    private Alignment currentAlignment;

                                                // 現在描画しているアライメント
    protected HashMap currentAlignmentHash = new HashMap();
    protected ArrayList currentAlignmentArrayList = new ArrayList();

    private int regCenter[];                    // 描画中心
    private int regWidth[];                     // 描画幅
    private boolean regDir[];                   // 向き

    ///////////////////////////////////////////////////////////////////////////
    //
    public static ViewWindowRegion Instance(MbgdDataMng dataMng, ViewWindow vWin) {
        if (_instance != null) {
        }

        //
        _instance = new ViewWindowRegion(dataMng, vWin);

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static ViewWindowRegion Instance() {
        if (_instance == null) {
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private ViewWindowRegion(MbgdDataMng dataMng, ViewWindow vWin) {
        _init(dataMng, vWin);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(MbgdDataMng dataMng, ViewWindow vWin) {
        mbgdDataMng = dataMng;
        viewWin     = vWin;

        zoomLevel = ZoomLevel.Instance();

        regCenter   = new int[2];
        regWidth    = new int[2];
        regDir      = new boolean[2];

        setRegCenter(MbgdDataMng.BASE_SPEC, 0);
        setRegCenter(MbgdDataMng.OPPO_SPEC, 0);
        setRegWidth(MbgdDataMng.BASE_SPEC, INIT_REGWIDTH);
        setRegWidth(MbgdDataMng.OPPO_SPEC, INIT_REGWIDTH);
        setRegDir(MbgdDataMng.BASE_SPEC, true);
        setRegDir(MbgdDataMng.OPPO_SPEC, true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDrawMode(int mode) {
        drawMode = mode;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDrawMode() {
        return drawMode;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getZoomCount() {
        return zoomLevel.getLevel(getRegWidth(MbgdDataMng.BASE_SPEC));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegCenter(boolean basespec, int cent) {
        if (basespec) {
            regCenter[0] = cent;
        }
        else {
            regCenter[1] = cent;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegCenter(boolean basespec) {
        if (basespec) {
            return regCenter[0];
        }
        else {
            return regCenter[1];
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegWidth(boolean basespec, int wid) {
        if (basespec) {
            regWidth[0] = wid;
        }
        else {
            regWidth[1] = wid;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegWidth(boolean basespec) {
        if (basespec) {
            return regWidth[0];
        }
        else {
            return regWidth[1];
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegDir(boolean basespec, boolean dir) {
        if (basespec) {
            regDir[0] = dir;
        }
        else {
            regDir[1] = dir;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getRegDir(boolean basespec) {
        if (basespec) {
            return regDir[0];
        }
        else {
            return regDir[1];
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region の表示領域を Left 方向に移動
    public boolean viewLeft() {
        int pos;
        int wid;
        int lenGenome;

        pos = getRegCenter(MbgdDataMng.BASE_SPEC);
        wid = getRegWidth(MbgdDataMng.BASE_SPEC);
        lenGenome = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);

        boolean regDir1 = getRegDir(MbgdDataMng.BASE_SPEC);
        if (regDir1) {
            pos -= wid / 2;
            if (pos < 0) {
                pos += lenGenome;
            }
        }
        else {
            pos += wid / 2;
            if (lenGenome <= pos) {
                pos -= lenGenome;
            }
        }

        viewGenomePos(pos, false);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region の表示領域を Right 方向に移動
    public boolean viewRight() {
        int pos;
        int wid;
        int lenGenome;

        pos = getRegCenter(MbgdDataMng.BASE_SPEC);
        wid = getRegWidth(MbgdDataMng.BASE_SPEC);
        lenGenome = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);

        boolean regDir1 = getRegDir(MbgdDataMng.BASE_SPEC);
        if (regDir1) {
            pos += wid / 2;
            if (lenGenome <= pos) {
                pos -= lenGenome;
            }
        }
        else {
            pos -= wid / 2;
            if (pos < 0) {
                pos += lenGenome;
            }
        }

        viewGenomePos(pos, true);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Region の表示領域を指定位置に移動
    //     boolean lr  viewLeft  or  viewRight
    //                  false         true
    public void viewGenomePos(int pos0, boolean lr) {
        int pos1, regWidth1, regWidth2;
        Alignment ali1;

        // 基準生物種の表示中心を設定
        setRegCenter(MbgdDataMng.BASE_SPEC, pos0);

        boolean ud = lr;
        Alignment cAlign = getCurrentAlignment();
        if (cAlign != null) {
            if (cAlign.getDir() != 1) {
                ud = false;
            }
        }

        // 基準生物種の位置(pos0)に合わせて、他方の生物種の表示領域を移動させる
        ali1 = AlignmentListFilter.searchAlignOppositeSpec(mbgdDataMng, viewWin, cAlign, ud);
        if (ali1 != null) {
                pos1 = AlignmentListFilter.getPosFromAlign(ali1,
                                                           mbgdDataMng,
                                                           viewWin,
                                                           lr);
        }
        else {
            // 適切な alignment が見つからなかった
            pos1 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
            regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
            regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
            if (cAlign != null) {
                // 前回基準となった alignment をもとに移動する
                double m =  (double)(cAlign.getTo2() - cAlign.getFrom2()) / (double)(cAlign.getTo1() - cAlign.getFrom1());
                if (lr) {
                    if (cAlign.getDir() == 1) {
                        pos1 += (int)(m * regWidth1 / 2.0);
                    }
                    else {
                        pos1 -= (int)(m * regWidth1 / 2.0);
                    }
                }
                else {
                    if (cAlign.getDir() == 1) {
                        pos1 -= (int)(m * regWidth1 / 2.0);
                    }
                    else {
                        pos1 += (int)(m * regWidth1 / 2.0);
                    }
                }
            }
            else {
                // 該当する Alignment が見つからなかった
                // 現在の描画方向をもとに、規定値(WIDTH/2)だけ移動する
                boolean dir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
                if (dir2) {
                    if (ud) {
                        pos1 += regWidth2 / 2;
                    }
                    else {
                        pos1 -= regWidth2 / 2;
                    }
                }
                else {
                    if (ud) {
                        pos1 -= regWidth2 / 2;
                    }
                    else {
                        pos1 += regWidth2 / 2;
                    }
                }
            }

            // 描画範囲の Alignment があれば、その Alignment をそろえる（補正）
            int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
            int regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            int regStart1 = regCenter1 - regWidth1 / 2;
            if (regStart1 < 0) {
                regStart1 += regMax1;
            }
            int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
            int regStart2 = pos1 - regWidth2 / 2;;
            if (regStart2 < 0) {
                regStart2 += regMax2;
            }
            Alignment alignList[] = mbgdDataMng.selectAlignList(regStart1, regWidth1, regMax1, regStart2, regWidth2, regMax2);
            if (alignList.length != 0) {
                Alignment calign = null;
                double minDist = regWidth1;
                double dist;
                boolean ft;
                boolean cft = true;
                for(int i = 0; i < alignList.length; i++) {
                    Alignment align = alignList[i];
                    if (Math.abs(align.getFrom1() - pos0) < Math.abs(align.getTo1() - pos0)) {
                        dist = Math.abs(align.getFrom1() - pos0);
                        ft = true;
                    }
                    else {
                        dist = Math.abs(align.getTo1() - pos0);
                        ft = false;
                    }

                    if (dist < minDist) {
                        // より、中心位置に近い alignment
                        minDist = dist;
                        calign = align;
                        cft = ft;
                    }
                }
                if (calign != null) {
                    // この alignment をそろえるように位置あわせする
                    if (cft) {
                        if (calign.getDir() == 1) {
                            pos1 = calign.getFrom2() - (calign.getFrom1() - pos0);
                        }
                        else {
                            pos1 = calign.getTo2() + (calign.getFrom1() - pos0);
                        }
                    }
                    else {
                        if (calign.getDir() == 1) {
                            pos1 = calign.getTo2() + (pos0 - calign.getTo1());
                        }
                        else {
                            pos1 = calign.getTo2() - (pos0 - calign.getFrom1());
                        }
                    }
                }
            }

        }
        setRegCenter(MbgdDataMng.OPPO_SPEC, pos1);

        setCurrentAlignment(ali1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean zoomUp() {
        int width, newWidth;
        int lev;

        width = getRegWidth(MbgdDataMng.BASE_SPEC);

        // 現在の拡大/縮小レベルを取得
        lev = zoomLevel.getLevel(width);

        // 現在のレベルを基準に１段階拡大表示する
        newWidth = zoomLevel.getRangeByLevel(lev - 1);

        if (width == newWidth) {
            // 拡大/縮小レベルに変化なし
            return false;
        }

        // 情報更新
        setRegWidth(MbgdDataMng.BASE_SPEC, newWidth);
        setRegWidth(MbgdDataMng.OPPO_SPEC, newWidth);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean zoomDown() {
        int width, newWidth;
        int maxWidth;
        int center;
        int lev;

        width = getRegWidth(MbgdDataMng.BASE_SPEC);

        // 現在の拡大/縮小レベルを取得
        lev = zoomLevel.getLevel(width);

        // 現在のレベルを基準に１段階縮小表示する
        newWidth = zoomLevel.getRangeByLevel(lev + 1);

        // 基準生物種のサイズより大きくは表示しない
        maxWidth = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        if (maxWidth < newWidth) {
            newWidth = maxWidth;
        }

        if (width == newWidth) {
            // 拡大/縮小レベルに変化なし
            return false;
        }

        center = getRegCenter(MbgdDataMng.BASE_SPEC);
        if (center - newWidth / 2 < 0) {
            // width が広がったことに合わせて center も変更する
            setRegCenter(MbgdDataMng.BASE_SPEC, newWidth / 2);
        }
        else if (maxWidth < center + newWidth / 2) {
            // width が広がったことに合わせて center も変更する
            setRegCenter(MbgdDataMng.BASE_SPEC, maxWidth - newWidth / 2);
        }

        maxWidth = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        center = getRegCenter(MbgdDataMng.OPPO_SPEC);
        if (center - newWidth / 2 < 0) {
            // width が広がったことに合わせて center も変更する
            setRegCenter(MbgdDataMng.OPPO_SPEC, newWidth / 2);
        }
        else if (maxWidth < center + newWidth / 2) {
            // width が広がったことに合わせて center も変更する
            setRegCenter(MbgdDataMng.OPPO_SPEC, maxWidth - newWidth / 2);
        }


        // 情報更新
        setRegWidth(MbgdDataMng.BASE_SPEC, newWidth);
        setRegWidth(MbgdDataMng.OPPO_SPEC, newWidth);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void exchangeBaseSpec() {
        int iWk;
        boolean bWk;

        iWk = regCenter[0];
        regCenter[0] = regCenter[1];
        regCenter[1] = iWk;

        iWk = regWidth[0];
        regWidth[0] = regWidth[1];
        regWidth[1] = iWk;

        bWk = regDir[0];
        regDir[0] = regDir[1];
        regDir[1] = bWk;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setCurrentAlignment(Alignment align) {
        currentAlignment = align;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment getCurrentAlignment() {
        return currentAlignment;
    }
}
