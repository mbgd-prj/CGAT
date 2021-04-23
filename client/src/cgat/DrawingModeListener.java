
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawingModeListener implements ActionListener {
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawingModeListener(ViewWindow vWin) {
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    // リスト選択
    public void actionPerformed(ActionEvent e) {
        // 選択されたモードをセットする
        int mode = 0;
        viewWin.setDrawMode(mode);
    }

}
