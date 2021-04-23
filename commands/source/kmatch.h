#ifndef _KMATCH_H_
#define _KMATCH_H_
#include "segments.h"

typedef enum Chain {
	DMY, DIR, REV, BOTH
} Chain;

typedef struct {
	int mindiff;
	int maxdiff;
	int mismatch;
	int mismatch2;
	int minmatchlen;
	int seqout;
	int ktup;
/**
	int bothchain;
**/
	Chain chains;
	int numrep;
	int verbose;
	double misRatio;
	char maskchar;
	double cutoff_prob;
	double cutoff_exp;
} KM_Options;
KM_Options KM_Opt;

SegmentPairSet *Kmatch(Seq *seq);
#endif
