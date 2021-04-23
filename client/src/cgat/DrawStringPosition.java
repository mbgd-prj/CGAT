package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

import java.awt.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawStringPosition {
    ArrayList rectangleList;
    FontMetrics fontMetrics;
    int height;
    static final int MAX_HEIGHT=2;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawStringPosition(FontMetrics fm) {
        rectangleList = new ArrayList();
        fontMetrics = fm;
        setHeight(fontMetrics.getHeight());     // ���褹��ʸ����ι⤵
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setHeight(int h) {
        height = h;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addRectangle(Rectangle rect) {
        rectangleList.add(new Rectangle(rect));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Rectangle setString(String str, int x, int y) {
        return(setString(str, x, y, false));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Rectangle setString(String str, int x, int y, boolean dir) {
        Rectangle rect;
        int width  = fontMetrics.stringWidth(str);      // ���褹��ʸ�������

        // drawString() �θ���(����)��Rectangel() �θ���(����)���Ѵ�
        y -= height;

        // ����Ѥߤ�ʸ����ȽŤʤ�ʤ����֤򻻽Ф���
	int hh = 0;
        for(;;) {
            rect = new Rectangle(x, y, width, height);

            for(int i = 0; i < rectangleList.size(); i++) {
                Rectangle tmpRect = (Rectangle)rectangleList.get(i);
                if (rect.intersects(tmpRect)) {
                    // �Ť�
                    rect = null;
                    break;
                }
            }

            if (rect != null) {
                // �Ťʤ�ǡ������ʤ�
                break;
            }

            // �Ťʤ�
            if (dir) {
                //
                y -= height;
            }
            else {
                y += height;
            }
            if (++hh > MAX_HEIGHT) {
		// outside of the display region
		break;
	    }
        }

	if (rect != null) {
        	rectangleList.add(rect);
	}

        return(rect);
    }


}
