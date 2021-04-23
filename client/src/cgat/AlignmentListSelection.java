
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentListSelection implements MouseListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentListSelection(ViewWindow vWin) {
        viewWin = vWin;
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
    // クリック時の処理
    //
    // 注意事項
    //   ダブルクリックした場合、合計２回も呼び出される。
    //     ダブルクリックの１回目のクリックでもこのメソッドが呼び出されるため。
    //   トリプルクリックした場合、合計３回も呼び出される。
    //
    //   mouseClicked() なのだから当たり前のことかもしれないが、
    //   ダブルクリックされた場合のみ処理したいときは、どのようにすれば良いのかな？
    //   (シングルクリックやトリプルクリックは処理しない)
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable)e.getSource();
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        int from1 = 1;
        int to1   = 1;
        int from2 = 1;
        int to2   = 1;
        for(int i = 0; i < table.getColumnCount(); i++) {
            // from1 を取得
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_FROM1)) {
                from1 = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // to1 を取得
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_TO1)) {
                to1 = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // from2 を取得
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_FROM2)) {
                from2 = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // to2 を取得
            if (table.getColumnName(i).equalsIgnoreCase(DispAlignmentList.TAB_ITEM_TO2)) {
                to2 = ((Integer)table.getValueAt(row, i)).intValue();
            }
        }


        // クリックした位置を中心に region を表示する
        viewWin.viewPos((from1 + to1) / 2, (from2 + to2) / 2);
    }

}
