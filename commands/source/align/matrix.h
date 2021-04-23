#ifndef MAT_H
#define AMINO "-ARNDCQEGHILKMFPSTWYVBZX*"
#define MATFILE "blosum62"	/* default matrix */
#define MATDIR "/bio/db/blast/matrix"
/* old
#define MATDIR "/bio/db/blast/matrix/aa"
*/

#define ANUM 25		/* the array size of AMINO */
#define AMNUM 23 	/* the number of alphabet in AMINO */
#define FSTAM 1		/* index of the first amino character ('A') in AMINO */
#define MAXCHR 128
#define DEFAULT_CHAR 'X'

#ifdef DOUBLESCORE
typedef double Score;
#define AtoScore(x) (atof(x))
#else
typedef int Score;
#define AtoScore(x) (atoi(x))
#endif

#ifdef DEFWM
extern int wm[ANUM][ANUM];	/* weight (scoring) matrix */
#endif

double get_matrix_scale();
Score **read_matrix(char *fn, char *let, char *char2num);
Score **create_matrix();
#define MAT_H
#endif		/* ifndef MAT_H */
