
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import javax.swing.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionListSelection implements MouseListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionListSelection(ViewWindow vWin) {
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

        String spec = "";
        int from = 1;
        int to   = 1;
        for(int i = 0; i < table.getColumnCount(); i++) {

            // SPEC name を取得
            if (i == 0) {       // 最初のフィールドに生物種名が格納されている
                spec = (String)table.getValueAt(row, i);
            }
            // from を取得
            if (table.getColumnName(i).equalsIgnoreCase(SearchResults.TAB_ITEM_FROM)) {
                from = ((Integer)table.getValueAt(row, i)).intValue();
            }
            // to を取得
            if (table.getColumnName(i).equalsIgnoreCase(SearchResults.TAB_ITEM_TO)) {
                to = ((Integer)table.getValueAt(row, i)).intValue();
            }
        }

        // クリックされた ORF を中心に表示する
        viewWin.viewPos(spec, (from + to) / 2);
    }


}
