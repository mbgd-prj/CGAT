package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class MessageWindow extends JFrame implements ActionListener {
    private static final String ERR_type = "text/html";
    private static final String ERR_text = "<h1>ERROR</h1>";

    private static final int MODE_text     = 0;
    private static final int MODE_url      = 1;
    private static final int MODE_initPage = 2;

    private int    mode;
    private String type;
    private String text;
    private String url;
    private URL    initPage;

    private JEditorPane ePane;

    ///////////////////////////////////////////////////////////////////////////
    //
    public MessageWindow() {
        super();

        getContentPane().setLayout(new BorderLayout());
        setSize(200, 400);

        setContents("text/plain", "");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setMode(int m) {
        mode = m;
        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getMode() {
        return mode;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setContents(String typ, String tex) {
        //
        setMode(MODE_text);
        type = typ;
        text = tex;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setContents(String u) {
        //
        setMode(MODE_url);
        url = u;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setContents(URL page) {
        //
        setMode(MODE_initPage);

        try {
            initPage = new URL(page.toString());
        }
        catch (Exception e) {
            initPage = null;
            setErrorContents(page.toString());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setErrorContents(String u) {
        setContents(ERR_type,
                    ERR_text+"<h3>" + u + "</h3>");
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private String getMyType() {
        return type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private String getText() {
        return text;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private String getUrl() {
        return url;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private URL getPage() {
        return initPage;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setContents() {
        //

        ePane = new JEditorPane();

        switch(getMode()) {
        default:
        case MODE_text:
            ePane.setContentType(getMyType());
            ePane.setText(getText());
            break;

        case MODE_url:
            try {
                ePane.setPage(getUrl());
            }
            catch (Exception e) {
                setErrorContents(getUrl());
                setContents();
                return;
            }
            break;

        case MODE_initPage:
            try {
                ePane.setPage(getPage());
            }
            catch (Exception e) {
                setErrorContents(getPage().toString());
                setContents();
                return;
            }
            break;
        }

        //
        JScrollPane js = new JScrollPane(ePane);
        getContentPane().add(js, BorderLayout.CENTER);

        return;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        setContents();
        setVisible(true);

        return;
    }

}
