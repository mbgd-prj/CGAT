#include <stdio.h>
#include <stdlib.h>
#include "hash.h"
/*
#define PRIME 2147483647
*/
#define PRIME 2047232111

/* defined in hash.h
typedef struct {
	HashRecord **table;
	int hashsize;
} Hash;
typedef struct HashRecord {
	int key;
	char *datum;
} HashRecord;

#define hashf(x,i,hashsize) \
	(((x) % hashsize + (1 + (x) % (hashsize-2)) * (i)) % hashsize)
*/

static HENTRY **hashtable;
static HENTRY *allocrec();
static HashDataBlock *init_datablock();
int Hashsiz;

#define BLOCKSIZ 2000
#define MAXBLOCK 6000


#ifdef HASH_DEBUGMAIN
main(argc, argv)
	int argc;
	char **argv;
{
	Hash *h;
	HENTRY ent;
	char p[4][100];
	strcpy(p[0], "AAA");
	ent.datum = p[0];

	h = Hcreate(1001);
	ent.key = "ABCDEFGHIJK";
	Hsearch(h, &ent, ENTER);

	ent.datum = p[1];
	strcpy(p[1], "BBB");
	printf("%s\n", ent.datum);
	ent.key = "ABCDEFGHIJ";
	Hsearch(h, &ent, FIND);
	printf("%s\n", ent.datum);
	ent.key = "ABCDEFGHIJK";
	Hsearch(h, &ent, FIND);
	printf("%s\n", ent.datum);
}
#endif

Hash *Hcreate(hashsize)
	int hashsize;
{
	Hash *hashp;
	if ((hashp = (Hash *) malloc(sizeof(Hash))) == NULL) {
		fprintf(stderr, "Can't alloc hash\n");
		return 0;
	}
	if ((hashp->table = (HENTRY **) calloc(hashsize, sizeof(HENTRY *))) == NULL) {
		fprintf(stderr, "Can't alloc table\n");
		return 0;
	}
	hashp->hashdatablock = init_datablock();
	hashp->hashsize = hashsize;
	return hashp;
}

Hsearch(hashp, entry, action)
	Hash *hashp;
	HENTRY *entry;
	ACTION action;
{
	HENTRY *p, *prevp;
	int i = 0;
	int hashsize = hashp->hashsize;
	int hashidx;
	register unsigned int keyi = 0;
	char *kp;
static int colicnt = 0;
	int len = strlen(entry->key);

/*
	for (kp = entry->key; *kp; kp++) {
		keyi += *kp;
		keyi += (keyi << 10);
		keyi ^= (keyi >> 6);
	}
	keyi += (keyi << 3);
	keyi ^= (keyi >> 11);
	keyi += (keyi << 15);
	hashidx = hashf(keyi,i,hashsize);
*/

	for (kp = entry->key; *kp; kp++) {
		keyi *= 113; keyi += *kp; /* keyi %= PRIME; */
	}
	hashidx = hashf(keyi,i,hashsize);

	p = hashp->table[hashidx];
/*
fprintf(stderr, "IN %d, %d, %d, %s\n", p, hashidx, keyi, entry->key);
*/
	while (p != NULL) {
		if (strcmp(p->key, entry->key) == 0) {
/*
			if (entry->datum)
*/
				entry->datum = p->datum;
			/*** found ***/
			return 1;
		}
/*
if (action==ENTER){
fprintf(stderr, "OO %d,%d, %d, %s, %s\n", ++colicnt, p, hashidx, entry->key, p->key);
}
*/
		if (++i >= hashsize) {
			fprintf(stderr, "Hash table overflows: %d\n", i);
			exit(1);
		}
		hashidx = hashf(keyi,i,hashsize);
		p = hashp->table[hashidx];
	}
	if (action == ENTER) {
		if ((hashp->table[hashidx] = allocrec(hashp->hashdatablock)) == NULL) {
			fprintf(stderr, "Can't alloc record\n");
			return -1;
		}
		hashp->table[hashidx]->key = entry->key;
		if (entry->datum)
			hashp->table[hashidx]->datum = entry->datum;
	}
	/*** not found / newly inserted ***/
	return 0;
}
HIsearch(hashp, entry, action)
	Hash *hashp;
	HENTRY *entry;
	ACTION action;
{
	HENTRY *p;
	int i = 0;
	int hashsize = hashp->hashsize;
	int hashidx;
	unsigned int keyi = (unsigned int) entry->key;

	hashidx = hashf(keyi,i,hashsize);

	p = hashp->table[hashidx];
	while (p != NULL) {
		if (p->key == entry->key) {
/*
			if (entry->datum)
*/
			entry->datum = p->datum;
			return 1;
		}
		if (++i >= hashsize) {
			fprintf(stderr, "Hash table overflows: %d\n", i);
			exit(1);
		}
		hashidx = hashf(keyi,i,hashsize);
		p = hashp->table[hashidx];
	}
	if (action == ENTER) {
		if ((hashp->table[hashidx] = allocrec(hashp->hashdatablock)) == NULL) {
			fprintf(stderr, "Can't alloc record\n");
			return -1;
		}
		hashp->table[hashidx]->key = entry->key;
/*
		if (entry->datum)
*/
			hashp->table[hashidx]->datum = entry->datum;
	}
	return 0;
}

Hdestroy(hashp)
	Hash *hashp;
{
	free(hashp->table);
	destroy_datablock(hashp->hashdatablock);
	free(hashp);
}

/*
HashRecord *Hwalk(hashp);
	Hash *hashp;
{
	static int i;
	if (hashp == NULL) {
		i = 0;
		return -1;
	} else {
		return (hashp->table[i++]);
	}
}
*/


static HashDataBlock *init_datablock()
{
	HashDataBlock *dblock;
	if ((dblock = (HashDataBlock *) malloc(sizeof(HashDataBlock))) == NULL) {
		fprintf(stderr, "Can't alloc hash datablock\n");
		exit(1);
	}
	if ((dblock->datablock = (HENTRY **)
			calloc(BLOCKSIZ, sizeof(HENTRY *))) == NULL) {
		fprintf(stderr, "Can't alloc hash datablock\n");
		exit(1);
	}
	dblock->blksiz = -1;
	dblock->recnum = BLOCKSIZ;
	return dblock;
}
destroy_datablock(dblock)
	HashDataBlock *dblock;
{
	int i;
	for (i = 0; i < dblock->blksiz; i++) {
		free(dblock->datablock[i]);
	}
	free(dblock->datablock);
	free(dblock);
}
static HENTRY *allocrec(dblock)
	HashDataBlock *dblock;
{
	if (++(dblock->recnum) >= BLOCKSIZ) {
		if (++(dblock->blksiz) >= MAXBLOCK) {
			fprintf(stderr, "Hash Block overflows\n");
			exit(1);
		}
		if ((dblock->datablock[dblock->blksiz] = (HENTRY *) malloc(sizeof(HENTRY) * BLOCKSIZ)) == NULL) {
			fprintf(stderr, "Can't alloc memory\n");
			exit(1);
		}
		dblock->recnum = 0;
	}
	return &(dblock->datablock[dblock->blksiz][dblock->recnum]);
}

