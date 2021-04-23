package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseMessageDialog {
    private static BaseMessageDialog _instance = null;
    private Component component;

    ///////////////////////////////////////////////////////////////////////////
    //
    public static BaseMessageDialog Instance() {
        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static BaseMessageDialog Instance(Component c) {
        if (_instance == null) {
            _instance = new BaseMessageDialog(c);
        }
        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private BaseMessageDialog(Component c) {
        component = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void message(String msg) {
        JOptionPane.showMessageDialog(component, msg);
    }


}
