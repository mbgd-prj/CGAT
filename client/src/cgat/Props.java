
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.text.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

///////////////////////////////////////////////////////////////////////////////
//
public class Props extends JTable {
    String[] columnNames = {
        "Value"
    };

    BaseProps baseProps;
    DefaultTableModel model;
    DateFormat df = DateFormat.getDateTimeInstance();
    NumberFormat nf = NumberFormat.getInstance();
    int times = 0;
    String propName;
    EditPropValueCommand editPropValueCommand;

    ///////////////////////////////////////////////////////////////////////////
    //
    public Props(String p, BaseProps b) {
        if (p == null) {
            return;
        }

        propName = p;
        baseProps = b;

        model = new DefaultTableModel(columnNames, 0);
        setModel(model);

        // Table を編集可能とする
        editPropValueCommand = new EditPropValueCommand(baseProps);
        DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField());
        cellEditor.setClickCountToStart(1);
        cellEditor.addCellEditorListener(editPropValueCommand);

        DefaultTableColumnModel cmodel = (DefaultTableColumnModel)getColumnModel();
        TableColumn column = cmodel.getColumn(0);
        column.setCellEditor(cellEditor);

        // カラム
        String[] row = new String[columnNames.length];

        // 表示データ
        ArrayList valueList = baseProps.getPropValues(propName);
        for(int i = 0; i < valueList.size(); i++) {
            row[0] = (String)valueList.get(i);
            model.addRow(row);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void valueChanged(ListSelectionEvent e) {
        DefaultListSelectionModel select = (DefaultListSelectionModel)e.getSource();
        if (select.isSelectionEmpty() == true) {
            return;     // for Swing 1.0.3
        }
        else if (e.getValueIsAdjusting() == true) {
            return;     // for Swing 1.1
        }
        super.valueChanged(e);
        int row = getSelectedRow();
        String propKey   = propName+"["+String.valueOf(row)+"]";
        String propValue = (String)model.getValueAt(row, 0);

        editPropValueCommand.setPropInfo(propName, row);
    }


}
