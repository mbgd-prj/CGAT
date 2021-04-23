
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

///////////////////////////////////////////////////////////////////////////////
//
public class BaseRegion implements Cloneable {
    public static final byte DIRM = -1;         // ������
    public static final byte DIRN =  0;         // �����ʤ�
    public static final byte DIRP =  1;         // ������

    private double from;
    private double to;
    private byte dir;               // �ǡ����θ���

    ///////////////////////////////////////////////////////////////////////////
    //
    public BaseRegion(double f, double t) {
        _init(f, t);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ���ԡ����󥹥ȥ饯��
    public BaseRegion(BaseRegion reg) {
        if (reg != null) {
            _init(reg.getFrom(), reg.getTo());
        }
        else {
            _init(0, 0);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _init(double f, double t) {
        setFrom(f);
        setTo(t);
        if (f == t) {
            setDir(DIRN);
        }
        else if (f < t) {
            setDir(DIRP);
        }
        else {
            setDir(DIRM);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setFrom(double f) {
        from = f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public double getFrom() {
        return(from);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setTo(double t) {
        to = t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public double getTo() {
        return(to);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void setDir(byte d) {
        dir = d;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public byte getDir() {
        return(dir);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public String toString() {
        String str;

        str = getClass().getName() + '@' + getFrom() + '-' + getTo();

        return(str);
    }

}
