
/**
 * �����ȥ�:     cgat<p>
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
    // �ġ���С��� DotPlot ��ʪ�郎����å����줿�Ȥ��ƤӽФ����
    public void actionPerformed(ActionEvent e) {
        // JToggleButton ��������֤����
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
