
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.table.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionListTableModel extends BaseTableModel {

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionListTableModel() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionListTableModel(Object [] colNames, int rowNum) {
        super(colNames, rowNum);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionListTableModel(Object [][] data, Object [] colNames) {
        super(data, colNames);
    }



}
