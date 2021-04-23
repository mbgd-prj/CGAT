
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class SelectSpecPairCommand extends Observable implements ActionListener {
    private JFrame frame;
    private MbgdDataMng mbgdDataMng;

    private boolean isDispSegs[];

    Cursor bakCursor;
    ProceedDialog proceedDialog = null;

    ///////////////////////////////////////////////////////////////////////////
    //
    public SelectSpecPairCommand(JFrame f, MbgdDataMng dataMng) {
        frame = f;
        mbgdDataMng = dataMng;

        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        int segN = MbgdDataMng.MAX_SEGS;
        isDispSegs = new boolean[segN];
        for(int i = 0; i < segN; i++) {
            isDispSegs[i] = true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isDispSegs(int idx) {
        return isDispSegs[idx];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        // 生物種選択ダイアログ表示
        SelectSpecPairDialog dialog = SelectSpecPairDialog.Instance(frame);

        dialog.showDialog();
        if (dialog.getStaSelect() == true) {
            // OK ボタンが押された
            // データロードに必要となる情報を取得
            HashMap specInfo = dialog.getSelectSpecInfo();

            //
            String url = (String)specInfo.get("PATH");

            // 生物種名
            String spec1 = (String)specInfo.get("SPEC1");
            String spec2 = (String)specInfo.get("SPEC2");

            //
            String fileAlign = (String)specInfo.get("FILE");

            // GeneAttr
            String geneAttr = dialog.getSelectGeneAttr();
            String geneColorType = dialog.getSelectGeneColorType();

            // Segment
            int maxSegNum = mbgdDataMng.getMaxSegNum();     // MAX セグメント数
            int segN = maxSegNum;
            String segDir[];
            String segCgi[];
            segDir = new String[maxSegNum];
            segCgi = new String[maxSegNum];

            for(int i = 0; i < maxSegNum; i++) {
                segDir[i] = dialog.getSelectSegType(i);
                segCgi[i] = dialog.getSelectSegCgi(i);
                isDispSegs[i] = true;
                if (segCgi[i].equals("")) {
                    segN--;
                    isDispSegs[i] = false;
                }
            }

            mbgdDataMng.setSegNum(segN);              // 表示するセグメント数
            for(int i = 0; i < maxSegNum; i++) {
                mbgdDataMng.setSegmentName(i, dialog.getSelectSegName(i));
            }

            // データの読み込み(別 Thread で行われるため、すぐに戻ってくる)
            mbgdDataMng.load(frame, spec1, spec2, url, fileAlign, geneAttr, geneColorType, segDir, segCgi);

            // 表示データの選択が行われた ---> Region 画面に表示する Alignment/Gene/Segs を調整
            setChanged();
            notifyObservers(new Integer(segN));
        }
    }

}
