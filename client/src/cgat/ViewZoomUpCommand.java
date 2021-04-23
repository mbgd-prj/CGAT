
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ViewZoomUpCommand implements ActionListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ViewZoomUpCommand(ViewWindow v) {
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        viewWin.zoomUpRegion();
    }

}
