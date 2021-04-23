package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ProceedDialog extends Thread implements Observer {
    public static int dialogWidth  = 200;
    public static int dialogHeight = 150;

    private JFrame  frame;
    private JDialog dialog;
    private Cursor  oldCursor;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ProceedDialog(JFrame f, String msg) {
        super();

        frame = f;

        dialog = new JDialog(frame, true);
        dialog.setSize(dialogWidth, dialogHeight);

        Dimension dim = dialog.getToolkit().getScreenSize();
        dialog.setLocation( (int)(dim.getWidth() - dialogWidth) / 2,
                            (int)(dim.getHeight() - dialogHeight) / 2);

        dialog.getContentPane().add(new JLabel(msg));

        // �ޥ������������㺽���ע���ѹ�
        oldCursor = frame.getCursor();
        Cursor newCursor = new Cursor(Cursor.WAIT_CURSOR);
        frame.setCursor(newCursor);


        dialog.setVisible(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void run() {
        // ����������ɽ������
        dialog.setVisible(true);

        // �Ԥ�
        try {
            wait();
        }
        catch (Exception e) {
            Dbg.println(1, "Error :: wait() : " + e);
        }

        // ���������򱣤�
        dialog.setVisible(false);

        // �ޥ�����������򸵤��᤹
        frame.setCursor(oldCursor);
    }

    public void update(Observable o, Object arg) {
        // ���������򱣤�
        dialog.setVisible(false);
    }

}
