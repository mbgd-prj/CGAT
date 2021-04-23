/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.util.zip.*;

///////////////////////////////////////////////////////////////////////////////
// ������ǥ������ˤ���ƥ����ȥե�����Σ����ɤ߹���
//     ���� CLASS     BaseFile, UrlFile
public class DiskFile extends BaseFile {
    FileInputStream fis;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DiskFile(String f) throws Exception {
//        try {
            FileInputStream fis = new FileInputStream(f);
            InputStream is;
            if (f.endsWith(".gz")) {
                // ���̥ե������gzip��
                is = new GZIPInputStream(fis);
            }
            else if (f.endsWith(".zip")) {
                // ���̥ե������gzip��
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
