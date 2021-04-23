package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class PropertiesCommand implements ActionListener {
    private JFrame frame;
    private MbgdDataMng mbgdDataMng;

    ///////////////////////////////////////////////////////////////////////////
    //
    public PropertiesCommand(JFrame f, MbgdDataMng m) {
        frame = f;
        mbgdDataMng = m;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        // ��ʪ�������������ɽ��
        PropertyDialog dialog = new PropertyDialog(frame, mbgdDataMng.getProperty());

        dialog.showDialog();
        if (dialog.getStaSelect() == true) {
            // OK �ܥ��󤬲����줿
            dialog.updateProperties();
            Properties newProp = dialog.getProperties();
            mbgdDataMng.setProperty(newProp);

            // ���򤷤���ʪ��򥯥ꥢ
            SelectSpecPairDialog d = SelectSpecPairDialog.Instance(frame);
            d._update();

            mbgdDataMng.saveUserProperties();
        }
    }

}
