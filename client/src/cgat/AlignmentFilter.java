package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */
import java.util.regex.*;

///////////////////////////////////////////////////////////////////////////////
// フィルタリング条件にあうデータを表示対象とする
// 【注意】マスクするわけでは無い
public class AlignmentFilter {

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentFilter() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterBetween(AlignmentList alignInfo, String nameItem,
                                     double val1, double val2) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if ((val1 <= val) && (val <= val2)) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterExceptBetween(AlignmentList alignInfo, String nameItem,
                                           double val1, double val2) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if ((val < val1) || (val2 < val)) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterLessThan(AlignmentList alignInfo, String nameItem,
                                      double val1) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if (val < val1) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterLessEqual(AlignmentList alignInfo, String nameItem,
                                       double val1) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if (val <= val1) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterEqual(AlignmentList alignInfo, String nameItem,
                                   double val1) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if (val == val1) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterEqual(AlignmentList alignInfo, String nameItem,
                                   String val1) {
        Alignment a;
        String val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = alignInfo.getAttr(nameItem, i);
            if (val.equalsIgnoreCase(val1)) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 正規表現でのフィルタリング
    public static void filterRegex(AlignmentList alignInfo, String nameItem,
                                   String pat) {
        Alignment a;
        String val;

        pat = ".*" + pat + ".*";
        Pattern p = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = alignInfo.getAttr(nameItem, i);
            if (p.matcher(val).matches()) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterSelect(AlignmentList alignInfo, String nameItem,
                                    String sel) {

        String pat;
        if (AlignmentFilterDialog.OPT_ORTHOLOGS.equals(sel)) {
            pat = "[3]";
        }
        else if (AlignmentFilterDialog.OPT_DUPLICATED.equals(sel)) {
            pat = "[12]";
        }
        else if (AlignmentFilterDialog.OPT_ELIMINATE_PARALOGS.equals(sel)) {
            pat = "[123]";
        }
        else if (AlignmentFilterDialog.OPT_PARALOGS.equals(sel)) {
            pat = "[0]";
        }
        else {
            return;
        }

        filterRegex(alignInfo, nameItem, pat);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterGreaterEqual(AlignmentList alignInfo, String nameItem,
                                          double val1) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if (val1 <= val) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void filterGreaterThan(AlignmentList alignInfo, String nameItem,
                                         double val1) {
        Alignment a;
        double val;

        for(int i = 0; i < alignInfo.size(); i++) {
            a = alignInfo.getAlignment(i);
            val = Double.parseDouble(alignInfo.getAttr(nameItem, i));
            if (val1 < val) {
//                a.setFilter(true);
            }
            else {
                a.setFilter(false);
            }
        }
    }

}
