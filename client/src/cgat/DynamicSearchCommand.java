package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DynamicSearchCommand implements ActionListener {
    private Frame frame;
    private MbgdDataMng dataMng;
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DynamicSearchCommand(Frame f, MbgdDataMng dmng, ViewWindow vWin) {
        frame = f;
        dataMng = dmng;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        DynamicSearchBaseDialog baseWin;
        DynamicSearchBaseDialog win;
        String dynSrchType;
        String cgiPath;

        String url = dataMng.getBasePath();

        // 検索種別画面を作成
        baseWin = new DynamicSearchBaseDialog(frame, url, "/dynSearch/getGuiInfo.cgi?type=top");
        baseWin.setSize(400, 300);

        // 表示
        baseWin.setVisible(true);

        // 検索種別取得
        dynSrchType = baseWin.getSearchType();
        cgiPath = baseWin.getCgiPath();
        if (cgiPath == null) {
            // 検索種別が選択されなかった
            return;
        }

        // 検索詳細画面を作成
        win = new DynamicSearchBaseDialog(frame, url, cgiPath);
        win.setSize(400, 300);

        // 検索詳細画面を表示
        win.setVisible(true);

        // 検索種別取得
        cgiPath = win.getCgiPath();
        if (cgiPath == null) {
            // 検索種別が選択されなかった
            return;
        }

        // 一覧画面を作成
        SearchOrfList srchResWin = new SearchOrfList("Search results");

        // 検索結果表示位置
        int maxSegNum = dataMng.getMaxSegNum();
        int segNum = dataMng.getSegNum();
        int segIdx = segNum - 1;
        if (segNum == 0) {
            // segment データ表示領域を追加
            segIdx = 0;
            segNum++;
            dataMng.setSegNum(segNum);
            dataMng.setSegmentName(segIdx, dynSrchType);
        }
        else if (segNum < maxSegNum) {
            segIdx = segNum;
            segNum++;
            dataMng.setSegNum(segNum);
            dataMng.setSegmentName(segIdx, dynSrchType);
        }
        else if (segNum == maxSegNum) {
            segIdx = maxSegNum - 1;
            dataMng.setSegmentName(segIdx, dynSrchType);
        }

        // CGI を実行し、検索結果の取得
        String sp1 = dataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        dataMng.loadSegment0(dataMng.SIDE0, segIdx, null, cgiPath, url+cgiPath);
        Dbg.println(1, "DynamicSearch :: " + url+cgiPath + " :: " + sp1);

        //
        String sp2 = dataMng.getSpecName(MbgdDataMng.OPPO_SPEC);
        dataMng.loadSegment0(dataMng.SIDE1, segIdx, null, cgiPath, url+cgiPath);
        Dbg.println(1, "DynamicSearch :: " + url+cgiPath + " :: " + sp2);


//	((AlignmentViewerFrame) frame).setDrawings(true, false);
	((AlignmentViewerFrame) frame).setFrameSize(segNum);

        //
        viewWin.setDrawMode(viewWin.getDrawMode());

/*
        // カラム名の設定
        String colNam[] = (String[])(ar.get(0));
        for(int i = 0; i < colNam.length; i++) {
            srchResWin.addColumn(colNam[i]);
        }

        // TableHeader のクリックイベントを取得 ---> クリックされた項目で Sort する
        SortTableCommand cmdSort = new SortTableCommand();
        srchResWin.addTableHeadAction(cmdSort);

        RegionListSelection rowSelect = new RegionListSelection(viewWin);
        srchResWin.addTableAction(rowSelect);

        // データの設定
        int loopMax = ar.size();
        for(int i = 1; i < loopMax; i++) {
            Object obj[] = (Object [])ar.get(i);
            srchResWin.addRow(obj);
        }

        //
        srchResWin.setColoredCell(4);

        // 表示
        srchResWin.setVisible(true);
*/

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CGI の実行結果を格納
    //     ar[0] には、カラムタイトルを格納
    public ArrayList execDynSearch(String u) {
        ArrayList ar = new ArrayList();

        try {
            String obj[];
            UrlFile uFile = new UrlFile(u);

            String title[] = null;
            Object dat[];
            for(;;) {
                StringTokenizer st;
                int n;
                int i;
                String buf = uFile.readLine();
                if (buf == null) {
                    break;
                }

                buf = buf.trim();
                if (buf.equals("")) {
                    continue;
                }

                if (buf.startsWith("#")) {
                    if (title == null) {
                        // 項目タイトルを読み込む
                        st = new StringTokenizer(buf.substring(1), "\t");
                        n = st.countTokens();
                        title = new String[n];
                        for(i = 0; i < n; i++) {
                            title[i] = st.nextToken();
                        }
                        ar.add(0, title);
                    }
                    continue;
                }

                // ダウンロードデータ
                st = new StringTokenizer(buf, "\t");
                n = st.countTokens();
                dat = new Object[n];
                for(i = 0; i < 1; i++) {    // spec
                    dat[i] = st.nextToken();
                }
                for(; i < 3; i++) {         // from, to
                    dat[i] = new Integer(st.nextToken());
                }
                for(; i < 4; i++) {         // dir
                    dat[i] = st.nextToken();
                }
                for(; i < 5; i++) {         // color
                    String wk = st.nextToken();
                    int c = 0;
                    if (wk.startsWith("#")) {
                        c = Integer.parseInt(wk.substring(1), 16);
                    }

                    dat[i] = new Color(c);
                }
                for(; i < n; i++) {         // name, ...
                    dat[i] = st.nextToken();
                }
                ar.add(dat);
            }
        }
        catch (Exception e) {
            Dbg.println(1, "ERROR :: "+e);
        }

        return ar;
    }

}
