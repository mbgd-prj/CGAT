
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawScale extends JComponent implements MouseListener, Observer {
    public static final boolean HORIZONTAL = true;
    public static final boolean VERTICAL = false;

    static int WIDTH  = 10;
    static int HEIGHT = 10;

    private MbgdDataMng mbgdDataMng;

    int memWidth  =  4;
    int memHeight = 10;

    String spec;
    boolean scaleType;              // true : 横スケール
    int scaleWinWidth;
    int scaleWinHeight;
    int scaleStart;
    int scaleWidth;
    int divisor;

    private static final Color markColor[] = {  Color.red,
                                                Color.blue,
                                                Color.green,
                                                Color.cyan,
                                                Color.magenta,
                                                Color.orange,
                                                Color.pink,
                                                Color.yellow};

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawScale(MbgdDataMng dataMng, String sp) {
        mbgdDataMng = dataMng;

        setSpec(sp);
        setScaleSize(WIDTH, HEIGHT);
        setScaleType(true);
        setScaleRange(0, 100);
        setDivisor(1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSpec(String sp) {
        spec = sp;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScaleSize(int w, int h) {
        setScaleWinWidth(w);
        setScaleWinHeight(h);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScaleWinWidth(int w) {
        scaleWinWidth = w;
        setSize(scaleWinWidth, scaleWinHeight);
        Dimension d = new Dimension(scaleWinWidth, scaleWinHeight);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScaleWinHeight(int h) {
        scaleWinHeight = h;
        setSize(scaleWinWidth, scaleWinHeight);
        Dimension d = new Dimension(scaleWinWidth, scaleWinHeight);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    ///////////////////////////////////////////////////////////////////////////
    // true  : 横スケール
    // false : 縦スケール
    public void setScaleType(boolean t) {
        scaleType = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScaleRange(int s, int w) {
        setScaleStart(s);
        setScaleWidth(w);
        repaint();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScaleStart(int s) {
        scaleStart = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setScaleWidth(int w) {
        scaleWidth = w;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getScaleStart() {
        return(scaleStart);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getScaleWidth() {
        return(scaleWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getScaleType() {
        return scaleType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDivisor(int d) {
        divisor = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDivisor() {
        return(divisor);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawScale() {
        Graphics g = getGraphics();
        if (g != null) {
            drawScale(g);
        }
        else {
            Dbg.println(9, "getGraphics() returns null");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawScale(Graphics g) {
        Color back;
        int w, h;

        back = g.getColor();
        g.setColor(Color.black);

        // 全長
        w = 0;
        h = 2;
        if (scaleType) {    // 横スケール
            g.fillRect(0, 0, scaleWinWidth, h);
        }
        else {              // 縦スケール
            g.fillRect(scaleWinWidth - h, 0, h, scaleWinHeight);
        }

        int N = 10;
        int tmpUnit = scaleWidth / N;        // とりあえず、１０等分したときの、ひと目盛
        int keta = (int)(Math.log(scaleWidth) / Math.log(N));        // 指数部
        double dUnit = scaleWidth / Math.pow(N, keta);               // 小数部
        int unit;

        dUnit += 0.5;
        unit = (int)dUnit;
        unit *= (int)(Math.pow(N, keta - 1));
        if (dUnit <= 1.0) {
            unit *= (int)(Math.pow(N, keta - 1));
        }
        else if (dUnit <= 3.0) {
            unit = (int)(3.0 * Math.pow(N, keta - 1));
        }
        else if (dUnit <= 5.0) {
            unit = (int)(5.0 * Math.pow(N, keta - 1));
        }
        else {
            unit = (int)(10.0 * Math.pow(N, keta - 1));
        }

        // 実際の分割数
        int n = scaleWidth / unit;
        int nn;
        if (n < 4) {
            nn = 1;
        }
        else if (n < 6) {
            nn = 2;
        }
        else {
            nn = 3;
        }


        int ofs = scaleStart % unit;     // 最初の目盛までのオフセット
        int ii = 0;
        for(int xPos = unit - ofs; xPos < scaleWidth; xPos += unit) {
            int winWidth;
            int scaWidth;
            if (scaleType) {    // 横スケール
                // Position を画面座標に変換
                int xOfs = (int)((double)xPos * (double)scaleWinWidth / (double)scaleWidth);

                // 目盛り線を描画
                g.fillRect(0 + xOfs - memWidth / 2, 0, memWidth, memHeight);
                if (ii % nn == 0) {
                    // 目盛を描画
                    int xOfsString = xOfs - 2 * (int)Math.log(scaleStart + xPos);
                    if (xOfsString < 0) {
                        xOfsString = 0;
                    }
                    g.drawString(Integer.toString(scaleStart + xPos), xOfsString, memHeight * 3);
                    g.fillRect(0 + xOfs - memWidth / 2, 0, memWidth, memHeight * 2);
                }
            }
            else {              // 縦スケール
                // Position を画面座標に変換
                int xOfs = (int)((double)xPos * (double)scaleWinHeight / (double)scaleWidth);

                // 目盛り線を描画
                g.fillRect(scaleWinWidth - memHeight, scaleWinHeight - xOfs - memWidth / 2, memHeight, memWidth);
                if (ii % nn == 0) {
                    // 目盛を描画
                    g.drawString(Integer.toString(scaleStart + xPos), memHeight, scaleWinHeight - xOfs);
                    g.fillRect(scaleWinWidth - memHeight * 2, scaleWinHeight - xOfs - memWidth / 2, memHeight * 2, memWidth);
                }
            }
            ii++;
        }

        g.setColor(back);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawMark(Graphics g) {
        int w, h;

        Color c;
        Polygon p;
        MarkEnt entList[] = mbgdDataMng.getMarkEntAll(getScaleType());
        int loopMax = entList.length;
        for(int i = 0; i < loopMax; i++) {
            MarkEnt ent   = entList[i];
            int posFrom   = ent.getPosFrom();
            int posTo     = ent.getPosTo();
            int colorType = ent.getColorType();

            int pos = (posFrom + posTo) / 2;        // ORF の中心位置
            if ((pos < scaleStart) || ((scaleStart + scaleWidth) < pos)) {
                // 表示範囲外
                continue;
            }

            // 表示色を決定
            c = markColor[colorType % markColor.length];
            g.setColor(c);
            //

            // Pos を描画
            if (scaleType == HORIZONTAL) {
                // 横スケール
                int xPos = (int)((float)(pos - scaleStart) / (float)scaleWidth * (float)scaleWinWidth);
                int x[] = { xPos - 3, xPos, xPos + 3 };
                int y[] = { 10, 0, 10 };
                p = new Polygon(x, y, 3);
                g.fillPolygon(p);
            }
            else {
                // 縦スケール
                int xPos = (int)((float)(pos - scaleStart) / (float)scaleWidth * (float)scaleWinHeight);
                int x[] = { scaleWinWidth - 10, scaleWinWidth, scaleWinWidth - 10 };
                int y[] = { scaleWinHeight - xPos - 3, scaleWinHeight - xPos, scaleWinHeight - xPos + 3 };
                p = new Polygon(x, y, 3);
                g.fillPolygon(p);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void paint(Graphics g) {
        super.paint(g);

        drawScale(g);

        drawMark(g);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void drawString1d2(Graphics g, int x, int y, int ival) {
        String str;
        int work;
        float fval;
        int keta;
        Font f;

        // 現在の Font を退避
        Font fontBack = g.getFont();

        keta = (int)(Math.log(ival) / Math.log(10));
        work = (int)((double)ival / Math.pow(10, keta - 2));

        fval = (float)work / 100.0f;

        //
        int w;
        int fontHeight;
        fontHeight = g.getFontMetrics().getHeight();

        // 小数部表示
        str = Float.toString(fval) + " x 10 ";
        f = new Font(g.getFont().getFontName(), Font.BOLD, 4);
        g.drawString(str, x, y + fontHeight + fontHeight / 2);

        // 指数部表示
        w = g.getFontMetrics().stringWidth(str);
        str = Integer.toString(keta);
        f = new Font(g.getFont().getFontName(), Font.BOLD, 2);
        g.drawString(str, x + w, y + fontHeight);

        // 退避していた Font を戻す
        g.setFont(fontBack);

        return;
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
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(Observable o, Object arg) {
        if (o instanceof ViewWindow) {
            update((ViewWindow)o, arg);
        }
        else if (o instanceof MbgdDataMng) {
            update((MbgdDataMng)o, arg);
        }
        else if (o instanceof SearchOrfCommand) {
            update((SearchOrfCommand)o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(ViewWindow vWin, Object arg) {
        int dpCenter;
        int dpStart;
        int dpWidth;

        if (scaleType) {    // 横
            dpCenter = vWin.getDpCenter(MbgdDataMng.BASE_SPEC);
            dpWidth = vWin.getDpWidth(MbgdDataMng.BASE_SPEC);
        }
        else {              // 縦
            dpCenter = vWin.getDpCenter(MbgdDataMng.OPPO_SPEC);
            dpWidth = vWin.getDpWidth(MbgdDataMng.OPPO_SPEC);
        }

        dpStart = dpCenter - dpWidth / 2;
        setScaleRange(dpStart, dpWidth);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(MbgdDataMng dataMng, Object arg) {
        int len1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int len2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        if (len1 < len2) {
            len1 = len2;
        }
        setScaleRange(0, len1);

        repaint();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(SearchOrfCommand soc, Object arg) {
        repaint();
    }



}

