#ifndef _DB_H_
#define _DB_H_

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

#define MAXDB 5

#include "seqset.h"

typedef struct {
    FILE *fp;
    int type;
    int status;
    char entbuf[BUFSIZ];
    char buf[BUFSIZ];
} DB;

#define dbptr(db) db->fp
#define dbtype(db) db->type
#define dbclose(db) fclose(dbptr(db))

DB *dbopen();
SeqSet *read_seqset();
SeqSet *read_seqset_alpha();

#endif
