package cgat;

/**
 * �����ȥ�:  cgat
 * @version 1.0
 */

///////////////////////////////////////////////////////////////////////////////
//
public class Dbg {
    protected static int dbgLevel = 0;

    ///////////////////////////////////////////////////////////////////////////
    //
    public Dbg() {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void setDbgLevel(int level) {
        dbgLevel = level;
        Dbg.println(1, "Set Debug Level :: " + dbgLevel);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static int getDbgLevel() {
        return(dbgLevel);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void print(int l, String str) {
        if (l <= dbgLevel) {
            // �ǥХå���å�����ɽ��
            System.err.print(str);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void println(int l, String str) {
        if (l <= dbgLevel) {
            // �ǥХå���å�����ɽ��
            System.err.println(str);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static void printStackTrace(int l, Throwable t) {
        if (l <= dbgLevel) {
            t.printStackTrace();
        }
    }

}

