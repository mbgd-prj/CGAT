package cgat;

import java.awt.event.*;
import java.util.*;

public class ChangeAlignmentColorCommand implements ActionListener {
	private MbgdDataMng dataMng;
	private RotateButton btn;

	public ChangeAlignmentColorCommand(MbgdDataMng m, RotateButton b) {
		dataMng = m;
		btn = b;
	}
	
    public void actionPerformed(ActionEvent e) {
		btn.setNext();
		dataMng.setAlignColorMode(btn.getSelectedIndex());
    }


}

