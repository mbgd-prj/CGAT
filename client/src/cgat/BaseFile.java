
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;

///////////////////////////////////////////////////////////////////////////////
// テキスト入力の抽象クラス
//     このクラスを継承しているクラス
//         DiskFile
//         UrlFile
public class BaseFile {
    BufferedReader br;

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseFile() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseFile(BufferedReader r) {
        br = r;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String readLine() {
        try {
            return(br.readLine());
        }
        catch (Exception e) {
            return("");
        }
    }
}
