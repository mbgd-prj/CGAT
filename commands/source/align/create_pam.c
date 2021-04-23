/* According to pam.c by S.F. Altschul */

#include <stdio.h>
#include <math.h>
#include <ctype.h>
#include <stdlib.h>
#include <strings.h>
#include "mathmat.h"
#include "matrix.h"
#include "pam.h"



#define AMINONUM 20
#define AMCHRNUM 24
#define MUTMATSIZ 10
#define CHRNUM 128

#define MAXDOUBLE 1e+300

static char amino[] = "ARNDCQEGHILKMFPSTWYVBZX*";
static char tr[CHRNUM];

int mut0[190] = {
 30,
109, 17,
154,  0,532,
 33, 10,  0,  0,
 93,120, 50, 76,  0,
266,  0, 94,831,  0,422,
579, 10,156,162, 10, 30,112,
 21,103,226, 43, 10,243, 23, 10,
 66, 30, 36, 13, 17,  8, 35,  0,  3,
 95, 17, 37,  0,  0, 75, 15, 17, 40,253,
 57,477,322, 85,  0,147,104, 60, 23, 43, 39,
 29, 17,  0,  0,  0, 20,  7,  7,  0, 57,207, 90,
 20,  7,  7,  0,  0,  0,  0, 17, 20, 90,167,  0, 17,
345, 67, 27, 10, 10, 93, 40, 49, 50,  7, 43, 43,  4,  7,
772,137,432, 98,117, 47, 86,450, 26, 20, 32,168, 20, 40,269,
590, 20,169, 57, 10, 37, 31, 50, 14,129, 52,200, 28, 10, 73,696,
  0, 27,  3,  0,  0,  0,  0,  0,  3,  0, 13,  0,  0, 10,  0, 17, 0,
 20,  3, 36,  0, 30,  0, 10,  0, 40, 13, 23, 10,  0,260,  0, 22, 23,  6,
365, 20, 13, 17, 33, 27, 37, 97, 30,661,303, 17, 77, 10, 50, 43, 186, 0, 17};

double fq[AMINONUM] = {
87.13, 40.90, 40.43, 46.87, 33.47, 38.26, 49.53, 88.61, 33.62, 36.89,
85.36, 80.48, 14.75, 39.77, 50.68, 69.58, 58.54, 10.49, 29.92, 64.72};

double mutab[AMINONUM] = {
100.0,  65.0, 134.0, 106.0,  20.0,  93.0, 102.0,  49.0,  66.0,  96.0,
 40.0,  56.0,  94.0,  41.0,  56.0, 120.0,  97.0,  18.0,  41.0,  74.0};

int jttpam1[20][20] = {
{98756,27,24,42,12,23,66,129,5,19,28,22,11,6,99,264,267,1,4,193},
{41,98962,19,8,21,125,20,102,74,13,34,390,10,3,36,69,38,18,8,11},
{43,23,98707,284,6,31,36,58,92,26,12,150,8,3,6,344,137,0,23,11},
{63,8,235,98932,2,21,478,95,24,6,6,17,4,1,6,40,25,1,15,21},
{44,52,13,5,99450,4,3,41,17,8,15,3,10,28,6,147,28,16,68,41},
{43,154,33,27,2,98955,211,17,130,4,64,176,11,2,81,37,31,2,8,12},
{82,16,25,398,1,140,99042,83,6,6,9,103,4,2,10,21,19,2,2,31},
{135,70,33,66,11,10,70,99369,5,3,6,16,3,2,11,129,19,8,2,32},
{17,164,171,53,15,233,15,15,98867,10,49,31,8,18,58,51,28,2,189,8},
{28,12,21,6,3,3,7,4,4,98722,212,12,113,31,5,28,149,2,10,630},
{24,19,6,3,3,29,6,5,12,122,99328,9,90,101,53,40,16,8,8,117},
{28,334,108,14,1,122,107,20,12,11,13,99101,15,1,11,32,57,1,3,8},
{36,22,14,10,8,19,11,10,8,253,350,37,98845,18,8,19,123,3,6,201},
{11,3,3,2,14,2,3,4,11,41,230,1,10,99357,8,65,8,8,179,40},
{150,36,5,7,3,66,12,16,26,5,97,13,4,6,99278,190,69,1,4,14},
{297,51,214,30,44,22,19,139,17,21,54,28,7,38,140,98548,278,4,20,27},
{351,33,100,22,9,21,20,24,11,134,25,57,49,6,59,325,98670,1,6,76},
{7,65,1,3,23,7,7,41,3,7,49,5,5,22,4,21,5,99684,24,16},
{11,12,30,23,43,10,4,4,134,16,22,5,4,222,6,43,12,11,99377,11},
{226,9,7,16,13,7,29,35,3,504,161,7,71,24,11,28,67,3,5,98772}
};
double jttfq[AMINONUM] = {
77.0, 51.0, 43.0, 52.0, 20.0, 41.0, 62.0, 74.0, 23.0, 53.0,
91.0, 59.0, 24.0, 40.0, 51.0, 69.0, 59.0, 14.0, 32.0, 66.0};

