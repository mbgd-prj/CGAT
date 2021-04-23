
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

///////////////////////////////////////////////////////////////////////////////
//
public class EditPropValueCommand implements CellEditorListener {
    BaseProps baseProp;
    String propName;
    int propRow;

    ///////////////////////////////////////////////////////////////////////////
    //
    public EditPropValueCommand(BaseProps b) {
        baseProp = b;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setPropInfo(String p, int r) {
        propName = p;
        propRow  = r;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void editingStopped(ChangeEvent e) {
        // ���ƹ���
        DefaultCellEditor editor = (DefaultCellEditor)e.getSource();

        baseProp.setProperty(propName, propRow, (String)editor.getCellEditorValue());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void editingCanceled(ChangeEvent e) {
    }



}
