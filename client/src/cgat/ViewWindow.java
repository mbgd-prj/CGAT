package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// 描画する位置情報を保持する
//   位置情報を保持するだけで、表示対象の生物種までは管理しない
public class ViewWindow extends Observable implements Observer {
    // Region の Zoom 状況に合わせて [描画モード] を変更する
    public static final int DRAWMODE_LEV0      = 20;   // 通常表示
    public static final int DRAWMODE_LEV1      = 10;   // ORF 名を描画する
    public static final int DRAWMODE_LEV2      = 10;   // Region 枠を描画する
    public static final int DRAWMODE_LEV3      =  4;   // 配列内容を '|' で描画する
    public static final int DRAWMODE_LEV4      =  1;   // 配列内容を描画する
    public static final int DRAWMODE_LEV5      = 13;   // Alignment の矢先を描画する

    // データ更新に関する定義
    public static final String CHANGE_SEGMENT   = "segment";           // Segment が変更
    public static final String CHANGE_REGION    = "region";           // Region が変更
    public static final String CHANGE_DOTPLOT   = "dotPlot";          // DotPlot が変更
//    public static final String CHANGE_REGZOOM   = "regZoom";          // Region の Zoom が変更
//    public static final String CHANGE_DPZOOM    = "dpZoom";           // DotPlot のZoom が変更
//    public static final String CHANGE_REGDIR    = "regDir";           // Region の向きが変更
    public static final String CHANGE_BASESPEC  = "baseSpec";         // 基準生物種が変更
    public static final String CHANGE_DRAWMODE  = "drawMode";         // 描画モードが変更
    public static final String CHANGE_SRCHORF   = "searchOrf";         // Search ORF
    public static final String CHANGE_COLOR     = "color";         // Search ORF

    private static ViewWindow _instance = null;
    private MbgdDataMng mbgdDataMng;

    private int                 dispMode;
    private ViewWindowRegion    viewWinRegion;
    private ViewWindowDotPlot   viewWinDotPlot;

    private AlignmentSequence   alignSequence;
    private AlignmentSegment    alignSegment;

