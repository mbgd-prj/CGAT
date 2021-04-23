
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseTable extends JTable{

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable(TableModel dm) {
        super(dm);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }


}
