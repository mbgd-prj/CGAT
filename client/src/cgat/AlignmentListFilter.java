package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// AlignmentList から、ある条件にマッチする Alignment を抽出する
public class AlignmentListFilter {

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentListFilter() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // 基準生物種の中心表示の Alignment-data を探し
    // それに対応する spec2 の Alignment-data を見つける
    // sp2 の alignment データを中心に表示する際の sp2 の表示中央位置を計算する
    // Alignment データが見つからなかった場合、sp2 の表示領域は、引数 lr により変化する
    public static int serarchPosOppositeSpec(MbgdDataMng dataMng, ViewWindow viewWin, boolean lr) {
//	Alignment ali = searchAlignOppositeSpec(dataMng, viewWin, null);
	Alignment ali = searchAlignOppositeSpec(dataMng, viewWin, null, true);
	return getPosFromAlign(ali, dataMng, viewWin, lr);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Alignment searchAlignOppositeSpecBack(MbgdDataMng dataMng, ViewWindow viewWin, ArrayList currentAlignmentArrayList) {
        int regCenter1, regCenter2;
        int regStart1, regWidth1, regMax1;
        int regMax2;
        Alignment align;
        int i;

        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart1 = regCenter1 - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

        // Region 描画範囲内の、基準生物種の Alignment データを選択
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1);

        int loopMax = alignList.length;
        Alignment alignCenter = null;         // 中心とする Alignment
        double minDiffOrth = Double.MAX_VALUE;  // Ortholog データの候補
        Alignment alignCenterOrth = null;
        double minDiffBest = Double.MAX_VALUE;  // Best Hit データの候補
        Alignment alignCenterBest = null;
        for(i = 0; i < loopMax; i++) {
            align = alignList[i];
            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 対象外
                continue;
            }

            // 表示範囲内の Alignment データをチェック
            Iterator it = currentAlignmentArrayList.iterator();
            while(it.hasNext()) {
                Alignment currentAlignment = (Alignment)it.next();
                if (align.equals(currentAlignment)) {
                    // Always choose the current alignment when available
                    alignCenter = align;
                    break;
                }
            }
            if (alignCenter != null) {
                break;
            }

            // 表示位置の中心
            double cPos1, cPos2;
            cPos1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            cPos2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);

            // 対象と align との距離
            double diffFrom1 = Math.abs(align.getFrom1() - cPos1);
            double diffTo1   = Math.abs(align.getTo1()   - cPos1);
            double diffFrom2 = Math.abs(align.getFrom2() - cPos2);
            double diffTo2   = Math.abs(align.getTo2()   - cPos2);
            double diff1 = diffTo1;
            if (diffFrom1 < diffTo1) {
                diff1 = diffFrom1;
            }
            double diff2 = diffTo2;
            if (diffFrom2 < diffTo2) {
                diff2 = diffFrom2;
            }
            
