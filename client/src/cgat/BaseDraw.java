
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseDraw extends JComponent {
    boolean debugMode = false;

    protected Color frameColor = Color.black;
    protected Color fgColor = Color.black;
    protected Color bgColor = new Color(160, 160, 160);

    static int WIDTH  = 450;
    static int HEIGHT = 75;
    static int XPAD = 2;
    static int YPAD = 2;
    int winWidth;           //
    int winHeight;          //

    HashMap fontWidth;              //　フォントの幅（各文字ごとの幅を保持する）
    int fh = -1;                    // フォントの高さ

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseDraw() {
        super();

        setDbgMode(false);

        setWindowSize(WIDTH, HEIGHT);

        Dimension d = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(d);        // 推奨サイズを設定
        setSize(d);

        // フォントサイズ
        fontWidth = new HashMap();
    }

    ///////////////////////////////////////////////////////////////////////////
    public float seqLenPerPixel(ViewWindow vwin) {
	int regWidth = vwin.getRegWidth();
	int winWidth = getWidth();
	return (float)regWidth / winWidth;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowSize(int w, int h) {
        winWidth  = w;
        winHeight = h;
        setSize(winWidth, winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowWidth(int w) {
        winWidth  = w;
        setSize(winWidth, winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowHeight(int h) {
        winHeight = h;
        setSize(winWidth, winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getWindowWidth() {
        return(winWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getWindowHeight() {
        return(winHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDbgMode(boolean dbg) {
        debugMode = dbg;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getDbgMode() {
        return(debugMode);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawChar(Graphics g, int x, int y, int w, char c) {
        String s;
        int dd;
        Integer fw;

        s = String.valueOf(c);
        fw = (Integer)fontWidth.get(s);
        if (fw == null) {
            fw = new Integer(g.getFontMetrics().stringWidth(s));
            fontWidth.put(s, fw);
        }
        dd = fw.intValue();

        g.drawString(s, x + (w - dd) / 2, y);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFgColor(Color c) {
        fgColor = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getFgColor() {
        return fgColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setBgColor(Color c) {
        bgColor = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getBgColor() {
        return bgColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint(Graphics g) {
        if (fh == -1) {
            fh = g.getFontMetrics().getHeight();
        }
        clear(g);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear(Graphics g) {
        Color c = getBgColor();
        g.setColor(c);
        g.fillRect(0, 0, winWidth, winHeight);
    }
    public void clearWhite(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, winWidth, winHeight);
    }

}
