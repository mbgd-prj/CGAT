
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.util.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ChromosomeInfo {
    private String dataFilename;

    private String  name;           //
    private boolean circular;       //
    private int     length;         //

    ///////////////////////////////////////////////////////////////////////////
    //
    public ChromosomeInfo() {
        setDataFilename("");

        setName("");
        setLength(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataFilename(String name) {
        dataFilename = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setName(String n) {
        name = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getName() {
        return name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setLength(int len) {
        length = len;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getLength() {
        return(length);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isCircular() {
        return(circular);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
        String fname = "";

        if (dataFilename.equals(filename)) {
            // 前回読み込んだデータと同じ ---> 再度読み込む必要がない
            return(true);
        }

        // データファイル名を保持
        setDataFilename(filename);

        if (filename.startsWith("http")) {
            UrlFile chrFile;
            try {
                fname = filename;
                chrFile = new UrlFile(fname);
            }
            catch (Exception e) {
                // エラーメッセージ表示
                String msg;
                msg = "File Not Found.\n" + "File : " + fname + "\n";
                BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                msgDialog.message(msg);

                return(false);
            }
            return(parse(fname, chrFile));
        }
        else {
            DiskFile chrFile;
            try {
                fname = filename;
                chrFile = new DiskFile(fname);
            }
            catch (Exception e) {
                // エラーメッセージ表示
                String msg;
                msg = "File Not Found.\n" + "File : " + fname + "\n";
                BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
                msgDialog.message(msg);

                return(false);
            }
            return(parse(fname, chrFile));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(File f) {
        return(parse(f.getAbsolutePath()));
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
    public boolean parse(String fname, BaseFile bf) {
        int lineNo = 0;

        try {
            for(;;) {
                // テキストを１行読み込みデータを取得する
                String buf = bf.readLine();
                lineNo++;
                if (buf == null) {
                    break;
                }
                StringTokenizer token = new StringTokenizer(buf, "\t");

                setName(token.nextToken());
                String  type = token.nextToken();
                circular = false;
                if (type.equalsIgnoreCase("circular")) {
                    circular = true;
                }
                int  len = Integer.valueOf(token.nextToken()).intValue();
                setLength(len);

                // １行読み込んで終了
                break;
            }
            return(true);
        }
        catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Data Format Error.\n" + "File : " + fname + "\n" + "Line : " + lineNo + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);

            // File IO Error
            Dbg.println(0, "File read error : chromosome info ");
            return(false);
        }
    }
}
