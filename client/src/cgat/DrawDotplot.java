
/**
 * タイトル:     cgat<p>
 * 説明:         For TEST<p>
 * 著作権:       Copyright (c) higu<p>
 * 会社名:       W&G<p>
 * @author higu
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawDotplot extends JComponent {
    AlignmentList alignmentList;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawDotplot() {
        setAlignmentList(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignmentList(AlignmentList aList) {
        alignmentList = aList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint(Graphics g) {
        // X 軸の目盛等を描画
        // Y 軸の目盛等を描画
        // plot 領域を描画


        return;
    }

}
