#include <stdio.h>
#include <strings.h>
#include <stdlib.h>
#include "kmatch.h"
#include "alphabet.h"
#include "segments.h"
#include "db.h"
#define MAXMIS 100

unsigned int *intseq;
unsigned int *firstocc, *prevocc, *complwd;
unsigned int *lnlist, *revlist;
unsigned int Mask, WDnum;
int alphaNum;
typedef struct Diag {
	int iprev;
	int idtlen;
	int prevmch;
} Diag;
Diag *diag;
Alphabet *alpha;

#ifdef KMATCH_MAIN

char *seq;
char file[100];
int seqsiz = 50000;
char buf[BUFSIZ];

DB *db;
SeqSet *seqset;

main(int argc, char **argv)
{
	int i, j;
	SegMatchRes matchres;
	double prob, exp;
	int print_flag;
	SegmentPairSet *seg;
	Alphabet *alpha;

	Kmatch_optinit();

	getargs(argc, argv);

	if (! (db = dbopen(file))) {
		fprintf(stderr, "Can't open file\n");
		exit(1);
	}
	alpha = create_dna2();
	Kmatch_init(alpha);

	seqset = read_seqset_alpha(db, 20000000, alpha);
	for (i = 0; i < seqset->seqnum; i++) {
		seg = Kmatch(&seqset->seq[i]);
		for (j = 0; j < seg->cnt; j++) {
			SegmentPair *segpair = &(seg->segpair[j]);
			if (KM_Opt.cutoff_prob) {
				cal_segmatch_prob(segpair, &matchres, 1.0, 2);
				if (prob >= KM_Opt.cutoff_prob) {
					continue;
				}
				exp = prob * (double) strlen(seqset->seq[i].seq)
					* KM_Opt.maxdiff;
			}
			if (KM_Opt.seqout) {
				printSegmentSeq(segpair);
			} else {
				printSegment(segpair);
			}
			if (KM_Opt.cutoff_prob) {
				printf("Pr: %.2lg, Exp: %.2lg\n", prob, exp);
			}
		}
		freeSegmentPairSet(seg);
	}
	return(0);
}

getargs(argc, argv)
	int argc;
	char **argv;
{
	int i;
	for (i = 1; i < argc; i++) {
		if (*argv[i] == '-') {
			switch (*++argv[i]) {
			case 'k':
				KM_Opt.ktup = atoi(++argv[i]);
				break;
			case 'M':
				KM_Opt.mismatch = atoi(++argv[i]);
				break;
			case 'm':
				KM_Opt.mismatch2 = atoi(++argv[i]);
				break;
			case 'L':
				KM_Opt.minmatchlen = atoi(++argv[i]);
				break;
			case 'R':
				KM_Opt.misRatio = atof(++argv[i]);
				break;
			case 'D':
				KM_Opt.maxdiff = atoi(++argv[i]);
				break;
			case 'd':
				KM_Opt.mindiff = atoi(++argv[i]);
				break;
			case 'S':
				KM_Opt.seqout = 1;
				break;
			case 'P':
				KM_Opt.cutoff_prob = atof(++argv[i]);
				break;
			case 'E':
				KM_Opt.cutoff_exp = atof(++argv[i]);
				break;
			case 'r':
				KM_Opt.numrep = atoi(++argv[i]);
				break;
			case 'C':
				KM_Opt.chains = REV;
				break;
			case 'X':
				KM_Opt.maskchar = *++argv[i];
				break;
			case 'b':
				KM_Opt.chains |= REV;
				break;
			case 'v':
				KM_Opt.verbose = 1;
				break;
			}
		} else {
			strcpy(file, argv[i]);
		}
	}
}

#endif

