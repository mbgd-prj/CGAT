package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BrowsFileList implements ActionListener {
    JTextField textField;

    ///////////////////////////////////////////////////////////////////////////
    //
    public BrowsFileList(JTextField tf) {
        textField = tf;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showDialog(null, "Select");
        if (ret == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }


}
