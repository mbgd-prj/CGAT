
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;

///////////////////////////////////////////////////////////////////////////////
// 文字を描く
// これらのメソッドでは、Font の向きは、変更しません。
//     たとえば drawStringUp2Down() では、上から下に向けて文字を描きますが
//     通常の横書き用 Font を渡しても、Font を 90 度右回転して描画しません。
//     90 度右回転した Font を準備してから drawStringUp2Down() を呼び出してください。
//     【ご参考】MyFont.java
// 注意：アルファベット以外の文字は、対応していません。
public class DrawString {

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawString() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // 左から右に描く（通常）
    public static void drawStringLeft2Right(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // 使用中のフォントを待避
        bakFont = g.getFont();

        // 指定されたフォントで文字を描く
        g.setFont(f);
        g.drawString(s, x, y);

        // 元のフォントに戻す
        g.setFont(bakFont);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 右から左に描く
    //     正確には、左から右へ文字を逆順に描画する
    public static void drawStringRight2Left(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // 使用中のフォントを待避
        bakFont = g.getFont();

        // 指定されたフォントで文字を描く
        g.setFont(f);

        // 文字を描画
        StringBuffer newStr = new StringBuffer("");
        for(int i = s.length() - 1; i >= 0; i--) {
            newStr.append(s.charAt(i));
        }
        g.drawString(newStr.toString(), x, y);

        // 元のフォントに戻す
        g.setFont(bakFont);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 上から下に描く
    public static void drawStringUp2Down(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // 使用中のフォントを待避
        bakFont = g.getFont();

        // 指定されたフォントで文字を描く
        g.setFont(f);

        // 文字を描画
        int fontHeight = g.getFontMetrics().getHeight();
        for(int i = 0; i < s.length(); i--) {
            String c = s.substring(i, i + 1);
            g.drawString(c, x, y + fontHeight * i);
        }

        // 元のフォントに戻す
        g.setFont(bakFont);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 下から上に描く
    //     正確には、上から下へ文字を逆順に描画する
    public static void drawStringDown2Up(Graphics g, Font f, String s, int x, int y) {
        Font bakFont;

        // 使用中のフォントを待避
        bakFont = g.getFont();

        // 指定されたフォントで文字を描く
        g.setFont(f);

        // 文字を描画
        int fontHeight = g.getFontMetrics().getHeight();
        for(int i = s.length() - 1; i >= 0; i--) {
            String c = s.substring(i, i + 1);
            g.drawString(c, x, y + fontHeight * (s.length() - i - 1));
        }

        // 元のフォントに戻す
        g.setFont(bakFont);
    }



}
