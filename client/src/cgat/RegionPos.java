
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

///////////////////////////////////////////////////////////////////////////////
// Region �β��̾�Ǥΰ��־����
public class RegionPos implements Cloneable {
    private BaseRegion regPos;      // Region ��Ǥΰ���
    private BaseRegion winPos;      // ���̾�Ǥΰ���

    ///////////////////////////////////////////////////////////////////////////
    //
    public RegionPos() {
        _init();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ���ԡ����󥹥ȥ饯��
    public RegionPos(RegionPos reg) {
        BaseRegion br;

        br = reg.getRegPos();
        if (br != null) {
            setRegPos(new BaseRegion(br));
        }

        br = reg.getWinPos();
        if (br != null) {
            setWinPos(new BaseRegion(br));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init() {
        regPos = new BaseRegion(null);
        winPos = new BaseRegion(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Object clone() {
        RegionPos obj = new RegionPos(this);

        return obj;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegPos(BaseRegion pos) {
        regPos = pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setRegPos(int f, int t) {
        regPos = new BaseRegion(f, t);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseRegion getRegPos() {
        return new BaseRegion(regPos);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWinPos(double f, double t) {
        winPos = new BaseRegion(f, t);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setWinPos(BaseRegion pos) {
        winPos = pos;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseRegion getWinPos() {
        return new BaseRegion(winPos);
    }

}
