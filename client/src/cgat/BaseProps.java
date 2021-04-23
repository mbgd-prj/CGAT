
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.io.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// プロパティーを管理する
// プロパティーは、以下のようなキーで格納されることとする
//      a01.b01.c01[index]
//      具体例（AlignmentViewerの場合)
//          "data.local[0]"
//          "data.local[1]"
//          "data.server[0]"
//          "data.server[1]"
//          "data.server[2]"
//          "browser.executable[0]"
//          "browser.options[0]"
//          "cgi.geneInfo.url[0]"
//          "cgi.geneInfo.options[0]"
//
//
// 連携：プロパティー画面(BasePropEdit)
//          表示例
//          root +-- data +--local
//               |        +--server
//               +-- browser +--executable
//               |           +--options
//               +-- cgi +--geneInfo +--url
//                                   +--options
//
//
//
public class BaseProps {
    Properties props    = new Properties();

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseProps() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setProperties(Properties p) {
        props = p;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Properties getProperties() {
        return(props);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadPropertyies(String filename) {
        try {
            props.load(new FileInputStream(filename));
        }
        catch (Exception e) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void storePropertyies(String filename, String header) {
        try {
            props.store(new FileOutputStream(filename), header);
        }
        catch (Exception e) {
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void load(InputStream is) throws Exception {
        props.load(is);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void store(OutputStream os, String head) throws Exception {
        props.store(os, head);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setProperty(String key, String val) {
        int idx = 0;
        setProperty(key, idx, val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getProperty(String key) {
        int idx = 0;
        return(getProperty(key, idx));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setProperty(String key, int idx, String val) {
        String propKey = key + "[" + idx + "]";
        props.setProperty(propKey, val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getProperty(String key, int idx) {
        String propKey = key + "[" + idx + "]";
        return(props.getProperty(propKey));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public HashMap getPropKeys(String p) {
        HashMap propKeys = new HashMap();
        String propKeyName;
        String key;
        int count;

        String prefix = "";
        if (! p.equals("")) {
            prefix = p + ".";
        }

        //
        for(Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
            propKeyName = (String)e.nextElement();
            if (propKeyName.startsWith(prefix)) {
                key = propKeyName.substring(prefix.length());

                int idx = key.indexOf(".");
                if (0 < idx) {
                    // 該当データあり
                    key = key.substring(0, idx);
                    if (propKeys.containsKey(key)) {
                        count = ((Integer)propKeys.get(key)).intValue();
                        count++;
                    }
                    else {
                        count = 1;
                    }
                }
                else {
                    idx = key.indexOf("[");
                    key = key.substring(0, idx);
                    count = 1;
                }
                propKeys.put(key, new Integer(count));
            }
        }

        return(propKeys);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ArrayList getPropValues(String prefix) {
        ArrayList propKeys   = new ArrayList();
        ArrayList propValues = new ArrayList();
        String propKeyName;

        // プロパティーのキーを順に取りだし、「対象データであるか」をチェック
        for(Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
            propKeyName = (String)e.nextElement();

            if (! propKeyName.startsWith(prefix)) {
                // prefix で始まっていない
                continue;
            }

            String work = propKeyName.substring(prefix.length());
            if (0 <= work.indexOf(".")) {
                // さらに下位の区分がある
                continue;
            }

            propKeys.add(propKeyName);
        }

        for(int i = 0; i < propKeys.size(); i++) {
            propValues.add(null);
        }
        for(int i = 0; i < propKeys.size(); i++) {
            propKeyName = (String)propKeys.get(i);

            String strIdx = propKeyName.substring(propKeyName.indexOf("[")+1, propKeyName.indexOf("]"));
            int idx = Integer.valueOf(strIdx).intValue();

            propValues.set(idx, props.getProperty(propKeyName));
        }

        return(propValues);
    }

    ///////////////////////////////////////////////////////////////////////////
    // プロパティーの複製を作成
    public Properties cloneProp() {
        Properties newProp = new Properties();

        for(Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            String val = props.getProperty(key);
            newProp.setProperty(key, val);
        }

        return(newProp);
    }







}
