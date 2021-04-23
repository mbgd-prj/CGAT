package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispAlignSequence extends BaseViewer {
    private static final String lineHead = "          ";

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispAlignSequence() {
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

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignSeq(AlignSeq as, String name1, String name2) {
        StringBuffer html = new StringBuffer();

        //
        html.append("<HR><CENTER><H2>");
        html.append(name1);
        html.append(" - ");
        html.append(name2);
        html.append("</H2></CENTER>\n");
        html.append("<PRE>\n");
        html.append(as.getAlignSeq(name1));
        html.append("</PRE>\n");

        setHtml(html.toString());

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAlignSeq(String name1, int from1, int to1, String seq1,
                            String name2, int from2, int to2, String seq2) {
        String html;
        String blank = "               ";
        String strSpPos;
        int i;

        int dir1 = 1;
        if (to1 < from1) {
            dir1 = -1;
        }
        int dir2 = 1;
        if (to2 < from2) {
            dir2 = -1;
        }

        html = "<HR><CENTER><H2>" + name1 + " - " + name2 + "</H2></CENTER>\n";
        html += "<PRE>\n";

        // ６０文字ずつ切り出して出力
        int loopMax = seq1.length();
        for(i = 0; i < loopMax; i += 60) {
            int endIdx = i + 60;
            if (loopMax < endIdx) {
                // 最後の部分配列
                endIdx = loopMax;
            }
            String subSeq1 = seq1.substring(i, endIdx);
            String subSeq2 = seq2.substring(i, endIdx);

            //
            strSpPos = blank + name1 + " " + String.valueOf(from1 + i * dir1);
            html += strSpPos.substring(strSpPos.length() - blank.length() + 1) + " ";
            html += subSeq1 + "\n";

            // 配列の相同性情報
            html += blank;
            for(int j = 0; j < subSeq1.length(); j++) {
                char c1 = subSeq1.charAt(j);
                char c2 = subSeq2.charAt(j);

                if (c1 == c2) {
                    html += ":";
                }
                else {
                    html += " ";
                }
            }
            html += "\n";

            //
            strSpPos = blank + name2 + " " + String.valueOf(from2 + i * dir2);
            html += strSpPos.substring(strSpPos.length() - blank.length() + 1) + " ";
            html += subSeq2 + "\n";
            html += "\n";       // 見やすさのための改行
        }
/*
                                "<PRE>\n" +
                                alignSeq1 + "\n" +
                                alignSeq2 + "\n" +
                                "</PRE>\n" +

                                "<HR>\n"    }
*/
        html += "</PRE>\n";

        setHtml(html);
    }
}
