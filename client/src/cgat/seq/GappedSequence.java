package cgat.seq;

class GappedSequence extends RawSequence {
	char GAP_SYMBOL = '-';
	int [] gaps;
	GappedSequence(String name, String seq) {
		setName(name);
		setSeqString(seq);
	}
	public void setGapSymbol(char gap) {
		GAP_SYMBOL = gap;
	}
	public void makeGapCntTab() {
		int i;
		int gapcnt = 0;
		int length = length();
		gaps = new int[length];
		for (i = 0; i < length; i++) {
			if (charAt(i)==GAP_SYMBOL) {
				++gapcnt;
			}
			gaps[i] = gapcnt;
		}
	}
	public int getOrigPos(int i) {
		return (i - gaps[i]);
	}
}
