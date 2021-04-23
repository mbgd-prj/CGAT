
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

///////////////////////////////////////////////////////////////////////////////
// URL �Ǽ����줿���ɥ쥹����Ρ��ƥ����ȥǡ��������ɤ߹���
//     ���� CLASS     BaseFile, DiskFile
public class UrlFile extends BaseFile {
    ///////////////////////////////////////////////////////////////////////////
    //
    public UrlFile(String u) throws Exception {
        URL url;
        InputStream is;

        //
        String cgiOpt = "";
        int idx = u.indexOf("?");
        if (idx < 0) {
            url = new URL(u);
            is = url.openStream();
        }
        else {
            url = new URL(u.substring(0, idx));
            cgiOpt = u.substring(idx + 1);

            //
            URLConnection urlc = url.openConnection();
            urlc.setDoOutput(true);   // set 'POST'

            PrintStream ps = new PrintStream(urlc.getOutputStream());
            ps.print(cgiOpt);
            ps.close();
            is = urlc.getInputStream();
        }

        if (u.endsWith(".gz")) {
            // ���̥ե������gzip��
            is = new GZIPInputStream(is);
        }
        else if (u.endsWith(".zip")) {
            // ���̥ե������gzip��
            is = new ZipInputStream(is);
        }
        br = new BufferedReader(new InputStreamReader(is));
    }

}
