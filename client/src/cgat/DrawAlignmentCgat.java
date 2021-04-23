
/**
 * タイトル:     cgat<p>
 * @version 1.0
 */
package cgat;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////////////
//
public class DrawAlignmentCgat extends DrawAlignment {
    protected JPopupMenu popup;

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignmentCgat(MbgdDataMng dataMng, ViewWindow vWin) {
        super(dataMng, vWin);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public DrawAlignmentCgat(MbgdDataMng dataMng, ViewWindow vWin, int w, int h) {
        super(dataMng, vWin, w, h);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseClicked(MouseEvent e) {
        int mod = e.getModifiers();
        if ((mod & MouseEvent.BUTTON1_MASK) != 0) {
            // left クリック
            displayClickedAlignment(e);
        }
        if ((mod & MouseEvent.BUTTON2_MASK) != 0) {
            // center クリック
        }
        if ((mod & MouseEvent.BUTTON3_MASK) != 0) {
            // right クリック
            popUpMenu(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseEntered(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseExited(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mousePressed(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void mouseReleased(MouseEvent e) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // クリックされた Alignment ペアを探し出し、そのペアを中心に表示する
    public void displayClickedAlignment(MouseEvent e) {
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int regStart1 = viewWin.getRegCenter(MbgdDataMng.BASE_SPEC) - regWidth1 / 2;
        int regMax1 = mbgdDataMng.getGenomeLength(MbgdDataMng.BASE_SPEC);
        int regWidth2 = viewWin.getRegWidth(MbgdDataMng.OPPO_SPEC);
        int regStart2 = viewWin.getRegCenter(MbgdDataMng.OPPO_SPEC) - regWidth2 / 2;
        int regMax2 = mbgdDataMng.getGenomeLength(MbgdDataMng.OPPO_SPEC);
        int minDistPairIdx;
        int from, to;
        int from1, to1;
        int from2, to2;
        int dir;
        if (regStart1 <= 0) {
            regStart1 += regMax1;
        }
        if (regStart2 <= 0) {
            regStart2 += regMax2;
        }

        // 表示 window サイズ
        int winWidth = getWindowWidth();
        int winHeight = getWindowHeight();

        // クリック位置
        int x = e.getX();
        int y = e.getY();

        // クリック位置に最も近い alignment データを探す
        minDistPairIdx = getClickedAlignment(x, y);
        if (minDistPairIdx < 0) {
            // データが見つからなかった
            Dbg.println(1, "DBG :: not found clicked align");
            return;
        }
        Dbg.println(1, "DBG :: Clicked Alignment index :: " + minDistPairIdx);

        // この alignment データを中心に表示
        Alignment align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, minDistPairIdx);
        from1 = align.getFrom1();
        to1   = align.getTo1();
        from2 = align.getFrom2();
        to2   = align.getTo2();
        dir   = align.getDir();

        // クリック位置を中心に描画する
        int sp1Reg = (from1 + to1) / 2;
        if (sp1Reg <= 0) {
            sp1Reg += regMax1;
        }

        int sp2Reg = (from2 + to2) / 2;
        if (sp2Reg <= 0) {
            sp2Reg += regMax2;
        }

        // Alignment データの向きを合わせて表示する
        viewWin.setRegDir(MbgdDataMng.BASE_SPEC, true);
        if (dir == 1) {
            viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, true);
        }
        else {
            viewWin.setRegDir(MbgdDataMng.OPPO_SPEC, false);
        }

        viewWin.viewPos(sp1Reg, sp2Reg, align);

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public void popUpMenu(MouseEvent e) {
		boolean isPopupMenu = false;
        JMenuItem jMenuItem;
        DispAlignmentSequenceCommand cmd1;
        DispAlignmentListCommand cmd2;

        // Popup Menu
        popup = new JPopupMenu();

		//
		float ratio = seqLenPerPixel(viewWin);
        int regWidth1 = viewWin.getRegWidth(MbgdDataMng.BASE_SPEC);
        int maxReAlignLength = Integer.parseInt(mbgdDataMng.getProperty(MbgdDataMng.OPT_AL_MAX_REALIGN));
		if (regWidth1 <= maxReAlignLength) {
			jMenuItem = new JMenuItem("View Re-Alignment");
			if (ratio <= 1) {     // 既にあるアライメント結果を利用
				DispReAlignmentSequenceCommand cmd3;
				cmd3 = new DispReAlignmentSequenceCommand(mbgdDataMng, viewWin);
				jMenuItem.addActionListener(cmd3);
			}
			else {
				ExecReAlignmentSequenceCommand cmd3;
				cmd3 = new ExecReAlignmentSequenceCommand(mbgdDataMng, viewWin);
				jMenuItem.addActionListener(cmd3);
			}
			popup.add(jMenuItem);
			isPopupMenu = true;
		}

        // クリック位置
        int x = e.getX();
        int y = e.getY();

        // クリック位置に最も近い alignment データを探す
        int minDistPairIdx = getClickedAlignment(x, y);

if (0 <= minDistPairIdx) {
        isPopupMenu = true;

        // クリックされた alignment データ
        Alignment align = mbgdDataMng.getAlignment(MbgdDataMng.BASE_SPEC, minDistPairIdx);
        String sp1Name = mbgdDataMng.getSpecName(MbgdDataMng.BASE_SPEC);
        String sp2Name = mbgdDataMng.getSpecName(MbgdDataMng.OPPO_SPEC);
        int from1 = align.getFrom1();
        int to1   = align.getTo1();
        int from2 = align.getFrom2();
        int to2   = align.getTo2();
        int dir   = align.getDir();

        // メニューアイテムの設定：アライメント配列表示
        jMenuItem = new JMenuItem("View Alignment");
        cmd1 = new DispAlignmentSequenceCommand(mbgdDataMng);
        cmd1.setRegion1(sp1Name, from1, to1);
        cmd1.setRegion2(sp2Name, from2, to2);
        cmd1.setDirection(dir);
        jMenuItem.addActionListener(cmd1);
        popup.add(jMenuItem);

		// ident をポップアップメニューに表示
		jMenuItem = new JMenuItem("Ident = " + align.getIdent());
		popup.add(jMenuItem);

        //
        jMenuItem = new JMenuItem("Color Legend");
        MessageWindow cmd4 = new MessageWindow();
        if (mbgdDataMng.getAlignColorMode() == 0) {
            String url = mbgdDataMng.getBasePath() + "cgi-bin/helpColorTab.cgi?type=align";
            cmd4.setContents(url);
        }
        else {
            String html = mbgdDataMng.getColorLegend();
            cmd4.setContents("text/html", html);
        }
        jMenuItem.addActionListener(cmd4);
        popup.add(jMenuItem);
}

        // メニュ選択時の処理
        if (isPopupMenu) {
            popup.setVisible(true);
            popup.show(e.getComponent(), x, y);
        }

    }

}
