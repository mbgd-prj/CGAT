
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ConnectServerDialog {
    protected static ConnectServerDialog _instance = null;
    protected MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

    protected JFrame frame;

    protected JTextField textCgatHome = new JTextField("");
    protected String cgatHome = "";

    protected JTextField textServerUrl = new JTextField("");
    protected String serverUrl = "";

    protected JTextField textProxyUrl = new JTextField("");
    protected String proxyUrl = "";

    protected boolean staSelect = false;


    ///////////////////////////////////////////////////////////////////////////
    //
    static public ConnectServerDialog Instance(JFrame f) {
        if (_instance == null) {
            _instance = new ConnectServerDialog(f);
        }

        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected ConnectServerDialog(JFrame f) {
        frame = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void showDialog() {
        textCgatHome.setText(cgatHome);
        textServerUrl.setText(serverUrl);
        textProxyUrl.setText(proxyUrl);
        Object[] opt = {"CGAT HOME", textCgatHome,
                        " ",
                        "Server URL", textServerUrl,
                        " ",
                        "Proxy URL", textProxyUrl };

        // ダイアログ表示
        setStaSelect(false);
        int ret = JOptionPane.showConfirmDialog(frame.getContentPane(),
                                                opt,
                                                "Environment",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
            // 内容の保存
            setCgatHome(textCgatHome.getText());
            setServerUrl(textServerUrl.getText(), textProxyUrl.getText());

            setStaSelect(true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setCgatHome(String h) {
        cgatHome = h;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setServerUrl(String s, String p) {
        setServerUrl(s);
        setProxyUrl(p);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setServerUrl(String s) {
        serverUrl = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getCgatHome() {
        return cgatHome;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getServerUrl() {
        return serverUrl;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setProxyUrl(String p) {
        proxyUrl = p;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getProxyUrl() {
        return proxyUrl;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setStaSelect(boolean sta) {
        staSelect = sta;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getStaSelect() {
        return staSelect;
    }

}
