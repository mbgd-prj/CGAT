
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AddListElmCommand implements ActionListener {
    JList list;
    JTextField text;

    ///////////////////////////////////////////////////////////////////////////
    //
    public AddListElmCommand(JList l, JTextField t) {
        list = l;
        text = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        String url = text.getText();
    }

}
