package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DispRawSequenceCommand implements ActionListener {
    String spName;
    int from;
    int to;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DispRawSequenceCommand() {
        setSpName("");
        setFrom(1);
        setTo(1);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSpName(String sp) {
        if (sp != null) {
            spName = sp;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpName() {
        return spName;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrom(int f) {
        from = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTo(int t) {
        to = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getFrom() {
        return(from);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getTo() {
        return(to);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        String sp;
        int f, t;

        sp = getSpName();
        f = getFrom();
        t = getTo();

        MbgdDataMng mbgdDataMng = MbgdDataMng.Instance();

        // 配列データの取得
        String sequence = "";
        try {
            UrlFile url;
            String cgiPath;

            cgiPath = mbgdDataMng.getBasePath()+"cgi-bin/getSequence.cgi?"
                        + "reg=" + sp + ":" + f + "-" + t;

            url = new UrlFile(cgiPath);
            sequence = url.readLine();
        }
        catch (Exception e2) {
            Dbg.println(1, "Exception : get sequence["+e2.getMessage()+"]");
        }

        DispRawSequence dispRawSeq = DispRawSequence.Instance();
        dispRawSeq.setSize(600, 400);
        dispRawSeq.setVisible(true);
        dispRawSeq.setTitle("Sequence : " + sp);

        String text = dispRawSeq.convFastaText( sequence, f, t);
        dispRawSeq.setHtml("<HR><PRE>" + text + "</PRE><HR>");
    }

}
