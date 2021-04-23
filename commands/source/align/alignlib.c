#include <stdio.h>
#include <stdlib.h>
#include "dp.h"
#define BIGMINUS -9999999

MakeAliInfo *alignCreate()
{
	MakeAliInfo *aliInfo;
	if ((aliInfo = (MakeAliInfo *) malloc(sizeof(MakeAliInfo))) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	alignInitialize(aliInfo);

	return aliInfo;
}
alignInitialize(MakeAliInfo *aliInfo)
{
	aliInfo->pathlen = 0;
	aliInfo->score = BIGMINUS;
	aliInfo->wm = NULL;
	aliInfo->cellmat = aliInfo->cellmat_bwd = (CellInfo *) NULL;
	aliInfo->path = (Path *) NULL;
	aliInfo->rseq1 = aliInfo->rseq2 = (char *) NULL;
	aliInfo->len1 = aliInfo->len2 = 0;
	aliInfo->aliseq1 = aliInfo->aliseq2 = NULL;
	aliInfo->noedgegap = 1;
	aliInfo->separate = ' ';
	aliInfo->local = 0;
	aliInfo->disp_align = 0;
	aliInfo->thre = 0;
	aliInfo->lsdp = 0;
	aliInfo->gap.init = -10.0; aliInfo->gap.ext = -2.0;
	aliInfo->gap.edge_init = aliInfo->gap.edge_ext = 0.0;
}
alignAlloc(MakeAliInfo *aliInfo, int len1, int len2)
{
	if ((aliInfo->cellmat = (CellInfo *) realloc(aliInfo->cellmat,
			(len2+30) * sizeof(CellInfo))
		    ) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((aliInfo->cellmat_bwd = (CellInfo *) realloc(aliInfo->cellmat_bwd,
			(len2+30) * sizeof(CellInfo))
		    ) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((aliInfo->path =
		(Path *) realloc(aliInfo->path,
			(len1+len2 + 20) * 2 * sizeof(Path) ))
			== NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((aliInfo->rseq1 = (char *) realloc(aliInfo->rseq1,
			len1 +5 )) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((aliInfo->rseq2 = (char *) realloc(aliInfo->rseq2,
			len2 +5 )) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((aliInfo->aliseq1 = (char *) realloc(aliInfo->aliseq1,
			len1+len2 +5 )) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
	if ((aliInfo->aliseq2 = (char *) realloc(aliInfo->aliseq2,
			len1+len2 +5 )) == NULL) {
		fprintf(stderr, "Can't alloc memory\n");
		exit(1);
	}
}

MakeAliInfo *alignInit(MakeAliInfo *aliInfo0,
		char *seq1, char *name1, char* seq2, char *name2)
{
	int seqlen1=strlen(seq1);
	int seqlen2=strlen(seq2);
	int score;
	MakeAliInfo *aliInfo;
	if (aliInfo0) {
		aliInfo = aliInfo0;
	} else {
		aliInfo = alignCreate();
	}
	alignAlloc(aliInfo, seqlen1, seqlen2);

	reverse2(seq1, aliInfo->rseq1);
	reverse2(seq2, aliInfo->rseq2);
	aliInfo->seq1 = seq1;
	aliInfo->seq2 = seq2;
	aliInfo->len1 = seqlen1;
	aliInfo->len2 = seqlen2;
	aliInfo->pathlen = 0;
	aliInfo->name1 = name1;
	aliInfo->name2 = name2;
	return aliInfo;
}
aliResInit(MakeAliInfo *aliInfo, Alignment *aliRes)
{
	aliRes->ent1 = aliInfo->name1;
	aliRes->ent2 = aliInfo->name2;
	aliRes->aliseq1 = aliInfo->aliseq1;
	aliRes->aliseq2 = aliInfo->aliseq2;
}

/*** output result ***/
print_result(Alignment *ali, MakeAliInfo *aliInfo)
{
	int total = ali->alilen;
	char separate = aliInfo->separate;
	double percent = (double) ali->match * 100/ ali->alilen;

	if (! aliInfo->disp_align && separate) {
		printf("%s%c%d%c%s%c%d%c", ali->ent1, separate, ali->len1,
			   separate, ali->ent2, separate, ali->len2, separate);
	} else {
		printf("%s(%d) x %s(%d): ",	
			ali->ent1,ali->len1,ali->ent2,ali->len2);
	}

	if (aliInfo->disp_align) {
		printf("\nAlignment: ");
		printf("[%d-%d:%d-%d] ",
			ali->from1,ali->to1,ali->from2,ali->to2);
		print_align(ali->ent1,ali->aliseq1,
				ali->ent2,ali->aliseq2,ali->alilen,aliInfo);
	} else if (! separate) {
		printf("align=");
		printf("[%d-%d:%d-%d] ",ali->from1,ali->to1,ali->from2,ali->to2)
;
	} else {
		printf("%d%c%d%c%d%c%d%c",ali->from1,separate,ali->to1,separate,
		ali->from2,separate,ali->to2,separate);
	}

	if (! aliInfo->disp_align && separate) {
		printf("%d%c%d%c%.1f%c"
		   , total, separate, ali->match, separate, percent, separate);
		printf("%d", (int)ali->score);
	} else {
		printf("l=%d,",total);  
		printf("m=%d(%.1f%%),", ali->match, percent);
		printf("s=%d",(int)ali->score);
	}
	if (calc_pam) {
		if (! aliInfo->disp_align && separate) {
			if (! ali->pamerr) {
				printf("%c%d%c%.1lf%c%.1lf%c%d", separate,
				ali->pam, separate, ali->exppam, separate,
				ali->sdpam, separate, (int)ali->origscore);
			} else {
				printf("%c%d%c%.1lfE%c%.1lfE%c%d", separate,
				ali->pam, separate, ali->exppam, separate,
				ali->sdpam, separate, (int)ali->origscore);
			}
		} else {
			if (! ali->pamerr) {
				printf(",PAM=(%d,%.1lf,%.1lf),s0=%d ",
				ali->pam, ali->exppam,ali->sdpam,(int)ali->origscore);
			} else {
				printf(",PAM=(%d,%.1lfE,%.1lfE),s0=%d ",
				ali->pam, ali->exppam,ali->sdpam,(int)ali->origscore);
			}
		}
	}
	putchar('\n');

	if (aliInfo->disp_align) {
		putchar('\n');
	}
	
	fflush( stdout );
}

sprint_result(Alignment *ali, char *outstr, MakeAliInfo *aliInfo)
{
    double percent = (double) ali->match * 100/ ali->alilen;
    char separate = aliInfo->separate;
    sprintf(outstr, "%s%c%d%c%s%c%d%c%d%c%d%c%d%c%d%c%d%c%d%c%.1f%c%d",
		   ali->ent1, separate, ali->len1,
                   separate, ali->ent2, separate, ali->len2, separate,
                   ali->from1, separate, ali->to1, separate,
                   ali->from2, separate, ali->to2, separate,
	           ali->alilen, separate, ali->match, separate, percent,
		   separate, (int)ali->score); 

    if (calc_pam) {
	    if (! ali->pamerr) {
		sprintf(&outstr[strlen(outstr)], "%c%d%c%.1lf%c%.1lf%c%d",
			separate, ali->pam, separate, ali->exppam, separate,
			ali->sdpam, separate, (int)ali->origscore);
	    } else {
		sprintf(&outstr[strlen(outstr)], "%c%d%c%.1lfE%c%.1lfE%c%d",
			separate, ali->pam, separate, ali->exppam, separate,
			ali->sdpam, separate, (int)ali->origscore);
	    }
    }
}

print_align(char *ent1, char *align_seq1, char *ent2, char *align_seq2,
		int alilen, MakeAliInfo *aliInfo)
{
	int i, j;
	char edge;
	int mlpam;
	double exppam, sdpam;
	int pamerr;

	printf("\n\n");
	writalin(ent1, align_seq1, ent2, align_seq2, wline, aliInfo->wm);

	return;
}  
reverse(string, len)
	char *string;
	int len;
{
	int i, j;
	char tmp;
	for (i = 0, j = len - 1; i < j; i++, j--) {
		tmp = string[i];
		string[i] = string[j];
		string[j] = tmp;
	}
}
reverse2(char *seq1,char *seq2)
{
	char *s2 = seq2 + strlen(seq1);
	*s2-- = '\0';
	while (*seq1) {
		*s2-- = *seq1++;
	}
}

#define DEFAULT_LINELEN 60

int writalin(char *ent1,char *seq1,char *ent2,char *seq2,
		int linelen,Score **wmat)
{
	int i,j;
	int l, len;
	int k;
	int match = 0;
	char *p1, *p2;

	if (linelen == 0) {
		linelen = DEFAULT_LINELEN;
	}
	len = strlen(seq1);
	for ( l = 0; l < len; l += linelen ) {
		printf("%15s ", ent1);
		for ( i = 0, p1 = &seq1[l]; i < linelen && *p1; i++, p1++) {
			putchar(*p1);
		}
		printf("\n                ");
		p1 = &seq1[l]; p2 = &seq2[l];
		for ( i = 0; i < linelen && *p1 && *p2; i++ ) {
			if (*p1 == *p2) {
				putchar(':');
				match++;
			} else if ( *p1 != '-' && *p2 != '-' &&
				BETTER(wmat[tr[*p1]][tr[*p2]], 0)) {
				putchar('.');
			} else {
				putchar(' ');
			}
			p1++; p2++;
		}
		putchar('\n');
		printf("%15s ",ent2);
		p2 = &seq2[l];
		for ( i = 0, p2 = &seq2[l]; i < linelen && *p2; i++, p2++) {
			putchar(*p2);
		}
		putchar('\n');
		putchar('\n');
	}
	return match;
}

int writalin_oneline(ent1,seq1,ent2,seq2)
	char *ent1, *seq1, *ent2, *seq2;
{
	int i, len = strlen(seq1), match = 0;

	printf("%s:%s\n", ent1, seq1);
	printf("%s:%s\n", ent2, seq2);
	for (i = 0; i < len; i++) {
		if (seq1[i] == seq2[i]) match++;
	}
	return match;
}

#include "db.h"
get_seqpair(DB *db, FILE *lfp, Seq *seq1, Seq *seq2)
{
	static char entname1[MAXNAMELEN], entname2[MAXNAMELEN];
	static char buf[BUFSIZ];
	if (lfp) {
		if (fgets(buf, BUFSIZ, lfp) == NULL) {
			/** EOF **/
			return -1;
		}
		if (sscanf(buf, "%s%s", entname1, entname2) < 2) {
			/** read error **/
			return 0;
		}
		if (getseq_byname(db, entname1, seq1) != GETSEQ_OK
			|| getseq_byname(db, entname2, seq2) != GETSEQ_OK) {
			/** read error **/
			return 0;
		}
	} else {
		if (getseq_next(db, seq1) == GETSEQ_END ||
			getseq_next(db, seq2) == GETSEQ_END) {
			/** EOF **/
			return -1;
		}
	}
	return 1;
}
