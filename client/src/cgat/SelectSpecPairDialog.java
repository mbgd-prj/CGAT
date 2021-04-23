
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class SelectSpecPairDialog {
    protected static SelectSpecPairDialog _instance = null;
    protected MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

    protected boolean staSelect;

    protected JFrame frame;

    protected JComboBox server;
    protected JComboBox spec;
    protected JComboBox attr;
//    protected JComboBox seg[];

    protected ArrayList alignFileList;
    protected InfoGeneAttr infoGeneAttr;
    protected InfoSegments infoSegments;

    protected HashMap selectSpecInfo;
    protected String selectGeneAttr;
    protected String selectGeneColorType;
    protected String selectSegName[];
    protected String selectSegCgi[];

    protected SelectSegmentInfo selectSegInfo;
    protected SelectSegment     selectSeg[];

    protected String defaultSp1  = "";
    protected String defaultSp2  = "";
    protected String defaultProg = "";

    ///////////////////////////////////////////////////////////////////////////
    //
    static public SelectSpecPairDialog Instance(JFrame f) {
        if (_instance == null) {
            _instance = new SelectSpecPairDialog(f);
            _instance._update();
        }

        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    protected void addAlignData(String spec1, String spec2, String path, String file, String colorTab, ArrayList fileList) {
        HashMap alignInfo = new HashMap();
        alignInfo.put("SPEC1", spec1);
        alignInfo.put("SPEC2", spec2);
        alignInfo.put("PATH", path);
        alignInfo.put("ALIGN", file);
        alignInfo.put("FILE",  file.substring(0, file.indexOf(".")));
        alignInfo.put("COLORTAB", colorTab);

        fileList.add(alignInfo);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected SelectSpecPairDialog(JFrame f) {
        frame = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void _init() {
        server = new JComboBox();
        spec = new JComboBox();
        attr = new JComboBox();

        //
        for(int i = 0; i < MbgdDataMng.MAX_URL_HOME; i++) {
            String key = MbgdDataMng.OPT_URL_HOME + i;
            String val = mbgdDataMng.getProperty(key);
            if (val == null) {
                continue;
            }
            val = val.trim();
            if ("".equals(val)) {
                continue;
            }

            server.addItem(val);
        }
        server.setSelectedIndex(0);
        server.addActionListener(new ListActionServer());

        String basePath = (String)server.getSelectedItem();
        mbgdDataMng.setBasePath(basePath);
//        _updateSpec();
        

        int maxSegNum = mbgdDataMng.getMaxSegNum();
//        seg = new JComboBox[maxSegNum];
//        for(int i = 0; i < maxSegNum; i++) {
//            seg[i] = new JComboBox();
//        }

        selectSegInfo = new SelectSegmentInfo();
        selectSeg = new SelectSegment[maxSegNum];
        for(int i = 0; i < maxSegNum; i++) {
            selectSeg[i] = new SelectSegment(frame, selectSegInfo);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void _update() {
        _init();

        setStaSelect(false, null, null, null, null, null);

        _updateSpec();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void _updateSpec() {
        String[] specList;
        String locate;
        int selectedIdx = -1;
        int maxSegNum = mbgdDataMng.getMaxSegNum();

        //
        spec.removeAllItems();
        attr.removeAllItems();
//        for(int i = 0; i < maxSegNum; i++) {
//            seg[i].removeAllItems();
//        }

        // ���򤹤� ��ʪ��ڥ� ��ɽ��
        alignFileList = getSpecList();
        specList = new String[alignFileList.size()];
        if (specList.length != 0) {
            selectedIdx = 0;
        }
        for(int i = 0; i < alignFileList.size(); i++) {
            HashMap alignInfo = (HashMap)alignFileList.get(i);
            if (((String)alignInfo.get("PATH")).startsWith("http")) {
                locate = "(Server)";
            }
            else {
                locate = "";
            }
            specList[i] = alignInfo.get("ALIGN") + locate;
            spec.addItem(specList[i]);
            if (selectedIdx < 0) {
                if (specList[i].startsWith(defaultProg + "." + defaultSp1 + "-" + defaultSp2) ||
                    specList[i].startsWith(defaultProg + "." + defaultSp2 + "-" + defaultSp1)) {
                    selectedIdx = i;
                }
            }
        }
        spec.setSelectedIndex(selectedIdx);

        // ɽ������ Gene attr ���������(server)
        String path = mbgdDataMng.getBasePath();
        infoGeneAttr = new InfoGeneAttr();
        infoGeneAttr.load(path + "cgi-bin/geneAttr.cgi");

        // ɽ������ Gene attr ���������(local)
try {
        String sep  = System.getProperty("file.separator");
        String dirCgat = mbgdDataMng.getCgatHome();
        infoGeneAttr.load(dirCgat + sep + "geneattr");
}
catch (Exception e) {
}

        String [] attrList;
        attrList = new String[infoGeneAttr.size() + 1];
        attrList[0] = "Function Category";
        attr.addItem(attrList[0]);
        for(int i = 0; i < infoGeneAttr.size(); i++) {
            attrList[i + 1] = infoGeneAttr.getName(i);
            attr.addItem(attrList[i + 1]);
        }

        // select segment data
        selectSegInfo.getListItem();
        for(int i = 0; i < maxSegNum; i++) {
            selectSeg[i].updateSegName();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void showDialog() {
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        Object[] opt = new Object[2 + (2 + maxSegNum) * 2];

        int idx = 0;
        opt[idx++] = "Server URL";
        opt[idx++] = server;
        opt[idx++] = "Please select alignment data";
        opt[idx++] = spec;
        opt[idx++] = "Gene Attribute";
        opt[idx++] = attr;
        for(int i = 0; i < maxSegNum; i++) {
            opt[idx++] = "Seg" + String.valueOf(i + 1);
            opt[idx++] = selectSeg[i];
        }

        //reset the status
        setStaSelect(false, null, null, null, null, null);

        // ��ʪ�������������ɽ��
        int ret = JOptionPane.showConfirmDialog(  frame.getContentPane(),
                                                opt,
                                                "Select species pair",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
            String geneAttr = null;
            String geneColorType = null;

            String segName[] = new String[maxSegNum];
            String segCgi[] = new String[maxSegNum];

            idx = attr.getSelectedIndex();
            if (idx != 0) {
                // Function Category �ʳ������򤵤줿
//                geneAttr = infoGeneAttr.getKey(idx - 1);
                geneAttr = (String)attr.getSelectedItem();
                geneColorType = infoGeneAttr.getType(idx - 1);
            }

            for(int i = 0; i < maxSegNum; i++) {
                segName[i] = selectSeg[i].getSelectedSegName();      // ̾��
                segCgi[i]  = selectSeg[i].getSelectedSegCgi();
            }

            //
            int idxOk = 0;
            for(idx = 0; idx < maxSegNum; idx++) {
                if (! segCgi[idx].equals("")) {
                    segName[idxOk] = segName[idx];
                    segCgi[idxOk]  = segCgi[idx];
                    idxOk++;
                }
            }
            for(; idxOk < maxSegNum; idxOk++) {
                segName[idxOk] = "";
                segCgi[idxOk]  = "";
            }

            int selIdx = spec.getSelectedIndex();
            setStaSelect(true, (HashMap)alignFileList.get(selIdx),
                                geneAttr, geneColorType,
                                segName,  segCgi);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setStaSelect(boolean sta, HashMap info,
                                String geneAttr, String geneColorType,
                                String segName[], String segCgi[]) {
        staSelect = sta;
        selectSpecInfo = info;
        selectGeneAttr = geneAttr;
        selectGeneColorType = geneColorType;
        selectSegName = segName;
        selectSegCgi  = segCgi;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getStaSelect() {
        return(staSelect);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ���ꤵ�줿 PATH �� URL �򸡺����������оݤȤʤ�ե�����ΰ������������
    protected ArrayList getSpecList() {
        String type;
        ArrayList alignFileList = new ArrayList();

try {
        // �����о�Υǡ���
        getSpecListUrl(alignFileList);
}
catch (Exception e) {
}


        // ������ǥ�����
try {
        getAlignFileListDisk(mbgdDataMng.getCgatHome(), alignFileList);
}
catch (Exception e) {
}


        return(alignFileList);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �ե�����ΰ�����ꡢɽ����ǽ����ʪ����Ȥ߹�碌���������
    //      alignFileList �� gene �� seg �ξ�����Ȥ߹���
    protected void makeupSpecList(ArrayList alignFileList,
                                    ArrayList geneFileList) {

        for(int i = 0; i < alignFileList.size(); i++) {
            HashMap alignInfo = (HashMap)alignFileList.get(i);
            alignInfo.put("SP1GENE", selectRegInfo(geneFileList, (String)alignInfo.get("SPEC1")));
            alignInfo.put("SP2GENE", selectRegInfo(geneFileList, (String)alignInfo.get("SPEC2")));
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ���ꤵ�줿 URL �򸡺����������оݤȤʤ�ե�����ΰ������������
    protected void getSpecListUrl(ArrayList alignFileList) {
        // �ǥ��쥯�ȥ���� align �ե�����򸡺�
        getAlignFileListUrl(mbgdDataMng.getBasePath(), alignFileList);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void getAlignFileListUrl(String url, ArrayList fileList) {
        String alignUrl = url + "/cgi-bin";
        String filelist = alignUrl + "/filelist.cgi";

        // �ե�����ΰ��������Ҥ��줿�ե���������
        try {
            UrlFile inAlign = new UrlFile(filelist);
            ArrayList workFileList = new ArrayList();
            String colorTab = "";

            // URL ����ǡ�����ꤳ��
            String buf;
            for(;;) {
                buf = inAlign.readLine();
                if (buf == null) {
                    // EOF ����
                    break;
                }
                if (buf.trim().equals("")) {
                    continue;
                }
                workFileList.add(buf);
            }

            // align �ǡ�������
            for(int i = 0; i < workFileList.size(); i++) {
                String file = (String)workFileList.get(i);
                if (file.endsWith(".gz")) {
                    file = file.substring(0, file.length() - ".gz".length());
                }

                // spec1, spec2 ̾��
                int sepIdx = file.indexOf("-");
                String spec1, spec2;
                spec1 = file.substring(file.indexOf(".") + 1, sepIdx);
                spec2 = file.substring(sepIdx + 1);
                if (0 <= spec2.indexOf(".")) {
                    // spec2 �� '.' ���ޤޤ��  --->  align �ǡ����ե�����ǤϤʤ�
                    continue;
                }

                // align �ǡ����ե�����
                addAlignData(spec1, spec2, url, file, colorTab, fileList);
            }

        }
        catch (Exception e) {
            Dbg.println(1, "Exception :: getAlignFileListUrl() :: " + filelist);
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void getGeneFileListUrl(String url, ArrayList fileList) {
        try {
            String geneUrl = url + "/gene";
            String filelist = geneUrl + "/filelist";
            DataInputStream in = new DataInputStream(new URL(filelist).openStream());
            BufferedReader inGene = new BufferedReader(new InputStreamReader(in));

            ArrayList workFileList = new ArrayList();
            String colorTabOrg = "";

            // URL ����ǡ�����ꤳ��
            String buf;
            for(;;) {
                buf = inGene.readLine();
                if (buf == null) {
                    // EOF ����
                    break;
                }
                workFileList.add(buf);
                // colorTab
                if (buf.equals("colorTab")) {
                    colorTabOrg = geneUrl + "/colorTab";
                }
            }

            // gene �ǡ�������
            for(int i = 0; i < workFileList.size(); i++) {
                String file = (String)workFileList.get(i);
                if (file.startsWith("gene.") == false) {
                    // gene �ǡ����ե�����Ǥʤ�
                    continue;
                }

                // spec ̾��
                String spec = file.substring("gene.".length());
                if (file.endsWith(".gz")) {
                    spec = spec.substring(0, spec.length() - ".gz".length());
                }
                if (0 <= spec.indexOf(".")) {
                    // spec �� '.' ���ޤޤ��  --->  gene �ǡ����ե�����ǤϤʤ�
                    continue;
                }

                // chromosome
                String chromosome = geneUrl + "/chromosome." + spec;
                if (selectFile("chromosome." + spec, workFileList) == null) {
                    // �������� chromosome �ե����뤬������ʤ�
                    continue;
                }

                // colorTab
                String colorTab = geneUrl + "/colorTab." + spec;
                if (selectFile("colorTab." + spec, workFileList) == null) {
                    // �������� chromosome �ե����뤬������ʤ�
                    colorTab = colorTabOrg;
                }

                // gene �ǡ����ե�����
                HashMap geneInfo = new HashMap();
                geneInfo.put("SPEC", spec);
                geneInfo.put("GENE", geneUrl + "/" + file);
                geneInfo.put("CHROMOSOME", chromosome);
                geneInfo.put("COLORTAB", colorTab);

                fileList.add(geneInfo);
            }
        }
        catch (Exception e) {
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void getSegFileListUrl(String type, String url, ArrayList fileList) {
        try {
            String segUrl = url + "/" + type;
            String filelist = segUrl + "/filelist";
            DataInputStream in = new DataInputStream(new URL(filelist).openStream());
            BufferedReader inSeg = new BufferedReader(new InputStreamReader(in));

            ArrayList workFileList = new ArrayList();
            String colorTabOrg = "";

            // URL ����ǡ�����ꤳ��
            String buf;
            for(;;) {
                buf = inSeg.readLine();
                if (buf == null) {
                    // EOF ����
                    break;
                }
                workFileList.add(buf);

                // colorTab
                if (buf.equals("colorTab")) {
                    colorTabOrg = segUrl + "/colorTab";
                }
            }

            // seg �ǡ�������
            for(int i = 0; i < workFileList.size(); i++) {
                String file =(String)workFileList.get(i);
                if (file.startsWith("seg.") == false) {
                    // type �ǡ����ե�����Ǥʤ�
                    continue;
                }

                // spec ̾��
                String spec = file.substring("seg.".length());
                if (file.endsWith(".gz")) {
                    spec = spec.substring(0, spec.length() - ".gz".length());
                }
                if (0 <= spec.indexOf(".")) {
                    // spec �� '.' ���ޤޤ��  --->  seg �ǡ����ե�����ǤϤʤ�
                    continue;
                }

                // colorTab
                String colorTab = segUrl + "/colorTab." + spec;
                if (selectFile("colorTab." + spec, workFileList) == null) {
                    // �������� chromosome �ե����뤬������ʤ�
                    colorTab = colorTabOrg;
                }

                // type �ǡ����ե�����
                HashMap segInfo = new HashMap();
                segInfo.put("SPEC", spec);
                segInfo.put("SEG", segUrl + "/" + file);
                segInfo.put("COLORTAB", colorTab);

                fileList.add(segInfo);
            }
        }
        catch (Exception e) {
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void getAlignFileListDisk(String path, ArrayList fileList) {
        String sep  = System.getProperty("file.separator");
        String dirDb = path + sep + "database";

        // PATH/align �ǥ��쥯�ȥ꤬���뤫��
        String dirAlign = dirDb + sep + "align";
        File alignDirFile = new File(dirAlign);
        if (! alignDirFile.isDirectory()) {
            // align �ǥ��쥯�ȥ꤬¸�ߤ��ʤ�
            return;
        }

        // colorTab �ե�����
        String colorTab = dirAlign + sep + "colorTab";
        File colorTabFile = new File(colorTab);
        if (! colorTabFile.isFile()) {
            // �ե����뤬�ʤ� or �ե�����ǤϤʤ�
            colorTab = "";
        }

        // align �ǡ�������
        String alignFileList[];
        alignFileList = alignDirFile.list();
        Arrays.sort(alignFileList);
        for(int i = 0; i < alignFileList.length; i++) {
            String file = alignFileList[i];
            if (file.startsWith(".")) {
                continue;
            }

            if (file.endsWith(".gz")) {
                file = file.substring(0, file.length() - ".gz".length());
            }

            // spec1, spec2 ̾��
            int sepIdx = file.indexOf("-");
            if (sepIdx < 0) {
                continue;
            }
            String spec1, spec2;
            spec1 = file.substring(file.indexOf(".") + 1, sepIdx);
            spec2 = file.substring(sepIdx + 1);
            if (0 <= spec2.indexOf(".")) {
                // spec2 �� '.' ���ޤޤ��  --->  align �ǡ����ե�����ǤϤʤ�
                continue;
            }

            // align �ǡ����ե�����
            addAlignData(spec1, spec2, path, file, colorTab, fileList);
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void getGeneFileListDisk(String path, ArrayList fileList) {
        String sep  = System.getProperty("file.separator");
        String dirDb = path + sep + "database";

        // PATH/gene �ǥ��쥯�ȥ꤬���뤫��
        String dirGene = dirDb + sep + "genes";
        File geneDirFile = new File(dirGene);
        if (! geneDirFile.isDirectory()) {
            // gene �ǥ��쥯�ȥ꤬¸�ߤ��ʤ�
            return;
        }

        // colorTab �ե�����
        String colorTabOrg = dirDb + sep + "colorTab";
        File colorTabFile = new File(colorTabOrg);
        if (! colorTabFile.isFile()) {
            // �ե����뤬�ʤ� or �ե�����ǤϤʤ�
            colorTabOrg = "";
        }

        // gene �ǡ�������
        String geneFileList[];
        geneFileList = geneDirFile.list();
        for(int i = 0; i < geneFileList.length; i++) {
            if (geneFileList[i].startsWith("gene.") == false) {
                // gene �ǡ����ե�����Ǥʤ�
                continue;
            }

            // spec ̾��
            String spec = geneFileList[i].substring("gene.".length());
            if (geneFileList[i].endsWith(".gz")) {
                spec = spec.substring(0, spec.length() - ".gz".length());
            }
            if (0 <= spec.indexOf(".")) {
                // spec �� '.' ���ޤޤ��  --->  gene �ǡ����ե�����ǤϤʤ�
                continue;
            }

            // chromosome
            String chromosome = dirGene + sep + "chromosome." + spec;
            File chromosomeFile = new File(chromosome);
            if (! chromosomeFile.isFile()) {
                // chromosome �ǡ�����̵��
                continue;
            }

            // colorTab
            String colorTab = dirGene + sep + "colorTab." + spec;
            colorTabFile = new File(colorTab);
            if (! colorTabFile.isFile()) {
                colorTab = colorTabOrg;
            }

            // gene �ǡ����ե�����
            HashMap geneInfo = new HashMap();
            geneInfo.put("SPEC", spec);
            geneInfo.put("GENE", dirGene + sep + geneFileList[i]);
            geneInfo.put("CHROMOSOME", chromosome);
            geneInfo.put("COLORTAB", colorTab);

            fileList.add(geneInfo);
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void getSegFileListDisk(String type, String path, ArrayList fileList) {
        String sep  = System.getProperty("file.separator");
        String dirDb = path + sep + "database";

        // PATH/type �ǥ��쥯�ȥ꤬���뤫��
        String dirSeg = dirDb + sep + type;
        File segDirFile = new File(dirSeg);
        if (! segDirFile.isDirectory()) {
            // type �ǥ��쥯�ȥ꤬¸�ߤ��ʤ�
            return;
        }

        // colorTab �ե�����
        String colorTabOrg = dirSeg + sep + "colorTab";
        File colorTabFile = new File(colorTabOrg);
        if (! colorTabFile.isFile()) {
            // �ե����뤬�ʤ� or �ե�����ǤϤʤ�
            colorTabOrg = "";
        }

        // type �ǡ�������
        String segFileList[];
        segFileList = segDirFile.list();
        for(int i = 0; i < segFileList.length; i++) {
            if (segFileList[i].startsWith("seg.") == false) {
                // type �ǡ����ե�����Ǥʤ�
                continue;
            }

            // spec ̾��
            String spec = segFileList[i].substring("seg.".length());
            if (segFileList[i].endsWith(".gz")) {
                spec = spec.substring(0, spec.length() - ".gz".length());
            }
            if (0 <= spec.indexOf(".")) {
                // spec �� '.' ���ޤޤ��  --->  seg �ǡ����ե�����ǤϤʤ�
                continue;
            }

            // colorTab
            String colorTab = dirSeg + sep + "colorTab." + spec;
            colorTabFile = new File(colorTab);
            if (! colorTabFile.isFile()) {
                colorTab = colorTabOrg;
            }

            // type �ǡ����ե�����
            HashMap segInfo = new HashMap();
            segInfo.put("SPEC", spec);
            segInfo.put("SEG", dirSeg + sep + segFileList[i]);
            segInfo.put("COLORTAB", colorTab);

            fileList.add(segInfo);
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected String selectFile(String filename, ArrayList fileList) {
        for(int i = 0; i < fileList.size(); i++) {
            String file = (String)fileList.get(i);
            if (file.equals(filename)) {
                return(file);
            }
        }
        return(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected HashMap selectRegInfo(ArrayList fileList, String spec) {
        HashMap selectInfo = null;

        for(int i = 0; i < fileList.size(); i++) {
            HashMap info = (HashMap)fileList.get(i);
            if (spec.equals((String)info.get("SPEC"))) {
                // �����ǡ�������
                selectInfo = info;
                break;
            }
        }
        if (selectInfo == null) {
            Dbg.println(1, "not found : "+spec);
        }
        return(selectInfo);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public HashMap getSelectSpecInfo() {
        return(selectSpecInfo);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectGeneAttr() {
        return(selectGeneAttr);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectGeneColorType() {
        return(selectGeneColorType);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectSegName(int idx) {
        return selectSegName[idx];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectSegCgi(int idx) {
        return selectSegCgi[idx];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
//    public String getSelectedSegName(int idx) {
//        String name = null;
//
//        name = (String)seg[idx].getSelectedItem();
//
//        return(name);
//    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSelectSegType(int idx) {

        return selectSeg[idx].getSelectedSegName();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDefault(String sp1, String sp2, String prog) {
        defaultSp1  = sp1;
        defaultSp2  = sp2;
        defaultProg = prog;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ListActionServer implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox jcb = (JComboBox)e.getSource();

            String basePath = (String)jcb.getSelectedItem();
            mbgdDataMng.setBasePath(basePath);

            _updateSpec();
        }
    }

}
