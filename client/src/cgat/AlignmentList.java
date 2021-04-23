
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

///////////////////////////////////////////////////////////////////////////////
// アライメントデータ群
//     アライメント情報をデータファイルより読み込み、昇順に ArrayList に格納する
public class AlignmentList {
    public static final String  ATTR_IDENTITY        = "Identity";
    public static final String  ATTR_SCORE           = "Score";
    public static final String  ATTR_BEST_HIT_STATUS = "Best Hit Status";
    public static final boolean TYPE_ATTR_STR = true;
    public static final boolean TYPE_ATTR_VAL = false;

    private BusyFlag busy;                          // データの load 中に BUSY となる

    private String dataFilename;                // データファイル名（２度読み防止のため）
    private boolean flagDataLoad = false;       // 実際にデータを読み込んだかどうか
    private String spec1Name;
    private String spec2Name;

    // Alignment データを格納
    private Alignment alignmentList[];
    private Alignment alignmentListTo[];

    // Alignment Sequence データファイルダウンロード用
    private String filenameAlignSeq;

    // Alignment データを from で sort したもの：alignment 描画時に利用する
    private Alignment alignmentList1[];       // base sp1-from
    private Alignment alignmentList2[];       // base sp2-from

    // Alignment データを to で sort したもの：alignment 描画時に利用する
    private Alignment alignmentListTo1[];       // base sp1-to
    private Alignment alignmentListTo2[];       // base sp2-to

    // Alignment データを "sp1:from1-to1,sp2:from2-to2" をキーに hash に格納したもの
    // Alignment データを "sp2:from2-to2,sp1:from1-to1" をキーに hash に格納したもの
    private HashMap alignmentHash;

