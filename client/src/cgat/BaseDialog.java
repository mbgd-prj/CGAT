package cgat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// ��������
public class BaseDialog extends JDialog {
    protected Container pane;

    ///////////////////////////////////////////////////////////////////////////
    //�ե졼��ι���
    public BaseDialog(JFrame f, boolean m, int width, int height, String title) {
        super(f, m);
        this.setSize(new Dimension(width, height));
        this.setTitle(title);
    }
}
