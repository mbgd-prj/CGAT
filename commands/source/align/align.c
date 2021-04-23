#include <stdio.h>
#include "matrix.h"
#include "db.h"
#include "dp.h"
#include "math.h"
#define max(a,b) (((a)>=(b)) ? (a) : (b))
#define pmax(a,b,c) ((a>=b) ? ((a>=c) ? DIAG : VERT) : ((b>=c) ? HORI : VERT))
#define BIGMINUS -9999999
#define MAXMATNUM 12

/*****

	Linear Space Dynamic Programming (LSDP) Alignment Program

*****/

typedef struct {
	int max_i, max_j, max_start_i, max_start_j;
	int match, len;
	Score maxscore;
} LocalAliRes;

MakeAliInfo *alignInit(), *alignCreate();
Score Align(MakeAliInfo *aliInfo, int beg1, int end1, int beg2, int end2,
		char eg1B, char eg2B, char eg1E, char eg2E);
Score CalcScore();
/*
int DEBUG;
*/

/***
	Path information
	DIAG -- align two characters
	VERT -- inserts in seq1, gaps in seq2
	HORI -- inserts in seq2, gaps in seq1
***/
enum alignPath {
	DIAG = 1, HORI, VERT, STOP,
};
enum edgeType {
	INTERNAL,   /* gaps on the edge are normal internal gaps */
	EDGE,	    /* treat gaps on the edge as edge gaps
			(=0 if noedgegap option is specified) */
};

/*
int iniGap = -10;
int extGap = -2;
int edgeGap = 0;
int edgeExtGap = 0;
*/

/**** main function for debugging ****/
#ifdef DEBUGMAIN

char tr[MAXCHR];

char filename[100] = "stdin";
char seq1[MAXSEQLEN];
char seq2[MAXSEQLEN];
char entname1[200], entname2[200];
char matfile[100];

main(int argc, char **argv)
{
	char *amino = AMINO;
	DB *db;
	Score **wm;
	MakeAliInfo *ali;
	Alignment aliRes;
	MakeAliInfo *aliInfo;

	aliInfo = alignCreate();
	getargs(argc,argv,aliInfo);

	make_char2num(amino,tr);
	aliInfo->wm =
		read_matrix(matfile, amino, tr);

	if ((db = dbopen(filename)) == NULL) {
		fprintf(stderr, "%s cannot be opened\n", filename);
		exit(1);
	}
	getseq(db, entname1, seq1, sizeof(seq1));
	while( getseq(db, entname2, seq2, sizeof(seq2)) == GETSEQ_OK ) {
		LSDP_match(aliInfo, entname1, seq1, entname2, seq2, &aliRes);
		print_result(&aliRes, aliInfo);
	}
}
getargs(int argc, char **argv, MakeAliInfo *aliInfo)
{
	int i;
	int status = 0;
	int gopt = 0;
	for (i = 1; i < argc; i++) {
		if (*argv[i]=='-') {
			switch (*++argv[i]) {
			case 'D':
				DEBUG = atoi(++argv[i]);
				if (! DEBUG){
					DEBUG = 1;
				}
				break;
			case 'e':
				if (*++argv[i]) {
					aliInfo->gap.edge_init = - atoi(argv[i]);
				} else {
					aliInfo->noedgegap = 0;
				}
				break;
			case 'l':
				aliInfo->local = 1;
				break;
			case 'i':
				aliInfo->gap.init = - atoi(++argv[i]);
				break;
			case 'g':
				gopt = 1;
				aliInfo->gap.init = - atoi(++argv[i]);
				break;
			case 'x':
				aliInfo->gap.ext = - atoi(++argv[i]);
				break;
			case 't':
				aliInfo->thre = atoi(++argv[i]);
				break;
			case 'm':
				if (*++argv[i]) {
					strcpy(matfile, argv[i]);
				} else {
					status = 1;
				}
				break;
			case 's':
				aliInfo->separate = *++argv[i];
				break;
			case 'd':
				aliInfo->disp_align = 1;
				break;
			}
		} else {
			switch (status) {
			case 0:
				strcpy(filename, argv[i]);
				break;
			case 1:
				strcpy(matfile, argv[i]);
				break;
			}
			status = 0;
		}
	}
/*
	if (gopt) {
		aliInfo->gap.init -= aliInfo->gap.ext;
	}
*/
}
#endif	/* define DEBUGMAIN */

