package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import javax.swing.*;
import javax.swing.table.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispAlignmentList extends JTable {
    AlignmentListTableModel alignTableModel;

    // テーブル項目名
//    static String TAB_ITEM_NAME  = "Spec name";
    static String TAB_ITEM_FROM1 = "from1";
    static String TAB_ITEM_TO1   = "to1";
    static String TAB_ITEM_FROM2 = "from2";
    static String TAB_ITEM_TO2   = "to2";
    static String TAB_ITEM_DIR   = "dir";
    static String TAB_ITEM_IDENT = "ident";
    static String TAB_ITEM_SCORE = "score";

Object [] colNames = {  //TAB_ITEM_NAME,
                        TAB_ITEM_FROM1, TAB_ITEM_TO1,
                        TAB_ITEM_FROM2, TAB_ITEM_TO2,
                        TAB_ITEM_DIR,
                        TAB_ITEM_IDENT,
                        TAB_ITEM_SCORE };

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispAlignmentList(ViewWindow viewWin) {
        super();

        //
        alignTableModel = new AlignmentListTableModel(colNames, 0);
        setModel(alignTableModel);

        JTableHeader tabHeader;
        tabHeader = getTableHeader();
        tabHeader.setUpdateTableInRealTime(true);   // Java 2 1.3 で廃止された
        tabHeader.setReorderingAllowed(false);      // カラムの移動を許可しない

        // TableHeader のクリックイベントを取得 ---> クリックされた項目で Sort する
        SortTableCommand cmdSort = new SortTableCommand();
        cmdSort.setTable(this);
        tabHeader.addMouseListener(cmdSort);

        setRowHeight(20);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        addMouseListener(new AlignmentListSelection(viewWin));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        for(int i = 0; i < alignTableModel.getRowCount(); i++) {
            alignTableModel.removeRow(0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addRow(Object [] dat) {
        alignTableModel.addRow(dat);
    }

}
