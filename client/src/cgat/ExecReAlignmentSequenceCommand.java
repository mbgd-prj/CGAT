package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ExecReAlignmentSequenceCommand implements ActionListener {
    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ExecReAlignmentSequenceCommand(MbgdDataMng m, ViewWindow v) {
		super();

		mbgdDataMng = m;
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
	public void actionPerformed(ActionEvent e) {
		// アライメントの再計算を別Threadで実行
		CgatThread ct = new ExecReAlignmentSequence(mbgdDataMng, viewWin);
        ct.setText("Now align sequence.");
        ct.startThread();
    }

}
