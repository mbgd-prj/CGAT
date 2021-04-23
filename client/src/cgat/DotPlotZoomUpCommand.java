
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DotPlotZoomUpCommand implements ActionListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DotPlotZoomUpCommand(ViewWindow v) {
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        viewWin.zoomUpDotPlot();
    }

}
