package cgat;

/**
 * �����ȥ�:  cgat
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
    // �ơ��֥����̾
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

        // ��̤��Ǽ���� Table
        resTab = new JTable();
        tabModel = new BaseTableModel();
        resTab.setModel(tabModel);
        resTab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        //
        scrollPane = new JScrollPane(resTab);
        getContentPane().add(scrollPane);

        setSize(700, 300);

        // ������֤ϡ���ɽ��
        setVisible(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Table ����ॿ���ȥ���ɲ�
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
        tabHeader.setReorderingAllowed(sta);      // �����ΰ�ư���������
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
        // ��������С��������θ���� Window �����������ꤹ��
        Dimension dim = scrollPane.getVerticalScrollBar().getPreferredSize();
        super.setSize(w + dim.width, h);
    }

}
