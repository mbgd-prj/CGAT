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
//
public class SearchOrfList extends JFrame {
    // テーブル項目名
    public static final String TAB_ITEM_SPEC  = "organism";
    public static final String TAB_ITEM_NAME  = "name";
    public static final String TAB_ITEM_FROM  = "from";
    public static final String TAB_ITEM_TO    = "to";
    public static final String TAB_ITEM_DIR   = "dir";

    private JScrollPane scrollPane;
    private JTable resTab;
    private BaseTableModel tabModel;

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColoredCell(int idx) {
        TableColumn col = resTab.getColumnModel().getColumn(idx);
        col.setCellRenderer(new ColoredTableCellRenderer());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public SearchOrfList(String title) {
        _init(title);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(String title) {
        setTitle(title);

        // 結果を格納する Table
        resTab = new JTable();
        tabModel = new BaseTableModel();
        resTab.setModel(tabModel);
        resTab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        //
        scrollPane = new JScrollPane(resTab);
        getContentPane().add(scrollPane);

        setSize(700, 300);

        // 初期状態は、非表示
        setVisible(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Table カラムタイトルの追加
    public void addColumn(Object colName) {
        tabModel.addColumn(colName);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColumnWidth(int idx, int width) {
        TableColumnModel tcm = resTab.getColumnModel();
        TableColumn tc = tcm.getColumn(idx);
        tc.setPreferredWidth(width);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setReorderingAllowed(boolean sta) {
        //
        JTableHeader tabHeader;
        tabHeader = resTab.getTableHeader();
        tabHeader.setReorderingAllowed(sta);      // カラムの移動を許可設定
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        tabModel.setRowCount(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addRow(Object dat[]) {
        tabModel.addRow(dat);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addTableHeadAction(SortTableCommand act) {
        act.setTable(resTab);
        resTab.getTableHeader().addMouseListener(act);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addTableAction(MouseListener act) {
        resTab.addMouseListener(act);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSize(int w, int h) {
        // スクロールバーの幅を考慮して Window サイズを設定する
        Dimension dim = scrollPane.getVerticalScrollBar().getPreferredSize();
        super.setSize(w + dim.width, h);
    }

}
