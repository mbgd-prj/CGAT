
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DotPlotZoomDownCommand implements ActionListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DotPlotZoomDownCommand(ViewWindow v) {
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        viewWin.zoomDownDotPlot();
    }

}
