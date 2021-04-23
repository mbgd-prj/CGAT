
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DirInfo extends Observable {
    BitSet dir;
    int specNo;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DirInfo() {
        dir = new BitSet(0);
        setDirInfo(true);
        setSpecNo(1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDirInfo(boolean d) {
        setDirInfoSilent(d);

//ViewRegionInfoList vRegInfo = ViewRegionInfoList.Instance();
//if (vRegInfo != null) {
//    vRegInfo.makeAlign();
//}
        // 向き情報が変更されたことを通知
        notifyObservers(new Integer(specNo));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDirInfoSilent(boolean d) {
        if (d) {
            dir.set(0);
        }
        else {
            dir.clear(0);
        }

        setChanged();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getDirInfo() {
        return(dir.get(0));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSpecNo(int sno) {
        specNo = sno;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSpecNo() {
        return(specNo);
    }

}
