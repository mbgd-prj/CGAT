
/**
 * �����ȥ�:     cgat<p>
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
    //�����ͤμ���
    public String getParameter(String key, String def) {
        return isStandalone ? System.getProperty(key, def) :
            (getParameter(key) != null ? getParameter(key) : def);
    }

    ///////////////////////////////////////////////////////////////////////////
    //���ץ�åȤι���
    public AlignmentViewerApplet() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////
    //���ץ�åȤν����
    public void init() {
        super.init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //���ץ�åȳ���
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

        // �¹ԴĶ��Υ����å�
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

            // �С����������å�OK ---> �����¹�
            setSize(new Dimension(width, height));      // �֥饦����� window ������


            //
            alignViewer = new AlignmentViewer(args, this);
            alignViewer.setVisible(true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //���ץ�åȤξ������
    public String getAppletInfo() {
        return "Applet Information";
    }

    ///////////////////////////////////////////////////////////////////////////
    //��������μ���
    public String[][] getParameterInfo() {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //��å����ե����������
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
            // ����С������������ʤ�
            return(false);
        }
        else {
            // ����С������ʾ�Ǥ���
            return(true);
        }
    }
}
