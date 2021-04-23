
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
// セグメントレベルでの表示をサポートする
//
// 描画範囲内の Alignment を選択する
// 基準生物種の描画位置を算出する
// 基準生物種/他方の生物種の From-To/Dir を格納する
//
// これらの情報をもとに Alignment を描画する
// これらの情報をもとに Gene/Segment は、描画対象の GeneSeg を検索する
//
public class AlignmentSegment extends Observable {
    public static final int SBJ = 0;
    public static final int QRY = 1;

    private MbgdDataMng mbgdDataMng;
    private ViewWindow  viewWin;

    private ArrayList       segPosList;        // 描画範囲内の Alignment

    ///////////////////////////////////////////////////////////////////////////
    //
    public AlignmentSegment(MbgdDataMng dataMng, ViewWindow vWin) {
        super();

        mbgdDataMng = dataMng;
        viewWin     = vWin;

        segPosList = new ArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void clear() {
        segPosList.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public ArrayList getAlignSegment() {
        return segPosList;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public int getSize() {
      return segPosList.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void alignment() {
        int from, to;
        int regStart;
        int regCenter;
        int regWidth;
        int regMax;

        // 現在の描画 Window
        regCenter = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC);
        regWidth = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        regStart = regCenter - regWidth / 2;
        regMax   = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);

        // 現在の描画範囲
        from = regCenter - regWidth / 2;
        if (from < 0) {
            from += regMax;
        }
        to   = regCenter + regWidth / 2;
        if (regMax < to) {
            to -= regMax;
        }

        //
        int zoomCount = viewWin.getZoomCount();

        // 描画範囲の Alignment を抽出
        ArrayList alignList = selectAlignmentList(from, to, regMax);
        segPosList.clear();
        for(int i = 0; i < alignList.size(); i++) {
            Alignment align = (Alignment)alignList.get(i);

            SegmentPos segPos = new SegmentPos();

            // 描画対象の Alignment
            segPos.setRegionPos(align.getFrom1(), align.getTo1(),
                                align.getFrom2(), align.getTo2(),
                                align.getDir(), align);

            // 描画位置（画面）：window width が不明なので割合
            double x1, x2;
            int f1 = align.getFrom1();
            int t1 = align.getTo1();
            int f2 = align.getFrom2();
            int t2 = align.getTo2();

            if (regStart + regWidth < f1) {
                // 0bp を挟んだデータ
                x1 = ((double)f1 - (double)regStart - (double)regMax)     / (double)regWidth;
                x2 = ((double)t1 - (double)regStart - (double)regMax + 1) / (double)regWidth;
            }
            else {
                x1 = ((double)f1 - (double)regStart)     / (double)regWidth;
                x2 = ((double)t1 - (double)regStart + 1) / (double)regWidth;
            }
            segPos.setScreenPos(x1, x2);

            segPosList.add(segPos);


            // 配列レベルの Alignment 結果が必要であるか？
            if (ViewWindow.DRAWMODE_LEV3 < zoomCount) {
                // 通常モードでの表示 ---> ダウンロードしない
            }
            else {
                // 配列レベルの Alignment 結果を download する
                mbgdDataMng.loadAlignmentSeq(f1, t1, f2, t2);

                // 描画配列を取得
                String seq1 = mbgdDataMng.getAlignmentSeq(f1, t1, f2, t2, MbgdDataMng.BASE_SPEC);
                String seq2 = mbgdDataMng.getAlignmentSeq(f1, t1, f2, t2, MbgdDataMng.OPPO_SPEC);

                // 描画範囲の部分配列
                // Gap が含まれる場合が考えられる
                //
                int idxSt, idxEd;
                if (from < to) {        // 描画領域が連続している場合
                    if (f1 < from) {    // 配列の途中から描画対象
                        idxSt = from - f1;
                        if (to < t1) {  // 配列の途中まで描画対象
                            idxEd = to - f1 + 1;
                        }
                        else {          // 配列の終わりまでが描画対象
                            idxEd = t1 - f1 + 1;
                        }
                    }
                    else {              // 配列の先頭から描画対象
                        idxSt = 0;
                        if (to < t1) {
                            idxEd = to - f1 + 1;
                        }
                        else {
                            idxEd = t1 - f1 + 1;
                        }
                    }
                }
                else {                  // 描画領域が分断している場合（0bp を挟む）
                    if (from < t1) {
                        if (f1 < from) {
                            idxSt = from - f1;
                            if (regMax < t1) {
                                idxEd = regMax - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                        else {
                            idxSt = 0;
                            if (regMax < t1) {
                                idxEd = regMax - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                    }
                    else {
                        if (f1 < 0) {
                            idxSt = 0 - f1;
                            if (to < t1) {
                                idxEd = t1 - to - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                        else {
                            idxSt = 0;
                            if (to < t1) {
                                idxEd = t1 - to - f1 + 1;
                            }
                            else {
                                idxEd = t1 - f1 + 1;
                            }
                        }
                    }
                }

                // 描画対象の部分配列
                String subseq1, subseq2;
                try {
                    subseq1 = seq1.substring(idxSt, idxEd);
                    subseq2 = seq2.substring(idxSt, idxEd);
                }
                catch (Exception e) {
                    subseq1 = "";
                    subseq2 = "";
                }
                segPos.setAlignSeq1(subseq1);
                segPos.setAlignSeq2(subseq2);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // 描画対象となる Alignment を取得
    public ArrayList selectAlignmentList(int from, int to, int regMax) {
        // 描画対象範囲内の Alignment データの取得
        ArrayList alignList0;
        ArrayList alignList1;

        //
        String spnameInit = mbgdDataMng.getSpecNameInit(MbgdDataMng.SIDE0);
        String spnameBase = mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        String best;
        if (spnameInit.equals(spnameBase)) {
            // 基準生物種は、download 時のまま
            best = Alignment.TYPE_best1;
        }
        else {
            // 基準生物種が入れ替えられた状態
            best = Alignment.TYPE_best2;
        }

        if (from < to) {
            alignList0 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, to,
                                                    Alignment.TYPE_ortholog);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, to,
                                                    best);
            alignList0.addAll(alignList1);
        }
        else {
            // 0 bp を挟む場合
            alignList0 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, regMax,
                                                    Alignment.TYPE_ortholog);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    from, regMax,
                                                    best);
            alignList0.addAll(alignList1);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    0, to,
                                                    Alignment.TYPE_ortholog);
            alignList0.addAll(alignList1);
            alignList1 = AlignmentListFilter.search(mbgdDataMng,
                                                    viewWin,
                                                    MbgdDataMng.BASE_SPEC,
                                                    0, to,
                                                    best);
            alignList0.addAll(alignList1);
        }

        return alignList0;
    }

}
