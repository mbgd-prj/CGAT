package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

///////////////////////////////////////////////////////////////////////////////
//
public class SearchOrfCommand extends Observable implements ActionListener {
    private JFrame frame;
    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    private JOptionPane optionPane;
    private JPanel panel;
    private JLabel lab1, lab2;
    private JScrollPane spOrfname;
    private JTextArea taOrfname;
    private JTextField tfOrfFilename;
    private JButton browsFile;

    ///////////////////////////////////////////////////////////////////////////
    //
    public SearchOrfCommand(JFrame f, MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        frame = f;
        mbgdDataMng = dataMng;
        viewWin     = vWin;
        frame.setTitle("Search ORF");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int showDialog() {
        lab1 = new JLabel("Input ORF name(s)");
        taOrfname = new JTextArea(10, 40);
        spOrfname = new JScrollPane(taOrfname);
        spOrfname.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        lab2 = new JLabel("filename : ");
        tfOrfFilename = new JTextField(20);
        browsFile = new JButton("Browse");

        // GUI の配置
        panel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        panel.setLayout(gridbag);

        GridBagConstraints c = new GridBagConstraints();

        // ORF 入力エリア(ラベル)
        c.weightx = 1.0;
        c.insets = new Insets(20, 0, 0, 0);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(lab1, c);
        panel.add(lab1);

        // ORF 入力エリア
        c.insets = new Insets(0, 0, 20, 0);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(spOrfname, c);
        panel.add(spOrfname);

        MbgdData mbgdData = MbgdData.Instance();
        if (! mbgdData.isApplet()) {        // アプレットでない場合のみ表示する
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(20, 0, 0, 0);
            gridbag.setConstraints(lab2, c);
            panel.add(lab2);

            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 20, 0);
            gridbag.setConstraints(tfOrfFilename, c);
            panel.add(tfOrfFilename);

            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(browsFile, c);
            panel.add(browsFile);

            // Browse ボタンに関連付け（JFileChooser で選択したファイルを JTextField に表示）
            BrowsFileList bowsFileList = new BrowsFileList(tfOrfFilename);
            browsFile.addActionListener(bowsFileList);
        }

        // サイズ変更不可
        frame.setResizable(false);

        Object [] msg = { panel };
        Object [] btn = {"All Clear", "Clear", "Search", "Cancel"};
        int ret;

        ret = JOptionPane.showOptionDialog( frame,
                                            msg,
                                            "SEARCH",
                                            JOptionPane.OK_CANCEL_OPTION,
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            btn,
                                            btn[2]);

        return ret;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ArrayList getOrfNameList() {
        ArrayList orfList = new ArrayList();    // 入力された ORF 名

        String orfnameLine;
        String orfnameField;
        StringTokenizer stLine;
        StringTokenizer stField;

        // テキストフィールドから ORF 名を取得
        stLine = new StringTokenizer(taOrfname.getText(), "\n");
        for( ; stLine.hasMoreTokens(); ) {
            stField = new StringTokenizer(stLine.nextToken(), " ");
            for( ; stField.hasMoreTokens(); ) {
                String orfname = stField.nextToken();
                orfname.trim();
                if (orfname.equals("")) {
                    // ORF 名未入力
                    continue;
                }
                orfList.add(orfname);
            }
        }

        return orfList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionAllClear() {
        mbgdDataMng.clearMarkEnt();

        // 検索結果一覧表示
//        dispHitOrfTable();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionClear() {
        ArrayList orfList = getOrfNameList();    // 入力された ORF 名

        int loopMax = orfList.size();
        for(int i = 0; i < loopMax; i++) {
            String orfName = (String)orfList.get(i);
            mbgdDataMng.delMarkEnt(orfName);
        }

        // 検索結果一覧表示
//        dispHitOrfTable();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionSearch() {
        StringTokenizer stLine;

        ArrayList orfList = getOrfNameList();    // 入力された ORF 名

        MbgdData mbgdData = MbgdData.Instance();
        if (! mbgdData.isApplet()) {        // アプレットでない場合のみ、指定されたファイルから ORF 名を取得
            File orfFile = new File(tfOrfFilename.getText());
            String inOrfName = "";
            if (orfFile.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(orfFile));
                    for(;;) {
                        String buf = br.readLine();
                        if (buf == null) {
                            break;
                        }
                        inOrfName = inOrfName + " " + buf;
                    }
                }
                catch (Exception e2) {
                    Dbg.println(1, "File read error : " + orfFile.getAbsolutePath());
                }
            }
            stLine = new StringTokenizer(inOrfName, " ");
            for( ; stLine.hasMoreTokens(); ) {
                orfList.add(stLine.nextToken());
            }
        }

        // 該当する ORF 名をハイライト表示
        boolean isExist = false;
        String spName;
        String sp1Name = mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        String sp2Name = mbgdDataMng.getSpecName(MbgdDataMng.OPPO_SPEC);
        RegionInfo rInfo;
        for(int i = 0; i < orfList.size(); i++) {
            String orfName = (String)orfList.get(i);
            orfName = orfName.toUpperCase();

            boolean basespec = true;
            spName = sp1Name;
            rInfo = mbgdDataMng.getGeneInfo(MbgdDataMng.BASE_SPEC, orfName);
            if (rInfo == null) {
                basespec = false;
                spName = sp2Name;
                rInfo = mbgdDataMng.getGeneInfo(MbgdDataMng.OPPO_SPEC, orfName);
            }

            if (rInfo != null) {
                int from = rInfo.getFrom();
                int to   = rInfo.getTo();
                int dir  = rInfo.getDir();

                mbgdDataMng.addMarkEnt(basespec, new MarkEnt(spName, orfName, from, to, dir, 1, new Color(0xffff)));
            }
        }

        // 検索結果一覧表示
        dispHitOrfTable();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void dispHitOrfTable() {
        // 検索結果一覧表示
        JFrame f = new JFrame();
        f.setTitle("ORF name");

        // 結果表示 Frame
        SearchOrfResFrame resFrame = SearchOrfResFrame.Instance(viewWin);
        resFrame.clear();

        //
        boolean isExist = false;

        MarkEnt entListBase[];
        MarkEnt entListOppo[];
        int loopMax;

        // Base SPEC
        entListBase = mbgdDataMng.getMarkEntAll(true);
        setHitOrfTableEnt(resFrame, entListBase);

        // Oppo SPEC
        entListOppo = mbgdDataMng.getMarkEntAll(false);
        setHitOrfTableEnt(resFrame, entListOppo);

        if ((entListBase.length == 0) && (entListOppo.length == 0)) {
            // 表示対象データがない
            int idx = 0;
            Object [] dat = new Object[5];
            dat[idx++] = "Nodata Found";
            dat[idx++] = "";
            dat[idx++] = new Integer(0);
            dat[idx++] = new Integer(0);
            dat[idx++] = "";
            resFrame.addRow(dat);
        }

        resFrame.setVisible(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setHitOrfTableEnt(SearchOrfResFrame resFrame, MarkEnt entList[]) {
        int loopMax;

        loopMax = entList.length;
        for(int i = 0; i < loopMax; i++) {
            MarkEnt ent = entList[i];
            int idx = 0;
            Object [] dat = new Object[6];

            dat[idx++] = ent.getSpName();
            dat[idx++] = new Integer(ent.getPosFrom());
            dat[idx++] = new Integer(ent.getPosTo());
            if (0 < ent.getDir()) {
                dat[idx++] = "+";
            }
            else {
                dat[idx++] = "-";
            }
            dat[idx++] = new Integer(ent.getColorType());
            dat[idx++] = ent.getName();
            resFrame.addRow(dat);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        int ret = showDialog();

        //
        switch (ret) {
        case 0:                 // All Clear ボタンがクリックされた
            actionAllClear();

            //
            setChanged();
            notifyObservers(ViewWindow.CHANGE_SRCHORF);
            break;

        case 1:                 // Clear ボタンがクリックされた
            actionClear();

            //
            setChanged();
            notifyObservers(ViewWindow.CHANGE_SRCHORF);
            break;

        case 2:                 // Ok ボタンがクリックされた
            actionSearch();

            //
            setChanged();
            notifyObservers(ViewWindow.CHANGE_SRCHORF);
            break;

        default:                // そのた
            break;
        }

    }

}
