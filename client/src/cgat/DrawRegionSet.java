
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
// パネルに、Region に関する GUI の部品を配置する
public class DrawRegionSet {
    private String id = "";

    private int         dataType;
    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    private LabelMultiLine regionLabel;
    private RegionText regionText;
    private InputRegionCommand inputRegCommand;

    JPanel header;
    JLabel lblFilter;
    DrawRegion drawRegion;
    int headerWidth = 80;
    int headerHeight = 50;
    int drawWidth = BaseDraw.WIDTH;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionSet(   int type,
                            MbgdDataMng dataMng, ViewWindow vWin) {
        _init(type, dataMng, vWin, BaseDraw.WIDTH, BaseDraw.HEIGHT);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionSet(   int type,
                            MbgdDataMng dataMng, ViewWindow vWin,
                            int w, int h) {
        _init(type, dataMng, vWin, w, h);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setId(String i) {
        id = i;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(  int type,
                        MbgdDataMng dataMng, ViewWindow vWin,
                        int w, int h) {
        int gridX;
        int gridY;
        int winHeight = h; //BaseDraw.HEIGHT;

        setDataType(type);
        setMbgdDataMng(dataMng);
        setViewWindow(vWin);

        header= new JPanel();
        header.setSize(headerWidth, winHeight);
        header.setPreferredSize(new Dimension(headerWidth, winHeight));
        header.setMinimumSize(new Dimension(headerWidth, headerHeight));


/*
        regionLabel = new LabelMultiLine("");
        regionText = new RegionText(null, 6);
        header.add(regionLabel);
        header.add(regionText);
*/

        regionLabel = new LabelMultiLine("");
        regionText = new RegionText(null, 6);
        header.setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(regionLabel);
        p.add(regionText);
        header.add(p, BorderLayout.CENTER);

        lblFilter = new JLabel(" ");
        lblFilter.setMinimumSize(new Dimension(10,50));
        header.add(lblFilter, BorderLayout.EAST);

        drawRegion = new DrawRegionCgat(type, dataMng, vWin, w);
        drawRegion.setPreferredSize(new Dimension(w, winHeight));
        drawRegion.setMinimumSize(new Dimension(w, winHeight));

        inputRegCommand = new InputRegionCommand(
		getDataType(), getMbgdDataMng(), getViewWindow());
        regionText.addActionListener(inputRegCommand);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataType(int type) {
        dataType = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDataType() {
        return dataType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMbgdDataMng(MbgdDataMng dataMng) {
        mbgdDataMng = dataMng;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public MbgdDataMng getMbgdDataMng() {
        return mbgdDataMng;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setViewWindow(ViewWindow vWin) {
        viewWin = vWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ViewWindow getViewWindow() {
        return viewWin;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegText(int pos) {
        regionText.setText(String.valueOf(pos));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setVisible(boolean sta) {
        setTextVisible(sta);
        setRegionVisible(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTextVisible(boolean sta) {
        regionText.setVisible(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegionVisible(boolean sta) {
        drawRegion.setVisible(sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDataLabel(String name) {
        String delim = " ";
        regionLabel.setText(name, delim);
        regionLabel.repaint();              // 内容が更新されたので再表示
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void repaint() {
        header.repaint();
        drawRegion.repaint();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowHeight(int h) {
        Dimension d = drawRegion.getSize();
        d.height = h;

        drawRegion.setSize(d);
        drawRegion.setPreferredSize(d);
        drawRegion.setMinimumSize(d);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFilter(boolean sta) {
        if (sta) {
            lblFilter.setText("*");
        }
        else {
            lblFilter.setText(" ");
        }
    }

}
