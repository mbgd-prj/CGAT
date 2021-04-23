
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ViewPositionCommand implements ActionListener {
    private ViewWindow viewWin;
    private int side;
    private int pos;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ViewPositionCommand(ViewWindow v) {
        viewWin = v;
        setSide(0);
        setPos(1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSide(int s) {
        side = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setPos(int p) {
        pos = p;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        viewWin.viewPosSide(side, pos);
    }

}
