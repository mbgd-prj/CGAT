
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawingModeListener implements ActionListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawingModeListener(ViewWindow vWin) {
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    // �ꥹ������
    public void actionPerformed(ActionEvent e) {
        // ���򤵤줿�⡼�ɤ򥻥åȤ���
        int mode = 0;
        viewWin.setDrawMode(mode);
    }

}
