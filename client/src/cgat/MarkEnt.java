
/**
 * ?????ȥ?:     cgat<p>
 * @version 1.0
 */
package cgat;
import java.awt.*;
///////////////////////////////////////////////////////////////////////////////
//
public class MarkEnt {
    private String  spName;
    private String  orfName;
    private int     posFrom;
    private int     posTo;
    private int     dir;
    private int     colorType;
    private Color   color;

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEnt(String s, String n, int f, int t, int d, int ct, Color col) {
        _init(s, n, f, t, d, ct, col);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEnt(MarkEnt e) {
        _init(  e.getSpName(),
                e.getName(),
                e.getPosFrom(),
                e.getPosTo(),
                e.getDir(),
                e.getColorType(),
                e.getColor());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(String s, String n, int f, int t, int d, int ct, Color col) {
        setSpName(s);
        setName(n);
        setPos(f, t);
        setDir(d);
        setColorType(ct);
        setColor(col);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setSpName(String s) {
        spName = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setName(String n) {
        orfName = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setPos(int f, int t) {
        posFrom = f;
        posTo   = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDir(int d) {
        dir = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColorType(int ct) {
        colorType = ct;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColor(Color col) {
        color = col;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getSpName() {
        return spName;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getName() {
        return orfName;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getPosFrom() {
        return posFrom;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getPosTo() {
        return posTo;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getDir() {
        return dir;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getColorType() {
        return colorType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getColor() {
        return color;
    }

}
