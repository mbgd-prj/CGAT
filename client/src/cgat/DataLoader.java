package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DataLoader extends Observable implements Runnable {
    private MbgdDataMng mbgdDataMng;

    private String infoSpName1;
    private String infoSpName2;
    private String infoUrl;
    private String infoAlignFile;
    private String infoGeneAttrDir;
    private String infoGeneAttrColorType;
    private String infoSegType[];
    private String infoSegCgi[];

    private JFrame frame;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DataLoader(JFrame f, MbgdDataMng dataMng) {
        frame = f;
        mbgdDataMng = dataMng;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void run() {
        boolean sta;

        // マウスカーソルを≪砂時計≫に変更
        Cursor oldCursor = frame.getCursor();
        Cursor newCursor = new Cursor(Cursor.WAIT_CURSOR);
        frame.setCursor(newCursor);

        // データの読み込み
        mbgdDataMng.execLoad(infoSpName1, infoSpName2,
                                infoUrl,
                                infoAlignFile,
                                infoGeneAttrDir,
                                infoGeneAttrColorType,
                                infoSegType,
                                infoSegCgi);

        //
        frame.setCursor(oldCursor);

        // データの load 終了を通知
        setChanged();
        notifyObservers();

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void load() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setInfo(String sp1, String sp2,
                        String url,
                        String falign,
                        String geneAttrDir, String geneAttrColorType,
                        String segType[],
                        String segCgi[]) {
        infoSpName1             = sp1;
        infoSpName2             = sp2;
        infoUrl                 = url;
        infoAlignFile           = falign;
        infoGeneAttrDir         = geneAttrDir;
        infoGeneAttrColorType   = geneAttrColorType;
        infoSegType             = segType;
        infoSegCgi              = segCgi;
    }

}
