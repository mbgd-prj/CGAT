
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
// マウスがクリックされたとき、JTable の内容をもとに sort を実行する
public class SortTableCommand implements MouseListener{
    private JTable tab;         // sort 対象の TABLE
    private int    tabColIdx;   // sort 基準となるカラムのインデックス(0-)

    ///////////////////////////////////////////////////////////////////////////
    //
    public SortTableCommand() {
        setTable(null);
        setBaseColumn(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public SortTableCommand(JTable t, int i) {
        setTable(t);
        setBaseColumn(i);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTable(JTable t) {
        tab = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setBaseColumn(int i) {
        tabColIdx = i;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
        int viewColumn;
        int modelColumn;

        if (tab == null) {
            return;
        }
        viewColumn = tab.getTableHeader().columnAtPoint(e.getPoint());
        modelColumn = tab.convertColumnIndexToModel(viewColumn);

        // クリックされたカラムを基準に並べ替え
        BaseTableModel tabModel = (BaseTableModel)tab.getModel();
        tabModel.sort(modelColumn);
        tab.repaint();
    }

}
