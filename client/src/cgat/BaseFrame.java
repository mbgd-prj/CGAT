
/**
 * �����ȥ�:     cgat<p>
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

        // ������ɥ����Ĥ���ץ��٥�Ȥ����
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
    //������ɥ����Ĥ���줿�Ȥ��˽�λ����褦�˥����С��饤��
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            try {
                System.exit(0);
            }
            catch (Exception nop) {
                // ���ץ�åȤξ�硢�㳰��ȯ������
                // ��������exit ���㳰�ʤΤ�̵�뤹��
            }
        }
    }
}
