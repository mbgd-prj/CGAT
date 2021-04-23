
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
// �����Τ����ϲ�ǽ�ʥƥ����ȥե������
public class NumericField extends JTextField {
    ///////////////////////////////////////////////////////////////////////////
    //
    public NumericField() {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public NumericField(String val) {
        super(val);
        enableEvents(AWTEvent.KEY_EVENT_MASK);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void processKeyEvent(KeyEvent e) {
        String validValues = "0123456789.+-";
        char ch = e.getKeyChar();
        if ((' ' <= ch) && (validValues.indexOf(ch) == -1)) {
            return;
        }
        super.processKeyEvent(e);
    }

}
