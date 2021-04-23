package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseFilterDialog extends JDialog {
    private int status;

    public static final String NAM_BETWEEN      = "Between (e.g. 10,20)";
    public static final String NAM_EXPTBETWEEN  = "Except Between";
    public static final String NAM_LESSTHAN     = "Less Than";
    public static final String NAM_LESSEQUAL    = "Less Equal";
    public static final String NAM_EQUAL        = "Equal";
    public static final String NAM_GREATEREQUAL = "Greater Equal";
    public static final String NAM_GREATERTHAN  = "Greater Than";
    public static final String NAM_REGEX        = "Regex";
    public static final String NAM_SELECT       = "Select";

    public static final String CondValList[] = { NAM_BETWEEN,
                                                 NAM_EXPTBETWEEN,
                                                 NAM_LESSTHAN,
                                                 NAM_LESSEQUAL,
                                                 NAM_EQUAL,
                                                 NAM_GREATEREQUAL,
                                                 NAM_GREATERTHAN };
    public static final String CondStrList[] = { NAM_REGEX,
                                                 NAM_EQUAL };

    public static final int STA_CANCEL       = -1;
    public static final int STA_FILTER       = 0;
    public static final int STA_CLEAR        = 1;
    public static final int STA_CLEAR_ALL    = 2;


    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseFilterDialog(Frame f, boolean sta) {
        super(f, sta);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getStatus() {
        return status;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setStatus(int sta) {
        status = sta;
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    class CmdCancel implements ActionListener {
        private BaseFilterDialog dialog;

        ///////////////////////////////////////////////////////////////////////
        //
        public CmdCancel(BaseFilterDialog d) {
            dialog = d;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(false);
            dialog.setStatus(BaseFilterDialog.STA_CANCEL);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class CmdFilter implements ActionListener {
        private BaseFilterDialog dialog;

        ///////////////////////////////////////////////////////////////////////
        //
        public CmdFilter(BaseFilterDialog d) {
            dialog = d;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(false);
            dialog.setStatus(BaseFilterDialog.STA_FILTER);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class CmdClear implements ActionListener {
        private BaseFilterDialog dialog;

        ///////////////////////////////////////////////////////////////////////
        //
        public CmdClear(BaseFilterDialog d) {
            dialog = d;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(false);
            dialog.setStatus(BaseFilterDialog.STA_CLEAR);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class CmdClearAll implements ActionListener {
        private BaseFilterDialog dialog;

        ///////////////////////////////////////////////////////////////////////
        //
        public CmdClearAll(BaseFilterDialog d) {
            dialog = d;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(false);
            dialog.setStatus(BaseFilterDialog.STA_CLEAR_ALL);
        }
    }

}
