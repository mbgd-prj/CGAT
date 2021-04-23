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
public class DynamicSearchBaseDialog extends JDialog implements ActionListener {
    protected AssembleGuiParts objGui;
    protected JButton btnOk;
    protected JButton btnCancel;
    protected String CGI_progname = null;
    protected String searchType = null;
    protected String cgiOpt = null;
    protected boolean isGetArgs = false;          // ��������ʤ����̡�CGI path �������

    ///////////////////////////////////////////////////////////////////////////
    //
    public DynamicSearchBaseDialog(Frame f, String u, String type) {
        super(f, true);
        setVisible(false);
        isGetArgs = false;

        // �������̲��̾�������
        UrlFile guiFile;
        try {
            String url = u + type;
            guiFile = new UrlFile(url);
        }
        catch (Exception e2) {
            // ���顼��å�����ɽ��
            String msg;
            msg = "Data not found.\n" + "Type : " + type + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);
            return;
        }

        // �������̲��̤����
        GuiComposition guiInfo = new GuiComposition();
        guiInfo.readComposition(guiFile);

        //
        objGui    = new AssembleGuiParts(guiInfo);
        btnOk     = new JButton("OK");
        btnOk.addActionListener(this);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);

        JPanel pnl = new JPanel();
        pnl.add(btnOk);
        pnl.add(btnCancel);

        // BorderLayout �����֤���
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(objGui, BorderLayout.CENTER);
        getContentPane().add(pnl, BorderLayout.SOUTH);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isGetArgs() {
        return isGetArgs;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getCgiPath() {
        if (cgiOpt == null) {
            return null;
        }

        // CGI ���֤�
        String cgi;

        cgi = objGui.getCgi();
        if (! cgiOpt.equals("")) {
            if (cgi.endsWith(".cgi")) {
                cgi += "?";
            }
            else {
                cgi += "&";
            }
            cgi = cgi + cgiOpt;
        }

        return cgi;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSearchType(String st) {
        searchType = st;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSearchType() {
        return searchType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        setVisible(false);

        if (e.getSource() == btnOk) {
            // OK �ܥ��󤬥���å����줿
            cgiOpt = "";

            int loopMax = objGui.countNameData();
            for(int i = 0; i < loopMax; i++) {
                String key = objGui.getAllNameData(i);
                String type = objGui.getType(key);
                String val;

                if ((type.equalsIgnoreCase(AssembleGuiParts.TYPE_string)) ||
                    (type.equalsIgnoreCase(AssembleGuiParts.TYPE_value)) ||
                    (type.equalsIgnoreCase(AssembleGuiParts.TYPE_tagfield))) {
                    JTextField tf = (JTextField)objGui.getData(key);
                    val = objGui.getTextfieldValue(tf);
                }
                else if (type.equalsIgnoreCase(AssembleGuiParts.TYPE_text)) {
                    JTextField tf = (JTextField)objGui.getData(key);
                    val = objGui.getTextfieldValue(tf);
                }
                else if (type.equalsIgnoreCase(AssembleGuiParts.TYPE_textarea)) {
                    JTextArea ta = (JTextArea)objGui.getData(key);
                    val = objGui.getTextareaValue(ta);
                }
                else if ((type.equalsIgnoreCase(AssembleGuiParts.TYPE_checkBox)) ||
                         (type.equalsIgnoreCase(AssembleGuiParts.TYPE_tagchkbox))) {
                    JCheckBox cb = (JCheckBox)objGui.getData(key);
                    val = objGui.getCheckboxValue(cb);
                }
                else if ((type.equalsIgnoreCase(AssembleGuiParts.TYPE_radio)) ||
                         (type.equalsIgnoreCase(AssembleGuiParts.TYPE_tagradio))) {
                    ButtonGroup btnGrp = objGui.getBtnGrp(key);
                    val = objGui.getRadiobuttonValue(btnGrp);
                }
                else if ((type.equalsIgnoreCase(AssembleGuiParts.TYPE_select)) ||
                         (type.equalsIgnoreCase(AssembleGuiParts.TYPE_tagselect))) {
                    JComboBox cb = (JComboBox)objGui.getData(key);
                    val = objGui.getListValue(cb);
                }
                else {
                    continue;
                }
                setSearchType(val);

                // cgiOpt �� CGI ���Ϥ���뤳�Ȥ�����ʤΤ� ���󥳡��� ����
                String workVal = "";
                for(int idx = 0; idx < val.length(); idx++) {
                    char c = val.charAt(idx);
                    if (c == ' ') {         // Ⱦ�ѥ��ڡ���
                        workVal += "+";
                    }
                    else if (c == '%') {    //
                        workVal += "%" +Integer.toString(c, 16);
                    }
                    else if (c == '=') {    //
                        workVal += "%" +Integer.toString(c, 16);
                    }
                    else if (c == '&') {    //
                        workVal += "%" +Integer.toString(c, 16);
                    }
                    else if (c == '+') {    //
                        workVal += "%" +Integer.toString(c, 16);
                    }
                    else if ((0x20 <= c) && (c < 0x7f)) {
                        workVal += c;
                    }
                    else {
                        workVal += "%" +Integer.toString(c, 16);
                    }
                }
                val = workVal;

                if (! cgiOpt.equals("")) {
                    cgiOpt += "&";
                }
                cgiOpt += key + "=" + val;
            }
        }
        else {
            // Cancel �ܥ��󤬥���å����줿
            cgiOpt = null;
        }
    }


}
