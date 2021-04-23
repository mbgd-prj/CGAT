/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.util.zip.*;

///////////////////////////////////////////////////////////////////////////////
// ローカルディスクにあるテキストファイルの１行読み込み
//     参考 CLASS     BaseFile, UrlFile
public class DiskFile extends BaseFile {
    FileInputStream fis;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DiskFile(String f) throws Exception {
//        try {
            FileInputStream fis = new FileInputStream(f);
            InputStream is;
            if (f.endsWith(".gz")) {
                // 圧縮ファイル（gzip）
                is = new GZIPInputStream(fis);
            }
            else if (f.endsWith(".zip")) {
                // 圧縮ファイル（gzip）
                is = new ZipInputStream(fis);
            }
            else {
                is = fis;
            }
            br = new BufferedReader(new InputStreamReader(is));
//        }
//        catch (Exception e) {
//        }
    }
}
