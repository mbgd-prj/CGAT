
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ViewRightCommand implements ActionListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ViewRightCommand(ViewWindow v) {
        viewWin = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        viewWin.viewRight();
    }

}