    ///////////////////////////////////////////////////////////////////////////
    //
    public static ViewWindow Instance(MbgdDataMng dataMng) {
        if (_instance == null) {
        	_instance = new ViewWindow(dataMng);

/**/
		AlignmentSequence alignSequence
			= new AlignmentSequence(dataMng, _instance);    
		AlignmentSegment  alignSegment
			= new AlignmentSegment(dataMng, _instance);
/**/
		_instance.setAlignmentInfo(alignSequence, alignSegment);

	}
        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected ViewWindow(MbgdDataMng dataMng) {
        _init(dataMng);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(MbgdDataMng dataMng) {
        mbgdDataMng = dataMng;
        viewWinRegion  = ViewWindowRegion.Instance(dataMng, this);
        viewWinDotPlot = ViewWindowDotPlot.Instance(dataMng, this);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDrawMode() {
        return viewWinRegion.getDrawMode();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDrawMode(int sta) {
        viewWinRegion.setDrawMode(sta);

        //
        setChanged();
        notifyObservers(ViewWindow.CHANGE_DRAWMODE);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignmentInfo(AlignmentSequence alignSeq, AlignmentSegment alignSeg) {
        alignSequence = alignSeq;
        alignSegment  = alignSeg;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getZoomCount() {
        return viewWinRegion.getZoomCount();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegCenter(boolean side, int pos) {
        viewWinRegion.setRegCenter(side, pos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegCenter(int type, int pos) {
        boolean side;

        switch(type) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = true;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = false;
            break;
        }

        viewWinRegion.setRegCenter(side, pos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegCenter(boolean side) {
        return viewWinRegion.getRegCenter(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegCenter(int type) {
        boolean side;

        switch(type) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = true;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = false;
            break;
        }

        return viewWinRegion.getRegCenter(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegWidth(boolean side, int wid) {
        viewWinRegion.setRegWidth(side, wid);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegWidth() {
	return getRegWidth(true);
    }
    public int getRegWidth(boolean side) {
        return viewWinRegion.getRegWidth(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDpCenter(boolean side, int pos) {
        viewWinDotPlot.setDpCenter(side, pos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDpCenter(boolean side) {
        return viewWinDotPlot.getDpCenter(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDpWidth(boolean side, int wid) {
        viewWinDotPlot.setDpWidth(side, wid);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDpWidth(boolean side) {
        return viewWinDotPlot.getDpWidth(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegWidth(int type) {
        boolean side;

        switch(type) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = true;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = false;
            break;
        }

        return viewWinRegion.getRegWidth(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegDir(boolean side, boolean sta) {
        viewWinRegion.setRegDir(side, sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegDir(int type, boolean sta) {
        boolean side;

        switch(type) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = true;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = false;
            break;
        }

        viewWinRegion.setRegDir(side, sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getRegDir(boolean side) {
        return viewWinRegion.getRegDir(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getRegDir(int type) {
        boolean side;

        switch(type) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = true;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = false;
            break;
        }

        return viewWinRegion.getRegDir(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void changeRegDir(int type) {
        boolean dir;

        dir = getRegDir(type);
        setRegDir(type, ! dir);

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void changeRegDir(boolean type) {
        boolean dir;

        dir = getRegDir(type);
        setRegDir(type, ! dir);

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 表示位置を指定
    public void viewPos(String spec, int pos) {
        if (mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC).equals(spec)) {
            // もう一方の生物種の表示領域も連動させる
            viewWinRegion.viewGenomePos(pos, true);
        }
        else {
            viewWinRegion.setRegCenter(MbgdDataMng.OPPO_SPEC, pos);
        }

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 表示位置を指定
    public void viewPosSide(int side, int pos) {
        if (side == 0) {
            // もう一方の生物種の表示領域も連動させる
            viewWinRegion.viewGenomePos(pos, true);
        }
        else {
            viewWinRegion.setRegCenter(MbgdDataMng.OPPO_SPEC, pos);
        }

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 表示位置を指定
    public void viewPos(int pos1) {
            viewWinRegion.viewGenomePos(pos1, true);
//        viewWinRegion.setRegCenter(MbgdDataMng.BASE_SPEC, pos1);

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 表示位置を指定
    public void viewPos(int pos1, int pos2) {
        viewWinRegion.setRegCenter(MbgdDataMng.BASE_SPEC, pos1);
        viewWinRegion.setRegCenter(MbgdDataMng.OPPO_SPEC, pos2);

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewPos(int pos1, int pos2, Alignment align) {
        viewWinRegion.setCurrentAlignment(align);
        viewPos(pos1, pos2);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewLeft() {
        boolean retSta;

        // Region 描画領域変更
        retSta = viewWinRegion.viewLeft();
        if (! retSta) {
            return;
        }

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewRight() {
        boolean retSta;

        // Region 描画領域変更
        retSta = viewWinRegion.viewRight();
        if (! retSta) {
            return;
        }

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void zoomUpRegion() {
        boolean retSta;

        // Region 拡大/縮小
        retSta = viewWinRegion.zoomUp();
        if (! retSta) {
            return;
        }

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void zoomDownRegion() {
        boolean retSta;

        // Region 拡大/縮小
        retSta = viewWinRegion.zoomDown();
        if (! retSta) {
            return;
        }

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void zoomUpDotPlot() {
        boolean retSta;

        retSta = viewWinDotPlot.zoomUp();
        if (! retSta) {
            return;
        }

        setChanged();
        notifyObservers(CHANGE_DOTPLOT);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void zoomDownDotPlot() {
        boolean retSta;

        retSta = viewWinDotPlot.zoomDown();
        if (! retSta) {
            return;
        }

        setChanged();
        notifyObservers(CHANGE_DOTPLOT);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTargetDotPlotX(boolean sta) {
        viewWinDotPlot.setTargetXSta(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTargetDotPlotY(boolean sta) {
        viewWinDotPlot.setTargetYSta(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 基準生物種の Alignment データに対応する位置を探す
    public int searchOppositeSpecPos() {
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentSequence getAlignSequence() {
        return alignSequence;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignSequence(boolean basespec) {
        int side;
        String seq;

        if (basespec) {
            side = AlignmentSequence.SBJ;
        }
        else {
            side = AlignmentSequence.QRY;
        }

try {
        seq = alignSequence.getAlignedSeq(side);
}
catch (InterruptedException ie) {
        seq = "";
}

        return seq;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignSequence(int dataType) {
        String seq;

        switch (dataType) {
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            return getAlignSequence(MbgdDataMng.BASE_SPEC);

        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
            return getAlignSequence(MbgdDataMng.OPPO_SPEC);

        default:
            return null;
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentSegment getAlignSegment() {
        return alignSegment;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void makeAlignment() {
        int drawMode = getDrawMode();

        switch (drawMode) {
        case ViewWindowRegion.MODE_SEQVIEW:             // SequenceView
            break;

        case ViewWindowRegion.MODE_SEQUENCE:
            makeAlignmentSequenceMode();
            break;

        case ViewWindowRegion.MODE_SEGMENT:
            makeAlignmentSegmentMode();
            break;

        default:
            break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // シーケンスモード
    public void makeAlignmentSequenceMode() {
        int zoomCount = getZoomCount();

        if (DRAWMODE_LEV3 < zoomCount) {
            // 通常描画モードなので、アライメントは不要
            return;
        }
        else {
            // 再アライメント
try {
            alignSequence.alignment();
}
catch (InterruptedException ie) {
}
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // セグメントモード
    public void makeAlignmentSegmentMode() {
        //
        alignSegment.alignment();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void exchangeBaseSpec() {
        viewWinRegion.exchangeBaseSpec();
        viewWinDotPlot.exchangeBaseSpec();

        // 再アライメント
        makeAlignment();

        setChanged();
        notifyObservers(ViewWindow.CHANGE_BASESPEC);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(Observable o, Object arg) {
        if (o instanceof MbgdDataMng) {
            update((MbgdDataMng)o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(MbgdDataMng dataMng, Object arg) {
        // データの読み込みが終了した
        if (arg == null) {
            // 表示領域を初期化
            setRegCenter(MbgdDataMng.BASE_SPEC, ViewWindowRegion.INIT_REGWIDTH / 2);
            setRegWidth(MbgdDataMng.BASE_SPEC,  ViewWindowRegion.INIT_REGWIDTH);
            setRegCenter(MbgdDataMng.OPPO_SPEC, ViewWindowRegion.INIT_REGWIDTH / 2);
            setRegWidth(MbgdDataMng.OPPO_SPEC,  ViewWindowRegion.INIT_REGWIDTH);

            int len = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
            setDpCenter(MbgdDataMng.BASE_SPEC, len / 2);
            setDpWidth(MbgdDataMng.BASE_SPEC, len);

            len = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
            setDpCenter(MbgdDataMng.OPPO_SPEC, len / 2);
            setDpWidth(MbgdDataMng.OPPO_SPEC, len);

            setChanged();
            notifyObservers(ViewWindow.CHANGE_DOTPLOT);
        }
        else if (((String)arg).equals(CHANGE_SEGMENT)) {
            setChanged();
            notifyObservers(ViewWindow.CHANGE_DOTPLOT);
        }
        else if (((String)arg).equals(CHANGE_BASESPEC)) {
            exchangeBaseSpec();
        }
        else if (((String)arg).equals(CHANGE_COLOR)) {
            setChanged();
            notifyObservers(CHANGE_COLOR);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 現在表示している Alignment を格納
    public void setCurrentAlignment(Alignment a) {
        viewWinRegion.setCurrentAlignment(a);
    }

}
