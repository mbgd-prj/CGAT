
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.print.*;

///////////////////////////////////////////////////////////////////////////////
// 画像表示データ群
public class DrawingSet extends JPanel implements Observer, Printable {
    protected MbgdDataMng mbgdDataMng;
    protected ViewWindow viewWin;

    static int drawingMode = 0;                    // 表示モード

    public JPanel              basePanel;

    protected JPanel              sp1NamePos;
    protected JLabel              sp1DpsRegFrom;
    protected JLabel              sp1DpsRegTo;

    protected BaseLabel           spec1Name;
    protected DrawRegionSet       sp1SegArea[];
    protected DrawRegionSet       sp1GeneArea;
    protected DrawAlignmentSet    alignmentArea;
    protected DrawRegionSet       sp2GeneArea;
    protected DrawRegionSet       sp2SegArea[];
    protected BaseLabel           spec2Name;


    protected JPanel              sp2NamePos;
    protected JLabel              sp2DpsRegFrom;
    protected JLabel              sp2DpsRegTo;

    // 表示順も設定可能としたい

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawingSet(MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        int hAlign = dataMng.getPropertyInt(MbgdDataMng.OPT_PANEL_ALIGN_H);
        int hGene  = dataMng.getPropertyInt(MbgdDataMng.OPT_PANEL_GENE_H);
        int hSeg   = dataMng.getPropertyInt(MbgdDataMng.OPT_PANEL_SEGMENT_H);
        int w = DrawRegion.WIDTH;

        _init(dataMng, vWin, w, hAlign, hGene, hSeg);
    }
    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawingSet(MbgdDataMng dataMng, ViewWindow vWin, int w, int hAlign, int hGene, int hSeg) {
        super();
        _init(dataMng, vWin, w, hAlign, hGene, hSeg);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void _init (MbgdDataMng dataMng, ViewWindow vWin, int w, int hAlign, int hGene, int hSeg) {
        mbgdDataMng = dataMng;
        viewWin = vWin;

        //
        int maxSegNum = 5;//mbgdDataMng.getMaxSegNum();

        // BasePanel
        basePanel = new JPanel();

        // Alignment
        alignmentArea   = new DrawAlignmentSet(dataMng, vWin, w, hAlign);

        // SpecName
        sp1NamePos = new JPanel(new GridLayout(2, 1));
        spec1Name = new BaseLabel("=== sequence-1 ===");
        sp1NamePos.add(spec1Name);

        // From/To for Spec1
        GridBagLayout gridbag = new GridBagLayout();
        JPanel panelSp1 = new JPanel(gridbag);
        sp1NamePos.add(panelSp1);
        sp1DpsRegFrom  = new JLabel("seq1 from");
        sp1DpsRegTo    = new JLabel("seq1 to");
        sp1DpsRegTo.setHorizontalAlignment(JLabel.RIGHT);

        // 
        GridBagConstraints gbcf  = new GridBagConstraints();
        gbcf.gridx = 1;
        gbcf.gridy = 1;
        gbcf.anchor = GridBagConstraints.WEST;
        gbcf.insets = new Insets(0, 100, 0, 0);
        panelSp1.add(sp1DpsRegFrom, gbcf);

        //
        GridBagConstraints gbct  = new GridBagConstraints();
        gbct.gridx = 2;
        gbct.gridy = 1;
        gbct.weightx = 1.0;
        gbct.anchor = GridBagConstraints.EAST;
        panelSp1.add(sp1DpsRegTo, gbct);

        //
        spec1Name.setVerticalAlignment(JLabel.BOTTOM);
        sp1DpsRegFrom.setVerticalAlignment(JLabel.BOTTOM);
        sp1DpsRegTo.setVerticalAlignment(JLabel.BOTTOM);

        sp1GeneArea = new DrawRegionSet(MbgdDataMng.BASE_GENE,
                                        dataMng, vWin, w, hGene);

        sp1GeneArea.setId("sp1GeneArea");
        sp1SegArea = new DrawRegionSet[maxSegNum];
        for(int i = 0; i < maxSegNum; i++) {
            sp1SegArea[i] = new DrawRegionSet(MbgdDataMng.BASE_SEG1 + i,
                                              dataMng, vWin, w, hSeg);

            sp1SegArea[i].setId("sp1SegArea" + String.valueOf(i + 1));
            sp1SegArea[i].setDataLabel("Segment" + String.valueOf(i + 1));
            sp1SegArea[i].setTextVisible(false);
        }

        // From/To for Spec2
        sp2NamePos = new JPanel(new GridLayout(2, 1));
        JPanel panelSp2 = new JPanel(new GridBagLayout());
        sp2NamePos.add(panelSp2);
        sp2DpsRegFrom  = new JLabel("seq2 from");
        sp2DpsRegTo    = new JLabel("seq2 to");
        sp2DpsRegTo.setHorizontalAlignment(JLabel.RIGHT);

        panelSp2.add(sp2DpsRegFrom, gbcf);
        panelSp2.add(sp2DpsRegTo  , gbct);

        spec2Name = new BaseLabel("=== sequence-2 ===");
        sp2NamePos.add(spec2Name);

        //
        sp2GeneArea = new DrawRegionSet(MbgdDataMng.OPPO_GENE,
                                        dataMng, vWin, w, hGene);
        sp2GeneArea.setId("sp2GeneArea");
        sp2SegArea = new DrawRegionSet[maxSegNum];
        for(int i = 0; i < maxSegNum; i++) {
            sp2SegArea[i] = new DrawRegionSet(MbgdDataMng.OPPO_SEG1 + i,
                                              dataMng, vWin, w, hSeg);
            sp2SegArea[i].setId("sp2SegArea" + String.valueOf(i + 1));
            sp2SegArea[i].setDataLabel("Segment" + String.valueOf(i + 1));
            sp2SegArea[i].setTextVisible(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getElementSize() {
        // 表示要素数を返す
        return 5 + mbgdDataMng.getMaxSegNum();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignmentSet getAlignmentArea() {
        return(alignmentArea);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel getSp1NamePosArea() {
        return(sp1NamePos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JLabel getSp1NameArea() {
        return(spec1Name);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel getSp2NamePosArea() {
        return(sp2NamePos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JLabel getSp2NameArea() {
        return(spec2Name);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionSet getSp1GeneArea() {
        return(sp1GeneArea);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionSet getSp2GeneArea() {
        return(sp2GeneArea);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionSet getSp1SegArea(int idx) {
        return sp1SegArea[idx];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawRegionSet getSp2SegArea(int idx) {
        return sp2SegArea[idx];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void exchangeDispSpec() {
        viewWin.exchangeBaseSpec();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel getDrawingPanel() {
        return basePanel;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDrawings(boolean isDispSegs[]) {
        int maxSegNum = mbgdDataMng.getMaxSegNum();

        // 表示 Item 数
        int numItem = 1 + 2 + 2;    // alignment + gene * 2 + name * 2;
        for(int i = 0; i < maxSegNum; i++) {
            if (isDispSegs[i]) {
                numItem += 2;           // + Segment
            }
        }

        //
        GridBagLayout gridbag = new GridBagLayout();
        basePanel.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        //
        basePanel.removeAll();

        //
        int rown =0;

        c.gridx = 0; c.gridy = rown++;
        c.gridheight = 1; c.gridwidth = GridBagConstraints.REMAINDER;
        basePanel.add(sp1NamePos, c);

        //
        for(int i = maxSegNum - 1; 0 <= i; i--) {
            if (isDispSegs[i]) {
                addRegion(rown++, sp1SegArea[i], c);
            }
        }

        // gene1
        addRegion(rown++,sp1GeneArea,c);

        // alignment
        addAlignment(rown++,alignmentArea,c);

        // gene2
        addRegion(rown++,sp2GeneArea,c);

        for(int i = 0; i < maxSegNum; i++) {
            if (isDispSegs[i]) {
                addRegion(rown++, sp2SegArea[i], c);
            }
        }

        //
        c.gridx = 0; c.gridy = rown++;
        c.gridheight = 1; c.gridwidth = GridBagConstraints.REMAINDER;
        basePanel.add(sp2NamePos, c);

        basePanel.validate();

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDrawings(boolean isDispSeg) {
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        boolean isDispSegList[] = new boolean[maxSegNum];

        for(int i = 0; i < maxSegNum; i++) {
            isDispSegList[i] = isDispSeg;
        }

        setDrawings(isDispSegList);
    }


    protected void addRegion(int rown, DrawRegionSet s, GridBagConstraints c){
	c.insets = new Insets(4,4,4,4);
//	c.ipadx = BaseDraw.XPAD; c.ipady = BaseDraw.YPAD;

	c.gridy = rown;
	c.gridheight = 1; c.gridwidth = 1;
	c.weightx = 0.0;
	basePanel.add(s.header,c);

	c.gridy = rown;
	c.weightx = 1.0;
	c.gridx = GridBagConstraints.RELATIVE;
	c.gridheight = 1; c.gridwidth = 1;

	basePanel.add(s.drawRegion,c);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void addAlignment(int rown, DrawAlignmentSet s, GridBagConstraints c){
        c.insets = new Insets(4,4,4,4);

        c.gridx = 0; c.gridy = GridBagConstraints.RELATIVE;
        c.gridheight = 1; c.gridwidth = 1;
        c.weightx = 0.0;
        basePanel.add(s.header,c);
        c.gridy = rown; c.weightx = 1.0;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridheight = 1; c.gridwidth = GridBagConstraints.RELATIVE;
        basePanel.add(s.drawAlignment,c);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWindowHeight(int hAlign, int hGene, int hSeg) {
        alignmentArea.setWindowHeight(hAlign);

        sp1GeneArea.setWindowHeight(hGene);
        sp2GeneArea.setWindowHeight(hGene);

        int maxSegNum = mbgdDataMng.getMaxSegNum();
        for(int i = maxSegNum - 1; 0 <= i; i--) {
            sp1SegArea[i].setWindowHeight(hSeg);
            sp2SegArea[i].setWindowHeight(hSeg);
        }
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
        else if (o instanceof AlignmentSegment) {
            update((AlignmentSegment)o, arg);
        }
        else if (o instanceof SelectSpecPairCommand) {
            update((SelectSpecPairCommand)o, arg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(ViewWindow vWin, Object arg) {
        int center1, start1, width1, max1;
        int from1, to1;
        boolean dir1;
        int center2, start2, width2, max2;
        int from2, to2;
        boolean dir2;

        int drawMode = vWin.getDrawMode();
        if (drawMode == ViewWindowRegion.MODE_SEQUENCE) {
            alignmentArea.setDirButtonEnabled(true);
        }
        else if (drawMode == ViewWindowRegion.MODE_SEGMENT) {
            alignmentArea.setDirButtonEnabled(false);
        }

        // spec の名称
        String sp1Name = mbgdDataMng.getSpecFullName(MbgdDataMng.BASE_SPEC);
        String sp2Name = mbgdDataMng.getSpecFullName(MbgdDataMng.OPPO_SPEC);
        spec1Name.setText(sp1Name);
        spec2Name.setText(sp2Name);

        // Segment の名称
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        String segName;
        for(int i = 0; i < maxSegNum; i++) {
            segName = mbgdDataMng.getSegmentName(i);
            segName = segName.replaceAll("\\(Server\\)", "");
            sp1SegArea[i].setDataLabel(segName);
            sp2SegArea[i].setDataLabel(segName);
        }

        // 表示範囲
        int zoomCount = vWin.getZoomCount();
        center1 = vWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        width1  = vWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        start1  = center1 - width1 / 2;
        max1    = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        dir1    = vWin.getRegDir(MbgdDataMng.BASE_SPEC);
        from1 = center1 - width1 / 2;
        to1   = center1 + width1 / 2;
        if (from1 < 0) {
            from1 += max1;
        }

        center2 = vWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
        width2  = vWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        start2  = center2 - width1 / 2;
        max2    = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        dir2    = vWin.getRegDir(MbgdDataMng.OPPO_SPEC);
        from2 = center2 - width2 / 2;
        to2   = center2 + width2 / 2;
        if (from2 < 0) {
            from2 += max2;
        }

        // DIR ボタン
        alignmentArea.setRegDir();

        // Region 画面の Gene 部に表示する [Region 中心位置]
        sp1GeneArea.setRegText(center1);
        sp2GeneArea.setRegText(center2);

        // Region 画面の 上段/下段 に表示する [Region 位置]
        if (dir1) {
            sp1DpsRegFrom.setText(String.valueOf(from1 + 1));
            sp1DpsRegTo.setText(String.valueOf(to1));
        }
        else {
            sp1DpsRegFrom.setText(String.valueOf(to1));
            sp1DpsRegTo.setText(String.valueOf(from1 + 1));
        }

        if (viewWin.getDrawMode() != ViewWindowRegion.MODE_SEGMENT) {
            if (dir2) {
                sp2DpsRegFrom.setText(String.valueOf(from2 + 1));
                sp2DpsRegTo.setText(String.valueOf(to2));
            }
            else {
                sp2DpsRegFrom.setText(String.valueOf(to2));
                sp2DpsRegTo.setText(String.valueOf(from2 + 1));
            }
        }
        else {
            // Segment で表示中は、From2/To2 に意味がないので表示しない
            sp2DpsRegFrom.setText("");
            sp2DpsRegTo.setText("");
        }

        spec1Name.repaint();
        spec2Name.repaint();

        if (viewWin.getDrawMode() == ViewWindowRegion.MODE_SEGMENT) {
            // セグメントレベルでの描画

            // 描画対象の Alignment データを選択
            // 画面上の描画位置を計算
            viewWin.makeAlignmentSegmentMode();
        }

        for(int i = 0; i < maxSegNum; i++) {
            sp1SegArea[i].repaint();
        }
        sp1GeneArea.repaint();
        alignmentArea.header.repaint();
        alignmentArea.drawAlignment.repaint();
        sp2GeneArea.repaint();
        for(int i = 0; i < maxSegNum; i++) {
            sp2SegArea[i].repaint();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(MbgdDataMng dataMng, Object arg) {
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        for(int i = 0; i < maxSegNum; i++) {
            String segName = dataMng.getSegmentName(i);
            sp1SegArea[i].setDataLabel(segName);
            sp2SegArea[i].setDataLabel(segName);
        }

        alignmentArea.setFilter(mbgdDataMng.isFilterAlignment());
        sp1GeneArea.setFilter(mbgdDataMng.isFilterGene());
        sp2GeneArea.setFilter(mbgdDataMng.isFilterGene());
        for(int i = 0; i < maxSegNum; i++) {
            sp1SegArea[i].setFilter(mbgdDataMng.isFilterSegment(i));
            sp2SegArea[i].setFilter(mbgdDataMng.isFilterSegment(i));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(AlignmentSegment alignSegment, Object arg) {
        //

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(SelectSpecPairCommand selSpPairCmd, Object arg) {
        int maxSegNum = mbgdDataMng.getMaxSegNum();
        boolean isDispSegs[] = new boolean[maxSegNum];

        //
        for(int i = 0; i < maxSegNum; i++) {
            isDispSegs[i] = selSpPairCmd.isDispSegs(i);
        }

        setDrawings(isDispSegs);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int print(Graphics g, PageFormat fmt, int pageIdx) {
	if(pageIdx > 0) {
		return Printable.NO_SUCH_PAGE;
	}
	Graphics2D g2 = (Graphics2D) g;
	g2.translate(fmt.getImageableX(), fmt.getImageableY());
	double pageWidth = fmt.getImageableWidth();
	double pageHeight = fmt.getImageableHeight();
	Dimension size = basePanel.getSize();
	double scale = 1.0;

	if(size.width > pageWidth) {
		scale = pageWidth / size.width;
	}
	if(pageHeight / size.height < scale) {
		scale = pageHeight / size.height;
	}
	g2.scale(scale,scale);
	pageWidth /= scale; pageHeight /= scale;

	g2.translate((pageWidth - size.width) / 2,
			(pageHeight - size.height)/2);
	basePanel.printAll(g2);
	return Printable.PAGE_EXISTS;
    }
}
