package cgat;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

///////////////////////////////////////////////////////////////////////////////
//
public class PropertyDialog {
    protected Properties prop;
    protected boolean staSelect = false;

    protected JFrame frame;
    protected JTabbedPane tabPane;

    public final String TXT_RESET_BTN = "Use Default Values";
    public final int PANEL_LOCATION  = 0;
    public final int PANEL_COLOR     = 1;
    public final int PANEL_PANEL     = 2;
    public final int PANEL_ALIGNMENT = 3;

    public final String[][] lstPanelPropKey = {
        //
        { MbgdDataMng.OPT_DIR_HOME,
          MbgdDataMng.OPT_URL_PROXY,
          MbgdDataMng.OPT_CMD_BROWSER_USER
        },

        //
        { MbgdDataMng.OPT_BG_DARK,
          MbgdDataMng.OPT_BG_LIGHT,
          MbgdDataMng.OPT_DP_FRAME_DARK,
          MbgdDataMng.OPT_DP_FRAME_LIGHT,
          MbgdDataMng.OPT_AL_SEQ_MAT_DARK,
          MbgdDataMng.OPT_AL_SEQ_MAT_LIGHT,
          MbgdDataMng.OPT_AL_SEQ_MIS_DARK,
          MbgdDataMng.OPT_AL_SEQ_MIS_LIGHT,
          MbgdDataMng.OPT_AL_SEQ_GAP_DARK,
          MbgdDataMng.OPT_AL_SEQ_GAP_LIGHT,
          MbgdDataMng.OPT_AL_ID_H_DARK,
          MbgdDataMng.OPT_AL_ID_H_LIGHT,
          MbgdDataMng.OPT_AL_ID_H_PERCENT,
          MbgdDataMng.OPT_AL_ID_H_PERCENT,
          MbgdDataMng.OPT_AL_ID_M_DARK,
          MbgdDataMng.OPT_AL_ID_M_LIGHT,
          MbgdDataMng.OPT_AL_ID_M_PERCENT,
          MbgdDataMng.OPT_AL_ID_M_PERCENT,
          MbgdDataMng.OPT_AL_ID_L_DARK,
          MbgdDataMng.OPT_AL_ID_L_LIGHT,
          MbgdDataMng.OPT_AL_ID_L_PERCENT,
          MbgdDataMng.OPT_AL_ID_L_PERCENT,
          MbgdDataMng.OPT_GENE_ATTR_H_DARK,
          MbgdDataMng.OPT_GENE_ATTR_H_LIGHT,
          MbgdDataMng.OPT_GENE_ATTR_L_DARK,
          MbgdDataMng.OPT_GENE_ATTR_L_LIGHT
        },

        //
        { MbgdDataMng.OPT_MAX_SEGMENTS,
          MbgdDataMng.OPT_PANEL_ALIGN_H,
          MbgdDataMng.OPT_PANEL_ALIGN_H,
          MbgdDataMng.OPT_PANEL_GENE_H,
          MbgdDataMng.OPT_PANEL_GENE_H,
          MbgdDataMng.OPT_PANEL_SEGMENT_H,
          MbgdDataMng.OPT_PANEL_SEGMENT_H
        },

        //
        { MbgdDataMng.OPT_DP_MATCH,
          MbgdDataMng.OPT_DP_MATCH,
          MbgdDataMng.OPT_DP_MISMATCH,
          MbgdDataMng.OPT_DP_MISMATCH,
          MbgdDataMng.OPT_DP_OPENGAP,
          MbgdDataMng.OPT_DP_OPENGAP,
          MbgdDataMng.OPT_DP_EXTGAP,
          MbgdDataMng.OPT_DP_EXTGAP,
          MbgdDataMng.OPT_AL_MAX_REALIGN
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Locations
    protected JTextField tfLocCgat;
    protected JTextField tfLocServer;
    protected JTextField tfLocProxy;
    protected JTextField tfLocBrowser;
    protected JTextField tfLocMbgdGene;
    protected JList lstLocServer;
    protected Vector vctLocServer = new Vector();

    ///////////////////////////////////////////////////////////////////////////
    // Color
    protected JButton btnColorBackgroundDark, btnColorBackgroundLight;
    protected JButton btnColorDotPlotFrameDark, btnColorDotPlotFrameLight;
    protected JButton btnColorMatchDark,      btnColorMatchLight;
    protected JButton btnColorMismatchDark,   btnColorMismatchLight;
    protected JButton btnColorGapDark,        btnColorGapLight;
    protected JButton btnColorIdentityHiDark, btnColorIdentityHiLight;
    protected JButton btnColorIdentityMdDark, btnColorIdentityMdLight;
    protected JButton btnColorIdentityLoDark, btnColorIdentityLoLight;
    protected JButton btnColorGeneAttrHiDark, btnColorGeneAttrHiLight;
    protected JButton btnColorGeneAttrLoDark, btnColorGeneAttrLoLight;
    protected JTextField tfColorIdentityHi;
    protected JTextField tfColorIdentityMd;
    protected JTextField tfColorIdentityLo;
    protected JSlider slColorIdentityHi;
    protected JSlider slColorIdentityMd;
    protected JSlider slColorIdentityLo;

    ///////////////////////////////////////////////////////////////////////////
    // Panel
    protected JComboBox  lstPanelMaxSegmentLane;
    protected JTextField tfPanelHeightAlignment;
    protected JTextField tfPanelHeightGene;
    protected JTextField tfPanelHeightSegment;
    protected JSlider slPanelHeightAlignment;
    protected JSlider slPanelHeightGene;
    protected JSlider slPanelHeightSegment;

    ///////////////////////////////////////////////////////////////////////////
    // Alignment
    protected JTextField tfAlignMatch;
    protected JTextField tfAlignMismatch;
    protected JTextField tfAlignOpenGap;
    protected JTextField tfAlignExtGap;
    protected JTextField tfAlignEdgeGap;
    protected JSlider slAlignMatch;
    protected JSlider slAlignMismatch;
    protected JSlider slAlignOpenGap;
    protected JSlider slAlignExtGap;
    protected JSlider slAlignEdgeGap;
    protected JComboBox lstAlignSizeRealign;
    protected Vector vctRealign;

    ///////////////////////////////////////////////////////////////////////////
    //
    public PropertyDialog(JFrame f, Properties p) {
        prop = new Properties(p);
        frame = f;

        setup();
        setValues();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setup() {
        tabPane = new JTabbedPane();

        JPanel panelLocations;
        if (! MbgdData.Instance().isApplet()) {
            panelLocations = createPanelLocations();
            tabPane.addTab("Locations", panelLocations);
        }

        JPanel panelColor     = createPanelColor();
        tabPane.addTab("Color",     panelColor);

        JPanel panelPanel     = createPanelPanel();
        tabPane.addTab("Panel",     panelPanel);

        JPanel panelAlignment = createPanelAlignment();
        tabPane.addTab("Alignment", panelAlignment);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setValues() {
        if (! MbgdData.Instance().isApplet()) {
            setValuePanelLocations();
        }
        setValuePanelColor();
        setValuePanelPanel();
        setValuePanelAlignment();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JButton createColorButton() {
        JButton button = new JButton("Change Color");
        button.setOpaque(false);
//        button.setContentAreaFilled(false);

        ActionListener al = new ButtonActionChangeColor(frame, button);
        button.addActionListener(al);

        return button;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColorButton(JButton button, Color bgc) {
/*
        button.setBackground(bgc);

        int r = bgc.getRed();   r = Math.abs(r - 255);
        int g = bgc.getGreen(); g = Math.abs(g - 255);
        int b = bgc.getBlue();  b = Math.abs(b - 255);
        button.setForeground(new Color(r, g, b));
*/


        button.setBackground(bgc);
        BufferedImage bi = new BufferedImage(10, 10,
                                             BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(bgc);
        g2d.fillRect(0, 0, 10, 10);
        ImageIcon ii = new ImageIcon(bi);
        button.setIcon(ii);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createPanelLocations() {
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc;
        JLabel label;
        JButton button;
        JTextField textField;
        JScrollPane jsp;
        JList list;
        String val;
        int x;
        int y = 1;
        JButton btnSelCgatHome;
        JButton btnSrvAdd, btnSrvUp, btnSrvDel, btnSrvDown;
        JButton btnSelBrowser;

        //
        panel = new JPanel(gbl);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("CGAT_HOME");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfLocCgat = new JTextField(30);
        panel.add(textField, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        button = btnSelCgatHome = new JButton("Choose");
        btnSelCgatHome.addActionListener(new ButtonActionChooseCgatHome(tfLocCgat));
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Server URL");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfLocServer = new JTextField(30);
        panel.add(textField, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        button = btnSrvAdd = new JButton("Add");
        panel.add(button, gbc);

        //
        x = 2;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.BOTH;
        list = lstLocServer = new JList();
        jsp = new JScrollPane(list);
        panel.add(jsp, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel panelBtn = new JPanel(new GridLayout(3, 1));
        panelBtn.add(button = btnSrvUp = new JButton("Up"));
        panelBtn.add(button = btnSrvDel = new JButton("Remove"));
        panelBtn.add(button = btnSrvDown = new JButton("Down"));
        panel.add(panelBtn, gbc);

        btnSrvAdd.addActionListener(new ButtonActionAddServer(list, vctLocServer, tfLocServer));
        btnSrvUp.addActionListener(new ButtonActionUpServer(list, vctLocServer));
        btnSrvDel.addActionListener(new ButtonActionDelServer(list, vctLocServer, tfLocServer));
        btnSrvDown.addActionListener(new ButtonActionDownServer(list, vctLocServer));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Proxy URL");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfLocProxy = new JTextField(30);
        panel.add(textField, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Browser Path");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfLocBrowser = new JTextField(30);
        panel.add(textField, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        button = btnSelBrowser = new JButton("Choose");
        btnSelBrowser.addActionListener(new ButtonActionChooseBrowser(tfLocBrowser));
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("URL for Gene Information");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfLocMbgdGene = new JTextField(30);
        panel.add(textField, gbc);

        //
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(panel, BorderLayout.CENTER);

        JPanel subPanel = new JPanel();
        button = new JButton(TXT_RESET_BTN);
        subPanel.add(button);
        basePanel.add(subPanel, BorderLayout.SOUTH);
        button.addActionListener(new ResetAllProperties(PANEL_LOCATION));

        return basePanel;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setValuePanelLocations() {
        JLabel label;
        JButton button;
        JTextField textField;
        JScrollPane jsp;
        JList list;
        String val;

        //
        val = prop.getProperty(MbgdDataMng.OPT_DIR_HOME);
        tfLocCgat.setText(val);

        //
        tfLocServer.setText("");

        //
        vctLocServer.clear();
        for(int i = 0; i < MbgdDataMng.MAX_URL_HOME; i++) {
            String key = MbgdDataMng.OPT_URL_HOME + i;
            val = prop.getProperty(key);
            if (val == null) {
                continue;
            }

            val = val.trim();
            if ("".equals(val)) {
                continue;
            }

            vctLocServer.add(val);
        }
        lstLocServer.setListData(vctLocServer);

        //
        val = prop.getProperty(MbgdDataMng.OPT_URL_PROXY);
        tfLocProxy.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_CMD_BROWSER_USER);
        tfLocBrowser.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_MBGD_URL_GENE);
        tfLocMbgdGene.setText(val);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createPanelColor() {
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc;
        JLabel label;
        JButton button;
        Color c;
        JTextField textField;
        JSlider slider;
        int slMin, slMax;
        String val;
        int x;
        int y = 1;

        //
        panel = new JPanel(gbl);

        //
        x = 2;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Dark");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Light");
        panel.add(label, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Background for Alignment/DotPlot");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorBackgroundDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorBackgroundLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Viewing Frame for DotPlot");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorDotPlotFrameDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorDotPlotFrameLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Match");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorMatchDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorMatchLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Mismatch");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorMismatchDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorMismatchLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Gap");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorGapDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorGapLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridheight = 2;
        label = new JLabel("Identity(High)");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorIdentityHiDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorIdentityHiLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 2;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 0;
        slMax = 100;
        slider = slColorIdentityHi = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfColorIdentityHi = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridheight = 2;
        label = new JLabel("Identity(Middle)");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorIdentityMdDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorIdentityMdLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 2;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 0;
        slMax = 100;
        slider = slColorIdentityMd = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfColorIdentityMd = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridheight = 2;
        label = new JLabel("Identity(low)");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorIdentityLoDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorIdentityLoLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 2;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 0;
        slMax = 100;
        slider = slColorIdentityLo = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfColorIdentityLo = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Gene Attribute(High)");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorGeneAttrHiDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorGeneAttrHiLight = createColorButton();
        panel.add(button, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Gene Attribute(Low)");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorGeneAttrLoDark = createColorButton();
        panel.add(button, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        button = btnColorGeneAttrLoLight = createColorButton();
        panel.add(button, gbc);

        //
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(panel, BorderLayout.CENTER);

        JPanel subPanel = new JPanel();
        button = new JButton(TXT_RESET_BTN);
        subPanel.add(button);
        basePanel.add(subPanel, BorderLayout.SOUTH);
        button.addActionListener(new ResetAllProperties(PANEL_COLOR));

        return basePanel;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setValuePanelColor() {
        JLabel label;
        JButton button;
        Color c;
        JTextField textField;
        JSlider slider;
        int slMin, slMax;
        String val;

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_BG_DARK), 16));
        setColorButton(btnColorBackgroundDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_BG_LIGHT), 16));
        setColorButton(btnColorBackgroundLight, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_DP_FRAME_DARK), 16));
        setColorButton(btnColorDotPlotFrameDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_DP_FRAME_LIGHT), 16));
        setColorButton(btnColorDotPlotFrameLight, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_SEQ_MAT_DARK), 16));
        setColorButton(btnColorMatchDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_SEQ_MAT_LIGHT), 16));
        setColorButton(btnColorMatchLight, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_SEQ_MIS_DARK), 16));
        setColorButton(btnColorMismatchDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_SEQ_MIS_LIGHT), 16));
        setColorButton(btnColorMismatchLight, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_SEQ_GAP_DARK), 16));
        setColorButton(btnColorGapDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_SEQ_GAP_LIGHT), 16));
        setColorButton(btnColorGapLight, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_ID_H_DARK), 16));
        setColorButton(btnColorIdentityHiDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_ID_H_LIGHT), 16));
        setColorButton(btnColorIdentityHiLight, c);

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_ID_H_PERCENT);
        slColorIdentityHi.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_ID_H_PERCENT);
        tfColorIdentityHi.setText(val);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_ID_M_DARK), 16));
        setColorButton(btnColorIdentityMdDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_ID_M_LIGHT), 16));
        setColorButton(btnColorIdentityMdLight, c);

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_ID_M_PERCENT);
        slColorIdentityMd.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_ID_M_PERCENT);
        tfColorIdentityMd.setText(val);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_ID_L_DARK), 16));
        setColorButton(btnColorIdentityLoDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_AL_ID_L_LIGHT), 16));
        setColorButton(btnColorIdentityLoLight, c);

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_ID_L_PERCENT);
        slColorIdentityLo.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_ID_L_PERCENT);
        tfColorIdentityLo.setText(val);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_H_DARK), 16));
        setColorButton(btnColorGeneAttrHiDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_H_LIGHT), 16));
        setColorButton(btnColorGeneAttrHiLight, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_L_DARK), 16));
        setColorButton(btnColorGeneAttrLoDark, c);

        //
        c = new Color(Integer.parseInt(prop.getProperty(MbgdDataMng.OPT_GENE_ATTR_L_LIGHT), 16));
        setColorButton(btnColorGeneAttrLoLight, c);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createPanelPanel() {
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc;
        JLabel label;
        JButton button;
        JTextField textField;
        JComboBox list;
        JSlider slider;
        int slMin, slMax;
        String val;
        int x;
        int y = 1;
        String listNumSeg[] = { "1", "2", "3", "4", "5" };

        //
        panel = new JPanel(gbl);

        //
        x = 1;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Maximan # of Segment Tracks");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        list = lstPanelMaxSegmentLane = new JComboBox(listNumSeg);
        Dimension dim = list.getMinimumSize();
        dim.width = 40;
        list.setMinimumSize(dim);
        list.setPreferredSize(dim);
        panel.add(list, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Height of Alignment Area");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 50;
        slMax = 250;
        slider = slPanelHeightAlignment = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfPanelHeightAlignment = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Height of Gene Area");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 50;
        slMax = 250;
        slider = slPanelHeightGene = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfPanelHeightGene = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Height of Segment Area");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 50;
        slMax = 250;
        slider = slPanelHeightSegment = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfPanelHeightSegment = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(panel, BorderLayout.CENTER);

        JPanel subPanel = new JPanel();
        button = new JButton(TXT_RESET_BTN);
        subPanel.add(button);
        basePanel.add(subPanel, BorderLayout.SOUTH);
        button.addActionListener(new ResetAllProperties(PANEL_PANEL));

        return basePanel;
    }

     ///////////////////////////////////////////////////////////////////////////
    //
    public void setValuePanelPanel() {
        JLabel label;
        JButton button;
        JTextField textField;
        JComboBox list;
        JSlider slider;
        int slMin, slMax;
        String val;

        //
        val = prop.getProperty(MbgdDataMng.OPT_MAX_SEGMENTS);
        lstPanelMaxSegmentLane.setSelectedIndex(Integer.parseInt(val) - 1);

        //
        val = prop.getProperty(MbgdDataMng.OPT_PANEL_ALIGN_H);
        slPanelHeightAlignment.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_PANEL_ALIGN_H);
        tfPanelHeightAlignment.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_PANEL_GENE_H);
        slPanelHeightGene.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_PANEL_GENE_H);
        tfPanelHeightGene.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_PANEL_SEGMENT_H);
        slPanelHeightSegment.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_PANEL_SEGMENT_H);
        tfPanelHeightSegment.setText(val);
    }

     ///////////////////////////////////////////////////////////////////////////
    //
    public JPanel createPanelAlignment() {
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc;
        JLabel label;
        JButton button;
        JTextField textField;
        JSlider slider;
        int slMin, slMax;
        JComboBox list;
        String val;
        int x;
        int y = 1;

        //
        panel = new JPanel(gbl);

        //
        x = 1;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Parameter for Dynamic Programming");
        panel.add(label, gbc);

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Match Score");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = 0;
        slMax = 20;
        slider = slAlignMatch = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfAlignMatch = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField, 0, 1));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Mismatch Score");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = -20;
        slMax = 0;
        slider = slAlignMismatch = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfAlignMismatch = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Open Gap Penalty");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = -20;
        slMax = 0;
        slider = slAlignOpenGap = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfAlignOpenGap = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Extension Gap Penalty");
        panel.add(label, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        slMin = -20;
        slMax = 0;
        slider = slAlignExtGap = new JSlider(slMin, slMax);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel.add(slider, gbc);

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        textField = tfAlignExtGap = new JTextField(5);
        panel.add(textField, gbc);
        // textfield をスライダと関連づけ
        textField.addActionListener(new TextFieldChanged(slMin, slMax, slider));
        slider.addChangeListener(new SliderStateChanged(textField));

        //
        x = 1;
        y++;
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        label = new JLabel("Size of Realignment");
        panel.add(label, gbc);

        //
        vctRealign = new Vector();
        vctRealign.add("1000");
        vctRealign.add("2000");
        vctRealign.add("3000");
        vctRealign.add("5000");
        vctRealign.add("10000");

        //
        gbc = new GridBagConstraints();
        gbc.gridx = x++;
        gbc.gridy = y;
        gbc.gridwidth = 2; x++;
        list = lstAlignSizeRealign = new JComboBox(vctRealign);
        panel.add(list, gbc);

        //
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(panel, BorderLayout.CENTER);

        JPanel subPanel = new JPanel();
        button = new JButton(TXT_RESET_BTN);
        subPanel.add(button);
        basePanel.add(subPanel, BorderLayout.SOUTH);
        button.addActionListener(new ResetAllProperties(PANEL_ALIGNMENT));

        return basePanel;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setValuePanelAlignment() {
        JLabel label;
        JButton button;
        JTextField textField;
        JSlider slider;
        int slMin, slMax;
        JComboBox list;
        String val;

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_MATCH);
        slAlignMatch.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_MATCH);
        tfAlignMatch.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_MISMATCH);
        slAlignMismatch.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_MISMATCH);
        tfAlignMismatch.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_OPENGAP);
        slAlignOpenGap.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_OPENGAP);
        tfAlignOpenGap.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_EXTGAP);
        slAlignExtGap.setValue(Integer.parseInt(val));

        //
        val = prop.getProperty(MbgdDataMng.OPT_DP_EXTGAP);
        tfAlignExtGap.setText(val);

        //
        val = prop.getProperty(MbgdDataMng.OPT_AL_MAX_REALIGN);
        int idx = vctRealign.indexOf(val);
        if (idx < 0) {
            idx = 0;
        }
        lstAlignSizeRealign.setSelectedIndex(idx);
    }

   ///////////////////////////////////////////////////////////////////////////
    //
    public void showDialog() {
        int ret = JOptionPane.showConfirmDialog(frame.getContentPane(),
                                                tabPane,
                                                "CGAT Properties",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        staSelect = false;
        if (ret == JOptionPane.OK_OPTION) {
            staSelect = true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getStaSelect() {
        return staSelect;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void updateProperties() {
        Color c;
        String v;

        ///////////////////////////////////////////////////////////////////////
        // Locations
        if (! MbgdData.Instance().isApplet()) {
            v = tfLocCgat.getText();
            prop.setProperty(MbgdDataMng.OPT_DIR_HOME, v);

            for(int i = 0; i < MbgdDataMng.MAX_URL_HOME; i++) {
                String key = MbgdDataMng.OPT_URL_HOME + i;
                try {
                    v = (String)vctLocServer.get(i);
                }
                catch (Exception e) {
                    v = "";
                }
                if (v == null) {
                    v = "";
                }
                prop.setProperty(key, v);
            }

            v = tfLocProxy.getText();
            prop.setProperty(MbgdDataMng.OPT_URL_PROXY, v);

            v = tfLocBrowser.getText();
            prop.setProperty(MbgdDataMng.OPT_CMD_BROWSER_USER, v);

            v = tfLocMbgdGene.getText();
            prop.setProperty(MbgdDataMng.OPT_MBGD_URL_GENE, v);
        }

        ///////////////////////////////////////////////////////////////////////
        // Color
        c = btnColorBackgroundDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_BG_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorBackgroundLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_BG_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorDotPlotFrameDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_DP_FRAME_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorDotPlotFrameLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_DP_FRAME_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorMatchDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_SEQ_MAT_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorMatchLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_SEQ_MAT_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorMismatchDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_SEQ_MIS_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorMismatchLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_SEQ_MIS_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorGapDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_SEQ_GAP_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorGapLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_SEQ_GAP_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorIdentityHiDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_ID_H_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorIdentityHiLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_ID_H_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorIdentityMdDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_ID_M_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorIdentityMdLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_ID_M_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorIdentityLoDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_ID_L_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorIdentityLoLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_AL_ID_L_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));

        v = tfColorIdentityHi.getText();
        try {
            double d = Double.parseDouble(v);
            if (0 < d) {
                prop.setProperty(MbgdDataMng.OPT_AL_ID_H_PERCENT, v);
            }
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfColorIdentityMd.getText();
        try {
            double d = Double.parseDouble(v);
            if (0 < d) {
                prop.setProperty(MbgdDataMng.OPT_AL_ID_M_PERCENT, v);
            }
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfColorIdentityLo.getText();
        try {
            double d = Double.parseDouble(v);
            if (0 < d) {
                prop.setProperty(MbgdDataMng.OPT_AL_ID_L_PERCENT, v);
            }
        }
        catch (Exception e) {
            // 数値変換エラー
        }

        c = btnColorGeneAttrHiDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_GENE_ATTR_H_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorGeneAttrHiLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_GENE_ATTR_H_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorGeneAttrLoDark.getBackground();
        prop.setProperty(MbgdDataMng.OPT_GENE_ATTR_L_DARK, Integer.toHexString(c.getRGB()&0xFFFFFF));
        c = btnColorGeneAttrLoLight.getBackground();
        prop.setProperty(MbgdDataMng.OPT_GENE_ATTR_L_LIGHT, Integer.toHexString(c.getRGB()&0xFFFFFF));

        ///////////////////////////////////////////////////////////////////////
        // Panel
        v = (String)lstPanelMaxSegmentLane.getSelectedItem();
        try {
            int i = Integer.parseInt(v);
            if ((1 <= i) && (i <= 5)) {
                prop.setProperty(MbgdDataMng.OPT_MAX_SEGMENTS, v);
            }
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfPanelHeightAlignment.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_PANEL_ALIGN_H, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfPanelHeightGene.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_PANEL_GENE_H, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfPanelHeightSegment.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_PANEL_SEGMENT_H, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }

        ///////////////////////////////////////////////////////////////////////
        // Alignment
        v = tfAlignMatch.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_DP_MATCH, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfAlignMismatch.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_DP_MISMATCH, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfAlignOpenGap.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_DP_OPENGAP, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = tfAlignExtGap.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_DP_EXTGAP, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
//        v = tfAlignEdgeGap.getText();
// Edge Gap は、常にOpenGapと一致させる
        v = tfAlignOpenGap.getText();
        try {
            int i = Integer.parseInt(v);
            prop.setProperty(MbgdDataMng.OPT_DP_EDGEGAP, v);
        }
        catch (Exception e) {
            // 数値変換エラー
        }
        v = (String)lstAlignSizeRealign.getSelectedItem();
        try {
            int i = Integer.parseInt(v);
            if (0 < i) {
                prop.setProperty(MbgdDataMng.OPT_AL_MAX_REALIGN, v);
            }
        }
        catch (Exception e) {
            // 数値変換エラー
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Properties getProperties() {
        return prop;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void loadDefaultProperties(int idx) {
        //
        Properties tmpProp = new Properties();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("cgat.properties");
            tmpProp.load(is);
            Dbg.println(1, "Success :: loadProperties()");
        }
        catch (Exception eDefault) {
            Dbg.println(1, "Error :: loadProperties()");
        }

        // Tab 単位でプロパティーをデフォルトに戻す
        String key, val;
        for(int i = 0; i < lstPanelPropKey[idx].length;  i++) {
            key = lstPanelPropKey[idx][i];
            val = tmpProp.getProperty(key);
            prop.remove(key);
            if (val != null) {
                prop.setProperty(key, val);
            }
        }
        if ((! MbgdData.Instance().isApplet()) && (idx == PANEL_LOCATION)) {
            for(int i = 0; i < MbgdDataMng.MAX_URL_HOME;  i++) {
                key = MbgdDataMng.OPT_URL_HOME + i;
                val = tmpProp.getProperty(key);
                prop.remove(key);
                if (val != null) {
                    prop.setProperty(key, val);
                }
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionChangeColor implements ActionListener {
        protected JFrame ccFrame;
        protected JButton ccButton;
        public ButtonActionChangeColor(JFrame f, JButton b) {
            ccFrame  = f;
            ccButton = b;
        }

        public void actionPerformed(ActionEvent e) {
            Color bgc = ccButton.getBackground();
            bgc = JColorChooser.showDialog(frame, "Select Color", bgc);
            if (bgc != null) {
                setColorButton(ccButton, bgc);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionChooseCgatHome implements ActionListener {
        protected JFileChooser fc;
        protected JTextField text;
        public ButtonActionChooseCgatHome(JTextField tf) {
            text = tf;

            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        public void actionPerformed(ActionEvent e) {
            int ret = fc.showOpenDialog(frame);
            if(ret == JFileChooser.APPROVE_OPTION) {
                text.setText(fc.getSelectedFile().getPath());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionAddServer implements ActionListener {
        protected JList list;
        protected Vector vector;
        protected JTextField text;
        public ButtonActionAddServer(JList l, Vector v, JTextField tf) {
            list = l;
            text = tf;
            vector = v;
        }

        public void actionPerformed(ActionEvent e) {
            String serv = text.getText().trim();
            if ("".equals(serv)) {
                return;
            }
            if (10 <= vector.size()) {
                return;
            }

            vector.add(serv);
            list.setListData(vector);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionUpServer implements ActionListener {
        protected JList list;
        protected Vector vector;
        public ButtonActionUpServer(JList l, Vector v) {
            list = l;
            vector = v;
        }

        public void actionPerformed(ActionEvent e) {
            int selIdx = list.getSelectedIndex();
            if (selIdx < 0) {
                return;
            }
            if (selIdx == 0) {
                return;
            }

            String servSel = (String)vector.get(selIdx);
            vector.removeElementAt(selIdx);
            vector.insertElementAt((Object)servSel, selIdx - 1);

            list.setListData(vector);
            list.setSelectedIndex(selIdx - 1);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionDelServer implements ActionListener {
        protected JList list;
        protected Vector vector;
        protected JTextField text;
        public ButtonActionDelServer(JList l, Vector v, JTextField tf) {
            list = l;
            vector = v;
            text = tf;
        }

        public void actionPerformed(ActionEvent e) {
            int selIdx = list.getSelectedIndex();
            if (selIdx < 0) {
                return;
            }
            String serv = (String)vector.get(selIdx);
            vector.removeElementAt(selIdx);

            list.setListData(vector);
            text.setText(serv);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionDownServer implements ActionListener {
        protected JList list;
        protected Vector vector;
        public ButtonActionDownServer(JList l, Vector v) {
            list = l;
            vector = v;
        }

        public void actionPerformed(ActionEvent e) {
            int selIdx = list.getSelectedIndex();
            if (selIdx < 0) {
                return;
            }
            if (selIdx == vector.size() - 1) {
                return;
            }

            String servSel = (String)vector.get(selIdx);
            vector.removeElementAt(selIdx);
            vector.insertElementAt((Object)servSel, selIdx + 1);

            list.setListData(vector);
            list.setSelectedIndex(selIdx + 1);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ButtonActionChooseBrowser implements ActionListener {
        protected JFileChooser fc;
        protected JTextField text;
        public ButtonActionChooseBrowser(JTextField tf) {
            text = tf;

            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }

        public void actionPerformed(ActionEvent e) {
            int ret = fc.showOpenDialog(frame);
            if(ret == JFileChooser.APPROVE_OPTION) {
                text.setText(fc.getSelectedFile().getPath());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class TextFieldChanged implements ActionListener {
        protected int slMin;
        protected int slMax;
        protected JSlider slider;
        public TextFieldChanged(int min, int max, JSlider s) {
            slMin = min;
            slMax = max;
            slider= s;
        }

        public void actionPerformed(ActionEvent e) {
            JTextField tf = (JTextField)e.getSource();
            String val = tf.getText();
            int v;
            try {
                v = Integer.parseInt(val);
                if (v < slMin) {
                    v = slMin;
                    tf.setText("" + v);
                }
                if (slMax < v) {
                    v = slMax;
                    tf.setText("" + v);
                }
                slider.setValue(v);
            }
            catch (Exception tfce) {
                tf.setText("" + slider.getValue());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class SliderStateChanged implements ChangeListener {
        protected JTextField textfield;
        protected Integer ngVal = null;
        protected Integer okVal = null;

        public SliderStateChanged(JTextField tf) {
            textfield = tf;
        }

        public SliderStateChanged(JTextField tf, int ng, int ok) {
            textfield = tf;
            ngVal = new Integer(ng);
            okVal = new Integer(ok);
        }

        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider)e.getSource();
            int v = slider.getValue();
            if (ngVal != null) {
                if (ngVal.intValue() == v) {
                    v = okVal.intValue();
                    slider.setValue(v);
                    slider.validate();
                }
            }
            textfield.setText("" + v);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ResetAllProperties implements ActionListener {
        protected int idxPanel;
        public ResetAllProperties(int idx) {
            idxPanel = idx;
        }

        public void actionPerformed(ActionEvent e) {
            loadDefaultProperties(idxPanel);
//            setValues();
            switch(idxPanel) {
            case PANEL_LOCATION:
                setValuePanelLocations();
                break;
            case PANEL_COLOR:
                setValuePanelColor();
                break;
            case PANEL_PANEL:
                setValuePanelPanel();
                break;
            case PANEL_ALIGNMENT:
                setValuePanelAlignment();
                break;
            default:
                break;
            }
            frame.validate();
        }
    }

}
