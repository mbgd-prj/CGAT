package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
// 動的 seg データ取得条件画面
public class SelectSegment extends JPanel {
    protected JComboBox segCB;
    protected SelectSegmentInfo segInfo;

    protected String selectedSegName = "";
    protected String selectedSegCgi  = "";

    ///////////////////////////////////////////////////////////////////////////
    //
    public SelectSegment(JFrame f, SelectSegmentInfo info) {
        super();

        segInfo = info;
        _init();

        // 項目変更時の処理
        segCB.addActionListener(new SelectSegmentAction(f, segInfo, this));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        setLayout(new BorderLayout());
        segCB = new JComboBox();
        add(segCB, BorderLayout.NORTH);

        updateSegName();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updateSegName() {
        segCB.removeAllItems();
        for(int i = 0; i < segInfo.size(); i++) {
            String name = segInfo.getName(i);
            if (name == null) {
                continue;
            }
            segCB.addItem(name);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSelectedSegName(String n) {
        selectedSegName = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedSegName() {
        return selectedSegName;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSelectedSegCgi(String cgi) {
        if (cgi == null) {
            cgi = "";
        }
        selectedSegCgi = cgi;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedSegCgi() {
        return selectedSegCgi;
    }

}
