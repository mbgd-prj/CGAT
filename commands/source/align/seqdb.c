#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include "db.h"

#define MAXNAME 50
#define MAXSEQLEN 50000
#define MAXTITLEN 4000
#define INIT_SEQBUF_SIZ 5000000
#define INIT_TITBUF_SIZ 20000
#define INIT_SEQNUM 100

struct Form {
    char hdr_ent[10];
    int hdr_entlen;
    int pos_ent;
    char hdr_tit[10];
    int hdr_titlen;
    int pos_tit;
    char hdr_seq[10];
    int hdr_seqlen;
    char end_mk[10];
    int end_mklen;
} form[MAXDB] = {
    { "ENTRY", 5, 6,
      "TITLE", 5, 16,
      "SEQUENCE", 8,
      "///", 3 },
    { "ID", 2, 3,
      "DE", 2, 5,
      "SQ", 2,
      "//", 2 },
    { "LOCUS", 5, 6,
      "DEFINITION", 10, 12,
      "ORIGIN", 6,
      "//", 2 },
    { "MEMBER", 6, 7,
      "DEFINITION", 10, 11,
      "SEQUENCE", 8,
      "STRUCTURE",  9 },
    { "CODE", 4, 5,
      "NAME", 4, 5,
      "SEQUENCE", 8,
      "END", 3 },
};
char gapmark = '-';

read_seqset(DB *db, SeqDBflag flag)
{
	int seqbuf_size = INIT_SEQBUF_SIZ;
	int titbuf_size = INIT_TITBUF_SIZ;
	int maxseqnum = INIT_SEQNUM;
	int *seqidx, *titidx;
	int seqi = 0, titi = 0;
	char *seqp, *titp;
	int sqn = 0, seqlen;
	int i;
	SeqSet *seqset;

	if ( (seqset = (SeqSet *) malloc(sizeof(SeqSet))) == NULL ) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}

	if ( (seqset->seqbuf = (char *) malloc(seqbuf_size * sizeof(char)))
			== NULL) {
		fprintf(stderr, "Can't alloc memory (seqbuf)\n");
		return -1;
	}
	if ( (seqset->titbuf = (char *) malloc(titbuf_size * sizeof(char)))
			== NULL) {
		fprintf(stderr, "Can't alloc memory (titbuf)\n");
		return -1;
	}
	if ( (seqidx = (int *) malloc(maxseqnum * sizeof(int)))==NULL) {
		fprintf(stderr, "Can't alloc memory (seq)\n");
		return -1;
	}
	if ( (titidx = (int *) malloc(maxseqnum * sizeof(int)))==NULL) {
		fprintf(stderr, "Can't alloc memory (tit)\n");
		return -1;
	}
	while (getseq(db, &seqset->titbuf[titi], &seqset->seqbuf[seqi], MAXSEQLEN) == GETSEQ_OK) {
		seqidx[sqn] = seqi;
		titidx[sqn] = titi;
		seqi += (strlen(&seqset->seqbuf[seqi])+1);
		titi += (strlen(&seqset->titbuf[titi])+1);
		if (seqi >= seqbuf_size - MAXSEQLEN) {
			seqbuf_size *= 1.5;
			if ( (seqset->seqbuf = (char *) realloc(seqset->seqbuf, 
					seqbuf_size * sizeof(char)))==NULL) {
				fprintf(stderr, "Can't alloc memory (seqbuf)\n");
				return -1;
			}
		}
		if (titi >= titbuf_size - MAXTITLEN) {
			titbuf_size *= 1.5;
			if ( (seqset->titbuf = (char *) realloc(seqset->titbuf,
					titbuf_size * sizeof(char)))==NULL) {
				fprintf(stderr, "Can't alloc memory (titbuf)\n");
				return -1;
			}
		}
		if (++sqn >= maxseqnum) {
			maxseqnum *= 1.5;
			if ( (seqidx = (int *) realloc(seqidx,
					maxseqnum * sizeof(int)))==NULL) {
				fprintf(stderr, "Can't alloc memory (seq)\n");
				return -1;
			}
			if ( (titidx = (int *) realloc(titidx,
					maxseqnum * sizeof(int)))==NULL) {
				fprintf(stderr, "Can't alloc memory (tit)\n");
				return -1;
			}
		}
	}
	seqset->seqnum = sqn;
	if ( (seqset->seq = (char **) malloc(sqn * sizeof(char *)))==NULL) {
		fprintf(stderr, "Can't alloc memory (seq)\n");
		return -1;
	}
	if ( (seqset->tit = (char **) malloc(sqn * sizeof(char *)))==NULL) {
		fprintf(stderr, "Can't alloc memory (tit)\n");
		return -1;
	}
	if (flag & FLAG_ADDHASH) {
		db->hash = Hcreate(sqn * 20);
	}
	for (i = 0; i < sqn; i++) {
		seqset->seq[i] = &(seqset->seqbuf[seqidx[i]]);
		seqset->tit[i] = &(seqset->titbuf[titidx[i]]);
		if (flag & FLAG_ADDHASH) {
/*
			addhash(db->hash, seqset->tit[i], seqset->seq[i]);
*/
			addhash(db->hash, seqset->tit[i], (char *)i);
		}
	}
	seqset->seqnum = sqn;
	db->seqset = seqset;
	return 0;
}
addhash(Hash *hash, char *key, char *data)
{
	HENTRY item;
	item.key = key;
	item.datum = data;
	Hsearch(hash, &item, ENTER);
}

