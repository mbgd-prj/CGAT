package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DynamicSearchCommand implements ActionListener {
    private Frame frame;
    private MbgdDataMng dataMng;
    private ViewWindow viewWin;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DynamicSearchCommand(Frame f, MbgdDataMng dmng, ViewWindow vWin) {
        frame = f;
        dataMng = dmng;
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        DynamicSearchBaseDialog baseWin;
        DynamicSearchBaseDialog win;
        String dynSrchType;
        String cgiPath;

        String url = dataMng.getBasePath();

        // �������̲��̤����
        baseWin = new DynamicSearchBaseDialog(frame, url, "/dynSearch/getGuiInfo.cgi?type=top");
        baseWin.setSize(400, 300);

        // ɽ��
        baseWin.setVisible(true);

        // �������̼���
        dynSrchType = baseWin.getSearchType();
        cgiPath = baseWin.getCgiPath();
        if (cgiPath == null) {
            // �������̤����򤵤�ʤ��ä�
            return;
        }

        // �����ܺٲ��̤����
        win = new DynamicSearchBaseDialog(frame, url, cgiPath);
        win.setSize(400, 300);

        // �����ܺٲ��̤�ɽ��
        win.setVisible(true);

        // �������̼���
        cgiPath = win.getCgiPath();
        if (cgiPath == null) {
            // �������̤����򤵤�ʤ��ä�
            return;
        }

        // �������̤����
        SearchOrfList srchResWin = new SearchOrfList("Search results");

        // �������ɽ������
        int maxSegNum = dataMng.getMaxSegNum();
        int segNum = dataMng.getSegNum();
        int segIdx = segNum - 1;
        if (segNum == 0) {
            // segment �ǡ���ɽ���ΰ���ɲ�
            segIdx = 0;
            segNum++;
            dataMng.setSegNum(segNum);
            dataMng.setSegmentName(segIdx, dynSrchType);
        }
        else if (segNum < maxSegNum) {
            segIdx = segNum;
            segNum++;
            dataMng.setSegNum(segNum);
            dataMng.setSegmentName(segIdx, dynSrchType);
        }
        else if (segNum == maxSegNum) {
            segIdx = maxSegNum - 1;
            dataMng.setSegmentName(segIdx, dynSrchType);
        }

        // CGI ��¹Ԥ���������̤μ���
        String sp1 = dataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        dataMng.loadSegment0(dataMng.SIDE0, segIdx, null, cgiPath, url+cgiPath);
        Dbg.println(1, "DynamicSearch :: " + url+cgiPath + " :: " + sp1);

        //
        String sp2 = dataMng.getSpecName(MbgdDataMng.OPPO_SPEC);
        dataMng.loadSegment0(dataMng.SIDE1, segIdx, null, cgiPath, url+cgiPath);
        Dbg.println(1, "DynamicSearch :: " + url+cgiPath + " :: " + sp2);


//	((AlignmentViewerFrame) frame).setDrawings(true, false);
	((AlignmentViewerFrame) frame).setFrameSize(segNum);

        //
        viewWin.setDrawMode(viewWin.getDrawMode());

/*
        // �����̾������
        String colNam[] = (String[])(ar.get(0));
        for(int i = 0; i < colNam.length; i++) {
            srchResWin.addColumn(colNam[i]);
        }

        // TableHeader �Υ���å����٥�Ȥ���� ---> ����å����줿���ܤ� Sort ����
        SortTableCommand cmdSort = new SortTableCommand();
        srchResWin.addTableHeadAction(cmdSort);

        RegionListSelection rowSelect = new RegionListSelection(viewWin);
        srchResWin.addTableAction(rowSelect);

        // �ǡ���������
        int loopMax = ar.size();
        for(int i = 1; i < loopMax; i++) {
            Object obj[] = (Object [])ar.get(i);
            srchResWin.addRow(obj);
        }

        //
        srchResWin.setColoredCell(4);

        // ɽ��
        srchResWin.setVisible(true);
*/

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CGI �μ¹Է�̤��Ǽ
    //     ar[0] �ˤϡ�����ॿ���ȥ���Ǽ
    public ArrayList execDynSearch(String u) {
        ArrayList ar = new ArrayList();

        try {
            String obj[];
            UrlFile uFile = new UrlFile(u);

            String title[] = null;
            Object dat[];
            for(;;) {
                StringTokenizer st;
                int n;
                int i;
                String buf = uFile.readLine();
                if (buf == null) {
                    break;
                }

                buf = buf.trim();
                if (buf.equals("")) {
                    continue;
                }

                if (buf.startsWith("#")) {
                    if (title == null) {
                        // ���ܥ����ȥ���ɤ߹���
                        st = new StringTokenizer(buf.substring(1), "\t");
                        n = st.countTokens();
                        title = new String[n];
                        for(i = 0; i < n; i++) {
                            title[i] = st.nextToken();
                        }
                        ar.add(0, title);
                    }
                    continue;
                }

                // ��������ɥǡ���
                st = new StringTokenizer(buf, "\t");
                n = st.countTokens();
                dat = new Object[n];
                for(i = 0; i < 1; i++) {    // spec
                    dat[i] = st.nextToken();
                }
                for(; i < 3; i++) {         // from, to
                    dat[i] = new Integer(st.nextToken());
                }
                for(; i < 4; i++) {         // dir
                    dat[i] = st.nextToken();
                }
                for(; i < 5; i++) {         // color
                    String wk = st.nextToken();
                    int c = 0;
                    if (wk.startsWith("#")) {
                        c = Integer.parseInt(wk.substring(1), 16);
                    }

                    dat[i] = new Color(c);
                }
                for(; i < n; i++) {         // name, ...
                    dat[i] = st.nextToken();
                }
                ar.add(dat);
            }
        }
        catch (Exception e) {
            Dbg.println(1, "ERROR :: "+e);
        }

        return ar;
    }

}
