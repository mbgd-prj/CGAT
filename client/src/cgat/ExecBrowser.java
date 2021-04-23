
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.applet.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ExecBrowser implements ActionListener {
    protected String url = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    public ExecBrowser() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ExecBrowser(String u) {
        url = null;
        if (u != null) {
            url = u;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void execBrowser(String u) {
        url = null;
        if (u != null) {
            url = u;
        }
        execBrowser();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void execBrowser() {
        if (url == null) {
            return;
        }

        String key;
        String cmd;
        String osType = System.getProperty("os.name");

        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

        if (osType.startsWith("Windows")) {
            // Windows
            key = MbgdDataMng.OPT_CMD_BROWSER_WIN;
        }
        else if (osType.startsWith("Mac")) {
            // Mac
            key = MbgdDataMng.OPT_CMD_BROWSER_MAC;
        }
        else {
            // のこりは、UNIX 系とする
            key = MbgdDataMng.OPT_CMD_BROWSER_UNIX;
        }

        //
        String browser = mbgdDataMng.getProperty(MbgdDataMng.OPT_CMD_BROWSER_USER);
        cmd = browser + " " + url;

        //
        Runtime runtime = Runtime.getRuntime();
        try {
            Dbg.println(1, "Exec :: " + cmd);
            runtime.exec(cmd);
        }
        catch (Exception e) {
            Dbg.println(0, "ERROR :: Can not execute :: " + cmd);
            String msg = "Can not open '" + url + "'\n\n"
                       + mbgdDataMng.getProperty(MbgdDataMng.OPT_MSG_BROWSERPATH);
            JOptionPane.showMessageDialog(null,
                                          msg,
                                          "ERROR",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void appletBrowser(Applet applet, String urlStr, String target) {
        URL url;

        try {
            url = new URL(urlStr);
            applet.getAppletContext().showDocument(url, target);
        }
        catch (Exception e) {
            Dbg.println(1, "Exception : applet("+urlStr+")");
            String msg = "Can not open '" + urlStr + "'\n";
            JOptionPane.showMessageDialog(null,
                                          msg,
                                          "ERROR",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        execBrowser();
    }

}