getseq(DB *db, char *entname, char *seq, int maxlen)
{
	return getseqtitle(db, entname, NULL, seq, maxlen);
}

getseqtitle(DB *db, char *entname, char *title, char *seq, int maxlen)
{
    FILE *fp = dbptr(db);
    int type = dbtype(db);

    if (db->seqset) {
	Seq tmpseq;
	int ret;
	ret = getseq_next(db, &tmpseq);
	if (ret == GETSEQ_OK) {
		strcpy(seq, tmpseq.seq);
		split_title(tmpseq.tit, entname, title);
	}
	return ret;
    } else if (type == STANF) {
	return get_stanf(fp, entname, title, seq, maxlen);
    } else if (type == FASTA) {
	if (db->status == EOF) return 0;
	return get_fasta(fp, entname, title, seq, maxlen,
			db->buf, db->entbuf, &(db->status));
    } else {
	return __getseq_from_file(fp, type, entname, title, seq, maxlen);
    }
}

split_title(char *enttit, char *ent, char *tit)
{
	while (*enttit && !isspace(*enttit)) {
		*ent++ = *enttit++;
	}
	while (*enttit && isspace(*enttit)) {
		enttit++;
	}
	while (*enttit) {
		*tit++ = *enttit++;
	}
}

getseq_byname(DB *db, char *entname, Seq *seq)
{
	HENTRY item;
	int num;

	item.key = entname;
	if ((Hsearch(db->hash, &item, FIND)) == 1) {
		num = (int) item.datum;
		if (num < 0 || num >= db->seqset->seqnum) {
			fprintf(stderr, "Illegal seqnum [%d] for %s\n",
				num, entname);
			return GETSEQ_ERROR;
		}
		seq->seq = db->seqset->seq[num];
		seq->tit = db->seqset->tit[num];
/*
		seq->seq = item.datum;
		seq->tit = entname;
*/
		return GETSEQ_OK;	/* found */
	} else {
		fprintf(stderr, "%s not found\n", entname);
		seq->seq = NULL;
		seq->tit = NULL;
		return GETSEQ_NOTFOUND;	/* not found */
	}
}
getseq_bynum(DB *db, int num, Seq *seq)
{
	if (num >= db->seqset->seqnum) {
		return GETSEQ_END;
	}
	seq->seq = db->seqset->seq[num];
	seq->tit = db->seqset->tit[num];
	return GETSEQ_OK;
}

