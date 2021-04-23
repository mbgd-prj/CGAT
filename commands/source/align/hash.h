#ifndef _HASH_H_

typedef enum {FIND, ENTER} ACTION;
/*
typedef struct HashRecord {
	char *key;
	char *datum;
	struct HashRecord *next;
} HashRecord;
*/

typedef struct {
	char *key;
	char *datum;
} HENTRY;

typedef struct HashDataBlock {
	HENTRY **datablock;
	int blksiz;
	int recnum;
} HashDataBlock;
typedef struct {
	HENTRY **table;
	int hashsize;
	HashDataBlock *hashdatablock;
} Hash;
Hash *Hcreate();
typedef struct {
	char *key;
	int datumidx;
} SHash;

#define hashf(x,i,hashsize) \
	(((x) % hashsize + (1 + (x) % (hashsize-2)) * (i)) % hashsize)


#define _HASH_H_
#endif
