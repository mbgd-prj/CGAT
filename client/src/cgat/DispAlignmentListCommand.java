package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispAlignmentListCommand implements ActionListener {
    private int dataType;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;


    JFrame frameAlignmentList = null;
    String spName = null;
    int from = 0;
    int to   = 0;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispAlignmentListCommand(int type, MbgdDataMng dataMng, ViewWindow vWin) {
        dataType = type;

        mbgdDataMng = dataMng;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegion(String s, int f, int t) {
        spName = s;
        from   = f;
        to     = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        ArrayList selectedAlign;

        // 中央寄せ
        DefaultTableCellRenderer alignCenter = new AlignTableCellRenderer(SwingConstants.CENTER);

        // 該当データを検索
        selectedAlign = mbgdDataMng.getAlignment(dataType, viewWin, from, to);

        //
        DispAlignmentList dispAlignmentList = new DispAlignmentList(viewWin);

        // dir は、中央寄せ
        dispAlignmentList.getColumnModel().getColumn(4).setCellRenderer(alignCenter);

        // 表示対象データを JTable に登録
        for(int i = 0; i < selectedAlign.size(); i++) {
            Object [] dat = new Object[7];

            // Object にデータをセット
            Alignment a = (Alignment)selectedAlign.get(i);
            int idx = 0;
//            dat[idx++] = spName;
            dat[idx++] = new Integer(a.getFrom1());
            dat[idx++] = new Integer(a.getTo1());
            dat[idx++] = new Integer(a.getFrom2());
            dat[idx++] = new Integer(a.getTo2());
            if (0 < a.getDir()) {
                dat[idx++] = "+";
            }
            else {
                dat[idx++] = "-";
            }
            dat[idx++] = new Float(a.getIdent());
            dat[idx++] = new Float(a.getScore());

            dispAlignmentList.addRow(dat);
        }

        //
        JScrollPane scrollPane = new JScrollPane(dispAlignmentList);

        if (frameAlignmentList == null) {
            frameAlignmentList = new JFrame("Alignment list");
        }
        frameAlignmentList.getContentPane().add(scrollPane);
        frameAlignmentList.setSize(500, 300);
        frameAlignmentList.setVisible(true);

//        dispAlignmentList.show();
    }

}