LSDP_match(MakeAliInfo *aliInfo,
		char *name1, char *seq1, char *name2, char *seq2,
		Alignment *alignRes)
{
	alignInit(aliInfo, seq1, name1, seq2, name2);

	alignRes->aliseq1 = aliInfo->aliseq1;
	alignRes->aliseq2 = aliInfo->aliseq2;

	LSDP_DoAlign(aliInfo,alignRes);

}

LSDP_DoAlign(MakeAliInfo *aliInfo, Alignment *alignRes)
{
	char edge;
	Score score;
	int beg1, end1, beg2, end2;
	LocalAliRes laliRes, *res = NULL;

	initAliPath(aliInfo);
	/** score calculation */
	if (aliInfo->local) {
		score = CalcScore(aliInfo, &laliRes);
		if (aliInfo->thre > 0 && score < (Score) aliInfo->thre) {
			return 0;
		}
	}

	/** alignment calculation */
	if (aliInfo->local) {
		beg1 = laliRes.max_start_i; beg2 = laliRes.max_start_j;
		end1 = laliRes.max_i - 1; end2 = laliRes.max_j - 1;
	} else {
		beg1 = 0; end1 = aliInfo->len1 - 1;
		beg2 = 0; end2 = aliInfo->len2 - 1;
	}

	if (aliInfo->noedgegap) {
		edge = EDGE;
	} else {
		edge = INTERNAL;
	}
	score = Align(aliInfo, beg1, end1, beg2, end2, edge, edge, edge, edge);
	aliInfo->score = score;
	LSDP_create_alignment(aliInfo, alignRes);
	return 1;
}


/***
			       j
	seqidx	     0   1   2   3   4   5
	matidx	   0   1   2   3   4   5   6
		0  +---+---+---+
		   |   |   |   |
		1  +---+---+---+ forward
		   |   |   | 1 3
	    i	2  +---+---+-2-+-2-+---+---+
		               3 1 |   |   | 
		3              +---+---+---+ backward
		               |   |   |   |
		4              +---+---+---+


	beg{1,2}, end{1,2}: seqidx
	i in w[i] or p[i]: matidx
	maxj_{fb}: (matid) End points of alignment path on the midline
	midi_{fb}, midj_{fb}, next{ij}: (seqidx) characters to be added to the alignment
	mid: seqlen
***/

