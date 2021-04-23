package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispReAlignmentSequenceCommand implements ActionListener {
    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispReAlignmentSequenceCommand(MbgdDataMng m, ViewWindow v) {
		mbgdDataMng = m;
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
		AlignmentSequence alignSeq = viewWin.getAlignSequence();

		//
		AlignSeq as = new AlignSeq();
		String spName1 = mbgdDataMng.getSpecName(MbgdDataMng.BASE_ALIGN);
		String spName2 = mbgdDataMng.getSpecName(MbgdDataMng.OPPO_ALIGN);

		//
		as.setSp1(spName1);
		as.setPos1(alignSeq.getRegPosStart(AlignmentSequence.SBJ));
		as.setDir1(true);
        String seq1;
try {
		seq1 = alignSeq.getAlignedSeq(AlignmentSequence.SBJ);
}
catch (InterruptedException ie) {
        seq1 = "";
}
		as.setSeq1(seq1);

		//
		as.setSp2(spName2);
		as.setPos2(alignSeq.getRegPosStart(AlignmentSequence.QRY));
		as.setDir2(viewWin.getRegDir(MbgdDataMng.OPPO_ALIGN));
        String seq2;
try {
        seq2 = alignSeq.getAlignedSeq(AlignmentSequence.QRY);
}
catch (InterruptedException ie) {
        seq2 = "";
}
		as.setSeq2(seq2);

		//
		as.updateMatches();
		as.reorder();

        // 配列表示 window
        DispAlignSequence dispAlignSeq = new DispAlignSequence();
        dispAlignSeq.setSize(600, 400);
        dispAlignSeq.setVisible(true);
        dispAlignSeq.setTitle("Alignment Sequence : " + spName1 + " - " + spName2);

        // 配列データを表示
        dispAlignSeq.setAlignSeq(as, spName1, spName2);
    }

}
