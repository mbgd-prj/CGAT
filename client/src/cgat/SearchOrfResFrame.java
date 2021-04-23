package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// Search ORF 実行結果を表示する Frame
public class SearchOrfResFrame {
    private static SearchOrfResFrame _instance = null;

    private ViewWindow viewWin;
    private JFrame frame;
    private SearchResults orfTab;

    ///////////////////////////////////////////////////////////////////////////
    //
    public static SearchOrfResFrame Instance(ViewWindow vWin) {
        if (_instance == null) {
            _instance = new SearchOrfResFrame(vWin);
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static SearchOrfResFrame Instance() {
        if (_instance == null) {
            Dbg.println(0, "System internal error at SearchOrfResFrame::Instance()");
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public SearchOrfResFrame(ViewWindow vWin) {
        _init(vWin);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(ViewWindow vWin) {
        viewWin = vWin;
        Object [] colNames = {  SearchResults.TAB_ITEM_SPEC,
                                SearchResults.TAB_ITEM_FROM,
                                SearchResults.TAB_ITEM_TO,
                                SearchResults.TAB_ITEM_DIR,
                                SearchResults.TAB_ITEM_COLOR,
                                SearchResults.TAB_ITEM_NAME };


        // 検索結果一覧表示 Frame 作成
        frame = new JFrame();
        frame.setTitle("ORF name");

        orfTab = new SearchResults(viewWin);
        orfTab.setColumnName(colNames);

        //
        JTableHeader tabHeader;
        tabHeader = orfTab.getTableHeader();
        tabHeader.setUpdateTableInRealTime(true);   // Java 2 1.3 で廃止された
        tabHeader.setReorderingAllowed(false);      // カラムの移動を許可しない

        // TableHeader のクリックイベントを取得 ---> クリックされた項目で Sort する
        SortTableCommand cmdSort = new SortTableCommand();
        cmdSort.setTable(orfTab);
        tabHeader.addMouseListener(cmdSort);

        //
        JScrollPane scrollPane = new JScrollPane(orfTab);
        frame.getContentPane().add(scrollPane);

        frame.setSize(400, 300);

        // 初期状態は、非表示
        setVisible(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setVisible(boolean sta) {
        frame.setVisible(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        orfTab.clearRows();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addRow(Object dat[]) {
        orfTab.addRow(dat);
    }

}
