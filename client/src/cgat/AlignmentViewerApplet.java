
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentViewerApplet extends Applet {
    static AlignmentViewer alignViewer = null;
    boolean isStandalone = false;
    String limitVer = "1.2";

    ///////////////////////////////////////////////////////////////////////////
    //引数値の取得
    public String getParameter(String key, String def) {
        return isStandalone ? System.getProperty(key, def) :
            (getParameter(key) != null ? getParameter(key) : def);
    }

    ///////////////////////////////////////////////////////////////////////////
    //アプレットの構築
    public AlignmentViewerApplet() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////
    //アプレットの初期化
    public void init() {
        super.init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //アプレット開始
    public void start() {
        try {
            startAlignmentViewer();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void stop() {
        super.stop();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void destroy() {
        alignViewer.closeFrame();
        alignViewer = null;
        super.destroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void startAlignmentViewer() {
        if (alignViewer != null) {
            return;
        }

        // 実行環境のチェック
        if (versionCheck(limitVer)) {
            String args[] = new String[32];
            int i = 0;

            int width  = Integer.valueOf(getParameter("width")).intValue();
            int height = Integer.valueOf(getParameter("height")).intValue();

            Dbg.println(3, "DocBase : "+getDocumentBase().toString());
            Dbg.println(3, "CodBase : "+getCodeBase().toString());
            MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
            mbgdDataMng.setBase(getDocumentBase().toString(),
                                getCodeBase().toString());
            mbgdDataMng.loadProperties();

            args[i++] = "-applet";
            args[i++] = getCodeBase().toString();
            args[i++] = "-width";
            args[i++] = getParameter("FRAMEWIDTH");
            args[i++] = "-height";
            args[i++] = getParameter("FRAMEHEIGHT");
            String basePath = getParameter("BASEPATH");
            if (basePath != null) {
              args[i++] = "-basepath";
              args[i++] = basePath;
            }

            // バージョンチェックOK ---> 処理実行
            setSize(new Dimension(width, height));      // ブラウザ内の window サイズ


            //
            alignViewer = new AlignmentViewer(args, this);
            alignViewer.setVisible(true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //アプレットの情報取得
    public String getAppletInfo() {
        return "Applet Information";
    }

    ///////////////////////////////////////////////////////////////////////////
    //引数情報の取得
    public String[][] getParameterInfo() {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //ルック＆フィールの設定
    static {
        try {
        }
        catch(Exception e) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean versionCheck(String ver) {
        String key = "java.version";
        if (System.getProperty(key).compareTo(ver) < 0) {
            // 指定バージョンに満たない
            return(false);
        }
        else {
            // 指定バージョン以上である
            return(true);
        }
    }
}
