package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class SelectSegmentAction implements ActionListener {
    JFrame frame;
    SelectSegmentInfo selSegInfo;
    SelectSegment     selSeg;

    ///////////////////////////////////////////////////////////////////////////
    //
    public SelectSegmentAction(JFrame f, SelectSegmentInfo info, SelectSegment s) {
        frame = f;
        selSegInfo = info;
        selSeg     = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        //
        JComboBox jcb = (JComboBox)e.getSource();
        if (jcb.getSelectedIndex() < 0) {
            return;
        }
        String sel = (String)jcb.getSelectedItem();
//        String type = selSegInfo.getType(sel);
        String type = (String)jcb.getSelectedItem();

        selSeg.setSelectedSegName(sel);

        if (sel.equalsIgnoreCase("No Data")) {
            // データを表示しない場合
            selSeg.setSelectedSegCgi("");
            return;
        }

        String url = MbgdDataMng.Instance().getBasePath();
        String arg = "/cgi-bin/getSegment.cgi?type=" + type;
        selSeg.setSelectedSegCgi(url + arg);

        return;
    }

}