Score CalcScore(MakeAliInfo *aliInfo, LocalAliRes *laliRes)
{
	char edge = (char)(aliInfo->noedgegap ?  EDGE : INTERNAL);
	CellInfo *cell = aliInfo->cellmat;
	int sub_align(char*, int, char*, int,
		char, char , char , char,
		Score **, GapPen *, CellInfo *, LocalAliRes *);

	if (aliInfo->local) {
		sub_align(aliInfo->seq1, aliInfo->len1,
			aliInfo->seq2, aliInfo->len2,
			edge, edge, edge, edge, aliInfo->wm, &(aliInfo->gap),
			cell, laliRes);
	} else {
		/* global */
		sub_align(aliInfo->seq1, aliInfo->len1,
			aliInfo->seq2, aliInfo->len2,
			edge, edge, edge, edge, aliInfo->wm, &(aliInfo->gap),
			cell, NULL);
		laliRes->maxscore = cell[aliInfo->len2].score;
/*
		laliRes->match = cell[aliInfo->len2].match;
		laliRes->len = cell[aliInfo->len2].len;
*/
	}
	return laliRes->maxscore;
}
Score Align(MakeAliInfo *aliInfo, int beg1, int end1, int beg2, int end2,
		char eg1B, char eg2B, char eg1E, char eg2E)
{
	int mid;
	int i, j, maxi_f, maxi_b, maxj_f, maxj_b, nextpath_f, nextpath_b;

	/* nextpos specifying the corners of the rectangles */
	int nexti_f, nextj_f, nexti_b, nextj_b;

	Score maxw, sumw, sumwg;
	char *seq1 = &aliInfo->seq1[beg1];
	char *seq2 = &aliInfo->seq2[beg2];
	char *rseq1 = &aliInfo->rseq1[aliInfo->len1 - end1 - 1];
	char *rseq2 = &aliInfo->rseq2[aliInfo->len2 - end2 - 1];
	int len1, len2;
	CellInfo *cell = aliInfo->cellmat;
	CellInfo *cell_bwd = aliInfo->cellmat_bwd;
	Score **wm = aliInfo->wm;
	int edgetype, maxedgetype, next_eg1, next_eg2;
	Score iniGap = aliInfo->gap.init - aliInfo->gap.ext; 

	/* partial alignment path (V:vertical; H:horizontal; D:diagonal) */
	int aliV_beg, aliV_end, aliV_beg0, aliV_end0;
	int aliH_beg, aliH_end;
	int aliD_f_i, aliD_f_j, aliD_b_i, aliD_b_j;

if (DEBUG) {
	printf("Align: [%d,%d],[%d,%d]\n",beg1,end1,beg2,end2);
	print_substr(aliInfo->seq1,beg1,end1);
	print_substr(aliInfo->seq2,beg2,end2);
	printf("edge=%d,%d,%d,%d\n",eg1B,eg2B,eg1E,eg2E);
}

	len1 = end1 - beg1 + 1;
	len2 = end2 - beg2 + 1;

/*
printf("LL]%d,%d\n",len1,len2);
if (eg1B==EDGE) putchar('>');
for(i=0;i<len1;i++){ putchar(seq1[i]); }
if (eg1E==EDGE) putchar('<');
putchar('\n');
if (eg2B==EDGE) putchar('>');
for(i=0;i<len2;i++){ putchar(seq2[i]); }
if (eg2E==EDGE) putchar('<');
printf("\n");
*/

	if (len1 <= 0) {
		for (j = 0; j < len2; j++) {
			setAliPath(aliInfo, -1, beg2 + j);
		}
		return 0;
	}
	if (len2 <= 0) {
		for (i = 0; i < len1; i++) {
			setAliPath(aliInfo, beg1 + i, -1);
		}
		return 0;
	}

	mid = len1 / 2;
	sub_align(seq1, mid, seq2, len2, eg1B, eg2B, INTERNAL, eg2E,
				wm, &(aliInfo->gap), cell, NULL);

	sub_align(rseq1, len1-mid, rseq2, len2, eg1E, eg2E, INTERNAL, eg2B,
				wm, &(aliInfo->gap), cell_bwd, NULL);

	maxw = (Score) BIGMINUS; maxj_f = maxj_b = 0;
	maxedgetype = 0;
	for (j = 0; j <= len2; j++) {
		/* sum of the fwd and bwd alignment scores */
		sumw = cell[j].score + cell_bwd[len2 - j].score;
/*
printf("%d,%d,%d,%d\n",eg1B,eg1E,eg2B,eg2E);
printf("P##>%d,%d\n",cell[j].path,cell_bwd[len2-j].path);
printf("1##>%lf,%lf\n",cell[j].score,cell[j].scoreV);
printf("2##>%lf,%lf\n",cell_bwd[len2-j].score,cell_bwd[len2-j].scoreV);
*/
		/* alignment path connected by a vertical gap */
		if ( (j == 0 && eg2B) || (j == len2 && eg2E) ) {
			sumwg = cell[j].scoreV + cell_bwd[len2 - j].scoreV;
		} else {
			/** correction for excess count of iniGap **/
			sumwg = cell[j].scoreV + cell_bwd[len2 - j].scoreV
					- iniGap;
		}
		if (sumw < sumwg) {
			/** path is (VERT,VERT) across the boundary **/
			sumw = sumwg;
			edgetype = 1;
		} else {
			edgetype = 0;
		}
		if (maxw < sumw) {
			maxw = sumw;
			maxj_f = maxj_b = j;
			maxedgetype = edgetype;
		}

DEBUG=0;
if (DEBUG) {
	printf("%d %lf,%lf, w(%lf,%lf), gw=(%lf,%lf) ",j,sumw,sumwg,
		cell[j].score,cell_bwd[len2-j].score,
		cell[j].scoreV,cell_bwd[len2-j].scoreV);
	if(j>0) printf("[%c,%c]", aliInfo->seq1[beg1+mid-1],aliInfo->seq2[beg2+j-1]);
	putchar('\n');
}
DEBUG=0;

	}

	/** calculate and save the partial alignment info. */
	aliH_beg = BIGMINUS; aliH_end = BIGMINUS; /* do no insert horigap */
	if (maxedgetype == 1) {
		nextpath_f = nextpath_b = VERT;
	} else {
		char horiflag = 0;
		nextpath_f = cell[maxj_f].path;
		nextpath_b = cell_bwd[len2-maxj_b].path;

		/* extend the horizontal gap and jump to the end of the gap */
		if (nextpath_f == HORI) {
			maxj_f = cell[maxj_f].jump;
			nextpath_f = cell[maxj_f].path;
			horiflag = 1;
		}
		if (nextpath_b == HORI) {
			maxj_b = len2 - (cell_bwd[len2-maxj_b].jump);
			nextpath_b = cell_bwd[len2-maxj_b].path;
			horiflag = 1;
		}

		/* setting the beginning and ending positions of the horigap */
		if (horiflag) {
			aliH_beg = beg2 + maxj_f;
			aliH_end = beg2 + maxj_b - 1;
		}
	}
/*
printf("aliH>%d,%d\n",aliH_beg,aliH_end);
*/

	/* setting the positions of the vertical gap */
	aliV_beg = aliV_beg0 = beg1 + mid - 1;
	aliV_end = aliV_end0 = beg1 + mid;

	maxi_f = maxi_b = mid;
	/* the next path should be VERT or DIAG */
	if (nextpath_f == VERT) {
		maxi_f = cell[maxj_f].jumpV;
		aliV_beg = beg1 + cell[maxj_f].jumpV;
		nexti_f = aliV_beg - 1;
		nextj_f = beg2 + maxj_f - 1;
	} else if (nextpath_f == DIAG) {
		aliD_f_i = beg1+mid-1;
		aliD_f_j = beg2 + maxj_f - 1;
		nexti_f = aliD_f_i - 1;
		nextj_f = aliD_f_j - 1;
	} else {
		nexti_f = nextj_f = BIGMINUS;
	}
/*
printf("NextPath: %d, Next_i: %d, Next_j: %d\n",nextpath_f,nexti_f,nextj_f);
if (nexti_f>0&&nextj_f>0) printf("%c%c\n",aliInfo->seq1[nexti_f],aliInfo->seq2[nextj_f]);
*/

	if (nextpath_b == VERT) {
		maxi_b = len1 - (cell_bwd[len2-maxj_b].jumpV);
		aliV_end = beg1 + (len1 - cell_bwd[len2-maxj_b].jumpV) - 1;
		nexti_b = aliV_end + 1;
		nextj_b = beg2 + maxj_b;
	} else if (nextpath_b == DIAG) {
		aliD_b_i = beg1 + mid;
		aliD_b_j = beg2 + maxj_b;
		nexti_b = aliD_b_i + 1;
		nextj_b = aliD_b_j + 1;
	} else {
		nexti_b = nextj_b = BIGMINUS;
	}
/*
printf("B: NextPath: %d, Next_i: %d, Next_j: %d\n",nextpath_b,nexti_b,nextj_b);
if (nexti_b>0&&nextj_b>0&&nexti_b<aliInfo->len1&&nextj_b<aliInfo->len2){
	printf("%c%c\n",aliInfo->seq1[nexti_b],aliInfo->seq2[nextj_b]);
}
*/


	/**      j_f   j_b
		+ + + + + +
	       	 \  
		+ +-+-+-+ +
		         \
		+-+-+-+ + +
	**/

if (DEBUG) {
printf("max=%lf,mid=%d,maxi={%d,%d},maxj={%d,%d},nextpath={%d,%d},edgetype=%d\n",
	maxw,mid,maxi_f,maxi_b,maxj_f,maxj_b,nextpath_f,nextpath_b,maxedgetype);
}

	next_eg1 = next_eg2 = INTERNAL;

	if (eg2E == EDGE && nextj_f == end2) {
		/* edge */
		next_eg2 = EDGE;
	}

	Align(aliInfo, beg1, nexti_f, beg2, nextj_f, eg1B, eg2B, next_eg1, next_eg2);

	switch (nextpath_f) {
	case DIAG: 
		setAliPath(aliInfo, aliD_f_i, aliD_f_j);
		break;
	case VERT:
/*
printf("######V>%d,%d\n",aliV_beg, aliV_beg0);
*/
		for (i = aliV_beg; i <= aliV_beg0; i++) {
			setAliPath(aliInfo, i, -1);
		}
		break;
	case STOP:
		break;
	case HORI:
	default:
		/* should not come here */
		printf("ERROR:f, %d,%d,%d,%lf\n",i,nextpath_f,maxj_f,maxw);
		break;
	}

	/* insert horizontal gaps */
	if (aliH_end >= 0) {
/*
		printf("######H>%d,%d\n",aliH_beg, aliH_end);
*/
		for (i = aliH_beg; i <= aliH_end; i++) {
			setAliPath(aliInfo, -1, i);
		}
	}

	switch (nextpath_b) {
	case DIAG: 
		setAliPath(aliInfo, aliD_b_i, aliD_b_j);
		break;
	case VERT:
/*
printf("######Vb>%d,%d\n",aliV_end0, aliV_end);
*/
		for (i = aliV_end0; i <= aliV_end; i++) {
			setAliPath(aliInfo, i, -1);
		}
		break;
	case STOP:
		break;
	case HORI:
	default:
		/* should not come here */
		printf("ERROR:b, %d,%d,%d,%lf\n",i,nextpath_b,maxj_b,maxw);
		break;
	}

	next_eg1 = next_eg2 = INTERNAL;

	if (eg2B == EDGE && nextj_b == 0) {
		next_eg2 = EDGE;
	}
	Align(aliInfo, nexti_b, end1, nextj_b, end2, next_eg1, next_eg2, eg1E, eg2E);

	return maxw;
}

