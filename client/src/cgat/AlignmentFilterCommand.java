package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentFilterCommand implements ActionListener, Observer {
    private Frame frame;
    private MbgdDataMng mbgdDataMng;
    private AlignmentFilterDialog dialog;

    public static final String MSG_ERROR_Filter = "Error in the Filter Specification.";

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentFilterCommand(Frame f, MbgdDataMng dataMng) {
        frame = f;
        dialog = AlignmentFilterDialog.Instance(f);

        _init(dataMng);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(MbgdDataMng dataMng) {
        mbgdDataMng = dataMng;

        setup();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setup() {
        int i;

        dialog.clear();

        String alignAttrName[];
        alignAttrName = new String[Alignment.MaxAttrNum];
        for(i = 0; i < Alignment.MaxAttrNum; i++) {
            alignAttrName[i] = mbgdDataMng.getAlignAttrName(i);
        }
        dialog.setAttrName(alignAttrName);

        dialog.setGuiParts();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        //
        setup();

        // ����������ɽ������
        dialog.setVisible(true);

        // ����å����줿�ܥ������
        int sta = dialog.getStatus();
        switch (sta) {
        case BaseFilterDialog.STA_CANCEL:
            // Cancel ---> �Ȥ��ˤʤˤ⤷�ʤ�
            return;
        case BaseFilterDialog.STA_CLEAR:
            // Clear ---> �ե��륿��󥰥ǡ����򥯥ꥢ
            mbgdDataMng.clearFilterAlignment();
            frame.repaint();
            return;
        case BaseFilterDialog.STA_CLEAR_ALL:
            mbgdDataMng.clearFilterAll();
            break;
        case BaseFilterDialog.STA_FILTER:
            break;
        }

        // ����̾
        int idxAlignItem = dialog.getSelectedAlignItemIndex();
        String nameAlignItem = dialog.getSelectedAlignItem();

        // ���
        int idxCond = dialog.getSelectedConditionIndex();
        String nameCond = dialog.getSelectedCondition();

        int idxSel = dialog.getSelectedTypeIndex();
        String nameSel = "";
        String text = dialog.getInputTextValue().trim();
        double val1 = 0;
        double val2 = 0;
        String str1 = "";
        if (BaseFilterDialog.NAM_SELECT.equals(nameCond)) {
            // ����
            if (0 <= idxSel) {
                nameSel = dialog.getSelectedType();
            }
        }
        else {
            // �ϰ�
            text = dialog.getInputTextValue().trim();
            val1 = 0;
            val2 = 0;
            str1 = "";
            StringTokenizer st = new StringTokenizer(text, ",");
            if (st.countTokens() == 0) {
                // ̤����
                return;
            }
            else {
                try {
                    str1 = st.nextToken();
                    val1 = Double.valueOf(str1).doubleValue();
                }
                catch (Exception e1) {
                    if (! SegmentFilterDialog.NAM_EQUAL.equals(nameCond) &&
                        ! SegmentFilterDialog.NAM_REGEX.equals(nameCond)) {
                        // ���ϥե����ޥåȥ��顼
                        // ���顼��å�����ɽ��
                        BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                        msgDialog.message(MSG_ERROR_Filter);
                        return;
                    }
                    else {
                        // equal Ƚ�̤ξ��ϡ����ͤǤϤʤ�ʸ����Ȥ���Ƚ�̲�ǽ�ʤ���
                    }
                }
                try {
                    if (SegmentFilterDialog.NAM_BETWEEN.equals(nameCond) ||
                        SegmentFilterDialog.NAM_EXPTBETWEEN.equals(nameCond)) {
                        // * between �黻��
                        val2 = Double.valueOf(st.nextToken()).doubleValue();
                    }
                }
                catch (Exception e2) {
                    // ���ϥե����ޥåȥ��顼
                    // ���顼��å�����ɽ��
                    BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                    msgDialog.message(MSG_ERROR_Filter);
                    return;
                }
            }
        }

        // ���˹�碌�ƥե��륿��󥰤���
        if (BaseFilterDialog.NAM_BETWEEN.equals(nameCond) ||
            BaseFilterDialog.NAM_EXPTBETWEEN.equals(nameCond)) {
            mbgdDataMng.filterAlignment(nameCond, nameAlignItem, val1, val2);
        }
        else if (BaseFilterDialog.NAM_LESSTHAN.equals(nameCond) ||
            BaseFilterDialog.NAM_LESSEQUAL.equals(nameCond) ||
            BaseFilterDialog.NAM_GREATEREQUAL.equals(nameCond) ||
            BaseFilterDialog.NAM_GREATERTHAN.equals(nameCond)) {
            mbgdDataMng.filterAlignment(nameCond, nameAlignItem, val1);
        }
        else if (BaseFilterDialog.NAM_SELECT.equals(nameCond)) {
            mbgdDataMng.filterAlignment(nameCond, nameAlignItem, nameSel);
        }
        else if (BaseFilterDialog.NAM_REGEX.equals(nameCond)) {
            mbgdDataMng.filterAlignment(nameCond, nameAlignItem, str1);
        }
        else if (BaseFilterDialog.NAM_EQUAL.equals(nameCond)) {
            //
            boolean dataType = mbgdDataMng.getAlignAttrDataType(nameAlignItem);
            if (AlignmentList.TYPE_ATTR_STR == dataType) {
                mbgdDataMng.filterAlignment(nameCond, nameAlignItem, str1);
            }
            else {
                mbgdDataMng.filterAlignment(nameCond, nameAlignItem, val1);
            }
        }

        frame.repaint();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(Observable o, Object arg) {
        if (o instanceof MbgdDataMng) {
            update((MbgdDataMng)o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(MbgdDataMng mbgdDataMng, Object arg) {
    }

}
