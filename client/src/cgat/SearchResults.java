
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

///////////////////////////////////////////////////////////////////////////////
//
public class SearchResults extends JTable {
    private RegionListTableModel regionTableModel;

    // テーブル項目名
    public static final String TAB_ITEM_SPEC  = "spec";
    public static final String TAB_ITEM_FROM  = "from";
    public static final String TAB_ITEM_TO    = "to";
    public static final String TAB_ITEM_DIR   = "dir";
    public static final String TAB_ITEM_COLOR = "Color";
    public static final String TAB_ITEM_NAME  = "name";

    ///////////////////////////////////////////////////////////////////////////
    //
    public SearchResults(ViewWindow viewWin) {
        super();

        //
        regionTableModel = new RegionListTableModel();
        setModel(regionTableModel);

        setRowHeight(20);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        addMouseListener(new RegionListSelection(viewWin));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearRows() {
        // ROW を 0 件とする ---> 全データを破棄する
        regionTableModel.setRowCount(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColumnName(Object [] dat) {
        for(int i = 0; i < dat.length; i++) {
            regionTableModel.addColumn(dat[i]);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addRow(Object [] dat) {
        regionTableModel.addRow(dat);
    }

}
