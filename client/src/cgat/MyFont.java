
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.geom.*;

///////////////////////////////////////////////////////////////////////////////
// Font ���ѷ����ܳѤ��ž���ˤ���
//
public class MyFont {
    Font myFont;

    ///////////////////////////////////////////////////////////////////////////
    //
    public MyFont(Font f) {
        setFont(f);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFont(Font f) {
        // �ե���Ȥ򥳥ԡ�����
        myFont = new Font(f.getName(), f.getStyle(), f.getSize());
    }

    ///////////////////////////////////////////////////////////////////////////
    // �ե���Ȥ��礭�����Ѥ���
    // ����
    //     w : ��������Ψ
    //     h : ��������Ψ
    public void trans(double w, double h) {
        AffineTransform at = new AffineTransform();
        at.setToScale(w, h);
        myFont.deriveFont(at);
    }

    ///////////////////////////////////////////////////////////////////////////
    // �ե���Ȥθ������Ѥ���ʺ���ž��
    // ����
    //     r : ��ž�ѡʥ饸�����
    public void trans(double r) {
        AffineTransform at = new AffineTransform();
        at.setToRotation(r);
        myFont.deriveFont(at);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Font getFont() {
        return(myFont);
    }




}
