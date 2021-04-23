package cgat;

/**
 * タイトル:  cgat
 * @version 1.0
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class AlignSeq {
	protected String sp1;
	protected int pos1;
	protected boolean dir1;
	protected String seq1;
	protected int gap1;

	protected String sp2;
	protected int pos2;
	protected boolean dir2;
	protected String seq2;
	protected int gap2;

	protected String mat;

	public AlignSeq() {
		clear();
	}

	public void clear() {
		sp1 = null;
		pos1 = -1;
		dir1 = true;
		seq1 = "";
		sp2 = null;
		pos2 = -1;
		dir2 = true;
		seq2 = "";
		mat = "";
	}

	public void setSp1(String sp) {
		sp1 = sp;
	}

	public String getSp1() {
		return sp1;
	}

	public void updatePosDir1(String p) {
		int pos0 = Integer.parseInt(p);
		if (pos1 < 0) {
			// まだ配列の向きが不明、仮に正方向とする
			setPos1(pos0);
			setDir1(true);
		} else {
			if (pos1 < pos0) {
				// 配列の向き::正方向
				setDir1(true);
			} else {
				// 配列の向き::負方向
				setDir1(false);
			}
		}
	}

	public void setPos1(int p) {
		pos1 = p;
	}

	public int getPos1() {
		return pos1;
	}

	public void setDir1(boolean d) {
		dir1 = d;
	}

	public boolean getDir1() {
		return dir1;
	}

	public void updateSeq1(String s) {
		seq1 += s;
	}

	public void setSeq1(String s) {
		seq1 = s;
		updateGap1();
	}

	public String getSeq1() {
		return seq1;
	}

	public int countGap(String s) {
		int gap = 0;
		int i;

		for(i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '-') {
				gap++;
			}
		}

		return gap;
	}

	public void updateGap1() {
		setGap1(countGap(seq1));
	}

	public void setGap1(int g) {
		gap1 = g;
	}

	public int getGap1() {
		return gap1;
	}

	public void setSp2(String sp) {
		sp2 = sp;
	}

	public String getSp2() {
		return sp2;
	}

	public void updatePosDir2(String p) {
		int pos0 = Integer.parseInt(p);
		if (pos2 < 0) {
			// まだ配列の向きが不明、仮に正方向とする
			setPos2(pos0);
			setDir2(true);
		} else {
			if (pos2 < pos0) {
				// 配列の向き::正方向
				setDir2(true);
			} else {
				// 配列の向き::負方向
				setDir2(false);
			}
		}
	}

	public void setPos2(int p) {
		pos2 = p;
	}

	public int getPos2() {
		return pos2;
	}

	public void setDir2(boolean d) {
		dir2 = d;
	}

	public boolean getDir2() {
		return dir2;
	}

	public void updateSeq2(String s) {
		seq2 += s;
	}

	public void setSeq2(String s) {
		seq2 = s;
		updateGap2();
	}

	public String getSeq2() {
		return seq2;
	}

	public void updateGap2() {
		setGap2(countGap(seq2));
	}

	public void setGap2(int g) {
		gap2 = g;
	}

	public int getGap2() {
		return gap2;
	}

	public void updateMatches() {
		String s1 = getSeq1();
		String s2 = getSeq2();
		int len = s1.length();
		StringBuffer m = new StringBuffer(len);
		int i;

		for (i = 0; i < len; i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
				m.append("|");
			} else {
				m.append(" ");
			}
		}

		mat = m.toString();
	}

	public void setMatches(String m) {
		mat = m;
	}

	public String getMatches() {
		return mat;
	}

	public void reorder() {
		// sp1, sp2 の順に sp1 を正方向に sp2 を合わせ出力する
//		String sp1 = getSp1();
		int p1 = getPos1();
		boolean d1 = getDir1();
		String s1 = getSeq1();
		int g1 = getGap1();

//		String sp2 = getSp2();
		int p2 = getPos2();
		boolean d2 = getDir2();
		String s2 = getSeq2();
		int g2 = getGap2();

		String m = getMatches();

		if (!d1) {
			// sp1 が逆向き ==> sp1 を順方向に調整する
			StringBuffer sb1 = new StringBuffer(getComplementSequence(s1));
			s1 = sb1.reverse().toString();
			p1 = p1 - s1.length() + g1 + 1;
			d1 = !d1;

			StringBuffer sb2 = new StringBuffer(getComplementSequence(s2));
			s2 = sb2.reverse().toString();
			if (d2) {
				p2 = p2 + s2.length() - g2 - 1;
			} else {
				p2 = p2 - s2.length() + g2 + 1;
			}
			d2 = !d2;

			StringBuffer sbm = new StringBuffer(m);
			m = sbm.reverse().toString();

			setPos1(p1);
			setDir1(d1);
			setSeq1(s1);
			setPos2(p2);
			setDir2(d2);
			setSeq2(s2);
			setMatches(m);
		}
	}

	public void reorder(String sp) {
		String sp1 = getSp1();
		if ((sp1 != null) && (! sp1.equals(sp))) {
			exchangeAlignSeq();
		}
	}

	public boolean parse(InputStream is) {
		return parse(new InputStreamReader(is));
	}

	public boolean parse(InputStreamReader isr) {
		return parse(new BufferedReader(isr));
	}

	public boolean parse(BufferedReader br) {
        boolean sta = false;
		String line;
		Pattern patSeq = Pattern.compile("^(\\S+)\\s+(\\d+)\\s+(\\S+)");
		Pattern patNxt = Pattern.compile("^\\d+.*");
		Matcher m;

		try {
			while ((line = br.readLine()) != null) {
				m = patNxt.matcher(line);
				if (m.matches()) {
                    // 次の AlignSeq が送られてきた？
                    break;
                }

				m = patSeq.matcher(line);
				if (m.matches() && m.groupCount() == 3) {
					// 配列内容
					String spFile = m.group(1);
					String spPos = m.group(2);
					String spSeq = m.group(3);

					// 生物種名の取り出し
					String sp[] = spFile.split("\\W+"); // 英数字以外でsplit
					int lstIdx = sp.length - 1;
					if ((sp1 == null) || (sp1.equals(sp[lstIdx]))) {
						setSp1(sp[lstIdx]);
						updatePosDir1(spPos);
						updateSeq1(spSeq);
					} else if ((sp2 == null) || (sp2.equals(sp[lstIdx]))) {
						setSp2(sp[lstIdx]);
						updatePosDir2(spPos);
						updateSeq2(spSeq);
					}

                    sta = true;  // 配列データを読み込んだ
				}
			}

			//
			updateMatches();
			reorder(); // sp1 を基準に並べ替える
		} catch (Exception e) {
			clear();
		}

        return sta;
	}

	public String formatAlignSeq(String sp1, int p1, boolean d1, String s1,
			String sp2, int p2, boolean d2, String s2, String m) {
		String pad = "                    ";
		int lenSpPos = 20;
		String wkStr;
		int idx;
		int len = s1.length();
		int step = 60;
		int i;
		int s, e;

		// アライメント結果（とりあえず、配列分領域を確保）
		StringBuffer alignSeq = new StringBuffer(s1.length() * 3);
		int k1 = 1;
		if (! d1) {
			k1 = -1;
		}
		int k2 = 1;
		if (! d2) {
			k2 = -1;
		}

		// 配列に gap が含まれている場合がある。位置を考慮する必要あり。
		String w1;
		int g1 = 0;
		String w2;
		int g2 = 0;

		//
		for (i = 0; i < len; i += step) {
			s = i;
			e = i + step;
			if (len < i + step) {
				e = len;
			}

			// sp1
			wkStr = pad + sp1 + " " + (p1 + (i - g1) * k1);
			idx = wkStr.length() - lenSpPos;
			w1 = s1.substring(s, e);
			alignSeq.append(wkStr.substring(idx) + " " + w1);
			alignSeq.append("\n");

			// match
			wkStr = pad;
			idx = wkStr.length() - lenSpPos;
			alignSeq.append(wkStr.substring(idx) + " "
					+ m.substring(s, e));
			alignSeq.append("\n");

			// sp2
			wkStr = pad + sp2 + " " + (p2 + (i - g2) * k2);
			idx = wkStr.length() - lenSpPos;
			w2 = s2.substring(s, e);
			alignSeq.append(wkStr.substring(idx) + " " + w2);
			alignSeq.append("\n");

			// 改行
			alignSeq.append("\n");

			// 以降の配列位置表示のため、GAP をカウントする。
			g1 += countGap(w1);
			g2 += countGap(w2);
		}

		return alignSeq.toString();
	}

	public void exchangeAlignSeq() {
		String sp1 = getSp1();
		int p1 = getPos1();
		boolean d1 = getDir1();
		String s1 = getSeq1();
		String sp2 = getSp2();
		int p2 = getPos2();
		boolean d2 = getDir2();
		String s2 = getSeq2();
		String m = getMatches();

		setSp1(sp2);
		setPos1(p2);
		setDir1(d2);
		setSeq1(s2);
		setSp2(sp1);
		setPos2(p1);
		setDir2(d1);
		setSeq2(s1);
		setMatches(m);
		reorder();
	}

	public AlignSeq getExchangedAlignSeq() {
		String sp1 = getSp1();
		int p1 = getPos1();
		boolean d1 = getDir1();
		String s1 = getSeq1();
		String sp2 = getSp2();
		int p2 = getPos2();
		boolean d2 = getDir2();
		String s2 = getSeq2();
		String m = getMatches();

		AlignSeq newAlignSeq = new AlignSeq();
		newAlignSeq.setSp1(sp2);
		newAlignSeq.setPos1(p2);
		newAlignSeq.setDir1(d2);
		newAlignSeq.setSeq1(s2);
		newAlignSeq.setSp2(sp1);
		newAlignSeq.setPos2(p1);
		newAlignSeq.setDir2(d1);
		newAlignSeq.setSeq2(s1);
		newAlignSeq.setMatches(m);
		newAlignSeq.reorder();

		return newAlignSeq;
	}

	public String getAlignSeq() {
		String sp1 = getSp1();
		int p1 = getPos1();
		boolean d1 = getDir1();
		String s1 = getSeq1();
		String sp2 = getSp2();
		int p2 = getPos2();
		boolean d2 = getDir2();
		String s2 = getSeq2();
		String m = getMatches();

		//
		return formatAlignSeq(sp1, p1, d1, s1, sp2, p2, d2, s2, m);
	}

	public String getAlignSeq(String sp) {
		String sp1 = getSp1();
		if ((sp1 != null) && sp1.equals(sp)) {
			return getAlignSeq();
		}

		return getExchangedAlignSeq().getAlignSeq();
	}

	public String getSeq(String sp) {
		String sp1 = getSp1();
		if ((sp1 != null) && sp1.equals(sp)) {
			return getSeq1();
		}

		return getSeq2();
	}

	public String getComplementSequence(String seq) {
		StringBuffer newSeq = new StringBuffer(seq.length());
		int i;

		for(i = 0; i < seq.length(); i++) {
			char c = seq.charAt(i);
			if      (c == 'A') newSeq.append('T');
			else if (c == 'a') newSeq.append('t');
			else if (c == 'T') newSeq.append('A');
			else if (c == 't') newSeq.append('a');
			else if (c == 'G') newSeq.append('C');
			else if (c == 'g') newSeq.append('c');
			else if (c == 'C') newSeq.append('G');
			else if (c == 'c') newSeq.append('g');
			else               newSeq.append('N');
		}

		return newSeq.toString();
	}

}

