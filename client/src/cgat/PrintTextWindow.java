package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class PrintTextWindow extends JFrame {
    JTextArea textViewArea;
    Container pane;

    ///////////////////////////////////////////////////////////////////////////
    //
    public PrintTextWindow() {
        super();

        setSize(500,500);

        pane = getContentPane();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(createTextArea(), BorderLayout.CENTER);
        panel.add(createButtonArea(), BorderLayout.SOUTH);

        pane.add(panel);

        setVisible(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createTextArea() {
        JPanel panel = new JPanel();

        textViewArea = new JTextArea();
        panel.add(textViewArea);

        return(panel);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createButtonArea() {
        JPanel panel = new JPanel();
        JButton closeButon = new JButton("Close");

        ClosePrintTextWindow closeWin = new ClosePrintTextWindow(this);

        closeButon.addActionListener(closeWin);

        panel.add(closeButon);

        return(panel);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setText(String t) {
        textViewArea.setText(t);
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    class ClosePrintTextWindow implements ActionListener {
        JFrame frame;
        public ClosePrintTextWindow(JFrame f) {
            frame = f;
        }

        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }




}