getseq_next(DB *db, Seq *seq) {
	if (! db->seqset) {
		return -1;
	} else {
		return getseq_bynum(db, db->curr++, seq);
	}
}

__getseq_from_file(DB *db, char *entname, char *title, char *seq, int maxlen)
{
    char buf[BUFSIZ];
    char *p;
    int err_flag = 0;
    int i = 0;
    FILE *fp = db->fp;
    int type = db->type;

    while (fgets(buf, sizeof(buf), fp) != NULL) {
	if (strncmp(buf, form[type].hdr_ent, form[type].hdr_entlen) == 0) {
	    sscanf(&buf[form[type].pos_ent], "%s", entname);
	}
	else if (title && strncmp(buf, form[type].hdr_tit, form[type].hdr_titlen) == 0) {
	    	strcpy(title, &buf[form[type].pos_tit]);
		chop(title);
	}
	else if (strncmp(buf, form[type].hdr_seq, form[type].hdr_seqlen) == 0) {
	    while (fgets(buf, sizeof(buf), fp) != NULL
		   && strncmp(buf, form[type].end_mk, form[type].end_mklen)
		                                                     != 0) {
		for (p = buf; *p != '\0' && *p != '\n'; p++) {
		    if (isupper(*p))
			seq[i++] = *p;
		    else if (islower(*p))
			seq[i++] = toupper(*p);
		    else if (*p == gapmark)
			seq[i++] = gapmark;
                    if (i > maxlen) {
			i = 0;
			err_flag = 1;
		    }
		}
	    }
	    seq[i] = '\0';
	    if (err_flag) {
		return GETSEQ_ERROR;
	    } else {
		return GETSEQ_OK;
	    }
	}
    }
    return GETSEQ_END;
}

get_stanf(FILE *fp, char *entname, char *title, char *seq, int maxlen)
{
    char buf[BUFSIZ], *p;
    int i = 0;
    int seqflag = 0;
    int err_flag = 0;

    while (fgets(buf, sizeof(buf), fp) != NULL) {
	if (buf[0] == ';') {
	    if (title) {
	    	chop(buf);
		strcat(title, &buf[1]);
		continue;
	    }
	} else if (!seqflag) {
	    sscanf(buf, "%s", entname);
	    seqflag = 1;
	}
	else {
	    for (p = buf; *p != '\0' && *p != '\n'; p++) {
		if (isupper(*p))
		    seq[i++] = *p;
		else if (islower(*p))
		    seq[i++] = toupper(*p);
		else if (*p == gapmark)
		    seq[i++] = gapmark;
		else if (*p == '1') {
		    seq[i] = '\0';
		    if (err_flag) {
			return GETSEQ_ERROR;
		    } else {
			return GETSEQ_OK;
		    }
		}
		if (i > maxlen) {
		    i = 0;
		    err_flag = 1;
		}
	    }
	}
    }
    return GETSEQ_END;
}

get_fasta(FILE *fp, char *entname, char *title, char *seq, int maxlen,
		char *buf, char *entbuf, int *status)
{
    char *p;
    int i = 0;
    int seqflag = 0;
    int err_flag = 0;

    if (*status == DBEOF) {
	return GETSEQ_END;
    }

    do {
	if (buf[0] == '\0') {
	    continue;
#ifdef ALLOW_COMMENT
	} else if (buf[0] == '#') {
	    continue;
#endif
	} else if (buf[0] == '>') {
	    if (seqflag) {
		seq[i] ='\0';
		if (err_flag) {
		    return GETSEQ_ERROR;
		} else {
		    return GETSEQ_OK;
		}
	    }
	    for (p = buf + 1; *p && isspace(*p); p++) 
		;
	    while (*p && ! isspace(*p)) {
		*entname++ = *p++;
	    }
	    *entname = '\0';
	    if (title) {
	        chop(p);
	        strcpy(title, p);
	    }
	    continue;
	} else {
	    seqflag = 1;
	    for (p = buf; *p != '\0' && *p != '\n'; p++) {
		if (isupper(*p))
		    seq[i++] = *p;
		else if (islower(*p))
		    seq[i++] = toupper(*p);
		else if (*p == gapmark)
		    seq[i++] = gapmark;
#ifdef ALLOW_DIGIT
		else if (isdigit(*p))
			seq[i++] = *p;
#endif
		if (i > maxlen) {
		    i = 0;
		    err_flag = 1;
		}
	    }
	}
    } while (fgets(buf, BUFSIZ, fp) != NULL);
    seq[i] = '\0';
    *status = DBEOF;
    return GETSEQ_OK;	/* the last sequence */
}

