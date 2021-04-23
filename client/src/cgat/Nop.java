
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// 何も処理しない
public class Nop implements ActionListener, MouseListener, Observer {

    ///////////////////////////////////////////////////////////////////////////
    //
    public Nop() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // for ActionListener
    public void actionPerformed(ActionEvent e) {
    }


    ///////////////////////////////////////////////////////////////////////////
    // for MouseListener
    public void mouseReleased(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // for MouseListener
    public void mousePressed(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // for MouseListener
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // for MouseListener
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // for MouseListener
    public void mouseClicked(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // for Observer
    public void update(Observable obsrv, Object arg) {
    }
}
