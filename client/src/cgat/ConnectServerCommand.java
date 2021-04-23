
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
public class ConnectServerCommand extends Observable implements ActionListener {
    protected JFrame frame;

    protected boolean isDispSegs[];

    Cursor bakCursor;
    ProceedDialog proceedDialog = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ConnectServerCommand(JFrame f) {
        frame = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

        // 生物種選択ダイアログ表示
        ConnectServerDialog dialog = ConnectServerDialog.Instance(frame);

        dialog.setCgatHome(mbgdDataMng.getCgatHome());
        dialog.setServerUrl(mbgdDataMng.getBasePath(), mbgdDataMng.getProxy());
        dialog.showDialog();
        if (dialog.getStaSelect() == true) {
            // OK ボタンが押された
            mbgdDataMng.setCgatHome(dialog.getCgatHome());
            mbgdDataMng.setBasePath(dialog.getServerUrl());
            mbgdDataMng.setProxy(dialog.getProxyUrl());
            mbgdDataMng.saveUserProperties();

            // 選択した生物種をクリア
            SelectSpecPairDialog d = SelectSpecPairDialog.Instance(frame);
            d._update();
        }
    }

}
