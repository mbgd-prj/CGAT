
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;

///////////////////////////////////////////////////////////////////////////////
//
public class RegionInfo {
    protected boolean filter;       // フィルタリング：true の場合【表示する】
    protected int     from;         // 開始位置
    protected int     to;           // 終了位置
    protected byte    dir;          // 向き（3→5:-1, なし:0, 5→3:1）
    protected int     lane = 1;     // レーン番号
    protected float   weight = 1.0f;// 高さの比
    protected String  color;
    protected String  attr[];
    protected boolean attrState[];

    static final int  maxAttrNum = 5;
//    static final int  idxName  = 0;
//    static final int  idxRsrv1 = 1;
//    static final int  idxRsrv2 = 2;
//    static final int  idxRsrv3 = 3;
//    static final int  idxRsrv4 = 4;

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo() {
        setFilter(true);
        initAttr();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo(int f, int t, byte d, String c) {
        _init(true, f, t, d, c, 1, 1.0f);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo(int f, int t, byte d, String c, int l, float r) {
        _init(true, f, t, d, c, l, r);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionInfo(RegionInfo info) {
        _init(info.getFilter(),
                info.getFrom(),
                info.getTo(),
                info.getDir(),
                info.getColor(),
                info.getLane(),
                info.getWeight());

        for(int i = 0; i < maxAttrNum; i++) {
            setAttr(i, info.getAttr(i));
            setAttrState(i, info.getAttrState(i));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void _init(boolean sta, int f, int t, byte d, String c, int l, float w) {
        setFilter(sta);
        setFrom(f);
        setTo(t);
        setDir(d);
        setColor(c);
        setLane(l);
        setWeight(w);

        initAttr();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    protected void initAttr() {
        attr = new String[maxAttrNum];
        attrState = new boolean[maxAttrNum];
        for(int i = 0; i < maxAttrNum; i++) {
            setAttr(i, "");
            setAttrState(i, false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAttr(int n, String a) {
        attr[n] = a;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getAttr(int n) {
        if ((n < 0) ||
            (maxAttrNum <= n) ||
            (attr[n] == null)) {
            return "";
        }
        return attr[n];
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setAttrState(int i, boolean s) {
        attrState[i] = s;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getAttrState(int i) {
        return(attrState[i]);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getFrom() {
        return(from);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getTo() {
        return(to);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public byte getDir() {
        return(dir);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String getColor() {
        return(color);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getLane() {
        return lane;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public float getWeight() {
        return weight;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrom(int f) {
        from = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTo(int t) {
        to = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDir(byte d) {
        dir = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setColor(String c) {
        color = c;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setLane(int l) {
        lane = l;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWeight(float w) {
        weight = w;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFilter(boolean f) {
        filter = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public boolean getFilter() {
        return(filter);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String toString() {
        return "" + from + " :: " + to + " :: " + dir + " :: " + lane + " :: " + weight + " :: " + color;
    }


}
