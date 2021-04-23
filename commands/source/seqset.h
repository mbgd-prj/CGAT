#ifndef _SEQSET_H_
#define _SEQSET_H_
#include "alphabet.h"

typedef struct {
    char *seq;
    char *tit;
    Alphabet *alpha;
} Seq;

typedef struct {
/*
    char **seq;
    char **tit;
*/
    Seq *seq;
    int seqnum;
    char *seqbuf, *titbuf;
    Alphabet *alpha;
} SeqSet;

char *getseq_fromset();
char *gettit_fromset();
#endif
