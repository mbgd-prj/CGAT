package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/////////////////////////////////////////////////////////////////////////////
//
public class AssembleGuiParts extends JScrollPane {
    public static final String TYPE_label       = "label";
    public static final String TYPE_string      = "string";
    public static final String TYPE_value       = "value";
    public static final String TYPE_text        = "text";
    public static final String TYPE_textarea    = "textarea";
    public static final String TYPE_checkBox    = "checkbox";
    public static final String TYPE_radio       = "radio";
    public static final String TYPE_select      = "select";
    public static final String TYPE_nextline    = "//";

    public static final String TYPE_tagh        = "<h>";
    public static final String TYPE_tagfield    = "<textfield>";
    public static final String TYPE_tagarea     = "<textarea>";
    public static final String TYPE_tagchkbox   = "<checkbox>";
    public static final String TYPE_tagradio    = "<radiobutton>";
    public static final String TYPE_tagselect   = "<select>";
    public static final String TYPE_tagbr       = "<br>";

    private GuiComposition guiComposition;                //��������
    private HashMap btnGrp;         //�饸���ܥ���򥰥롼�ײ����뤿���ButtonGroup���֥������Ȥ��Ǽ
    private HashMap lstGrp;         //ʣ������ꥹ�Ȥ�������뤿���JComboBox���֥������Ȥ��Ǽ
    private HashMap guiType;
    private HashMap guiData;
    private Vector nameData;
    public final int co_min = 3;              //GUI���ʤ�ɽ����ȹԤκǾ���
    public final int ro_min = 1;
    public final int de_field = 5;
    public final int de_rows = 5;
    public final int de_columns =10;

    ///////////////////////////////////////////////////////////////////////////
    //���󥹥ȥ饯���������ѡ����饹�ν������������Ϳ����줿�������������ѿ��˳�Ǽ���롣
    public AssembleGuiParts(GuiComposition info) {
        super();

        guiComposition = info;

        _init();
        place();
    }

    ///////////////////////////////////////////////////////////////////////////
    //��ʬ���ȤΥ��饹�ν����
    private void _init() {
        btnGrp = new HashMap();
        lstGrp = new HashMap();
        guiType = new HashMap();
        guiData = new HashMap();
        nameData = new Vector();
    }

