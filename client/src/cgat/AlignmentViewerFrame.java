
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

///////////////////////////////////////////////////////////////////////////////
//
public class AlignmentViewerFrame extends BaseFrame implements Observer, ActionListener {
    static int WIDTH_SCALE = DotPlot.WIDTH;
    static int HEIGHT_SCALE = 80;
    static int WIDTH_ABOUT  = 600;
    static int HEIGHT_ABOUT = 250;

    protected ViewWindow viewWin;                     // Region/DotPlot 描画領域

    protected DrawingSet  drawingSet;

//    protected ConnectServerCommand connectServerListener;         // Server 選択
    protected SelectSpecPairCommand selectSpecListener;           // 生物種選択
    protected ExitCommand exitListener;                           // システム終了
    protected PrintPageCommand printAlignmentArea;                 // 印刷
    protected PrintPageCommand printDotPlotArea;                 // 印刷
    protected PropertiesCommand propertiesListener;
    protected DispSegmentDataTable dispSegDataTab;                // Segment data table 表示
    protected DispRawSequencePairCommand dispRawSeqListener;      // 配列表示
    protected SearchOrfCommand searchOrf;                         // ORF 検索
    protected AlignmentFilterCommand filterAlignment;             // Alignment データフィルタリング
    protected SegmentFilterCommand filterSegment;                 // Segment データフィルタリング
    protected DynamicSearchCommand dynSearch;                     // CGI による検索
    protected JPanel panelBase;
    protected JPanel panelRegion;
//    protected JPanel panelDotPlot;
    protected PanelPrintable panelDotPlot;
    protected DotPlot dotPlot;
    protected DrawScale xscale;
    protected DrawScale yscale;

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentViewerFrame(String sp1, String sp2, String prog) {
        super(200, 100);

        // CGAT のデータ領域
        MbgdDataMng dataMng = MbgdDataMng.Instance();

        // 描画領域に関する情報
        viewWin = ViewWindow.Instance(dataMng);
        viewWin.setDrawMode(ViewWindowRegion.MODE_SEQUENCE);

        panelBase = new JPanel();
        pane.add(panelBase);

        // Alignment + Region + DotPlot
        createDrawingArea(dataMng, viewWin);

        // MENU
        createMenu(dataMng, viewWin, sp1, sp2, prog);

        // TOOLBAR
        createToolbar(dataMng, viewWin);

/* to be moved to ViewWindow */
        // アライメント結果表示モード
        AlignmentSequence alignSequence = new AlignmentSequence(dataMng, viewWin);
        AlignmentSegment  alignSegment  = new AlignmentSegment(dataMng, viewWin);
        viewWin.setAlignmentInfo(alignSequence, alignSegment);
/**/

        ///////////////////////////////////////////////////////////////////////
        // set Observer Pattern（データの依存関係）

        // 表示データ選択時
        selectSpecListener.addObserver(drawingSet);     // Segs の表示/非表示を調整
        selectSpecListener.addObserver(this);

        // データロード時
        dataMng.addObserver(this);
        dataMng.addObserver(viewWin);                   // 描画領域を初期化
        dataMng.addObserver(drawingSet);                // Segment name

        // 描画領域が変更された場合、Alignment/Gene/Segment を描画し直す
        viewWin.addObserver(drawingSet);

        // 描画領域が変更された場合、DotPlot を描画し直す
        viewWin.addObserver(dotPlot);
        viewWin.addObserver(xscale);
        viewWin.addObserver(yscale);

        // セグメントレベルの表示
        alignSegment.addObserver(drawingSet);

        // Search ORF 処理時
        searchOrf.addObserver(xscale);
        searchOrf.addObserver(yscale);
        setSize(1000,10);

        int segNum = dataMng.getSegNum();
        setFrameSize(segNum);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void createMenu(MbgdDataMng dataMng, ViewWindow viewWin, String sp1, String sp2, String prog) {
        SelectSpecPairDialog selSpDiglog;


        // 生物種選択
//        connectServerListener = new ConnectServerCommand(this);
        selectSpecListener = new SelectSpecPairCommand(this, dataMng);
        selSpDiglog = SelectSpecPairDialog.Instance(this);
        selSpDiglog.setDefault(sp1, sp2, prog);
        selSpDiglog._update();
        

        // システム終了
        exitListener = new ExitCommand();

        // 印刷
        printAlignmentArea = new PrintPageCommand(drawingSet);
        printDotPlotArea = new PrintPageCommand(panelDotPlot);

        //
        propertiesListener = new PropertiesCommand(this, dataMng);

        // Gene/Segment data table
        dispSegDataTab = new DispSegmentDataTable(this, dataMng, viewWin);

        // 配列表示
        dispRawSeqListener = new DispRawSequencePairCommand(dataMng, viewWin);

        // ORF 検索
        searchOrf = new SearchOrfCommand(this, dataMng, viewWin);

        // Alignment データフィルタリング
        filterAlignment = new AlignmentFilterCommand(this, dataMng);

        // Segment データフィルタリング
        filterSegment = new SegmentFilterCommand(this, dataMng);

        // CGI による検索
        dynSearch = new DynamicSearchCommand(this, dataMng, viewWin);

        //
        String url;

        //
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menu, subMenu;
        JMenuItem item;

        menu = new JMenu("File");
        menuBar.add(menu);

/*
        if (! MbgdData.Instance().isApplet()) {
            item = new JMenuItem("Connect");
            item.addActionListener(connectServerListener);
            menu.add(item);
        }
*/

        item = new JMenuItem("Load");
        item.addActionListener(selectSpecListener);
        menu.add(item);


        item = new JMenuItem("Print");
        item.addActionListener(printAlignmentArea);
        menu.add(item);

/*
        subMenu = new JMenu("Print");
        menu.add(subMenu);
        item = new JMenuItem("Alignment Display Panel");
        item.addActionListener(printAlignmentArea);
        subMenu.add(item);

        item = new JMenuItem("Dotplot Display Panel");
        item.addActionListener(printDotPlotArea);
        subMenu.add(item);
*/


        item = new JMenuItem("Properties");
        item.addActionListener(propertiesListener);
        menu.add(item);

	if (! MbgdData.Instance().isApplet()) {
	        item = new JMenuItem("Exit");
       		item.addActionListener(new java.awt.event.ActionListener() {
       	     	    public void actionPerformed(java.awt.event.ActionEvent evt) {
               		exitMenuItemActionPerformed(evt);
            	    }
		});
	}
        menu.add(item);

        //
        menu = new JMenu("Search");
        menuBar.add(menu);

        item = new JMenuItem("Mark Genes");
        item.addActionListener(searchOrf);
        menu.add(item);

        item = new JMenuItem("Filter Alignment");
        item.addActionListener(filterAlignment);
        menu.add(item);

        item = new JMenuItem("Filter Gene/Segment");
        item.addActionListener(filterSegment);
        menu.add(item);

        item = new JMenuItem("Dynamic Search");
        item.addActionListener(dynSearch);
        menu.add(item);

        //
        menu = new JMenu("View");
        menuBar.add(menu);

        item = new JMenuItem("Gene/Segment Data Table");
        item.addActionListener(dispSegDataTab);
        menu.add(item);

        item = new JMenuItem("Sequence Window");
        item.addActionListener(dispRawSeqListener);
        menu.add(item);

        item = new JMenuItem("Exchange Reference Genome");
        item.addActionListener(dataMng);
        menu.add(item);

        //
        menu = new JMenu("Help");
        menuBar.add(menu);

        item = new JMenuItem("Manual");
        url = dataMng.getProperty(MbgdDataMng.OPT_URL_MANUAL);
        item.addActionListener(new ExecBrowser(url));
        menu.add(item);

        item = new JMenuItem("Website");
        url = dataMng.getProperty(MbgdDataMng.OPT_URL_WEBSITE);
        item.addActionListener(new ExecBrowser(url));
        menu.add(item);

        item = new JMenuItem("About CGAT");
        item.addActionListener(this);

        menu.add(item);

    }
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit( 0 );
    }


