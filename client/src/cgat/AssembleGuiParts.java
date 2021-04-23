package cgat;

/**
 * タイトル:  cgat
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

    private GuiComposition guiComposition;                //構成情報
    private HashMap btnGrp;         //ラジオボタンをグループ化するためのButtonGroupオブジェクトを格納
    private HashMap lstGrp;         //複数選択リストを作成するためのJComboBoxオブジェクトを格納
    private HashMap guiType;
    private HashMap guiData;
    private Vector nameData;
    public final int co_min = 3;              //GUI部品の表示列と行の最小値
    public final int ro_min = 1;
    public final int de_field = 5;
    public final int de_rows = 5;
    public final int de_columns =10;

    ///////////////////////////////////////////////////////////////////////////
    //コンストラクタ・スーパークラスの初期化・引数で与えられた構成情報をメンバ変数に格納する。
    public AssembleGuiParts(GuiComposition info) {
        super();

        guiComposition = info;

        _init();
        place();
    }

    ///////////////////////////////////////////////////////////////////////////
    //自分自身のクラスの初期化
    private void _init() {
        btnGrp = new HashMap();
        lstGrp = new HashMap();
        guiType = new HashMap();
        guiData = new HashMap();
        nameData = new Vector();
    }

    ///////////////////////////////////////////////////////////////////////////
    //初期化
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
    //ButtonGroupオブジェクトを格納、nameをキーに保存する。
    public void putBtnGrp(String key, ButtonGroup val) {
        btnGrp.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //指定のキーをマップする値を返す。
    public ButtonGroup getBtnGrp(String key) {
        return (ButtonGroup)btnGrp.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //JComboオブジェクトを格納、nameをキーに保存する。
    public void putLst(String key, JComboBox val) {
        lstGrp.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //指定のキ\uFF0Dのマップする値を返す。
    public JComboBox getLst(String key) {
        return (JComboBox)lstGrp.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //nameをキーにGUI種別を保持
    public void putType(String key, String val) {
        guiType.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //nameをキーにGUI種別を返す
    public String getType (String key) {
        return (String)guiType.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUIのそれぞれのオブジェクトを保持
    public void putData(String key,Object val){
        guiData.put(key,val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUIのオブジェクトを返す
    public Object getData(String key) {
        return guiData.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI種別がテキストフィールドの時
    public String getTextfieldValue(JTextField val) {
        return val.getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI種別がテキストエリアの時
    public String getTextareaValue(JTextArea val) {
        return val.getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI種別がチェックボックスの時
    public String getCheckboxValue(JCheckBox val) {
        if (val.isSelected()) {
            return "on";
        }
        else {
            return "off";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI種別がボタングループの時
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
    //GUI種別がコンボボックスの時
    public  String getListValue(JComboBox val) {
        return (String)val.getSelectedItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //nameのリストを作る
    public void setAllNameData(String val) {
        if(! nameData.contains(val)){
            nameData.add(val);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //得られた要素はいくつなのかをカウントする
    public int countNameData() {
        return nameData.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUI構成情報のnameリストをすべて得る
    public String getAllNameData(int idx) {
        return (String)nameData.get(idx);
    }

    ///////////////////////////////////////////////////////////////////////////
    //GUIパーツ配置
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
            GuiElement e = guiComposition.get(i);     //dataの取得
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
                    jrb.setSelected(true);    // 一番目のボタンにチェック入れる
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

        // Bse Panel を add
        JViewport jvp = getViewport();
        basePanel.validate();
        jvp.add(basePanel);
    }

}