    //
    private String attrName[];

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentList() {
        busy = new BusyFlag();

        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        alignmentList1 = null;
        alignmentList2 = null;
        alignmentHash  = new HashMap();

        alignmentList   = alignmentList1;       // デフォルトは sp1
        alignmentListTo = alignmentListTo1;     // デフォルトは sp1

        //
        attrName = new String[Alignment.MaxAttrNum];
        Arrays.fill(attrName, null);
        int i = 0;
        attrName[i++] = ATTR_IDENTITY;
        attrName[i++] = ATTR_SCORE;
        attrName[i++] = ATTR_BEST_HIT_STATUS;

        setDataFilename("");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        alignmentList1 = null;
        alignmentList2 = null;
        alignmentHash.clear();

        alignmentList   = alignmentList1;     // デフォルトは sp1
        alignmentListTo = alignmentListTo1;     // デフォルトは sp1

        setDataFilename("");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFlagDataLoad(boolean f) {
        flagDataLoad = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getFlagDataLoad() {
        return(flagDataLoad);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getSpecStaOLD() {
        if (alignmentList == alignmentList1) {
            return(true);
        }
        else {
            return(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String name) {
        dataFilename = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void setSpecName(String sp1, String sp2) {
        spec1Name = sp1;
        spec2Name = sp2;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private String getSpec1Name() {
        return spec1Name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private String getSpec2Name() {
        return spec2Name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        if (busy.getBusyFlagOwner() != null) {
            // データ load 中である
            return(0);
        }

        try {
            return(alignmentList1.length);
        }
        catch (NullPointerException np) {
            return(0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment getAlignment(String key) {
        return (Alignment)alignmentHash.get(key);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment getAlignment(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return(alignmentList[index]);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment getAlignment1(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return(alignmentList1[index]);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment getAlignment2(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return(alignmentList2[index]);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignAttrName(int index) {
        String name;
        if (attrName[index] != null) {
            name = attrName[index];
        }
        else {
            name = "";
        }

        return name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getAttrDataType(String key) {
        if (ATTR_BEST_HIT_STATUS.equals(key)) {
            return TYPE_ATTR_STR;
        }

        return TYPE_ATTR_VAL;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAttr(String key, int idx) {
        Alignment a = getAlignment(idx);
        if (a == null) {
            return "";
        }

        if (ATTR_SCORE.equals(key)) {
            return String.valueOf(a.getScore());
        }
        else if (ATTR_IDENTITY.equals(key)) {
            return String.valueOf(a.getIdent());
        }
        else if (ATTR_BEST_HIT_STATUS.equals(key)) {
            return a.getType();
        }

        return "";
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignSeq getAlignSeq(String sp1, int from1, int to1,
                                String sp2, int from2, int to2) {
        String hashKey;

        hashKey = sp1 + ":" + from1 + "-" + to1
                + ","
                + sp2 + ":" + from2 + "-" + to2;
        Alignment a = (Alignment)alignmentHash.get(hashKey);
        if (a == null) {
            hashKey = sp2 + ":" + from2 + "-" + to2
                    + ","
                    + sp1 + ":" + from1 + "-" + to1;
            a = (Alignment)alignmentHash.get(hashKey);
        }

        AlignSeq as = null;
        if (a != null) {
            as = a.getAlignSeq();
        }

        return as;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAlignSeq(String sp1, int from1, int to1,
                              String sp2, int from2, int to2,
                              int side) {
        AlignSeq as;
        String seq;

        as = getAlignSeq(sp1, from1, to1, sp2, from2, to2);
        if (as != null) {
            if (MbgdDataMng.SIDE0 == side) {
                seq = as.getAlignSeq(sp1);
            }
            else {
                seq = as.getAlignSeq(sp2);
            }
        }
        else {
            seq = "";
        }

        return seq;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 指定された Alignment に対応するアライメント結果をサーバよりダウンロードする
    // すでにダウンロードされていた場合は、ダウンロードしない
    public void loadAlignSeq(int index) {
        if ((index < 0) || (size() <= index)) {
            return;
        }

        //

        Alignment align = alignmentList[index];
        if (align.isAlignSeq()) {
            return;
        }

        // １組の alignSeq データをダウンロード
        loadAlignSeq(getSpec1Name(), align.getFrom1(), align.getTo1(),
                     getSpec2Name(), align.getFrom2(), align.getTo2());

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadAlignSeq(String sp1Name, int from1, int to1,
                               String sp2Name, int from2, int to2) {
        if (haveAlignSeq(sp1Name, from1, to1, sp2Name, from2, to2)) {
            // download 済み
            return;
        }

        MbgdData mbgdData = MbgdData.Instance();
        String server = mbgdData.getServerUrl();
        String file   = filenameAlignSeq;

        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        try {
            if (dataFilename.startsWith("http")) {
                // server ファイルからアライメント結果を読み込む
                loadAlignSeqServer(server, file,
                                    sp1Name, from1, to1,
                                    sp2Name, from2, to2);
                Dbg.println(1, "load align seq from server");
            }
            else {
                loadAlignSeqLocal(server, file,
                                    sp1Name, from1, to1,
                                    sp2Name, from2, to2);
                Dbg.println(1, "load align seq from local");
            }
        }
        catch (Exception e) {
            // Genome 配列を元に alignment を計算する
            makeAlignSeqLocal(sp1Name, from1, to1,
                              sp2Name, from2, to2);
            Dbg.println(1, "make align seq from local");
        }

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadAlignSeqServer(String server, String file,
                                        String sp1Name, int from1, int to1,
                                        String sp2Name, int from2, int to2) throws Exception {
        // alignment 結果をサーバより取得
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        String cgiPath;
        String alignSeq = "";
        AlignSeq as;
        cgiPath = mbgdDataMng.getBasePath()+"cgi-bin/getAlignSeq.cgi?"
                    + "file=" + file + "&"
                    + "reg=" + sp1Name + ":" + from1 + "-" + to1 + "&"
                    + "reg=" + sp2Name + ":" + from2 + "-" + to2;

        // サーバからデータを取得
        URL url = new URL(cgiPath);
        as = new AlignSeq();
        boolean sta = as.parse(url.openStream());
        if (! sta) {
            // 配列データが取得できなかった
            throw new Exception("Can not get AlignSeq");
        }

        // sp1Name を基準に並べ替え
        as.reorder(sp1Name);
        String s1 = as.getSeq1();
        String s2 = as.getSeq2();
        setAlignSeq(sp1Name, from1, to1, sp2Name, from2, to2, as);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadAlignSeqLocal(String server, String file, String sp1Name, int from1, int to1,
                                        String sp2Name, int from2, int to2) throws Exception {
        // alignment 結果を local ファイルより取得
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        AlignSeq as;
        String sep = System.getProperty("file.separator");
        String dirCgat = mbgdDataMng.getCgatHome();
        String dirDb  = dirCgat + sep + "database";
        String filename = dirDb + sep + "alignSeq" + sep
                        + file + "." + sp1Name + "-" + sp2Name;
        //
        FileInputStream fis;
        try {
            fis = new FileInputStream(filename);
        }
        catch (Exception eFis1) {
            filename = dirDb + sep + "alignSeq" + sep
                     + file + "." + sp2Name + "-" + sp1Name;
            try {
                fis = new FileInputStream(filename);
            }
            catch (Exception eFis2) {
                // 該当ファイル無し
                throw new Exception("Can not get AlignSeq");
            }
        }

        //
        InputStreamReader isr;
        BufferedReader br;
        Pattern patAlignSeqHead1 = Pattern.compile("^" + from1 + "\\s+" + to1 + "\\s+" + from2 + "\\s+" + to2 + "\\s+.*");
        Pattern patAlignSeqHead2 = Pattern.compile("^" + from2 + "\\s+" + to2 + "\\s+" + from1 + "\\s+" + to1 + "\\s+.*");
        Matcher m1, m2;

        try {
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            for(;;) {
                String buf = br.readLine();
                if (buf == null) {
                    // 該当配列無し
                    throw new Exception("Can not get AlignSeq");
                }

                m1 = patAlignSeqHead1.matcher(buf);
                if (m1.matches()) {
                    break;
                }

                m2 = patAlignSeqHead2.matcher(buf);
                if (m2.matches()) {
                    break;
                }
            }
        }
        catch (Exception eBr) {
            throw new Exception("Can not get AlignSeq");
        }

        // データを取得
        as = new AlignSeq();
        boolean sta = as.parse(br);
        if (! sta) {
            // 配列データが取得できなかった
            throw new Exception("Can not get AlignSeq");
        }

        // sp1Name を基準に並べ替え
        as.reorder(sp1Name);
        String s1 = as.getSeq1();
        String s2 = as.getSeq2();
        setAlignSeq(sp1Name, from1, to1, sp2Name, from2, to2, as);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void makeAlignSeqLocal( String sp1Name, int from1, int to1,
                                   String sp2Name, int from2, int to2) {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        ViewWindow viewWin = ViewWindow.Instance(mbgdDataMng);
        String sp1;
        int pos1;
        boolean dir1;
        String seq1;
        String sp2;
        int pos2;
        boolean dir2;
        String seq2;
        int wp2 = viewWin.getRegWidth() / 2;

        //
        sp1 = mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        pos1 = viewWin.getRegCenter(MbgdDataMng.BASE_ALIGN) - wp2;
        dir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        seq1 = mbgdDataMng.getGenomeSequence(MbgdDataMng.BASE_SPEC, from1 - 1, to1 - 1);
        if (! dir1) {
//            seq1 = cgat.seq.DNASequence.reverseComplement(seq1);
        }
        seq1 = seq1.toUpperCase();

        //
        sp2 = mbgdDataMng.getSpecName(MbgdDataMng.OPPO_SPEC);
        pos2 = viewWin.getRegCenter(MbgdDataMng.OPPO_ALIGN) - wp2;
        dir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        seq2 = mbgdDataMng.getGenomeSequence(MbgdDataMng.OPPO_SPEC, from2 - 1, to2 - 1);
        if (! dir2) {
//            seq2 = cgat.seq.DNASequence.reverseComplement(seq2);
        }
        seq2 = seq2.toUpperCase();

        // local でアライメントを計算
        int match    = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_MATCH);
        int mismatch = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_MISMATCH);
        int opengap  = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_OPENGAP);
        int extgap   = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_EXTGAP);
        int edgegap  = mbgdDataMng.getPropertyInt(MbgdDataMng.OPT_DP_EDGEGAP);
        DynamicProgramming dp = new DynamicProgramming(match, mismatch, opengap, extgap, edgegap);
        try {
            dp.alignment(seq1, seq2);
        }
        catch (InterruptedException ie) {
            Dbg.println(0, "Error :: dp.alignment() " + ie);
            Dbg.println(1, "seq1 :: " + seq1);
            Dbg.println(1, "seq2 :: " + seq2);
            return;
        }

        //
        AlignSeq as = new AlignSeq();
        as.setSp1(sp1);
        as.setPos1(pos1);
        as.setDir1(dir1);
        as.setSeq1(seq1);
        as.setSp2(sp2);
        as.setPos2(pos2);
        as.setDir2(dir2);
        as.setSeq2(seq2);
        as.updateMatches();

        // sp1Name を基準に並べ替え
        as.reorder(sp1Name);
        String s1 = as.getSeq1();
        String s2 = as.getSeq2();
        setAlignSeq(sp1Name, from1, to1, sp2Name, from2, to2, as);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String sp1, String sp2,
                        String fileAlign,
                        String fileAlignSeq) {
        boolean sta;

        if (dataFilename.equals(fileAlign)) {
            // 前回読み込んだデータと同じ ---> 再度読み込む必要がない
            setFlagDataLoad(false);
            return(true);
        }

        // データ load 開始
        busy.getBusyFlag();
        setFlagDataLoad(true);

        // データを初期化
        clear();

        // データファイル名を保持
        setDataFilename(fileAlign);
        setSpecName(sp1, sp2);

        Dbg.println(1, "Start");

        // alignment データを load
        sta = loadAlign(fileAlign, sp1, sp2);
        Dbg.println(1, "OK(alignment)");

        // ダウンロード時のために、ファイル名を待避しておく
        filenameAlignSeq = fileAlignSeq;

        // データ load 終了
        busy.freeBusyFlag();
        return(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean loadAlign(String filename, String sp1, String sp2) {
        String fname = "";
        boolean sta;

        if (filename.startsWith("http")) {
            UrlFile alignFile;
            try {
                // 圧縮ファイルのダウンロードを試みる
                fname = filename + ".gz";
                alignFile = new UrlFile(fname);
            }
            catch (Exception e1) {
                try {
                    // 非圧縮ファイルのダウンロード
                    fname = filename;
                    alignFile = new UrlFile(fname);
                }
                catch (Exception e2) {
                    // エラーメッセージ表示
                    String msg;
                    msg = "File Not Found.\n" + "File : " + fname + "\n";
                    BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                    msgDialog.message(msg);

                    return(false);
                }
            }

            sta = parse(fname, sp1, sp2, alignFile);

            return(sta);
        }
        else {
            DiskFile alignFile;
            try {
                // 圧縮ファイルのダウンロードを試みる
                fname = filename + ".gz";
                alignFile = new DiskFile(fname);
            }
            catch (Exception e1) {
                try {
                    // 非圧縮ファイルのダウンロード
                    fname = filename;
                    alignFile = new DiskFile(fname);
                }
                catch (Exception e2) {
                    // エラーメッセージ表示
                    String msg;
                    msg = "File Not Found.\n" + "File : " + fname + "\n";
                    BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                    msgDialog.message(msg);

                    // データ load 終了
                    return(false);
                }
            }

            sta = parse(fname, sp1, sp2, alignFile);

            return(sta);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Alignment データを行単位で読み込み、メモリに格納
    //   入力レコード件数が不明であるため、最初は ArrayList に読み込む
    //   件数が確定したあと、配列に移し替える
    public boolean parse(String fname, String sp1, String sp2, BaseFile bf) {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        ArrayList wkData;
        int lenWkData;
        int lineNo = 0;

        //
        alignmentHash.clear();
        String hashKey1, hashKey2;

        // データ一時格納領域を初期化
        wkData = new ArrayList();

        try {
            for(;;) {
                String buf = bf.readLine();     // １行読み込み
                lineNo++;                       // 読み込んだ行数をカウント（エラーメッセージで利用）
                if (buf == null) {
                    break;
                }
                if (lineNo % 2000 == 0) {
                    Dbg.println(1, "read :: " + lineNo);
                }

                if ("".equals(buf)) {
                    break;
                }

/*
                if (buf.startsWith("#")) {
                    // '#' で始まっている行
                    if (attrName[0] == null) {
                        // 先頭にある "#" を取り除く
                        int i;
                        for(i = 1; i < buf.length(); i++) {
                            if (buf.charAt(i) != '#') {
                                break;
                            }
                        }
                        buf = buf.substring(i);

                        // 前後の空白文字を取り除く
                        buf = buf.trim();

                        // "\t" で区切られた attrName を取り込む
                        StringTokenizer attrToken = new StringTokenizer(buf, "\t");

                        int idx = 0;
                        while(attrToken.hasMoreTokens()) {
                            try {
                                String name = attrToken.nextToken();
                                if ("from1".equals(name) ||
                                    "to1".equals(name) ||
                                    "from2".equals(name) ||
                                    "to2".equals(name) ||
                                    "dir".equals(name)) {
                                    continue;
                                }
                                attrName[idx++] = name;
                                if (Alignment.MaxAttrNum <= idx) {
                                    break;
                                }
                            }
                            catch (Exception e) {
                            }
                        }
                    }
                    continue;
                }
*/

                StringTokenizer token = new StringTokenizer(buf);

                int   from1  = Integer.valueOf(token.nextToken()).intValue();
                int   to1    = Integer.valueOf(token.nextToken()).intValue();
                int   from2  = Integer.valueOf(token.nextToken()).intValue();
                int   to2    = Integer.valueOf(token.nextToken()).intValue();
                String  strDir = token.nextToken();
                byte  dir;
                if (strDir.equalsIgnoreCase("+1") ||
                    strDir.equalsIgnoreCase("1") ||
                    strDir.equalsIgnoreCase("DIR") ||
                    strDir.equalsIgnoreCase("+") ||
                    strDir.equalsIgnoreCase("f")) {
                    dir = 1;
                }
                else if (strDir.equalsIgnoreCase("-1") ||
                         strDir.equalsIgnoreCase("INV") ||
                         strDir.equalsIgnoreCase("-") ||
                         strDir.equalsIgnoreCase("r")) {
                    dir = -1;
                }
                else {
                    dir = Byte.valueOf(strDir).byteValue();
                }
                float ident  = Float.valueOf(token.nextToken()).floatValue();
                int match = 0;
                int length = 0;
                float score = 0;
                score  = Float.valueOf(token.nextToken()).floatValue();
                String type   = token.nextToken();
                Alignment inf = new Alignment(from1, to1, from2, to2, dir, ident, match, length, score, type);

                wkData.add(inf);

                //
                hashKey1= sp1 + ":" + String.valueOf(from1) + "-" + String.valueOf(to1)
                        + ","
                        + sp2 + ":" + String.valueOf(from2) + "-" + String.valueOf(to2);
                hashKey2= sp2 + ":" + String.valueOf(from2) + "-" + String.valueOf(to2)
                        + ","
                        + sp1 + ":" + String.valueOf(from1) + "-" + String.valueOf(to1);

                // どちらの生物種から KEY を作成しても引けるようにする
                alignmentHash.put(hashKey1, inf);
            }
        }
        catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Data Format Error.\n" + "File : " + fname + "\n" + "Line : " + lineNo + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // File IO Error
            return(false);
        }

        // 読み込んだデータを配列にコピーし、from1/from2 で並べ替える
        lenWkData = wkData.size();

        // コピー
        alignmentList1   = new Alignment[lenWkData];
        alignmentList2   = new Alignment[lenWkData];
        alignmentListTo1 = new Alignment[lenWkData];
        alignmentListTo2 = new Alignment[lenWkData];
        System.arraycopy(wkData.toArray(), 0, alignmentList1,   0, lenWkData);
        System.arraycopy(wkData.toArray(), 0, alignmentList2,   0, lenWkData);
        System.arraycopy(wkData.toArray(), 0, alignmentListTo1, 0, lenWkData);
        System.arraycopy(wkData.toArray(), 0, alignmentListTo2, 0, lenWkData);

        //
        alignmentList = alignmentList1;

        //
        Comparator c;

        // from1 で並べ替え
        c = new CompAlignByFrom1();
        Arrays.sort(alignmentList1, c);

        // from2 で並べ替え
        c = new CompAlignByFrom2();
        Arrays.sort(alignmentList2, c);

        // from1 で並べ替え
        c = new CompAlignByTo1();
        Arrays.sort(alignmentListTo1, c);

        // from2 で並べ替え
        c = new CompAlignByTo2();
        Arrays.sort(alignmentListTo2, c);

        return(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // alignSeq データの読み込み
    //
    // alignSeq データファイルのフォーマット
    //
    //     SPEC1  pos   ATGCATGCATGCATGC ...
    //                  :::::::: ::::::: ...
    //     SPEC2  pos   ATGCATGC-TGCATGC ...
    //
    //     SPEC1  pos   ATGCATGCATGCATGC ...
    //                  :::::::: ::::::: ...
    //     SPEC2  pos   ATGCATGC-TGCATGC ...
    //
    //
    public String[] parseAlignSeq(String alignSeq) {
        String alignSeq1 = "";
        String alignSeq2 = "";
        StringTokenizer st = new StringTokenizer(alignSeq, "\r\n");
        String elm;

        StringTokenizer stElm;
        while(st.hasMoreTokens()) {
            elm = st.nextToken().trim();
            if (elm.equals("")) {
                // blank 行は、スキップする
                continue;
            }

            // SPEC1
            stElm = new StringTokenizer(elm);
            stElm.nextToken();      // 生物種名
            stElm.nextToken();      // 位置
            alignSeq1 += stElm.nextToken().trim();

            // match info
            elm = st.nextToken().trim();

            // SPEC2
            elm = st.nextToken().trim();
            stElm = new StringTokenizer(elm);
            stElm.nextToken();      // 生物種名
            stElm.nextToken();      // 位置
            alignSeq2 += stElm.nextToken().trim();

        }

        String res[] = new String[2];

        res[0] = alignSeq1;
        res[1] = alignSeq2;

        return res;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignSeq(String sp1, int from1, int to1,
                            String sp2, int from2, int to2,
                            AlignSeq as) {
        String hashKey;

        hashKey = sp1 + ":" + from1 + "-" + to1
                + ","
                + sp2 + ":" + from2 + "-" + to2;
        Alignment a = (Alignment)alignmentHash.get(hashKey);
        if (a == null) {
            hashKey = sp2 + ":" + from2 + "-" + to2
                    + ","
                    + sp1 + ":" + from1 + "-" + to1;
            a = (Alignment)alignmentHash.get(hashKey);
        }

        if (a != null) {
            a.setAlignSeq(as);
        }
        else {
            Dbg.println(1, "Can not found alignment data : key="+hashKey);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean haveAlignSeq(String sp1, int from1, int to1,
                            String sp2, int from2, int to2) {
        String hashKey;

        hashKey = sp1 + ":" + from1 + "-" + to1
                + ","
                + sp2 + ":" + from2 + "-" + to2;
        Alignment a = (Alignment)alignmentHash.get(hashKey);
        if (a == null) {
            hashKey = sp2 + ":" + from2 + "-" + to2
                    + ","
                    + sp1 + ":" + from1 + "-" + to1;
            a = (Alignment)alignmentHash.get(hashKey);
        }
        if (a != null) {
            return a.haveAlignSeq();
        }
        else {
            // 該当する Alignment が見つからない
            Dbg.println(1, "Can not found alignment data : key="+hashKey);

            // download しても無駄 ---> donwload 済みとして処理する
            return true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 全 alignment データを表示対象とする
    public void clearFilter() {
        int loopMax = size();
        for(int i = 0; i < loopMax; i++) {
            setFilter(i, true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFilter(int i, boolean f) {
        Alignment a = alignmentList[i];
        a.setFilter(f);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 描画領域に存在する Alignmnet を選択する
    //
    // [描画領域に存在する] とは
    //      描画領域：posST - posED
    //     Alignment の from が posED 以下
    //     Alignment の to   が posST 以上
    //
    //                   posST      posED
    //                     |----------|
    // genome   +------------------------------------------------------+
    //
    //  align        |---|
    //         ◎       |---------|
    //         ◎   |----------------------------------|
    //         ◎               |----|
    //         ◎              |------------------------------|
    //                                    |--------|
    //
    // 単純に from や to でソートしただけでは、表示対象であるかどうかは不明である
    // そこで、以下のように Alignment の選択を行う
    // (1) Alignment の from が posED 以下であるデータを選択する
    // (2) 処理(1) の結果を to で sort する
    // (3) 処理(2) の結果の中で to が posST 以上であるデータを選択する
    //  なお、posST と posED の位置によっては、from --> to の順ではなく
    //  to --> from の順で絞り込む
    //
    //
    //
    // 以下のデータを利用し選択するため AlignmentListFilter を使わない
    // alignmentList1
    // alignmentList2
    // alignmentListTo1
    // alignmentListTo2
    public Alignment[] selectAlignList( boolean baseSpec,
                                        int regStart, int regWidth, int regMax) {
        Alignment alignList[];
        Alignment selAlign[];

        if (baseSpec) {
            selAlign = selectAlignListBaseSpec(regStart, regWidth, regMax);
        }
        else {
            // base spec の入れ替えが行われていた場合
            selAlign = selectAlignListOppoSpec(regStart, regWidth, regMax);
        }

        // selAlign を from1 で昇順 SORT
        Comparator c = new CompAlignByTypeFrom1();
        Arrays.sort(selAlign, c);

        // データ保護のため、複製を返す
        int loopMax = selAlign.length;
        alignList = new Alignment[loopMax];
        for(int i = 0; i < loopMax; i++) {
            alignList[i] = new Alignment(selAlign[i]);
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private Alignment[] selectAlignListBaseSpec(int regStart, int regWidth, int regMax) {
        Alignment alignList[], alignListWk[];
        Alignment align;
        int posST = regStart;
        int posED = regStart + regWidth;
        int idxSt, idxEd, idx;
        int loopMax;
        int i;

        //
        alignList   = new Alignment[0];
        alignListWk = new Alignment[0];

        if (regMax > 0 && regMax < regStart + regWidth) {
            // 0 bp を挟むデータ選択 ---> [後ろの部分]＋[前の部分] の二段階処理
            // Alignment データは、0 bp を挟むことはないため
            posED %= regMax;

            // to を昇順に sort したものを後ろから調べる
            loopMax = alignmentListTo1.length;
            for(i = loopMax - 1; 0 <= i; i--) {
                if (alignmentListTo1[i].getTo1() < posST) {
                    // これより後のデータをコピーする
                    alignListWk = new Alignment[loopMax - i - 1];
                    System.arraycopy(alignmentListTo1, i + 1, alignListWk, 0, loopMax - i - 1);
                    break;
                }
            }

            // from を昇順に sort したものを前から調べる
            for(i = 0; i < loopMax; i++) {
                if (posED < alignmentList1[i].getFrom1()) {
                    // これより前のデータをコピーする
                    alignList = new Alignment[alignListWk.length + i];
                    System.arraycopy(alignListWk, 0, alignList, 0, alignListWk.length);
                    System.arraycopy(alignmentList1, 0, alignList, alignListWk.length, i);
                    break;
                }
            }
        }
        else if (regStart + regWidth / 2 < regMax / 2) {
            // from --> to の順で絞り込む
            loopMax = alignmentList1.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignmentList1[i].getFrom1()) {
                    // これより前のデータをコピーする
                    alignListWk = new Alignment[i];
                    System.arraycopy(alignmentList1, 0, alignListWk, 0, i);
                    break;
                }
            }

            // to で sort
            Arrays.sort(alignListWk, new CompAlignByTo1());
            //
            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignListWk[i].getTo1()) {
                    // これ以降のデータをコピーする
                    alignList = new Alignment[loopMax - i];
                    System.arraycopy(alignListWk, i, alignList, 0, loopMax - i);
                    break;
                }
            }
        }
        else {
            // to --> from の順で絞り込む
            loopMax = alignmentListTo1.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignmentListTo1[i].getTo1()) {
                    // これ以降のデータをコピーする
                    alignListWk = new Alignment[loopMax - i];
                    System.arraycopy(alignmentListTo1, i, alignListWk, 0, loopMax - i);
                    break;
                }
            }

            // from で sort
            Arrays.sort(alignListWk, new CompAlignByFrom1());

            //
            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk[i].getFrom1()) {
                    // これより前のデータをコピーする
                    break;
                }
            }
            alignList = new Alignment[i];
            System.arraycopy(alignListWk, 0, alignList, 0, i);
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private Alignment[] selectAlignListOppoSpec(int regStart, int regWidth, int regMax) {
        Alignment alignList[], alignListWk[];
        Alignment align;
        int posST = regStart;
        int posED = regStart + regWidth;
        int idxSt, idxEd, idx;
        int loopMax;
        int i;

        //
        alignList    = new Alignment[0];
        alignListWk  = new Alignment[0];

        if (regMax < regStart + regWidth) {
            // 0 bp を挟むデータ選択 ---> [後ろの部分]＋[前の部分] の二段階処理
            // Alignment データは、0 bp を挟むことはないため
            posED %= regMax;

            // to を昇順に sort したものを後ろから調べる
            loopMax = alignmentListTo2.length;
            for(i = loopMax - 1; 0 <= i; i--) {
                if (alignmentListTo2[i].getTo2() < posST) {
                    // これより後のデータをコピーする
                    alignListWk = new Alignment[loopMax - i - 1];
                    System.arraycopy(alignmentListTo2, i + 1, alignListWk, 0, loopMax - i - 1);
                    break;
                }
            }

            // from を昇順に sort したものを前から調べる
            for(i = 0; i < loopMax; i++) {
                if (posED < alignmentList2[i].getFrom2()) {
                    // これより前のデータをコピーする
                    alignList = new Alignment[alignListWk.length + i + 1];
                    System.arraycopy(alignListWk, 0, alignList, 0, alignListWk.length);
                    System.arraycopy(alignmentList2, 0, alignList, alignListWk.length, i + 1);
                    break;
                }
            }
        }
        else if (regStart + regWidth / 2 < regMax / 2) {
            // from --> to の順で絞り込む
            loopMax = alignmentList2.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignmentList2[i].getFrom2()) {
                    // これより前のデータをコピーする
                    alignListWk = new Alignment[i];
                    System.arraycopy(alignmentList2, 0, alignListWk, 0, i);
                    break;
                }
            }

            // to で sort
            Arrays.sort(alignListWk, new CompAlignByTo2());
            //
            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignListWk[i].getTo2()) {
                    // これ以降のデータをコピーする
                    alignList = new Alignment[loopMax - i];
                    System.arraycopy(alignListWk, i, alignList, 0, loopMax - i);
                    break;
                }
            }
        }
        else {
            // to --> from の順で絞り込む
            loopMax = alignmentListTo2.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignmentListTo2[i].getTo2()) {
                    // これ以降のデータをコピーする
                    alignListWk = new Alignment[loopMax - i];
                    System.arraycopy(alignmentListTo2, i, alignListWk, 0, loopMax - i);
                    break;
                }
            }

            // from で sort
            Arrays.sort(alignListWk, new CompAlignByFrom2());
            //
            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk[i].getFrom2()) {
                    // これより前のデータをコピーする
                    break;
                }
            }
            alignList = new Alignment[i];
            System.arraycopy(alignListWk, 0, alignList, 0, i);
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Alignment[] selectAlignList( boolean baseSpec,
                                        int regStart1, int regWidth1, int regMax1,
                                        int regStart,  int regWidth,  int regMax) {
        Alignment alignList[];
        Alignment selAlign[];

        if (baseSpec) {
            selAlign = selectAlignListBaseSpec( regStart1, regWidth1, regMax1,
                                            regStart, regWidth,  regMax);
        }
        else {
            // load したデータと基準生物種が入れ替わっていた場合
            selAlign = selectAlignListOppoSpec( regStart1, regWidth1, regMax1,
                                            regStart, regWidth,  regMax);
        }

        // データ保護のため、複製を返す
        int loopMax = selAlign.length;
        alignList = new Alignment[loopMax];
        for(int i = 0; i < loopMax; i++) {
            alignList[i] = new Alignment(selAlign[i]);
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private Alignment[] selectAlignListBaseSpec(int regStart1, int regWidth1, int regMax1,
                                                int regStart,  int regWidth,  int regMax) {
        Alignment alignList[], alignListWk0[], alignListWk[];
        Alignment align;
        int posST = regStart;
        int posED = regStart + regWidth;
        int idxSt, idxEd, idx;
        int loopMax;
        int i;

        // regStart1, regWidth1 での絞り込み
        alignList   = new Alignment[0];
        alignListWk = new Alignment[0];
        alignListWk0 = selectAlignListBaseSpec(regStart1, regWidth1, regMax1);
        Arrays.sort(alignListWk0, new CompAlignByFrom2());

        if (regMax > 0 && regMax < regStart + regWidth) {
            // 0 bp を挟むデータ選択
            posED %= regMax;

            // 前方の有効データを取得
            loopMax = alignListWk0.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk0[i].getFrom2()) {
                    break;
                }
            }
            if (1 <= i) {
                alignListWk = new Alignment[i];
                System.arraycopy(alignListWk0, 0, alignListWk, 0, i);
            }

            // 後方の有効データを取得
            Arrays.sort(alignListWk0, new CompAlignByTo2());
            for(i = loopMax - 1; 0 <= i; i--) {
                if (alignListWk0[i].getTo2() < posST) {
                    alignList = new Alignment[alignListWk.length + loopMax - i];
                    System.arraycopy(alignListWk, 0, alignList, 0, alignListWk.length);
                    System.arraycopy(alignListWk0, i, alignList, alignListWk.length, loopMax - i);
                    break;
                }
                else if (i == 0) {
                    alignList = new Alignment[loopMax];
                    System.arraycopy(alignListWk0, 0, alignList, 0, loopMax);
                }
            }
        }
        else if (regStart + regWidth / 2 < regMax / 2) {
            // 表示対象の Alignment データが前の方にある
            loopMax = alignListWk0.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk0[i].getFrom2()) {
                    break;
                }
            }

            alignListWk = new Alignment[i];
            System.arraycopy(alignListWk0, 0, alignListWk, 0, i);
            Arrays.sort(alignListWk, new CompAlignByTo2());

            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignListWk[i].getTo2()) {
                    alignList = new Alignment[loopMax - i];
                    System.arraycopy(alignListWk, i, alignList, 0, loopMax - i);
                    break;
                }
            }
        }
        else {
            // 表示対象の Alignment データが後ろの方にある
            loopMax = alignListWk0.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk0[i].getFrom2()) {
                    break;
                }
            }

            alignListWk = new Alignment[i];
            System.arraycopy(alignListWk0, 0, alignListWk, 0, i);
            Arrays.sort(alignListWk, new CompAlignByTo2());

            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignListWk[i].getTo2()) {
                    alignList = new Alignment[loopMax - i];
                    System.arraycopy(alignListWk, i, alignList, 0, loopMax - i);
                    break;
                }
            }
        }

        Comparator c = new CompAlignByTypeFrom1();
        Arrays.sort(alignList, c);

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private Alignment[] selectAlignListOppoSpec(int regStart1, int regWidth1, int regMax1,
                                                int regStart,  int regWidth,  int regMax) {
        Alignment alignList[], alignListWk0[], alignListWk[];
        Alignment align;
        int posST = regStart;
        int posED = regStart + regWidth;
        int idxSt, idxEd, idx;
        int loopMax;
        int i;

        //
        alignList = new Alignment[0];
        alignListWk0 = selectAlignListOppoSpec(regStart1, regWidth1, regMax1);
        Arrays.sort(alignListWk0, new CompAlignByFrom1());

        if (regMax > 0 && regMax < regStart + regWidth) {
            // 0 bp を挟むデータ選択
            posED %= regMax;

            // 前方の有効データを取得
            loopMax = alignListWk0.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk0[i].getFrom1()) {
                    alignListWk = new Alignment[i];
                    System.arraycopy(alignListWk0, 0, alignListWk, 0, i);
                    break;
                }
            }

            // 後方の有効データを取得
            Arrays.sort(alignListWk0, new CompAlignByTo1());
            for(i = loopMax - 1; 0 <= i; i--) {
                if (alignListWk0[i].getTo1() < posST) {
                    alignList = new Alignment[alignListWk0.length + loopMax - i];
                    System.arraycopy(alignListWk0, 0, alignList, 0, alignListWk0.length);
                    System.arraycopy(alignListWk0, i, alignList, alignListWk0.length, loopMax - i);
                    break;
                }
            }
        }
        else if (regStart + regWidth / 2 < regMax / 2) {
            // 表示対象の Alignment データが前の方にある
            loopMax = alignListWk0.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk0[i].getFrom1()) {
                    break;
                }
            }

            alignListWk = new Alignment[i];
            System.arraycopy(alignListWk0, 0, alignListWk, 0, i);
            Arrays.sort(alignListWk, new CompAlignByTo1());

            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignListWk[i].getTo1()) {
                    alignList = new Alignment[loopMax - i];
                    System.arraycopy(alignListWk, i, alignList, 0, loopMax - i);
                    break;
                }
            }
        }
        else {
            // 表示対象の Alignment データが後ろの方にある
            loopMax = alignListWk0.length;
            for(i = 0; i < loopMax; i++) {
                if (posED < alignListWk0[i].getFrom1()) {
                    break;
                }
            }

            alignListWk = new Alignment[i];
            System.arraycopy(alignListWk0, 0, alignListWk, 0, i);
            Arrays.sort(alignListWk, new CompAlignByTo1());

            loopMax = alignListWk.length;
            for(i = 0; i < loopMax; i++) {
                if (posST <= alignListWk[i].getTo1()) {
                    alignList = new Alignment[loopMax - i];
                    System.arraycopy(alignListWk, i, alignList, 0, loopMax - i);
                    break;
                }
            }
        }

        return alignList;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 任意の位置(pos)に最も近い Alignment を探す
    private Alignment searchAlignmentBaseSpec(int pos) {
        Alignment align = null;
        int idxSt = 0;
        int idxEd = alignmentList1.length;
        int idx;

        for(;;) {
            if (idxEd - idxSt == 0) {
                idx = idxSt;
                break;
            }
            else if (idxEd - idxSt == 1) {
                align = alignmentList1[idxSt];
                break;
            }
            idx = (idxSt + idxEd) / 2;
            align = alignmentList1[idx];
            if (pos <= align.getFrom1()) {
                idx--;
                idxEd = idx;
            }
            else if (pos == align.getFrom1()) {
                idxEd = idx;
            }
            else {
                idx++;
                idxSt = idx;
            }
        }

        return align;
    }

    ///////////////////////////////////////////////////////////////////////////
    // from1 を基準にソートするための評価関数
    //   from1 が同じ場合は、from2 でsort
    class CompAlignByFrom1 implements Comparator {
        public int compare(Object objA, Object objB) {
            Alignment a = (Alignment)objA;
            Alignment b = (Alignment)objB;

            int aFrom1 = a.getFrom1();
            int bFrom1 = b.getFrom1();
            if (aFrom1 == bFrom1) {
                return(a.getFrom2() - b.getFrom2());
            }
            return(aFrom1 - bFrom1);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // type, from1 を基準にソートするための評価関数
    //   type, from1 が同じ場合は、from2 でsort
    class CompAlignByTypeFrom1 implements Comparator {
        public int compare(Object objA, Object objB) {
            Alignment a = (Alignment)objA;
            Alignment b = (Alignment)objB;

            String aType = a.getType();
            String bType = b.getType();
            int sta = aType.compareToIgnoreCase(bType);
            if (sta != 0) {
                // type が異なる
                return sta;
            }
            int aFrom1 = a.getFrom1();
            int bFrom1 = b.getFrom1();
            if (aFrom1 == bFrom1) {
                return(a.getFrom2() - b.getFrom2());
            }
            return(aFrom1 - bFrom1);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // from2 を基準にソートするための評価関数
    //   from2 が同じ場合は、from1 でsort
    class CompAlignByFrom2 implements Comparator {
        public int compare(Object objA, Object objB) {
            Alignment a = (Alignment)objA;
            Alignment b = (Alignment)objB;

            int aFrom2 = a.getFrom2();
            int bFrom2 = b.getFrom2();
            if (aFrom2 == bFrom2) {
                return(a.getFrom1() - b.getFrom1());
            }
            return(aFrom2 - bFrom2);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // to1 を基準にソートするための評価関数
    //   to1 が同じ場合は、to2 でsort
    class CompAlignByTo1 implements Comparator {
        public int compare(Object objA, Object objB) {
            Alignment a = (Alignment)objA;
            Alignment b = (Alignment)objB;

            int aTo1 = a.getTo1();
            int bTo1 = b.getTo1();
            if (aTo1 == bTo1) {
                return(a.getTo2() - b.getTo2());
            }
            return(aTo1 - bTo1);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // to2 を基準にソートするための評価関数
    //   to2 が同じ場合は、from1 でsort
    class CompAlignByTo2 implements Comparator {
        public int compare(Object objA, Object objB) {
            Alignment a = (Alignment)objA;
            Alignment b = (Alignment)objB;

            int aTo2 = a.getTo2();
            int bTo2 = b.getTo2();
            if (aTo2 == bTo2) {
                return(a.getTo1() - b.getTo1());
            }
            return(aTo2 - bTo2);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class CompAlignForDotPlot implements Comparator {
        public int compare(Object objA, Object objB) {
            Alignment a = (Alignment)objA;
            Alignment b = (Alignment)objB;
            return(a.getFrom2() - b.getFrom2());
        }
    }

}
