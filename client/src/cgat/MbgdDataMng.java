package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
// CGAT が扱うデータの管理
//   Alignment
//   Region(Gene/Segment)
// 内部で保持しているデータを表示上のデータ（基準生物種か否か）の対応付けを管理する
//
//
// CGAT では、このクラスを介して、データの取得を行う
public class MbgdDataMng extends Observable implements ActionListener, Observer{
    protected String CgatHome = "";
    protected Properties cgatProperties = new Properties();
    protected           int     use_color = ColorTab.USE_COLOR_DARK;

    public static final int     MAX_SEGS   = 5;
    public static       int     NUM_SEGS   = 3;

    public static final int     SIDE0      = 0;         // from1, to1 側
    public static final int     SIDE1      = 1;         // from2, to2 側

    public static final boolean BASE_SPEC       = true;       // 基準生物種
    public static final int     BASE_ALIGN      =  1;
    public static final int     BASE_GENE       =  2;
    public static final int     BASE_GENEATTR   =  3;
    public static final int     BASE_SEG1       =  4;
    public static final int     BASE_SEG2       =  5;
    public static final int     BASE_SEG3       =  6;
    public static final int     BASE_SEG4       =  7;
    public static final int     BASE_SEG5       =  8;

    public static final boolean OPPO_SPEC       = false;      // 他方(Opposite)の生物種
    public static final int     OPPO_ALIGN      = 11;
    public static final int     OPPO_GENE       = 12;
    public static final int     OPPO_GENEATTR   = 13;
    public static final int     OPPO_SEG1       = 14;
    public static final int     OPPO_SEG2       = 15;
    public static final int     OPPO_SEG3       = 16;
    public static final int     OPPO_SEG4       = 17;
    public static final int     OPPO_SEG5       = 18;

    //
    public static final String  OPT_DEBUG         = "cgat.debug";
    public static final String  OPT_DIR_HOME      = "cgat.dir.home";
    public static final String  OPT_URL_HOME      = "cgat.url.home";
    public static final int     MAX_URL_HOME      = 10;
    public static final String  OPT_URL_PROXY     = "cgat.url.proxy";
    public static final String  OPT_URL_WEBSITE   = "cgat.url.website";
    public static final String  OPT_URL_MANUAL    = "cgat.url.manual";
    public static final String  OPT_ABOUT_CGAT    = "cgat.about";
    public static final String  OPT_MSG_BROWSERPATH = "cgat.msg.browserpath";

    //
    public static final String  OPT_CMD_BROWSER_USER  = "cgat.cmd.browser.user";
    public static final String  OPT_CMD_BROWSER_WIN   = "cgat.cmd.browser.win";
    public static final String  OPT_CMD_BROWSER_MAC   = "cgat.cmd.browser.mac";
    public static final String  OPT_CMD_BROWSER_UNIX  = "cgat.cmd.browser.unix";

    //
    public static final String  OPT_BG_DARK       = "cgat.color.bg.dark";
    public static final String  OPT_BG_LIGHT      = "cgat.color.bg.light";
    public static final String  OPT_DP_FRAME_DARK = "cgat.color.dotplot.frame.dark";
    public static final String  OPT_DP_FRAME_LIGHT= "cgat.color.dotplot.frame.light";

    //
    public static final String  OPT_AL_SEQ_MAT_DARK = "cgat.alignment.sequence.color.match.dark";
    public static final String  OPT_AL_SEQ_MIS_DARK = "cgat.alignment.sequence.color.mismatch.dark";
    public static final String  OPT_AL_SEQ_GAP_DARK = "cgat.alignment.sequence.color.gap.dark";
    public static final String  OPT_AL_SEQ_MAT_LIGHT = "cgat.alignment.sequence.color.match.light";
    public static final String  OPT_AL_SEQ_MIS_LIGHT = "cgat.alignment.sequence.color.mismatch.light";
    public static final String  OPT_AL_SEQ_GAP_LIGHT = "cgat.alignment.sequence.color.gap.light";
    public static final String  OPT_AL_ID_H_DARK = "cgat.alignment.identity.color.high.dark";
    public static final String  OPT_AL_ID_M_DARK = "cgat.alignment.identity.color.middle.dark";
    public static final String  OPT_AL_ID_L_DARK = "cgat.alignment.identity.color.low.dark";
    public static final String  OPT_AL_ID_H_LIGHT = "cgat.alignment.identity.color.high.light";
    public static final String  OPT_AL_ID_M_LIGHT = "cgat.alignment.identity.color.middle.light";
    public static final String  OPT_AL_ID_L_LIGHT = "cgat.alignment.identity.color.low.light";
    public static final String  OPT_AL_ID_H_PERCENT = "cgat.alignment.identity.high.percent";
    public static final String  OPT_AL_ID_M_PERCENT = "cgat.alignment.identity.middle.percent";
    public static final String  OPT_AL_ID_L_PERCENT = "cgat.alignment.identity.low.percent";
    public static final String  OPT_GENE_ATTR_H_DARK = "cgat.gene.attr.color.high.dark";
    public static final String  OPT_GENE_ATTR_L_DARK = "cgat.gene.attr.color.low.dark";
    public static final String  OPT_GENE_ATTR_H_LIGHT = "cgat.gene.attr.color.high.light";
    public static final String  OPT_GENE_ATTR_L_LIGHT = "cgat.gene.attr.color.low.light";

    //
    public static final String  OPT_TT_LEFT       = "cgat.tooltip.left";
    public static final String  OPT_TT_RIGHT      = "cgat.tooltip.right";
    public static final String  OPT_TT_REG_UP     = "cgat.tooltip.region.up";
    public static final String  OPT_TT_REG_DOWN   = "cgat.tooltip.region.down";
    public static final String  OPT_TT_DOTPLOT    = "cgat.tooltip.dotplot";
    public static final String  OPT_TT_DP_UP      = "cgat.tooltip.dotplot.up";
    public static final String  OPT_TT_DP_DOWN    = "cgat.tooltip.dotplot.down";
    public static final String  OPT_TT_DP_X       = "cgat.tooltip.dotplot.x";
    public static final String  OPT_TT_DP_Y       = "cgat.tooltip.dotplot.y";
    public static final String  OPT_TT_COLOR      = "cgat.tooltip.color";
    public static final String  OPT_TT_COLOR_BG   = "cgat.tooltip.color.bg";
    public static final String  OPT_TT_GENE_TAB   = "cgat.tooltip.genetab";
    public static final String  OPT_TT_ALIGN      = "cgat.tooltip.alignment";
    public static final String  OPT_TT_EXCHANGE   = "cgat.tooltip.exchange";

    //
    public static final String  OPT_MAX_SEGMENTS  = "cgat.max.segments";
    public static final String  OPT_PANEL_ALIGN_H = "cgat.panel.alignment.alignment.height";
    public static final String  OPT_PANEL_GENE_H  = "cgat.panel.alignment.gene.height";
    public static final String  OPT_PANEL_SEGMENT_H = "cgat.panel.alignment.segment.height";

    //
    public static final String  OPT_DP_MATCH     = "cgat.dp.match";
    public static final String  OPT_DP_MISMATCH  = "cgat.dp.mismatch";
    public static final String  OPT_DP_OPENGAP   = "cgat.dp.opengap";
    public static final String  OPT_DP_EXTGAP    = "cgat.dp.extgap";
    public static final String  OPT_DP_EDGEGAP   = "cgat.dp.edgegap";
    public static final String  OPT_AL_MAX_REALIGN = "cgat.alignment.max.realignment";

    //
    public static final String  OPT_PANEL_GENESET_YOFS  = "cgat.panel.alignment.geneset.yofs";

    //
    public static final String  OPT_MBGD_URL_GENE = "mbgd.url.gene";

    //
    private static MbgdDataMng _instance = null;
    protected String docBase = "";
    protected String codeBase = "";
    protected String basePath = "";
    protected String proxy = "";

    // 基準生物種
    private int selectedSpec = SIDE0;               // 基準生物種
    private int oppositeSpec = SIDE1;               //

    private interface LOADSTAT {
	static int NONE = 0, SPECNAME = 1, ALIGNMENT = 2, GENOME = 3, GENE = 4,
		GENEATTR = 5, SEGMENT = 6, DONE = 10;
    }
    private boolean alignViewerMode;

    //
    protected int minAlignGap = 30;

    //
	protected int alignColorMode = 0;


    // ダウンロードしたデータ
    protected boolean staSameSpecPair = false;
//    private boolean         isReady = false;        // データの準備完了？
    private int         loadStatus = LOADSTAT.NONE;     // データの準備完了？
    private String          spFileName = null;          //
    private RGB             rgb;
    private String          loadedSp1 = "";             // 生物種名
    private String          loadedSp2 = "";             // 生物種名
    private String          specName[];                 // 生物種名
    private int             specChr[];
    private GenomeSequence  infoGenome[];               // Genome データ
    private AlignmentList   infoAlign;                  // Alignment データ
    private ColorTab        infoAlignColor;             // Alignment データ色情報
    private RegionInfoList  infoGene[];                 // Gene データ
    private ColorTab        infoGeneColor[];            // Gene データ色情報
    private GeneAttr        infoGeneAttr[];             // GeneAttr データ
    private ColorTab        infoGeneAttrColor[];        // GeneAttr データ色情報
    private int             maxSegNum;                  // Segment 数（最大）
    private int             segNum;                     // Segment 数（表示中）
    private RegionInfoList  infoSegs[][];               // Segment データ [spec][segNo]
    private ColorTab        infoSegsColor[][];          // Segment データ色情報

