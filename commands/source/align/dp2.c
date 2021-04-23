#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <math.h>
#include "db.h"
#include "matrix.h"
#include "pam.h"
#include "dp.h"

/**************************************/
/*  dynamic programming alignment routine
/**************************************/

static Score *weight;
static Score *gweight1, *gweight3;
static char *path;
static int *gpath;
static int *gpath1, *gpath3;
static int msize;
/*
static char align_seq1[MAXI*2];
static char align_seq2[MAXJ*2];
*/
/** following global variables are used in mpos(i,j) macro **/
static int winwid;
static int wsz;
static int iofst, jofst;


typedef struct MinPos {
	Score score;
	int from1, to1;
	int from2, to2;
} MinPos;


match(char *ent1, char *seq1, char *ent2, char *seq2,
		MakeAliInfo *aliInfo, Alignment *align_res)
{
	Score score;
	Score score250;
	int i,j;
	int len1,len2;
	int alilen;
	int pamerr;
	int estpam, mlpam, maxpam;
	double exppam, sdpam;
	int matchnum;
	char *align_seq1, *align_seq2;


	len1 = strlen(seq1);
	len2 = strlen(seq2);

	if (maxdiffrate) {
	    if  (
		(len1 >= len2 && len1 - len2 > (double) len2 * maxdiffrate) ||
		(len2 > len1 && len2 - len1 > (double) len1 * maxdiffrate)) {
				return 0;
	    }
	}
	wsz = -1;
#ifdef XXX
	if (len1 == len2 && strcmp(seq1,seq2) == 0) {
		/*** Completely Identical !!! ***/
		wsz = 0;
	} else
#endif
	if (winsizerate) {
		wsz = (len1 > len2 ? len1 : len2) * winsizerate;
		if (wsz < 0) {
			wsz = 1;
		}
	} else if (winsize) {
		wsz = winsize;
	}
	if (wsz>=0) {
		/* used in mpos(i,j) macro */
		iofst = wsz + (len1 > len2 ? len1-len2 : 0) + 1;
		jofst = wsz + (len1 > len2 ? 0: len2-len1) + 1;
		winwid = iofst + jofst + 1;
	} else {
		wsz = -1;
		winwid = len2 + 1;
		iofst = jofst = 0;
	}

	if (wsz >= 0 || iofst > 0 || jofst > 0) {
		fprintf(stderr, "EE>>>>>>> %d,%d,%d\n",wsz,iofst,jofst);
	}

	alignInit(aliInfo, seq1, ent1, seq2, ent2);
	aliResInit(aliInfo, align_res);

	if (! aliInfo->lsdp) {
		if (mem_alloc(len1, winwid) < 0) {
			return 0;
		}
	}

	if (calc_pam) {
		set_matrix_from_allpam(aliInfo->allpam, initpam,
			aliInfo->wm, amino, tr);
		if (aliInfo->gapscale) {
			get_pamgap(initpam, &gap, &extgap);
			aliInfo->gap.init = gap;
			aliInfo->gap.ext = extgap;
		}
	}

	if (verbose_flag) {
		printf("gapscore=%.2lf,%.2lf\n",gap,extgap);
	}

	DoAlign(aliInfo,seq1,len1,seq2,len2,align_res);
	matchnum = align_res->match; alilen = align_res->alilen;
	align_seq1 = align_res->aliseq1; align_seq2 = align_res->aliseq2;
	score250 = score = align_res->score;

	if (verbose_flag) {
		printf("init_ident=%.2lf\n", (double)matchnum *100/ alilen);
	}

	if (calc_pam) {
		int pam0 = initpam;
		Score score2;

		alilen = strlen(align_seq1);

		if (verbose_flag) {
			printf("\npam=%d score=%d\n",initpam,(int)score);
		}

		do {
			score2 = score;
			pamerr=calpam(aliInfo->allpam, align_seq1, align_seq2, alilen, &maxpam, &exppam, &sdpam);
			if (calc_pam & CALC_PAM_EXPPAM) {
				mlpam = (int) rint(exppam);
			} else {
				mlpam = maxpam;
			}
			if (calc_pam & CALC_PAM_ONEITER) {
				/* no itration */
				break;
			}
/*
			if ( (double) (pam0 - mlpam) / pam0 <= pam_stop ) {
				break;
			}
*/
			if (pam0 == mlpam) break;
			pam0 = mlpam;
			set_matrix_from_allpam(aliInfo->allpam, mlpam,
					aliInfo->wm, amino, tr);
			if (aliInfo->gapscale) {
				get_pamgap(mlpam, &gap, &extgap);
				aliInfo->gap.init = gap;
				aliInfo->gap.ext = extgap;
if (verbose_flag == 1) {
	printf("gap=%.1f,%.1f\n",(float)gap,(float)extgap);
}
			}

			DoAlign(aliInfo,seq1,len1,seq2,len2,align_res);
			matchnum = align_res->match; alilen = align_res->alilen;
			score250 = score = align_res->score;

if (verbose_flag) {
	printf("pam=%d score=%.1f,score2=%.1f\n",mlpam,(float)score,(float)score2);
}
			if (score2 - score > 0.001) {
				fprintf(stderr, "***** ERROR!!! ***** %s %s %.1f %.1f\n",ent1,ent2,(float)score2,(float)score);
abort();
			}
		} while (score2 < score);
	}

	if (BETTER(threshold, score)) {
		/* BAD SCORE. SKIP !! */
		return 0;
	}

	/* add pam distance to the alignment result */
	if (calc_pam) {
		align_res->pamerr = pamerr;
		align_res->pam = mlpam;
		align_res->exppam = exppam;
		align_res->sdpam = sdpam;
/*
		align_res->origscore = (int) score250;
*/
		align_res->origscore = score250;
	}
	return 1;
}

