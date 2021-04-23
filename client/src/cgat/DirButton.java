
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DirButton extends BaseButton {
    private int dataType;       //

    ///////////////////////////////////////////////////////////////////////////
    //
    public DirButton(int type) {
        super("");
        dataType = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setButtonLabel(boolean d) {
        if (d) {
            setText("+");
        }
        else {
            setText("-");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ����ܥ������ˤ�ꡢ�����ɽ������ѹ��ˤʤä����ν���
    public void update(Observable o, Object arg) {
        if (o instanceof ViewWindow) {
            update((ViewWindow) o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(ViewWindow viewWin, Object arg) {
        boolean dir;

        // ���ߤ� DIR �����
        dir = viewWin.getRegDir(dataType);

        // �ܥ����٥�򹹿�
        setButtonLabel(dir);
    }

}
