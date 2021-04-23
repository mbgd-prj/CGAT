package cgat;

import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class RotateButton extends BaseButton {
	protected int selectedIndex = 0;
	protected ArrayList labels = new ArrayList();
	protected ArrayList icons = new ArrayList();
	
	public RotateButton() {
		super("");
	}
	
	public void clearLabel() {
		labels.clear();
	}

	public void addLabel(String lab) {
		labels.add(lab);
		if (labels.size() == 1) {
			setText(lab);
		}
        clearIcon();
	}

	public void clearIcon() {
		icons.clear();
	}

	public void addIcon(String filename) {
        URL url = getClass().getClassLoader().getResource(filename);
        addIcon(new ImageIcon(url));
	}

	public void addIcon(Icon icon) {
		icons.add(icon);
		if (icons.size() == 1) {
			setIcon(icon);
		}
        clearLabel();
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

    public void setNext() {
    	selectedIndex++;

        if (labels.size() != 0) {
            selectedIndex %= labels.size();
			setText((String)labels.get(selectedIndex));
		}
        else if (icons.size() != 0) {
            selectedIndex %= icons.size();
			setIcon((Icon)icons.get(selectedIndex));
		}
		else {
			setText("");
		}
    }

}