DoAlign(MakeAliInfo *aliInfo, char *seq1, int len1, char *seq2, int len2,
		Alignment *aliRes)
{
	if (aliInfo->lsdp) {
		/** linear-space DP **/
		LSDP_DoAlign(aliInfo, aliRes);
	} else {
		DP_DoAlign(seq1,len1,seq2,len2,aliRes);
	}
}
DP_DoAlign(seq1,len1,seq2,len2,ali)
	char *seq1,*seq2;
	int len1,len2;
	Alignment *ali;
{
	MinPos minw;
	int alilen, matchnum;
	Score score;


	score = calscore(seq1,len1,seq2,len2,&minw);
    	make_align(seq1, len1, seq2, len2, ali->aliseq1, ali->aliseq2,
			&alilen, &matchnum, &minw);

	ali->len1 = len1; ali->len2 = len2;
	ali->from1 = minw.from1; ali->to1 = minw.to1;
	ali->from2 = minw.from2; ali->to2 = minw.to2;
	ali->score = score; ali->match = matchnum;
	ali->alilen = alilen;
}
Score calscore(seq1,len1,seq2,len2,minw)
	char *seq1,*seq2;
	int len1,len2;
	MinPos *minw;
{
    int minj, maxj;
    Score *w0;
    char *p0;
    Score w1, w2, w3;

    Score gap1, gap3;
    int am1,am2;
    int i, j;
    int ini_wini, ini_winj;

    ini_wini = (wsz < 0 ? len1 : iofst);
    ini_winj = (wsz < 0 ? len2 : jofst);
    weight[ mpos(0,0) ] = 0;
    path[ mpos(0,0) ] = 0;
    minw->score = 0;
    for (i = 1; i <= ini_wini; i++) {
	if (gotoh_gap_flag) {
		weight[ mpos(i,0) ] /* = gweight1[mpos2(0,0,j)] */
			= edgegap + min(edgegap, extgap) * (i - 1);
		gpath[mpos(i,0)] = i; /* direction of 1 */
	} else {
		weight[ mpos(i,0) ] = edgegap * i;
	}
	path[ mpos(i,0) ] = edgepath1;
    }
    for (j = 1; j <= ini_winj; j++) {
	if (gotoh_gap_flag) {
		weight[ mpos(0,j) ]
			= edgegap + min(edgegap, extgap) * (j - 1);
		gweight1[ mpos2(0,0,j) ] = weight[ mpos(0,j) ]
				+ gap;	/** initial gap open **/
		gpath[mpos(0,j)] = j;	/*  direction of 3 */
		gpath1[mpos2(0,0,j)] = 0;
	} else {
		weight[ mpos(0,j) ] = edgegap * j;
	}
	path[ mpos(0,j) ] = edgepath3;
    }
    for (i = 1; i <= len1; i++) {
	am1 = tr[seq1[i-1]];
	if (wsz>=0) {
		int tmpflag1 = 0, tmpflag2 = 0;
		minj = i - wsz;
		maxj = i + wsz;
		/* expand window */
		if (len1 > len2) {
			minj = minj - len1 + len2;
		} else {
			maxj = maxj - len1 + len2;
		}
		minj = (minj > 1) ? minj : 1;
		if (minj < 1) {
			tmpflag1 = 1;
			minj = 1;
		}
		if (maxj > len2) {
			tmpflag2 = 1;
			maxj = len2;
		}

		if (! tmpflag1) {
			weight[ mpos(i,minj-1) ] = BADVALUE;
			if (gotoh_gap_flag) {
				gweight3[ minj-1 ] = BADVALUE;
			}
		}
		if (! tmpflag2) {
			weight[ mpos(i-1,maxj) ] = BADVALUE;
			if (gotoh_gap_flag) {
				gweight1[ mpos2(0,i-1,maxj) ] = BADVALUE;
			}
		}
	} else {
		minj = 1; maxj = len2;
	}
	if (gotoh_gap_flag) {
					/** initial gap open **/
		gweight3[0] = edgegap + min(edgegap, extgap) * (i - 1) + gap;
		gpath3[0] = 0;
	}
	for (j = minj; j <= maxj; j++) {
	    if (i == len1) 
		gap3 = edgegap;
	    else 
		gap3 = gap;
	    if (j == len2)
		gap1 = edgegap;
	    else 
		gap1 = gap;
	    
/** ???
	    if (seq1[i-1] == 'O') {
		min0(weight[ mpos(i-1,j) ], weight[ mpos(i-1,j-1) ], weight[ mpos(i,j-1) ]+gap3, &weight[ mpos(i,j) ], &path[ mpos(i,j) ]);
		goto label;
	    }
	    if (seq2[j-1] == 'O') {
		min0(weight[ mpos(i-1,j) ]+gap1, weight[ mpos(i-1,j-1) ], weight[ mpos(i,j-1) ], &weight[ mpos(i,j) ], &path[ mpos(i,j) ]);
		goto label;
	    }
**/
	    
	    am2 = tr[seq2[j-1]];
	    
	    if (gotoh_gap_flag) {
		if (BETTER(weight[mpos(i-1,j)]+gap1,gweight1[mpos2(0,i-1,j)]+extgap)) {
		    	w1 = gweight1[mpos2(1,i,j)] = weight[ mpos(i-1,j) ] + gap1;
			gpath1[mpos2(1,i,j)] = 1;
		} else {
			w1 = gweight1[mpos2(1,i,j)]
				= gweight1[mpos2(0,i-1,j)] + extgap;
			gpath1[mpos2(1,i,j)] = gpath1[mpos2(0,i-1,j)] + 1;
		}
	    	w2 = weight[ mpos(i-1,j-1) ] + weight_matrix[am1][am2];
		if (BETTER(weight[mpos(i,j-1)]+gap3,gweight3[j-1]+extgap)) {
	    		w3 = gweight3[j] = weight[mpos(i,j-1)] + gap3;
			gpath3[j] = 1;
		} else {
			w3 = gweight3[j] = gweight3[j-1] + extgap;
			gpath3[j] = gpath3[j-1] + 1;
		}
	    } else {
		w1 = weight[ mpos(i-1,j) ] + gap1;
		w2 = weight[ mpos(i-1,j-1) ] + weight_matrix[am1][am2];
		w3 = weight[ mpos(i,j-1) ] + gap3;
	    }
	    w0 = &weight[ mpos(i,j) ];
	    p0 = &path[ mpos(i,j) ];
	    
	    if (BETTER(w1, w2)) {
		if (BETTER(w1, w3)) {
			*w0 = w1;
			*p0 = 1;
			if (gotoh_gap_flag) {
				gpath[mpos(i,j)] = gpath1[mpos2(1,i,j)];
			}
		} else {
			*w0 = w3;
			*p0 = 3;
			if (gotoh_gap_flag) {
				gpath[mpos(i,j)] = gpath3[j];
			}
		}
	    } else {
		if (BETTER(w2, w3)) {
			*w0 = w2;
			*p0 = 2;
		} else {
			*w0 = w3;
			*p0 = 3;
			if (gotoh_gap_flag) {
				gpath[mpos(i,j)] = gpath3[j];
			}
		}
	    }
	  label:
	    if (search == LOCAL) {
		if (BETTER(0, *w0)) {
		    *w0 = 0;
		    *p0 = 0;
		}
		if (BETTER(weight[ mpos(i,j) ], minw->score)) {
		    minw->score = weight[ mpos(i,j) ];
		    minw->to1 = i;
		    minw->to2 = j;
		    /*	printf("mw=%d(%d,%d)\n",minw->score,minw->to1,minw->to2);  */
		}
	    }
	}
	if (gotoh_gap_flag) {
		for (j = minj; j <= maxj; j++) {
			gweight1[mpos2(0,i-1,j)] = gweight1[mpos2(1,i,j)];
			gpath1[mpos2(0,i-1,j)] = gpath1[mpos2(1,i,j)];
		}
	}
    }
    if (search == GLOBAL) {
	minw->score = weight[ mpos(len1,len2) ];
	minw->from1 = minw->from2 = 1;
	minw->to1 = len1; minw->to2 = len2;
    }

/* FOR DEBUG */

#define PATH 1
#define WEIGHT 2
#define GPATH 4
#define GWEIGHT 8
    if (path_flag & PATH) {
	printf("\nPATH MATRIX\n");
	for (i = 0; i <= len1; i++) {
      		for (j = 0; j <= len2; j++) {
      		printf("%c ",path[ mpos(i,j) ]+'0');
      		}
     	 	putchar('\n');
      	}
    }
    if (path_flag & WEIGHT) {
	printf("\nWEIGHT MATRIX\n");
	for (i = 0; i <= len1; i++) {
      		for (j = 0; j <= len2; j++) {
/*
      		printf("%3d",(int)weight[ mpos(i,j) ]);
*/
      		printf(" %3lf", (float)weight[ mpos(i,j) ]);
      		}
     	 	putchar('\n');
      	}
    }
/* can't write
    if (gotoh_gap_flag && path_flag & GWEIGHT) {
	printf("\nGOTOH'S GAP WEIGHT MATRIX\n");
	for (i = 0; i <= len1; i++) {
		for (j = 0; j <= len2; j++) {
			printf("(%3d,%3d) ",
			   (int)gweight1[mpos(i,j)], (int)gweight3[mpos(i,j)]);
		}
		putchar('\n');
	}
    }
*/
    if (gotoh_gap_flag && path_flag & GPATH) {
	printf("\nGOTOH'S PATH MATRIX\n");
	for (i = 0; i <= len1; i++) {
		for (j = 0; j <= len2; j++) {
			printf("%3d ", gpath[mpos(i,j)]);
		}
		putchar('\n');
	}
    }

/*  END OF DEBUG  */

    return minw->score;
}

