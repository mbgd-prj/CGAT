
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.table.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseTableModel extends DefaultTableModel {
    private Vector tabSortInfo[];

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTableModel() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTableModel(Object [] colNames, int rowNum) {
        super(colNames, rowNum);

        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTableModel(Object [][] data, Object [] colNames) {
        super(data, colNames);

        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        tabSortInfo = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Class getColumnClass(int col) {
        try {
            return(getValueAt(0, col).getClass());
        }
        catch (Exception e) {
            return "".getClass();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isCellEditable(int row, int col) {
        return(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Vector getInfo(int idx) {
        try {
            return(tabSortInfo[idx]);
        } catch (java.lang.NullPointerException np) {
            return(null);
        } catch (java.lang.ArrayIndexOutOfBoundsException np) {
            return(null);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void sort(int columnIdx) {
        tabSortInfo = null;

        if (getColumnCount() <= columnIdx) {
            // column の範囲外
            return;
        }

        // table 内容をもとに、sort 情報を作成
        tabSortInfo = new Vector[dataVector.size()];
        for(int i = 0; i < dataVector.size(); i++) {
            tabSortInfo[i] = (Vector)dataVector.get(i);
        }
        Arrays.sort(tabSortInfo, new CompTabInfo(columnIdx));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sort された情報をもとにデータを返す
    public Object getValueAt(int row, int column) {
        Object ret = null;

        if (tabSortInfo == null) {
            // sort 情報が無い
            ret = super.getValueAt(row, column);
        }
        else {
            Vector v = getInfo(row);
            ret = v.get(column);
        }

        return(ret);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class CompTabInfo implements Comparator {
        int baseIdx;

        ///////////////////////////////////////////////////////////////////////
        //
        public CompTabInfo(int idx) {
            baseIdx = idx;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public int compare(Object objA, Object objB) {
            Vector vecA = (Vector)objA;
            Vector vecB = (Vector)objB;

            Object a = vecA.get(baseIdx);
            Object b = vecB.get(baseIdx);

            if ((a == null) && (b == null)) {
                return 0;
            }
            else if (a == null) {
                return -1;
            }
            else if (b == null) {
                return 1;
            }

            String className = a.getClass().toString();
            if (className.endsWith("String")) {
                String strA = (String)a;
                String strB = (String)b;
                return(strA.compareTo(strB));
            }
            else if (className.endsWith("Integer")) {
                Integer intA = (Integer)a;
                Integer intB = (Integer)b;
                return(intA.compareTo(intB));
            }
            else if (className.endsWith("Float")) {
                Float floatA = (Float)a;
                Float floatB = (Float)b;
                return(floatA.compareTo(floatB));
            }
            else {
                if (a.equals(b)) {
                    return 0;
                }
                return(a.toString().compareTo(b.toString()));
            }
        }
    }

}
