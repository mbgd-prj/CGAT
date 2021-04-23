package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ExecReAlignmentSequence extends CgatThread {
    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ExecReAlignmentSequence(MbgdDataMng m, ViewWindow v) {
		super();

		mbgdDataMng = m;
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
	public void run() {
try {
		// アライメントの実行
		AlignmentSequence alignSeq = new AlignmentSequence(mbgdDataMng, viewWin);
try {
		alignSeq.alignment();
}
catch (InterruptedException ie) {
		return;
}
        catch (OutOfMemoryError oome) {
            setText(oome.toString());
		    return;
        }

        if (myThread.isInterrupted()) {
            Dbg.println(1, "Thread Interrupted.");
            return;
        }

		// アライメント結果の表示
		AlignSeq as = new AlignSeq();
		String spName1 = mbgdDataMng.getSpecName(MbgdDataMng.BASE_ALIGN);
		String spName2 = mbgdDataMng.getSpecName(MbgdDataMng.OPPO_ALIGN);

		//
		as.setSp1(spName1);
		as.setPos1(alignSeq.getRegPosStart(AlignmentSequence.SBJ));
		as.setDir1(true);
		as.setSeq1(alignSeq.getAlignedSeq(AlignmentSequence.SBJ));

		//
		as.setSp2(spName2);
		as.setPos2(alignSeq.getRegPosStart(AlignmentSequence.QRY));
		as.setDir2(viewWin.getRegDir(MbgdDataMng.OPPO_ALIGN));
		as.setSeq2(alignSeq.getAlignedSeq(AlignmentSequence.QRY));

		//
		as.updateMatches();
		as.reorder();
        if (myThread.isInterrupted()) {
            return;
        }

        // 配列表示 window
        DispAlignSequence dispAlignSeq = new DispAlignSequence();
        dispAlignSeq.setSize(600, 400);
        dispAlignSeq.setVisible(true);
        dispAlignSeq.setTitle("Alignment Sequence : " + spName1 + " - " + spName2);

        // 配列データを表示
        dispAlignSeq.setAlignSeq(as, spName1, spName2);
}
catch (Exception e) {
Dbg.println(1, "catch exception :: Thread Interrupted.");
}

        closeDialog();
    }

}
