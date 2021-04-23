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
public class BaseViewer extends JFrame {
    private JScrollPane scrollPane;
    private JEditorPane editorPane;
    private JButton     closeButton;

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseViewer() {
        super();

        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        Container pane = getContentPane();
        JPanel basePanel = new JPanel();
        pane.add(basePanel);

        //
        basePanel.setLayout(new BorderLayout());

        // 表示領域（スクロール可能とする）
        editorPane = new JEditorPane();
        scrollPane = new JScrollPane(editorPane);
        editorPane.setEditable(true);
        scrollPane.setVisible(true);

        // close ボタン
        JPanel jp = new JPanel();
        closeButton = new JButton("Close");
        closeButton.addActionListener(new CloseAction(this));
        jp.add(closeButton);

        // JFrame に組み込む
        basePanel.add(scrollPane, BorderLayout.CENTER);
        basePanel.add(jp, BorderLayout.SOUTH);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setText(String text) {
        editorPane.setContentType("text/plain");
        editorPane.setText(text);

        //
//        JScrollBar sb = scrollPane.getVerticalScrollBar();
//        sb.setValue(sb.getMinimum());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setHtml(String html) {
        editorPane.setContentType("text/html");
        editorPane.setEditable(true);
        editorPane.setText(html);
        editorPane.setVisible(true);
        editorPane.setEditable(false);

        //
        JScrollBar sb = scrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMinimum());

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class CloseAction implements ActionListener {
        private BaseViewer baseViewer;

        ///////////////////////////////////////////////////////////////////////
        //
        public CloseAction(BaseViewer bv) {
            baseViewer = bv;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void actionPerformed(ActionEvent e) {
            baseViewer.dispose();
        }

    }


}
