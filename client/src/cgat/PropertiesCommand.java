package cgat;

/**
 * タイトル:  cgat
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
        // 生物種選択ダイアログ表示
        PropertyDialog dialog = new PropertyDialog(frame, mbgdDataMng.getProperty());

        dialog.showDialog();
        if (dialog.getStaSelect() == true) {
            // OK ボタンが押された
            dialog.updateProperties();
            Properties newProp = dialog.getProperties();
            mbgdDataMng.setProperty(newProp);

            // 選択した生物種をクリア
            SelectSpecPairDialog d = SelectSpecPairDialog.Instance(frame);
            d._update();

            mbgdDataMng.saveUserProperties();
        }
    }

}
