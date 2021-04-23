package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class LabelMultiLine extends JPanel {
    private JLabel [] text;

    ///////////////////////////////////////////////////////////////////////////
    //
    public LabelMultiLine(String str, String delim) {
        super();
        _init(str, delim);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public LabelMultiLine(String str) {
        _init(str, "\n");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void _init(String str, String delim) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        text = new JLabel[3];
        for(int i = 0; i < 3; i++) {
            text[i] = new JLabel("");
            add(text[i]);
        }

        setText(str, delim);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setText(String str, String delim) {
        StringTokenizer labelList = new StringTokenizer(str, delim);
        for(int i = 0; i < 3; i++) {
            if (labelList.hasMoreTokens() == false) {
                // これ以上 token がない
                text[i].setText("");
                continue;
            }

            String txt = labelList.nextToken();
            text[i].setText(txt);
        }
    }



/*
何度か setText() を呼び出すと、画面に表示されない現象が発生した。
  正確には、画面が更新されない。
  ウィンドウサイズをマウスで変更すると表示(描画)される。

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setText(String str, String delim) {
        // 登録済みの GUI を削除
        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        StringTokenizer labelList = new StringTokenizer(str, delim);
        for(; labelList.hasMoreTokens(); ) {
            String txt = labelList.nextToken();
            JLabel lab = new JLabel(txt);
            add(lab);
        }
    }
*/

}