mem_alloc(int len1, int winwid) {
    int msize = (len1+1) * (winwid);
    int i;

    if ( ( weight = (Score *) realloc(weight, (sizeof (Score)) * msize) ) == NULL) {
	printf("Cannot make weight matrix.\n");
	fprintf(stderr, "Cannot make weight matrix.\n");
	return -1;
    }
    if ( ( path = (char *) realloc(path, (sizeof (char)) * msize) ) == NULL) {
	printf("Cannot make path matrix.\n");
	fprintf(stderr, "Cannot make path matrix.\n");
	return -1;
    }
    if (gotoh_gap_flag) {
	if (( gweight1 = (Score *) realloc(gweight1, (sizeof (Score)) * (winwid) * 2) ) == NULL) {
		printf("Cannot make weight matrix.\n");
		fprintf(stderr, "Cannot make weight matrix(P).\n");
		return -1;
	}
	if (( gweight3 = (Score *) realloc(gweight3,(sizeof (Score)) * (winwid)) ) == NULL) {
		printf("Cannot make weight matrix.\n");
		fprintf(stderr, "Cannot make weight matrix(Q).\n");
		return -1;
	}
	if (( gpath1 = (int *) realloc(gpath1, sizeof (int) * (winwid) * 2) ) == NULL) {
		printf("Cannot make path matrix.\n");
		fprintf(stderr, "Cannot make path matrix(P).\n");
		return -1;
	}
	if (( gpath3 = (int *) realloc(gpath3, sizeof (int) * (winwid)) ) == NULL) {
		printf("Cannot make path matrix.\n");
		fprintf(stderr, "Cannot make path matrix(Q).\n");
		return -1;
	}
	if (( gpath = (int *) realloc(gpath, sizeof (int) * msize ) ) == NULL) {
		printf("Cannot make path matrix.\n");
		fprintf(stderr, "Cannot make path matrix(Q).\n");
		return -1;
	}
	for (i = 0; i < msize; i++) gpath[i] = 0;
    	gweight1[ mpos(0,0) ] = gweight3[ 0 ] = 0;
    	gpath1[ mpos(0,0) ] = gpath3[ 0 ] = 0;
    }
    return 0;
}
allfree()
{
	if (weight != NULL) 
		free(weight);
	if (path != NULL)
		free(path);
	if (gweight1 != NULL)
		free(gweight1);
	if (gweight3 != NULL)
		free(gweight3);
	if (gpath1 != NULL)
		free(gpath1);
	if (gpath3 != NULL)
		free(gpath3);
	if (gpath != NULL)
		free(gpath);
}

