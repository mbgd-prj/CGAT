package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionColorList {

    ArrayList regColorList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionColorList() {
        clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        regColorList = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        return(regColorList.size());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void insert(RegionColor r) {
        regColorList.add(r);
    }
    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionColor getRegionColor(int index) {
        if ((index < 0) || (size() <= index)) {
            return(null);
        }
        return((RegionColor)regColorList.get(index));
    }




}