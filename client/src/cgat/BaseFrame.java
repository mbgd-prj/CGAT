
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseFrame extends JFrame {
    protected JMenuBar menuBar;
    protected JMenu menu=null;
    protected JToolBar toolBar;
    protected Container pane;

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseFrame() {
        super();
        try {
            pane = getContentPane();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseFrame(int width, int height) {
        super();

        // ウィンドウ「閉じる」イベントを許可
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            setSize(new Dimension(width, height));

            pane = getContentPane();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //ウィンドウが閉じられたときに終了するようにオーバーライド
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            try {
                System.exit(0);
            }
            catch (Exception nop) {
                // アプレットの場合、例外が発生する
                // しかし、exit の例外なので無視する
            }
        }
    }
}
