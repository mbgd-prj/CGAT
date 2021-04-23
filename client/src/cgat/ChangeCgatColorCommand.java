package cgat;

import java.awt.event.*;
import java.util.*;

public class ChangeCgatColorCommand implements ActionListener {
	private MbgdDataMng dataMng;
    private RotateButton btn;

	public ChangeCgatColorCommand(MbgdDataMng m, RotateButton b) {
		dataMng = m;
        btn = b;
	}
	
    public void actionPerformed(ActionEvent e) {
        btn.setNext();
        int selIdx = btn.getSelectedIndex();
        if (selIdx == 0) {
			dataMng.setUseColor(ColorTab.USE_COLOR_DARK);
		}
		else {
			dataMng.setUseColor(ColorTab.USE_COLOR_LIGHT);
		}
    }


}

