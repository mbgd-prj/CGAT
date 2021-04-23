
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RGB {
    private String dataFilename;
    private Hashtable colorTable;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RGB() {
        colorTable = new Hashtable();
        setDataFilename("");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String name) {
        dataFilename = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        colorTable.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRGB(String color) {
        // Hash
        Integer rgb;

        rgb = (Integer)colorTable.get(color);
        if (rgb != null) {
            return(rgb.intValue());
        }

        // Key
        rgb = (Integer)colorTable.get(color.toLowerCase());
        if (rgb != null) {
            return(rgb.intValue());
        }

        //
        return(-1);
    }

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

                StringTokenizer token = new StringTokenizer(buf, "\t");

                int r = Integer.valueOf(token.nextToken()).intValue();
                int g = Integer.valueOf(token.nextToken()).intValue();
                int b = Integer.valueOf(token.nextToken()).intValue();
                String name = token.nextToken();
                Integer rgb = new Integer((r << 16) | (g << 8) | b);

                //
                colorTable.put(name, rgb);

                //
                colorTable.put(name, rgb);
            }
        }
        catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Data Format Error.\n" + "File : " + fname + "\n" + "Line : " + lineNo + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // File IO Error
            Dbg.println(0, "File read error : RGB info");
            return(false);
        }
    }
}
