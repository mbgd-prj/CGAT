package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class AlignTableCellRenderer extends DefaultTableCellRenderer {
    protected int alignMode = SwingConstants.LEFT;

    public AlignTableCellRenderer(int m) {
        super();
        setAlignMode(m);
    }

    public void setAlignMode(int m) {
        alignMode = m;
    }

    public Component getTableCellRendererComponent(JTable tab,
                                                   Object val,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int col) {
        super.getTableCellRendererComponent(tab, val,isSelected, hasFocus, row, col);


        //
        setHorizontalAlignment(alignMode);
        return this;
    }
}
