#ifndef _SEGMENTS_H
#define _SEGMENTS_H
#include "alphabet.h"
#include "seqset.h"

typedef struct Segment {
	Seq *seq;
        int from, to, dir;
} Segment;

typedef struct SegmentPair {
        Segment seg1, seg2;
	signed char dir;
} SegmentPair;

typedef struct SegIdx {
	int pos;
	char type;
	int seg;
} SegIdx;

typedef struct SegMatchRes {
	double prob;
	double ident;
} SegMatchRes;

typedef struct SegmentPairSet {
        SegmentPair *segpair;
        int segSize;
        int cnt;
	SegIdx *segidx1, *segidx2;
	SeqSet *seqset;
} SegmentPairSet;

SegmentPairSet *createSegmentPairSet();
#endif
