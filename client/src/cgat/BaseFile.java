
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;

///////////////////////////////////////////////////////////////////////////////
// �ƥ��������Ϥ���ݥ��饹
//     ���Υ��饹��Ѿ����Ƥ��륯�饹
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