enum EDGEFLAG {BEG1,BEG2,END1,END2};

/* edgeflag
	  seq2
	s +-BEG1--+
	e |	  |
	qBEG2	 END2
	1 |	  |
	  +-END1--+
*/

sub_align(char *seq1, int len1, char* seq2, int len2,
		char eg1B, char eg2B, char eg1E, char eg2E,
		Score **wm, GapPen *gap, CellInfo *cell, LocalAliRes *lalires)
{
	static char edgeflag[4] = {0, 0, 0, 0};
	edgeflag[BEG1] = eg1B; edgeflag[BEG2] = eg2B;
	edgeflag[END1] = eg1E; edgeflag[END2] = eg2E;
	sub_align_core(seq1, len1, seq2, len2, edgeflag, wm, gap,
			cell, lalires);
}

sub_align_core(char *seq1, int len1, char* seq2, int len2, char *edgeflag,
		Score **wm, GapPen *gap, CellInfo *cell, LocalAliRes *laliRes)
{
	register int i,j;
	Score w1, w2, w3, *wm1;
	Score iniGap = gap->init - gap->ext;
	Score extGap = gap->ext;
	Score edgeGap = gap->edge_init - gap->edge_ext;
	Score edgeExtGap = gap->edge_ext;


	CellInfo tmpCell1, tmpCell2;
	CellInfo *prevDiagCell = &tmpCell1, *prevVertCell = &tmpCell2;
	CellInfo prevVertCellG;
	CellInfo *tmpCellPtr; 	/* for swap */

	Score prevDiag, prevVert;
	Score GscoreH;/* Gotoh's score matrix for affingap cost of
				horizontal gap */

	static Score iniGapE[4], extGapE[4];
	Score iniGapH, extGapH, iniGapV, extGapV; /* hori/vert gaps */
	int jumpH;  /* position of the prev match before the horizontal gap */
	int jumpV;  /* position of the prev match before the vertical gap */


	if (laliRes) {
		laliRes->maxscore = 0.0;
	}

if (DEBUG) {
	printf(">edge=%d,%d,%d,%d\n", edgeflag[0],edgeflag[1],edgeflag[2],edgeflag[3]);
}

	for (i = 0; i < 4; i++) {	/* BEG1,BEG2,END1,END2 */
		if (edgeflag[i] == EDGE) {
			iniGapE[i] = edgeGap;
			extGapE[i] = edgeExtGap;
		} else {
			iniGapE[i] = iniGap; extGapE[i] = extGap;
		}
	}

	/** the first line */
	for (j = 0; j <= len2; j++) {
		if (j == 0) {
			/* the upper-left corner */
			cell[j].score = 0;
			cell[j].path = STOP;
		} else {
			cell[j].score = iniGapE[BEG1] + extGapE[BEG1] * j;
			/* path = HORI; jumpH = 0 */   
			cell[j].path = HORI;
			cell[j].jump = 0;   
		}

		cell[j].scoreV = cell[j].score + iniGap; /* not an edge gap! */
		cell[j].start_i = 0;
		cell[j].start_j = j;
/*
		cell[0].match = 0;
		cell[0].len = j;
*/

		if (DEBUG >=2) {
			printf(" (%d,%d,%d)",cell[j].score,
					cell[j].scoreV,cell[j].path);
		}
	}
	if (DEBUG >=2) {
		putchar('\n');
	}
	for (i = 1; i <= len1; i++) {
		/** j = 0 **/
		wm1 = wm[tr[seq1[i-1]]];

		memcpy(prevVertCell, &cell[0], sizeof(CellInfo));
		prevVert = prevVertCell->score;

		cell[0].score = iniGapE[BEG2] + extGapE[BEG2] * i;
		cell[0].scoreV = cell[0].score;
		cell[0].start_i = i;
		cell[0].start_j = 0;
/*
		cell[0].match = 0;
		cell[0].len = i;
*/

		GscoreH = cell[0].score + iniGap;	/* Not an edge gap! */

		jumpH = 0;

		cell[0].path = VERT;
		cell[0].jump = 0;

/*
		if (DEBUG >= 2) {
			printf(" (%d,%d,%d)",cell[0].score,
					cell[0].scoreV,cell[0].path);
		}
*/
		if (i == len1) {
			iniGapH = iniGapE[END1];
			extGapH = extGapE[END1];
		} else {
			iniGapH = iniGap;
			extGapH = extGap;
		}

		for (j = 1; j <= len2; j++) {
			tmpCellPtr = prevDiagCell;
			prevDiagCell = prevVertCell;
			prevVertCell = tmpCellPtr;
			memcpy(prevVertCell, &cell[j], sizeof(CellInfo));

			prevDiag = prevDiagCell->score;
			prevVert = prevVertCell->score;

/*
			prevDiag = prevVert;
			prevVert = cell[j].score;
*/
			if (GscoreH < cell[j-1].score + iniGapH){
				/* initiate a new gap; position=jumpH */
				GscoreH = cell[j-1].score + iniGapH;
				jumpH = j-1;
			}
			GscoreH += extGapH;

			if (j == len2) {
				iniGapV = iniGapE[END2];
				extGapV = extGapE[END2];
			} else {
				iniGapV = iniGap;
				extGapV = extGap;
			}

			if (cell[j].scoreV < prevVert + iniGapV) {
				cell[j].scoreV = prevVert + iniGapV;
				cell[j].jumpV = i-1;
				cell[j].startV_i = prevVertCell->start_i;
				cell[j].startV_j = prevVertCell->start_j;
			}
			cell[j].scoreV += extGapV;

			w1 = prevDiag + wm1[tr[seq2[j-1]]];
			w2 = GscoreH;
			w3 = cell[j].scoreV;

			if (w1 >= w2) {
				if (w1 >= w3) {
					cell[j].path = DIAG;
				} else {
					cell[j].path = VERT;
				}
			} else {
				if (w2 >= w3) {
					cell[j].path = HORI;
				} else {
					cell[j].path = VERT;
				}
			}
			switch (cell[j].path) {
			  case DIAG:
				cell[j].score = w1;
				cell[j].start_i = prevDiagCell->start_i;
				cell[j].start_j = prevDiagCell->start_j;
/*
				cell[j].match = prevDiagCell->match;
				cell[j].len = prevDiagCell->len + 1;
				if (seq2[j-1] == seq1[i-1]) {
					cell[j].match++;
				}
*/
				break;
			  case VERT:
				cell[j].score = w3;
				cell[j].start_i = cell[j].startV_i;
				cell[j].start_j = cell[j].startV_j;
/*
				cell[j].match = cell[j].match;
				cell[j].len = cell[j].len + 1;
*/
				break;
			  case HORI:
				cell[j].score = w2;
				/* path=HORI; set horizontal jump */
				cell[j].jump = jumpH;
				cell[j].start_i = cell[jumpH].start_i;
				cell[j].start_j = cell[jumpH].start_j;
/*
				cell[j].match = cell[jumpH].match;
				cell[j].len = cell[jumpH].len + 1;
*/
				break;
			   default:
				printf("????: %d,%d\n",j,cell[j].path);
			}

			if (laliRes && cell[j].score > laliRes->maxscore) {
				/** update local maximum score **/
				laliRes->maxscore = cell[j].score;
				laliRes->max_i = i;
				laliRes->max_j = j;
				laliRes->max_start_i = cell[j].start_i;
				laliRes->max_start_j = cell[j].start_j;
/*
				laliRes->match = cell[j].match;
				laliRes->len = cell[j].len;
*/
			} else if (laliRes && cell[j].score <= 0) {
				/** reset minus score for local alignment **/
				cell[j].score = 0;
				cell[j].path = STOP;
				cell[j].start_i = i;
				cell[j].start_j = j;
/*
				cell[j].match = 0;
				cell[j].len = 0;
*/
			}

DEBUG=0;
			if (DEBUG >=2) {
/*
				printf(" (%.0lf,%.0lf,%.0lf,%d)",
					cell[j].score,
				    GscoreH,cell[j].scoreV,cell[j].path);
*/
				printf("(%3.0lf,%3.0lf,%3.0lf,%1d)",
/*
					cell[j].score,cell[j].path);
*/
					cell[j].score,
				    GscoreH,cell[j].scoreV,cell[j].path);
				printf("%c%c",seq1[i-1],seq2[j-1]);
			}
		}
		if (DEBUG>=2) {
			putchar('\n');
		}
	}
	if (DEBUG>=2) {
		putchar('\n');
	}
}

