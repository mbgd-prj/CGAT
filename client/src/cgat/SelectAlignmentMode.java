package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
// Alignment データ表示モード
public class SelectAlignmentMode implements ActionListener {
    private ViewWindow viewWin;
    private DrawingSet drawingSet;
	private RotateButton btn;

    //////////////////////////////////////////////////////////////////////////
    //
    public SelectAlignmentMode(ViewWindow v, DrawingSet dSet, RotateButton b) {
        viewWin = v;
        drawingSet = dSet;
        btn = b;
    }

    //////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        btn.setNext();
        int selIdx = btn.getSelectedIndex();
        if (selIdx == 0) {
            viewWin.setDrawMode(ViewWindowRegion.MODE_SEQUENCE);
        }
        else {
            viewWin.setDrawMode(ViewWindowRegion.MODE_SEGMENT);
        }
    }

}
