package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
//  ActionListener  : メニューから選択されたときの処理
//  Observer        : data load の通知を受けて、ダイアログ表示メッセージを更新する
public class SegmentFilterCommand implements ActionListener, Observer {
    private Frame frame;
    private MbgdDataMng mbgdDataMng;
    private SegmentFilterDialog dialog;

    public static final String MSG_ERROR_Filter = "Error in the Filter Specification.";

    ///////////////////////////////////////////////////////////////////////////
    //
    public SegmentFilterCommand(Frame f, MbgdDataMng dataMng) {
        frame = f;
        dialog = SegmentFilterDialog.Instance(f);

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
        int i, j;
        int n;

        dialog.clear();

        //
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        String segName[] = new String[1 + maxSegNum];


        // セグメント名称
        segName[0] = "Gene";
        for(i = 0; i < maxSegNum; i++) {
            segName[i + 1] = mbgdDataMng.getSegmentName(i);
        }
        dialog.setSegmentName(segName);

        // Gene Item Name
        String segItemName[];
        segItemName = new String[RegionInfo.maxAttrNum];
        for(j = 0; j < RegionInfo.maxAttrNum; j++) {
            segItemName[j] = mbgdDataMng.getGeneAttrName(MbgdDataMng.BASE_GENE, j);
        }
        dialog.setSegmentItem(0, segItemName);

        // seg Item Name
        n = 1;
        for(i = 0; i < maxSegNum; i++) {
            if (segName[i + 1] == null) {
                continue;
            }
            if ("".equals(segName[i + 1])) {
                continue;
            }

            segItemName = new String[RegionInfo.maxAttrNum];
            for(j = 0; j < RegionInfo.maxAttrNum; j++) {
                segItemName[j] = mbgdDataMng.getSegAttrName(MbgdDataMng.BASE_SEG1, i, j);
            }
            dialog.setSegmentItem(n, segItemName);

            n++;
        }

        dialog.setGuiParts();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        int i;

        //
        setup();

        // ダイアログを表示する
        dialog.setVisible(true);

        // Segment 名
        int idxSegment = dialog.getSelectedSegmentIndex();

        // クリックされたボタン種別
        int sta = dialog.getStatus();
        switch (sta) {
        case BaseFilterDialog.STA_CANCEL:
            // Cancel ---> とくになにもしない
            return;
        case BaseFilterDialog.STA_CLEAR:
            // Clear ---> フィルタリングデータをクリア
            mbgdDataMng.clearFilterGeneSegment(idxSegment);
            frame.repaint();
            return;
        case BaseFilterDialog.STA_CLEAR_ALL:
            mbgdDataMng.clearFilterAll();
            break;
        case BaseFilterDialog.STA_FILTER:
            break;
        }

        // 項目名
        int idxSegmentItem = dialog.getSelectedSegmentItemIndex();
        String nameSegmentItem = dialog.getSelectedSegmentItem();

        // 条件
        int idxCond = dialog.getSelectedConditionIndex();
        String nameCond = dialog.getSelectedCondition();

        // 範囲
        String text = dialog.getInputTextValue().trim();
        double val1 = 0;
        double val2 = 0;
        String str1 = "";
        StringTokenizer st = new StringTokenizer(text, ",");
        if (st.countTokens() == 0) {
            // 未入力
            return;
        }
        else {
            try {
                str1 = st.nextToken();
                val1 = Double.valueOf(str1).doubleValue();
            }
            catch (Exception e1) {
                // 数値変換に失敗
                if (! SegmentFilterDialog.NAM_REGEX.equals(nameCond) &&
                    ! SegmentFilterDialog.NAM_EQUAL.equals(nameCond)) {
                    // エラーメッセージ表示
                    BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                    msgDialog.message(MSG_ERROR_Filter);
                    return;
                }
                else {
                    // equal 判別の場合は、数値ではなく文字列として判別可能
                    // regex 判別の場合は、数値ではなく文字列として判別可能
                }
            }
            try {
                if (SegmentFilterDialog.NAM_BETWEEN.equals(nameCond) ||
                    SegmentFilterDialog.NAM_EXPTBETWEEN.equals(nameCond)) {
                    // * between 演算子
                    val2 = Double.valueOf(st.nextToken()).doubleValue();
                }
            }
            catch (Exception e2) {
                // 入力フォーマットエラー
                // エラーメッセージ表示
                BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                msgDialog.message(MSG_ERROR_Filter);
                return;
            }
        }

        // 条件に合わせてフィルタリングする
        if (BaseFilterDialog.NAM_BETWEEN.equals(nameCond) ||
            BaseFilterDialog.NAM_EXPTBETWEEN.equals(nameCond)) {
            mbgdDataMng.filterGeneSegment(nameCond, idxSegment, idxSegmentItem, val1, val2);
        }
        else if (BaseFilterDialog.NAM_LESSTHAN.equals(nameCond) ||
                 BaseFilterDialog.NAM_LESSEQUAL.equals(nameCond) ||
                 BaseFilterDialog.NAM_GREATEREQUAL.equals(nameCond) ||
                 BaseFilterDialog.NAM_GREATERTHAN.equals(nameCond)) {
            mbgdDataMng.filterGeneSegment(nameCond, idxSegment, idxSegmentItem, val1);
        }
        else if (BaseFilterDialog.NAM_REGEX.equals(nameCond)) {
            mbgdDataMng.filterGeneSegment(nameCond, idxSegment, idxSegmentItem, str1);
        }
        else if (BaseFilterDialog.NAM_EQUAL.equals(nameCond)) {
            //
            boolean attrType;
            if (idxSegment == 0) {
                attrType = mbgdDataMng.getGeneAttrDataType(idxSegmentItem);
            }
            else {
                attrType = mbgdDataMng.getSegAttrDataType(idxSegment - 1, idxSegmentItem);
            }
            if (attrType != RegionInfoList.TYPE_ATTR_STR) {
                mbgdDataMng.filterGeneSegment(nameCond, idxSegment, idxSegmentItem, val1);
            }
            else {
                mbgdDataMng.filterGeneSegment(nameCond, idxSegment, idxSegmentItem, str1);
            }
        }

        frame.repaint();
        return;
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
