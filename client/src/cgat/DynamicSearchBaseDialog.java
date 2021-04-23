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
public class DynamicSearchBaseDialog extends JDialog implements ActionListener {
    protected AssembleGuiParts objGui;
    protected JButton btnOk;
    protected JButton btnCancel;
    protected String CGI_progname = null;
    protected String searchType = null;
    protected String cgiOpt = null;
    protected boolean isGetArgs = false;          // 引数を取らない画面（CGI path を取得）

    ///////////////////////////////////////////////////////////////////////////
    //
    public DynamicSearchBaseDialog(Frame f, String u, String type) {
        super(f, true);
        setVisible(false);
        isGetArgs = false;

        // 検索種別画面情報を取得
        UrlFile guiFile;
        try {
            String url = u + type;
            guiFile = new UrlFile(url);
        }
        catch (Exception e2) {
            // エラーメッセージ表示
            String msg;
            msg = "Data not found.\n" + "Type : " + type + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);
            return;
        }

        // 検索種別画面を作成
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

        // BorderLayout で配置する
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

        // CGI を返す
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
            // OK ボタンがクリックされた
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

                // cgiOpt は CGI へ渡されることが前提なので エンコード する
                String workVal = "";
                for(int idx = 0; idx < val.length(); idx++) {
                    char c = val.charAt(idx);
                    if (c == ' ') {         // 半角スペース
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
            // Cancel ボタンがクリックされた
            cgiOpt = null;
        }
    }


}
