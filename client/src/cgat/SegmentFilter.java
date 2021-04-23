package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */
import java.util.regex.*;

///////////////////////////////////////////////////////////////////////////////
// フィルタリング条件にあうデータを表示対象とする
// 【注意】マスクするわけでは無い
public class SegmentFilter {

    ///////////////////////////////////////////////////////////////////////////
    //
    public SegmentFilter() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterBetween(RegionInfoList regInfo, int idxItem, double val1, double val2) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if ((val1 <= val) && (val <= val2)) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterExceptBetween(RegionInfoList regInfo, int idxItem, double val1, double val2) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if ((val < val1) || (val2 < val)) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterLessThan(RegionInfoList regInfo, int idxItem, double val1) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if (val < val1) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterLessEqual(RegionInfoList regInfo, int idxItem, double val1) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if (val <= val1) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterEqual(RegionInfoList regInfo, int idxItem, double val1) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if (val == val1) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterEqual(RegionInfoList regInfo, int idxItem, String val1) {
        RegionInfo r;
        String val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = r.getAttr(idxItem);
            if (val.equalsIgnoreCase(val1)) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 正規表現でのフィルタリング
    public static void filterRegex(RegionInfoList regInfo, int idxItem, String pat) {
        RegionInfo r;
        String val;

        pat = ".*" + pat + ".*";
        Pattern p = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = r.getAttr(idxItem);
            if (p.matcher(val).matches()) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterGreaterEqual(RegionInfoList regInfo, int idxItem, double val1) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if (val1 <= val) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterGreaterThan(RegionInfoList regInfo, int idxItem, double val1) {
        RegionInfo r;
        double val;

        for(int i = 0; i < regInfo.size(); i++) {
            r = regInfo.getRegionInfo(i);
            val = Double.valueOf(r.getAttr(idxItem)).doubleValue();
            if (val1 < val) {
//                r.setFilter(true);
            }
            else {
                r.setFilter(false);
            }
        }
    }

}
