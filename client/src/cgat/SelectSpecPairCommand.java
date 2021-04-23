
/**
 * �����ȥ�:     cgat<p>
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
        // ��ʪ�������������ɽ��
        SelectSpecPairDialog dialog = SelectSpecPairDialog.Instance(frame);

        dialog.showDialog();
        if (dialog.getStaSelect() == true) {
            // OK �ܥ��󤬲����줿
            // �ǡ������ɤ�ɬ�פȤʤ��������
            HashMap specInfo = dialog.getSelectSpecInfo();

            //
            String url = (String)specInfo.get("PATH");

            // ��ʪ��̾
            String spec1 = (String)specInfo.get("SPEC1");
            String spec2 = (String)specInfo.get("SPEC2");

            //
            String fileAlign = (String)specInfo.get("FILE");

            // GeneAttr
            String geneAttr = dialog.getSelectGeneAttr();
            String geneColorType = dialog.getSelectGeneColorType();

            // Segment
            int maxSegNum = mbgdDataMng.getMaxSegNum();     // MAX �������ȿ�
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

            mbgdDataMng.setSegNum(segN);              // ɽ�����륻�����ȿ�
            for(int i = 0; i < maxSegNum; i++) {
                mbgdDataMng.setSegmentName(i, dialog.getSelectSegName(i));
            }

            // �ǡ������ɤ߹���(�� Thread �ǹԤ��뤿�ᡢ��������äƤ���)
            mbgdDataMng.load(frame, spec1, spec2, url, fileAlign, geneAttr, geneColorType, segDir, segCgi);

            // ɽ���ǡ��������򤬹Ԥ�줿 ---> Region ���̤�ɽ������ Alignment/Gene/Segs ��Ĵ��
            setChanged();
            notifyObservers(new Integer(segN));
        }
    }

}
