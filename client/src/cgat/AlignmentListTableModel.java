
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.table.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentListTableModel extends BaseTableModel {

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentListTableModel(Object [] colNames, int rowNum) {
        super(colNames, rowNum);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentListTableModel(Object [][] data, Object [] colNames) {
        super(data, colNames);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Class getColumnClass(int col) {
        return(getValueAt(0, col).getClass());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean isCellEditable(int row, int col) {
        return(false);
    }


}
