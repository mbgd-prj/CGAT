package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ZoomLevel {
    private static ZoomLevel _instance = null;

    private ArrayList zoomTab;

    ///////////////////////////////////////////////////////////////////////////
    //
    public static ZoomLevel Instance() {
        if (_instance == null) {
            _instance = new ZoomLevel();
        }

        return _instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private ZoomLevel() {
        _initTable();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    private void _initTable() {
        int lev = 0;

        //
        zoomTab = new ArrayList();

        ///////////////////////////////////////////////////////////////////////
        // 特に sort は行いません。
        // データ格納順は、小 ---> 大 となるようにしてください。
        // 【注意】範囲に重なりがないようにしてください

        // Level 0
        zoomTab.add(new ZoomRange(lev++,          0,         50));
        zoomTab.add(new ZoomRange(lev++,         50,        100));
        zoomTab.add(new ZoomRange(lev++,        100,        200));
        zoomTab.add(new ZoomRange(lev++,        200,        300));
        zoomTab.add(new ZoomRange(lev++,        300,        500));
        zoomTab.add(new ZoomRange(lev++,        500,       1000));
        zoomTab.add(new ZoomRange(lev++,       1000,       2000));
        zoomTab.add(new ZoomRange(lev++,       2000,       3000));
        zoomTab.add(new ZoomRange(lev++,       3000,       5000));
        zoomTab.add(new ZoomRange(lev++,       5000,      10000));
        // Level 10
        zoomTab.add(new ZoomRange(lev++,      10000,      20000));
        zoomTab.add(new ZoomRange(lev++,      20000,      30000));
        zoomTab.add(new ZoomRange(lev++,      30000,      50000));
        zoomTab.add(new ZoomRange(lev++,      50000,     100000));
        zoomTab.add(new ZoomRange(lev++,     100000,     200000));
        zoomTab.add(new ZoomRange(lev++,     200000,     300000));
        zoomTab.add(new ZoomRange(lev++,     300000,     500000));
        zoomTab.add(new ZoomRange(lev++,     500000,    1000000));
        zoomTab.add(new ZoomRange(lev++,    1000000,    2000000));
        zoomTab.add(new ZoomRange(lev++,    2000000,    3000000));
        // Level 20
        zoomTab.add(new ZoomRange(lev++,    3000000,    5000000));
        zoomTab.add(new ZoomRange(lev++,    5000000,   10000000));
        zoomTab.add(new ZoomRange(lev++,   10000000,   20000000));
        zoomTab.add(new ZoomRange(lev++,   20000000,   30000000));
        zoomTab.add(new ZoomRange(lev++,   30000000,   50000000));
        zoomTab.add(new ZoomRange(lev++,   50000000,  100000000));
        zoomTab.add(new ZoomRange(lev++,  100000000,  200000000));
        zoomTab.add(new ZoomRange(lev++,  200000000,  300000000));
        zoomTab.add(new ZoomRange(lev++,  300000000,  500000000));
        zoomTab.add(new ZoomRange(lev++,  500000000, 1000000000));
        // Level 20
        zoomTab.add(new ZoomRange(lev++, 1000000000, 2000000000));

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getLevel(int width) {
        int loopMax = zoomTab.size();
        for(int i = 0; i < loopMax; i++) {
            ZoomRange range = (ZoomRange)zoomTab.get(i);
            if (width <= range.getTo()) {
                return range.getLevel();
            }
        }

        return Integer.MAX_VALUE;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRangeByLevel(int level) {
        ZoomRange range;

        if (zoomTab.size() <= level) {
            return Integer.MAX_VALUE;
        }

        if (level <= 0) {
            level = 0;
        }

        range = (ZoomRange)zoomTab.get(level);
        return range.getTo();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getRangeByWidth(int width) {
        int loopMax = zoomTab.size();
        for(int i = 0; i < loopMax; i++) {
            ZoomRange range = (ZoomRange)zoomTab.get(i);
            if (width <= range.getTo()) {
                return range.getTo();
            }
        }

        return Integer.MAX_VALUE;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class ZoomRange {
        private int rangeLevel;
        private int rangeFrom;
        private int rangeTo;

        ///////////////////////////////////////////////////////////////////////
        //
        public ZoomRange(int level, int from, int to) {
            rangeLevel  = level;
            rangeFrom   = from;
            rangeTo     = to;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public int getLevel() {
            return rangeLevel;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public int getFrom() {
            return rangeFrom;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public int getTo() {
            return rangeTo;
        }

    }

}
