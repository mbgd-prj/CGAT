package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DirButtonCommand implements ActionListener {
    private boolean basespec;
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DirButtonCommand(boolean spec, ViewWindow vWin) {
        basespec = spec;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        viewWin.changeRegDir(basespec);
    }

}
