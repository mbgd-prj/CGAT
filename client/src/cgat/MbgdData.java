
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class MbgdData { // extends Observable implements Observer {

    boolean isApplet   = false;
    Applet  thisApplet = null;

    // BasePath (BaseUrl)
    String basePath = "";
    String serverUrl = "";
    String proxy = "";

    // プロパティーファイル
    String PropDir  = null;
    String PropFile = null;

//    BaseProps mbgdProps = new BaseProps();

    static String PROP_BROWSEREXE    = "mbgd.browser.executable";
    static String PROP_BROWSEROPT    = "mbgd.browser.options";
    static String PROP_DBGETURL      = "mbgd.dbget.url";
    static String PROP_DBGETCGI      = "mbgd.dbget.cgi";
    static String PROP_DATASERVER    = "mbgd.data.server";
    static String PROP_DATAPATH      = "mbgd.data.local";
    static String PROP_MAXHIT        = "mbgd.search.maxHit";

    RGB rgb;

    static MbgdData _instance = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    static public MbgdData Instance() {
        if (_instance == null) {
            _instance = new MbgdData(null);
        }

        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    static public MbgdData Instance(Applet a) {
        if (_instance == null) {
            _instance = new MbgdData(a);
        }

        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private MbgdData(Applet a) {
        super();

        // アプレットかどうかの判定
        isApplet = false;
        thisApplet = a;
        if (thisApplet != null) {
            isApplet = true;
        }
    }

/*
    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadProps() {
        try {
            mbgdProps.load(new FileInputStream(PropFile));
        }
        catch (FileNotFoundException e) {
            mbgdProps.setProperty(PROP_DATASERVER, 0, "");
            mbgdProps.setProperty(PROP_DATASERVER, 1, "");
            mbgdProps.setProperty(PROP_DATASERVER, 2, "");
            mbgdProps.setProperty(PROP_DATAPATH,   0, "");
            mbgdProps.setProperty(PROP_DATAPATH,   1, "");
            mbgdProps.setProperty(PROP_DATAPATH,   2, "");
            mbgdProps.setProperty(PROP_BROWSEREXE, 0, "");
            mbgdProps.setProperty(PROP_BROWSEROPT, 0, "");
            mbgdProps.setProperty(PROP_DBGETURL,   0, "");
            mbgdProps.setProperty(PROP_DBGETCGI,   0, "");
        }
        catch (Exception e) {
            Dbg.println(1, "Error : load properties("+PropFile+")");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void storeProps() {
        try {
            mbgdProps.store(new FileOutputStream(PropFile), "AlignmentViewer Props");
        }
        catch (Exception e) {
            Dbg.println(1, "Error : save properties("+PropFile+")");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setProps(String key, int idx, String val) {
        mbgdProps.setProperty(key, idx, val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getProps(String key, int idx) {
        return(mbgdProps.getProperty(key, idx));
    }
*/

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isApplet() {
        return isApplet;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Applet getApplet() {
        return(thisApplet);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getServerUrl() {
        return(serverUrl);
    }

}
