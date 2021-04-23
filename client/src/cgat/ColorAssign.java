
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.util.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////////////
//
public class ColorAssign {
    HashMap hashColorTab;       // entId に割り当てた色情報
    HashMap countEntId;         // entId の出現回数をカウント

    // Color 自動割り当て
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
        // EntId で高速検索
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
    // 名称の出現回数をカウント
    // seg データの名称ごとに色を割り振るための［前処理］
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
    // 色がアサインされていない Ent ID の内で多く使われているものから順に色をアサインする
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

        // 昇順に並べる
        Arrays.sort(entList, new CompNewColorEnt());

        int idxColor   = 0;
//        int idxPattern = 0;
        for(int i = entList.length - 1; 0 <= i; i--) {
            // 色を割り振る
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
    // NewColorEnt クラスを昇順にソートするための比較関数
    class CompNewColorEnt implements Comparator {
        public int compare(Object objA, Object objB) {
            NewColorEnt a = (NewColorEnt)objA;
            NewColorEnt b = (NewColorEnt)objB;

            return(a.getCount() - b.getCount());
        }
    }

}
