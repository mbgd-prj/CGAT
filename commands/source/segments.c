#include <stdio.h>
#include <strings.h>
#include <stdlib.h>
#include <ctype.h>
#include <math.h>
#include "segments.h"
#include "seqset.h"

double lbinom();
static initSegSize = 1000;
int printCharSeq(Segment *, signed char );

SegmentPairSet *createSegmentPairSet()
{
	SegmentPairSet *seg;
	if ((seg = (SegmentPairSet *) malloc(sizeof(SegmentPairSet)))== NULL){
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	if ((seg->segpair = (SegmentPair *)
			calloc(initSegSize,sizeof(SegmentPair)))== NULL){
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	seg->segSize = initSegSize;
	seg->segidx1 = seg->segidx2 = NULL;
	seg->cnt = 0;
	return seg;
}
/*
setSeqInSegPairSet(SegmentPairSet *seg, SeqSet *seqset)
{
	seg->seqset = seqset;
}
*/
addSegment(SegmentPairSet *seg, Seq *seq1, int from1, int to1,
		Seq *seq2, int from2, int to2, int dir)
{
	if (seg->cnt >= seg->segSize) {
		seg->segSize *= 1.5;
		if ((seg->segpair = (SegmentPair *)
			realloc(seg->segpair,
				seg->segSize * sizeof(SegmentPair))) == NULL) {
			fprintf(stderr, "Can't allocate memory\n");
			exit(1);
		}
	}
	seg->segpair[seg->cnt].seg1.seq = seq1;
	seg->segpair[seg->cnt].seg1.from = from1;
	seg->segpair[seg->cnt].seg1.to = to1;
	seg->segpair[seg->cnt].seg2.seq = seq2;
	seg->segpair[seg->cnt].seg2.from = from2;
	seg->segpair[seg->cnt].seg2.to = to2;
	seg->segpair[seg->cnt].dir = dir;
	seg->cnt++;
}
cmprSegPair(SegmentPair *a, SegmentPair *b)
{
	if (a->seg1.from == b->seg1.from) {
		return a->seg2.from - b->seg2.from;
	} else {
		return a->seg1.from - b->seg1.from;
	}
}
cmprSeg(Segment *a, Segment *b)
{
	return a->from - b->from;
}
createSegIndex(SegmentPairSet *seg)
{
	int i, j, k;
	SegIdx *segidx1;
	if ( (seg->segidx1 = malloc(seg->cnt * sizeof(SegIdx))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ( (seg->segidx2 = malloc(seg->cnt * sizeof(SegIdx))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	for (i = j = k = 0; i < seg->cnt; i++) {
		seg->segidx1[j].type = 'f';
		seg->segidx1[j].seg = j;
		seg->segidx1[j].pos = seg->segpair[i].seg1.from;
		j++;
		seg->segidx1[j].type = 't';
		seg->segidx1[j].seg = j;
		seg->segidx1[j].pos = seg->segpair[i].seg1.to;
		j++;

		seg->segidx2[k].type = 'f';
		seg->segidx2[k].seg = k;
		seg->segidx2[k].pos = seg->segpair[i].seg2.from;
		k++;

		seg->segidx2[k].type = 't';
		seg->segidx2[k].seg = k;
		seg->segidx2[k].pos = seg->segpair[i].seg2.to;
		k++;
	}
	qsort(seg->segidx1, seg->cnt*2, sizeof(SegIdx), (int (*)(const void *, const void *)) cmprSeg);
	qsort(seg->segidx2, seg->cnt*2, sizeof(SegIdx), (int (*)(const void *, const void *)) cmprSeg);
}
sortSegment(SegmentPairSet *seg)
{
	qsort(seg->segpair, seg->cnt, sizeof(SegmentPair), (int (*)(const void *, const void *)) cmprSegPair);
}
ovlpcheck(SegmentPairSet *seg)
{
	createSegIndex(seg);
}

printSegmentSet(SegmentPairSet *seg, int format)
{
	int i;
	sortSegment(seg);
	for (i = 0 ; i < seg->cnt; i++) {
		switch (format) {
		case 1:
			printSegmentSeq(&(seg->segpair[i]));
			break;
		default:
			printSegment(&(seg->segpair[i]));
			break;
		}
	}
}
printSegmentOLD(SegmentPair *segpair)
{
	printf("%d %d %d %d %d\n",
		segpair->seg1.from+1, segpair->seg1.to+1,
		segpair->seg2.from+1, segpair->seg2.to+1,
		segpair->dir);
}
printSegment(SegmentPair *segpair)
{
	printf("%s %d %d %s %d %d %d\n",
		segpair->seg1.seq->tit,
		segpair->seg1.from+1, segpair->seg1.to+1,
		segpair->seg2.seq->tit,
		segpair->seg2.from+1, segpair->seg2.to+1,
		segpair->dir);
}
printSegmentSeq(SegmentPair *segpair)
{
	printSegment(segpair);
	printCharSeq(&(segpair->seg1), (signed char) 1);
	putchar('\n');
	printCharSeq(&(segpair->seg2), segpair->dir);
	putchar('\n');
}
printCharSeq(Segment *seg, signed char dir)
{
	int i;
	char *seq = seg->seq->seq;
	Alphabet *alpha = seg->seq->alpha;
	if (dir > 0) {
		for (i = seg->from; i <= seg->to; i++) {
			putchar(seq[i]);
		}
	} else {
		for (i = seg->to; i >= seg->from; i--) {
			putchar(alpha->chars[alpha->comple[alpha->idx[seq[i]]]]);
		}
	}
}
freeSegmentPairSet(SegmentPairSet *seg)
{
	free(seg->segpair);
	free(seg);
}

cal_segmatch_prob(SegmentPair *segpair, SegMatchRes *matchres,
			double space, int cnttype)
{
	int i, j;
	char *seq1 = segpair->seg1.seq->seq;
	char *seq2 = segpair->seg2.seq->seq;
	int len, matchnum;
	Alphabet *alpha = segpair->seg1.seq->alpha;
	int alphanum = alpha->origto - alpha->origfrom + 1;
	int *acnt, totcnt;
	double *aprob, matchprob;

	if (space <= 0.0) {
		space = 1.0;
	}
	if (cnttype != 1) {
		cnttype = 2;
	}

	len = segpair->seg1.to - segpair->seg1.from + 1;

	if ((acnt = (int*) malloc(alphanum*sizeof(int)))==NULL) {
		fprintf(stderr, "Can't alloc memory\n"); exit(1);
	}
	if ((aprob = (double*) malloc(alphanum*sizeof(double)))==NULL) {
		fprintf(stderr, "Can't alloc memory\n"); exit(1);
	}
	for (i = alpha->origfrom; i <= alpha->origto; i++) {
		acnt[i] = 0; aprob[i] = 0.0;
	}

	matchnum = 0;
	if (segpair->dir > 0) {
		for (i = segpair->seg1.from, j = segpair->seg2.from;
				i <= segpair->seg1.to; i++, j++) {
			if (cnttype == 2) {
				acnt[alpha->idx[seq1[i]]]++;
				acnt[alpha->idx[seq2[j]]]++;
			}
			if (seq1[i] == seq2[j]) {
				matchnum++;
			}
		}
	} else {
		for (i = segpair->seg1.from, j = segpair->seg2.to;
				i <= segpair->seg1.to; i++, j--) {
			if (cnttype == 2) {
				acnt[alpha->idx[seq1[i]]]++;
				acnt[alpha->comple[alpha->idx[seq2[j]]]]++;
			}
			if (alpha->idx[seq1[i]] ==
					alpha->comple[alpha->idx[seq2[j]]]) {
				matchnum++;
			}
		}
	}
	if (cnttype == 1) {
		for (i = segpair->seg1.from; i <= segpair->seg2.to; i++) {
			acnt[alpha->idx[seq1[i]]]++;
			acnt[alpha->comple[alpha->idx[seq2[i]]]]++;
		}
	}
	totcnt = 0; matchprob = 0.0;
	for (i = alpha->origfrom; i <= alpha->origto; i++) {
		totcnt += acnt[i];
	}
	for (i = alpha->origfrom; i <= alpha->origto; i++) {
		aprob[i] = (double) acnt[i] / totcnt;
	}
	for (i = alpha->origfrom; i <= alpha->origto; i++) {
		matchprob += aprob[i] * aprob[i];
	}
	if (matchprob >= 0.999999) {
		matchprob = 0.999999;
	}
	free(acnt); free(aprob);
	matchres->prob = exp(lbinom(len, matchnum, matchprob) + log(space));
	matchres->ident = (double) matchnum / len;
}
double lbinom(int n, int m, double p)
{
	double lprob, lprob0;
	int i, k;
	double t1, t2, t3;
	static double logi[1000];
	static flag = 0;
	if (! flag) {
		for (i = 1; i < 1000; i++) {
			logi[i] = log(i);
		}
		flag = 1;
	}

	t3 = 0.0;
	for (i = 1; i <= n; i++) {
		t3 += logi[i];
	}
	lprob = -100.0;
	for (k = m; k <= n; k++) {
		t1 = 0.0, t2 = 0.0;
		for (i = 1; i <= n; i++) {
			if (i <= k) {
				t1 += logi[i];
			}
			if (i <= n-k) {
				t2 += logi[i];
			}
		}
		lprob0 = log(p) * k + log(1-p) * (n-k) + (t3 - t1 - t2);
		lprob += log(1 + exp(lprob0 - lprob));
	}
	return lprob;
}
