package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispAlignmentSequenceCommand implements ActionListener {
    private MbgdDataMng mbgdDataMng;

    String sp1Name;
    int from1, to1;
    String sp2Name;
    int from2, to2;
    int dir;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispAlignmentSequenceCommand(MbgdDataMng dataMng) {
        mbgdDataMng = dataMng;
        setRegion1(null, 1, 1);
        setRegion2(null, 1, 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegion1(String s, int f, int t) {
        sp1Name = s;
        from1   = f;
        to1     = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegion2(String s, int f, int t) {
        sp2Name = s;
        from2   = f;
        to2     = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDirection(int d) {
        dir = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        if ((sp1Name == null) || (sp2Name == null)) {
            // データが未設定
            return;
        }

        // alignment 結果をサーバより取得
        String alignSeq1 = "";
        String alignSeq2 = "";
        MbgdData mbgdData = MbgdData.Instance();

        mbgdDataMng.loadAlignmentSeq(sp1Name, from1, to1, sp2Name, from2, to2);
        alignSeq1 = mbgdDataMng.getAlignmentSeq(sp1Name, from1, to1,
                                                sp2Name, from2, to2,
                                                true);
        alignSeq2 = mbgdDataMng.getAlignmentSeq(sp1Name, from1, to1,
                                                sp2Name, from2, to2,
                                                false);
        AlignSeq as = mbgdDataMng.getAlignSeq(sp1Name, from1, to1,
                                              sp2Name, from2, to2);

        // 配列表示 window
        DispAlignSequence dispAlignSeq = new DispAlignSequence();
        dispAlignSeq.setSize(600, 400);
        dispAlignSeq.setVisible(true);
        dispAlignSeq.setTitle("Alignment Sequence : " + sp1Name + " - " + sp2Name);

        // 配列データを表示
//        dispAlignSeq.setAlignSeq(sp1Name, from1, to1, alignSeq1,
//                                 sp2Name, from2, to2, alignSeq2);
        dispAlignSeq.setAlignSeq(as, sp1Name, sp2Name);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ２つの配列
    public String convAlignSequenceFormat(int from1, int to1, String seq1,
                                          int from2, int to2, String seq2) {
        String alignSeq = "";
        String pos;
        String posHead = "          ";   // ' ' が 10 文字
        int    posHeadLen = posHead.length();

        int dir1;
        int dir2;

        //
        dir1 = -1;
        if (from1 < to1) {
            dir1 = 1;
        }
        dir2 = -1;
        if (from2 < to2) {
            dir2 = 1;
        }

        for(int i = 0; i < seq1.length(); i += 60) {
            String subSeq1;
            String subSeq2;
            String comp = "";

            try {
                subSeq1 = seq1.substring(i, i + 60);
                subSeq2 = seq2.substring(i, i + 60);
            }
            catch (IndexOutOfBoundsException e) {
                subSeq1 = seq1.substring(i);
                subSeq2 = seq2.substring(i);
            }
            catch (Exception e2) {
                continue;
            }

            for(int j = 0; j < subSeq1.length(); j++) {
                char c1, c2;
                c1 = subSeq1.charAt(j);
                c2 = subSeq2.charAt(j);
                if (c1 == c2) {
                    comp += ":";
                }
                else {
                    comp += " ";
                }
            }

            //
            pos = String.valueOf(from1 + i * dir1);
            alignSeq += posHead.substring(pos.length()) + pos;
            alignSeq += "  " + subSeq1 + "\n";

            //
            alignSeq += posHead;
            alignSeq += "  " + comp    + "\n";

            //
            pos = String.valueOf(from2 + i * dir2);
            alignSeq += posHead.substring(pos.length()) + pos;
            alignSeq += "  " + subSeq2 + "\n";
            alignSeq += "\n";

        }

        return alignSeq;
    }


}
