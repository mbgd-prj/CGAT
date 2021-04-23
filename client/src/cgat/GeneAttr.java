package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// ORF 名と属性値の組み合わせを保存する
//
//
public class GeneAttr {
    String dataFilename;

    HashMap attrValues;
    String colorType;
    Float minValue;
    Float maxValue;

    ///////////////////////////////////////////////////////////////////////////
    //
    public GeneAttr() {
        clear();
        setColorType("");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        return attrValues.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getMinValue() {
        return(minValue.floatValue());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getMaxValue() {
        return(maxValue.floatValue());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColorType(String c) {
        colorType = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getColorType() {
        return colorType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        attrValues = new HashMap();
        minValue = null;
        maxValue = null;

        setDataFilename("");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String name) {
        dataFilename = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void add(String name, String val) {
        attrValues.put(name, val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String get(String name) {
        try {
            String key = name.toUpperCase();
            String ret = (String)attrValues.get(key);
            return ret;
        }
        catch (Exception e) {
            // 該当する ORF データがない
            return(null);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
        String fname = "";

        if (dataFilename.equals(filename)) {
            // 前回読み込んだデータと同じ ---> 再度読み込む必要がない
            return(true);
        }

        // データを初期化
        clear();

        // データファイル名を保持
        setDataFilename(filename);

        if (filename.startsWith("http")) {
            UrlFile attrFile;
            try {
                fname = filename + ".gz";
                attrFile = new UrlFile(fname);
            }
            catch (Exception e) {
                try {
                    fname = filename;
                    attrFile = new UrlFile(fname);
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
            return(parse(fname, attrFile));
        }
        else {
            DiskFile attrFile;
            try {
                fname = filename;
                attrFile = new DiskFile(fname);
            }
            catch (Exception e) {
                // エラーメッセージ表示
                String msg;
                msg = "File Not Found.\n" + "File : " + fname + "\n";
                BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                msgDialog.message(msg);

                return(false);
            }
            return(parse(fname, attrFile));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(String fname, BaseFile bf) {
        int lineNo = 0;

        try {
            for(;;) {
                //
                String buf = bf.readLine();
                lineNo++;
                if (buf == null) {
                    return(true);
                }
                StringTokenizer token = new StringTokenizer(buf);

                String name = token.nextToken().toUpperCase();
                String val  = token.nextToken();
                Float fVal = new Float(val);
                add(name, val);

                // 最大値/最小値
                if (minValue == null) {
                    minValue = fVal;
                    maxValue = fVal;
                }
                else {
                    if (fVal.floatValue() < minValue.floatValue()) {
                        // より小さい値
                        minValue = fVal;
                    }
                    if (maxValue.floatValue() < fVal.floatValue()) {
                        // より大きい値
                        maxValue = fVal;
                    }
                }
            }
        }
        catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Data Format Error.\n" + "File : " + fname + "\n" + "Line : " + lineNo + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // File IO Error
            Dbg.println(0, "File read error : Gene attr file");
            return(false);
        }
    }

}
