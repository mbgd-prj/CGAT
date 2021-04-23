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
// �⡼����ʥ������������
public class SegmentFilterDialog extends BaseFilterDialog implements ActionListener {
    private static SegmentFilterDialog _instance = null;

    private JComboBox cbSegName = null;
    private JComboBox cbSegItem = null;
    private JComboBox cbCond    = null;
    private JTextField tfInput  = null;

    private String segmentName[];
    private String segmentItem[][];

    ///////////////////////////////////////////////////////////////////////////
    //
    public static SegmentFilterDialog Instance(Frame f) {
        if (_instance == null) {
            _instance = new SegmentFilterDialog(f, true);
            _instance.setTitle("Filter Gene/Segment");
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static SegmentFilterDialog Instance() {
        if (_instance == null) {
            Dbg.println(0, "Error!! Can not create 'SegmentFilterDialog'");
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private SegmentFilterDialog(Frame f, boolean sta) {
        super(f, sta);
        setVisible(false);
        setSize(500, 100);
//        setResizable(false);
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        setStatus(STA_CANCEL);
        segmentName = null;
        segmentItem = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // GUI ����
    public void setGuiParts() {
        JButton btnClearAll;
        JButton btnClear;
        JButton btnFilter;
        JButton btnCancel;
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());

        // ���٤Ƥ� GUI �����
        pane.removeAll();
        int x = 0;
        int y = 0;

        JPanel pCenter = new JPanel();
        cbSegName   = new JComboBox(segmentName);
        cbSegItem   = new JComboBox(segmentItem[0]);
        cbCond      = new JComboBox(CondValList);
        tfInput     = new JTextField(20);
        Dimension d = tfInput.getMinimumSize();
        d.width = 200;
        tfInput.setMinimumSize(d);

        cbSegName.addActionListener(this);
        cbSegItem.addActionListener(this);
        updateSegItem();
        updateSegCond();

        // GridBagLayout ������
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c;
        pCenter.setLayout(layout);

        // ��������̾
        c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        pCenter.add(cbSegName, c);

        // ����̾
        c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        pCenter.add(cbSegItem, c);

        // ���
        c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        pCenter.add(cbCond, c);

        // �ƥ����ȥե������
        c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        pCenter.add(tfInput, c);

        JPanel pSouth = new JPanel();
        btnClearAll = new JButton("Reset All Filters");
        btnClear    = new JButton("Reset This Filter");
        btnFilter   = new JButton("Filter");
        btnCancel   = new JButton("Cancel");

        CmdClearAll cmdClearAll = new CmdClearAll(this);
        CmdClear    cmdClear    = new CmdClear(this);
        CmdFilter   cmdFilter   = new CmdFilter(this);
        CmdCancel   cmdCancel   = new CmdCancel(this);

        btnClearAll.addActionListener(cmdClearAll);
        btnClear.addActionListener(cmdClear);
        btnFilter.addActionListener(cmdFilter);
        btnCancel.addActionListener(cmdCancel);

        pSouth.add(btnClearAll);
        pSouth.add(btnClear);
        pSouth.add(btnFilter);
        pSouth.add(btnCancel);

        //
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        pane.add(pCenter, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        pane.add(pSouth,  c);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSegmentName(String segName[]) {
        int n = 0;

        // ͭ���� segName �ο��򥫥����
        for(int i = 0; i < segName.length; i++) {
            if (segName[i] == null) {
                continue;
            }
            if ("".equals(segName[i])) {
                continue;
            }
            n++;
        }
        segmentName = new String[n];            // segmentName ��Ǽ�ΰ�
        segmentItem = new String[n][];          // segmentItem ��Ǽ�ΰ�

        n = 0;
        for(int i = 0; i < segName.length; i++) {
            if (segName[i] == null) {
                continue;
            }
            if ("".equals(segName[i])) {
                continue;
            }
            segmentName[n++] = segName[i];
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSegmentItem(int segIdx, String segItem[]) {
        int n = 0;

        for(int i = 0; i < segItem.length; i++) {
            if (segItem[i] == null) {
                continue;
            }
            if ("".equals(segItem[i])) {
                continue;
            }
            n++;
        }

        segmentItem[segIdx] = new String[n];

        n = 0;
        for(int i = 0; i < segItem.length; i++) {
            if (segItem[i] == null) {
                continue;
            }
            if ("".equals(segItem[i])) {
                continue;
            }
            segmentItem[segIdx][n++] = segItem[i];
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // segmentName �����򤷤��Ȥ��ν���
    public void actionPerformed(ActionEvent e) {
        JComboBox srcObj = (JComboBox)e.getSource();
        if (srcObj == cbSegName) {
            updateSegItem();
        }
        updateSegCond();

        Container pane = getContentPane();
        pane.validate();
    }

    ///////////////////////////////////////////////////////////////////////////
    // SegmentItem �ι�������
    public void updateSegItem() {
        // ���򤵤줿 segment �Υ���ǥå��������
        int idxSegment = cbSegName.getSelectedIndex();
        if (idxSegment < 0) {
            return;
        }

        // segment item �򹹿�
        cbSegItem.removeAllItems();
        for(int i = 0; i < segmentItem[idxSegment].length; i++) {
            cbSegItem.addItem(segmentItem[idxSegment][i]);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // �ʤ���߾��ι���
    public void updateSegCond() {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

        // ���򤵤줿 segment �Υ���ǥå��������
        int idxSegment = cbSegName.getSelectedIndex();
        if (idxSegment < 0) {
            return;
        }

        // ���򤵤줿 segmentItem �Υ���ǥå��������
        int idxSegmentItem = cbSegItem.getSelectedIndex();
        if (idxSegmentItem < 0) {
            return;
        }

        // �ʤ���߾��򹹿�
        cbCond.removeAllItems();
        boolean attrType;
        if (idxSegment == 0) {
            attrType = mbgdDataMng.getGeneAttrDataType(idxSegmentItem);

        }
        else {
            attrType = mbgdDataMng.getSegAttrDataType(idxSegment - 1, idxSegmentItem);
        }
        if (attrType != RegionInfoList.TYPE_ATTR_STR) {
            // ʸ����ǡ����ǤϤʤ�
            for(int i = 0; i < CondValList.length; i++) {
                cbCond.addItem(CondValList[i]);
            }
        }
        else {
            // ʸ����ǡ���
            for(int i = 0; i < CondStrList.length; i++) {
                cbCond.addItem(CondStrList[i]);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedSegmentName() {
        return (String)cbSegName.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSelectedSegmentIndex() {
        return cbSegName.getSelectedIndex();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedSegmentItemName() {
        return (String)cbSegItem.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSelectedSegmentItemIndex() {
        return cbSegItem.getSelectedIndex();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedSegmentItem() {
        return (String)cbSegItem.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedCondition() {
        return (String)cbCond.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSelectedConditionIndex() {
        return cbCond.getSelectedIndex();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getInputTextValue() {
        return tfInput.getText();
    }

}
