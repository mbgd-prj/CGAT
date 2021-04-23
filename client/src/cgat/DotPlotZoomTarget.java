
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DotPlotZoomTarget extends Observable implements ActionListener {
    static final boolean X = true;
    static final boolean Y = false;

    private ViewWindow viewWin;
    boolean target;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DotPlotZoomTarget(ViewWindow v, boolean t) {
        viewWin = v;
        target = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ツールバーの DotPlot 生物種がクリックされたとき呼び出される
    public void actionPerformed(ActionEvent e) {
        // JToggleButton の選択状態を取得
        JToggleButton jtb = (JToggleButton)e.getSource();
        boolean sta = jtb.isSelected();

        if (target) {
            viewWin.setTargetDotPlotX(sta);
        }
        else {
            viewWin.setTargetDotPlotY(sta);
        }
    }

}