DB *dbopen(char *dbname)
{
    char *filename;
    DB *db;

    if (dbname == NULL) {
	return NULL;
    } else if ((db = (DB *) malloc(sizeof(DB))) == NULL) {
	return NULL;
    } else if (strcmp(dbname, "stdin") == 0) {
	db->fp = stdin;
    } else if ((filename = getenv(dbname)) != NULL) {
	if ((db->fp = fopen(filename, "r")) == NULL) {
		free(db);
		return NULL;
	}
    } else {
	if ((db->fp = fopen(dbname, "r")) == NULL) {
		free(db);
		return NULL;
	}
    }
    db->type = getdbtype(db->fp);
    db->buf[0] = '\0';
    db->entbuf[0] = '\0';
    db->status = FIRST;
    db->seqset = NULL;
    db->curr = 0;
    return db;
}
DB *dupdb(DB *db)
{
    DB * newdb;
    if ((newdb = (DB *) malloc(sizeof(DB))) == NULL) {
	return NULL;
    }
    newdb->type = db->type;
    newdb->curr = db->curr;
    strcpy(newdb->buf, db->buf);
    strcpy(newdb->entbuf, db->entbuf);
    newdb->status = db->status;
    newdb->seqset = db->seqset;
    newdb->hash = db->hash;
    return newdb;
}
dbclose(DB *db)
{
	if (db == NULL || db->fp == NULL || db->fp == stdin) {
		return 0;
	}
	fclose(db->fp);
}

getdbtype(FILE *fp)
{
    int db;
    int i;
    char str[BUFSIZ];

#ifdef ALLOW_COMMENT
    char buf[BUFSIZ];
    if ((str[0] = getc(fp)) == '#') {
	do {
		if (fgets(buf, sizeof(buf), fp) == 0) {
			fprintf(stderr, "Unmature EOF\n");
			fclose(fp);
			exit(1);
		}
	} while ((str[0] = getc(fp)) == '#');
    }
    ungetc(str[0], fp);
#endif
    if ((str[0] = getc(fp)) == ';') {
	ungetc(str[0], fp);
	return STANF;
    } else if (str[0] == '>') {
	ungetc(str[0], fp);
	return FASTA;
    } 
    for (i = 1; i < BUFSIZ; i++) {
	if ((str[i] = getc(fp)) == ' ') {
	    str[i] = '\0';
	    for (db = 0; db < MAXDB; db++) {
		if (strcmp(str, form[db].hdr_ent) == 0) {
		    while (--i >= 0)
			ungetc(str[i], fp);
		    return db;
		}
	    }
	    break;
	}
    }
    while (--i)
	ungetc(str[i], fp);
    return DBNONE;
}
chop(char *str)
{
	if (! *str)
		return(0);
	while (*++str);
	*(str - 1) = '\0';
	return(0);
}
set_dbptr(DB *db, int pos)
{
	db->curr = pos;
}

dbrewind(DB *db)
{
	if (db->seqset) {
		db->curr = 0;
		return 0;
	} else if (db->fp == stdin) {
		return 1;
	}
	db->status = FIRST;
	db->buf[0] = '\0';
	db->entbuf[0] = '\0';
	rewind(dbptr(db));
	return 0;
}
