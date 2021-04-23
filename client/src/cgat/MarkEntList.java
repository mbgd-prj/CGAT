
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////////////
// Search ORF �Ǹ�������������ݻ����뤿��� class
public class MarkEntList {
    private HashMap   markEntHash;

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEntList() {
        markEntHash = new HashMap();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clearMark() {
        // �����Ǥ���
        markEntHash.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addMark(String s, String n, int f, int t, int d, int ct, Color col) {
        // Ʊ�� Pos �Υǡ�������
        delMark(n);

        // �ǡ�������Ͽ
        String name = n.toUpperCase();      // ��ʸ�����Ѵ�
        MarkEnt ent = new MarkEnt(s, name, f, t, d, ct, col);
        markEntHash.put(name, ent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void addMark(MarkEnt e) {
        String n = e.getName();

        // Ʊ�� Pos �Υǡ�������
        delMark(n);

        // �ǡ�������Ͽ
        String name = n.toUpperCase();      // ��ʸ�����Ѵ�
        MarkEnt ent = new MarkEnt(e);
        markEntHash.put(name, ent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void delMark(String n) {
        String name = n.toUpperCase();      // ��ʸ�����Ѵ�
        markEntHash.remove(name);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEnt getMark(String n) {
        try {
            MarkEnt ent = (MarkEnt)markEntHash.get(n);

            return new MarkEnt(ent);
        }
        catch (Exception e) {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public MarkEnt[] getMarkAll() {
        MarkEnt entList[];
        Object entListWk[];
        int loopMax;

        Collection collect = markEntHash.values();
        if (collect.size() == 0) {
            return new MarkEnt[0];
        }
        entListWk = collect.toArray();

        loopMax = entListWk.length;
        entList = new MarkEnt[loopMax];
        for(int i = 0; i < loopMax; i++) {
            // �ͤ�ʣ�����֤�
            entList[i] = new MarkEnt((MarkEnt)entListWk[i]);
        }

        return entList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int size() {
        return(markEntHash.size());
    }

}