Kmatch_init(Alphabet *alpha0)
{
	int i;
	alpha = alpha0;
	alphaNum = alpha->origto - alpha->origfrom + 2;

	if (! KM_Opt.mismatch) {
		KM_Opt.mismatch = KM_Opt.misRatio * KM_Opt.minmatchlen;
	}
	if (! KM_Opt.ktup) {
		KM_Opt.ktup = (int) ((KM_Opt.minmatchlen - KM_Opt.mismatch) / (KM_Opt.mismatch + 1));
		if (KM_Opt.ktup > 8) {
			KM_Opt.ktup = 8;
		} else if (KM_Opt.ktup < 3) {
			KM_Opt.ktup = 3;
		}
	}
	if (! KM_Opt.mismatch2) {
		KM_Opt.mismatch2 = KM_Opt.mismatch;
	}
	if (KM_Opt.verbose) {
		fprintf(stderr, "mismatch: %d\n", KM_Opt.mismatch);
		fprintf(stderr, "mismatch2: %d\n", KM_Opt.mismatch2);
		fprintf(stderr, "ktup: %d\n", KM_Opt.ktup);
	}

	for (i = 0, WDnum = 1; i < KM_Opt.ktup; i++) {
	 	WDnum *= alphaNum;
	}
	Mask = WDnum / alphaNum;
	if (KM_Opt.chains & REV) {
		create_comple();
	}
}

Kmatch_optinit()
{
	KM_Opt.maxdiff = 2000;
	KM_Opt.mismatch = 0;
	KM_Opt.minmatchlen = 30;
	KM_Opt.seqout = 0;
	KM_Opt.chains = DIR;
	KM_Opt.numrep = 0;
	KM_Opt.verbose = 0;
	KM_Opt.misRatio = 0.1;
	KM_Opt.maskchar = 'N';
	KM_Opt.cutoff_prob = 0.0;
}

SegmentPairSet *Kmatch(Seq *seq)
{
	int i;
	SegmentPairSet *segset;

	segset = createSegmentPairSet();
/*
	setSeqInSegPairSet(segset, seqset);
*/
	if (! KM_Opt.maxdiff) {
		KM_Opt.maxdiff = strlen(seq->seq);
	}
	createHash(seq);
	if (KM_Opt.chains & DIR) {
		diagCheck(seq, segset);
	}
	if (KM_Opt.chains & REV) {
		diagCheckRev(seq, segset);
	}
	destroyHash();
	return segset;
}

