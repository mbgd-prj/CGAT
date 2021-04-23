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
public class DispSegmentDataTable implements ActionListener {
    private Frame frame;
    private MbgdDataMng mbgdDataMng;
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispSegmentDataTable(Frame f, MbgdDataMng dataMng, ViewWindow vWin) {
        frame = f;
        mbgdDataMng = dataMng;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        int idx;

        // 表示対象選択ダイアログ表示
        String spName = "";
        String segName = "";
        ButtonGroup  bg  = new ButtonGroup();
        JRadioButton rb[] = new JRadioButton[(1 + maxSegNum) * 2];

        for(int i = 0; i < rb.length; i++) {
            rb[i] = new JRadioButton();
            bg.add(rb[i]);
        }

        idx = 0;

        // Gene
        segName = "Gene";
        spName  = mbgdDataMng.getSpecName(true);
        rb[idx].setSelected(true);
        rb[idx].setText(segName + " : " + spName);
        if (spName.equals("")) {
            rb[idx].setEnabled(false);
        }
        idx++;

        // Seg
        for(int i = 0; i < maxSegNum; i++) {
            segName = mbgdDataMng.getSegmentName(i);
            spName  = mbgdDataMng.getSpecName(true);
            rb[idx].setText(segName + " : " + spName);
            if (segName.equals("")) {
                rb[idx].setEnabled(false);
            }
            idx++;
        }

        // Gene
        segName = "Gene";
        spName  = mbgdDataMng.getSpecName(false);
        rb[idx].setText(segName + " : " + spName);
        if (spName.equals("")) {
            rb[idx].setEnabled(false);
        }
        idx++;

        // Seg
        for(int i = 0; i < maxSegNum; i++) {
            segName = mbgdDataMng.getSegmentName(i);
            spName  = mbgdDataMng.getSpecName(false);
            rb[idx].setText(segName + " : " + spName);
            if (segName.equals("")) {
                rb[idx].setEnabled(false);
            }
            idx++;
        }

        int sta = JOptionPane.showConfirmDialog(frame,
                                                rb,
                                                "",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (sta != 0) {
            return;
        }

        // 選択された Segment データを表示
        int type[] = new int[(1 + maxSegNum) * 2];

        idx = 0;
        type[idx++] = MbgdDataMng.BASE_GENE;
        for(int i = 0; i < maxSegNum; i++) {
            type[idx++] = MbgdDataMng.BASE_SEG1 + i;
        }
        type[idx++] = MbgdDataMng.OPPO_GENE;
        for(int i = 0; i < maxSegNum; i++) {
            type[idx++] = MbgdDataMng.OPPO_SEG1 + i;
        }

        for(int i = 0; i < rb.length; i++) {
            if (rb[i].isSelected()) {
                dispTabFrame(type[i], rb[i].getText());
                break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void dispTabFrame(int type, String title) {
        int loopMax;
        int attrIdx;

        // 一覧画面を作成
        SearchOrfList srchResWin = new SearchOrfList(title);

        // カラム名の設定
        srchResWin.addColumn("Spec");
        srchResWin.addColumn("From");
        srchResWin.addColumn("To");
        srchResWin.addColumn("Dir");
        srchResWin.addColumn("Color");

        //
        int nGeneAttr = 0;
        nGeneAttr += mbgdDataMng.getGeneAttrSize(MbgdDataMng.BASE_GENE);
        nGeneAttr += mbgdDataMng.getGeneAttrSize(MbgdDataMng.OPPO_GENE);
        if (nGeneAttr != 0) {
            srchResWin.addColumn("Gene Attr");
        }
        String specName = mbgdDataMng.getSpecName(type);
        int segNo = type % 10 - MbgdDataMng.BASE_SEG1;

        for(attrIdx = 0; attrIdx < RegionInfo.maxAttrNum; attrIdx++) {
            String attrName = mbgdDataMng.getSegAttrName(type, segNo, attrIdx);
            if ((attrName == null) || attrName.equals("")) {
                break;
            }
            srchResWin.addColumn(attrName);
        }
        int nObj = 6 + attrIdx;
        if (nGeneAttr != 0) {
            nObj++;
        }

        // TableHeader のクリックイベントを取得 ---> クリックされた項目で Sort する
        SortTableCommand cmdSort = new SortTableCommand();
        srchResWin.addTableHeadAction(cmdSort);

        RegionListSelection rowSelect = new RegionListSelection(viewWin);
        srchResWin.addTableAction(rowSelect);

        //
        RegionInfoList regInfoList = mbgdDataMng.getRegionInfoList(type);
        if (regInfoList == null) {
            return;
        }
        int idxName    = regInfoList.getAttrIndex(RegionInfoList.ATTR_name);

        // テーブルの表示データ長にあわせカラム幅を変化させる
        int lenContent[] = new int[nObj];
        for(int ii = 0; ii < nObj; ii++) {
            lenContent[ii] = 0;
        }

        // データの設定
        String colorType = mbgdDataMng.getColorType(type);
        loopMax = mbgdDataMng.getRegionSize(type);
        for(int i = 0; i < loopMax; i++) {
            Object obj[] = new Object[nObj];
            RegionInfo r = null;
            String color = "white";
            String valGeneAttr = "";
            switch (type) {
            case MbgdDataMng.BASE_GENE:
                r = mbgdDataMng.getGeneInfo(true, i);
                valGeneAttr = r.getAttr(idxName);
                valGeneAttr = color = mbgdDataMng.getGeneAttr(type, valGeneAttr);
                if (color == null) {
                    color = r.getColor();
                    valGeneAttr = "";
                }
                break;
            case MbgdDataMng.OPPO_GENE:
                r = mbgdDataMng.getGeneInfo(false, i);
                valGeneAttr = r.getAttr(idxName);
                valGeneAttr = color = mbgdDataMng.getGeneAttr(type, valGeneAttr);
                if (color == null) {
                    color = r.getColor();
                    valGeneAttr = "";
                }
                break;
            case MbgdDataMng.BASE_SEG1:
            case MbgdDataMng.OPPO_SEG1:
            case MbgdDataMng.BASE_SEG2:
            case MbgdDataMng.OPPO_SEG2:
            case MbgdDataMng.BASE_SEG3:
            case MbgdDataMng.OPPO_SEG3:
            case MbgdDataMng.BASE_SEG4:
            case MbgdDataMng.OPPO_SEG4:
            case MbgdDataMng.BASE_SEG5:
            case MbgdDataMng.OPPO_SEG5:
                r = mbgdDataMng.getSegmentInfo(type, i);
                color = r.getColor();
                break;
            }
            if (r == null) {
                continue;
            }
            if (! r.getFilter()) {
                continue;
            }

            int idx = 0;
            obj[idx++] = specName;
            obj[idx++] = new Integer(r.getFrom());
            obj[idx++] = new Integer(r.getTo());
            if (r.getDir() == 1) {
                obj[idx++] = "+";
            }
            else {
                obj[idx++] = "-";
            }

            //
            Color c;
            if (color.startsWith("#")) {
                c = new Color(Integer.parseInt(color.substring(1), 16));
            }
            else {
                c = mbgdDataMng.getColor(type, color, colorType);
            }
            obj[idx++] = c;

            //
            if (nGeneAttr != 0) {
                if (! "".equals(valGeneAttr)) {
                    obj[idx++] = Integer.valueOf(valGeneAttr);
                }
                else {
                    obj[idx++] = null;
                }
            }

            for(int j = 0; j < attrIdx; j++) {
                obj[idx++] = r.getAttr(j);
            }

            srchResWin.addRow(obj);

            //
            for(int ii = 0; ii < nObj; ii++) {
                if (obj[ii] == null) {
                    continue;
                }
                else if (obj[ii] instanceof Color) {
                    lenContent[ii] = 1;
                    continue;
                }

                int len = obj[ii].toString().length();
                if (lenContent[ii] < len) {
                    lenContent[ii] = len;
                }
            }
        }

        int w = 0;
        for(int ii = 0; ii < nObj; ii++) {
            if ((loopMax != 0) && (lenContent[ii] <= 0)) {
            }
            else if (lenContent[ii] <= 5) {
                w += 50;
                lenContent[ii] = 50;
            }
            else if (lenContent[ii] <= 10) {
                w += 100;
                lenContent[ii] = 100;
            }
            else if (lenContent[ii] <= 15) {
                w += 150;
                lenContent[ii] = 150;
            }
            else {
                w += 200;
                lenContent[ii] = 200;
            }
        }

        //
        srchResWin.setColoredCell(4);

        //
        int idx = 0;
        srchResWin.setColumnWidth(idx, lenContent[idx]); idx++;  // spec
        srchResWin.setColumnWidth(idx, lenContent[idx]); idx++;  // from
        srchResWin.setColumnWidth(idx, lenContent[idx]); idx++;  // to
        srchResWin.setColumnWidth(idx, lenContent[idx]); idx++;  // dir
        srchResWin.setColumnWidth(idx, lenContent[idx]); idx++;  // color
        for(int j = 0; j < attrIdx; j++) {
            srchResWin.setColumnWidth(idx, lenContent[idx]); idx++;
        }
        Dimension dim = srchResWin.getSize();
        srchResWin.setSize(w, dim.height);

        // 表示
        srchResWin.setVisible(true);

        return;
    }

}
