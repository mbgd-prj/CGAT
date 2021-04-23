#ifndef _DB_H_
#define _DB_H_
#include "hash.h"

/***
#define NONE -3
#define FASTA -2
#define STANF -1
#define NBRF 0
#define SWISS 1
#define GENBANK 2
#define PDBSTR 3
#define PRF 4

#define FIRST 0
#define CONT 1
#define DBEOF 2
***/

#define MAXDB 5

typedef enum {
	DBNONE = -3, FASTA, STANF, NBRF, SWISS, GENBANK, PDBSTR, PRF
} SeqDB_DBtype;
typedef enum {
	FIRST, CONT, DBEOF
} SeqDB_Stat;
typedef enum {
	GETSEQ_ERROR = -1, GETSEQ_OK, GETSEQ_END, GETSEQ_NOTFOUND,
} SeqDB_RetCode;

typedef struct {
	char *seq;
	char *tit;
} Seq;
typedef struct {
    char **seq;
    char **tit;
    int seqnum;
    char *seqbuf, *titbuf;
} SeqSet;

typedef struct {
    FILE *fp;
    int type;
    int status;
    char entbuf[BUFSIZ];
    char buf[BUFSIZ];
    SeqSet *seqset;
    int curr;
    Hash *hash;
} DB;

typedef int SeqDBflag;
#define FLAG_ADDHASH 1

#define dbptr(db) db->fp
#define dbtype(db) db->type

DB *dbopen();
DB *dupdb();
/*
SeqSet *read_seqset();
*/
#endif