double jttmutab[AMINONUM] = {
100.0,  83.0, 104.0, 86.0,  44.0,  84.0,  77.0,  50.0,  91.0, 103.0,
 54.0,  72.0,  93.0, 51.0,  58.0, 117.0, 107.0,  25.0,  50.0,  98.0};
char jtt1file[100];

/*
double scale = 10.0;
*/
double scale = 1.0;
void _log10(double *x)
{
	if (*x == 0.0) *x = -9999.0;
	else *x = (log10(*x))*scale;
}
void _log(double *x)
{
	if (*x == 0.0) *x = -9999.0;
	else *x = (log(*x))/scale;
}
void _mulscale(x)
	double *x;
{
	(*x) /= scale;
}

/*
MathMat *create_mutmat0();
AllPam **create_allpam();
*/

/*
#define MAIN
*/
#ifdef MAIN
int maxpam = 500;
char filename[100] = PAMFILE;
enum {create, read} mode = read;
int jtt = 0;
double pam;
main(argc, argv)
	int argc;
	char **argv;
{
	int i;
	MathMat *mut0 = NULL;
	MathMat **mut;
	AllPam *allpam;
	AllPam *allpam2;
	get_args(argc, argv);
	if (jtt1file[0]) {
		read_jttpam1(jtt1file);
	}
	if (mode == create) {
		if (jtt) {
			mut0 = create_mutmat_jtt();
		} else {
			mut0 = create_mutmat0();
		}
		allpam = create_allpam(mut0, maxpam);
		write_allpam(filename, allpam);
	} else if (mode == read) {
		allpam2 = read_allpam(filename);
		if (pam) {
			char *p;
			MathMat *newmat;
			printf("# Matrix PAM %.2f",pam);
			printf( jtt ? " (JTT)" : " (Dayhoff)" );
			if (scale) printf(" scale = %.4lf\n",scale);
			printf("\n");
/*
			if (scale) {
				apply_all_MathMat(allpam2->pam[pam],_mulscale);
			}
			printd_MathMat_Names(allpam2->pam[pam], "%3d",
					amino, "%3c");
			printd_MathMat(allpam2->pam[pam], "%3d");
*/
/*
			mut0 = create_mutmat_jtt();
*/
			mut0 = create_mutmat0();
			pow_MathMat(mut0, pam, &newmat);
			mut_to_pam(newmat);
			if (scale) {
				apply_all_MathMat(newmat,_mulscale);
			}
			printd_MathMat(newmat, "%3d");
		} else {
		    for (i = 0; i <= allpam2->maxpam; i++) {
			printf("***** PAM %d *****\n", i);
			if (scale) {
				apply_all_MathMat(allpam2->pam[i],_mulscale);
			}
			printd_MathMat(allpam2->pam[i], "%3d");
		    }
		}
	}
}
get_args(argc, argv)
	int argc;
	char **argv;
{
	int i;
	for (i = 1; i < argc; i++) {
		if (*argv[i] == '-') {
			switch (*++argv[i]) {
			case 's':
				scale = atof(++argv[i]);
				break;
			case 'c':
				mode = create;
				break;
			case 'j':
				jtt = 1;
				if (argv[i]+1) {
					strcpy(jtt1file, ++argv[i]);
				}
				break;
			case 'r':
				mode = read;
				break;
			case 'f':
				strcpy(filename, ++argv[i]);
				break;
			case 'p':
				pam = atof(++argv[i]);
				break;
			}
		} else {
			maxpam = atoi(argv[i]);
		}
	}
}
#endif


