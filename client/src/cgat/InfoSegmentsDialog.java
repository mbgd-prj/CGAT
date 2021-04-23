package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class InfoSegmentsDialog {
    InfoSegments infoSegments;
    String seg1Dir;
    String seg2Dir;

    ///////////////////////////////////////////////////////////////////////////
    //
    public InfoSegmentsDialog(JFrame frame, InfoSegments infoSeg) {
        infoSegments = infoSeg;

        String [] segList;
        segList = new String[infoSegments.size() + 1];
        segList[0] = new String("### no data ###");
        for(int i = 0; i < infoSegments.size(); i++) {
            segList[i + 1] = infoSegments.getName(i);
        }

        //
        JComboBox seg1 = new JComboBox(segList);
        if (1 <= infoSegments.size()) {
            seg1.setSelectedIndex(1);
        }
        else {
            seg1.setSelectedIndex(0);
        }

        //
        JComboBox seg2 = new JComboBox(segList);
        if (2 <= infoSegments.size()) {
            seg2.setSelectedIndex(2);
        }
        else {
            seg2.setSelectedIndex(0);
        }

        Object[] msg = {"Select SPEC", ""};

        Object[] opt = {"Seg1", seg1, "", "Seg2", seg2};

        // セグメント情報選択ダイアログ表示
        int ret = JOptionPane.showConfirmDialog(  frame.getContentPane(),
                                                opt,
                                                "Select species pair",
                                                JOptionPane.OK_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        seg1Dir = null;
        seg2Dir = null;
        if (ret == 0) {
            int idx;

            // Seg-1
            idx = seg1.getSelectedIndex();
            if (idx == 0) {
                seg1Dir = null;
            }
            else {
                seg1Dir = infoSegments.getDir(idx - 1);
            }

            // Seg-2
            idx = seg2.getSelectedIndex();
            if (idx == 0) {
                seg2Dir = null;
            }
            else {
                seg2Dir = infoSegments.getDir(idx - 1);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSeg1Dir() {
        return(seg1Dir);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSeg2Dir() {
        return(seg2Dir);
    }


}