createHash(Seq *Seq)
{
	static char tmpsq[20];
	int i, j;
	int id = 0;
	int compid;
	int skip = 0;
	char *seq = Seq->seq;
	int seqlen = strlen(seq);

	if (! firstocc) {
		if ((firstocc = (unsigned int *) malloc(WDnum *
				sizeof(unsigned int))) == NULL) {
			fprintf(stderr, "Can't allocate memory\n");
			exit(1);
		}
	}
	if (! prevocc) {
		if ((prevocc = (unsigned int *) malloc(WDnum *
			sizeof(unsigned int))) == NULL) {
			fprintf(stderr, "Can't allocate memory\n");
			exit(1);
		}
	}
	for (i = 0; i < WDnum; i++) {
		firstocc[i] = prevocc[i] = 0;
	}

	if ((intseq = (unsigned int *) malloc(seqlen * sizeof(unsigned int))) == NULL) {
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	if ((lnlist = (unsigned int *) malloc((seqlen+1) * sizeof(unsigned int))) == NULL) {
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	for (i = 0; i <= seqlen; i++) {
		lnlist[i] = 0;
	}
	if (KM_Opt.chains & REV) {
		if ((revlist = (unsigned int *)
			malloc((seqlen+1) * sizeof(unsigned int))) == NULL) {
			fprintf(stderr, "Can't allocate memory\n");
			exit(1);
		}
		for (i = 0; i <= seqlen; i++) {
			revlist[i] = 0;
		}
	}
	for (i = 0; i < seqlen; i++) {
		id %= Mask;
		id *= alphaNum;
		id += alpha->idx[seq[i]] - alpha->origfrom;
		intseq[i] = id;

		if (skip) {
			if (++skip > KM_Opt.ktup) {
				skip = 0;
			}
		}
		if (KM_Opt.maskchar && seq[i] == KM_Opt.maskchar) {
			skip = 1;
		}
		if (skip) {
			if (firstocc[id] == 0)
				firstocc[id] = prevocc[id] = i + 1;
			continue;
		}

		if (KM_Opt.chains & REV) {
			/* palindromic word should not match itself */
			if (prevocc[complwd[id]] &&
					! revlist[prevocc[complwd[id]]]) {
				revlist[prevocc[complwd[id]]] = i + 1;
			}
		}
		if (firstocc[id] == 0) {
			firstocc[id] = prevocc[id] = i + 1;
		} else {
			lnlist[prevocc[id]] = i + 1;
			prevocc[id] = i + 1;
		}
	}
	if (KM_Opt.chains & REV) {
		for (i = 0; i < seqlen; i++) {
			if (! revlist[i]) {
				for (j = lnlist[i]; j && ! revlist[j];
					j = lnlist[j]) {
				}
				if (j) {
					revlist[i] = revlist[j];
				} else {
				}
			}
		}
	}
}
destroyHash()
{
	free(intseq);
	free(lnlist);
	if (KM_Opt.chains & REV) {
		free(revlist);
	}
}

printHash() {
	int i, pos;
	static char tmpsq[20];
	for (i = 0; i < WDnum; i++) {
		id2wd(i,KM_Opt.ktup,tmpsq);
		printf("%d  %s  ", i, tmpsq);
		for (pos = firstocc[i]; pos > 0; pos = lnlist[pos+1]) {
			printf(" %d", pos);
		}
		putchar('\n');
	}
}
diagCheck(Seq *Seq, SegmentPairSet *seg)
{
	int i, j, id, dg;
	int fr1, fr2, mchlen;
	int flagFound;
	char tmpsq[20];
	int Lemb;
	Diag *diag;
	char *seq = Seq->seq;
	int seqlen = strlen(seq);

	if ((diag = (Diag *) calloc(KM_Opt.maxdiff+1, sizeof(Diag))) == NULL) {
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	for (i = KM_Opt.ktup-1; i < seqlen; i++) {
		id = intseq[i];
		flagFound = 0;
		for (j = lnlist[i+1]; j > 0 && j < i + KM_Opt.maxdiff; j = lnlist[j]) {
		  /* ktup words { seq[i-ktup+1 .. i], seq[j-ktup .. j-1] } */
			dg = j - (i+1);
			if (dg < 0 || dg > KM_Opt.maxdiff) {
				printf("?? %d,%d,%d\n", i, j, dg);
				id2wd(id,KM_Opt.ktup,tmpsq);
/*
				fprintf(stderr, "%s %d %d %d\n", tmpsq, i, j, lnlist[i+1]);
*/
			}
			if (j - (i+1) + 1 < KM_Opt.mindiff) {
				continue;
			}
			if (seq[i+1] == seq[j]) {
				diag[dg].idtlen++; /* idt block extension */
				Lemb = 0;
				continue;
			} else {
				Lemb = 1;	/* left embedded */
			}
			if (i <= diag[dg].prevmch){
				/* the region has already been reported */
				Lemb = 0;
				diag[dg].idtlen = 0;
				continue;
			}
			mchlen = 0;
		/** at least two ktup words are included in the region */
			if ( (diag[dg].iprev <= i - KM_Opt.ktup
					&& diag[dg].iprev > i - KM_Opt.minmatchlen) 
				  || (diag[dg].idtlen >= KM_Opt.ktup)
				  || (KM_Opt.ktup + diag[dg].idtlen >= KM_Opt.minmatchlen) ) {
				mchlen = diag[dg].idtlen  + KM_Opt.ktup;
				fr1 = i-mchlen+1; fr2 = j -mchlen;
				cmprseq(Seq, i+1, j, KM_Opt.ktup + diag[dg].idtlen,
					&fr1, &fr2, &mchlen, 1);
				if (fr2 - (fr1+mchlen-1) - 1 >= KM_Opt.mindiff
				    && mchlen >= KM_Opt.minmatchlen) {
					addSegment(seg, Seq, fr1,fr1+mchlen-1,
						Seq, fr2,fr2+mchlen-1, 1);
					flagFound = 1;
					diag[dg].prevmch = fr1+mchlen-1;
				}
			}
			diag[dg].idtlen = 0;
			diag[dg].iprev = i;
		}
	}
	free(diag);
}

diagCheckRev(Seq *Seq, SegmentPairSet *seg)
{
	int i, j, id, dg;
	int fr1, fr2, mchlen;
	int flagFound;
	char tmpsq[20];
	int Lemb;
	int maxdg = 100000;
	char *seq = Seq->seq;
	int seqlen = strlen(seq);

	if ((diag = (Diag *) calloc(maxdg, sizeof(Diag))) == NULL) {
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	for (i = KM_Opt.ktup-1; i < seqlen; i++) {
		id = intseq[i];
		flagFound = 0;
		for (j = revlist[i+1]; j > 0 && j < i + KM_Opt.maxdiff; j = lnlist[j]){

		  /* ktup words { seq[i-ktup+1 .. i], seq[j-ktup .. j-1] } */
			dg = (j + (i+1)) % maxdg;
			if (dg < 0 || dg > maxdg) {
				printf("?? %d,%d,%d\n", i, j, dg);
				id2wd(id,KM_Opt.ktup,tmpsq);
				fprintf(stderr, "%s %d %d %d\n", tmpsq, i, j, lnlist[i+1]);
			}
			if (j - (i+1) + 1 < KM_Opt.mindiff) {
				continue;
			}
			if (seq[i+1] == alpha->compleNuc[seq[j-KM_Opt.ktup-1]] &&
					i+1 < j-KM_Opt.ktup-1) {
				diag[dg].idtlen++; /* idt block extension */
				Lemb = 0;

				continue;
			} else {
				Lemb = 1;	/* left embedded */
			}
			if (i <= diag[dg].prevmch){
				/* the region has been already reported */
				Lemb = 0;
				diag[dg].idtlen = 0;
				continue;
			}
			if ( (diag[dg].iprev <= i - KM_Opt.ktup
				&& diag[dg].iprev > i - KM_Opt.minmatchlen) 
				  || (diag[dg].idtlen >= KM_Opt.ktup)
				  || (KM_Opt.ktup + diag[dg].idtlen >= KM_Opt.minmatchlen) ) {

				mchlen = diag[dg].idtlen  + KM_Opt.ktup;
				fr1 = i-mchlen+1; fr2 = j + diag[dg].idtlen -1;
				cmprseq(Seq, i+1, j + diag[dg].idtlen,
					KM_Opt.ktup + diag[dg].idtlen,
					&fr1, &fr2, &mchlen, -1);
				if ((fr2-(mchlen-1)) - (fr1+mchlen-1) - 1
						>= KM_Opt.mindiff
				    && mchlen >= KM_Opt.minmatchlen) {
					fr2 -= (mchlen - 1);
					addSegment(seg, Seq,fr1,fr1+mchlen-1,
						Seq,fr2,fr2+mchlen-1, -1);
					flagFound = 1;
					diag[dg].prevmch = fr1+mchlen-1;
				}
			} else {
			}
			diag[dg].idtlen = 0;
			diag[dg].iprev = i;
		}
	}
	free(diag);
}


/* pos1 (pos2) is an index of the next char of the identical word (WWWW)*/
/*    xxxx WWWW Pxxxx    */
/*              ^        */
/* idtlen = length(WWWW) */

cmprseq(Seq *Seq, int pos1, int pos2, int idtlen,
	int *fr1, int *fr2, int *mchlen, int dir)
{
	int i;
	int misL = 0, misR = 0, p1 = pos1, p2 = pos2, len = 0;
	static int tmpMchPosR[MAXMIS];
	static int tmpMchPosL[MAXMIS];
	int maxmisR = 0;
	char *seq = Seq->seq;

	for (i = 0; i < MAXMIS; i++) {
		tmpMchPosL[i] = tmpMchPosR[i] = -1;
	}

	if (dir < 0) {
		p2 = pos2 - idtlen - 1;
	}

	while (misR <= KM_Opt.mismatch2) {
		if ( ((dir > 0 && seq[p1] != seq[p2]) ||
			      (dir < 0 && seq[p1] != alpha->compleNuc[seq[p2]]))
			|| (KM_Opt.maskchar &&
			      (seq[p1] == KM_Opt.maskchar
				|| seq[p2] == KM_Opt.maskchar)) ){
			tmpMchPosR[misR+1] = tmpMchPosR[misR];
			misR++;
		} else {
			tmpMchPosR[misR] = len;
		}
		p1++; p2+=dir; len++;
		if (p1 > p2) break;
	}

/* both p1 and p2 point to the previous char of the identical words */
/*    xxxP WWWW xxxxx */

	p1 = pos1 - idtlen - 1;
	if (dir > 0) {
		p2 = pos2 - idtlen - 1;
	} else {
		p2 = pos2;
	}

	len = 0;
	while (misL <= KM_Opt.mismatch2) {
		if ( ((dir > 0 && seq[p1] != seq[p2]) ||
			    (dir < 0 && seq[p1] != alpha->compleNuc[seq[p2]])) 
			|| (KM_Opt.maskchar &&
			    (seq[p1] == KM_Opt.maskchar
				|| seq[p2] == KM_Opt.maskchar))) {
		    int misR, totlen;
/* misL + misR <= KM_Opt.mismatch */
		    for (misR = KM_Opt.mismatch2 - misL; misR >= 0; misR--) {
			totlen = (tmpMchPosL[misL]+1) +
				idtlen + (tmpMchPosR[misR] + 1);
			if ( totlen < KM_Opt.minmatchlen) {
				break;
			}
			if (! KM_Opt.misRatio ||
			    misL + misR <= (int)(totlen * KM_Opt.misRatio) ) {
				*fr1 = pos1 - idtlen - 1 - tmpMchPosL[misL];
				if (dir > 0) {
				    *fr2 = pos2 - idtlen - 1 - tmpMchPosL[misL];
				} else {
				    *fr2 = pos2 + tmpMchPosL[misL];
				}
				if (maxmisR < misR) {
					maxmisR = misR;
				}
				*mchlen = (tmpMchPosL[misL] + 1) + idtlen +
					(tmpMchPosR[maxmisR] + 1);
/*				
				*mchlen = totlen;
*/
				break;
			}
		    }
		    tmpMchPosL[misL+1] = tmpMchPosL[misL];
		    misL++;
		} else {
			tmpMchPosL[misL] = len;
		}
		p1--; p2-=dir; len++;
		if (p1<0) break;
		if (p1 > p2) break;
	}
}


/** for debug 
printseq(char *seq, int from, int len)
{
	register int i;
	for (i = from; i < from+len; i++) {
		putchar(seq[i]);
	}
}
printseqrev(char *seq, int from, int len)
{
	register int i;
	for (i = from+len-1; i >= from; i--) {
		putchar(alpha->compleNuc[seq[i]]);
	}
}
 end of for debug */


wd2id(s, len)
	char *s;
	int len;
{
	int i;
	int id = 0;
	char S[12];
	for (i = 0; i < len; i++){
		if (alpha->idx[s[i]] < alpha->origfrom ||
			alpha->idx[s[i]] > alpha->origto) return -1;
		id *= alphaNum;
		id += alpha->idx[s[i]] - alpha->origfrom;
	}
	return id;
}
id2wd(id, len, s)
	int id;
	int len;
	char *s;
{
	register int l;
	for (l = len - 1; l >= 0; l--) { 
		s[l] = alpha->chars[(id % alphaNum) + alpha->origfrom];
		id /= alphaNum;
	}
	s[len] = '\0';
}


create_comple()
{
	int i;
	if ((complwd = (unsigned int *)
			calloc(WDnum, sizeof(unsigned int))) == NULL) {
		fprintf(stderr, "Can't allocate memory\n");
		exit(1);
	}
	for (i = 0; i < WDnum; i++) {
		complwd[i] = complement(i, KM_Opt.ktup);
	}
}
complement(id, len)
	int id;
	int len;
{
	int i, j;
	int comp = 0;
	while (len--) {
		i = id % alphaNum;
		comp *= alphaNum;
		comp += (alpha->comple[i+1]-1);

		id /= alphaNum;
	}
	return comp;
}
