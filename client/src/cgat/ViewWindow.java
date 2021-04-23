package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// ���褹����־�����ݻ�����
//   ���־�����ݻ���������ǡ�ɽ���оݤ���ʪ��ޤǤϴ������ʤ�
public class ViewWindow extends Observable implements Observer {
    // Region �� Zoom �����˹�碌�� [����⡼��] ���ѹ�����
    public static final int DRAWMODE_LEV0      = 20;   // �̾�ɽ��
    public static final int DRAWMODE_LEV1      = 10;   // ORF ̾�����褹��
    public static final int DRAWMODE_LEV2      = 10;   // Region �Ȥ����褹��
    public static final int DRAWMODE_LEV3      =  4;   // �������Ƥ� '|' �����褹��
    public static final int DRAWMODE_LEV4      =  1;   // �������Ƥ����褹��
    public static final int DRAWMODE_LEV5      = 13;   // Alignment ����������褹��

    // �ǡ��������˴ؤ������
    public static final String CHANGE_SEGMENT   = "segment";           // Segment ���ѹ�
    public static final String CHANGE_REGION    = "region";           // Region ���ѹ�
    public static final String CHANGE_DOTPLOT   = "dotPlot";          // DotPlot ���ѹ�
//    public static final String CHANGE_REGZOOM   = "regZoom";          // Region �� Zoom ���ѹ�
//    public static final String CHANGE_DPZOOM    = "dpZoom";           // DotPlot ��Zoom ���ѹ�
//    public static final String CHANGE_REGDIR    = "regDir";           // Region �θ������ѹ�
    public static final String CHANGE_BASESPEC  = "baseSpec";         // �����ʪ�郎�ѹ�
    public static final String CHANGE_DRAWMODE  = "drawMode";         // ����⡼�ɤ��ѹ�
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

        // �ƥ��饤����
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

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ɽ�����֤����
    public void viewPos(String spec, int pos) {
        if (mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC).equals(spec)) {
            // �⤦��������ʪ���ɽ���ΰ��Ϣư������
            viewWinRegion.viewGenomePos(pos, true);
        }
        else {
            viewWinRegion.setRegCenter(MbgdDataMng.OPPO_SPEC, pos);
        }

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ɽ�����֤����
    public void viewPosSide(int side, int pos) {
        if (side == 0) {
            // �⤦��������ʪ���ɽ���ΰ��Ϣư������
            viewWinRegion.viewGenomePos(pos, true);
        }
        else {
            viewWinRegion.setRegCenter(MbgdDataMng.OPPO_SPEC, pos);
        }

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ɽ�����֤����
    public void viewPos(int pos1) {
            viewWinRegion.viewGenomePos(pos1, true);
//        viewWinRegion.setRegCenter(MbgdDataMng.BASE_SPEC, pos1);

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ɽ�����֤����
    public void viewPos(int pos1, int pos2) {
        viewWinRegion.setRegCenter(MbgdDataMng.BASE_SPEC, pos1);
        viewWinRegion.setRegCenter(MbgdDataMng.OPPO_SPEC, pos2);

        // �ƥ��饤����
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

        // Region �����ΰ��ѹ�
        retSta = viewWinRegion.viewLeft();
        if (! retSta) {
            return;
        }

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void viewRight() {
        boolean retSta;

        // Region �����ΰ��ѹ�
        retSta = viewWinRegion.viewRight();
        if (! retSta) {
            return;
        }

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void zoomUpRegion() {
        boolean retSta;

        // Region ����/�̾�
        retSta = viewWinRegion.zoomUp();
        if (! retSta) {
            return;
        }

        // �ƥ��饤����
        makeAlignment();

        setChanged();
        notifyObservers(CHANGE_REGION);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void zoomDownRegion() {
        boolean retSta;

        // Region ����/�̾�
        retSta = viewWinRegion.zoomDown();
        if (! retSta) {
            return;
        }

        // �ƥ��饤����
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
    // �����ʪ��� Alignment �ǡ������б�������֤�õ��
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
    // �������󥹥⡼��
    public void makeAlignmentSequenceMode() {
        int zoomCount = getZoomCount();

        if (DRAWMODE_LEV3 < zoomCount) {
            // �̾�����⡼�ɤʤΤǡ����饤���Ȥ�����
            return;
        }
        else {
            // �ƥ��饤����
try {
            alignSequence.alignment();
}
catch (InterruptedException ie) {
}
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // �������ȥ⡼��
    public void makeAlignmentSegmentMode() {
        //
        alignSegment.alignment();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void exchangeBaseSpec() {
        viewWinRegion.exchangeBaseSpec();
        viewWinDotPlot.exchangeBaseSpec();

        // �ƥ��饤����
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
        // �ǡ������ɤ߹��ߤ���λ����
        if (arg == null) {
            // ɽ���ΰ������
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
    // ����ɽ�����Ƥ��� Alignment ���Ǽ
    public void setCurrentAlignment(Alignment a) {
        viewWinRegion.setCurrentAlignment(a);
    }

}
