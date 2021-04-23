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
// Search ORF �¹Է�̤�ɽ������ Frame
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


        // ������̰���ɽ�� Frame ����
        frame = new JFrame();
        frame.setTitle("ORF name");

        orfTab = new SearchResults(viewWin);
        orfTab.setColumnName(colNames);

        //
        JTableHeader tabHeader;
        tabHeader = orfTab.getTableHeader();
        tabHeader.setUpdateTableInRealTime(true);   // Java 2 1.3 ���ѻߤ��줿
        tabHeader.setReorderingAllowed(false);      // �����ΰ�ư����Ĥ��ʤ�

        // TableHeader �Υ���å����٥�Ȥ���� ---> ����å����줿���ܤ� Sort ����
        SortTableCommand cmdSort = new SortTableCommand();
        cmdSort.setTable(orfTab);
        tabHeader.addMouseListener(cmdSort);

        //
        JScrollPane scrollPane = new JScrollPane(orfTab);
        frame.getContentPane().add(scrollPane);

        frame.setSize(400, 300);

        // ������֤ϡ���ɽ��
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
