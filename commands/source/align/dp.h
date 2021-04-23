#ifndef _DP_H_
#define _DP_H_
#include "matrix.h"

/***
#ifdef DOUBLESCORE
typedef double Score;
#define AtoScore(x) (atof(x))
#else
typedef int Score;
#define AtoScore(x) (atoi(x))
#endif
***/

#define DATA_FILENAME "stdin"
#define DAYHOFFFILE "/home/uchiyama/src/dayhoff.mt"
#define NUCSCOREFILE "/home/uchiyama/src/nucscore.mt"
#define STARTNUM 1
#define MAXSEQLEN 100000
#define MAXI MAXSEQLEN
#define MAXJ MAXSEQLEN
#define MAXNAMELEN 200
#define BREAKPOINT 1.5
#define COUNT 0
#define NOCOUNT 1
#define COUNT_OR_NOCOUNT COUNT
#define YES 1
#define NO 0
#define CONTINUE 1
#define END 0
#define _OVERFLOW -1
#define FOUND 1
#define TITLEN 10000
/*
#define GLOBAL 0
#define LOCAL 1
*/

typedef enum {GLOBAL, LOCAL} AlignMethod;
typedef enum {ALL, NUM, NAME, ALTER, PVM, MPI} SeqGetMethod;

/***
#define ALL 0
#define NUM 1
#define NAME 2
#define ALTER 3
***/

#ifndef MINUS
#define BADVALUE (Score) -999999
#define BETTER(a,b) (a > b)
#define GAP (Score) -8
#define EXTGAP (Score) -2
#define EDGEGAP (Score) 0
#else
#define BADVALUE (Score) 999999
#define BETTER(a,b) (a < b)
#define GAP (Score) 8
#define EXTGAP (Score) 2
#define EDGEGAP (Score) 0
#endif
    
/*
#define mpos(i,j) ( (i) * (len2+1) + (j) ) 
*/
#define mpos(i,j) ( (i) * (winwid) + ((j)-((wsz<0) ? 0 : (i)-(iofst))) )
#define mpos2(ii,i,j) ( (ii) * (winwid) + ((j)-((wsz<0) ? 0 : (i)-(iofst))) )
#define min(a, b) ( BETTER((a),(b)) ? (a) : (b) )

/***
Score weight_matrix[ANUM][ANUM];
***/
Score **weight_matrix;
Score calscore();

AlignMethod search;
SeqGetMethod get;

char display;
char scan;
char count_or_not;
Score gap;
Score edgegap;
Score extgap;
char edgepath1;
char edgepath3;
int gapscale;
/**
Score constgap, pamgap, lengap;
**/
double pam_stop;

Score threshold;

char *amino;
char tr[128];
int entnum1, entnum2;
char entname1[MAXNAMELEN],entname2[MAXNAMELEN];
short verbose_flag;
short path_flag;
short gotoh_gap_flag;
short title_flag;
char separate;
short walimode;
char pamfile[200];
int initpam;
int wline;
char outputline[80];
char title[TITLEN];
int winsize;
double winsizerate;
double maxdiffrate;

int calc_pam;
#define CALC_PAM_ONEITER 2
#define CALC_PAM_EXPPAM 4

#include "pam.h"
AllPam *allpam;

typedef struct {
	Score score;
	Score scoreV;  /* for calculating vertical affine gap cost (Gotoh) */
	char path;
	int jump, jumpV;
	int start_i, start_j;	   /** start pos of local alignment **/
	int startV_i, startV_j;
/*
	int match, len;
*/
} CellInfo;

typedef struct Path {
	int i, j;
} Path; 
typedef struct {
	Score init, ext, edge_init, edge_ext;
} GapPen;
typedef struct {
	char *name1, *name2;
	char *seq1, *seq2;
	char *rseq1, *rseq2;
	int len1, len2;
	Path *path;
	int pathlen;
	char *aliseq1, *aliseq2;
	Score score;
	Score **wm;
	CellInfo *cellmat, *cellmat_bwd;
/**
	int beg1,end1,beg2,end2;
**/
	GapPen gap;
	AllPam *allpam;

	int gapscale;
	char local;
	char noedgegap;
	char disp_align;
	char calc_pam;
	char separate;
	Score thre;
	char lsdp;
} MakeAliInfo;

typedef struct {
	char *ent1;
	char *ent2;
	int len1;
	int len2;
	int from1, to1, from2, to2;
	Score score;
	int match;
	int alilen;
	char *aliseq1;
	char *aliseq2;
	int pamerr;
	int pam;
	double sdpam;
	double exppam;
	Score origscore;
} Alignment;

MakeAliInfo *alignInit(), *alignCreate();
int DEBUG;

#endif
