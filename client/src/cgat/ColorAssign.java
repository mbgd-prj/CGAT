
/**
 * �����ȥ�:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ColorAssign {
    HashMap hashColorTab;       // entId �˳�����Ƥ�������
    HashMap countEntId;         // entId �νи�����򥫥����

    // Color ��ư�������
    Color autoAssignColor[] = { Color.red,
                                Color.blue,
                                Color.green,
                                Color.yellow,
                                Color.cyan,
                                Color.orange,
                                Color.darkGray,
                                Color.gray,
                                Color.lightGray,
                                Color.magenta,
                                Color.pink };
    int autoAssignPattern[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };


    ///////////////////////////////////////////////////////////////////////////
    //
    public ColorAssign() {
        hashColorTab = new HashMap();
        countEntId   = new HashMap();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void add(ColorTabEnt ent) {
        // EntId �ǹ�®����
        hashColorTab.put(ent.getEntId(), ent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Color getColor(String entId) {
        ColorTabEnt colTabEnt = (ColorTabEnt)hashColorTab.get(entId);

        return(colTabEnt.getColor());
    }

/*
    ///////////////////////////////////////////////////////////////////////////
    //
    public int getPattern(String entId) {
        ColorTabEnt colTabEnt = (ColorTabEnt)hashColorTab.get(entId);

        return(colTabEnt.getPattern());
    }
*/

    ///////////////////////////////////////////////////////////////////////////
    // ̾�Τνи�����򥫥����
    // seg �ǡ�����̾�Τ��Ȥ˿����꿶�뤿��Ρ���������
    public void countColorType(String entId) {
        try {
            //
            NewColorEnt newColorEnt = (NewColorEnt)countEntId.get(entId);
            newColorEnt.countUp();
        }
        catch (Exception e) {
            //
            countEntId.put(entId, new NewColorEnt(entId));
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // �����������󤵤�Ƥ��ʤ� Ent ID �����¿���Ȥ��Ƥ����Τ����˿��򥢥����󤹤�
    public void assignColor() {
        NewColorEnt entList[];
        int idx;

        //
        entList = new NewColorEnt[countEntId.size()];
        Collection c = countEntId.values();

        idx = 0;
        for(Iterator i = c.iterator(); i.hasNext();) {
            entList[idx++] = (NewColorEnt)i.next();
        }

        // ������¤٤�
        Arrays.sort(entList, new CompNewColorEnt());

        int idxColor   = 0;
//        int idxPattern = 0;
        for(int i = entList.length - 1; 0 <= i; i--) {
            // �����꿶��
            NewColorEnt ent = entList[i];
            String entId = ent.getEntId();

            add(new ColorTabEnt(ent.getEntId(),
                                autoAssignColor[idxColor % autoAssignColor.length],
//                                autoAssignPattern[idxPattern % autoAssignPattern.length]));
""));
            //
            idxColor++;
            if (idxColor == autoAssignColor.length) {
//                idxPattern++;
//                if (idxPattern == autoAssignPattern.length) {
//                    idxPattern = 0;
//                }
                idxColor = 0;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    class NewColorEnt {
        String entId;
        int    count;

        ///////////////////////////////////////////////////////////////////////
        //
        public NewColorEnt(String e) {
            entId = e;
            count = 1;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public void countUp() {
            count++;
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public int getCount() {
            return(count);
        }

        ///////////////////////////////////////////////////////////////////////
        //
        public String getEntId() {
            return(entId);
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // NewColorEnt ���饹�򾺽�˥����Ȥ��뤿�����Ӵؿ�
    class CompNewColorEnt implements Comparator {
        public int compare(Object objA, Object objB) {
            NewColorEnt a = (NewColorEnt)objA;
            NewColorEnt b = (NewColorEnt)objB;

            return(a.getCount() - b.getCount());
        }
    }

}
