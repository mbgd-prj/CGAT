package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.io.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class InfoGeneAttr {
    private ArrayList attrName;
    private ArrayList attrKey;
    private ArrayList attrType;

    ///////////////////////////////////////////////////////////////////////////
    //
    public InfoGeneAttr() {
        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        attrName = new ArrayList();
        attrKey  = new ArrayList();
        attrType = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        attrName.clear();
        attrKey.clear();
        attrType.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void insert(String name, String key, String type) {
        if (! attrName.contains(name)) {
            attrName.add(name);
            attrKey.add(key);
            attrType.add(type);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        return(attrName.size());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getName(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return((String)attrName.get(index));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getKey(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return((String)attrKey.get(index));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getType(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return((String)attrType.get(index));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean load(String filename) {
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
            return loadLocal(filename);
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
                String key  = token.nextToken();
                String type;
                try {
                    type = token.nextToken();
                }
                catch (Exception ee) {
                    type = "";
                }
                insert(name+"(Server)", key, type);
            }
        }
        catch (Exception e) {
            // File IO Error
            Dbg.println(0, "File read error : InfoSegs");
            return(false);
        }

        return(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean loadLocal(String filename) {
        String sep  = System.getProperty("file.separator");
        String dirCgat = MbgdDataMng.Instance().getCgatHome();
        String dirDb = dirCgat + sep + "database";
        String dirAttr = dirDb + sep + "geneattr";
        File attrDirFile = new File(dirAttr);
        if (! attrDirFile.isDirectory()) {
            // gene attr ディレクトリが存在しない
            return false;
        }

        // gene attr データ検索
        String attrFileList[];
        attrFileList = attrDirFile.list();
        Arrays.sort(attrFileList);
        for(int i = 0; i < attrFileList.length; i++) {
            String file = attrFileList[i];
            if (file.startsWith(".")) {
                continue;
            }

            File fileAttr = new File(dirAttr + sep + file);
            if (! fileAttr.isDirectory()) {
                // gene attr データディレクトリではない
                continue;
            }

            insert(file, file, "");
        }

        return true;
    }

}
