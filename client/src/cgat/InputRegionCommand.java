package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class InputRegionCommand implements ActionListener {
    private int         dataType;
    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    ///////////////////////////////////////////////////////////////////////
    //
    public InputRegionCommand(int type, MbgdDataMng dataMng, ViewWindow vWin) {
        dataType = type;
        mbgdDataMng = dataMng;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JTextField) {
            JTextField tf = (JTextField)e.getSource();
            int pos;
            try {
                pos = Integer.valueOf(tf.getText()).intValue();
                if ((0 < pos) && (pos <= mbgdDataMng.getGenomeLength(dataType))) {
                    boolean basespec;
                    // 表示領域更新
                    switch (dataType) {
                    case MbgdDataMng.BASE_GENE:
                        basespec = true;
                        viewWin.viewPos(pos);
                        break;
                    default:
                        basespec = false;
                        break;
                    }
                }
            }
            catch (NumberFormatException nfe) {
                // 変換エラー：数字以外が入力された ---> ORF 名として検索
                String orfName = tf.getText().toUpperCase();
                RegionInfo r;
                boolean basespec;

                switch (dataType) {
                case MbgdDataMng.BASE_GENE:
                    basespec = true;
                    r = mbgdDataMng.getGeneInfo(basespec, orfName);
                    if (r != null) {
                        // 該当する ORF が存在する ---> 表示領域更新
                        pos = (r.getFrom() + r.getTo()) / 2;
                        viewWin.viewPos(pos);
                    }
                    break;
                default:
                    basespec = false;
                    r = mbgdDataMng.getGeneInfo(basespec, orfName);
                    if (r != null) {
                        // 該当する ORF が存在する ---> 表示領域更新
                        pos = (r.getFrom() + r.getTo()) / 2;
                        viewWin.viewPos(viewWin.getRegCenter(! basespec), pos);
                    }
                    break;
                }

            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
