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
// Alignment �Υե��륿��󥰾�����ϲ���
public class AlignmentFilterDialog extends BaseFilterDialog implements ActionListener {
    public static final String OPT_ORTHOLOGS          = "Orthologs (BBH)";
    public static final String OPT_DUPLICATED         = "Duplicated";
    public static final String OPT_ELIMINATE_PARALOGS = "Eliminate Paralogs";
    public static final String OPT_PARALOGS           = "Paralogs";
    public static final String SelStrList[] = { OPT_ORTHOLOGS,
                                                OPT_DUPLICATED,
                                                OPT_ELIMINATE_PARALOGS,
                                                OPT_PARALOGS };

    private static AlignmentFilterDialog _instance = null;

    private JComboBox   cbAlignAttr = null;
    private JComboBox   cbAlignCond = null;
    private JTextField  tfInput     = null;
    private JComboBox   cbSelect    = null;

    private String alignAttrName[];

    ///////////////////////////////////////////////////////////////////////////
    //
    public static AlignmentFilterDialog Instance(Frame f) {
        if (_instance == null) {
            _instance = new AlignmentFilterDialog(f, true);
            _instance.setTitle("Filter Alignment");
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static AlignmentFilterDialog Instance() {
        if (_instance == null) {
            Dbg.println(1, "Error!! Can not create 'AlignmentFilterDialog'");
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentFilterDialog(Frame f, boolean sta) {
        super(f, sta);

        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        setSize(500, 100);
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        alignAttrName = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAttrName(String attrName[]) {
        int n = 0;

        for(int i = 0; i < attrName.length; i++) {
            if (attrName[i] == null) {
                continue;
            }
            if ("".equals(attrName[i])) {
                continue;
            }
            n++;
        }

        alignAttrName = new String[n];

        n = 0;
        for(int i = 0; i < alignAttrName.length; i++) {
            if (attrName[i] == null) {
                continue;
            }
            if ("".equals(attrName[i])) {
                continue;
            }
            alignAttrName[n++] = attrName[i];
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setGuiParts() {

        cbAlignAttr = new JComboBox(alignAttrName);
        cbAlignCond = new JComboBox(CondValList);
        tfInput     = new JTextField(20);
        Dimension d = tfInput.getMinimumSize();
        d.width = 200;
        tfInput.setMinimumSize(d);
        cbSelect    = new JComboBox(SelStrList);

        cbAlignAttr.addActionListener(this);
        cbAlignCond.addActionListener(this);

        updateAlignItem();
        updateAlignCond();

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // GUI ����
    public void layoutGuiParts(boolean isTextField) {
        JButton btnClear;
        JButton btnFilter;
        JButton btnCancel;
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());

        // ���٤Ƥ� GUI �����
        pane.removeAll();
        int x = 0;
        int y = 0;

        // GridBagLayout ������
        JPanel pCenter = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c;
        pCenter.setLayout(layout);

        // align ����̾
        c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        pCenter.add(cbAlignAttr, c);

        // ���
        c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        pCenter.add(cbAlignCond, c);

        if (isTextField) {
            // �ƥ����ȥե������
            c = new GridBagConstraints();
            c.gridx = x++;
            c.gridy = y;
            pCenter.add(cbSelect, c);
        }
        else {
            // select
            c = new GridBagConstraints();
            c.gridx = x++;
            c.gridy = y;
            pCenter.add(tfInput, c);
        }

        JPanel pSouth = new JPanel();
        btnClear    = new JButton("Reset Alignment Filters");
        btnClear.setToolTipText("Alignment");
        btnFilter   = new JButton("Filter");
        btnCancel   = new JButton("Cancel");

        CmdClear    cmdClear    = new CmdClear(this);
        CmdFilter   cmdFilter   = new CmdFilter(this);
        CmdCancel   cmdCancel   = new CmdCancel(this);

        btnClear.addActionListener(cmdClear);
        btnFilter.addActionListener(cmdFilter);
        btnCancel.addActionListener(cmdCancel);

        pSouth.add(btnClear);
        pSouth.add(btnFilter);
        pSouth.add(btnCancel);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        pane.add(pCenter, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        pane.add(pSouth,  c);

        pane.validate();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedAlignItem() {
        return (String)cbAlignAttr.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSelectedAlignItemIndex() {
        return cbAlignAttr.getSelectedIndex();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedCondition() {
        return (String)cbAlignCond.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSelectedConditionIndex() {
        return cbAlignCond.getSelectedIndex();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getInputTextValue() {
        return tfInput.getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectedType() {
        return (String)cbSelect.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSelectedTypeIndex() {
        return cbSelect.getSelectedIndex();
    }

    ///////////////////////////////////////////////////////////////////////////
    // AlignmentItem �ι�������
    public void updateAlignItem() {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

        // ���򤵤줿 align �Υ���ǥå��������
        int idxAlignAttr = cbAlignAttr.getSelectedIndex();
        if (idxAlignAttr < 0) {
            return;
        }
        String nameAlignAttr = (String)cbAlignAttr.getSelectedItem();

        // �ʤ���߾��򹹿�
        cbAlignCond.removeAllItems();
        boolean attrType = mbgdDataMng.getAlignAttrDataType(nameAlignAttr);
        if (attrType != RegionInfoList.TYPE_ATTR_STR) {
            // ʸ����ǡ����ǤϤʤ�
            for(int i = 0; i < CondValList.length; i++) {
                cbAlignCond.addItem(CondValList[i]);
            }
        }
        else {
            // ʸ����ǡ���
            cbAlignCond.addItem(NAM_SELECT);
            for(int i = 0; i < CondStrList.length; i++) {
                cbAlignCond.addItem(CondStrList[i]);
            }

            cbSelect.removeAllItems();
            for(int i = 0; i < SelStrList.length; i++) {
                cbSelect.addItem(SelStrList[i]);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // AlignmentItem �ι�������
    public void updateAlignCond() {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

        // ���򤵤줿 cond �Υ���ǥå��������
        int idxAlignCond = cbAlignCond.getSelectedIndex();
        if (idxAlignCond < 0) {
            return;
        }

        String txtAlignCond = (String)cbAlignCond.getSelectedItem();
        boolean isTextField;
        if (NAM_SELECT.equals(txtAlignCond)) {
            isTextField = true;
        }
        else {
            isTextField = false;
        }
        layoutGuiParts(isTextField);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        JComboBox srcObj = (JComboBox)e.getSource();
        if (srcObj == cbAlignAttr) {
            updateAlignItem();
        }
        updateAlignCond();

        repaint();
    }
}