            double diff = diff1 * diff1 + diff2 * diff2;
            if (align.getType().equalsIgnoreCase(Alignment.TYPE_ortholog)) {
                if (diff < minDiffOrth) {
                    // より中心に近い Alignment データ
                    minDiffOrth = diff;
                    alignCenterOrth = align;
                }
            }
            else {
                if (diff < minDiffBest) {
                    // より中心に近い Alignment データ
                    minDiffBest = diff;
                    alignCenterBest = align;
                }
            }

        }

        if (alignCenter == null) {
            if (alignCenterOrth != null) {
                // 最も近い Ortholog を採用
                alignCenter = alignCenterOrth;
            }
            else if (alignCenterBest != null) {
                // 最も近い Best hit を採用
                alignCenter = alignCenterBest;
            }
        }

        return alignCenter;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Alignment searchAlignOppositeSpec(MbgdDataMng dataMng, ViewWindow viewWin) {
        int regCenter1, regCenter2;
        int regStart1, regWidth1, regMax1;
        int regMax2;
        Alignment align;
        int i;

        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart1 = regCenter1 - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);

        // Region 描画範囲内の、基準生物種の Alignment データを選択
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1);

        int loopMax = alignList.length;
        Alignment alignCenter = null;         // 中心とする Alignment
        double minDiffOrth = Double.MAX_VALUE;  // Ortholog データの候補
        Alignment alignCenterOrth = null;
        double minDiffBest = Double.MAX_VALUE;  // Best Hit データの候補
        Alignment alignCenterBest = null;
        for(i = 0; i < loopMax; i++) {
            align = alignList[i];
            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 対象外
                continue;
            }

            // 表示位置の中心
            double cPos1, cPos2;
            cPos1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
            cPos2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);

            // 対象と align との距離
            double diffFrom1 = Math.abs(align.getFrom1() - cPos1);
            double diffTo1   = Math.abs(align.getTo1()   - cPos1);
            double diffFrom2 = Math.abs(align.getFrom2() - cPos2);
            double diffTo2   = Math.abs(align.getTo2()   - cPos2);
            double diff1 = diffTo1;
            if (diffFrom1 < diffTo1) {
                diff1 = diffFrom1;
            }
            double diff2 = diffTo2;
            if (diffFrom2 < diffTo2) {
                diff2 = diffFrom2;
            }
            
            double diff = diff1 * diff1 + diff2 * diff2;
            if (align.getType().equalsIgnoreCase(Alignment.TYPE_ortholog)) {
                if ((align.getFrom1() <= cPos1) && (cPos1 <= align.getTo1())) {
                    alignCenter = align;
                    break;
                }
                if (diff < minDiffOrth) {
                    // より中心に近い Alignment データ
                    minDiffOrth = diff;
                    alignCenterOrth = align;
                }
            }
            else {
                if (diff < minDiffBest) {
                    // より中心に近い Alignment データ
                    minDiffBest = diff;
                    alignCenterBest = align;
                }
            }

        }

        if (alignCenter == null) {
            if (alignCenterOrth != null) {
                // 最も近い Ortholog を採用
                alignCenter = alignCenterOrth;
            }
            else if (alignCenterBest != null) {
                // 最も近い Best hit を採用
                alignCenter = alignCenterBest;
            }
        }

        return alignCenter;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static Alignment searchAlignOppositeSpec(MbgdDataMng dataMng,
                                                    ViewWindow viewWin,
                                                    Alignment prevAlign,
                                                    boolean lr) {
        Alignment alignCenter = null;
        int regCenter1, regCenter2;
        int regStart1, regWidth1, regMax1;
        int regStart2, regWidth2, regMax2;
        Alignment align;
        int i;

        // 基準生物種検索範囲
        regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regWidth1 *= 0.05;
        if (regWidth1 < 30) {
            regWidth1 = 30;
        }
        regStart1 = regCenter1 - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        if (regStart1 < 0) {
            regStart1 += regMax1;
        }

        //
        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
        regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int moveSp2 = regWidth2 / 2;
        if (prevAlign != null) {
            double len1 = prevAlign.getTo1() - prevAlign.getFrom1();
            double len2 = prevAlign.getTo2() - prevAlign.getFrom2();
            moveSp2 = (int)((double)moveSp2 * len2 / len1);
        }
        if (lr) {
            regCenter2 += moveSp2;
            regCenter2 %= regMax2;
        }
        else {
            regCenter2 -= moveSp2;
            if (regCenter2 < 0) {
                regCenter2 += regMax2;
            }
        }

        int w = regWidth2 / 2;
        regStart2 = regCenter2 - w;
        if (regStart2 < 0) {
            regStart2 += regMax2;
        }

        // Region 描画範囲内の、基準生物種の Alignment データを選択
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1,
                                                        regStart2, w * 2, regMax2);


        double minDist = 999999;
        double maxAlignScore = 0;
        double minAlignDist = 99999;
        int loopMax = alignList.length;
        for(i = 0; i < loopMax; i++) {
            align = alignList[i];
            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 対象外
                continue;
            }

            if (prevAlign != null) {
                if (prevAlign.isSameAlignment(align)) {
                    return align;
                }
                if (prevAlign.getDir() != align.getDir()) {
                    // 向きが違う
                    continue;
                }

                if (lr) {
                    // 右に移動した場合
                    if ((w < Math.abs(prevAlign.getTo1() - align.getFrom1())) ||
                        (w < Math.abs(prevAlign.getTo2() - align.getFrom2()))) {
                        // 離れている alignment である
                        continue;
                    }
                }
                else {
                    // 左に移動した場合
                    if ((w < Math.abs(prevAlign.getFrom1() - align.getTo1())) ||
                        (w < Math.abs(prevAlign.getFrom2() - align.getTo2()))) {
                        // 離れている alignment である
                        continue;
                    }
                }

                // align と描画中心(仮)との距離
                double m = (align.getTo2() - align.getFrom2()) / (align.getTo1() - align.getFrom1());
                double n = align.getFrom1() - m * align.getFrom2();
                double dist = Math.abs(regCenter2 - m * regCenter1 - n) / Math.sqrt(1 + m * m);
                if (maxAlignScore < align.getScore()) {
                    alignCenter = align; 
                    maxAlignScore = align.getScore();
                    minAlignDist = dist;
                }
                else if (maxAlignScore == align.getScore()) {
                    if (dist < minAlignDist) {
                        alignCenter = align; 
                        minAlignDist = dist;
                    }
                }
            }
        }

        if (alignCenter == null) {
            // 隣接アライメント無し
            alignList = dataMng.selectAlignList(regStart1, regWidth1, regMax1);
            Alignment alignOrtholog = null;
            double maxOrthologScore = 0;
            double minOrthologDist = 99999;
            Alignment alignNotOrtholog = null;
            double maxNotOrthologScore = 0;
            double minNotOrthologDist = 99999;
            loopMax = alignList.length;
            for(i = 0; i < loopMax; i++) {
                align = alignList[i];
                if (! align.getFilter()) {
                    // フィルタリングされた alignment データ ----> 対象外
                    continue;
                }

                double m = (align.getTo2() - align.getFrom2()) / (align.getTo1() - align.getFrom1());
                double n = align.getFrom1() - m * align.getFrom2();
                double dist = Math.abs(regCenter2 - m * regCenter1 - n) / Math.sqrt(1 + m * m);
                if (align.getType().equals(Alignment.TYPE_ortholog)) {
                    if (maxOrthologScore < align.getScore()) {
                        alignOrtholog = align;
                        maxOrthologScore = align.getScore();
                        minOrthologDist = dist;
                    }
                    else if (maxOrthologScore == align.getScore()) {
                        if (dist < minOrthologDist) {
                            alignOrtholog = align;
                            minOrthologDist = dist;
                        }
                    }
                }
                else {
                    if (maxNotOrthologScore < align.getScore()) {
                        alignNotOrtholog = align;
                        maxNotOrthologScore = align.getScore();
                        minNotOrthologDist = dist;
                    }
                    else if (maxNotOrthologScore == align.getScore()) {
                        if (dist < minNotOrthologDist) {
                            alignNotOrtholog = align;
                            minNotOrthologDist = dist;
                        }
                    }
                }
            }
            if (alignOrtholog != null) {
                alignCenter = alignOrtholog;
            }
            else if (alignNotOrtholog != null) {
                alignCenter = alignNotOrtholog;
            }
            else {
//Dbg.println(1, "not found");
            }
        }


        // 表示画面中心と判断された alignment
        return alignCenter;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public static int getPosFromAlign(Alignment alignCenter, MbgdDataMng dataMng, ViewWindow viewWin, boolean lr) {
        int regCenter1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        int regCenter2;
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        if (alignCenter == null) {
            // 該当する Alignment が見つからなかった
            // 現在の描画方向をもとに、規定値(WIDTH/2)だけ移動する
            boolean dir2 = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);
            regCenter2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC);
            if (dir2) {
                if (lr) {
                    regCenter2 += regWidth1 / 2;
                }
                else {
                    regCenter2 -= regWidth1 / 2;
                }
            }
            else {
                if (lr) {
                    regCenter2 += regWidth1 / 2;
                }
                else {
                    regCenter2 -= regWidth1 / 2;
                }
            }
            return regCenter2;
        }

        //
        int from1 = alignCenter.getFrom1();
        int to1   = alignCenter.getTo1();
        int from2 = alignCenter.getFrom2();
        int to2   = alignCenter.getTo2();
        byte dir  = alignCenter.getDir();

        //
        // Genome 描画の向き
        boolean regDir1 = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);
        if (dir == Alignment.DIR_DIR) {
            regCenter2 = (from2 + to2) / 2 + (regCenter1 - (from1 + to1) / 2);
            double r = (double)(regCenter1 - from1) / (double)(to1 - from1);
            regCenter2 = from2 + (int)((to2 - from2) * r);
            if (regDir1) {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, true);
            }
            else {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, false);
            }
        }
        else {
            regCenter2 = (from2 + to2) / 2 - (regCenter1 - (from1 + to1) / 2);
            double r = (double)(regCenter1 - from1) / (double)(to1 - from1);
            regCenter2 = to2 - (int)((to2 - from2) * r);
            if (regDir1) {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, false);
            }
            else {
                viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, true);
            }
        }

        if (regCenter2 < 0) {
            regCenter2 += regMax2;
        }
        else if (regMax2 <= regCenter2) {
            regCenter2 %= regMax2;
        }

        return regCenter2;
    }

    ///////////////////////////////////////////////////////////////////////////
    // spec１ の中心表示の Alignment-data を決定し
    // それに対応する spec2 の Alignment-data を探す
    // sp2 の alignment データを中心に表示する際の sp2 の開始位置を計算する
    public static Alignment serarchSp2Pos(MbgdDataMng dataMng, ViewWindow viewWin) {
        int regStart1, regWidth1, regMax1;
        int regMax2;
        boolean sp1Dir, sp2Dir;

        regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        regMax1 = dataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        sp1Dir  = viewWin.getRegDir(MbgdDataMng.BASE_SPEC);

        regMax2 = dataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        sp2Dir  = viewWin.getRegDir(MbgdDataMng.OPPO_SPEC);

        // 表示対象領域内にある Alignment データを選択する
//        ArrayList dispAlignList = selectDispAlignList(dataMng, viewWin, regStart1, regWidth1, regMax1);
        Alignment alignList[] = dataMng.selectAlignList(regStart1, regWidth1, regMax1);

        // 画面中心に表示するデータの判定
        int centerPos = regStart1 + regWidth1 / 2;
        centerPos %= regMax1;
        if (centerPos < 0) {
            centerPos += regMax1;
        }

        Alignment centerObj = null;     //
        int minDist = 9999999;          //
        int dist;
        int from1, to1;
        int from2, to2;
        int dir;

        int loopMax = alignList.length;
        for(int i = 0; i < loopMax; i++) {
            Alignment align = alignList[i];
            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 対象外
                continue;
            }

            String type  = align.getType();
            if (type.equals(Alignment.TYPE_ortholog) != true) {
                // orthologue 以外のデータは、Skip
                continue;
            }

            // MbgdDataMng からデータを取得した時点で、from1/to1 は基準生物種の情報
            from1 = align.getFrom1();
            to1   = align.getTo1();

            // 表示中心との距離を求める
            if (centerPos < from1) {
                dist = centerPos + regMax1 - (from1 + to1) / 2;
            }
            else if (to1 < centerPos) {
                dist = regMax1 - centerPos + (from1 + to1) / 2;
            }
            else {
                dist = Math.abs(centerPos - ((from1 + to1) / 2 + regMax1));
            }

            if (dist < minDist) {
                // より中心に近いデータを発見
                minDist = dist;
                centerObj = align;
            }
        }

        return(centerObj);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 与えられた range 内に存在する alignment の一覧を作成する
    public static ArrayList search(MbgdDataMng dataMng, ViewWindow viewWin,
                                    boolean basespec, int from, int to) {
        ArrayList dispAlignList = new ArrayList();

        // 検索
        int loopMax = dataMng.getAlignmentSize();
        for(int i = 0; i < loopMax; i++) {
            Alignment align = dataMng.getAlignment(basespec, i);
            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 対象外
                continue;
            }

            int f, t;

            f = align.getFrom1();
            t = align.getTo1();

            if (t < f) {
                int w = f;
                f = t;
                t = w;
            }

            // 領域が重なるかを判定
            if ((from <= f) && (t <= to)) {
                dispAlignList.add(align);
            }
            else if ((f <= from) && (from   <= t)) {
                dispAlignList.add(align);
            }
        }

        return(dispAlignList);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 与えられた range 内に存在する alignment の一覧を作成する
    // ただし、対象となるデータ種別を指定可能とする
    public static ArrayList search(MbgdDataMng dataMng, ViewWindow viewWin,
                                    boolean basespec, int from, int to,
                                    String type) {
        ArrayList dispAlignList = new ArrayList();

        // 検索
        int loopMax = dataMng.getAlignmentSize();
        for(int i = 0; i < loopMax; i++) {
            Alignment align = dataMng.getAlignment(basespec, i);
            if (! align.getFilter()) {
                // フィルタリングされた alignment データ ----> 対象外
                continue;
            }

            int f, t;

            if (! align.getType().equalsIgnoreCase(type)) {
                // 指定されたデータ種別ではない
                continue;
            }

            f = align.getFrom1();
            t = align.getTo1();

            if (t < f) {
                int w = f;
                f = t;
                t = w;
            }

            // 領域が重なるかを判定
            if ((from <= f) && (f <= to)) {
                dispAlignList.add(align);
            }
            else if ((from <= t) && (t <= to)) {
                dispAlignList.add(align);
            }
            else if ((f <= from) && (from   <= t)) {
                dispAlignList.add(align);
            }
        }

        return(dispAlignList);
    }

}

