package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

///////////////////////////////////////////////////////////////////////////////
//
public class GuiElement {
    protected String type;
    protected String text;
    protected String varname;
    protected String val;
    protected int nrow;
    protected int ncol;

    ///////////////////////////////////////////////////////////////////////////
    //
    public GuiElement (String typ, String tex) {
        setType(typ);
        setText(tex);
        setVarname("");
        setVal("");
        setNrow(0);
        setNcol(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public GuiElement (String typ, String tex, String var) {
        setType(typ);
        setText(tex);
        setVarname(var);
        setVal("");
        setNrow(0);
        setNcol(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public GuiElement (String typ, String tex, String var, String val) {
        setType(typ);
        setText(tex);
        setVarname(var);
        setVal(val);
        setNrow(0);
        setNcol(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public GuiElement (String typ, String tex, String var, String val, int nr, int nc) {
        setType(typ);
        setText(tex);
        setVarname(var);
        setVal(val);
        setNrow(nr);
        setNcol(nc);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setType(String t) {
        type = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getType() {
        return type;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setText(String t) {
        text = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getText() {
        return text;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setVarname(String v) {
        varname = v;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getVarname() {
        return varname;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setVal(String d) {
        val = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getVal() {
        return val;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setNrow(int n) {
        nrow = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getNrow() {
        return nrow;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setNcol(int n) {
        ncol = n;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getNcol() {
        return ncol;
    }

}
