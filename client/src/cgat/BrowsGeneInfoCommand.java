
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.applet.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BrowsGeneInfoCommand implements ActionListener {
    private String strUrl;
    private String strOpt;

    ///////////////////////////////////////////////////////////////////////////
    //
    public BrowsGeneInfoCommand(String u, String o) {
        strUrl   = u;
        strOpt   = o;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {
        ExecBrowser browser = new ExecBrowser();

        MbgdData mbgdData = MbgdData.Instance();

	if (mbgdData.isApplet()) {
	        browser.appletBrowser(mbgdData.getApplet(), strUrl, strOpt);
	} else {
	        browser.execBrowser(strUrl);
	}
    Dbg.println(1, "URL :: "+strUrl);

    }

}