    ///////////////////////////////////////////////////////////////////////////
    // MenuBar の表示を制御する（表示/非表示）
    public void setVisibleMenuBar(boolean sta) {
        JMenuBar mb = getJMenuBar();
        if (mb != null) {
            mb.setVisible(sta);
	    }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void createToolbar(MbgdDataMng dataMng, ViewWindow viewWin) {
        JToggleButton tb;
        RotateButton btnColor, btnAlign;
        String name, tooltip;
        URL u;
        Insets is0 = new Insets(0, 0, 0, 0);

        // Region を Left/Right
        ViewLeftCommand     viewLeft     = new ViewLeftCommand(viewWin);
        ViewRightCommand    viewRight    = new ViewRightCommand(viewWin);

        // Region を拡大/縮小
        ViewZoomUpCommand   viewZoomUp   = new ViewZoomUpCommand(viewWin);
        ViewZoomDownCommand viewZoomDown = new ViewZoomDownCommand(viewWin);

        // DotPlot を拡大/縮小
        DotPlotZoomUpCommand   dotPlotZoomUp   = new DotPlotZoomUpCommand(viewWin);
        DotPlotZoomDownCommand dotPlotZoomDown = new DotPlotZoomDownCommand(viewWin);

        // ボタンを押し続けたときの処理
        ButtonAcceleration viewLeftBtnAct  = new ButtonAcceleration(1000, viewLeft);
        ButtonAcceleration viewRightBtnAct = new ButtonAcceleration(1000, viewRight);

        //
        toolBar = new JToolBar();
        pane.add(toolBar, BorderLayout.NORTH);

        String f = "";

        String basePath = dataMng.getBasePath();
        String docBase  = dataMng.getDocBase();

        try {
            JButton b;

        name = "image/ArrowLeft.jpeg";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_LEFT);
	    b = iconButton(name, is0);
            b.addMouseListener(viewLeftBtnAct);
            toolBar.add(b);
        b.setToolTipText(tooltip);

        name = "image/ArrowRight.jpeg";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_RIGHT);
	    b = iconButton(name, is0);
            b.addMouseListener(viewRightBtnAct);
            toolBar.add(b);
        b.setToolTipText(tooltip);

        name = "image/RegionZoomUp.jpeg";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_REG_UP);
	    b = iconButton(name, is0);
            b.addActionListener(viewZoomUp);
            toolBar.add(b);
        b.setToolTipText(tooltip);