MathVect *fqv, *mutabv;
MathMat *create_mutmat0()
{
	int i, j, k;
	double sum, lambda_inv;
	MathMat *tmpmat;
	MathMat *sumvect;
	MathMat *mutmat = create_MathMat_init(0.0, AMCHRNUM,AMCHRNUM);
	MathMat *mut = submat_MathMat(mutmat, 0, 0, AMINONUM, AMINONUM);
	for (i = 0, k = 0; i < AMINONUM; i++) {
		for (j = 0; j < i; j++) {
			set_MathMat(mut,i,j,(double)mut0[k]);
			set_MathMat(mut,j,i,(double)mut0[k]);
			k++;
		}
	}
/*
	tmpmat = create_MathMat_init(1.0, 1, AMINONUM);
	if ((sumvect = MathMat_multiply(tmpmat, mut)) == NULL) {
		fprintf(stderr, "Error\n");
		exit(1);
	}
*/

	fqv = create_MathVect(AMINONUM);
	set_MathVect0(fqv, fq);
	sum = sum_MathVect(fqv);
	div_MathVect(fqv,sum);

	mutabv = create_MathVect(AMINONUM);
	set_MathVect0(mutabv, mutab);
	lambda_inv = dot_MathVect(fqv, mutabv) * 100.0;
	div_MathVect(mutabv, lambda_inv);


/*
	for (i = 0; i < AMINONUM; i++) {
		fq[i] /= sum;
	}
	for (i = 0, sum = 0; i < AMINONUM; i++) {
		sum += mutab[i] * fq[i];
	}
	for (i = 0; i < AMINONUM; i++) {
		mutab[i] /= (sum * 100);
	}
*/
	for (i = 0; i < AMINONUM; i++) {
		set_MathMat(mut, i, i, (1.0-get_MathVect(mutabv,i)));
	}
	for (i = 0; i < AMINONUM; i++) {
		for (j = 0, sum = 0; j < AMINONUM; j++) {
			if (j != i) {
				sum += get_MathMat(mut,i,j);
			}
		}
		sum /= get_MathVect(mutabv,i);
		for (j = 0; j < AMINONUM; j++) {
			if (j != i) {
				get_MathMat(mut,i,j) /= sum;
			}
		}
	}
/*
	printf_MathMat(mut,"%.2lg");
*/
	free_subMat(mut);
	return mutmat;
}
MathMat *create_mutmat_jtt()
{
	MathMat *mutmat = create_MathMat_init(0.0, AMCHRNUM,AMCHRNUM);
	MathMat *mut = submat_MathMat(mutmat, 0, 0, AMINONUM, AMINONUM);
	int i, j;
	double sum, lambda_inv;
	for (i = 0; i < AMINONUM; i++) {
int t=0;
		for (j = 0; j < AMINONUM; j++) {
t += jttpam1[i][j];
			set_MathMat(mut, i, j, (double) jttpam1[i][j]/100000.0);
		}
	}

	fqv = create_MathVect(AMINONUM);
	set_MathVect0(fqv, jttfq);
	sum = sum_MathVect(fqv);
	div_MathVect(fqv,sum);

	mutabv = create_MathVect(AMINONUM);
	set_MathVect0(mutabv, jttmutab);
	lambda_inv = dot_MathVect(fqv, mutabv) * 100.0;
	div_MathVect(mutabv, lambda_inv);

/*
	printf_MathMat(mut,"%.2lg");
*/

	free_subMat(mut);
	return mutmat;
}
AllPam *create_allpam(mut0, maxpam)
	MathMat *mut0;
	int maxpam;
{
	int i;
	AllPam *allpam;
	MathMat **mut, *submat;
	if ((mut = (MathMat **)malloc(sizeof(MathMat *) * (maxpam+1))) == NULL){
		fprintf(stderr, "Can't alloc mutmat\n");
		exit(1);
	}
	mut[0] = create_MathMat(AMCHRNUM, AMCHRNUM);
	for (i = 0; i < AMINONUM; i++) {
		set_MathMat(mut[0],i,i, 1.0);
	}
	mut[1] = mut0;
	for (i = 2; i <= maxpam; i++) {
		mut[i] = MathMat_multiply(mut[i-1],mut0);
	}
/***
	for (i = 0; i <= maxpam; i++) {
		Mat_div_Vect(mut[i], fqv);
		symmetrize_MathMat(mut);
	}
	for (i = 0; i <= maxpam; i++) {
		apply_all_MathMat(mut[i],_log10);
		add_bzx(mut[i]);
	}
***/
	for (i = 0; i <= maxpam; i++) {
		mut_to_pam(mut[i]);
	}
	if ((allpam = (AllPam *) malloc(sizeof(AllPam))) == NULL) {
		return NULL;
	}
	allpam->pam = mut;
	allpam->freq = fqv;
	allpam->amino = amino;
	for (i = 0; i < strlen(amino); i++) {
		tr[amino[i]] = i;
		tr[tolower(amino[i])] = i;
	}
	allpam->tr = tr;
	allpam->maxpam = maxpam;
	allpam->scale = scale;
	return allpam;
}
mut_to_pam(mut)
	MathMat *mut;
{
	Mat_div_Vect(mut, fqv);
	symmetrize_MathMat(mut);
	apply_all_MathMat(mut,_log);
	add_bzx(mut);
}