    ///////////////////////////////////////////////////////////////////////////
    //�����
    public void clear() {
        removeAll();
        btnGrp.clear();
        lstGrp.clear();
        guiType.clear();
        guiData.clear();
        nameData.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getCgi() {
        return guiComposition.getCgi();
    }

    ///////////////////////////////////////////////////////////////////////////
    //ButtonGroup���֥������Ȥ��Ǽ��name�򥭡�����¸���롣
    public void putBtnGrp(String key, ButtonGroup val) {
        btnGrp.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //����Υ�����ޥåפ����ͤ��֤���
    public ButtonGroup getBtnGrp(String key) {
        return (ButtonGroup)btnGrp.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //JCombo���֥������Ȥ��Ǽ��name�򥭡�����¸���롣
    public void putLst(String key, JComboBox val) {
        lstGrp.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //����Υ�\uFF0D�Υޥåפ����ͤ��֤���
    public JComboBox getLst(String key) {
        return (JComboBox)lstGrp.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //name�򥭡���GUI���̤��ݻ�
    public void putType(String key, String val) {
        guiType.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //name�򥭡���GUI���̤��֤�
    public String getType (String key) {
        return (String)guiType.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI�Τ��줾��Υ��֥������Ȥ��ݻ�
    public void putData(String key,Object val){
        guiData.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI�Υ��֥������Ȥ��֤�
    public Object getData(String key) {
        return guiData.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI���̤��ƥ����ȥե�����ɤλ�
    public String getTextfieldValue(JTextField val) {
        return val.getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI���̤��ƥ����ȥ��ꥢ�λ�
    public String getTextareaValue(JTextArea val) {
        return val.getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI���̤������å��ܥå����λ�
    public String getCheckboxValue(JCheckBox val) {
        if (val.isSelected()) {
            return "on";
        }
        else {
            return "off";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI���̤��ܥ��󥰥롼�פλ�
    public String getRadiobuttonValue(ButtonGroup val) {
        JRadioButton rb = new JRadioButton();
        Enumeration e = val.getElements();
        while(e.hasMoreElements()) {
            rb = (JRadioButton) e.nextElement();
            if (rb.isSelected())
                break;
        }
        return rb.getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI���̤�����ܥܥå����λ�
    public  String getListValue(JComboBox val) {
        return (String)val.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //name�Υꥹ�Ȥ���
    public void setAllNameData(String val) {
        if(! nameData.contains(val)){
            nameData.add(val);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //����줿���ǤϤ����ĤʤΤ��򥫥���Ȥ���
    public int countNameData() {
        return nameData.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI���������name�ꥹ�Ȥ򤹤٤�����
    public String getAllNameData(int idx) {
        return (String)nameData.get(idx);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI�ѡ�������
    public void place() {

        //
        int n = guiComposition.countData();
        GridLayout gl = new GridLayout(n, 1);
        GridBagLayout gbl = new GridBagLayout();
        JPanel basePanel  = new JPanel();
        basePanel.setLayout(gbl);


        for(int i = 0; i < n; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = i;

            //
            GuiElement e = guiComposition.get(i);     //data�μ���
            String type    = e.getType();
            String text    = e.getText();
            String varname = e.getVarname();
            String val     = e.getVal();

            putType(varname, type);

            JComponent jc;
            if (type.equalsIgnoreCase(TYPE_label)) {
                JLabel jl = new JLabel(text);
                jl.setHorizontalAlignment(SwingConstants.CENTER);

                jc = jl;
            }
            else if (type.equalsIgnoreCase(TYPE_text)) {
                setAllNameData(varname);
                JPanel jp = new JPanel();
                jp.setLayout(new GridLayout(1, 2));
                JLabel jl = new JLabel(text);
                jl.setHorizontalAlignment(SwingConstants.RIGHT);
                jp.add(jl);

                JTextField jt = new JTextField(10);
                jt.setText(val);
                jp.add(jt);
                putData(varname, jt);

                jc = jp;
            }
            else if (type.equalsIgnoreCase(TYPE_textarea)) {
                setAllNameData(varname);
                JPanel jp = new JPanel();
                jp.setLayout(new GridLayout(1, 2));
                JLabel jl = new JLabel(text);
                jl.setHorizontalAlignment(SwingConstants.RIGHT);
                jp.add(jl);

                int nr = e.getNrow();
                int nc = e.getNcol();
                JTextArea jta = new JTextArea(nr, nc);
                jta.setText(val);
                JScrollPane jsp = new JScrollPane(jta);
                jp.add(jsp);
//                putData(varname, jsp);
//                jp.add(jta);
                putData(varname, jta);

                jc = jp;
            }
            else if (type.equalsIgnoreCase(TYPE_radio)) {
                setAllNameData(varname);
                JPanel jp = new JPanel();
                jp.setLayout(new GridLayout(1, 2));
                JLabel jl = new JLabel(text);
                jl.setHorizontalAlignment(SwingConstants.RIGHT);
                jp.add(jl);

                JRadioButton jrb = new JRadioButton(val);
                jp.add(jrb);

                ButtonGroup bg = getBtnGrp(varname);
                if (bg == null) {
                    putData(varname, bg);
                    bg = new ButtonGroup();
                    putBtnGrp(varname, bg);
                    jrb.setSelected(true);    // �����ܤΥܥ���˥����å������
                }
                bg.add(jrb);

                jc = jp;
            }
            else {
                continue;
            }
            gbl.setConstraints(jc, gbc);
            basePanel.add(jc);
        }

        // Bse Panel �� add
        JViewport jvp = getViewport();
        basePanel.validate();
        jvp.add(basePanel);
    }

}
