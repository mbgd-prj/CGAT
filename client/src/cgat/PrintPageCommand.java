package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
//
public class PrintPageCommand implements ActionListener {
    private Printable prt;

    ///////////////////////////////////////////////////////////////////////////
    //
    public PrintPageCommand(Printable p) {
        prt = p;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void actionPerformed(ActionEvent e) {

        try {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob != null) {
                printJob.setPrintable(prt, printJob.defaultPage());
                if (printJob.printDialog()) {
                    try {
                        printJob.print();
                    } catch(Exception ex) {
                        Dbg.printStackTrace(1, ex);
                    }
                }
            }
        }
        catch (java.lang.SecurityException secExcept) {
            Dbg.println(0, "You do not have permission.");
        }
        catch (Exception except) {
            Dbg.printStackTrace(1, except);
        }
    }

}
