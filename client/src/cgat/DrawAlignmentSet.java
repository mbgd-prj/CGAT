
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// �ѥͥ�ˡ�Alignment �˴ؤ��� GUI �����ʤ����֤���
public class DrawAlignmentSet {
    static int WIDTH  = DrawAlignment.WIDTH + 100;
    static int HEIGHT = DrawAlignment.HEIGHT;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow viewWin;

    private JButton sp1DirButton;
    private JButton sp2DirButton;
    private JTextField sp1RegionText;
    private JTextField sp2RegionText;

    JPanel header;
    JLabel lblFilter;
    int headerWidth = 80;
    int headerHeight = 50;
    int drawWidth = BaseDraw.WIDTH;
    DrawAlignment drawAlignment;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignmentSet(MbgdDataMng dataMng, ViewWindow vWin, int w, int h) {
        _init(dataMng, vWin, w, h);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(MbgdDataMng dataMng, ViewWindow vWin, int w, int h) {
        int gridX;
        int gridY;

	int winHeight = BaseDraw.HEIGHT;

	Box button_box = Box.createVerticalBox();

        mbgdDataMng = dataMng;
        viewWin = vWin;

	header= new JPanel(new BorderLayout());
        header.setLayout(new BorderLayout());
//	header.setSize(headerWidth, winHeight);
//        header.setPreferredSize(new Dimension(headerWidth, winHeight));
//        header.setMinimumSize(new Dimension(headerWidth, headerHeight));

        // DIR �ܥ��󥯥�å����ν���
        DirButtonCommand cmdBase = new DirButtonCommand(MbgdDataMng.BASE_SPEC, vWin);
        DirButtonCommand cmdOppo = new DirButtonCommand(MbgdDataMng.OPPO_SPEC, vWin);

        // DIR �ܥ���
        sp1DirButton = new JButton("+");
        button_box.add(sp1DirButton);

        sp2DirButton = new JButton("+");
        button_box.add(sp2DirButton);
        header.add(button_box, BorderLayout.CENTER);

        lblFilter = new JLabel(" ");
        lblFilter.setMinimumSize(new Dimension(10,50));
        header.add(lblFilter, BorderLayout.EAST);

        // Region �����ΰ�
        drawAlignment = new DrawAlignmentCgat(mbgdDataMng, viewWin, w, h);

        // DIR �ܥ��󥯥�å����ν���
        sp1DirButton.addActionListener(cmdBase);
        sp2DirButton.addActionListener(cmdOppo);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignment getDrawAlignment() {
        return(drawAlignment);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowWidth(int w) {
        WIDTH = w - 100;
        drawAlignment.setWindowWidth(WIDTH);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowHeight(int h) {
        Dimension d = drawAlignment.getSize();
        d.height = h;

        drawAlignment.setSize(d);
        drawAlignment.setPreferredSize(d);
        drawAlignment.setMinimumSize(d);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegDir() {
        boolean dir;

        //
        dir = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        if (dir) {
            sp1DirButton.setText("+");
        }
        else {
            sp1DirButton.setText("-");
        }

        //
        dir = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        if (dir) {
            sp2DirButton.setText("+");
        }
        else {
            sp2DirButton.setText("-");
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDirButtonEnabled(boolean sta) {
        sp2DirButton.setEnabled(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFilter(boolean sta) {
        if (sta) {
            lblFilter.setText("*");
        }
        else {
            lblFilter.setText("");
        }
    }

}