/** B=20,Z=21,X=22,*=23  B=N(2)D(3), Z=Q(5)E(6)**/
add_bzx(mut)
	MathMat *mut;
{
	int i, j;
	double tmpval;
	double minval = 0.0;
	for (i = 0; i < 23; i++) {
		tmpval = (get_MathMat(mut,2,i) * get_MathVect(fqv, 2) + get_MathMat(mut,3,i) * get_MathVect(fqv,3))
				/(get_MathVect(fqv, 2) + get_MathVect(fqv, 3));
		set_MathMat(mut,i,20,tmpval); set_MathMat(mut,20,i,tmpval);
		tmpval = (get_MathMat(mut,5,i) * get_MathVect(fqv, 5) + get_MathMat(mut,6,i) * get_MathVect(fqv,6))
				/(get_MathVect(fqv, 5) + get_MathVect(fqv, 6));
		set_MathMat(mut,i,21,tmpval); set_MathMat(mut,21,i,tmpval);
		tmpval = 0.0;
		for (j = 0; j < 20; j++) {
			tmpval += get_MathMat(mut,j,i) * get_MathVect(fqv, j);
			if (minval > get_MathMat(mut,i,j)) {
				minval = get_MathMat(mut,i,j);
			}
		}
		set_MathMat(mut,i,22,tmpval); set_MathMat(mut,22,i,tmpval);
	}
	
/*
	tmpval = (get_MathMat(mut,2,20) * get_MathVect(fqv, 2) + get_MathMat(mut,3,20) * get_MathVect(fqv,3))
			/(get_MathVect(fqv, 2) + get_MathVect(fqv, 3));
	set_MathMat(mut,20,20,tmpval);
	tmpval = (get_MathMat(mut,5,21) * get_MathVect(fqv, 5) + get_MathMat(mut,6,21) * get_MathVect(fqv,6))
			/(get_MathVect(fqv, 5) + get_MathVect(fqv, 6));
	set_MathMat(mut,21,21,tmpval);
	tmpval = (get_MathMat(mut,2,21) * get_MathVect(fqv, 2) + get_MathMat(mut,3,21) * get_MathVect(fqv,3))
			/(get_MathVect(fqv, 2) + get_MathVect(fqv, 3));
	set_MathMat(mut,20,21,tmpval); set_MathMat(mut,21,20,tmpval);
*/

	for (i = 0; i < AMCHRNUM; i++) {
		set_MathMat(mut,i,23,minval); set_MathMat(mut,23,i,minval);
	}
}
AllPam *read_allpam(filename)
	char *filename;
{
	FILE  *fp;
	int maxpam = 0;
	AllPam *allpam;
	int i, j;
	int readlen;
	
	if (! *filename) {
		strcpy(filename, PAMFILE);
	}
	if ((fp = fopen(filename, "r")) == NULL) {
		if ( ((filename = getenv("PAMFILE")) == NULL) ||
		     ((fp = fopen(filename, "r")) == NULL)
		   ) {
			fprintf(stderr, "Can't open allpam file: %s\n",
						filename);
			exit(1);
		}
	}
	if (! fread(&maxpam, sizeof(int), 1, fp)) {
		fprintf(stderr, "read error (maxpam)\n");
	}
	if ((allpam= (AllPam *) malloc(sizeof(AllPam))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((allpam->pam = (MathMat **) malloc(sizeof(MathMat *) * (maxpam+1))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	allpam->maxpam = maxpam;
	if (! fread(&(allpam->scale), sizeof(double), 1, fp)) {
		fprintf(stderr, "read error\n");
	}
	if (! fread(&readlen, sizeof(int), 1, fp)) {
		fprintf(stderr, "read error\n");
	}
	if ((allpam->amino = (char *) malloc(sizeof(char)*readlen)) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if (! fread(allpam->amino, sizeof(char), readlen, fp)) {
		fprintf(stderr, "read error\n");
	}
	if (! fread(&readlen, sizeof(int), 1, fp)) {
		fprintf(stderr, "read error\n");
	}
	if ((allpam->tr = (char *) malloc(sizeof(char)*readlen)) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if (! fread(allpam->tr, sizeof(char), readlen, fp)) {
		fprintf(stderr, "read error\n");
	}
	for (i = 0; i <= maxpam; i++) {
		allpam->pam[i] = create_MathMat(AMCHRNUM, AMCHRNUM);
		for (j = 0; j < AMCHRNUM; j++) {
			if (fread(allpam->pam[i]->matrix[j], sizeof(double), AMCHRNUM, fp) != (size_t)AMCHRNUM) {
				fprintf(stderr, "read error %d,%d\n",i,j);
			}
		}
	}
	fclose(fp);
	return allpam;
}
write_allpam(filename, allpam)
	char *filename;
	AllPam *allpam;
{
	FILE  *fp;
	int i, j;
	int writelen;
	
	if (! *filename) {
		strcpy(filename, PAMFILE);
	}
	if ((fp = fopen(filename, "w")) == NULL) {
		if ( ((filename = getenv("PAMFILE")) == NULL) ||
		     ((fp = fopen(filename, "w")) == NULL)
		   ) {
			fprintf(stderr, "Can't open allpam file: %s\n",
						filename);
			exit(1);
		}
	}
	if (! fwrite(&(allpam->maxpam), sizeof(int), 1, fp)) {
		fprintf(stderr, "write error\n");
	}
	if (! fwrite(&(allpam->scale), sizeof(double), 1, fp)) {
		fprintf(stderr, "write error\n");
	}
	writelen = strlen(allpam->amino) + 1;
	if (! fwrite(&writelen, sizeof(int), 1, fp)) {
		fprintf(stderr, "write error\n");
	}
	if (! fwrite(allpam->amino, sizeof(char), writelen, fp)) {
		fprintf(stderr, "write error\n");
	}
	writelen = CHRNUM;
	if (! fwrite(&writelen, sizeof(int), 1, fp)) {
		fprintf(stderr, "write error\n");
	}
	if (! fwrite(allpam->tr, sizeof(char), writelen, fp)) {
		fprintf(stderr, "write error\n");
	}
	for (i = 0; i <= allpam->maxpam; i++) {
		for (j = 0; j < AMCHRNUM; j++) {
			if (fwrite(allpam->pam[i]->matrix[j], sizeof(double), AMCHRNUM, fp) != AMCHRNUM) {
				fprintf(stderr, "write error\n");
			}
		}
	}
	fclose(fp);
}

static int gapscale_flag = 1;

/*      Benner, Cohen, Gonnet  JMB 229,1065 (1993) [eq.12] */
/* gap(len,PAM) = -constgap + pamgap*log10(PAM) - lengap*(len-1) */
static double constgap = 37.31, pamgap = 6.88, lengap = 1.47;

calpam(allpam, seq1, seq2, len, maxpam, exppam, sdpam)
	AllPam *allpam;
	char *seq1, *seq2;
	int len;
	int *maxpam;
	double *exppam, *sdpam;
{
	int i, j, k;
	double score;
	double mlscore;
	double sump;
	double expsqpam;
	char *tr = allpam->tr;
	double p;
	int error_flag = 0;
	double corr = 0.0;
	int begin,end, flag;
	int gopen_num = 0,gext_num = 0;
	enum {BEGIN, GAP1, GAP2, MATCH} status;
	int ampair[AMCHRNUM][AMCHRNUM];
double tmp,tmp2;
/*
printf("%s\n%s\n",seq1,seq2);
*/

	mlscore = 0; *maxpam = 0; *exppam = 0.0; sump = 0.0; expsqpam = 0.0;

	if (gapscale_flag) {
		gopen_num = gext_num = 0;
		status = BEGIN;
		for (j = 0; j < len; j++) {
			if (seq1[j] == '-') {
				if (status == GAP2) {
					break;
				} else {
					status = GAP1;
				}
			} else if (seq2[j] == '-') {
				if (status == GAP1) {
					break;
				} else {
					status = GAP2;
				}
			} else {
				break;
			}
		}
		begin = j;
		status = BEGIN;
		for (j = len - 1; j >= 0; j--) {
			if (seq1[j] == '-') {
				if (status == GAP2) {
					break;
				} else {
					status = GAP1;
				}
			} else if (seq2[j] == '-') {
				if (status == GAP1) {
					break;
				} else {
					status = GAP2;
				}
			} else {
				break;
			}
		}
		end = j;
		status = BEGIN;
		for (j = begin; j <= end; j++) {
			if (seq1[j] == '-') {
				if (status == GAP1) {
					gext_num++;
				} else {
					status = GAP1;
					gopen_num++;
				}
			} else if (seq2[j] == '-') {
				if (status == GAP2) {
					gext_num++;
				} else {
					status = GAP2;
					gopen_num++;
				}
			} else {
				status = MATCH;
			}
		}
	}

	for (i = 0; i < AMCHRNUM; i++) {
		for (j = 0; j < AMCHRNUM; j++) {
			ampair[i][j] = 0;
		}
	}
	for (i = begin; i <= end; i++) {
		if (seq1[i] != '-' && seq2[i] != '-') {
/*
tmp += allpam->pam[232]->matrix[tr[seq1[i]]][tr[seq2[i]]]*10/log(10),score*10/log(10);
printf("%c,%c,%lf,%lf\n",seq1[i],seq2[i],allpam->pam[232]->matrix[tr[seq1[i]]][tr[seq2[i]]]*10/log(10),tmp);
*/
/*
			if (tr[seq1[i]] <= tr[seq2[i]]) {
				ampair[tr[seq1[i]]][tr[seq2[i]]]++;
			} else {
				ampair[tr[seq2[i]]][tr[seq1[i]]]++;
			}
*/
			ampair[tr[seq1[i]]][tr[seq2[i]]]++;
		}
	}
/*
printf(">%lf,%lf\n",-constgap + pamgap * log10((double) 232+1),- lengap);
tmp2 = (-constgap + pamgap * log10((double) 232+1)) * (double) gopen_num - lengap * gext_num;
printf("#%lf,%lf,%d,%d\n",tmp2,tmp+tmp2,gopen_num,gext_num);
*/

	for (i = 0; i <= allpam->maxpam; i++) {
		if (gapscale_flag) {
/*
			score = pamgap * log((double) (i+1)) * (double) gopen_num;
*/
			score = (-constgap + pamgap * log10((double) i+1)) * (double) gopen_num - lengap * gext_num;
		} else {
			score = 0;
		}
		for (j = 0; j < AMCHRNUM; j++) {
/*
			for (k = j; k < AMCHRNUM; k++) {
*/
			for (k = 0; k < AMCHRNUM; k++) {
				score += (allpam->pam[i]->matrix[j][k] * ampair[j][k]/log(10)*10);
			}
		}

/***
		for (j = begin; j <= end; j++) {
			if (seq1[j] != '-' && seq2[j] != '-') {
				score += allpam->pam[i]->matrix[tr[seq1[j]]][tr[seq2[j]]];
			}
		}
***/

		if (mlscore < score) {
			mlscore = score;
			*maxpam = i;
		}
		p = exp((double)score*allpam->scale - corr);
		while (p == HUGE_VAL || p > MAXDOUBLE) {
			corr += 5.0;
			p = exp((double)score*allpam->scale - corr);
			sump /= exp(5.0);
			*exppam /= exp(5.0);
			expsqpam /= exp(5.0);
		}
		*exppam += (double) i * p;
		expsqpam += (double) i * (double) i * p;
		sump += p;

/*
		printf("%d %lf %lf %lf\n",i, score, exp((double)score*allpam->scale), allpam->scale);
		printf("%d %lf\n",i, score);
*/

	}
/*
printf("ss>%lf,%lf,%d,%lf,%lf,%d,%lf,%lf,%d,%lf\n",
	mlscore*10/log(10),lengap,gext_num,constgap,
	(mlscore - lengap*gext_num - constgap*gopen_num)* 10/log(10),
	*maxpam,(pamgap*log(*maxpam+1)-constgap)*10/log(10),-lengap*10/log(10),gopen_num,constgap);
*/
	*exppam /= sump;
	*sdpam = expsqpam / sump - (*exppam * *exppam);
	if (*sdpam < 0) {
		error_flag = 1;
		*sdpam = 0;
	} else {
		*sdpam = sqrt(*sdpam);
	}
	return error_flag;
}

set_matrix_from_allpam(allpam, pamdist, wm, letters, char2num)
	AllPam *allpam;
	int pamdist;
	Score **wm;
	char *letters;
	char *char2num;
{
	int i, j;
	static char trans[128];

	if (pamdist < 0) { pamdist = 0; }
	if (pamdist > allpam->maxpam) { pamdist = allpam->maxpam; }
/**
	if (allpam->pam[pamdist]->row > strlen(letters)) {
		fprintf(stderr, "ERR\n");
		exit(1);
	}
**/

	/* trans[wm_AACode] = allpam_AACode */
	for (i = 0; letters[i]; i++) {
		trans[i] = allpam->tr[letters[i]];
	}
	for (i = 0; i < allpam->pam[pamdist]->row; i++) {
		for (j = 0; j < allpam->pam[pamdist]->col; j++) {
			wm[i][j] = (Score) (allpam->pam[pamdist]->
				matrix[trans[i]][trans[j]] / log(10.0)*10.0);
		}
	}
}

resetset_gapscale()
{
	gapscale_flag = 0;
}
set_gapscale(gconst,gpam,glen)
	double gconst, gpam, glen;
{
	gapscale_flag = 1;
/*
	constgap = gconst * log(10.0)/10.0;
	pamgap = gpam * log(10.0) / 10.0;
	lengap = glen * log(10.0) / 10.0;
*/
	constgap = gconst;
	pamgap = gpam;
	lengap = glen;
}
get_pamgap(int pam, Score *inigap, Score *extgap)
{
	*inigap = -constgap + pamgap * log10((double) (pam+1));
	*extgap =  -lengap;
}

read_jttpam1(filename)
	char *filename;
{
	FILE *fp;
	enum {Amino, Pam1, Fq, Mutab} mode;
	static char buf[BUFSIZ];
	char amino0[50], conv[50], *p, *endp;
	int i, j, val;
	double vald;

	if ((fp = fopen(filename, "r")) == NULL) {
		fprintf(stderr, "Can't open file: %s\n", filename);
		exit(1);
	}
	while (fgets(buf, BUFSIZ, fp) != NULL) {
		if (strncmp(buf, "amino =", 7) == 0) {
			mode = Amino;
		} else if (strncmp(buf, "pam1 =", 6) == 0) {
			i = 0;
			mode = Pam1;
		} else if (strncmp(buf, "fq =", 4) == 0) {
			mode = Fq;
		} else if (strncmp(buf, "mutab =", 7) == 0) {
			mode = Mutab;
		} else if (mode == Amino) {
			strcpy(amino0, buf);
			for (i = 0; i < strlen(amino0); i++) {
				for (j = 0; j < strlen(amino0); j++) {
					if (amino0[i] == amino[j]) {
						conv[i] = j;
						break;
					}
				}
			}
		} else if (mode == Pam1) {
			if (i > AMINONUM) {
				fprintf(stderr, "format error(i): %d\n",i);
				exit(2);
			}
			p = buf;
			for (j = 0; j < AMINONUM; j++) {
				val = (int) strtol(p, &endp, 10);
				if (endp == NULL) {
					fprintf(stderr, "format error: %d\n",j);
					exit(3);
				}
				jttpam1[conv[i]][conv[j]] = val;
				p = endp;
			}
			i++;
		} else if (mode == Fq) {
			i = 0;
			p = buf;
			for (i = 0; i < AMINONUM; i++) {
				vald = strtod(p, &endp);
				if (endp == NULL) {
					fprintf(stderr, "format error\n");
					exit(1);
				}
				jttfq[conv[i]] = vald;
				p = endp;
			}
		} else if (mode == Mutab) {
			i = 0;
			p = buf;
			for (i = 0; i < AMINONUM; i++) {
				vald = strtod(p, &endp);
				if (endp == NULL) {
					fprintf(stderr, "format error\n");
					exit(1);
				}
				jttmutab[conv[i]] = vald;
				p = endp;
			}
		}
	}
	fclose(fp);
}
