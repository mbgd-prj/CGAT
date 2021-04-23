
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.event.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DirButton extends BaseButton {
    private int dataType;       //

    ///////////////////////////////////////////////////////////////////////////
    //
    public DirButton(int type) {
        super("");
        dataType = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setButtonLabel(boolean d) {
        if (d) {
            setText("+");
        }
        else {
            setText("-");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 矢印ボタン等により、配列の表示報告が変更になった場合の処理
    public void update(Observable o, Object arg) {
        if (o instanceof ViewWindow) {
            update((ViewWindow) o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(ViewWindow viewWin, Object arg) {
        boolean dir;

        // 現在の DIR を取得
        dir = viewWin.getRegDir(dataType);

        // ボタンラベルを更新
        setButtonLabel(dir);
    }

}
