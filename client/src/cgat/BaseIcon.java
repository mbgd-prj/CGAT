
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;

public class BaseIcon implements Icon {
    static final int width  = 16;
    static final int height = 16;
    Color color;

    public BaseIcon() {
        color = Color.red;
    }
    public BaseIcon(Color c) {
        color = c;
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(this.color);
        g.fillOval(x, y, width, height);
    }
    public int getIconWidth() {
        return(width);
    }
    public int getIconHeight() {
        return(height);
    }
}