        name = "image/RegionZoomDown.jpeg";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_REG_DOWN);
	    b = iconButton(name, is0);
            b.addActionListener(viewZoomDown);
            toolBar.add(b);
        b.setToolTipText(tooltip);

	} catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Icon File Not Found.\n" + "File : " + f + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance(this);
            msgDialog.message(msg);
        }

		// 表示色（アライメント種別 or アライメント一致度
        JPanel pCol = new JPanel(new GridLayout(2, 1));
        pCol.setPreferredSize(null);
        JLabel lCol = new JLabel("Alignment Color");
        lCol.setPreferredSize(null);
        lCol.setFont(new Font("Serif", Font.PLAIN, 12));
        pCol.add(lCol);
        pCol.setMaximumSize(new Dimension(120, 40));
        btnColor = new RotateButton();
		btnColor.addLabel("Best Hit");
		btnColor.addLabel("Identity");
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_COLOR);
		//
		ActionListener alBtnColor = new ChangeAlignmentColorCommand(dataMng, btnColor);
		btnColor.addActionListener(alBtnColor);
        btnColor.setToolTipText(tooltip);
        pCol.add(btnColor);
        toolBar.add(pCol);


        // 表示モード（配列 or セグメント）
        JPanel pAlign = new JPanel(new GridLayout(2, 1));
        pAlign.setPreferredSize(null);
        JLabel lAlign = new JLabel("Alignment Mode");
        lAlign.setPreferredSize(null);
        lAlign.setFont(new Font("Serif", Font.PLAIN, 12));
        pAlign.add(lAlign);
        pAlign.setMaximumSize(new Dimension(120, 40));
        btnAlign = new RotateButton();
		btnAlign.addLabel("Regionwise");
		btnAlign.addLabel("Ref-Target");
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_ALIGN);
        btnAlign.addActionListener(new SelectAlignmentMode(viewWin, drawingSet, btnAlign));
        btnAlign.setToolTipText(tooltip);
