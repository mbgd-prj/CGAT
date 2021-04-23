package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class ColoredTableCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable tab,
                                                   Object val,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int col) {

        Color c = (Color)tab.getValueAt(row, col);
        setForeground(c);
        setBackground(c);
        validate();
        return this;
    }
}
