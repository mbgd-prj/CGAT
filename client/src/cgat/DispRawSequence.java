package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispRawSequence extends BaseViewer {
    static DispRawSequence _instance = null;

    String lineHead = "          ";

    ///////////////////////////////////////////////////////////////////////////
    //
    static public DispRawSequence Instance() {
        if (_instance == null) {
            _instance = new DispRawSequence();
        }
        return(_instance);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private DispRawSequence() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////
    // １つの配列を 60文字/１行で表示する
    static public String convFastaHtml(String seq1) {
        String html = "";
        int len = seq1.length();

        for(int i = 0; i < len; i += 60) {
            int e = i + 60;
            if (len < e) {
                e = len;
            }
            html += seq1.substring(i, e) + "<BR>\n";
        }

        return(html);
    }

    ///////////////////////////////////////////////////////////////////////////
    // １つの配列を 60文字/１行で表示する
    static public String convFastaText(String seq1) {
        String text = "";
        int len = seq1.length();

        for(int i = 0; i < len; i += 60) {
            int e = i + 60;
            if (len < e) {
                e = len;
            }
            text += seq1.substring(i, e) + "\n";
        }

        return(text);
    }

    ///////////////////////////////////////////////////////////////////////////
    // １つの配列を 60文字/１行で表示する
    public String convFastaHtml(String seq1, int start1, int max1,
                                RegionColorList regColorList) {
        String html = "";
        int len = seq1.length();

        // 最初の (from+to+色) 情報を取得
        int idx = 0;
        RegionColor regColor = regColorList.getRegionColor(idx++);

        for(int i = 0; i < len; i += 60) {
            int e = i + 60;
            if (len < e) {
                e = len;
            }

            String pos = Integer.toString((start1 + i) % max1 + 1);
            html += lineHead.substring(0, lineHead.length() - pos.length());
            html += pos;
            html += "  ";

            if (regColor != null) {
            }
            html += seq1.substring(i, e) + "\n";
        }

        return(html);
    }

    ///////////////////////////////////////////////////////////////////////////
    // １つの配列を 60文字/１行で表示する
    public String convFastaText(String seq1, int start1, int max1) {
        String text = "";
        int len = seq1.length();

        for(int i = 0; i < len; i += 60) {
            int e = i + 60;
            if (len < e) {
                e = len;
            }

            String pos = Integer.toString((start1 + i) % max1 + 1);
            text += lineHead.substring(0, lineHead.length() - pos.length());
            text += pos;
            text += "  ";
            text += seq1.substring(i, e) + "\n";
        }

        return(text);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String convFastaHtml(String seq1,
                                String seq2) {
        String html = "";

        return(html);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String convFastaHtml(String seq1, int start1, int max1,
                                String seq2, int start2, int max2,
                                RegionColorList regColorList) {
        String html = "";



        return(html);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String convFastaText(String seq1, int start1, int max1,
                                String seq2, int start2, int max2) {
        String text = "";
        int len;
        int len1 = seq1.length();
        int len2 = seq2.length();

        len = len1;
        if (len < len2) {
            len = len2;
        }

        for(int i = 0; i < len; i += 60) {
            if (len1 < i) {
                // seq2 の方が長い場合
                int len0;
                if (len2 < i + 60) {
                    len0 = len2;
                }
                else {
                    len0 = 60;
                }

                text += lineHead + "  ";
                for(int j = 0; j < len0; j++) {
                    text += "-";
                }
            }
            else {
                int e = i + 60;
                if (len1 < e) {
                    e = len1;
                }
                String pos = Integer.toString((start1 + i) % max1 + 1);
                text += lineHead.substring(0, lineHead.length() - pos.length());
                text += pos;
                text += "  ";
                text += seq1.substring(i, e) + "\n";
            }

            if (len2 < i) {
                // seq1 の方が長い場合
                int len0;
                if (len1 < i + 60) {
                    len0 = len1;
                }
                else {
                    len0 = 60;
                }

                text += lineHead + "  ";
                for(int j = 0; j < len0; j++) {
                    text += "-";
                }
            }
            else {
                int e = i + 60;
                if (len2 < e) {
                    e = len2;
                }
                String pos = Integer.toString((start2 + i) % max2 + 1);
                text += lineHead.substring(0, lineHead.length() - pos.length());
                text += pos;
                text += "  ";
                text += seq2.substring(i, e) + "\n";
            }

            text += "\n";
        }

        return(text);
    }

}
