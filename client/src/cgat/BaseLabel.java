
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseLabel extends JLabel {
    JLabelMouseListenerCommand mouseListener = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseLabel(String s) {
        super(s);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColor(Color fg0, Color bg0, Color fg1, Color bg1) {
        if (mouseListener != null) {
            removeMouseListener(mouseListener);
        }
        JLabelMouseListenerCommand mouseListener = new JLabelMouseListenerCommand(this);
        addMouseListener(mouseListener);

        mouseListener.setColor(fg0, bg0, fg1, bg1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class JLabelMouseListenerCommand implements MouseListener {
        BaseLabel baseLabel;
        Color fg0Color;
        Color bg0Color;
        Color fg1Color;
        Color bg1Color;


        ///////////////////////////////////////////////////////////////////////
        //
        public JLabelMouseListenerCommand(BaseLabel l) {
            baseLabel = l;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void setColor(Color fg0, Color bg0, Color fg1, Color bg1) {
            fg0Color = fg0;
            bg0Color = bg0;
            fg1Color = fg1;
            bg1Color = bg1;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void mouseReleased(MouseEvent e) {
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void mousePressed(MouseEvent e) {
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void mouseExited(MouseEvent e) {
            baseLabel.setForeground(fg0Color);
            baseLabel.setBackground(bg0Color);
            baseLabel.repaint();
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void mouseEntered(MouseEvent e) {
            baseLabel.setForeground(fg1Color);
            baseLabel.setBackground(bg1Color);
            baseLabel.repaint();
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void mouseClicked(MouseEvent e) {
        }

    }

}