    private String          infoSegsName[];             // Segment データ名称

    private MarkEntList     infoSearchOrf[];            // Search ORF で検索した ORF

    private String          infoGeneUrl;                // Region をクリックして Jump する先
    private String          infoSegsUrl[];              // Region をクリックして Jump する先

    //
    protected boolean isFilterAlignment;
    protected boolean isFilterGene;
    protected boolean isFilterSegment[];

    //
	protected String CGI_getAlign;
	protected String CGI_getColorTab;
	protected String CGI_getSequence;
	protected String CGI_getRegInfoUrl;
	protected String CGI_getGene;
	protected String CGI_getGeneAttr;
	protected String CGI_getSegment;


    ///////////////////////////////////////////////////////////////////////////
    // Singleton パターンを適用
    public static MbgdDataMng Instance() {
        if (_instance == null) {
            _instance = new MbgdDataMng();
        }

        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private MbgdDataMng() {
        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        int i;
        int j;

        String dirCgat = "";
        try {
//            dirCgat = System.getenv("CGAT_HOME");
            dirCgat = System.getProperty("CGAT_HOME");
        }
        catch (Exception e) {
        }

        if ((dirCgat == null) || (dirCgat.trim().equals(""))) {
            dirCgat = getProperty(OPT_DIR_HOME);
        }
        if (dirCgat != null) {
            setCgatHome(dirCgat);
        }

        //
        loadProperties();
        int n = MAX_SEGS;

        rgb = null;

        specName            = new String[2];
        specChr             = new int[2];

        infoGenome          = new GenomeSequence[2];
        infoGene            = new RegionInfoList[2];
        infoGeneColor       = new ColorTab[2];
        infoGeneAttr        = new GeneAttr[2];
        infoGeneAttrColor   = new ColorTab[2];
        infoSegsName        = new String[n];
        infoSegs            = new RegionInfoList[2][n];
        infoSegsColor       = new ColorTab[2][n];

        infoSearchOrf       = new MarkEntList[2];

        // 変数初期化
        rgb             = null;
        infoAlign       = null;
        infoAlignColor  = null;
        for(i = 0; i < 2; i++) {
            specName[i]             = null;
            specChr[i]              = 1;
            infoGenome[i]           = null;
//            infoChromosome[i]       = null;
            infoGene[i]             = null;
            infoGeneColor[i]        = null;
            infoGeneAttr[i]         = null;
            infoGeneAttrColor[i]    = null;

            infoSearchOrf[i]        = null;

            for(j = 0; j < n; j++) {
                infoSegsName[j]     = null;
                infoSegs[i][j]      = null;
                infoSegsColor[i][j] = null;
            }
        }
        for(j = 0; j < n; j++) {
            infoSegsName[j]         = null;
        }

        infoSegsUrl = new String[n];

        //
        isFilterAlignment = false;
        isFilterGene      = false;
        isFilterSegment = new boolean[n];
        for(j = 0; j < n; j++) {
            isFilterSegment[j] = false;
        }

        setDefaultCgiPrograms();
    }

    ///////////////////////////////////////////////////////////////////////////
    // データの準備が完了したかを返す
    public boolean isReady() {
	return isReady(LOADSTAT.DONE);
    }
    public boolean isReady(int sta) {
        return (loadStatus >= sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    // [データの準備が完了] 状況の更新
    public void setReady(int sta) {
        loadStatus = sta;
        Dbg.println(1, "LoadStatus: "+sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void setSpecName(int side, String n) {
        if (n == null) {
            specName[side] = null;
        }
        else {
            specName[side] = n;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpecName(boolean basespec) {
        String spname;

        if (! isReady(LOADSTAT.SPECNAME)) {
            // データ準備が [未完了]
            return "";
        }

        if (basespec) {         // 基準生物種の生物種名
            spname = specName[selectedSpec];
        }
        else {
            spname = specName[oppositeSpec];
        }

        if (spname == null) {
            return "";
        }

        return spname;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpecNameInit(int side) {
        String spname;

        if (! isReady(LOADSTAT.SPECNAME)) {
            // データ準備が [未完了]
            return "";
        }

        try {
            spname = specName[side];
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            return "";
        }

        return spname;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpecFullName(boolean basespec) {
        String spname;

        if (! isReady()) {
            // データ準備が [未完了]
            return "";
        }

        if (basespec) {         // 基準生物種の生物種名
            spname = infoGenome[selectedSpec].getName();
        }
        else {
            spname = infoGenome[oppositeSpec].getName();
        }

        if (spname == null) {
            return "";
        }

        return spname;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpecName(int type) {

        if (! isReady()) {
            // データ準備が [未完了]
            return "";
        }

        switch (type) {
        case BASE_ALIGN:
        case BASE_GENE:
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            return getSpecName(BASE_SPEC);

        case OPPO_ALIGN:
        case OPPO_GENE:
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return getSpecName(OPPO_SPEC);
        }

        return "";
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSpecChr(int side, int chr) {
        specChr[side] = chr;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSpecChr(boolean basespec) {
        String spname;

        if (! isReady(LOADSTAT.SPECNAME)) {
            // データ準備が [未完了]
            return 1;
        }

        int chr;
        if (basespec) {         // 基準生物種の生物種名
            chr = specChr[selectedSpec];
        }
        else {
            chr = specChr[oppositeSpec];
        }

        return chr;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignAttrName(int idx) {
        if (! isReady()) {
            // データ準備が [未完了]
            return "";
        }

        return infoAlign.getAlignAttrName(idx);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void setMaxSegNum(int n) {
        maxSegNum = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected int getMaxSegNum() {
        return maxSegNum;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected synchronized void setSegNum(int n) {
        segNum = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected synchronized int getSegNum() {
        return segNum;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSegmentName(int idx, String name) {
        infoSegsName[idx] = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSegmentName(int idx) {
        if (infoSegsName == null) {
            return "";
        }

        try {
            if (infoSegsName[idx] == null) {
                return "";
            }
            return infoSegsName[idx];
        }
        catch (Exception e) {
            return "";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSpFileName(String name) {
        spFileName = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpFileName() {
        return spFileName;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 基準生物種の入れ替え
    private void exchangeBaseSpec() {
        int wk;

        wk           = selectedSpec;
        selectedSpec = oppositeSpec;
        oppositeSpec = wk;

        setChanged();
        notifyObservers(ViewWindow.CHANGE_BASESPEC);
    }

    ///////////////////////////////////////////////////////////////////////////
    // データをダウンロードする
    //
    //  sp1         :
    //  sp2         :
    //  url         :
    //  falign      :
    //  fgeneAttr   :
    //  segs[]      :
    public void load(JFrame f,
                        String sp1, String sp2,
                        String url,
                        String falign,
                        String geneAttrDir,
                        String geneAttrColorType,
                        String segType[],
                        String segCgi[]) {

        staSameSpecPair = false;
        if (sp1.equals(loadedSp1) &&
            sp2.equals(loadedSp2)) {
            staSameSpecPair = true;
        }
        loadedSp1 = sp1;
        loadedSp2 = sp2;

        //
        setSpFileName(falign);

        DataLoader dataLoader = new DataLoader(f, this);
        dataLoader.setInfo(sp1, sp2,
                            url,
                            falign,
                            geneAttrDir, geneAttrColorType,
                            segType, segCgi);

        // データの load 終了を監視する
        dataLoader.addObserver(this);

        // データの読み込み開始
        Thread thread = new Thread(dataLoader);
        thread.start();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void makeObj() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void execLoad(String sp1, String sp2,
                        String path,
                        String falign,
                        String geneAttrDir,
                        String geneAttrColorType,
                        String segType[],
                        String segCgi[]) {
        int sp1chr = getSpecChr(BASE_SPEC);
        int sp2chr = getSpecChr(OPPO_SPEC);
        String fileRgb;
        String fileAlign;
        String fileAlignSeq;
        String fileAlignColor;
        String fileGenome1 = null;
        String fileGenome2 = null;
        String fileChromosome = null;
        String fileGene1 = null;
        String fileGene2 = null;
        String fileGeneColor1 = null;
        String fileGeneColor2 = null;
        String fileGeneAttr1 = null;
        String fileGeneAttr2 = null;
        String fileGeneAttrColor1 = null;
        String fileGeneAttrColor2 = null;
        String fileGeneUrl = null;
        String fileSegUrl[] = new String[segCgi.length];
        boolean bSta;


        //
        String url = path;
        String sep = System.getProperty("file.separator");
        String dirCgat = getCgatHome();
        String dirDb  = dirCgat + sep + "database";
        String dirEtc = dirCgat + sep + "etc";
        if (path.startsWith("http")) {
            fileRgb = url + "rgb.txt";
            fileAlign      = url + CGI_getAlign
                           + "type=" + falign + "&"
                           + "spec=" + sp1 + "&spec=" + sp2 + "&"
                           + "sp1chr=" + sp1chr + "&"
                           + "sp2chr=" + sp2chr;
            fileAlignColor = url + CGI_getColorTab
                           + "type=align";
            fileGenome1 = url + CGI_getSequence
                       + "reg=" + sp1 + "&" + "chr=" + sp1chr;
            fileGenome2 = url + CGI_getSequence
                       + "reg=" + sp2 + "&" + "chr=" + sp2chr;
            fileGeneUrl = url + CGI_getRegInfoUrl
                        + "type=gene";
            fileGene1     = url + CGI_getGene
                          + "spec=" + sp1 + "&" + "chr=" + sp1chr;
            fileGeneColor1= url + CGI_getColorTab
                          + "type=gene&"
                          + "spec=" + sp1;
            fileGene2     = url + CGI_getGene
                          + "spec=" + sp2 + "&" + "chr=" + sp2chr;
            fileGeneColor2= url + CGI_getColorTab
                          + "type=gene&"
                          + "spec=" + sp2;
        }
        else {
            fileRgb = dirEtc + sep + "rgb.txt";
            fileAlign      = dirDb + sep + "align" + sep
                           + falign + "." + sp1 + "-" + sp2;
            fileAlignColor = dirEtc + sep + "colorTab" + sep + "colorTab.align";
            fileGenome1 = dirDb + sep + "genomes" + sep + sp1;
            fileGenome2 = dirDb + sep + "genomes" + sep + sp2;
            fileGeneUrl = "";
            fileGene1     = dirDb + sep + "genes" + sep + "tab" + sep + sp1;
            fileGeneColor1= dirEtc + sep + "colorTab" + sep + "colorTab.gene";
            fileGene2     = dirDb + sep + "genes" + sep + "tab" + sep + sp2;
            fileGeneColor2= dirEtc + sep + "colorTab" + sep + "colorTab.gene";
        }

        if (geneAttrDir != null) {
            if (geneAttrDir.endsWith("(Server)")) {
                String ga = geneAttrDir.replaceAll("\\(Server\\)", "");
                fileGeneAttr1 = url + CGI_getGeneAttr
                              + "type=" + ga + "&"
                              + "spec=" + sp1;
                fileGeneAttrColor1 = url + CGI_getColorTab
                                   + "typ=" + ga + "&"
                                   + "spec=" + sp1;
                fileGeneAttr2 = url + CGI_getGeneAttr
                              + "type=" + ga + "&"
                              + "spec=" + sp2;
                fileGeneAttrColor2 = url + CGI_getColorTab
                                   + "typ=" + ga + "&"
                                   + "spec=" + sp2;
            }
            else {
                fileGeneAttr1 = dirDb + sep + "geneattr" + sep + geneAttrDir + sep + "attr." + sp1;
                fileGeneAttrColor1 = "no_gene_attr";
                fileGeneAttr2 = dirDb + sep + "geneattr" + sep + geneAttrDir + sep + "attr." + sp2;
                fileGeneAttrColor2 = "no_gene_attr";
            }
        }

        for(int segNo = 0; segNo < segCgi.length; segNo++) {
            segCgi[segNo] = null;
            if (segType[segNo] == null) {
                continue;
            }
            if (segType[segNo].equalsIgnoreCase("No Data")) {
                continue;
            }
            String st = segType[segNo].replaceAll("\\(Server\\)", "");
            if (segType[segNo].endsWith("(Server)")) {
                segCgi[segNo] = url + CGI_getSegment
                                  + "type=" + st;
            }
            else {
                segCgi[segNo] = dirDb + sep + "segment" + sep + st + sep + "seg." + sp2;
            }
        }

        alignViewerMode = true;            // AlignmentViewer モード
        if (sp2 == null) {
            alignViewerMode = false;       // SequenceViewer モード
        }
        // [データロード開始] 状態
        setReady(LOADSTAT.NONE);

        // RGB 情報の取得
        if (rgb == null) {
            rgb = new RGB();
            try {
                URL u = this.getClass().getClassLoader().getResource("image/rgb.txt");
                BaseFile bf = new BaseFile(new BufferedReader(new InputStreamReader(u.openStream())));
                rgb.parse("rgb.txt", bf);
            }
            catch (IOException ioe) {
                rgb.load(fileRgb);
            }
        }

        // 生物種名を格納
        setSpecName(MbgdDataMng.SIDE0, sp1);
        setSpecName(MbgdDataMng.SIDE1, sp2);
        setReady(LOADSTAT.SPECNAME);

        // Alignment データ
        if (infoAlign == null) {
            infoAlign      = new AlignmentList();
            infoAlignColor = new ColorTab(rgb);
        }
        if (alignViewerMode) {
            fileAlignSeq   = falign;
            infoAlign.load(sp1, sp2, fileAlign, fileAlignSeq);
            infoAlignColor.load(fileAlignColor);
        }
        setReady(LOADSTAT.ALIGNMENT);

        // Genome データ
        if (infoGenome[MbgdDataMng.SIDE0] == null) {
            infoGenome[MbgdDataMng.SIDE0]  = new GenomeSequence("", "");
        }
        infoGenome[MbgdDataMng.SIDE0].load(fileGenome1);

        //
        if (infoGenome[MbgdDataMng.SIDE1] == null) {
            infoGenome[MbgdDataMng.SIDE1]  = new GenomeSequence("", "");
        }
        if (alignViewerMode) {
            infoGenome[MbgdDataMng.SIDE1].load(fileGenome2);
        }
        setReady(LOADSTAT.GENOME);

        // Web 上の gene データ
        try {
            infoGeneUrl = new UrlFile(fileGeneUrl).readLine().trim();
        }
        catch (Exception e) {
            infoGeneUrl = getProperty(OPT_MBGD_URL_GENE);
        }

        // Gene データ
        if (infoGene[MbgdDataMng.SIDE0] == null) {
            infoGene[MbgdDataMng.SIDE0]      = new RegionInfoList();
            infoGeneColor[MbgdDataMng.SIDE0] = new ColorTab(rgb);
            infoSearchOrf[MbgdDataMng.SIDE0] = new MarkEntList();
        }
        infoGene[MbgdDataMng.SIDE0].load(fileGene1);
        infoGene[MbgdDataMng.SIDE0].setDataName("gene");
        bSta = infoGeneColor[MbgdDataMng.SIDE0].load(fileGeneColor1);

        //
        if (infoGene[MbgdDataMng.SIDE1] == null) {
            infoGene[MbgdDataMng.SIDE1]      = new RegionInfoList();
            infoGeneColor[MbgdDataMng.SIDE1] = new ColorTab(rgb);
            infoSearchOrf[MbgdDataMng.SIDE1] = new MarkEntList();
        }
        if (alignViewerMode) {
            infoGene[MbgdDataMng.SIDE1].load(fileGene2);
            infoGene[MbgdDataMng.SIDE1].setDataName("gene");
            bSta = infoGeneColor[MbgdDataMng.SIDE1].load(fileGeneColor2);
        }
        setReady(LOADSTAT.GENE);

        // GeneAttr データ
        if (infoGeneAttr[MbgdDataMng.SIDE0] == null) {
            infoGeneAttr[MbgdDataMng.SIDE0]      = new GeneAttr();
            infoGeneAttrColor[MbgdDataMng.SIDE0] = new ColorTab(rgb);
        }
        if (infoGeneAttr[MbgdDataMng.SIDE1] == null) {
            infoGeneAttr[MbgdDataMng.SIDE1]      = new GeneAttr();
            infoGeneAttrColor[MbgdDataMng.SIDE1] = new ColorTab(rgb);
        }

        if (geneAttrDir != null) {
            // load GeneAttr
            infoGeneAttr[SIDE0].load(fileGeneAttr1);
            infoGene[MbgdDataMng.SIDE0].setDataName(geneAttrDir);

            // load GeneAttr color
            infoGeneAttr[SIDE0].setColorType(geneAttrColorType);
            bSta = infoGeneAttrColor[SIDE0].load(fileGeneAttrColor1);
            if (! bSta) {
                // colorTab が読み込めなかったので、デフォルトを設定する
                infoGeneAttrColor[SIDE0].setDefaultColorTab(infoGeneAttr[SIDE0].getMinValue(),
                                                                infoGeneAttr[SIDE0].getMaxValue());
                infoGene[MbgdDataMng.SIDE0].setDataName("float");
            }
            if (alignViewerMode) {
                // load GeneAttr
                infoGeneAttr[SIDE1].load(fileGeneAttr2);
                infoGene[MbgdDataMng.SIDE1].setDataName(geneAttrDir);

                // load GeneAttr color
                infoGeneAttr[SIDE1].setColorType(geneAttrColorType);
                bSta = infoGeneAttrColor[SIDE1].load(fileGeneAttrColor2);
                if (! bSta) {
                    // colorTab が読み込めなかったので、デフォルトを設定する
                    infoGeneAttrColor[SIDE1].setDefaultColorTab(infoGeneAttr[SIDE1].getMinValue(),
                                                                infoGeneAttr[SIDE1].getMaxValue());
                    infoGene[MbgdDataMng.SIDE0].setDataName("float");
                }
            }
        }
        else {
            infoGeneAttr[SIDE0].clear();
            infoGeneAttr[SIDE1].clear();
        }
        setReady(LOADSTAT.GENEATTR);

    	loadSegment(path, segType, segCgi);
        setReady(LOADSTAT.SEGMENT);

        // [データロード終了] 状態
        setReady(LOADSTAT.DONE);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadSegment(String path, String[] segType, String[] segCgi){
        String sep = System.getProperty("file.separator");
        String dirCgat = getCgatHome();
        String dirDb  = dirCgat + sep + "database";
        String fileSeg;

        int loopMax = segCgi.length;
        Dbg.println(1, "Loop>>"+loopMax);
        for(int segNo = 0; segNo < loopMax; segNo++) {
Dbg.println(1, ">"+segNo+" "+segCgi[segNo]);
            if ((segType[segNo] == null) ||
                 segType[segNo].equals("No Data")) {
                continue;
            }

            String st = segType[segNo].replaceAll("\\(Server\\)", "");
            if (segType[segNo].endsWith("(Server)")) {
                fileSeg = path + "/cgi-bin/getRegInfoUrl.cgi?type="+st;
            }
            else {
                fileSeg = path + sep +  "database" + sep + "segment" + sep +st;
segCgi[segNo] = null;
            }
Dbg.println(3, fileSeg+" "+segNo);
            try {
               infoSegsUrl[segNo] = new UrlFile(fileSeg).readLine().trim();
            }
            catch (Exception e) {
                infoSegsUrl[segNo] = "";
            }
Dbg.println(3, infoSegsUrl[segNo]+" "+segNo);

            loadSegment0(SIDE0, segNo, path, st, segCgi[segNo]);
            if (alignViewerMode) {
                loadSegment0(SIDE1, segNo, path, st, segCgi[segNo]);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadSegment0(int seqNum, int segNo, String path,
		String segType, String segCgi){
        // Segment データ
	    String sp = getSpecName( isBaseSpec(seqNum) );
        String fileSegment = null;
        String fileSegmentColor = null;

        //
        String sep = System.getProperty("file.separator");
        String dirCgat = getCgatHome();
        String dirDb  = dirCgat + sep + "database";
        String dirEtc = dirCgat + sep + "etc";

            if (infoSegs[seqNum][segNo] == null) {
                infoSegs[seqNum][segNo]      = new RegionInfoList();
                infoSegsColor[seqNum][segNo] = new ColorTab(rgb);
            }

            if ((segType == null) || segType.equals("")) {
               		return;
            }

//            if (path.startsWith("http")) {
            if (segCgi != null) {
                // Web 上の segment データ(CGI により取得)
                fileSegment      = segCgi;
                if (fileSegment.endsWith(".cgi")) {
                    fileSegment += "?";
                }
                fileSegment += "&spec=" + sp;
                fileSegmentColor = path + "/cgi-bin/getColorTab.cgi?"
                                 + "type=" + segType + "&"
                                 + "spec=" + sp;
            }
            else {
                fileSegment      = dirDb + sep + "segment" + sep + segType + sep + "seg." + sp;
                fileSegmentColor = dirEtc + sep + "colorTab" + sep + "colorTab." + segType;
            }

            Dbg.println(3, "fileSegment      :: " + fileSegment);
            Dbg.println(3, "fileSegmentColor :: " + fileSegmentColor);
            infoSegs[seqNum][segNo].load(fileSegment);
            infoSegs[seqNum][segNo].setDataName(segType);
            boolean bSta = infoSegsColor[seqNum][segNo].load(fileSegmentColor);
            if (! bSta) {
                infoSegsColor[seqNum][segNo].load(fileSegmentColor);
            }

            // 色の自動割り当て(seg1, seg2)
            ColorAssign colAssignSeg = new ColorAssign();

            int loopMax = infoSegs[seqNum][segNo].size();
            for(int idx = 0; idx < loopMax; idx++) {
                RegionInfo r = infoSegs[seqNum][segNo].getRegionInfo(idx);
                colAssignSeg.countColorType(r.getColor());
            }

            // 色のアサイン
            colAssignSeg.assignColor();

            // assign した色情報を ColorTab に登録
            infoSegsColor[seqNum][segNo].setColorAssign(colAssignSeg);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadAlignmentSeq(int f1, int t1, int f2, int t2) {
        //
        String name1 = getSpecName(BASE_SPEC);
        String name2 = getSpecName(OPPO_SPEC);

        //
        loadAlignmentSeq(name1, f1, t1, name2, f2, t2);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadAlignmentSeq(String name1, int f1, int t1, String name2, int f2, int t2) {
//        MbgdData mbgdData = MbgdData.Instance();
//        String server = mbgdData.getServerUrl();

        //
//        infoAlign.loadAlignSeqServer(server, getSpFileName(), name1, f1, t1, name2, f2, t2);
        infoAlign.loadAlignSeq(name1, f1, t1, name2, f2, t2);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignmentSeq(int f1, int t1, int f2, int t2, boolean side) {
        //
        String name1 = getSpecName(BASE_SPEC);
        String name2 = getSpecName(OPPO_SPEC);

        //
        String seq;
        seq = getAlignmentSeq(name1, f1, t1, name2, f2, t2, side);

        return seq;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignSeq getAlignSeq(String name1, int f1, int t1,
                                String name2, int f2, int t2) {
        AlignSeq as = infoAlign.getAlignSeq(name1, f1, t1,
                                            name2, f2, t2);
        return as;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignmentSeq(String name1, int f1, int t1, String name2, int f2, int t2, boolean side) {
        MbgdData mbgdData = MbgdData.Instance();
        String server = mbgdData.getServerUrl();

        //
        String seq;

        AlignSeq as = infoAlign.getAlignSeq(name1, f1, t1, name2, f2, t2);
        if (side) {
              seq = as.getSeq1();
        }
        else {
              seq = as.getSeq2();
        }
        return seq;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getGenomeSequence(boolean basespec, int from, int to) {
        String subSeq;

        if (! isReady(LOADSTAT.GENOME)) {
            // データ準備が [未完了]
            return "";
        }

        if (basespec) {         // 基準生物種のデータが要求
            subSeq = infoGenome[selectedSpec].getSequence(from, to);
        }
        else {
            subSeq = infoGenome[oppositeSpec].getSequence(from, to);
        }

        return subSeq;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getGenomeLength(boolean basespec) {
        if (! isReady(LOADSTAT.GENOME)) {
            // データ準備が [未完了]
            return 0;
        }

        if (basespec) {         // 基準生物種のデータが要求
            return infoGenome[selectedSpec].getLength();
        }
        else {
            return infoGenome[oppositeSpec].getLength();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getGenomeLength(int type) {
        boolean side;

        if (! isReady(LOADSTAT.GENOME)) {
            // データ準備が [未完了]
            return 0;
        }

        switch(type) {
        case MbgdDataMng.BASE_ALIGN:
        case MbgdDataMng.BASE_GENE:
        case MbgdDataMng.BASE_SEG1:
        case MbgdDataMng.BASE_SEG2:
        case MbgdDataMng.BASE_SEG3:
        case MbgdDataMng.BASE_SEG4:
        case MbgdDataMng.BASE_SEG5:
            side = true;
            break;

        case MbgdDataMng.OPPO_ALIGN:
        case MbgdDataMng.OPPO_GENE:
        case MbgdDataMng.OPPO_SEG1:
        case MbgdDataMng.OPPO_SEG2:
        case MbgdDataMng.OPPO_SEG3:
        case MbgdDataMng.OPPO_SEG4:
        case MbgdDataMng.OPPO_SEG5:
        default:
            side = false;
            break;
        }

        return getGenomeLength(side);
    }

    ///////////////////////////////////////////////////////////////////////////
    // alignment データの取得
    public Alignment getAlignment(boolean basespec, String key) {
        Alignment info;

        if (! isReady(LOADSTAT.ALIGNMENT)) {
            // データ準備が [未完了]
            return null;
        }

        info = new Alignment(infoAlign.getAlignment(key));
        if (selectedSpec != SIDE0) {
            info.exchangeSpec();
        }

        return info;
    }

    ///////////////////////////////////////////////////////////////////////////
    // alignment データの取得
    public Alignment getAlignment(boolean basespec, int index) {
        Alignment info;

        if (! isReady(LOADSTAT.ALIGNMENT)) {
            // データ準備が [未完了]
            return null;
        }

        if (basespec) {         // 基準生物種のデータが要求
            if (selectedSpec == SIDE0) {        // 基準生物種が SIDE0 の場合
                info = new Alignment(infoAlign.getAlignment1(index));
            }
            else {
                info = new Alignment(infoAlign.getAlignment2(index));
                info.exchangeSpec();
            }
        }
        else {
            if (selectedSpec != SIDE0) {        // 基準生物種が SIDE0 でない場合
                info = new Alignment(infoAlign.getAlignment1(index));
            }
            else {
                info = new Alignment(infoAlign.getAlignment2(index));
                info.exchangeSpec();
            }
        }

        return info;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ArrayList getAlignment(int dataType, ViewWindow viewWin,  int from, int to) {
        ArrayList alignList;
        boolean basespec;

        switch (dataType) {
        case BASE_GENE:
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            basespec = true;
            break;

        case OPPO_GENE:
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            basespec = false;
            break;

        default:
            // 0 件 Hit した状態
            return new ArrayList();
        }

        if (from < to) {
            alignList = AlignmentListFilter.search(this, viewWin, basespec, from, to);
        }
        else {
            // 0 bp を挟んだ検索
            int genomeLength = getGenomeLength(basespec);
            ArrayList wk = AlignmentListFilter.search(this, viewWin, basespec, from, genomeLength);
            alignList = AlignmentListFilter.search(this, viewWin, basespec, 0, to);
            alignList.addAll(wk);
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getAlignmentSize() {
        if (! isReady(LOADSTAT.ALIGNMENT)) {
            // データ準備が [未完了]
            return 0;
        }

        return infoAlign.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getAlignAttrDataType(String nameItem) {
        boolean dataType;

        if (! isReady(LOADSTAT.ALIGNMENT)) {
            // データ準備が [未完了]
            return false;
        }

        dataType = infoAlign.getAttrDataType(nameItem);

        return dataType;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Gene データの取得
    public RegionInfo getGeneInfo(boolean basespec, int index) {
        RegionInfo info;

        if (! isReady(LOADSTAT.GENE)) {
            // データ準備が [未完了]
            return null;
        }

        if (basespec) {         // 基準生物種のデータが要求
            info = new RegionInfo(infoGene[selectedSpec].getRegionInfo(index));
        }
        else {
            info = new RegionInfo(infoGene[oppositeSpec].getRegionInfo(index));
        }

        return info;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Gene データの取得
    public RegionInfo getGeneInfo(boolean basespec, String orfName) {
        RegionInfo info;

        if (! isReady(LOADSTAT.GENE)) {
            // データ準備が [未完了]
            return null;
        }

        if (basespec) {         // 基準生物種のデータが要求
            info = infoGene[selectedSpec].getRegionInfo(orfName);
        }
        else {
            info = infoGene[oppositeSpec].getRegionInfo(orfName);
        }

        if (info == null) {
            return null;
        }
        return new RegionInfo(info);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Segment データの取得
    public RegionInfo getSegmentInfo(boolean basespec, int segNo, int index) {
        RegionInfo info;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return null;
        }

        if (basespec) {         // 基準生物種のデータが要求
            info = new RegionInfo(infoSegs[selectedSpec][segNo].getRegionInfo(index));
        }
        else {
            info = new RegionInfo(infoSegs[oppositeSpec][segNo].getRegionInfo(index));
        }

        return info;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Segment データの取得
    public RegionInfo getSegmentInfo(int type, int index) {
        RegionInfo info;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return null;
        }

        switch (type) {
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            return getSegmentInfo(true, type - BASE_SEG1, index);

        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return getSegmentInfo(false, type - OPPO_SEG1, index);
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegionMaxLane(int type) {

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return 1;
        }

        //
        int maxLane = 1;
        switch (type) {
        case BASE_GENE:
            maxLane = infoGene[selectedSpec].getMaxLane();
            break;
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            maxLane = infoSegs[selectedSpec][type - BASE_SEG1].getMaxLane();
            break;

        case OPPO_GENE:
            maxLane = infoGene[oppositeSpec].getMaxLane();
            break;
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            maxLane = infoSegs[oppositeSpec][type - OPPO_SEG1].getMaxLane();
            break;
        }

        if (maxLane <= 0) {
            maxLane = 1;
        }

        return maxLane;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfoList getRegionInfoList(int type) {

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return null;
        }

        switch (type) {
        case BASE_GENE:
            return infoGene[selectedSpec];
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            return infoSegs[selectedSpec][type - BASE_SEG1];

        case OPPO_GENE:
            return infoGene[oppositeSpec];
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return infoSegs[oppositeSpec][type - OPPO_SEG1];
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo getRegionInfo(int type, int index) {

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return null;
        }

        switch (type) {
        case BASE_GENE:
            return getGeneInfo(BASE_SPEC, index);
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            return getSegmentInfo(BASE_SPEC, type - BASE_SEG1, index);

        case OPPO_GENE:
            return getGeneInfo(OPPO_SPEC, index);
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return getSegmentInfo(OPPO_SPEC, type - OPPO_SEG1, index);
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSegAttrName(int type, int idxSeg, int idxItem) {
        String name = "unknown";

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return null;
        }

        switch (type) {
        case BASE_GENE:
            return infoGene[selectedSpec].getAttrName(idxItem);
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            try {
                return infoSegs[selectedSpec][idxSeg].getAttrName(idxItem);
            }
            catch (Exception e) {
                return null;
            }

        case OPPO_GENE:
            return infoGene[selectedSpec].getAttrName(idxItem);
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            try {
                return infoSegs[oppositeSpec][idxSeg].getAttrName(idxItem);
            }
            catch (Exception e) {
                return null;
            }
        }

        return name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getGeneAttrDataType(int idxItem) {
        boolean dataType;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return false;
        }

        dataType = infoGene[selectedSpec].getAttrDataType(idxItem);

        return dataType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getSegAttrDataType(int idxSeg, int idxItem) {
        boolean dataType;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return false;
        }

        dataType = infoSegs[selectedSpec][idxSeg].getAttrDataType(idxItem);

        return dataType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearFilterAlignment() {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        isFilterAlignment = false;
        infoAlign.clearFilter();
        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void filterAlignment(String condName, String nameAlignItem,
                                String str1) {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        isFilterAlignment = true;
        if (BaseFilterDialog.NAM_EQUAL.equals(condName)) {
            AlignmentFilter.filterEqual(infoAlign, nameAlignItem, str1);
        }
        else if (BaseFilterDialog.NAM_REGEX.equals(condName)) {
            AlignmentFilter.filterRegex(infoAlign, nameAlignItem, str1);
        }
        else if (BaseFilterDialog.NAM_SELECT.equals(condName)) {
            AlignmentFilter.filterSelect(infoAlign, nameAlignItem, str1);
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void filterAlignment(String condName, String nameAlignItem,
                                double val1) {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        isFilterAlignment = true;
        if (BaseFilterDialog.NAM_LESSTHAN.equals(condName)) {
            AlignmentFilter.filterLessThan(infoAlign, nameAlignItem, val1);
        }
        else if (BaseFilterDialog.NAM_LESSEQUAL.equals(condName)) {
            AlignmentFilter.filterLessEqual(infoAlign, nameAlignItem, val1);
        }
        else if (BaseFilterDialog.NAM_EQUAL.equals(condName)) {
            AlignmentFilter.filterEqual(infoAlign, nameAlignItem, val1);
        }
        else if (BaseFilterDialog.NAM_GREATEREQUAL.equals(condName)) {
            AlignmentFilter.filterGreaterEqual(infoAlign, nameAlignItem, val1);
        }
        else if (BaseFilterDialog.NAM_GREATERTHAN.equals(condName)) {
            AlignmentFilter.filterGreaterThan(infoAlign, nameAlignItem, val1);
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void filterAlignment(String condName, String nameAlignItem,
                                double val1, double val2) {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        isFilterAlignment = true;
        if (BaseFilterDialog.NAM_BETWEEN.equals(condName)) {
            AlignmentFilter.filterBetween(infoAlign, nameAlignItem, val1, val2);
        }
        else if (BaseFilterDialog.NAM_EXPTBETWEEN.equals(condName)) {
            AlignmentFilter.filterExceptBetween(infoAlign, nameAlignItem, val1, val2);
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearFilterGeneSegment(int idxSeg) {
        int i;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        if (idxSeg == 0) {
            isFilterGene = false;
            for(i = 0; i < 2; i++) {
                infoGene[i].clearFilter();
            }
        }
        else {
            isFilterSegment[idxSeg - 1] = false;
            for(i = 0; i < 2; i++) {
                infoSegs[i][idxSeg - 1].clearFilter();
            }
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearFilterAll() {
        // Clear Alignment
        clearFilterAlignment();

        // Clear Gene
        clearFilterGeneSegment(0);

        // Clear Segment
        int segNum = getSegNum();
        for(int i = 0; i < segNum; i++) {
            clearFilterGeneSegment(i + 1);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void filterGeneSegment(String condName, int idxSeg, int idxSegItem,
                                  String str1) {
        int i;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        if (BaseFilterDialog.NAM_EQUAL.equals(condName)) {
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterEqual(infoGene[i], idxSegItem, str1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterEqual(infoSegs[i][idxSeg - 1], idxSegItem, str1);
                }
            }
        }
        else if (BaseFilterDialog.NAM_REGEX.equals(condName)) {
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterRegex(infoGene[i], idxSegItem, str1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterRegex(infoSegs[i][idxSeg - 1], idxSegItem, str1);
                }
            }
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void filterGeneSegment(String condName, int idxSeg, int idxSegItem,
                                  double val1) {
        int i;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        if (BaseFilterDialog.NAM_LESSTHAN.equals(condName)) {
            //
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterLessThan(infoGene[i], idxSegItem, val1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterLessThan(infoSegs[i][idxSeg - 1], idxSegItem, val1);
                }
            }
        }
        else if (BaseFilterDialog.NAM_LESSEQUAL.equals(condName)) {
            //
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterLessEqual(infoGene[i], idxSegItem, val1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterLessEqual(infoSegs[i][idxSeg - 1], idxSegItem, val1);
                }
            }
        }
        else if (BaseFilterDialog.NAM_EQUAL.equals(condName)) {
            //
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterEqual(infoGene[i], idxSegItem, val1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterEqual(infoSegs[i][idxSeg - 1], idxSegItem, val1);
                }
            }
        }
        else if (BaseFilterDialog.NAM_GREATEREQUAL.equals(condName)) {
            //
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterGreaterEqual(infoGene[i], idxSegItem, val1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterGreaterEqual(infoSegs[i][idxSeg - 1], idxSegItem, val1);
                }
            }
        }
        else if (BaseFilterDialog.NAM_GREATERTHAN.equals(condName)) {
            //
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterGreaterThan(infoGene[i], idxSegItem, val1);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterGreaterThan(infoSegs[i][idxSeg - 1], idxSegItem, val1);
                }
            }
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void filterGeneSegment(String condName, int idxSeg, int idxSegItem,
                                  double val1, double val2) {
        int i;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return;
        }

        if (BaseFilterDialog.NAM_BETWEEN.equals(condName)) {
            // val1 <= VALUE <= val2
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterBetween(infoGene[i], idxSegItem, val1, val2);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterBetween(infoSegs[i][idxSeg - 1], idxSegItem, val1, val2);
                }
            }
        }
        else if (BaseFilterDialog.NAM_EXPTBETWEEN.equals(condName)) {
            // VALUE < val1 or val2 < VALUE
            if (idxSeg == 0) {
                isFilterGene = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterExceptBetween(infoGene[i], idxSegItem, val1, val2);
                }
            }
            else {
                isFilterSegment[idxSeg - 1] = true;
                for(i = 0; i < 2; i++) {
                    SegmentFilter.filterExceptBetween(infoSegs[i][idxSeg - 1], idxSegItem, val1, val2);
                }
            }
        }

        setChanged();
        notifyObservers();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getGeneAttrSize(int type) {
        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return 0;
        }

        switch (type) {
        case BASE_GENE:
            return infoGeneAttr[selectedSpec].size();

        case OPPO_GENE:
            return infoGeneAttr[oppositeSpec].size();

        default:
            return 0;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getGeneAttrMin(int type) {
        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return 0;
        }

        switch (type) {
        case BASE_GENE:
            return infoGeneAttr[selectedSpec].getMinValue();

        case OPPO_GENE:
            return infoGeneAttr[oppositeSpec].getMinValue();

        default:
            return 0;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getGeneAttrMax(int type) {
        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return 0;
        }

        switch (type) {
        case BASE_GENE:
            return infoGeneAttr[selectedSpec].getMaxValue();

        case OPPO_GENE:
            return infoGeneAttr[oppositeSpec].getMaxValue();

        default:
            return 0;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRegionSize(int type) {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return 0;
        }

        switch (type) {
        case BASE_GENE:
            return infoGene[selectedSpec].size();
        case BASE_GENEATTR:
            return infoGeneAttr[selectedSpec].size();
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            return infoSegs[selectedSpec][type - BASE_SEG1].size();

        case OPPO_GENE:
            return infoGene[oppositeSpec].size();
        case OPPO_GENEATTR:
            return infoGeneAttr[oppositeSpec].size();
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return infoSegs[oppositeSpec][type - OPPO_SEG1].size();
        }

        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getGeneAttrName(int type, int idx) {
        String name = "unknown";

        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return null;
        }

        switch (type) {
        case BASE_GENE:
            return infoGene[selectedSpec].getAttrName(idx);

        case OPPO_GENE:
            return infoGene[oppositeSpec].getAttrName(idx);
        }

        return name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ColorTab getAlignColor() {
        if (! isReady(LOADSTAT.ALIGNMENT)) {
            // データ準備が [未完了]
            return null;
        }

        return infoAlignColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getColorType(int type) {
        RegionInfoList info[];
        String colorType;
        int side;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return "";
        }

        switch (type) {
        case BASE_GENE:
            if (infoGeneAttr[selectedSpec].size() != 0) {
                return infoGeneAttr[selectedSpec].getColorType();
            }
            else {
                return infoGene[selectedSpec].getColorType();
            }
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            return infoSegs[selectedSpec][type - BASE_SEG1].getColorType();

        case OPPO_GENE:
            if (infoGeneAttr[oppositeSpec].size() != 0) {
                return infoGeneAttr[oppositeSpec].getColorType();
            }
            else {
                return infoGene[oppositeSpec].getColorType();
            }
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return infoSegs[oppositeSpec][type - OPPO_SEG1].getColorType();

        default:
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getGeneAttr(int type, String key) {
        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return null;
        }

        switch (type) {
        case BASE_GENE:
            return infoGeneAttr[selectedSpec].get(key);

        case OPPO_GENE:
            return infoGeneAttr[oppositeSpec].get(key);
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getGeneAttrColor(int type, String key, String colType) {
        Color c = Color.white;

        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return c;
        }

        String colId = getGeneAttr(type, key);

        switch (type) {
        case BASE_GENE:
            infoGeneAttrColor[selectedSpec].setUseColor(use_color);
            c = infoGeneAttrColor[selectedSpec].getColor(colId, colType);
            break;

        case OPPO_GENE:
            infoGeneAttrColor[oppositeSpec].setUseColor(use_color);
            c = infoGeneAttrColor[oppositeSpec].getColor(colId, colType);
            break;
        }

        return c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getGeneAttrColor(int type, float val) {
        Color c = Color.white;

        if (! isReady(LOADSTAT.GENEATTR)) {
            // データ準備が [未完了]
            return c;
        }

        switch (type) {
        case BASE_GENE:
            infoGeneAttrColor[selectedSpec].setUseColor(use_color);
            c = infoGeneAttrColor[selectedSpec].getColorFloat(val);
            break;

        case OPPO_GENE:
            infoGeneAttrColor[oppositeSpec].setUseColor(use_color);
            c = infoGeneAttrColor[oppositeSpec].getColorFloat(val);
            break;
        }

        return c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getAlignColor(int type, String colId, String colType, Alignment align) {
        Color c = Color.white;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return c;
        }

		if (getAlignColorMode() == 0) {
			infoAlignColor.setUseColor(use_color);
			c = infoAlignColor.getColor(colId, colType);
		}
		else {
            int l, m, h;
            if (use_color == ColorTab.USE_COLOR_DARK) {
                // DARK
                l = getPropertyInt(OPT_AL_ID_L_DARK, 16);
                m = getPropertyInt(OPT_AL_ID_M_DARK, 16);
                h = getPropertyInt(OPT_AL_ID_H_DARK, 16);
            }
            else {
                // LIGHT
                l = getPropertyInt(OPT_AL_ID_L_LIGHT, 16);
                m = getPropertyInt(OPT_AL_ID_M_LIGHT, 16);
                h = getPropertyInt(OPT_AL_ID_H_LIGHT, 16);
            }

            // 下限の色
            Color lCol = new Color(l);
            double lr = lCol.getRed();
            double lg = lCol.getGreen();
            double lb = lCol.getBlue();

            // 中間の色
            Color mCol = new Color(m);
            double mr = mCol.getRed();
            double mg = mCol.getGreen();
            double mb = mCol.getBlue();

            // 上限の色
            Color hCol = new Color(h);
            double hr = hCol.getRed();
            double hg = hCol.getGreen();
            double hb = hCol.getBlue();

            // lCol から hCol の間で無段階色
            double lIdent = Double.parseDouble(cgatProperties.getProperty(OPT_AL_ID_L_PERCENT));
            double mIdent = Double.parseDouble(cgatProperties.getProperty(OPT_AL_ID_M_PERCENT));
            double hIdent = Double.parseDouble(cgatProperties.getProperty(OPT_AL_ID_H_PERCENT));
            double ident = (double)align.getIdent();
            if (ident < lIdent) {
                return lCol;
            }
            else if (hIdent < ident) {
                return hCol;
            }

            double ratio;
            int r, g, b;
            if (ident < mIdent) {
                ratio = (ident - lIdent) / (mIdent - lIdent);
                r = (int)(lr + (mr - lr) * ratio);
                g = (int)(lg + (mg - lg) * ratio);
                b = (int)(lb + (mb - lb) * ratio);
            }
            else {
                ratio = (ident - mIdent) / (hIdent - mIdent);
                r = (int)(mr + (hr - mr) * ratio);
                g = (int)(mg + (hg - mg) * ratio);
                b = (int)(mb + (hb - mb) * ratio);
            }
            if (r < 0)   r = 0;
            if (255 < r) r = 255;
            if (g < 0)   g = 0;
            if (255 < g) g = 255;
            if (b < 0)   b = 0;
            if (255 < b) b = 255;
            c = new Color(r, g, b);
		}

		return c;
	}

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getColorLegend() {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return "";
        }

        // DARK
        String d, dl, dm, dh;
        dl = getProperty(OPT_AL_ID_L_DARK);
        dm = getProperty(OPT_AL_ID_M_DARK);
        dh = getProperty(OPT_AL_ID_H_DARK);

        // LIGHT
        String l, ll, lm, lh;
        ll = getProperty(OPT_AL_ID_L_LIGHT);
        lm = getProperty(OPT_AL_ID_M_LIGHT);
        lh = getProperty(OPT_AL_ID_H_LIGHT);

        // ident
        String ident;
        String lIdent = cgatProperties.getProperty(OPT_AL_ID_L_PERCENT);
        String mIdent = cgatProperties.getProperty(OPT_AL_ID_M_PERCENT);
        String hIdent = cgatProperties.getProperty(OPT_AL_ID_H_PERCENT);

        StringBuffer htmlLegend = new StringBuffer("");
        htmlLegend.append("<html>");
        htmlLegend.append("<head>");
        htmlLegend.append("</head>");
        htmlLegend.append("<body>");
        htmlLegend.append("<table border>");
        htmlLegend.append("<tr><th nowrap>Ident[%]</th>");
        htmlLegend.append("<th bgcolor=\"black\"><font color=\"white\">Background</font></th>");
        htmlLegend.append("<th bgcolor=\"white\"><font color=\"black\">Background</font></th>");
        htmlLegend.append("</tr>");

        ident = lIdent; d = dl; l = ll;
        htmlLegend.append("<tr><td align=\"right\">" + ident + "</td>");
        htmlLegend.append("<td bgcolor=\"#" + d + "\"></td>");
        htmlLegend.append("<td bgcolor=\"#" + l + "\"></td>");
        htmlLegend.append("</tr>");

        ident = mIdent; d = dm; l = lm;
        htmlLegend.append("<tr><td align=\"right\">" + ident + "</td>");
        htmlLegend.append("<td bgcolor=\"#" + d + "\"></td>");
        htmlLegend.append("<td bgcolor=\"#" + l + "\"></td>");
        htmlLegend.append("</tr>");

        ident = hIdent; d = dh; l = lh;
        htmlLegend.append("<tr><td align=\"right\">" + ident + "</td>");
        htmlLegend.append("<td bgcolor=\"#" + d + "\"></td>");
        htmlLegend.append("<td bgcolor=\"#" + l + "\"></td>");
        htmlLegend.append("</tr>");

        htmlLegend.append("</table>");
        htmlLegend.append("</body>");
        htmlLegend.append("</html>");

        return htmlLegend.toString();
	}

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getColor(int type, String colId, String colType) {
        Color c = Color.white;

        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return c;
        }

        switch (type) {
        case BASE_ALIGN:
            infoAlignColor.setUseColor(use_color);
            c = infoAlignColor.getColor(colId, colType);
            break;

        case BASE_GENE:
            if (infoGeneAttr[selectedSpec].size() != 0) {
                infoGeneAttrColor[selectedSpec].setUseColor(use_color);
                c = infoGeneAttrColor[selectedSpec].getColorFloat(colId);
                break;
            }
            else {
                infoGeneColor[selectedSpec].setUseColor(use_color);
                c = infoGeneColor[selectedSpec].getColor(colId, colType);
                break;
            }
        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
            infoSegsColor[selectedSpec][type - BASE_SEG1].setUseColor(use_color);
            c = infoSegsColor[selectedSpec][type - BASE_SEG1].getColor(colId, colType);
            break;

        case OPPO_GENE:
            if (infoGeneAttr[selectedSpec].size() != 0) {
                infoGeneAttrColor[oppositeSpec].setUseColor(use_color);
                c = infoGeneAttrColor[oppositeSpec].getColorFloat(colId);
                break;
            }
            else {
                infoGeneColor[oppositeSpec].setUseColor(use_color);
                c = infoGeneColor[oppositeSpec].getColor(colId, colType);
                break;
            }
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            infoSegsColor[oppositeSpec][type - OPPO_SEG1].setUseColor(use_color);
            c = infoSegsColor[oppositeSpec][type - OPPO_SEG1].getColor(colId, colType);
            break;
        }

        return c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getPattern(int type, String patId, String patType) {
        if (! isReady(LOADSTAT.SEGMENT)) {
            // データ準備が [未完了]
            return 0;
        }

        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getType(int type) {
        if (! isReady()) {
            // データ準備が [未完了]
            return null;
        }


        switch (type) {
        case BASE_ALIGN:
            return "align";

        case BASE_GENE:
        case OPPO_GENE:
            return infoGene[0].getDataName();

        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return infoSegs[0][type % 10 - BASE_SEG1].getDataName();
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment[] selectAlignList( int regStart1, int regWidth1, int regMax1,
                                        int regStart,  int regWidth,  int regMax) {
        Alignment alignList[];
        int i;
        boolean baseSpec;

        if (selectedSpec == this.SIDE0) {
            baseSpec = true;
        }
        else {
            baseSpec = false;
        }
        alignList = infoAlign.selectAlignList(  baseSpec,
                                                regStart1, regWidth1, regMax1,
                                                regStart, regWidth, regMax);

        // 基準生物種が入れ替えられている場合
        if (selectedSpec != SIDE0) {
            int loopMax = alignList.length;
            for(i = 0; i < loopMax; i++) {
                alignList[i].exchangeSpec();
            }
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment[] selectAlignList( int regStart1, int regWidth1, int regMax1) {
        Alignment alignList[];
        int i;
        boolean baseSpec;

        if (selectedSpec == this.SIDE0) {
            baseSpec = true;
        }
        else {
            baseSpec = false;
        }
        alignList = infoAlign.selectAlignList(  baseSpec,
                                                regStart1, regWidth1, regMax1);

        // 基準生物種が入れ替えられている場合
        if (selectedSpec != SIDE0) {
            int loopMax = alignList.length;
            for(i = 0; i < loopMax; i++) {
                alignList[i].exchangeSpec();
            }
        }

        return alignList;
    }

    public boolean isBaseSpec(int specNo) {
	return (specNo == selectedSpec);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearMarkEnt() {
        for(int i = 0; i < 2; i++) {
            if (infoSearchOrf[i] == null) {
                continue;
            }
            infoSearchOrf[i].clearMark();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void delMarkEnt(String n) {
        for(int i = 0; i < 2; i++) {
            if (infoSearchOrf[i] == null) {
                continue;
            }
            infoSearchOrf[i].delMark(n);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addMarkEnt(boolean basespec, MarkEnt ent) {
        if (basespec) {
            addMarkEnt(selectedSpec, ent);
        }
        else {
            addMarkEnt(oppositeSpec, ent);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addMarkEnt(int side, MarkEnt ent) {
        infoSearchOrf[side].addMark(ent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEnt getMarkEnt(int side, String orfName) {
        return infoSearchOrf[side].getMark(orfName);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEnt[] getMarkEntAll(boolean basespec) {
        if (! isReady()) {
            // データ準備が [未完了]
            return new MarkEnt[0];
        }

        if (basespec) {
            return infoSearchOrf[selectedSpec].getMarkAll();
        }
        else {
            return infoSearchOrf[oppositeSpec].getMarkAll();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        if (! isReady()) {
            // データ準備が [未完了]
            return;
        }

        // 基準生物種を入れ替える
        exchangeBaseSpec();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getRegInfoUrl(int dataType) {
        switch (dataType) {
        case BASE_GENE:
        case OPPO_GENE:
            return infoGeneUrl;

        case BASE_SEG1:
        case BASE_SEG2:
        case BASE_SEG3:
        case BASE_SEG4:
        case BASE_SEG5:
        case OPPO_SEG1:
        case OPPO_SEG2:
        case OPPO_SEG3:
        case OPPO_SEG4:
        case OPPO_SEG5:
            return infoSegsUrl[dataType % 10 - BASE_SEG1];

        default:
            return "";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(Observable o, Object arg) {
        if (o instanceof DataLoader) {
            update((DataLoader)o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(DataLoader dataLoader, Object arg) {
        // データの load が終了した
        for(int i = 0; i < 2; i++) {
            infoSearchOrf[i] = new MarkEntList();
        }

        setChanged();
        if (staSameSpecPair) {
            // 同じ生物種対：segment データを読み直した
            notifyObservers(ViewWindow.CHANGE_SEGMENT);
        }
        else {
            // 新しい生物種対を読み込んだ
            notifyObservers();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMinAlignGaps(int d) {
        minAlignGap = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    // alignment を描画する際、隣接する alignment との差が小さい場合
    // 矢先を描画しない。そのための判定基準
    public int getMinAlignGaps() {
        return minAlignGap;
    }

	///////////////////////////////////////////////////////////////////////////
	//
	public void setAlignColorMode(int mode) {
		alignColorMode = mode;

        setChanged();
        notifyObservers(ViewWindow.CHANGE_COLOR);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public int getAlignColorMode() {
		return alignColorMode;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void setUseColor(int uc) {
		use_color = uc;

        setChanged();
        notifyObservers(ViewWindow.CHANGE_COLOR);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public int getUseColor() {
		return use_color;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void updateProperties() {
        //
        int dbgLevel = getPropertyInt(OPT_DEBUG);
        Dbg.setDbgLevel(dbgLevel);

        //
        String cgatHome = "";
        try {
            // -D で指定した場合
            String wk = System.getProperty("CGAT_HOME");
            if (wk != null) {
                cgatHome = wk;
            }
        }
        catch (Exception eSysProp) {
        }
        if (cgatHome.equals("")) {
            String wk = getProperty(OPT_DIR_HOME);
            if (wk != null) {
                cgatHome = wk;
            }
        }
/* Java 1.5 環境では、環境変数を取得可能
   Java 1.4 環境では、Error となる(?)ためコメントアウト
   実行環境のバージョンチェック
        if (cgatHome.equals("")) {
            try {
                String wk = System.getenv("CGAT_HOME");
                if (wk != null) {
                    cgatHome = wk;
                }
            }
            catch (Exception eEnv) {
            }
        }
*/
        setCgatHome(cgatHome);

        //
        setProxy(getProperty(MbgdDataMng.OPT_URL_PROXY));

        //
        String browserUser = getProperty(MbgdDataMng.OPT_CMD_BROWSER_USER);
        if ((browserUser == null) || ("".equals(browserUser))) {
            String key;
            String osType = System.getProperty("os.name");
            if (osType.startsWith("Windows")) {
                // Windows
                key = MbgdDataMng.OPT_CMD_BROWSER_WIN;
            }
            else if (osType.startsWith("Mac")) {
                // Mac
                key = MbgdDataMng.OPT_CMD_BROWSER_MAC;
            }
            else {
                // のこりは、UNIX 系とする
                key = MbgdDataMng.OPT_CMD_BROWSER_UNIX;
            }
            String browser = getProperty(key);
            setProperty(MbgdDataMng.OPT_CMD_BROWSER_USER, browser);
        }

        //
        NUM_SEGS = getPropertyInt(OPT_MAX_SEGMENTS);
        if (NUM_SEGS < 1) {
            NUM_SEGS = 1;
        }
        if (5 < NUM_SEGS) {
            NUM_SEGS = 5;
        }
        setMaxSegNum(NUM_SEGS);

        if (infoGeneAttrColor != null) {
            if (infoGeneAttrColor[0] != null) {
                infoGeneAttrColor[0].updateDefaultColor();
            }
            if (infoGeneAttrColor[1] != null) {
                infoGeneAttrColor[1].updateDefaultColor();
            }
        }

        // Alignment/Gene/Segment Window Height
        setChanged();
        notifyObservers();
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void loadProperties() {
        String sep = System.getProperty("file.separator");
        String fileProperty;
        String dirCgatHome;

        // デフォルトプロパティー読込
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("cgat.properties");
            cgatProperties.load(is);
            Dbg.println(1, "Success :: loadProperties()");
        }
        catch (Exception eDefault) {
            Dbg.println(1, "Error :: loadProperties()");
        }

        // user.home のプロパティー読込(local)
        try {
            String dirHome = System.getProperty("user.home");
            fileProperty = dirHome + sep + ".cgat" + sep + "cgat.properties";
            InputStream is = new FileInputStream(fileProperty);
            cgatProperties.load(is);
            Dbg.println(1, "Success :: loadProperties() :: " + fileProperty);
        }
        catch (Exception eUserHome) {
            // ファイル読込でのエラーは無視する
        }

        // . のプロパティー読込(local)
        try {
            dirCgatHome = ".";
            fileProperty = dirCgatHome + sep + "cgat.properties";
            InputStream is = new FileInputStream(fileProperty);
            cgatProperties.load(is);
            Dbg.println(1, "Success :: loadProperties() :: " + fileProperty);
        }
        catch (Exception eCdir) {
            // ファイル読込でのエラーは無視する
        }

        // ユーザ指定(FILE_PARAM)のプロパティー読込
        try {
            fileProperty = System.getProperty("FILE_PARAM");
            InputStream is = new FileInputStream(fileProperty);
            cgatProperties.load(is);
            Dbg.println(1, "Success :: loadProperties() :: " + fileProperty);
        }
//        catch (SecurityException se) {
//        }
        catch (Exception eFileParam) {
            // ファイル読込でのエラーは無視する
        }

        // 前回入力したユーザプロパティー読込
        loadUserProperties();

        //
        updateProperties();
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void setProperty(Properties prop) {
        cgatProperties = prop;
        updateProperties();
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void setProperty(String key, String val) {
        if ((key == null) || (key.equals(""))) {
            return;
        }
        cgatProperties.setProperty(key, val);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public Properties getProperty() {
        return new Properties(cgatProperties);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public String getProperty(String key) {
        if ((key == null) || (key.equals(""))) {
            return null;
        }
        return cgatProperties.getProperty(key);
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public int getPropertyInt(String key) {
        if ((key == null) || (key.equals(""))) {
            return 0;
        }

        int v = 0;
        try {
            v = Integer.parseInt(cgatProperties.getProperty(key));
        }
        catch (NumberFormatException nfe) {
            System.err.println("NumberFormatException :: Property [" + key + "] :: " + cgatProperties.getProperty(key));
        }
        return v;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public int getPropertyInt(String key, int radix) {
        if ((key == null) || (key.equals(""))) {
            return 0;
        }

        int v = Integer.parseInt(cgatProperties.getProperty(key), radix);
        return v;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void setCgatHome(String h) {
        CgatHome = h;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public String getCgatHome() {
        return CgatHome;
	}

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setBasePath(String b) {
        if ((b != null) && ! b.endsWith("/")) {
            b += "/";
        }
        basePath = b;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getBasePath() {
        return basePath;
    }

	///////////////////////////////////////////////////////////////////////////
	//
	public void setDocBase(String db) {
        docBase = db;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public String getDocBase() {
        return docBase;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void setCodeBase(String cb) {
        codeBase = cb;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public String getCodeBase() {
        return codeBase;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	public void setBase(String db, String cb) {
        setDocBase(db);
        setCodeBase(cb);
	}

    ///////////////////////////////////////////////////////////////////////////
    //
    public void saveUserProperties() {
        try {
            String sep = System.getProperty("file.separator");
            String dirHome = System.getProperty("user.home");
            String key;
            String val;

            // ユーザプロパティー格納ディレクトリ
            File dirCgat = new File(dirHome + sep + ".cgat");
            dirCgat.mkdir();

            //
            Properties userProperty = new Properties();

            // プロパティーを保存
            String lstKey[] = { OPT_DIR_HOME,
                                OPT_URL_PROXY,
                                OPT_CMD_BROWSER_USER,
                                OPT_MBGD_URL_GENE,
                                OPT_BG_DARK,
                                OPT_BG_LIGHT,
                                OPT_DP_FRAME_DARK,
                                OPT_DP_FRAME_LIGHT,
                                OPT_AL_SEQ_MAT_DARK,
                                OPT_AL_SEQ_MIS_DARK,
                                OPT_AL_SEQ_GAP_DARK,
                                OPT_AL_SEQ_MAT_LIGHT,
                                OPT_AL_SEQ_MIS_LIGHT,
                                OPT_AL_SEQ_GAP_LIGHT,
                                OPT_AL_ID_H_DARK,
                                OPT_AL_ID_M_DARK,
                                OPT_AL_ID_L_DARK,
                                OPT_AL_ID_H_LIGHT,
                                OPT_AL_ID_M_LIGHT,
                                OPT_AL_ID_L_LIGHT,
                                OPT_AL_ID_H_PERCENT,
                                OPT_AL_ID_M_PERCENT,
                                OPT_AL_ID_L_PERCENT,
                                OPT_MAX_SEGMENTS,
                                OPT_PANEL_ALIGN_H,
                                OPT_PANEL_GENE_H,
                                OPT_PANEL_SEGMENT_H };
            for(int i = 0; i < lstKey.length; i++) {
                key = lstKey[i];
                val = getProperty(key);
                if (val == null) {
                    continue;
                }
                userProperty.setProperty(key, val);
            }

            for(int i = 0; i < MAX_URL_HOME; i++) {
                key = MbgdDataMng.OPT_URL_HOME + i;
                val = getProperty(key);
                if (val == null) {
                    continue;
                }
                userProperty.setProperty(key, val);
            }


            //
            File fileCgat = new File(dirCgat + sep + "user.properties");
            userProperty.store(new FileOutputStream(fileCgat), "cgat user properties");
            Dbg.println(1, "Success :: storeProperties() :: " + fileCgat);
        }
        catch (Exception e) {
Dbg.println(1, e.toString());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadUserProperties() {
        try {
            String sep = System.getProperty("file.separator");
            String dirHome = System.getProperty("user.home");
            String key;

            // ユーザプロパティー格納ディレクトリ
            File dirCgat = new File(dirHome + sep + ".cgat");
            File fileCgat = new File(dirCgat + sep + "user.properties");
            cgatProperties.load(new FileInputStream(fileCgat));
            Dbg.println(1, "Success :: loadProperties() :: " + fileCgat);
        }
        catch (Exception e) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setProxy(String p) {
        if (p == null) {
            p = "";
        }
        proxy = p.trim();

        //
        String host = "";
        String port = "80";
        if (p.startsWith("http")) {
            int idx = p.indexOf(":", 6);       // http: や https:
            int idx0 = p.indexOf("//");
            if (idx0 < 0) {
                idx0 = 0;
            }
            else {
                idx0 += 2;
            }
            host = p.substring(idx0, idx);
            port = p.substring(idx + 1);
        }
        else {
            host = p;
        }

        try {
            System.setProperty("http.proxyHost", host); 
            System.setProperty("http.proxyPort", port);
            Dbg.println(0, "http.proxyHost :: " + host);
            Dbg.println(0, "http.proxyPort :: " + port);
        }
        catch(AccessControlException ace) {
            // applet の場合、システムプロパティーを変更できないため
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getProxy() {
        return proxy;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isFilterAlignment() {
        return isFilterAlignment;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isFilterGene() {
        return isFilterGene;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isFilterSegment(int idx) {
        return isFilterSegment[idx];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDefaultCgiPrograms() {
		CGI_getAlign      = "/cgi-bin/getAlign.cgi?";
		CGI_getColorTab   = "/cgi-bin/getColorTab.cgi?";
		CGI_getSequence   = "/cgi-bin/getSequence.cgi?";
		CGI_getRegInfoUrl = "/cgi-bin/getRegInfoUrl.cgi?";
		CGI_getGene       = "/cgi-bin/getGene.cgi?";
		CGI_getGeneAttr   = "/cgi-bin/getGeneAttr.cgi?";
		CGI_getSegment    = "/cgi-bin/getSegment.cgi?";
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMbgdCgiPrograms(String clustid) {
        String opt = "clustid=" + clustid + "&";
		CGI_getAlign      = "/htbin/CGAT/getAlign.cgi?" + opt;
		CGI_getColorTab   = "/htbin/CGAT/getColorTab.cgi?" + opt;
		CGI_getSequence   = "/htbin/CGAT/getSequence.cgi?" + opt;
		CGI_getRegInfoUrl = "/htbin/CGAT/getRegInfoUrl.cgi?" + opt;
		CGI_getGene       = "/htbin/CGAT/getGene.cgi?" + opt;
		CGI_getGeneAttr   = "/htbin/CGAT/getGeneAttr.cgi?" + opt;
		CGI_getSegment    = "/htbin/CGAT/getSegment.cgi?" + opt;
    }

}