/**********************************************/
/*     alignment                              */
/**********************************************/
make_align(seq1, len1, seq2, len2, align_seq1, align_seq2, alilen0, match, minw)
     char *seq1,*seq2;
     char *align_seq1, *align_seq2;
     int len1, len2;
     int *alilen0;
     int *match;
     MinPos *minw;
{
    int i,j,k,alilen;
    *match = 0;

/*
    len1 = strlen(seq1);
    len2 = strlen(seq2);
*/
    /*  printf("path = %s\n",path); */
/*
    strcpy(align_seq1,seq1);
    strcpy(align_seq2,seq2);
*/
    
    i = minw->to1;
    j = minw->to2;
    
    alilen = 0;
    while (path[ mpos(i,j) ] != 0) {
	if (i < 0 || j < 0) {
	    fprintf(stderr,"Strange matrix! %d,%d,%d\n",i,j,alilen);
align_seq1[alilen] = align_seq2[alilen] = '\0';
fprintf(stderr, "%s\n%s\n",align_seq1,align_seq2);
	    exit(1);
	}
	switch (path[ mpos(i,j) ]) {
	  case 1: 
	    if (gotoh_gap_flag) {
		k = gpath[ mpos(i,j) ];
		while (k--) {
			align_seq1[alilen] = seq1[--i];
			align_seq2[alilen] = '-';
			alilen++;
		}
	    } else {
		align_seq1[alilen] = seq1[--i];
		align_seq2[alilen] = '-';
		alilen++;
/*
	    	if (i > 0) i--;
*/
	    }
	    break;
	  case 2:
	    align_seq1[alilen] = seq1[--i];
	    align_seq2[alilen] = seq2[--j];
	    if (align_seq1[alilen]==align_seq2[alilen]) (*match)++;
	    alilen++;
	    break; 
	  case 3:
	    if (gotoh_gap_flag) {
		k = gpath[ mpos(i,j) ];
		while (k--) {
		    align_seq1[alilen] = '-';
		    align_seq2[alilen] = seq2[--j];
		    alilen++;
		}
	    } else {
		align_seq1[alilen] = '-';
		align_seq2[alilen] = seq2[--j];
		alilen++;
	    }
	    break;
	  default:
	    fprintf(stderr, "strange path_number'%c' exist at (%d,%d)\n",path[ mpos(i,j) ],i,j);
	    exit(1);
	}
    }
    minw->from1 = i + 1;
    minw->from2 = j + 1;
    
    align_seq1[alilen] = align_seq2[alilen] = '\0';
    reverse(align_seq1,alilen);
    reverse(align_seq2,alilen);

    if (count_or_not == NOCOUNT) {
    	for (i = 0; align_seq1[i] == '-' || align_seq2[i] == '-'; i++) {
	    if (align_seq1[i] != '-') minw->from1++;
	    if (align_seq2[i] != '-') minw->from2++;
	}
    	for (i = alilen-1; align_seq1[i] == '-' || align_seq2[i] == '-'; i--) {
	    if (align_seq1[i] != '-') minw->to1--;
	    if (align_seq2[i] != '-') minw->to2--;
	}
    }

    *alilen0 = alilen;
}
