
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

///////////////////////////////////////////////////////////////////////////////
//
public class SelectFileCommand implements ActionListener {
    private JFrame frame;
    private Object selectedFilename;

    ///////////////////////////////////////////////////////////////////////////
    //
    public SelectFileCommand(JFrame f, Object n) {
        frame = f;
        selectedFilename = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSelectedFilename(String n) {
        if (selectedFilename instanceof StringBuffer) {
            StringBuffer sb = (StringBuffer)selectedFilename;
            sb.replace(0, sb.length(), n);
        }
        else if (selectedFilename instanceof JTextField) {
            JTextField tf = (JTextField)selectedFilename;
            tf.setText(n);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedFilename() {
        if (selectedFilename instanceof StringBuffer) {
            StringBuffer sb = (StringBuffer)selectedFilename;
            return sb.toString();
        }
        else if (selectedFilename instanceof JTextField) {
            JTextField tf = (JTextField)selectedFilename;
            return tf.getText();
        }
        return "";
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfile = new JFileChooser();
        int ret = jfile.showDialog(frame, "Select");
        if (ret == JFileChooser.APPROVE_OPTION) {
            setSelectedFilename(jfile.getSelectedFile().getAbsolutePath());
        }
    }

}
