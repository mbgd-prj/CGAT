
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.geom.*;

///////////////////////////////////////////////////////////////////////////////
// Font を変形（倍角や回転等）する
//
public class MyFont {
    Font myFont;

    ///////////////////////////////////////////////////////////////////////////
    //
    public MyFont(Font f) {
        setFont(f);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFont(Font f) {
        // フォントをコピーする
        myFont = new Font(f.getName(), f.getStyle(), f.getSize());
    }

    ///////////////////////////////////////////////////////////////////////////
    // フォントの大きさを変える
    // 引数
    //     w : 横幅の倍率
    //     h : 縦幅の倍率
    public void trans(double w, double h) {
        AffineTransform at = new AffineTransform();
        at.setToScale(w, h);
        myFont.deriveFont(at);
    }

    ///////////////////////////////////////////////////////////////////////////
    // フォントの向きを変える（左回転）
    // 引数
    //     r : 回転角（ラジアン）
    public void trans(double r) {
        AffineTransform at = new AffineTransform();
        at.setToRotation(r);
        myFont.deriveFont(at);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Font getFont() {
        return(myFont);
    }




}
