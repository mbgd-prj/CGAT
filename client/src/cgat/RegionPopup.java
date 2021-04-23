
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionPopup extends BaseFrame {
    JPopupMenu popup;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionPopup(String specName, String orfName, String dispName) {
        popup = new JPopupMenu();

        JMenuItem jMenuItem;
        jMenuItem = new JMenuItem(dispName);
        popup.add(jMenuItem);
    }
}
