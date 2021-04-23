
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentViewer implements ActionListener {
    boolean packFrame = false;
    AlignmentViewerFrame frame;

    ///////////////////////////////////////////////////////////////////////////
    //アプリケーションの構築
    public AlignmentViewer(String[] args, Applet applet) {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        int width  = 1000;
        int height =  850;
        boolean pairwise = false;
        String sp1 = "";
        int sp1chr = 1;
        String sp2 = "";
        int sp2chr = 1;
        String prog = "";
        String seg = null;
        String clustid = "";
        String basePath = null;
        String proxy = null;

        //
	/* 2015/10/09  genome pair (sp1,sp2), alignment program (prog), and segment data argument (seg) can be specified in the command line argument */
        for(int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                break;
            }
//Dbg.println(1, "ARGS :: " + args[i] + " :: " + args[i + 1]);
Dbg.println(1, "ARGS :: " + args[i]);
            if (args[i].equalsIgnoreCase("-applet")) {
                basePath = args[i + 1];
                i++;
            }
            else if (args[i].equalsIgnoreCase("-basepath")) {
              basePath = args[i + 1];
              i++;
            } else if (args[i].equalsIgnoreCase("-sp1")) {
              sp1 = args[i + 1]; i++;
            } else if (args[i].equalsIgnoreCase("-sp2")) {
              sp2 = args[i + 1]; i++;
            } else if (args[i].equalsIgnoreCase("-prog")) {
              prog = args[i + 1]; i++;
            } else if (args[i].equalsIgnoreCase("-seg")) {
              seg = args[i + 1]; i++;
            } else if (args[i].equalsIgnoreCase("-debug")) {
              try {
                  Dbg.setDbgLevel(Integer.parseInt(args[i + 1]));
              }
              catch (NumberFormatException nfe) {
              }
              i++;
            }
        }

        if (applet != null) {
            // HTML で指定された [default] を取得
            String pw = applet.getParameter("pairwise");
            if ((pw != null) && pw.equalsIgnoreCase("on")) {
                pairwise = true;
            }
            sp1     = applet.getParameter("sp1");
            sp2     = applet.getParameter("sp2");
            try {
                sp1chr  = Integer.parseInt(applet.getParameter("sp1chr"));
            }
            catch (NumberFormatException nfe) {
                sp1chr = 1;
            }
            mbgdDataMng.setSpecChr(0, sp1chr);

            try {
                sp2chr  = Integer.parseInt(applet.getParameter("sp2chr"));
            }
            catch (NumberFormatException nfe) {
                sp2chr = 1;
            }
            mbgdDataMng.setSpecChr(1, sp2chr);

            prog    = applet.getParameter("prog");
            clustid = applet.getParameter("clustid");
        }

        //
        if (basePath == null) {
            basePath = mbgdDataMng.getProperty(MbgdDataMng.OPT_URL_HOME);
        }
        proxy = mbgdDataMng.getProperty(MbgdDataMng.OPT_URL_PROXY);
        mbgdDataMng.setBasePath(basePath);
        mbgdDataMng.setProxy(proxy);

        MbgdData mbgdData = MbgdData.Instance(applet);
        frame = new AlignmentViewerFrame(sp1, sp2, prog);
        frame.setTitle("CGAT");
        if (pairwise) {
            // MenuBar を非表示
            frame.setVisibleMenuBar(false);
            mbgdDataMng.setMbgdCgiPrograms(clustid);

            // データの自動ダウンロード
			URL u = applet.getCodeBase();
			String url = u.getProtocol() + "://" + u.getHost();
            int port = u.getPort();
            if (0 < port) {
				url += ":" + port;
            }
			String falign = "pairwise";
			String geneAttrDir = null;
			String geneAttrColorType = "";
			String segType[] = new String[MbgdDataMng.MAX_SEGS];
			String segCgi[]  = new String[MbgdDataMng.MAX_SEGS];
            for(int i = 0; i < MbgdDataMng.MAX_SEGS; i++) {
                segType[i] = segCgi[i] = null;
            }
            mbgdDataMng.load(frame, sp1, sp2,
                                    url,
                                    falign,
                                    geneAttrDir, geneAttrColorType,
                                    segType, segCgi);
        } else if (basePath != null && sp1 != null && sp2 != null) {
		String geneAttrDir = null;
		String geneAttrColorType = "";
		String segType[] = new String[MbgdDataMng.MAX_SEGS];
		String segCgi[]  = new String[MbgdDataMng.MAX_SEGS];
		int segN = 0;

		if (seg != null ) {
			String segType0[] = seg.split(",", 0);
			for (int i = 0; i < segType0.length; i++) {
				segType[i] = segType0[i]+"(Server)";
			}
			segN = segType0.length;
		} else {
		}
       		mbgdDataMng.load(frame, sp1, sp2,
                                    basePath,
                                    prog,
                                    geneAttrDir, geneAttrColorType,
                                    segType, segCgi);
		mbgdDataMng.setSegNum(segN);
//		frame.setFrameSize(new Integer(segN));
	}

        // 共通のメッセージ表示ダイアログ
        BaseMessageDialog.Instance(frame);

        //validate() はサイズを調整する
        //pack() は有効なサイズ情報をレイアウトなどから取得する
        if (packFrame) {
            frame.pack();
        }
        else {
            frame.validate();
            frame.setVisible(true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void closeFrame() {
        frame.dispose();
        frame = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setVisible(boolean sta) {
        frame.setVisible(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        setVisible(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //Main メソッド
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        AlignmentViewer prog = new AlignmentViewer(args, null);
        prog.setVisible(true);
    }
}
