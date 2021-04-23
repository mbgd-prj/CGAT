package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class InfoSegments {
    ArrayList segNameList;
    ArrayList segDirList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public InfoSegments() {
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        segNameList = new ArrayList();
        segDirList  = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void insert(String name, String dir) {
        segNameList.add(name);
        segDirList.add(dir);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        return(segNameList.size());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getName(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return((String)segNameList.get(index));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getDir(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return((String)segDirList.get(index));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
        clear();
        if (filename.startsWith("http")) {
            UrlFile infoSegsFile;
            try {
                infoSegsFile = new UrlFile(filename);
            }
            catch (Exception e) {
                return(false);
            }
            return(parse(infoSegsFile));
        }
        else {
            DiskFile infoSegsFile;
            try {
                infoSegsFile = new DiskFile(filename);
            }
            catch (Exception e) {
                return(false);
            }
            return(parse(infoSegsFile));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean parse(BaseFile bf) {
        try {
            for(;;) {
                String buf = bf.readLine();     // １行読み込み
                if (buf == null) {
                    break;
                }
                if ("".equals(buf)) {
                    break;
                }
                StringTokenizer token = new StringTokenizer(buf, "\t");

                String name = token.nextToken();
                String dir  = token.nextToken();

                insert(name, dir);
            }
        }
        catch (Exception e) {
            // File IO Error
            Dbg.println(0, "File read error : InfoSegs");
            return(false);
        }

        return(true);
    }

}
