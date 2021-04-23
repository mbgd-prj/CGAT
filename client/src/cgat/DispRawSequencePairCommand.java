package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispRawSequencePairCommand implements ActionListener {
    private MbgdDataMng mbgdDataMng;
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispRawSequencePairCommand(MbgdDataMng dataMng, ViewWindow vWin) {
        mbgdDataMng = dataMng;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();
        String sp1Name = mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        String sp2Name = mbgdDataMng.getSpecName(MbgdDataMng.OPPO_SPEC);

        // 配列データの取得
        String sequence1 = "";
        String sequence2 = "";
        int start1, width1, max1;
        int start2, width2, max2;

        try {
            String basePath = mbgdDataMng.getBasePath();
            UrlFile url;
            String cgiPath;
            int from, to;

            width1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
            start1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - width1 / 2;
            max1   = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);

            from = start1 + 1;
            to   = start1 + width1;

            cgiPath = basePath+"cgi-bin/getSequence.cgi?"
                        + "reg=" + sp1Name + ":" + from + "-" + to;

            url = new UrlFile(cgiPath);
            sequence1 = url.readLine();

            if (max1 < to) {
                // 0bp をまたいでいる場合
                from = 1;
                to   = to - max1;
                cgiPath = basePath+"cgi-bin/getSequence.cgi?"
                            + "reg=" + sp1Name + ":" + from + "-" + to;
                url = new UrlFile(cgiPath);
                sequence1 += url.readLine();
            }


            width2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
            start2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - width2 / 2;
            max2   = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

            from = start2 + 1;
            to   = start2 + width2;
            cgiPath = basePath+"cgi-bin/getSequence.cgi?"
                        + "reg=" + sp2Name + ":" + from + "-" + to;

            url = new UrlFile(cgiPath);
            sequence2 = url.readLine();

            if (max2 < to) {
                // 0bp をまたいでいる場合
                from = 1;
                to   = to - max2;
                cgiPath = basePath+"cgi-bin/getSequence.cgi?"
                            + "reg=" + sp2Name + ":" + from + "-" + to;
                url = new UrlFile(cgiPath);
                sequence2 += url.readLine();
            }
        }
        catch (Exception e2) {
            Dbg.println(3, "Exception : get sequence["+e2.getMessage()+"]");
            return;
        }

        // 配列表示 window
        DispRawSequence dispRawSeq = DispRawSequence.Instance();
        dispRawSeq.setSize(600, 400);
        dispRawSeq.setVisible(true);
        dispRawSeq.setTitle("Sequence : " + sp1Name + " - " + sp2Name);

        // 配列データを整形
        String text1 = dispRawSeq.convFastaText(sequence1, start1, max1);
        String text2 = dispRawSeq.convFastaText(sequence2, start2, max2);
        dispRawSeq.setHtml( "<HR><CENTER><H2>" + sp1Name + "</H2></CENTER>\n" +
                            "<PRE>\n" + text1 + "</PRE>\n" +
                            "<HR><CENTER><H2>" + sp2Name + "</H2></CENTER>\n" +
                            "<PRE>\n" + text2 + "</PRE>\n" +
                            "<HR>\n");

    }

}
