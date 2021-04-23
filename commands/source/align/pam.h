#ifndef PAM_H
#include "mathmat.h"

#define PAMFILE "allpamout"

typedef struct {
	MathMat **pam;
	int maxpam;
	MathVect *freq;
	char *amino;
	char *tr;
	double scale;
} AllPam;

AllPam *create_allpam();
AllPam *read_allpam();
MathMat *create_mutmat0();
MathMat *create_mutmat_jtt();

/*
struct pamgap {
	double constgap, pamgap, lengap
} PamGap;
*/

#define PAM_H
#endif