pAlign.add(btnAlign);
        toolBar.add(pAlign);

        try {
            JButton b;
        name = "image/Exchange24.gif";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_EXCHANGE);
	    b = iconButton(name, null);
            b.addActionListener(dataMng);
            toolBar.add(b);
        b.setToolTipText(tooltip);


            toolBar.add(new JToolBar.Separator());

        //
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_DOTPLOT);
	    tb = new JToggleButton("DotPlot", true);
            tb.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			panelDotPlot.setVisible(
				e.getStateChange() == ItemEvent.SELECTED);
		}
	    });
        tb.setToolTipText(tooltip);
            toolBar.add(tb);

		// DotPlot
        name = "image/DotPlotZoomUp.jpeg";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_DP_UP);
	    b = iconButton(name, is0);
            b.addActionListener(dotPlotZoomUp);
            toolBar.add(b);
        b.setToolTipText(tooltip);

        name = "image/DotPlotZoomDown.jpeg";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_DP_DOWN);
	    b = iconButton(name, is0);
            b.addActionListener(dotPlotZoomDown);
            toolBar.add(b);
        b.setToolTipText(tooltip);

        } catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Icon File Not Found.\n" + "File : " + f + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);
        }


        //
        name = "image/DotPlotX24.gif";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_DP_X);
        u = this.getClass().getClassLoader().getResource(name);
        tb = new JToggleButton(new ImageIcon(u), true);
        tb.addActionListener(new DotPlotZoomTarget(viewWin,
                                                   DotPlotZoomTarget.X));
        tb.setToolTipText(tooltip);
        toolBar.add(tb);

        name = "image/DotPlotY24.gif";
        tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_DP_Y);
        u = this.getClass().getClassLoader().getResource(name);
        tb = new JToggleButton(new ImageIcon(u), true);
        tb.addActionListener(new DotPlotZoomTarget(viewWin,
                                                   DotPlotZoomTarget.Y));
        tb.setToolTipText(tooltip);
        toolBar.add(tb);


        try {
            JButton b = null;

            // Change BG color
            tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_COLOR_BG);
            RotateButton btnBw = new RotateButton();
            name = "image/DotPlotBw24.gif";
            URL u1 = getClass().getClassLoader().getResource(name);
            btnBw.addIcon(new ImageIcon(u1));
            name = "image/DotPlotWb24.gif";
            URL u2 = getClass().getClassLoader().getResource(name);
            btnBw.addIcon(new ImageIcon(u2));
            btnBw.addActionListener(new ChangeCgatColorCommand(dataMng, btnBw));
            btnBw.setToolTipText(tooltip);
            toolBar.add(btnBw);

            // Gene/Segment table
            name = "image/RegionTable24.gif";
            tooltip = dataMng.getProperty(MbgdDataMng.OPT_TT_GENE_TAB);
            b = iconButton(name, null);
            b.addActionListener(dispSegDataTab);
            b.setToolTipText(tooltip);
            toolBar.add(b);


        } catch (Exception e) {
            // エラーメッセージ表示
            String msg;
            msg = "Icon File Not Found.\n" + "File : " + f + "\n";
            BaseMessageDialog msgDialog = BaseMessageDialog.Instance();
            msgDialog.message(msg);
        }

    }
    private JButton iconButton(String name, Insets is){
		// icon images are loaded from jar file
            URL u = this.getClass().getClassLoader().getResource(name);
            ImageIcon i = new ImageIcon(u);
            JButton b = new JButton(i);
            if (is != null) {
                b.setMargin(is);    //
            }
            b.setRolloverEnabled(true);
	    return b;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void createDrawingArea(MbgdDataMng dataMng, ViewWindow viewWin) {
        // GridBagLayout
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c;//  = new GridBagConstraints();
        panelBase.setLayout(gridbag);

        c  = new GridBagConstraints();
        c.weightx = 1.0;
        c.gridx = 0; c.gridy = 0;
        c.ipadx = 800;
        c.ipady = DrawAlignmentSet.HEIGHT;
        c.gridwidth  = 1;
        c.gridheight = 1;

        JPanel panelAlignArea = createRegionArea(dataMng, viewWin);
        gridbag.setConstraints(panelAlignArea, c);
        panelBase.add(panelAlignArea);

        c  = new GridBagConstraints();
        c.weightx = 0.0;
        c.gridx = 1; c.gridy = 0;
        c.ipadx = DotPlot.WIDTH + HEIGHT_SCALE + 10;
        c.ipady = DotPlot.HEIGHT + HEIGHT_SCALE;
        c.gridwidth  = 1;
        c.gridheight = 1;
        JPanel panelDotPlotArea = createDotPlotArea(dataMng, viewWin);
        gridbag.setConstraints(panelDotPlotArea, c);
        panelBase.add(panelDotPlotArea);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createRegionArea(MbgdDataMng dataMng, ViewWindow viewWin) {
        //
        drawingSet = new DrawingSet(dataMng, viewWin);
        panelRegion = drawingSet.getDrawingPanel();

        drawingSet.setDrawings(false);

        return(panelRegion);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createDotPlotArea(MbgdDataMng dataMng, ViewWindow viewWin) {
        panelDotPlot = new PanelPrintable();
        panelDotPlot.setLayout(null);

        // DotPlot
        dotPlot = new DotPlot(dataMng, viewWin);

        // X-scale
        xscale = new DrawScale(dataMng, "");
        xscale.setScaleType(DrawScale.HORIZONTAL);
        xscale.setScaleSize(WIDTH_SCALE, HEIGHT_SCALE);

        // Y-scale
        yscale = new DrawScale(dataMng, "");
        yscale.setScaleType(DrawScale.VERTICAL);
        yscale.setScaleSize(HEIGHT_SCALE, WIDTH_SCALE);

        // パネルに配置(DotPlot)
        panelDotPlot.add(dotPlot);
        dotPlot.setLocation(HEIGHT_SCALE, 0);

        // パネルに配置(X-scale)
        panelDotPlot.add(xscale, BorderLayout.WEST);
        xscale.setLocation(HEIGHT_SCALE, WIDTH_SCALE);

        // パネルに配置(Y-scale)
        panelDotPlot.add(yscale, BorderLayout.SOUTH);
        yscale.setLocation(0, 0);

//panelDotPlot.setMinimumSize(new Dimension(WIDTH_SCALE+HEIGHT_SCALE, WIDTH_SCALE+HEIGHT_SCALE));
panelDotPlot.validate();

        return panelDotPlot;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void update(Observable o, Object arg) {
        int segNum = MbgdDataMng.Instance().getSegNum();

        setFrameSize(segNum);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrameSize(int segN) {
        MbgdDataMng mbgdMng = MbgdDataMng.Instance();
        int height = 0;

        //
        int hAlign = mbgdMng.getPropertyInt(MbgdDataMng.OPT_PANEL_ALIGN_H);
        int hGene = mbgdMng.getPropertyInt(MbgdDataMng.OPT_PANEL_GENE_H);
        int hSeg = mbgdMng.getPropertyInt(MbgdDataMng.OPT_PANEL_SEGMENT_H);
        height += hAlign + hGene * 2 + (hSeg + 5) * segN * 2;

        //
        drawingSet.setWindowHeight(hAlign, hGene, hSeg);

        // Menu/name/pos 分のサイズを加算
        height += 270;

        if (height < 600) {
            // DotPlot を表示するために必要なサイズ
            height = 600;
        }

        int maxSegNum = mbgdMng.getMaxSegNum();
        boolean staSeg[] = new boolean[maxSegNum];
        for(int i = 0; i < maxSegNum; i++) {
            staSeg[i] = false;
            if (i < segN) {
                staSeg[i] = true;
            }
        }
        drawingSet.setDrawings(staSeg);

        setSize(getWidth(), height);
        validate(); // 画面サイズが変更になったため、GUIを再配置
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        StringBuffer txtAboutCgat = new StringBuffer();
try {
        InputStream is = getClass().getClassLoader().getResourceAsStream("about_CGAT.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        for(;;) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            if (txtAboutCgat.length() != 0) {
                txtAboutCgat.append("\n");
            }
            txtAboutCgat.append(line);
        }
}
catch (Exception e2) {
}

        //
        JTextArea doc = new JTextArea(txtAboutCgat.toString());
        Dimension d = doc.getPreferredSize();
        if (WIDTH_ABOUT < d.getWidth()) {
            d.setSize(WIDTH_ABOUT, d.getHeight());
        }
        if (HEIGHT_ABOUT < d.getHeight()) {
            d.setSize(d.getWidth(), HEIGHT_ABOUT);
        }
        JViewport viewport = new JViewport();
        viewport.setView(doc);
        viewport.setViewSize(d);
        viewport.setPreferredSize(d);
        viewport.setSize(d);

        doc.setEditable(false);
        JScrollPane pane = new JScrollPane();
        pane.setViewport(viewport);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(pane, BorderLayout.CENTER);

        //
        JOptionPane opane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = opane.createDialog(this, "About CGAT");
        dialog.show();
    }

}

