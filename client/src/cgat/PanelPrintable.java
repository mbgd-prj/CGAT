
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.print.*;

///////////////////////////////////////////////////////////////////////////////
// 画像表示データ群
public class PanelPrintable extends JPanel implements Printable {
    protected JPanel basePanel = new JPanel();

    public PanelPrintable() {
        super();
basePanel = this;
    }

    public JPanel getPanel() {
        return basePanel;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int print(Graphics g, PageFormat fmt, int pageIdx) {
	if(pageIdx > 0) {
		return Printable.NO_SUCH_PAGE;
	}

	Graphics2D g2 = (Graphics2D) g;
	g2.translate(fmt.getImageableX(), fmt.getImageableY());
	double pageWidth = fmt.getImageableWidth();
	double pageHeight = fmt.getImageableHeight();
	Dimension size = basePanel.getSize();
	double scale = 1.0;

	if(size.width > pageWidth) {
		scale = pageWidth / size.width;
	}
	if(pageHeight / size.height < scale) {
		scale = pageHeight / size.height;
	}
	g2.scale(scale,scale);
	pageWidth /= scale; pageHeight /= scale;

	g2.translate((pageWidth - size.width) / 2,
			(pageHeight - size.height)/2);
	basePanel.printAll(g2);
	return Printable.PAGE_EXISTS;
    }
}
