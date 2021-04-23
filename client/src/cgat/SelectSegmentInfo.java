package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.io.*;
import java.net.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// ưŪ seg �ǡ�������������
public class SelectSegmentInfo {
    private ArrayList nameList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public SelectSegmentInfo() {
        super();

        _init();

        getListItem();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        //
        nameList = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        return nameList.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getName(int i) {
        String name;

        try {
            name = (String)nameList.get(i);
        }
        catch (Exception e) {
            return null;
        }

        return name;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void getListItemServer() {
        // �����Ф�ꡢɽ��̾�Ρʥǥ��쥯�ȥ�̾�ˤ����
        String u = MbgdDataMng.Instance().getBasePath();
        u += "cgi-bin/segment.cgi";
        UrlFile url = null;

        nameList.add("No Data");
        try {
            url = new UrlFile(u);
            for(;;) {
                String buf = url.readLine();
                if (buf == null) {
                    break;
                }
                if (buf.trim().equals("")) {
                    continue;
                }
                if (buf.startsWith("#")) {
                    continue;
                }

                StringTokenizer st = new StringTokenizer(buf);

                String dspName = st.nextToken();

                // ɽ��̾�Τ���¸
                if (! nameList.contains(dspName)) {
                    nameList.add(dspName + "(Server)");
                }
            }
        }
        catch (Exception e) {
            Dbg.println(1, "Exception :: getListItemServer() :: " + u);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void getListItemLocal() {
        String sep  = System.getProperty("file.separator");
        String dirCgat = MbgdDataMng.Instance().getCgatHome();
        String dirDb = dirCgat + sep + "database";
        String dirSeg = dirDb + sep + "segment";

        File segDirFile = new File(dirSeg);
        if (! segDirFile.isDirectory()) {
            // segment �ǥ��쥯�ȥ꤬¸�ߤ��ʤ�
            return;
        }

        // segment �ǡ�������
        String segFileList[];
        segFileList = segDirFile.list();
        Arrays.sort(segFileList);
        for(int i = 0; i < segFileList.length; i++) {
            String file = segFileList[i];
            if (file.startsWith(".")) {
                continue;
            }

            File fileSeg = new File(dirSeg + sep + file);
            if (! fileSeg.isDirectory()) {
                // segment �ǡ����ǥ��쥯�ȥ�ǤϤʤ�
                continue;
            }

            if (! nameList.contains(file)) {
                nameList.add(file);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void getListItem() {
        nameList.clear();

        getListItemServer();
        try {
            getListItemLocal();
        }
        catch (Exception e) {
        }
    }

}
