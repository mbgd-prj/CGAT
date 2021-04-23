 
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ColorTab {
    public static final String TYPE_STR   = "str";
    public static final String TYPE_INT   = "int";
    public static final String TYPE_FLOAT = "float";

    public static final int     USE_COLOR_LIGHT   = 0;
    public static final int     USE_COLOR_DARK    = 1;
    protected int use_color = USE_COLOR_LIGHT;

    protected String dataFilename;
    protected boolean entIdTypeString = false;

    protected RGB rgb;
    protected boolean isSetDefaultColor = false;
    protected ColorTabEnt defaultColorTabEnt;
    protected ColorTabEnt colorTab[];

    protected HashMap hashColorTab = new HashMap();     // entId で検索するため
    protected ColorAssign colorAssign;

    //
    protected int idxDefaultColor = 0;
    protected ArrayList defaultColor = new ArrayList();


    ///////////////////////////////////////////////////////////////////////////
    //
    public ColorTab(RGB c) {
        rgb = c;
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        colorTab = null;

        setDataFilename("");
        hashColorTab.clear();

        idxDefaultColor = 0;
        defaultColor.clear();
        defaultColor.add(Color.blue);
        defaultColor.add(Color.cyan);
        defaultColor.add(Color.darkGray);
        defaultColor.add(Color.gray);
        defaultColor.add(Color.green);
        defaultColor.add(Color.lightGray);
        defaultColor.add(Color.magenta);
        defaultColor.add(Color.orange);
        defaultColor.add(Color.pink);
        defaultColor.add(Color.red);
        defaultColor.add(Color.yellow);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColorAssign(ColorAssign c) {
        colorAssign = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String name) {
        dataFilename = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        try {
            return(colorTab.length);
        }
        catch (NullPointerException np) {
            return(0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ColorTabEnt getColorTab(int idx) {
        return(colorTab[idx]);
    }

    ///////////////////////////////////////////////////////////////////////////
    // colVal に対応した色を返す(離散値用)
    protected Color getColorInt(String colEntId) {
        Color c = Color.white;
        ColorTabEnt ent1;
        ColorTabEnt ent2;
        float val1;
        float val2;
        int i;

        float val = Float.valueOf(colEntId).floatValue();
        for(i = 0; i < size() - 1; i++) {
            ent1 = getColorTab(i);
            ent2 = getColorTab(i + 1);

            val1 = Float.valueOf(ent1.getEntId()).floatValue();
            val2 = Float.valueOf(ent2.getEntId()).floatValue();
            if ((val1 <= val) && (val < val2)) {
                //
                return(getColor(ent1));
            }
        }

        if (size() != 0) {
            // 最後の色エントリー（上限未設定のケース）
            ent1 = getColorTab(i);
            val1 = Float.valueOf(ent1.getEntId()).floatValue();
            if (val1 <= val) {
                return(getColor(ent1));
            }
        }

        return(c);
    }

    ///////////////////////////////////////////////////////////////////////////
    // colVal に対応した色を返す(連続値用)
    public Color getColorFloat(String colEntId) {
        float val = Float.valueOf(colEntId).floatValue();

        return getColorFloat(val);
    }

    ///////////////////////////////////////////////////////////////////////////
    // colVal に対応した色を返す(連続値用)
    public Color getColorFloat(float val) {
        for(int i = 0; i < size() - 1; i++) {
            ColorTabEnt ent1 = getColorTab(i);
            ColorTabEnt ent2 = getColorTab(i + 1);

            Float val1 = Float.valueOf(ent1.getEntId());
            Float val2 = Float.valueOf(ent2.getEntId());

            if ((val1.floatValue() <= val) && (val < val2.floatValue())) {
                //
                Color c1, c2;

                // 連続値：色を補間して求める
                c1 = getColor(ent1);
                try {
                    c2 = getColor(ent2);
                }
                catch (Exception e) {
                    return(c1);
                }
                Color newColor;
                float R, G, B;
                float cwk1, cwk2;
                float a, b;

                // Red
                cwk1 = (float)c1.getRed();
                cwk2 = (float)c2.getRed();
                a = (cwk2 - cwk1) / (val2.floatValue() - val1.floatValue());
                b = cwk2 - a * val2.floatValue();
                R = a * val + b;     // 色を算出

                // Green
                cwk1 = (float)c1.getGreen();
                cwk2 = (float)c2.getGreen();
                a = (cwk2 - cwk1) / (val2.floatValue() - val1.floatValue());
                b = cwk2 - a * val2.floatValue();
                G = a * val + b;     // 色を算出

                // Blue
                cwk1 = (float)c1.getBlue();
                cwk2 = (float)c2.getBlue();
                a = (cwk2 - cwk1) / (val2.floatValue() - val1.floatValue());
                b = cwk2 - a * val2.floatValue();
                B = a * val + b;     // 色を算出

                newColor = new Color((int)R, (int)G, (int)B);
                return(newColor);
            }
        }

        return(Color.white);
    }

    ///////////////////////////////////////////////////////////////////////////
    // colVal に対応した色を返す(文字用)
    protected Color getColorStr(String colEntId) {
        Color c = Color.white;

        try {
            ColorTabEnt colTabEnt = (ColorTabEnt)hashColorTab.get(colEntId);
            c = getColor(colTabEnt);
        }
        catch (Exception e1) {
            // ColorTab ファイルに該当する色の設定なし
            try {
                c = colorAssign.getColor(colEntId);
            }
            catch (Exception e2) {
                // 自動割り当てされていない
            }
        }

        return(c);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getColor(String colEntId, String type) {
        //
        if ((type == null) || (type.equalsIgnoreCase(TYPE_INT))) {
            return(getColorInt(colEntId));
        }
        else if (type.equalsIgnoreCase(TYPE_FLOAT)) {
            return(getColorFloat(colEntId));
        }
        else if (type.equalsIgnoreCase(TYPE_STR)) {
            return(getColorStr(colEntId));
        }
        else {
            return(Color.white);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Color getLightColor(double val, double max) {
        int r, g, b;

        double ratio = val / max;
        if (ratio < 0) {
            r = 255;
            g =   0;
            b =   0;
        }
        else if (ratio < 256 / (256 * 3)) {                   // 0 <= r < 1/3
            r = 255;
            g = (int)(255 * ratio * 3);
            b =   0;
        }
        else if (ratio < (256 * 2) / (256 * 3)) {             // 1/3 <= r < 2/3
            r = 255 - (int)(255 * (ratio - 1/3) * 3);
            g = 255;
            b =   0;
        }
        else if (ratio < 1) {                                 // 1/3 <= r < 2/3
            r =   0;
            g = 255;
            b = (int)(255 * (ratio - 2/3) * 3);
        }
        else {
            r =   0;
            g = 255;
            b = 255;
        }

        return new Color(r, g, b);
    }

/*
    ///////////////////////////////////////////////////////////////////////////
    // colVal に対応した色を返す(文字用)
    protected int getPatternStr(String colEntId) {
        int p = 0;

        try {
            ColorTabEnt colTabEnt = (ColorTabEnt)hashColorTab.get(colEntId);
            p = colTabEnt.getPattern();
        }
        catch (Exception e1) {
            // ColorTab ファイルに該当する色の設定なし
            try {
                p = colorAssign.getPattern(colEntId);
            }
            catch (Exception e2) {
                // 自動割り当てされていない
            }
        }

        return(p);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getPattern(String colEntId, String type) {
        //
        if ((type == null) || (type.equalsIgnoreCase(TYPE_INT))) {
            return(0);
        }
        else if (type.equalsIgnoreCase(TYPE_FLOAT)) {
            return(0);
        }
        else if (type.equalsIgnoreCase(TYPE_STR)) {
            return(getPatternStr(colEntId));
        }
        else {
            return(0);
        }
    }
*/

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
        String fname;

        if (dataFilename.equals(filename)) {
            // 前回読み込んだデータと同じ ---> 再度読み込む必要がない
            return(true);
        }

        // データを初期化
        clear();

        // データファイル名を保持
        setDataFilename(filename);
        if (filename.startsWith("http")) {
            UrlFile colorFile;
            try {
                fname = filename;
                colorFile = new UrlFile(fname);
            }
            catch (Exception e) {
                return(false);
            }
            return(parse(fname, colorFile));
        }
        else {
            DiskFile colorFile;
            try {
                fname = filename;
                colorFile = new DiskFile(fname);
            }
            catch (Exception e) {
                return(false);
            }
            return(parse(fname, colorFile));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(String fname) {
        try {
            DiskFile df = new DiskFile(fname);
            return(parse(fname, df));
        }
        catch (Exception e) {
            return(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(File f) {
        return(parse(f.getAbsolutePath()));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(String fname, BaseFile bf) {
        //
        ArrayList wkData = new ArrayList();
        int lenData;
        int lineNo = 0;
        ArrayList colNameList = new ArrayList();

        try {
            for(;;) {
                //
                String buf = bf.readLine();
                lineNo++;
                if (buf == null) {
                    // ファイルを読み終わった
                    lenData = wkData.size();
                    if (lenData == 0) {
                        return false;
                    }
                    colorTab = new ColorTabEnt[lenData];
                    for(int i = 0; i < lenData; i++) {
                        colorTab[i] = (ColorTabEnt)wkData.get(i);
                        hashColorTab.put(colorTab[i].getEntId(), colorTab[i]);
                    }

                    if (entIdTypeString != true) {
                        // Ent Id は、数値のみ  ---> 昇順にソート
                        CompColorEnt c = new CompColorEnt();
                        Arrays.sort(colorTab, c);
                    }
                    return(true);
                }

                //
                buf = buf.trim();
                if (buf.startsWith("#")) {
                    if (buf.startsWith("#id")) {
                        //
                        colNameList.clear();
                        String bufHeader = buf.substring(1).trim(); // # を除去
                        StringTokenizer stHeader = new StringTokenizer(bufHeader, "\t");
                        try {
                            while (stHeader.hasMoreTokens()) {
                                colNameList.add(stHeader.nextToken());
                            }
                        }
                        catch (Exception e) {
                        }
                    }
                    continue;
                }

                String tok[] = buf.split("[\t\n]");
                String colEntId = "0";
                String colDark  = "white";
                String colLight = null;
                String legend  = "";
                Iterator it = colNameList.iterator();
try {
                int colIdx = 0;
                while (it.hasNext()) {
                    String colName = (String)it.next();
                    if ("id".equalsIgnoreCase(colName)) {
                        colEntId = tok[colIdx];
                    }
                    else if ("color".equalsIgnoreCase(colName)) {
                        colDark = tok[colIdx];
                    }
                    else if ("color2".equalsIgnoreCase(colName)) {
                        colLight = tok[colIdx];
                    }
                    else if ("legend".equalsIgnoreCase(colName)) {
                        legend = tok[colIdx];
                    }
                    colIdx++;
                }
}
catch (Exception e) {
}
finally {
                if (colLight == null) {
                    colLight = colDark;
                }
}

                // Ent ID 種別判別
                try {
                    // colEntId を Float 型に変換してみる
                    Float.valueOf(colEntId);
                }
                catch (NumberFormatException nfe) {
                    // Float 型へ変換できない
                    entIdTypeString = true;
                }

                // Color
                Color cd = getColor(colDark);
                Color cl = getColor(colLight);

                wkData.add(new ColorTabEnt(colEntId,
                                           cd,
                                           cl,
                                           legend));
            }
        }
        catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Data Format Error.\n" + "File : " + fname + "\n" + "Line : " + lineNo + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // File IO Error
            Dbg.println(0, "File read error : ColorTab info");
            return(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // デフォルトの colorTab を設定する
    //     連続値用：青 ---> 赤 に変化する
    public void setDefaultColorTab(float min, float max) {
        isSetDefaultColor = true;

        max *= 1.001;       //
        colorTab = new ColorTabEnt[2];
        colorTab[0] = new ColorTabEnt(Float.toString(min), Color.white, "");
        colorTab[1] = new ColorTabEnt(Float.toString(max), Color.white,  "");

        updateDefaultColor();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updateDefaultColor() {
        if (! isSetDefaultColor) {
            return;
        }

        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        Properties prop = mbgdDataMng.getProperty();

        Color ldark = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_L_DARK), 16));
        Color hdark = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_H_DARK), 16));
        Color llight = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_L_LIGHT), 16));
        Color hlight = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_H_LIGHT), 16));

        colorTab[0].setColorDark(ldark);
        colorTab[0].setColorLight(llight);

        colorTab[1].setColorDark(hdark);
        colorTab[1].setColorLight(hlight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setUseColor(int uc) {
        use_color = uc;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getUseColor() {
        return use_color;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected Color getColor(ColorTabEnt cte) {
        Color c;

        if (use_color == USE_COLOR_DARK) {
            c = cte.getColorDark();
        }
        else {
            c = cte.getColorLight();
        }

        return c;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ColorEnt を基準にソートするための評価関数
    class CompColorEnt implements Comparator {
        public int compare(Object objA, Object objB) {
            ColorTabEnt a = (ColorTabEnt)objA;
            ColorTabEnt b = (ColorTabEnt)objB;

            float floatA = Float.valueOf(a.getEntId()).floatValue();
            float floatB = Float.valueOf(b.getEntId()).floatValue();
            if (floatA - floatB < 0.0) {
                return(-1);
            }
            else if (floatA == floatB) {
                return(0);
            }
            else {
                return(1);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected Color getColor(String cStr) {
        Color c;

        if (cStr.startsWith("#")) {
            // １文字目が '#' の場合
            String hex = cStr.substring(1);
            c = new Color(Integer.parseInt(hex, 16));
        }
        else {
            c = new Color(rgb.getRGB(cStr));
        }

        return c;
    }

}