initAliPath(MakeAliInfo *ali)
{
	ali->pathlen = 0;
}
setAliPath(MakeAliInfo *ali, int i, int j)
{
	ali->path[ali->pathlen].i = i;
	ali->path[ali->pathlen].j = j;
	ali->pathlen++;
}

print_substr(char *str,int from,int to)
{
	int i;
	for (i = from; i <= to; i++) {
		putchar(str[i]);
	}
	putchar('\n');
}

print_alires(MakeAliInfo *ali, LocalAliRes *laliRes)
{
	float ident;
	ident = 100.0 * laliRes->match / laliRes->len;
	printf("%s %s %.0lf %d %d %.1f\n",
		ali->name1, ali->name2,
		laliRes->maxscore, laliRes->match, laliRes->len, ident);
}
LSDP_create_alignment(MakeAliInfo *aliInfo, Alignment *aliRes)
{
	int i, sqi, matchnum = 0;

	aliRes->ent1 = aliInfo->name1;
	aliRes->ent2 = aliInfo->name2;
	aliRes->len1 = aliInfo->len1;
	aliRes->len2 = aliInfo->len2;

	for (i = 0; i < aliInfo->pathlen; i++) {
		if (aliInfo->path[i].i >= 0 && aliInfo->path[i].j >= 0) break;
	}
	if (i < aliInfo->pathlen) {
		aliRes->from1 = aliInfo->path[i].i + 1;
		aliRes->from2 = aliInfo->path[i].j + 1;
	} else {
		/* not aligned at all */
		aliRes->from1 = aliRes->from2 = 0;
	}
	for (i = aliInfo->pathlen - 1; i >= 0; i--) {
		if (aliInfo->path[i].i >= 0 && aliInfo->path[i].j >= 0) break;
	}
	if (i >= 0) {
		aliRes->to1 = aliInfo->path[i].i + 1;
		aliRes->to2 = aliInfo->path[i].j + 1;
	} else {
		/* not aligned at all */
		aliRes->to1 = aliRes->to2 = 0;
	}
	aliRes->score = aliInfo->score;

	for (i = 0; i < aliInfo->pathlen; i++) {
		sqi = aliInfo->path[i].i;
		if (sqi >= 0) {
			aliRes->aliseq1[i] = aliInfo->seq1[sqi];
		} else {
			aliRes->aliseq1[i] = '-';
		}
		sqi = aliInfo->path[i].j;
		if (sqi >= 0) {
			aliRes->aliseq2[i] = aliInfo->seq2[sqi];
		} else {
			aliRes->aliseq2[i] = '-';
		}
		if (aliRes->aliseq1[i] == aliRes->aliseq2[i]) {
			matchnum++;
		}
	}
	aliRes->aliseq1[i] = aliRes->aliseq2[i] = '\0';
	aliRes->match = matchnum;
	aliRes->alilen = aliInfo->pathlen;
}
